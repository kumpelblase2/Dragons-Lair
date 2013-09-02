package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;

public class BroadcastEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		final String message = e.getOption("message");
		final String dungeon = e.getOption("dungeon_id");
		final String permission = e.getOption("permission");
		ActiveDungeon ad = null;
		if(dungeon != null)
		{
			try
			{
				final Integer id = Integer.parseInt(dungeon);
				ad = DragonsLairMain.getDungeonManager().getActiveDungeonByID(id);
			}
			catch(final Exception ex)
			{
				ad = DragonsLairMain.getDungeonManager().getActiveDungeonByName(dungeon);
			}
		}

		try
		{
			if(ad != null)
			{
				for(final String member : ad.getCurrentParty().getMembers())
				{
					final Player pl = Bukkit.getPlayer(member);
					if(permission != null)
						if(pl.hasPermission(permission)) pl.sendRawMessage(GeneralUtilities.replaceColors(message));
				}
			}
			else if(permission != null)
				Bukkit.broadcast(GeneralUtilities.replaceColors(message), permission);
			else
				Bukkit.broadcastMessage(GeneralUtilities.replaceColors(message));
		}
		catch(final Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to execute event with id " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}

		return true;
	}
}