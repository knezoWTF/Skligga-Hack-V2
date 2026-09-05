package net.skliggahack.module.modules.hud;

import net.minecraft.client.gui.DrawContext;
import net.skliggahack.event.events.RenderHudListener;
import net.skliggahack.module.Category;
import net.skliggahack.module.Module;

import static net.skliggahack.SkliggaHack.MC;

public class SkliggaVersionText extends Module implements RenderHudListener
{

	public SkliggaVersionText()
	{
		super("SkliggaVersionText", "SkliggaHax 4.2.0", false, Category.HUD);
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
		context.drawTextWithShadow(MC.textRenderer, "SkliggaHax 4.2.0", 10, 60, 0xFF00CC00);
	}
}
