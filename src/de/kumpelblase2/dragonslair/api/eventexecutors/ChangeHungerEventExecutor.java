package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class ChangeHungerEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			int amount = Integer.parseInt(e.getOption("amount"));
			final String type = e.getOption("change_type");
			final String scope = e.getOption("scope");
			if(scope == null || scope.equalsIgnoreCase("single") || scope.equalsIgnoreCase("interactor"))
			{
				if(type.equals("set"))
				{
					if(amount > 20)
						amount = 20;
					if(amount < 0)
						amount = 0;
					p.setFoodLevel(amount);
				}
				else if(type.equals("add"))
				{
					if(p.getFoodLevel() + amount >= 20)
						p.setFoodLevel(20);
					else
						p.setFoodLevel(p.getFoodLevel() + amount);
				}
				else if(type.equals("remove"))
					if(p.getFoodLevel() - amount <= 0)
						p.setFoodLevel(0);
					else
						p.setFoodLevel(p.getFoodLevel() - amount);
			}
			else
			{
				final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad != null)
					for(final String member : ad.getCurrentParty().getMembers())
					{
						final Player pl = Bukkit.getPlayer(member);
						if(type.equals("set"))
						{
							if(amount > 20)
								amount = 20;
							if(amount < 0)
								amount = 0;
							pl.setFoodLevel(amount);
						}
						else if(type.equals("add"))
						{
							if(pl.getFoodLevel() + amount >= 20)
								pl.setFoodLevel(20);
							else
								pl.setFoodLevel(pl.getFoodLevel() + amount);
						}
						else if(type.equals("remove"))
							if(pl.getFoodLevel() - amount <= 0)
								pl.setFoodLevel(0);
							else
								pl.setFoodLevel(pl.getFoodLevel() - amount);
					}
				else
					return false;
			}
		}
		catch(final Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to parse event " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		return true;
	}
}
