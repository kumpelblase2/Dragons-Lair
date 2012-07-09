package de.kumpelblase2.dragonslair.api;

import java.sql.*;
import java.util.*;
import de.kumpelblase2.dragonslair.*;
import de.kumpelblase2.dragonslair.utilities.GeneralUtilities;

public class Trigger
{
	private int id;
	private TriggerType type;
	private Option[] type_options;
	private List<Integer> events = new ArrayList<Integer>();
	private Set<Cooldown> cooldowns = Collections.synchronizedSet(new HashSet<Cooldown>());
	
	public Trigger()
	{
		this.id = -1;
		this.type_options = new Option[0];
	}
	
	public Trigger(ResultSet result)
	{
		try
		{
			this.id = result.getInt(TableColumns.Triggers.ID);
			this.type = TriggerType.valueOf(result.getString(TableColumns.Triggers.TYPE).toUpperCase());
			String options = result.getString(TableColumns.Triggers.TYPE_OPTIONS);
			if(options.contains(":"))
			{
				String[] optionSplitt = options.split(";");
				this.type_options = new Option[ optionSplitt.length ];
				for(int i = 0; i < optionSplitt.length; i++)
				{
					try
					{
						String[] splitt = optionSplitt[i].split(":");
						this.type_options[i] = new Option(splitt[0], splitt[1]);
					}
					catch(Exception e)
					{
						DragonsLairMain.Log.warning("Unable to parse trigger option: " + optionSplitt[i]);
					}
				}
			}
			else
			{
				this.type_options = new Option[0];
			}
			String eventsString = result.getString(TableColumns.Triggers.ACTION_EVENT_ID);
			if(eventsString.contains(";"))
			{
				String[] eventsSplitt = eventsString.split(";");
				Integer[] eventIDs = new Integer[ eventsSplitt.length ];
				for(int i = 0; i < eventsSplitt.length; i++)
				{
					try
					{
						eventIDs[i] = Integer.parseInt(eventsSplitt[i]);
					}
					catch(Exception e)
					{
						DragonsLairMain.Log.warning("Unable to parse event id " + eventsSplitt[i]);
						continue;
					}
				}
				this.events = new ArrayList<Integer>(Arrays.asList(eventIDs));
			}
			else
			{
				try
				{
					if(eventsString != null && !eventsString.equals(""))
					{
						Integer id = Integer.parseInt(eventsString);
						this.events.add(id);
					}
				}
				catch(Exception e)
				{
					DragonsLairMain.Log.warning("Unable to parse event id " + eventsString);
				}
			}
			
			String cooldownString = result.getString(TableColumns.Triggers.COOLDOWNS);
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
	
	public TriggerType getType()
	{
		return this.type;
	}
	
	public void setType(TriggerType inType)
	{
		this.type = inType;
	}
	
	public Option[] getOptions()
	{
		return this.type_options;
	}
	
	public String getOption(String key)
	{
		for(Option o : this.type_options)
		{
			if(o.getType().equals(key))
				return o.getValue();
		}
		return null;
	}
	
	public void setOptions(String... inOptions)
	{
		Option[] tmpOptions = new Option[inOptions.length];
		for(int i = 0; i < inOptions.length; i++)
		{
			try
			{
				String[] splitt = inOptions[i].split(":");
				tmpOptions[i] = new Option(splitt[0], splitt[1]);
			}
			catch(Exception e)
			{
				DragonsLairMain.Log.warning("Unable to parse trigger option: " + inOptions[i]);
			}
		}
		this.type_options = tmpOptions;
	}
	
	public void setOptions(Option... options)
	{
		this.type_options = options;
	}
	
	public List<Integer> getEventIDs()
	{
		return this.events;
	}
	
	public void setEventIDs(List<Integer> inEvent)
	{
		this.events = inEvent;
	}
	
	public void save()
	{
		try
		{
			GeneralUtilities.recalculateOptions(this);
			StringBuilder optionString = new StringBuilder();
			for(int i = 0; i < this.type_options.length; i++)
			{
				optionString.append(this.type_options[i].getType() + ":" + this.type_options[i].getValue());
				if(i != this.type_options.length - 1)
					optionString.append(";");
			}
			
			StringBuilder eventString = new StringBuilder();
			for(int i = 0; i < this.events.size(); i++)
			{
				if(this.events.get(i) == 0)
					continue;
				
				eventString.append(this.events.get(i));
				if(i != this.events.size() - 1)
					eventString.append(";");
			}
			
			if(this.id != -1)
			{
				PreparedStatement st = DragonsLairMain.createStatement("REPLACE INTO " + Tables.TRIGGERS + "(" +
						"trigger_id," +
						"trigger_type," +
						"trigger_type_options," +
						"trigger_action_event," +
						"trigger_cooldowns" +
						") VALUES(?,?,?,?,?)");
				st.setInt(1, this.id);
				st.setString(2, this.type.toString().toLowerCase());
				st.setString(3, optionString.toString());
				st.setString(4, eventString.toString());
				st.setString(5, this.getCooldowns());
				st.execute();
			}
			else
			{
				PreparedStatement st = DragonsLairMain.createStatement("INSERT INTO " + Tables.TRIGGERS + "(" +
						"trigger_type," +
						"trigger_type_options," +
						"trigger_action_event," +
						"trigger_cooldowns" +
						") VALUES(?,?,?,?)");
				st.setString(1, this.type.toString().toLowerCase());
				st.setString(2, optionString.toString());
				st.setString(3, eventString.toString());
				st.setString(4, this.getCooldowns());
				st.execute();
				ResultSet keys = st.getGeneratedKeys();
				if(keys.next())
					this.id = keys.getInt(1);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to save trigger " + this.id);
			DragonsLairMain.Log.warning(e.getMessage());
		}
	}
	
	public String getOptionString()
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < this.type_options.length; i++)
		{
			sb.append(this.type_options[i].getType() + ":" + this.type_options[i].getValue());
			if(i != this.type_options.length)
				sb.append(";");
		}
		return sb.toString();
	}
	
	public void addOption(Option o)
	{
		List<Option> options = new ArrayList<Option>(Arrays.asList(this.type_options));
		options.add(o);
		this.type_options = options.toArray(new Option[0]);
	}
	
	public void setOption(String key, String value)
	{
		for(Option o : this.type_options)
		{
			if(o.getType().equals(key))
			{
				o.setValue(value);
				return;
			}
		}
		
		this.addOption(new Option(key, value));
	}
	
	public void removeOption(String key)
	{
		List<Option> options = new ArrayList<Option>(Arrays.asList(this.type_options));
		for(int i = 0; i < options.size(); i++)
		{
			if(options.get(i).getType().equals(key))
			{
				options.remove(i);
				return;
			}
		}
	}

	public void remove()
	{
		try
		{
			PreparedStatement st = DragonsLairMain.createStatement("DELETE FROM " + Tables.TRIGGERS + " WHERE `trigger_ id` = ?");
			st.setInt(1, this.getID());
			st.execute();
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.warning("Unable to remove trigger with id " + this.getID());
			DragonsLairMain.Log.warning(e.getMessage());
		}
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
			return GeneralUtilities.getDefaultCooldown(this.getType());
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
