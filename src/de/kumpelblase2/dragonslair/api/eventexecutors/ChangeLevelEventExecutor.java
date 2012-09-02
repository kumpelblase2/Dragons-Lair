package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.ActiveDungeon;
import de.kumpelblase2.dragonslair.api.Event;

public class ChangeLevelEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			final int level = Integer.parseInt(e.getOption("amount"));
			final String scope = e.getOption("scope");
			if(scope == null || scope.equalsIgnoreCase("single") || scope.equalsIgnoreCase("interactor"))
			{
				final String type = e.getOption("change_type");
				if(type.equals("set"))
					p.setLevel(level);
				else if(type.equals("add"))
					p.setLevel(p.getLevel() + level);
				else if(type.equals("remove"))
					p.setLevel(p.getLevel() - level);
			}
			else
			{
				final ActiveDungeon ad = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
				if(ad != null)
					for(final String member : ad.getCurrentParty().getMembers())
					{
						final Player pl = Bukkit.getPlayer(member);
						final String type = e.getOption("change_type");
						if(type.equals("set"))
							pl.setLevel(level);
						else if(type.equals("add"))
							pl.setLevel(pl.getLevel() + level);
						else if(type.equals("remove"))
							pl.setLevel(pl.getLevel() - level);
					}
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
