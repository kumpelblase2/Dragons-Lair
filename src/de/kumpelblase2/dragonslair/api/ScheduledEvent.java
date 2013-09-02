package de.kumpelblase2.dragonslair.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import de.kumpelblase2.dragonslair.*;

public class ScheduledEvent
{
	private final List<Integer> eventIDs = new ArrayList<Integer>();
	private boolean autoStart;
	private boolean repeat;
	private int startDelay;
	private int repeatDelay;
	private int id = -1;

	public ScheduledEvent()
	{
	}

	public ScheduledEvent(final ResultSet result)
	{
		try
		{
			final String events = result.getString(TableColumns.Scheduled_Events.EVENT_IDS);
			for(final String event : events.split(":"))
			{
				this.eventIDs.add(Integer.parseInt(event));
			}

			this.autoStart = result.getBoolean(TableColumns.Scheduled_Events.AUTO_START);
			this.repeat = result.getBoolean(TableColumns.Scheduled_Events.REPEATING);
			this.repeatDelay = result.getInt(TableColumns.Scheduled_Events.REPEATING_DELAY);
			this.startDelay = result.getInt(TableColumns.Scheduled_Events.INIT_DELAY);
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setAutoStart(final boolean auto)
	{
		this.autoStart = auto;
	}

	public boolean shouldStartAutomatically()
	{
		return this.autoStart;
	}

	public void addEventID(final Integer id)
	{
		this.eventIDs.add(id);
	}

	public void addEventIDs(final List<Integer> ids)
	{
		this.eventIDs.addAll(ids);
	}

	public void removeEventID(final Integer id)
	{
		this.eventIDs.remove(id);
	}

	public void setRepeat(final boolean repeat)
	{
		this.repeat = repeat;
	}

	public boolean shouldRepeat()
	{
		return this.repeat;
	}

	public void setInitDelay(final int delay)
	{
		this.startDelay = delay;
	}

	public int getInitDelay()
	{
		return this.startDelay;
	}

	public void setRepeatDelay(final int delay)
	{
		this.repeatDelay = delay;
	}

	public int getRepeatDelay()
	{
		return this.repeatDelay;
	}

	public int getID()
	{
		return this.id;
	}

	public void save()
	{
		try
		{
			if(this.id == -1)
			{
				final PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.SCHEDULED_EVENTS + "(" + "event_ids," + "repeating," + "init_delay," + "repeating_delay," + "auto_start" + ") VALUES(?,?,?,?,?)");
				st.setString(1, this.getEventIDString());
				st.setBoolean(2, this.repeat);
				st.setInt(3, this.startDelay);
				st.setInt(4, this.repeatDelay);
				st.setBoolean(5, this.autoStart);
				st.execute();
				final ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
			else
			{
				final PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.SCHEDULED_EVENTS + "(" + "schedule_id," + "event_ids," + "repeating," + "init_delay," + "repeating_delay," + "auto_start" + ") VALUES(?,?,?,?,?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.getEventIDString());
				st.setBoolean(3, this.repeat);
				st.setInt(4, this.startDelay);
				st.setInt(5, this.repeatDelay);
				st.setBoolean(6, this.autoStart);
				if(!st.execute())
					DragonsLairMain.Log.warning("Unable to save event " + this.id);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save event " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}

	public String getEventIDString()
	{
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i < this.eventIDs.size(); i++)
		{
			sb.append(this.eventIDs.get(i));
			if(i != this.eventIDs.size() - 1)
				sb.append(",");
		}

		return sb.toString();
	}

	public List<Integer> getEventIDs()
	{
		return this.eventIDs;
	}

	public void remove()
	{
		if(this.id == -1)
			return;

		try
		{
			final PreparedStatement st = DragonsLairMain.createStatement("DELETE FROM " + Tables.SCHEDULED_EVENTS + " WHERE `schedule_id` = ?");
			st.setInt(1, this.id);
			st.execute();
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove scheduled event " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
}