package de.kumpelblase2.dragonslair.api;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class ItemTracker
{
	private final Set<DroppedItemEntry> droppedItems = new HashSet<DroppedItemEntry>();
	
	public void addItem(Item item, String dungeon)
	{
		this.droppedItems.add(new DroppedItemEntry(item, dungeon));
	}
	
	public void removeItem(Item item)
	{
		this.removeItem(item.getEntityId());
	}
	
	public void removeItem(int itemID)
	{
		Iterator<DroppedItemEntry> it = droppedItems.iterator();
		while(it.hasNext())
		{
			DroppedItemEntry entry = it.next();
			if(entry.getEntityID() == itemID)
			{
				it.remove();
				return;
			}
		}
	}
	
	public boolean canCollect(Item item, Player p)
	{
		return this.canCollect(item.getEntityId(), p);
	}
	
	public boolean canCollect(int itemID, Player p)
	{
		ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		String dungeon = (ad == null ? "_GENERAL_" : ad.getInfo().getName());
		for(DroppedItemEntry entry : this.droppedItems)
		{
			if(entry.getEntityID() == itemID)
			{
				if(entry.getDungeon().equals(dungeon) || DragonsLairMain.canPlayersInteract())
					return true;
				return false;
			}
		}
		return true;
	}
}
