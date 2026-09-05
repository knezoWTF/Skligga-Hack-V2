package net.skliggahack.gui.window;


import net.minecraft.client.gui.DrawContext;
import net.skliggahack.gui.ClickGui;
import net.skliggahack.gui.component.Component;
import net.skliggahack.util.RenderUtils;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

import static net.skliggahack.SkliggaHack.MC;

public class Window
{
	public final ClickGui parent;
	private double x, y;
	private double width, length;
	private double scrollAmount = 0;
	protected boolean minimized = false;
	protected ArrayList<Component> components = new ArrayList<>();
	private String title = "";
	private boolean isDraggable = true;
	private boolean draggable = true;
	private boolean closable = true;
	private boolean minimizable = true;
	private boolean resizable = true;
	private boolean pinnable = true;

	public Window(ClickGui parent, double x, double y, double width, int length)
	{
		this.parent = parent;
		this.x = x;
		this.y = y;
		this.width = width;
		this.length = length;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void addComponent(Component component)
	{
		components.add(component);
	}

	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		if (!minimized)
		{
			int bgColor = 0x66666666;
			if (parent.getTopWindow() == this)
				bgColor = 0x99666666;
			RenderUtils.fill(context, x, y, x + width, y + length, bgColor);
			for (Component component : components)
			{
				component.render(context, mouseX, mouseY, delta);
			}
		}
		if (draggable)
		{
			int barColor = 0xFF333333;
			if (parent.getTopWindow() == this)
				barColor = 0xFF00CC00;
			RenderUtils.fill(context, x, y, x + width, y + 10, barColor);
		}
		if (closable)
		{
			double x = getX() + width - 10;
			double y = getY();
			RenderUtils.fill(context, x, y, x + 10, y + 10, 0xFFFF3333);
			RenderUtils.drawText(context, "x", x + 2, y, 0xFFFFFFFF);
		}
		if (minimizable)
		{
			double x = getX() + width - 25;
			double y = getY();
			RenderUtils.fill(context, x, y, x + 10, y + 10, 0xFFFF3333);
			RenderUtils.drawText(context, minimized ? "+" : "-", x + 2, y, 0xFFFFFFFF);
		}
		RenderUtils.drawText(context, title, x + 2, y + 1, 0xFFFFFFFF);
	}

	public void onMouseMoved(double mouseX, double mouseY)
	{
		for (Component component : components)
		{
			component.onMouseMoved(mouseX, mouseY);
		}
	}

	public void onMouseClicked(double mouseX, double mouseY, int button)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_1)
		{
			if (closable)
			{
				double x = getX() + width - 10;
				double y = getY();
				if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + 10, y + 10))
					parent.close(this);
			}
			if (minimizable)
			{
				double x = getX() + width - 25;
				double y = getY();
				if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + 10, y + 10))
					minimized = !minimized;
			}
		}
		if (!minimized && !canDrag(mouseX, mouseY))
		{
			if (RenderUtils.isHoveringOver(mouseX, mouseY, x, draggable ? y + 10 : y, x + width, y + length))
			{
				for (Component component : components)
				{
					component.onMouseClicked(mouseX, mouseY, button);
				}
			}
		}
	}

	public void onMouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (minimized)
			return;
		if (!RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + width, y + length))
			return;
		for (Component component: components)
		{
			if (component.onMouseScrolled(mouseX, mouseY, amount))
				return;
		}

		scrollAmount += amount * 2;
		if (scrollAmount > 0)
			scrollAmount = 0;
		else
		{
			for (Component component : components)
			{
				component.setY(component.getY() + amount * 2);
			}
		}
	}

	public boolean onMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		for (Component component : components)
		{
			if (component.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY))
				return true;
		}
		return false;
	}

	public void onClose()
	{

	}

	public boolean canDrag(double mouseX, double mouseY)
	{
		if (!draggable)
			return false;
		return RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + width, y + 10);
	}

	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
	}

	public double getWidth()
	{
		return width;
	}

	public void setWidth(double width)
	{
		this.width = width;
	}

	public double getLength()
	{
		return length;
	}

	public void setLength(double length)
	{
		this.length = length;
	}

	public boolean isDraggable()
	{
		return isDraggable;
	}

	public void setIsDraggable(boolean isDraggable)
	{
		this.isDraggable = isDraggable;
	}

	public boolean isHoveringOver(double mouseX, double mouseY)
	{
		return minimized ? canDrag(mouseX, mouseY) : RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x + width, y + length);
	}
}
