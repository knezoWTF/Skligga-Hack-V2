package net.skliggahack.gui.component;

import net.minecraft.client.gui.DrawContext;
import net.skliggahack.gui.window.Window;
import net.skliggahack.util.MathUtils;
import net.skliggahack.util.RenderUtils;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.skliggahack.SkliggaHack.MC;

public class SliderComponent extends Component
{

	private double width;
	private double value;
	private final double min, max, step;
	private final DisplayType displayType;
	private final Consumer<Double> action;
	private final Supplier<Boolean> availability;

	public SliderComponent(Window parent, double x, double y, double width, double value, double min, double max, double step, DisplayType displayType, Consumer<Double> action, Supplier<Boolean> availability, String name)
	{
		super(parent, x, y, 10, name);
		this.width = width;
		this.action = action;
		this.value = value;
		this.min = min;
		this.max = max;
		this.step = step;
		this.displayType = displayType;
		this.availability = availability;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		super.render(context, mouseX, mouseY, delta);
		renderBackGround(context);
		renderSlider(context);
		renderValue(context);
	}

	private void renderBackGround(DrawContext context)
	{
		double parentX = parent.getX();
		double parentY = parent.getY();
		double parentWidth = parent.getWidth();
		double parentLength = parent.getLength();
		double parentX2 = parent.getX() + parentWidth;
		double parentY2 = parent.getY() + parentLength;
		double x = getX() + parentX;
		double y = Math.max(getY() + parentY, parentY);
		double x2 = x + width;
		double y2 = Math.min(getY() + parentY + 10, parentY2);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		RenderUtils.outline(context, x, y, x2, y2, 0xFF999999);
	}

	private void renderSlider(DrawContext context)
	{
		double offset = (value - min) / (max - min) * width;
		double parentX = parent.getX();
		double parentY = parent.getY();
		double parentWidth = parent.getWidth();
		double parentLength = parent.getLength();
		double parentX2 = parent.getX() + parentWidth;
		double parentY2 = parent.getY() + parentLength;
		double x = getX() + parentX + offset - 2;
		double y = Math.max(getY() + parentY, parentY);
		double x2 = x + 4;
		double y2 = Math.min(getY() + parentY + 10, parentY2);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		int color = 0xFF666666;
		if (availability.get())
			color = 0xFF00CC00;
		RenderUtils.fill(context, x, y, x2, y2, color);
	}

	private void renderValue(DrawContext context)
	{
		double parentX = parent.getX();
		double parentY = parent.getY();
		double parentWidth = parent.getWidth();
		double parentLength = parent.getLength();
		double parentX2 = parent.getX() + parentWidth;
		double parentY2 = parent.getY() + parentLength;
		double x = getX() + parentX + width / 2;
		double y = Math.max(getY() + parentY, parentY);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		String display = null;
		switch (displayType)
		{
			case DECIMAL -> display = String.format("%.3f", value);
			case INTEGER -> display = String.valueOf((int) value);
		}
		RenderUtils.drawText(context, display, x, y, 0xFFFFFFFF);
	}

	private void slide(double mouseX, double mouseY)
	{
		if (!availability.get())
			return;
		double parentX = parent.getX();
		double parentY = parent.getY();
		double parentWidth = parent.getWidth();
		double parentLength = parent.getLength();
		double parentX2 = parent.getX() + parentWidth;
		double parentY2 = parent.getY() + parentLength;
		double x = getX() + parentX;
		double y = Math.max(getY() + parentY, parentY);
		double x2 = x + width;
		double y2 = Math.min(getY() + parentY + 10, parentY2);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2))
		{
			value = (mouseX - x) / width * (max - min) + min;
			value = MathUtils.roundToStep(value, step);
			if (value < min)
				value = min;
			if (value > max)
				value = max;
			action.accept(value);
		}
	}

	@Override
	public void onMouseMoved(double mouseX, double mouseY)
	{
		if (GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_1) != GLFW.GLFW_PRESS && GLFW.glfwGetMouseButton(MC.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_2) != GLFW.GLFW_PRESS)
			return;
		slide(mouseX, mouseY);
	}

	@Override
	public void onMouseClicked(double mouseX, double mouseY, int button)
	{
		slide(mouseX, mouseY);
	}

	@Override
	public boolean onMouseScrolled(double mouseX, double mouseY, double amount)
	{
		if (!availability.get())
			return false;
		double parentX = parent.getX();
		double parentY = parent.getY();
		double parentWidth = parent.getWidth();
		double parentLength = parent.getLength();
		double parentX2 = parent.getX() + parentWidth;
		double parentY2 = parent.getY() + parentLength;
		double x = getX() + parentX;
		double y = Math.max(getY() + parentY, parentY);
		double x2 = x + width;
		double y2 = Math.min(getY() + parentY + 10, parentY2);
		if (getY() + 10 <= 0)
			return false;
		if (parentY2 - (getY() + parentY) <= 0)
			return false;
		if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2))
		{
			value += -amount * step;
			value = MathUtils.roundToStep(value, step);
			if (value < min)
				value = min;
			if (value > max)
				value = max;
			action.accept(value);
			return true;
		}
		return super.onMouseScrolled(mouseX, mouseY, amount);
	}

	public enum DisplayType
	{
		DECIMAL,
		INTEGER,
		DEGREE
	}
}
