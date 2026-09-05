package net.skliggahack.gui.component;

import net.minecraft.client.gui.DrawContext;
import net.skliggahack.gui.window.Window;

import static net.skliggahack.SkliggaHack.MC;

public abstract class Component
{

	public final Window parent;
	private double x, y;
	private final double length;
	private final String name;

	public Component(Window parent, double x, double y, double length, String name)
	{
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.length = length;
		this.name = name;
	}

	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		double parentX = parent.getX();
		double parentY = parent.getY();
		double parentWidth = parent.getWidth();
		double parentLength = parent.getLength();
		double parentX2 = parent.getX() + parentWidth;
		double parentY2 = parent.getY() + parentLength;
		double x = getX() + parentX;
		double y = getY() + parentY - 10;
		if (getY() + parentY - parentY - 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		String trimmed = MC.textRenderer.trimToWidth(name, (int) (parentX2 - x));
		context.drawTextWithShadow(MC.textRenderer, trimmed, (int) x, (int) y, 0xFFFFFFFF);
	}

	public void onMouseMoved(double mouseX, double mouseY)
	{

	}

	public void onMouseClicked(double mouseX, double mouseY, int button)
	{

	}

	public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
	{
		return false;
	}

	public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		return false;
	}

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

	public double getLength()
	{
		return length;
	}

	public String getName()
	{
		return name;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public void setY(double y)
	{
		this.y = y;
	}
}
