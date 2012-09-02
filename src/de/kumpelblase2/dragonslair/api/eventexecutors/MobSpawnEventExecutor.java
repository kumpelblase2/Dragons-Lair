package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.*;
import de.kumpelblase2.dragonslair.api.*;

public class MobSpawnEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
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
			final String world = e.getOption("world");
			final int x = Integer.parseInt(e.getOption("x"));
			final int y = Integer.parseInt(e.getOption("y"));
			final int z = Integer.parseInt(e.getOption("z"));
			final World w = Bukkit.getWorld(world);
			final Location l = new Location(w, x, y, z);
			final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
			final DungeonManager dm = DragonsLairMain.getDungeonManager();
			for(int i = 0; i < amount; i++)
				if(ad != null)
					dm.getSpawnedMobs().add(new EventMonster(e, ad, (LivingEntity)w.spawnEntity(l, mobType)));
				else
					w.spawnEntity(l, mobType);
		}
		catch(final Exception ex)
		{
			DragonsLairMain.Log.warning("Couldn't execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		return true;
	}
}
