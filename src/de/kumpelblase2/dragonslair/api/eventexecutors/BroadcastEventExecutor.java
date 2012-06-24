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
	public boolean executeEvent(Event e, Player p)
	{
		String message = e.getOption("message");
		String dungeon = e.getOption("dungeon_id");
		String permission = e.getOption("permission");
		
		try
		{
			if(dungeon != null)
			{
				int id = Integer.parseInt(dungeon);
				ActiveDungeon ad;
				ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad == null || ad.getInfo().getID() != id)
				{
					for(ActiveDungeon a : DragonsLairMain.getInstance().getDungeonManager().getActiveDungeons())
					{
						if(ad.getInfo().getID() == id)
						{
							ad = a;
							break;
						}
					}
				}
				
				for(String member : ad.getCurrentParty().getMembers())
				{
					Player pl = Bukkit.getPlayer(member);
					if(permission != null)
					{
						if(pl.hasPermission(permission))
							pl.sendRawMessage(GeneralUtilities.replaceColors(message));
					}
				}
			}
			else
			{
				if(permission != null)
					Bukkit.broadcast(GeneralUtilities.replaceColors(message), permission);
				else
					Bukkit.broadcastMessage(GeneralUtilities.replaceColors(message));
			}
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to execute event with id " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		
		return true;
	}

}
