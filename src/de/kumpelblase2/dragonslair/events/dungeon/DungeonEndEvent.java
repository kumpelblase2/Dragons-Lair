package de.kumpelblase2.dragonslair.events.dungeon;

import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.events.BaseEvent;

public class DungeonEndEvent extends BaseEvent
{
	private ActiveDungeon dungeon;
	
	public DungeonEndEvent(ActiveDungeon dungeon)
	{
		this.dungeon = dungeon;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
	
	public ActiveDungeon getDungeon()
	{
		return this.dungeon;
	}
}
