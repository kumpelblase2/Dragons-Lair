package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.*;
import de.kumpelblase2.dragonslair.api.*;

public class MobSpawnEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			EntityType mobType;
			if(EntityType.fromName(e.getOption("mob_id")) == null)
				mobType = EntityType.fromId(Integer.parseInt(e.getOption("mob_id")));
			else
				mobType = EntityType.fromName(e.getOption("mob_id"));
			
			int amount = 1;
			if(e.getOption("amount") != null)
				amount = Integer.parseInt(e.getOption("amount"));
			
			String world = e.getOption("world");
			int x = Integer.parseInt(e.getOption("x"));
			int y = Integer.parseInt(e.getOption("y"));
			int z = Integer.parseInt(e.getOption("z"));
			World w = Bukkit.getWorld(world);
			Location l = new Location(w, x, y, z);
			ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
			DungeonManager dm = DragonsLairMain.getInstance().getDungeonManager();
			for(int i = 0; i < amount; i++)
			{
				if(ad != null)
					dm.getSpawnedMobs().add(new EventMonster(e, ad, w.spawnCreature(l, mobType)));
				else
					w.spawnCreature(l, mobType);
			}
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Couldn't execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		return true;
	}
}
