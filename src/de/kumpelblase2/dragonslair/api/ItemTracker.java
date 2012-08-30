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

	public void addItem(final Item item, final String dungeon)
	{
		this.droppedItems.add(new DroppedItemEntry(item, dungeon));
	}

	public void removeItem(final Item item)
	{
		this.removeItem(item.getEntityId());
	}

	public void removeItem(final int itemID)
	{
		final Iterator<DroppedItemEntry> it = this.droppedItems.iterator();
		while(it.hasNext())
		{
			final DroppedItemEntry entry = it.next();
			if(entry.getEntityID() == itemID)
			{
				it.remove();
				return;
			}
		}
	}

	public boolean canCollect(final Item item, final Player p)
	{
		return this.canCollect(item.getEntityId(), p);
	}

	public boolean canCollect(final int itemID, final Player p)
	{
		final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
		final String dungeon = (ad == null ? "_GENERAL_" : ad.getInfo().getName());
		for(final DroppedItemEntry entry : this.droppedItems)
			if(entry.getEntityID() == itemID)
			{
				if(entry.getDungeon().equals(dungeon) || DragonsLairMain.canPlayersInteract())
					return true;
				return false;
			}
		return true;
	}
}
