package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class SetLevelEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			int level = Integer.parseInt(e.getOption("level"));
			String scope = e.getOption("level");
			if(scope == null || scope.equalsIgnoreCase("single") || scope.equalsIgnoreCase("interactor"))
			{
				p.setLevel(level);
			}
			else
			{
				ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad != null)
				{
					for(String meber : ad.getCurrentParty().getMembers())
					{
						Bukkit.getPlayer(meber).setLevel(level);
					}
				}
			}
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to parse event " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		return true;
	}
}
