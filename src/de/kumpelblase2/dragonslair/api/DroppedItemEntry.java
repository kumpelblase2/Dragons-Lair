package de.kumpelblase2.dragonslair.api;

import org.bukkit.entity.Item;

public class DroppedItemEntry
{
	private final int entityID;
	private final String dungeon;

	public DroppedItemEntry(final Item item, final String dungeon)
	{
		this.entityID = item.getEntityId();
		this.dungeon = dungeon;
	}

	public int getEntityID()
	{
		return this.entityID;
	}

	public String getDungeon()
	{
		return this.dungeon;
	}
}