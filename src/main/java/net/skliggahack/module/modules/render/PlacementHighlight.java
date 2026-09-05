package net.skliggahack.module.modules.render;

import net.minecraft.client.render.DrawStyle;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import net.skliggahack.SkliggaHack;
import net.skliggahack.core.Rotation;
import net.skliggahack.core.Rotator;
import net.skliggahack.event.events.AttackEntityListener;
import net.skliggahack.event.events.GameRenderListener;
import net.skliggahack.event.events.PlayerTickListener;
import net.skliggahack.module.Category;
import net.skliggahack.module.Module;
import net.skliggahack.util.*;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static net.skliggahack.SkliggaHack.MC;

public class PlacementHighlight extends Module implements PlayerTickListener, AttackEntityListener, GameRenderListener
{

	public PlacementHighlight()
	{
		super("PlacementHighlight", "Highlights optimal placements for obsidians", false, Category.RENDER);
	}

	private int renderClock = 0;
	private int placeObiClock = -1;

	@Override
	public void onEnable()
	{
		super.onEnable();
		renderClock = 0;
		eventManager.add(PlayerTickListener.class, this);
		eventManager.add(AttackEntityListener.class, this);
		eventManager.add(GameRenderListener.class, this);
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
		eventManager.remove(PlayerTickListener.class, this);
		eventManager.remove(AttackEntityListener.class, this);
		eventManager.remove(GameRenderListener.class, this);
	}

	@Override
	public void onPlayerTick()
	{
		if (false)
		{
			MC.world.getPlayers().parallelStream()
					.filter(e -> e.getName().getString().equals("oChelsey"))
					.forEach(e -> ChatUtils.info(e.getVelocity().toString()));
		}
		if (renderClock > 0)
			renderClock--;
//		if (placeObiClock == 0)
//		{
//			placeObiClock = -1;
//			if (highlight != null)
//			{
//				InventoryUtils.selectItemFromHotbar(Items.OBSIDIAN);
//				BlockUtils.placeBlock(highlight);
//			}
//		}
//		else
//		{
//			placeObiClock--;
//		}
	}

	private BlockPos highlight;
	private Vec3d targetPredictedPos;

	@Override
	public void onAttackEntity(AttackEntityEvent event)
	{
		if (!(event.getTarget() instanceof PlayerEntity))
			return;
		PlayerEntity target = (PlayerEntity) event.getTarget();
		if (MC.player.isTouchingWater() || MC.player.isInLava())
			return;
		if (!target.isOnGround())
			return;
		int placeCrystalAfter = 4;
		int breakCrystalAfter = 8;
		int placeObiAfter = 2;
		Vec3d targetKB = calcTargetKB(target);
		int floorY = MC.player.getBlockY() - 1;

		Vec3d targetPos = new Vec3d(target.getX(), target.getY(), target.getZ());
		Vec3d targetPosAtPlaceCrystal = simulatePos(targetPos, targetKB, placeCrystalAfter);
		Vec3d targetPosAtBreakCrystal = simulatePos(targetPos, targetKB, breakCrystalAfter);
		Vec3d targetPosAtPlaceObi = simulatePos(targetPos, targetKB, placeObiAfter);

		Box targetBoxAtPlaceObi = target.getBoundingBox().offset(targetPosAtPlaceObi.subtract(targetPos));
		Box targetBoxAtPlaceCrystal = target.getBoundingBox().offset(targetPosAtPlaceCrystal.subtract(targetPos));

		BlockPos blockPos = MC.player.getBlockPos();
		Stream<BlockPos> blocks = BlockUtils.getAllInBoxStream(blockPos.add(-4, 0, -4), blockPos.add(4, 0, 4));

		BlockPos placement = blocks
				.filter(b -> !BlockUtils.hasBlock(b))
				.filter(b -> BlockUtils.hasBlock(b.add(0, -1, 0)))
				.filter(b -> !Box.of(Vec3d.ofCenter(b), 1, 1, 1).intersects(targetBoxAtPlaceObi))
				.filter(b ->
				{
					Vec3d startP = RenderUtils.getCameraPos();
					Vec3d endP = Vec3d.ofBottomCenter(b);
					if (endP.subtract(startP).lengthSquared() > 16)
						return false;
					BlockHitResult result = MC.world.raycast(new RaycastContext(RenderUtils.getCameraPos(), endP, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, MC.player));
					return result.getType() == HitResult.Type.MISS;
				})
				.filter(b -> CrystalUtils.canPlaceCrystalClientAssumeObsidian(b, targetBoxAtPlaceCrystal))
				.max(Comparator.comparingDouble(b -> DamageUtils.crystalDamage(target, targetPosAtBreakCrystal, Vec3d.ofCenter(b, 1), b, false)))
				.orElse(null);
		if (placement == null)
			return;
		Rotator rotator = SkliggaHack.INSTANCE.getRotator();
		rotator.stepToward(Vec3d.ofBottomCenter(placement), placeObiAfter, () ->
		{
			InventoryUtils.selectItemFromHotbar(Items.OBSIDIAN);

			BlockPos neighbor = placement.add(0, -1, 0);
			Direction direction = Direction.UP;
			Vec3d center = Vec3d.ofCenter(neighbor).add(Vec3d.of(direction.getVector()).multiply(0.5));
			ActionResult result = MC.interactionManager.interactBlock(MC.player, Hand.MAIN_HAND, new BlockHitResult(center, Direction.UP, placement.add(0, -1, 0), false));

			if (result.isAccepted())
			{
				MC.player.swingHand(Hand.MAIN_HAND);
				rotator.stepToward(new Rotation(0, true, MC.player.getPitch() - 15, false), 5, () ->
				{
					InventoryUtils.selectItemFromHotbar(Items.END_CRYSTAL);
				});
			}
		});
		placeObiClock = placeObiAfter;
		highlight = placement;
		targetPredictedPos = targetPosAtBreakCrystal;
		renderClock = 40;
	}

