package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class DungeonEndEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			final String dungeon_id = e.getOption("dungeon_id");
			final Location bak = p.getLocation();
			final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
			if(ad != null)
			{
				if(e.getOption("give_items") != null && !Boolean.parseBoolean(e.getOption("give_items")))
					ad.getSavedPlayers().clear();
			}

			DragonsLairMain.getDungeonManager().stopDungeon(Integer.parseInt(dungeon_id), false);
			if(e.getOption("warp_on_end") != null && !Boolean.parseBoolean(e.getOption("warp_on_end")))
				p.teleport(bak);
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