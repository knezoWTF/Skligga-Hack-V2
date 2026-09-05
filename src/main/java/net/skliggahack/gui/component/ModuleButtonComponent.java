package net.skliggahack.gui.component;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.skliggahack.gui.ClickGui;
import net.skliggahack.gui.window.ModuleSettingWindow;
import net.skliggahack.gui.window.Window;
import net.skliggahack.module.Module;
import net.skliggahack.util.RenderUtils;
import org.lwjgl.glfw.GLFW;

import static net.skliggahack.SkliggaHack.MC;

public class ModuleButtonComponent extends Component
{

	private final Module module;
	private boolean settingWindowOpened = false;
	private ModuleSettingWindow moduleSettingWindow;

	public ModuleButtonComponent(Window parent, Module module, double x, double y)
	{
		super(parent, x, y, 10, module.getName());
		this.module = module;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		double parentX = parent.getX();
		double parentY = parent.getY();
		double parentWidth = parent.getWidth();
		double parentLength = parent.getLength();
		double parentX2 = parent.getX() + parentWidth;
		double parentY2 = parent.getY() + parentLength;
		double x = getX() + parentX;
		double y = Math.max(getY() + parentY, parentY);
		double x2 = parentX2 - getX();
		double y2 = Math.min(getY() + parentY + 10, parentY2);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		int color;
		if (parent == parent.parent.getTopWindow() && RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2))
		{
			if (module.isEnabled())
				color = 0xFF66CC66;
			else
				color = 0xFF999999;
		}
		else
		{
			if (module.isEnabled())
				color = 0xFF00CC00;
			else
				color = 0x66666666;
		}
		RenderUtils.fill(context, x, y, x2, y2, color);
		double textX = x + 2;
		double textY = y + 1;
		String trimmed = MC.textRenderer.trimToWidth(module.getName(), (int) (x2 - textX));
		context.drawTextWithShadow(MC.textRenderer, trimmed, (int) textX, (int) textY, 0xFF000000);
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
		double y = getY() + parentY;
		double x2 = parentX2 - getX();
		double y2 = Math.min(y + 20, parentY2);
		if (getY() + 10 <= 0)
			return;
		if (parentY2 - (getY() + parentY) <= 0)
			return;
		if (RenderUtils.isHoveringOver(mouseX, mouseY, x, y, x2, y2))
		{
			if (button == GLFW.GLFW_MOUSE_BUTTON_1)
			{
				module.toggle();
			}
			else
			{
				if (!settingWindowOpened)
				{
					ClickGui gui = parent.parent;
					moduleSettingWindow = new ModuleSettingWindow(gui, mouseX, mouseY, module, this);
					gui.add(moduleSettingWindow);
					settingWindowOpened = true;
				}
				else
				{
					parent.parent.moveToTop(moduleSettingWindow);
				}
			}
		}
	}

	public void settingWindowClosed()
	{
		settingWindowOpened = false;
		moduleSettingWindow = null;
	}
}
