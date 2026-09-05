package net.skliggahack.util;

// algorithms copied from meteor, slightly modified

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.explosion.ExplosionImpl;

import java.util.List;
import java.util.Objects;

import static net.skliggahack.SkliggaHack.MC;

public enum DamageUtils
{
	;
	// Crystal damage

	public static double crystalDamage(PlayerEntity player, Vec3d playerPos, Vec3d crystal, BlockPos obsidianPos, boolean ignoreTerrain) {
		if (player == null) return 0;
		if (PlayerUtils.getGameMode(player) == GameMode.CREATIVE) return 0;

		double modDistance = Math.sqrt(playerPos.squaredDistanceTo(crystal));
		if (modDistance > 12) return 0;

		double exposure = getExposure(crystal, player, playerPos, obsidianPos, ignoreTerrain);
		double impact = (1 - (modDistance / 12)) * exposure;
		double damage = ((impact * impact + impact) / 2 * 7 * (6 * 2) + 1);

		damage = getDamageForDifficulty(damage);
		damage = DamageUtil.getDamageLeft(player, (float) damage, getDamageSource(), (float) player.getArmor(), (float) player.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue());
		damage = resistanceReduction(player, damage);

		damage = blastProtReduction(player, damage);

		return damage < 0 ? 0 : damage;
	}

	public static double crystalDamage(PlayerEntity player, Vec3d crystal, boolean predictMovement, BlockPos obsidianPos, boolean ignoreTerrain) {
		if (player == null) return 0;
		if (PlayerUtils.getGameMode(player) == GameMode.CREATIVE) return 0;

		Vec3d playerPos = new Vec3d(player.getX(), player.getY(), player.getZ());

		if (predictMovement)
			playerPos = new Vec3d(playerPos.x + player.getVelocity().x, playerPos.y + player.getVelocity().y, playerPos.z + player.getVelocity().z);

		double modDistance = Math.sqrt(playerPos.squaredDistanceTo(crystal));
		if (modDistance > 12) return 0;

		double exposure = getExposure(crystal, player, predictMovement, obsidianPos, ignoreTerrain);
		double impact = (1 - (modDistance / 12)) * exposure;
		double damage = ((impact * impact + impact) / 2 * 7 * (6 * 2) + 1);

		damage = getDamageForDifficulty(damage);
		damage = DamageUtil.getDamageLeft(player, (float) damage, getDamageSource(), (float) player.getArmor(), (float) player.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue());
		damage = resistanceReduction(player, damage);

		damage = blastProtReduction(player, damage);

		return damage < 0 ? 0 : damage;
	}

	public static double crystalDamage(PlayerEntity player, Vec3d crystal) {
		return crystalDamage(player, crystal, false, null, false);
	}

	public static double crystalDamage(PlayerEntity player, Vec3d playerPos, Vec3d crystal) {
		return crystalDamage(player, playerPos, crystal, null, false);
	}

	// Sword damage

	public static double getSwordDamage(PlayerEntity entity, boolean charged) {
		// Get sword damage
		double damage = 0;
		if (charged) {
			if (entity.getActiveItem().getItem() == Items.DIAMOND_SWORD) {				damage += 7;
			} else if (entity.getActiveItem().getItem() == Items.GOLDEN_SWORD) {
				damage += 4;
			} else if (entity.getActiveItem().getItem() == Items.IRON_SWORD) {
				damage += 6;
			} else if (entity.getActiveItem().getItem() == Items.STONE_SWORD) {
				damage += 5;
			} else if (entity.getActiveItem().getItem() == Items.WOODEN_SWORD) {
				damage += 4;
			}
			damage *= 1.5;
		}

		int sharpLevel = EnchantmentHelper.getLevel(getEnchantment(Enchantments.SHARPNESS), entity.getActiveItem());
		if (sharpLevel > 0) {
			damage += (0.5 * sharpLevel) + 0.5;
		}

		if (entity.getActiveStatusEffects().containsKey(StatusEffects.STRENGTH)) {
			int strength = Objects.requireNonNull(entity.getStatusEffect(StatusEffects.STRENGTH)).getAmplifier() + 1;
			damage += 3 * strength;
		}

		// Reduce by resistance
		damage = resistanceReduction(entity, damage);

		// Reduce by armour
		damage = DamageUtil.getDamageLeft(entity, (float) damage, getDamageSource(), (float) entity.getArmor(), (float) entity.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue());

		// Reduce by enchants
		damage = normalProtReduction(entity, damage);

		return damage < 0 ? 0 : damage;
	}

	// Bed damage

