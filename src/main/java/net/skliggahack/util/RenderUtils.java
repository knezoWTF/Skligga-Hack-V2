package net.skliggahack.util;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import static net.skliggahack.SkliggaHack.MC;

public enum RenderUtils
{
	;

	public static void fill(DrawContext context, double x1, double y1, double x2, double y2, int color)
	{
		context.fill((int) x1, (int) y1, (int) x2, (int) y2, color);
	}

	public static void outline(DrawContext context, double x1, double y1, double x2, double y2, int color)
	{
		int ix1 = (int) x1, iy1 = (int) y1, ix2 = (int) x2, iy2 = (int) y2;
		context.fill(ix1, iy1, ix2, iy1 + 1, color);
		context.fill(ix1, iy2 - 1, ix2, iy2, color);
		context.fill(ix1, iy1, ix1 + 1, iy2, color);
		context.fill(ix2 - 1, iy1, ix2, iy2, color);
	}

	public static void drawText(DrawContext context, String text, double x, double y, int color)
	{
		context.drawTextWithShadow(MC.textRenderer, text, (int) x, (int) y, color);
	}

	public static void drawSolidBox(Box bb, int color)
	{
		try (var scope = MC.worldRenderer.startDrawingGizmos())
		{
			GizmoDrawing.box(bb, DrawStyle.filled(color));
		}
	}

	public static void drawOutlinedBox(Box bb, int color)
	{
		try (var scope = MC.worldRenderer.startDrawingGizmos())
		{
			GizmoDrawing.box(bb, DrawStyle.stroked(color));
		}
	}

	public static void drawSolidBox(BlockPos pos, int color)
	{
		drawSolidBox(new Box(pos), color);
	}

	public static void drawOutlinedBox(BlockPos pos, int color)
	{
		drawOutlinedBox(new Box(pos), color);
	}

	public static boolean isHoveringOver(double mouseX, double mouseY, double x1, double y1, double x2, double y2)
	{
		return mouseX > Math.min(x1, x2) && mouseX < Math.max(x1, x2) && mouseY > Math.min(y1, y2) && mouseY < Math.max(y1, y2);
	}

	public static Vec3d getCameraPos()
	{
		return MC.gameRenderer.getCamera().getCameraPos();
	}

	public static BlockPos getCameraBlockPos()
	{
		return MC.gameRenderer.getCamera().getBlockPos();
	}

}
