package net.skliggahack.gui;

import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;
import net.skliggahack.SkliggaHack;
import net.skliggahack.gui.component.ModuleButtonComponent;
import net.skliggahack.gui.window.Window;
import net.skliggahack.module.Category;
import net.skliggahack.module.Module;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.TreeMap;

import static net.skliggahack.SkliggaHack.MC;

public class ClickGui
{

	private final ArrayList<Window> windows = new ArrayList<>();
	private Window draggingWindow = null;
	private double globalShiftX = 0;
	private double globalShiftY = 0;

	private static final Identifier chubLogo = Identifier.of("chub", "logo.png");

	public void init()
	{
		TreeMap<Category, Window> categorizedWindows = new TreeMap<>();
		TreeMap<Category, Double> heights = new TreeMap<>();
		double x = 25;
		for (Category category : Category.values())
		{
			Window window = new Window(this, x, 25, 125, 400);
			window.setTitle(category.toString());
			categorizedWindows.put(category, window);
			heights.put(category, 20.0);
			windows.add(window);
			x += 150;
		}
		for (Module module : SkliggaHack.INSTANCE.getModuleManager().getModules())
		{
			Category category = module.getCategory();
			Window window = categorizedWindows.get(category);
			double y = heights.get(category);
			window.addComponent(new ModuleButtonComponent(window, module, 10, y));
			heights.put(category, y + 20);
		}
	}

	public void render(DrawContext context, int mouseX, int mouseY, float delta)
	{
		//renderLogo(context);
		for (Window window : windows)
		{
			window.setX(window.getX() + globalShiftX);
			window.setY(window.getY() + globalShiftY);
			try
			{
				window.render(context, mouseX, mouseY, delta);
			}
			finally
			{
				window.setX(window.getX() - globalShiftX);
				window.setY(window.getY() - globalShiftY);
			}
		}
	}

	public void handleMouseMoved(double mouseX, double mouseY)
	{
		for (Window window : windows)
		{
			window.onMouseMoved(mouseX, mouseY);
		}
	}

	public void handleMouseClicked(double mouseX, double mouseY, int button)
	{
		int clickedWindowIndex = -1;
		for (int i = windows.size() - 1; i >= 0; i--)
		{
			if (windows.get(i).isHoveringOver(mouseX, mouseY))
			{
				clickedWindowIndex = i;
				break;
			}
		}

		if (clickedWindowIndex == -1)
			return;

		Window clickedWindow = windows.get(clickedWindowIndex);

		if (clickedWindow == getTopWindow())
		{
			clickedWindow.onMouseClicked(mouseX, mouseY, button);
		}

		if (button != GLFW.GLFW_MOUSE_BUTTON_1)
			return;
		if (!windows.contains(clickedWindow))
			return;

		if (clickedWindow.canDrag(mouseX, mouseY))
			draggingWindow = clickedWindow;

		windows.remove(clickedWindowIndex);
		windows.add(clickedWindow);
	}

	public void handleMouseReleased(double mouseX, double mouseY, int button)
	{
		if (button == GLFW.GLFW_MOUSE_BUTTON_1)
			draggingWindow = null;
	}

	public void handleMouseScrolled(double mouseX, double mouseY, double amount)
	{
		Window top = getTopWindow();
		if (top == null)
			return;
		top.onMouseScrolled(mouseX, mouseY, amount);
	}

	public void handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY)
	{
		for (Window window : windows)
		{
			if (window.onMouseDragged(mouseX, mouseY, button, deltaX, deltaY))
				return;
		}
		if (button != GLFW.GLFW_MOUSE_BUTTON_1)
			return;
		if (GLFW.glfwGetKey(MC.getWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS)
		{
			globalShiftX += deltaX;
			globalShiftY += deltaY;
			return;
		}
		if (draggingWindow != null)
		{
			draggingWindow.setX(draggingWindow.getX() + deltaX);
			draggingWindow.setY(draggingWindow.getY() + deltaY);
		}
	}

	public Window getTopWindow()
	{
		int size = windows.size();
		if (size == 0)
			return null;
		return windows.get(size - 1);
	}

	public void moveToTop(Window window)
	{
		windows.remove(window);
		windows.add(window);
	}

	public void add(Window window)
	{
		windows.add(window);
	}

	public void close(Window window)
	{
		window.onClose();
		windows.remove(window);
	}

	public void renderLogo(DrawContext context)
	{
		context.drawTexture(RenderPipelines.GUI_TEXTURED, chubLogo, 0, 3, 0, 0, 80, 60, 80, 60);
	}
}
