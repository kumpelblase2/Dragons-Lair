package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class ChangeHealthEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			int amount = Integer.parseInt(e.getOption("amount"));
			String type = e.getOption("change_type");
			String scope = e.getOption("scope");
			
			if(scope == null || scope.equalsIgnoreCase("single") || scope.equalsIgnoreCase("interactor"))
			{
				if(type.equals("set"))
				{
					if(amount > 20)
						amount = 20;
					if(amount < 0)
						amount = 0;
					p.setHealth(amount);
				}
				else if(type.equals("add"))
				{
					if(p.getHealth() + amount >= 20)
						p.setHealth(20);
					else
						p.setHealth(p.getHealth() + amount);
				}
				else if(type.equals("remove"))
				{
					if(p.getHealth() - amount <= 0)
						p.setHealth(0);
					else
						p.setHealth(p.getHealth() - amount);
				}
			}
			else
			{
				ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad != null)
				{
					for(String member : ad.getCurrentParty().getMembers())
					{
						Player pl = Bukkit.getPlayer(member);
						if(type.equals("set"))
						{
							if(amount > 20)
								amount = 20;
							if(amount < 0)
								amount = 0;
							pl.setHealth(amount);
						}
						else if(type.equals("add"))
						{
							if(pl.getHealth() + amount >= 20)
								pl.setHealth(20);
							else
								pl.setHealth(pl.getHealth() + amount);
						}
						else if(type.equals("remove"))
						{
							if(pl.getHealth() - amount <= 0)
								pl.setHealth(0);
							else
								pl.setHealth(pl.getHealth() - amount);
						}
					}
				}
				else
					return false;
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
