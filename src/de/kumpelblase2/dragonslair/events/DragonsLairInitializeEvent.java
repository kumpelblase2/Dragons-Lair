package de.kumpelblase2.dragonslair.events;

import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class DragonsLairInitializeEvent extends BaseEvent
{
	private final DragonsLairMain instance;
	private static HandlerList handlers = new HandlerList();

	public DragonsLairInitializeEvent(final DragonsLairMain plugin)
	{
		this.instance = plugin;
	}

	public DragonsLairMain getInstance()
	{
		return this.instance;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}
