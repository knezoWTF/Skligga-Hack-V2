package net.skliggahack.event.events;

import net.minecraft.client.gui.DrawContext;
import net.skliggahack.event.Event;
import net.skliggahack.event.Listener;

import java.util.ArrayList;

public interface RenderHudListener extends Listener
{
	void onRenderHud(DrawContext context, double partialTicks);

	class RenderHudEvent extends Event<RenderHudListener>
	{

		private final DrawContext context;
		private final double partialTicks;

		public RenderHudEvent(DrawContext context, double partialTicks)
		{
			this.context = context;
			this.partialTicks = partialTicks;
		}

		@Override
		public void fire(ArrayList<RenderHudListener> listeners)
		{
			listeners.forEach(e -> e.onRenderHud(context, partialTicks));
		}

		@Override
		public Class<RenderHudListener> getListenerType()
		{
			return RenderHudListener.class;
		}
	}
}