	public static double bedDamage(LivingEntity player, Vec3d bed) {
		if (player instanceof PlayerEntity && ((PlayerEntity) player).getAbilities().creativeMode) return 0;

		double modDistance = Math.sqrt(player.squaredDistanceTo(bed));
		if (modDistance > 10) return 0;

		double exposure = ExplosionImpl.calculateReceivedDamage(bed, player);
		double impact = (1.0 - (modDistance / 10.0)) * exposure;
		double damage = (impact * impact + impact) / 2 * 7 * (5 * 2) + 1;

		// Multiply damage by difficulty
		damage = getDamageForDifficulty(damage);

		// Reduce by resistance
		damage = resistanceReduction(player, damage);

		// Reduce by armour
		damage = DamageUtil.getDamageLeft(player, (float) damage, getDamageSource(), (float) player.getArmor(), (float) player.getAttributeInstance(EntityAttributes.ARMOR_TOUGHNESS).getValue());

		// Reduce by enchants
		damage = blastProtReduction(player, damage);

		if (damage < 0) damage = 0;
		return damage;
	}

	// Anchor damage

	public static double anchorDamage(LivingEntity player, Vec3d anchor) {
		BlockPos anchorPos = BlockPos.ofFloored(anchor);
		BlockState state = BlockUtils.getBlockState(anchorPos);
		MC.world.removeBlock(anchorPos, false);
		double damage = bedDamage(player, anchor);
		MC.world.setBlockState(anchorPos, state);
		return damage;
	}

	// Utils

	private static double getDamageForDifficulty(double damage) {
		return switch (MC.world.getLevelProperties().getDifficulty()) {
			case PEACEFUL -> 0;
			case EASY     -> Math.min(damage / 2 + 1, damage);
			case HARD     -> damage * 3 / 2;
			default       -> damage;
		};
	}

	private static double normalProtReduction(LivingEntity player, double damage) {
		int protLevel = 0;
		RegistryEntry<Enchantment> protection = getEnchantment(Enchantments.PROTECTION);
		for (ItemStack stack : getArmorStacks(player))
			protLevel += EnchantmentHelper.getLevel(protection, stack);
		if (protLevel > 20) protLevel = 20;

		damage *= 1 - (protLevel / 25.0);
		return damage < 0 ? 0 : damage;
	}

	private static double blastProtReduction(LivingEntity player, double damage) {
		int protLevel = 0;
		RegistryEntry<Enchantment> protection = getEnchantment(Enchantments.PROTECTION);
		RegistryEntry<Enchantment> blastProtection = getEnchantment(Enchantments.BLAST_PROTECTION);
		for (ItemStack stack : getArmorStacks(player))
			protLevel += EnchantmentHelper.getLevel(protection, stack)
				+ 2 * EnchantmentHelper.getLevel(blastProtection, stack);
		if (protLevel > 20) protLevel = 20;

		damage *= (1 - (protLevel / 25.0));
		return damage < 0 ? 0 : damage;
	}

	private static List<ItemStack> getArmorStacks(LivingEntity player) {
		return List.of(
			player.getEquippedStack(EquipmentSlot.HEAD),
			player.getEquippedStack(EquipmentSlot.CHEST),
			player.getEquippedStack(EquipmentSlot.LEGS),
			player.getEquippedStack(EquipmentSlot.FEET));
	}

	private static DamageSource getDamageSource() {
		return MC.world.getDamageSources().generic();
	}

	public static RegistryEntry<Enchantment> getEnchantment(RegistryKey<Enchantment> key) {
		return MC.world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(key.getValue()).orElseThrow();
	}

	private static double resistanceReduction(LivingEntity player, double damage) {
		if (player.hasStatusEffect(StatusEffects.RESISTANCE)) {
			int lvl = (player.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1);
			damage *= (1 - (lvl * 0.2));
		}

		return damage < 0 ? 0 : damage;
	}

