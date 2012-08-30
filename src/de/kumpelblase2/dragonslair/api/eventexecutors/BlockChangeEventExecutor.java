package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class BlockChangeEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			final String world = e.getOption("world");
			final int id = Integer.parseInt(e.getOption("block_id"));
			final int x = Integer.parseInt(e.getOption("x"));
			final int y = Integer.parseInt(e.getOption("y"));
			final int z = Integer.parseInt(e.getOption("z"));
			final int x2 = (e.getOption("x2") == null) ? x : Integer.parseInt(e.getOption("x2"));
			final int y2 = (e.getOption("y2") == null) ? y : Integer.parseInt(e.getOption("y2"));
			final int z2 = (e.getOption("z2") == null) ? z : Integer.parseInt(e.getOption("z2"));
			final int minx = (x > x2) ? x2 : x;
			final int maxx = (x < x2) ? x2 : x;
			final int miny = (y > y2) ? y2 : y;
			final int maxy = (y < y2) ? y2 : y;
			final int minz = (z > z2) ? z2 : z;
			final int maxz = (z < z2) ? z2 : z;
			final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
			Bukkit.getScheduler().scheduleSyncDelayedTask(DragonsLairMain.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					for(int x = minx; x <= maxx; x++)
						for(int y = miny; y <= maxy; y++)
							for(int z = minz; z <= maxz; z++)
							{
								final Block b = Bukkit.getWorld(world).getBlockAt(x, y, z);
								if(ad != null)
									DragonsLairMain.getInstance().getLoggingManager().logBlockPlace(ad, b.getState());
								Bukkit.getWorld(world).getBlockAt(x, y, z).setTypeId(id);
							}
				}
			});
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
