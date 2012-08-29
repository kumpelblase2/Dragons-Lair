package de.kumpelblase2.dragonslair.events.dungeon;

import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.events.BaseEvent;

public class ChapterChangeEvent extends BaseEvent
{
	private final ActiveDungeon dungeon;
	private final Chapter nextChapter;
	
	public ChapterChangeEvent(ActiveDungeon dungeon, Chapter chapter)
	{
		this.dungeon = dungeon;
		this.nextChapter = chapter;
	}
	
	public Dungeon getDungeon()
	{
		return this.dungeon.getInfo();
	}
	
	public ActiveDungeon getActiveDungeon()
	{
		return this.dungeon;
	}
	
	public Chapter getNextChapter()
	{
		return this.nextChapter;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
