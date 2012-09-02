package de.kumpelblase2.dragonslair.events;

import org.bukkit.event.*;

public abstract class BaseEvent extends Event implements Cancellable
{
	protected static HandlerList handlers = new HandlerList();
	private boolean cancelled = false;

	@Override
	public boolean isCancelled()
	{
		return this.cancelled;
	}

	@Override
	public void setCancelled(final boolean arg0)
	{
		this.cancelled = arg0;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
