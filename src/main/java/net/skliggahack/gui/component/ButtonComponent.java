package net.skliggahack.gui.component;

import net.minecraft.client.gui.DrawContext;
import net.skliggahack.gui.window.Window;
import net.skliggahack.util.RenderUtils;
import org.lwjgl.glfw.GLFW;

import java.util.function.Supplier;

public class ButtonComponent extends Component
{

	private final Runnable action;
	private final Supplier<String> display;

	public ButtonComponent(Window parent, double x, double y, double length, String name, Runnable action, Supplier<String> display)
	{
		super(parent, x, y, length, name);
		this.action = action;
		this.display = display;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);
		double parentX = parent.getX();
		double parentY = parent.getY();
		double parentWidth = parent.getWidth();
		double parentLength = parent.getLength();
		double parentX2 = parent.getX() + parentWidth;
		double parentY2 = parent.getY() + parentLength;
		double x = getX() + parentX;
		double y = Math.max(getY() + parentY, parentY);
		double x2 = Math.min(x + getLength(), parentX2);
		double y2 = Math.min(y + 10, parentY2);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		int color = 0x66666666;
		if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2))
			color = 0xFF999999;
		RenderUtils.fill(context, x, y, x2, y2, color);
		RenderUtils.drawText(context, display.get(), x, y, 0xFFFFFFFF);
	}

	@Override
	public void onMouseClicked(double mouseX, double mouseY, int button)
	{
		double parentX = parent.getX();
		double parentY = parent.getY();
		double parentWidth = parent.getWidth();
		double parentLength = parent.getLength();
		double parentX2 = parent.getX() + parentWidth;
		double parentY2 = parent.getY() + parentLength;
		double x = getX() + parentX;
		double y = Math.max(getY() + parentY, parentY);
		double x2 = Math.min(x + getLength(), parentX2);
		double y2 = Math.min(y + 10, parentY2);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2))
		{
			if (button == GLFW.GLFW_MOUSE_BUTTON_1)
			{
				action.run();
			}
		}
	}
}
