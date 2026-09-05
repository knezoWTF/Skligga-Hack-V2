package net.skliggahack.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.skliggahack.event.EventManager;
import net.skliggahack.event.events.RenderHudListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.skliggahack.SkliggaHack.MC;

@Mixin(InGameHud.class)
public class InGameHudMixin
{
	@Inject(
		at = @At("HEAD"),
		method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V")
	private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci)
	{
		if (MC.inGameHud.getDebugHud().shouldShowDebugHud())
			return;

		RenderHudListener.RenderHudEvent event = new RenderHudListener.RenderHudEvent(context, tickCounter.getDynamicDeltaTicks());
		EventManager.fire(event);
	}
}
