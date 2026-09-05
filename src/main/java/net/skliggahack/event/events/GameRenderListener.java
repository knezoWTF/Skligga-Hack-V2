package net.skliggahack.event.events;

import net.skliggahack.event.Event;
import net.skliggahack.event.Listener;

import java.util.ArrayList;

public interface GameRenderListener extends Listener
{
	void onGameRender(float tickDelta);

	class GameRenderEvent extends Event<GameRenderListener>
	{

		private float tickDelta;

		public GameRenderEvent(float tickDelta)
		{
			this.tickDelta = tickDelta;
		}

		@Override
		public void fire(ArrayList<GameRenderListener> listeners)
		{
			listeners.forEach(e -> e.onGameRender(tickDelta));
		}

		@Override
		public Class<GameRenderListener> getListenerType()
		{
			return GameRenderListener.class;
		}
	}
}
