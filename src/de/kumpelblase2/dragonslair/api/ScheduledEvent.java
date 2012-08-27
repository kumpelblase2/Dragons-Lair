package de.kumpelblase2.dragonslair.api;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.TableColumns;
import de.kumpelblase2.dragonslair.Tables;

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
	
	public ScheduledEvent(ResultSet result)
	{
		try
		{
			String events = result.getString(TableColumns.Scheduled_Events.EVENT_IDS);
			for(String event : events.split(":"))
			{
				this.eventIDs.add(Integer.parseInt(event));
			}
			this.autoStart = result.getBoolean(TableColumns.Scheduled_Events.AUTO_START);
			this.repeat = result.getBoolean(TableColumns.Scheduled_Events.REPEATING);
			this.repeatDelay = result.getInt(TableColumns.Scheduled_Events.REPEATING_DELAY);
			this.startDelay = result.getInt(TableColumns.Scheduled_Events.INIT_DELAY);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setAutoStart(boolean auto)
	{
		this.autoStart = auto;
	}
	
	public boolean shouldStartAutomatically()
	{
		return this.autoStart;
	}
	
	public void addEventID(Integer id)
	{
		this.eventIDs.add(id);
	}
	
	public void addEventIDs(List<Integer> ids)
	{
		this.eventIDs.addAll(ids);
	}
	
	public void removeEventID(Integer id)
	{
		this.eventIDs.remove(id);
	}
	
	public void setRepeat(boolean repeat)
	{
		this.repeat = repeat;
	}
	
	public boolean shouldRepeat()
	{
		return this.repeat;
	}
	
	public void setInitDelay(int delay)
	{
		this.startDelay = delay;
	}
	
	public int getInitDelay()
	{
		return this.startDelay;
	}
	
	public void setRepeatDelay(int delay)
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
				PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.SCHEDULED_EVENTS + "(" +
						"event_ids," +
						"repeating," +
						"init_delay," +
						"repeating_delay," +
						"auto_start" +
						") VALUES(?,?,?,?,?)");
				st.setString(1, this.getEventIDString());
				st.setBoolean(2, this.repeat);
				st.setInt(3, this.startDelay);
				st.setInt(4, this.repeatDelay);
				st.setBoolean(5, this.autoStart);
				st.execute();
				ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
			else
			{
				PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.SCHEDULED_EVENTS + "(" +
						"schedule_id," +
						"event_ids," +
						"repeating," +
						"init_delay," +
						"repeating_delay," +
						"auto_start" +
						") VALUES(?,?,?,?,?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.getEventIDString());
				st.setBoolean(3, this.repeat);
				st.setInt(4, this.startDelay);
				st.setInt(5, this.repeatDelay);
				st.setBoolean(6, this.autoStart);
				
				if(!st.execute())
				{
					DragonsLairMain.Log.warning("Unable to save event " + this.id);
				}
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save event " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}

	public String getEventIDString()
	{
		StringBuilder sb = new StringBuilder();
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
			PreparedStatement st = DragonsLairMain.createStatement("DELETE FROM " + Tables.SCHEDULED_EVENTS + " WHERE `schedule_id` = ?");
			st.setInt(1, this.id);
			st.execute();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove scheduled event " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
}
