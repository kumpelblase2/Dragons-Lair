package de.kumpelblase2.dragonslair.events.dungeon;

import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.events.BaseEvent;

public class ObjectiveChangeEvent extends BaseEvent
{
	private final ActiveDungeon dungeon;
	private final Objective nextObjective;
	private static HandlerList handlers = new HandlerList();

	public ObjectiveChangeEvent(final ActiveDungeon d, final Objective objective)
	{
		this.dungeon = d;
		this.nextObjective = objective;
	}

	public Dungeon getDungeon()
	{
		return this.dungeon.getInfo();
	}

	public ActiveDungeon getActiveDungeon()
	{
		return this.dungeon;
	}

	public Objective getNextObjective()
	{
		return this.nextObjective;
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