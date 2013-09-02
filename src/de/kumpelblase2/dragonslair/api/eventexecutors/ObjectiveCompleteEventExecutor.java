package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.events.dungeon.ObjectiveChangeEvent;

public class ObjectiveCompleteEventExecutor implements EventExecutor
{
	@Override
	public boolean executeEvent(final Event e, final Player p)
	{
		try
		{
			final int nextID = Integer.parseInt(e.getOption("objective_id"));
			final ActiveDungeon d = DragonsLairMain.getDungeonManager().getDungeonOfPlayer(p.getName());
			if(d == null)
				return false;

			final Objective o = DragonsLairMain.getSettings().getObjectives().get(nextID);
			if(o == null)
				return false;

			final ObjectiveChangeEvent event = new ObjectiveChangeEvent(d, o);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled())
				d.setNextObjective(event.getNextObjective());
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