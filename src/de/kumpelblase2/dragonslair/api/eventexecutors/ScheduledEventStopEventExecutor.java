package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.api.Event;

public class ScheduledEventStopEventExecutor implements EventExecutor
{

	@Override
	public boolean executeEvent(Event e, Player p)
	{
		try
		{
			Integer id = Integer.parseInt(e.getOption("event_id"));
			DragonsLairMain.getEventScheduler().cancelEvent(id);
		}
		catch(Exception ex)
		{
			DragonsLairMain.Log.warning("Unable to execute event with id: " + e.getID());
			DragonsLairMain.Log.warning(ex.getMessage());
			return false;
		}
		return true;
	}

}
