package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class ChangeLevelEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			int level = Integer.parseInt(e.getOption("amount"));
			String scope = e.getOption("scope");
			if(scope == null || scope.equalsIgnoreCase("single") || scope.equalsIgnoreCase("interactor"))
			{
				String type = e.getOption("change_type");
				if(type.equals("set"))
					p.setLevel(level);
				else if(type.equals("add"))
					p.setLevel(p.getLevel() + level);
				else if(type.equals("remove"))
					p.setLevel(p.getLevel() - level);
			}
			else
			{
				ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad != null)
				{
					for(String member : ad.getCurrentParty().getMembers())
					{
						Player pl = Bukkit.getPlayer(member);
						String type = e.getOption("change_type");
						if(type.equals("set"))
							pl.setLevel(level);
						else if(type.equals("add"))
							pl.setLevel(pl.getLevel() + level);
						else if(type.equals("remove"))
							pl.setLevel(pl.getLevel() - level);
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
