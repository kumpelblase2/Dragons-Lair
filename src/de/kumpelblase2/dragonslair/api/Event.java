package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import java.util.*;
import de.kumpelblase2.dragonslair.*;

public class Event
{
	private int id;
	private EventActionType actionType;
	private Option[] actionOptions;
	private Set<Cooldown> cooldowns = Collections.synchronizedSet(new HashSet<Cooldown>());
	
	public Event()
	{
		this.id = -1;
		this.actionOptions = new Option[0];
	}
	
	public Event(ResultSet result)
	{
		try
		{
			this.id = result.getInt(TableColumns.Events.ID);
			this.actionType = EventActionType.valueOf(result.getString(TableColumns.Events.ACTION_TYPE).toUpperCase());
			String options = result.getString(TableColumns.Events.ACTION_OPTIONS);
			if(options != null && options.length() > 0 && options.contains(":"))
			{
				String[] optionSplitt = options.split(";");
				this.actionOptions = new Option[ optionSplitt.length ];
				for(int i = 0; i < optionSplitt.length; i++)
				{
					try
					{
						String[] splitt = optionSplitt[i].split(":");
						this.actionOptions[i] = new Option(splitt[0], splitt[1]);
					}
					catch(Exception e)
					{
						DragonsLairMain.Log.warning("Unable to parse event option: " + optionSplitt[i]);
					}
				}
			}
			else
			{
				this.actionOptions = new Option[0];
			}
			
			String cooldownString = result.getString(TableColumns.Events.COOLDOWNS);
			if(cooldownString != null && cooldownString.length() > 0)
			{
				String[] splitted = cooldownString.split(";");
				for(String cd : splitted)
				{
					String[] cdSplit = cd.split(":");
					this.cooldowns.add(new Cooldown(cdSplit[0], Integer.parseInt(cdSplit[1])));
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public int getID()
	{
		return this.id;
	}
	
	public void setActionType(EventActionType type)
	{
		this.actionType = type;
	}
	
	public EventActionType getActionType()
	{
		return this.actionType;
	}
	
	public void setOptions(Option... options)
	{
		this.actionOptions = options;
	}
	
	public Option[] getOptions()
	{
		return this.actionOptions;
	}
	
	public String getOption(String key)
	{
		for(int i = 0; i < this.actionOptions.length; i++)
		{
			if(this.actionOptions[i].getType().equals(key))
				return this.actionOptions[i].getValue();
		}
		return null;
	}
	
	public void save()
	{
		try
		{
			StringBuilder optionString = new StringBuilder();
			for(int i = 0; i < this.actionOptions.length; i++)
			{
				optionString.append(this.actionOptions[i].getType() + ":" + this.actionOptions[i].getValue());
				if(i != this.actionOptions.length - 1)
					optionString.append(";");
			}
			
			if(this.id != -1)
			{
				PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.EVENTS + "(" +
						"event_id," +
						"event_action_type," +
						"event_action_options," +
						"event_cooldowns" +
						") VALUES(?,?,?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.actionType.toString().toLowerCase());
				st.setString(3, optionString.toString());
				st.setString(4, this.getCooldowns());
				st.execute();
			}
			else
			{
				PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.EVENTS + "(" +
						"event_action_type," +
						"event_action_options," +
						"event_cooldowns" +
						") VALUES(?,?,?)");
				st.setString(1, this.actionType.toString().toLowerCase());
				st.setString(2, optionString.toString());
				st.setString(3, this.getCooldowns());
				st.execute();
				ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save event " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
	
	public String getOptionString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < this.actionOptions.length; i++)
		{
			if(this.actionOptions[i] == null)
				continue;
			
			sb.append(this.actionOptions[i].getType() + ":" + this.actionOptions[i].getValue());
			if(i != this.actionOptions.length - 1)
				sb.append(";");
		}
		return sb.toString();
	}

	public void removeOption(String option)
	{
		if(this.getOption(option) == null)
			return;
		
		ArrayList<Option> options = new ArrayList<Option>(Arrays.asList(this.actionOptions));
		for(int i = 0; i < options.size(); i++)
		{
			if(options.get(i).getType().equals(option))
			{
				options.remove(i);
				break;
			}
		}
		this.actionOptions = options.toArray(new Option[0]);
	}
	
	public void setOption(String key, String value)
	{
		for(int i = 0; i < this.actionOptions.length; i++)
		{
			if(this.actionOptions[i].getType().equals(key))
			{
				this.actionOptions[i].setValue(value);
				return;
			}
		}
		
		ArrayList<Option> options = new ArrayList<Option>(Arrays.asList(this.actionOptions));
		options.add(new Option(key, value));
		this.actionOptions = options.toArray(new Option[0]);
	}

	public void remove()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("DELETE FROM " + Tables.EVENTS + " WHERE `event_id` = ?");
			st.setInt(1, this.getID());
			st.execute();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove event with id " + this.getID());
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
	
	private String getCooldowns()
	{
		StringBuilder sb = new StringBuilder();
		for(Cooldown cd : this.cooldowns)
		{
			sb.append(cd.getDungeonName() + ":" + cd.getRemainingTime());
			sb.append(";");
		}
		if(sb.length() > 1)
			sb.substring(0, sb.length() - 1);
		
		return sb.toString();
	}
	
	public boolean canUse(String name)
	{
		Iterator<Cooldown> cooldown = this.cooldowns.iterator();
		while(cooldown.hasNext())
		{
			Cooldown cd = cooldown.next();
			if(cd.equals(name))
			{
				if(!cd.isOnCooldown())
				{
					this.cooldowns.remove(cd);
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public int getCooldown()
	{
		if(this.getOption("cooldown") == null)
		{
			return 0;
		}
		else
		{
			return Integer.parseInt(this.getOption("cooldown"));
		}
	}
	
	public void use(String name)
	{
		this.cooldowns.add(new Cooldown(name, this.getCooldown()));
	}
	
	public void removeCooldown(String name)
	{
		Iterator<Cooldown> cooldown = this.cooldowns.iterator();
		while(cooldown.hasNext())
		{
			Cooldown cd = cooldown.next();
			if(cd.equals(name))
			{
				this.cooldowns.remove(cd);
				return;
			}
		}
	}
	
	public void clearCooldowns()
	{
		Iterator<Cooldown> cooldown = this.cooldowns.iterator();
		while(cooldown.hasNext())
		{
			Cooldown cd = cooldown.next();
			if(!cd.isOnCooldown())
				this.cooldowns.remove(cd);
		}
	}
}
