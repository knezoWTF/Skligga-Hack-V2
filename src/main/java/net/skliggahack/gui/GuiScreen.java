package net.skliggahack.gui;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.skliggahack.SkliggaHack;

public class GuiScreen extends net.minecraft.client.gui.screen.Screen
{

	private final ClickGui gui;

	public GuiScreen()
	{
		super(Text.literal("gui"));
		gui = SkliggaHack.INSTANCE.getClickGui();
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);
		gui.render(context, mouseX, mouseY, delta);
	}

	@Override
	public void mouseMoved(double mouseX, double mouseY)
	{
		gui.handleMouseMoved(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(Click click, boolean doubled)
	{
		gui.handleMouseClicked(click.x(), click.y(), click.button());
		return super.mouseClicked(click, doubled);
	}

	@Override
	public boolean mouseReleased(Click click)
	{
		gui.handleMouseReleased(click.x(), click.y(), click.button());
		return super.mouseReleased(click);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount)
	{
		gui.handleMouseScrolled(mouseX, mouseY, verticalAmount);
		return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
	}

	@Override
	public boolean mouseDragged(Click click, double deltaX, double deltaY)
	{
		gui.handleMouseDragged(click.x(), click.y(), click.button(), deltaX, deltaY);
		return super.mouseDragged(click, deltaX, deltaY);
	}
}
