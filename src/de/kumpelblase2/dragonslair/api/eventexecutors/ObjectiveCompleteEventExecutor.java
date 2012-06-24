package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.events.dungeon.ObjectiveChangeEvent;

public class ObjectiveCompleteEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			int nextID = Integer.parseInt(e.getOption("objective_id"));
			ActiveDungeon d = DragonsLairMain.getInstance().getDungeonManager().getDungeonOfPlayer(p.getName());
			if(d == null)
				return false;
			
			Objective o = DragonsLairMain.getSettings().getObjectives().get(nextID);
			if(o == null)
				return false;
			ObjectiveChangeEvent event = new ObjectiveChangeEvent(d, o);
			Bukkit.getPluginManager().callEvent(event);
			if(!event.isCancelled())
				d.setNextObjective(event.getNextObjective());
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Couldn't execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		return true;
	}

}
