package net.skliggahack.gui.component;

import net.minecraft.client.gui.DrawContext;
import net.skliggahack.gui.window.Window;
import net.skliggahack.util.RenderUtils;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CheckboxComponent extends Component
{

	private final static double checkboxSize = 10;

	private boolean value;
	private final Consumer<Boolean> action;
	private final Supplier<Boolean> availability;

	public CheckboxComponent(Window parent, double x, double y, boolean value, Consumer<Boolean> action, Supplier<Boolean> availability, String name)
	{
		super(parent, x, y, 10, name);
		this.value = value;
		this.action = action;
		this.availability = availability;
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
		double x2 = x + checkboxSize;
		double y2 = Math.min(getY() + parentY + checkboxSize, parentY2);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		RenderUtils.outline(context, x, y, x2, y2, 0xFF999999);
		if (!availability.get() && !value)
		{
			RenderUtils.fill(context, x, y, x2, y2, 0xFF666666);
		}
		if (value)
		{
			x += 1;
			y += 1;
			x2 -= 1;
			y2 -= 1;
			int color = 0xFF999999;
			if (availability.get())
				color = 0xFF00CC00;
			RenderUtils.fill(context, x, y, x2, y2, color);
		}
	}

	@Override
	public void onMouseClicked(double mouseX, double mouseY, int button)
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
		double x2 = x + checkboxSize;
		double y2 = Math.min(getY() + parentY + checkboxSize, parentY2);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2))
		{
			value = !value;
			action.accept(value);
		}
	}
}
