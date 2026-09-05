package net.skliggahack.module.modules.hud;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.skliggahack.event.events.RenderHudListener;
import net.skliggahack.module.Category;
import net.skliggahack.module.Module;

public class SkliggaLogo extends Module implements RenderHudListener
{
	private static final Identifier logoId = Identifier.of("skliggas", "logo.png");

	public SkliggaLogo()
	{
		super("SkliggaLogo", "SKLIGGER", false, Category.HUD);
	}

	@Override
	public void onEnable()
	{
		super.onEnable();
		eventManager.add(RenderHudListener.class, this);
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
		eventManager.remove(RenderHudListener.class, this);
	}

	@Override
	public void onRenderHud(DrawContext context, double partialTicks)
	{
		context.drawTexture(RenderPipelines.GUI_TEXTURED, logoId, 28, 11, 0, 0, 38, 41, 75, 81);
	}
}
