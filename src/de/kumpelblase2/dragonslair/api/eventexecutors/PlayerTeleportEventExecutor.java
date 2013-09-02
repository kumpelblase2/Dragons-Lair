package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.utilities.WorldUtility;

public class PlayerTeleportEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			final String scope = e.getOption("scope");
			final String world = e.getOption("world");
			final int x = Integer.parseInt(e.getOption("x"));
			final int y = Integer.parseInt(e.getOption("y"));
			final int z = Integer.parseInt(e.getOption("z"));
			if(scope == null || scope.equalsIgnoreCase("single"))
				WorldUtility.enhancedTeleport(p, new Location(Bukkit.getWorld(world), x, y, z));
			else
			{
				final ActiveDungeon d = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				for(final String member : d.getCurrentParty().getMembers())
				{
					final Player pl = Bukkit.getPlayer(member);
					WorldUtility.enhancedTeleport(pl, new Location(Bukkit.getWorld(world), x, y, z));
				}
			}
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