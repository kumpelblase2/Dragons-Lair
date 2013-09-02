package de.kumpelblase2.dragonslair.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.Tables;
import de.kumpelblase2.dragonslair.tasks.EventSchedulingTask;

public class EventScheduler
{
	private final Map<Integer, ScheduledEvent> events = new HashMap<Integer, ScheduledEvent>();
	private final Map<Integer, Integer> scheduledIDs = new HashMap<Integer, Integer>();

	public void load()
	{
		try
		{
			final PreparedStatement st = DragonsLairMain.createStatement("SELECT * FROM " + Tables.SCHEDULED_EVENTS);
			final ResultSet rs = st.executeQuery();
			while(rs.next())
			{
				final ScheduledEvent event = new ScheduledEvent(rs);
				this.events.put(event.getID(), event);
				if(event.shouldStartAutomatically())
				{
					DragonsLairMain.debugLog("Starting scheduled event with id '" + event.getID() + "'");
					if(event.shouldRepeat())
						this.scheduledIDs.put(event.getID(), Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonsLairMain.getInstance(), new EventSchedulingTask(event), event.getInitDelay() * 20, event.getRepeatDelay() * 20));
					else
						Bukkit.getScheduler().scheduleSyncDelayedTask(DragonsLairMain.getInstance(), new EventSchedulingTask(event), event.getInitDelay() * 20);
				}
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.warning("Unable to load scheduled events:");
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}

	public void startEvent(final ScheduledEvent event)
	{
		if(event == null)
			return;

		DragonsLairMain.debugLog("Starting scheduled event with id '" + event.getID() + "'");
		if(event.shouldRepeat())
			this.scheduledIDs.put(event.getID(), Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonsLairMain.getInstance(), new EventSchedulingTask(event), event.getInitDelay() * 20, event.getRepeatDelay() * 20));
		else
			Bukkit.getScheduler().scheduleSyncDelayedTask(DragonsLairMain.getInstance(), new EventSchedulingTask(event), event.getInitDelay() * 20);
	}

	public void startEvent(final Integer id)
	{
		if(id == null)
			return;

		this.startEvent(this.events.get(id));
	}

	public ScheduledEvent getEventByID(final int eventID)
	{
		return this.events.get(eventID);
	}

	public void cancelEvent(final Integer id)
	{
		if(!this.scheduledIDs.containsKey(id))
			return;

		Bukkit.getScheduler().cancelTask(this.scheduledIDs.get(id));
	}

	public Map<Integer, ScheduledEvent> getEvents()
	{
		return this.events;
	}

	public void addEvent(final ScheduledEvent event)
	{
		this.events.put(event.getID(), event);
		if(event.shouldStartAutomatically())
			this.startEvent(event);
	}

	public void removeEvent(final Integer id)
	{
		this.cancelEvent(id);
		this.events.get(id).remove();
		this.events.remove(id);
	}
}