package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class KillPlayerEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(Event e, Player p)
	{
		if(e.getOption("scope") == null)
		{
			p.setHealth(0);
		}
		else
		{
			String scope = e.getOption("scope");
			if(scope.equalsIgnoreCase("all") || scope.equalsIgnoreCase("party"))
			{
				ActiveDungeon ad = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad != null)
				{
					for(String member : ad.getCurrentParty().getMembers())
					{
						Bukkit.getPlayer(member).setHealth(0);
					}
				}
			}
			else
			{
				p.setHealth(0);
			}
		}
		
		return false;
	}

}