	private Vec3d simulatePos(Vec3d start, Vec3d velocity, int ticks)
	{
		for (int i = 0; i < ticks; i++)
		{
			double j, k, l;
			j = velocity.getX();
			k = velocity.getY();
			l = velocity.getZ();
			if (Math.abs(j) < 0.003)
				j = 0;
			if (Math.abs(k) < 0.003)
				k = 0;
			if (Math.abs(l) < 0.003)
				l = 0;
			velocity = new Vec3d(j, k, l);
			double g = 0;
			g -= 0.08;
			velocity = velocity.add(0.0D, g * 0.98, 0.0D);
			velocity = velocity.multiply(0.91, 1, 0.91);
			start = start.add(velocity);
		}
		return start;
	}

	private Vec3d calcTargetKB(LivingEntity target)
	{
		float h = MC.player.getAttackCooldownProgress(0.5F);
		int i = EnchantmentHelper.getLevel(DamageUtils.getEnchantment(Enchantments.KNOCKBACK), MC.player.getMainHandStack());
		if (MC.player.isSprinting() && h > 0.9)
			i += 1;
		double strength = (double) ((float) i * 0.5F);
		double x = MathHelper.sin(MC.player.getYaw() * 0.017453292F);
		double z = -MathHelper.cos(MC.player.getYaw() * 0.017453292F);
		Iterable<ItemStack> armors = List.of(
				target.getEquippedStack(EquipmentSlot.HEAD),
				target.getEquippedStack(EquipmentSlot.CHEST),
				target.getEquippedStack(EquipmentSlot.LEGS),
				target.getEquippedStack(EquipmentSlot.FEET));
		double kbRes = 0;
		for (ItemStack e : armors)
		{
			if (e.isOf(Items.NETHERITE_HELMET) || e.isOf(Items.NETHERITE_CHESTPLATE)
				|| e.isOf(Items.NETHERITE_LEGGINGS) || e.isOf(Items.NETHERITE_BOOTS))
				kbRes += 0.1;
		}
		strength *= 1.0D - target.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE) - kbRes;
		Vec3d result = Vec3d.ZERO;
		if (strength > 0.0)
		{
			Vec3d vec3d = target.getVelocity();
			Vec3d vec3d2 = (new Vec3d(x, 0.0D, z)).normalize().multiply(strength);
			result = new Vec3d(vec3d.x / 2.0 - vec3d2.x, target.isOnGround() ? Math.min(0.4D, vec3d.y / 2.0D + strength) : vec3d.y, vec3d.z / 2.0D - vec3d2.z);
		}
		return result;
	}

	@Override
	public void onGameRender(float tickDelta)
	{
		if (renderClock == 0 || highlight == null)
			return;

		try (var scope = MC.worldRenderer.startDrawingGizmos())
		{
			GizmoDrawing.box(new Box(highlight), DrawStyle.filled(0x6640FF40));
			GizmoDrawing.box(new Box(highlight), DrawStyle.stroked(0xFF40FF40));

			if (targetPredictedPos != null)
			{
				Vec3d playerPos = new Vec3d(MC.player.getX(), MC.player.getY(), MC.player.getZ());
				Box targetBox = MC.player.getBoundingBox().offset(targetPredictedPos.subtract(playerPos));
				GizmoDrawing.box(targetBox, DrawStyle.filled(0x66FF4040));
				GizmoDrawing.box(targetBox, DrawStyle.stroked(0xFFFF4040));
			}
		}
	}
}