	private static double getExposure(Vec3d source, Entity entity, boolean predictMovement, BlockPos obsidianPos, boolean ignoreTerrain) {
		Box box = entity.getBoundingBox();
		if (predictMovement) {
			Vec3d v = entity.getVelocity();
			box.offset(v.x, v.y, v.z);
		}

		double d = 1 / ((box.maxX - box.minX) * 2 + 1);
		double e = 1 / ((box.maxY - box.minY) * 2 + 1);
		double f = 1 / ((box.maxZ - box.minZ) * 2 + 1);
		double g = (1 - Math.floor(1 / d) * d) / 2;
		double h = (1 - Math.floor(1 / f) * f) / 2;

		if (!(d < 0) && !(e < 0) && !(f < 0)) {
			int i = 0;
			int j = 0;

			for (double k = 0; k <= 1; k += d) {
				for (double l = 0; l <= 1; l += e) {
					for (double m = 0; m <= 1; m += f) {
						double n = MathHelper.lerp(k, box.minX, box.maxX);
						double o = MathHelper.lerp(l, box.minY, box.maxY);
						double p = MathHelper.lerp(m, box.minZ, box.maxZ);

						Vec3d vec3d = new Vec3d(n + g, o, p + h);

						RaycastContext raycastContext = new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);

						if (raycast(raycastContext, obsidianPos, ignoreTerrain).getType() == HitResult.Type.MISS) i++;

						j++;
					}
				}
			}

			return (double) i / j;
		}

		return 0;
	}

	private static double getExposure(Vec3d source, Entity entity, Vec3d playerPos, BlockPos obsidianPos, boolean ignoreTerrain) {
		Box box = entity.getBoundingBox();
		Vec3d v = playerPos.subtract(new Vec3d(entity.getX(), entity.getY(), entity.getZ()));
		box.offset(v.x, v.y, v.z);

		double d = 1 / ((box.maxX - box.minX) * 2 + 1);
		double e = 1 / ((box.maxY - box.minY) * 2 + 1);
		double f = 1 / ((box.maxZ - box.minZ) * 2 + 1);
		double g = (1 - Math.floor(1 / d) * d) / 2;
		double h = (1 - Math.floor(1 / f) * f) / 2;

		if (!(d < 0) && !(e < 0) && !(f < 0)) {
			int i = 0;
			int j = 0;

			for (double k = 0; k <= 1; k += d) {
				for (double l = 0; l <= 1; l += e) {
					for (double m = 0; m <= 1; m += f) {
						double n = MathHelper.lerp(k, box.minX, box.maxX);
						double o = MathHelper.lerp(l, box.minY, box.maxY);
						double p = MathHelper.lerp(m, box.minZ, box.maxZ);

						Vec3d vec3d = new Vec3d(n + g, o, p + h);

						RaycastContext raycastContext = new RaycastContext(vec3d, source, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);

						if (raycast(raycastContext, obsidianPos, ignoreTerrain).getType() == HitResult.Type.MISS) i++;

						j++;
					}
				}
			}

			return (double) i / j;
		}

		return 0;
	}


	private static BlockHitResult raycast(RaycastContext context, BlockPos obsidianPos, boolean ignoreTerrain) {
		return BlockView.raycast(context.getStart(), context.getEnd(), context, (raycastContext, blockPos) -> {
			BlockState blockState;
			if (blockPos.equals(obsidianPos)) blockState = Blocks.OBSIDIAN.getDefaultState();
			else {
				blockState = MC.world.getBlockState(blockPos);
				if (isBlastWeak(blockState) && ignoreTerrain) blockState = Blocks.AIR.getDefaultState();
			}

			Vec3d vec3d = raycastContext.getStart();
			Vec3d vec3d2 = raycastContext.getEnd();

			VoxelShape voxelShape = raycastContext.getBlockShape(blockState, MC.world, blockPos);
			BlockHitResult blockHitResult = MC.world.raycastBlock(vec3d, vec3d2, blockPos, voxelShape, blockState);
			VoxelShape voxelShape2 = VoxelShapes.empty();
			BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, blockPos);

			double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
			double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());

			return d <= e ? blockHitResult : blockHitResult2;
		}, (raycastContext) -> {
			Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
			return BlockHitResult.createMissed(raycastContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(raycastContext.getEnd()));
		});
	}

	// Vanilla blast resistances >= 600 (everything else is treated as breakable terrain)
	private static boolean isBlastWeak(BlockState state) {
		return !(state.isOf(Blocks.BEDROCK)
			|| state.isOf(Blocks.OBSIDIAN)
			|| state.isOf(Blocks.CRYING_OBSIDIAN)
			|| state.isOf(Blocks.RESPAWN_ANCHOR)
			|| state.isOf(Blocks.ANCIENT_DEBRIS)
			|| state.isOf(Blocks.NETHERITE_BLOCK)
			|| state.isOf(Blocks.ENCHANTING_TABLE)
			|| state.isOf(Blocks.ENDER_CHEST)
			|| state.isOf(Blocks.ANVIL)
			|| state.isOf(Blocks.CHIPPED_ANVIL)
			|| state.isOf(Blocks.DAMAGED_ANVIL)
			|| state.isOf(Blocks.REINFORCED_DEEPSLATE));
	}
}
