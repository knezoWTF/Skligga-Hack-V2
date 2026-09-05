package net.skliggahack.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.skliggahack.event.EventManager;
import net.skliggahack.event.events.GameRenderListener.GameRenderEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.skliggahack.SkliggaHack.MC;

@Mixin(GameRenderer.class)
public class GameRendererMixin
{
	@Inject(
			at = @At("HEAD"),
			method = "renderWorld(Lnet/minecraft/client/render/RenderTickCounter;)V")
	private void onRenderWorld(RenderTickCounter tickCounter, CallbackInfo ci)
	{
		if (MC.world == null || MC.player == null)
			return;
		GameRenderEvent event = new GameRenderEvent(tickCounter.getTickProgress(false));
		EventManager.fire(event);
	}
}
