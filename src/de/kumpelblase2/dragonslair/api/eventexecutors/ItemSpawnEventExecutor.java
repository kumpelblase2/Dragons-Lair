package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import de.kumpelblase2.dragonslair.api.Event;

public class ItemSpawnEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		
		String world, itemid;
		int x, y, z, amount;
		itemid = e.getOption("item_id");
		world = e.getOption("world");
		try
		{
			x = Integer.parseInt(e.getOption("x"));
			y = Integer.parseInt(e.getOption("y"));
			z = Integer.parseInt(e.getOption("z"));
			if(e.getOption("amount") == null)
				amount = 1;
			else
				amount = Integer.parseInt(e.getOption("amount"));
		}
		catch(Exception ex)
		{
			return false;
		}		
		
		Material m;
		try
		{
			m = Material.getMaterial(Integer.parseInt(itemid));
		}
		catch(Exception ex)
		{
			m = Material.getMaterial(itemid.replace(" ", "_").toUpperCase());
			if(m == null)
				return false;
		}
		
		if(world == null || itemid == null)
			return false;		
		
		World w = Bukkit.getWorld(world);
		if(w != null)
		{
			w.dropItemNaturally(new Location(w, x, y, z), new ItemStack(m, amount));
			return true;
		}
		
		return false;
	}

}
