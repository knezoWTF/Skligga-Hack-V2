package net.skliggahack.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.skliggahack.event.EventManager;
import net.skliggahack.event.events.EntityDespawnListener;
import net.skliggahack.event.events.EntitySpawnListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.skliggahack.SkliggaHack.MC;

@Mixin(ClientWorld.class)
public class ClientWorldMixin
{

	@Unique
	private Entity skligga$despawningEntity;

	@Inject(method = "addEntity(Lnet/minecraft/entity/Entity;)V", at = @At("TAIL"))
	private void onAddEntity(Entity entity, CallbackInfo ci)
	{
		if (entity != null)
			EventManager.fire(new EntitySpawnListener.EntitySpawnEvent(entity));
	}

	@Inject(method = "removeEntity(ILnet/minecraft/entity/Entity$RemovalReason;)V", at = @At("HEAD"))
	private void onRemoveEntityHead(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci)
	{
		skligga$despawningEntity = MC.world == null ? null : MC.world.getEntityById(entityId);
	}

	@Inject(method = "removeEntity(ILnet/minecraft/entity/Entity$RemovalReason;)V", at = @At("TAIL"))
	private void onRemoveEntityTail(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci)
	{
		if (skligga$despawningEntity != null)
		{
			EventManager.fire(new EntityDespawnListener.EntityDespawnEvent(skligga$despawningEntity));
			skligga$despawningEntity = null;
		}
	}

}
