package de.kumpelblase2.dragonslair.events.dungeon;

import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.api.Dungeon;
import de.kumpelblase2.dragonslair.events.BaseEvent;

public class DungeonStartEvent extends BaseEvent
{
	private final Dungeon dungeon;
	private static final HandlerList handlers = new HandlerList();

	public DungeonStartEvent(final Dungeon dungeon)
	{
		this.dungeon = dungeon;
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

	public Dungeon getDungeon()
	{
		return this.dungeon;
	}
}