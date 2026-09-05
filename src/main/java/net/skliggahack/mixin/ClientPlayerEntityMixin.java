package net.skliggahack.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import net.skliggahack.event.EventManager;
import net.skliggahack.event.events.PlayerTickListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin
{
	@Inject(at = @At("HEAD"), method = "tick()V")
	private void onTick(CallbackInfo ci)
	{
		EventManager.fire(new PlayerTickListener.PlayerTickEvent());
	}
}
