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
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			String scope = e.getOption("scope");
			String world = e.getOption("world");
			int x = Integer.parseInt(e.getOption("x"));
			int y = Integer.parseInt(e.getOption("y"));
			int z = Integer.parseInt(e.getOption("z"));
			if(scope == null || scope.equalsIgnoreCase("single"))
			{
				WorldUtility.enhancedTelepot(p, new Location(Bukkit.getWorld(world), x, y, z));
			}
			else
			{
				ActiveDungeon d = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
				for(String member : d.getCurrentParty().getMembers())
				{
					Player pl = Bukkit.getPlayer(member);
					WorldUtility.enhancedTelepot(pl, new Location(Bukkit.getWorld(world), x, y, z));
				}
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
