package de.kumpelblase2.dragonslair.settings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.Tables;
import de.kumpelblase2.dragonslair.api.*;

public class Settings
{
	private Map<Integer, Event> events = new HashMap<Integer, Event>();
	private Map<Integer, Trigger> triggers = new HashMap<Integer, Trigger>();
	private Map<Integer, Chapter> chapers = new HashMap<Integer, Chapter>();
	private Map<Integer, Dialog> dialogs = new HashMap<Integer, Dialog>();
	private Map<Integer, Dungeon> dungeons = new HashMap<Integer, Dungeon>();
	private Map<Integer, Objective> objectives = new HashMap<Integer, Objective>();
	private Map<Integer, NPC> npcs = new HashMap<Integer, NPC>();
	
	public void loadAll()
	{
		this.loadChapters();
		this.loadObjectives();
		this.loadDialogs();
		this.loadNPCs();
		this.loadTriggers();
		this.loadEvents();
		this.loadDungeons();
	}
	
	public void loadDungeons()
	{
		this.dungeons.clear();
		try
		{
			PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.DUNGEONS);
			ResultSet result = m.executeQuery();
			while(result.next())
			{
				Dungeon d = new Dungeon(result);
				this.dungeons.put(d.getID(), d);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.info("Unable to load dungeons from database: " + e.getMessage());
			return;
		}
	}
	
	public void loadTriggers()
	{
		this.triggers.clear();
		try
		{
			PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.TRIGGERS);
			ResultSet result = m.executeQuery();
			while(result.next())
			{
				Trigger t = new Trigger(result);
				this.triggers.put(t.getID(), t);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.info("Unable to load triggers from database: " + e.getMessage());
			return;
		}
	}
	
	public void loadChapters()
	{
		this.chapers.clear();
		try
		{
			PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.CHAPTERS);
			ResultSet result = m.executeQuery();
			while(result.next())
			{
				Chapter c = new Chapter(result);
				this.chapers.put(c.getID(), c);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.info("Unable to load chapters from database: " + e.getMessage());
			return;
		}
	}
	
	public void loadDialogs()
	{
		this.dialogs.clear();
		try
		{
			PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.DIALOGS);
			ResultSet result = m.executeQuery();
			while(result.next())
			{
				Dialog d = new Dialog(result);
				this.dialogs.put(d.getID(), d);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.info("Unable to load dialogs from database: " + e.getMessage());
			return;
		}
	}
	
	public void loadObjectives()
	{
		this.objectives.clear();
		try
		{
			PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.OBJECTIVES);
			ResultSet result = m.executeQuery();
			while(result.next())
			{
				Objective o = new Objective(result);
				this.objectives.put(o.getID(), o);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.info("Unable to load objectives from database: " + e.getMessage());
			return;
		}
	}
	
	public void loadNPCs()
	{
		this.npcs.clear();
		try
		{
			PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.NPCS);
			ResultSet result = m.executeQuery();
			while(result.next())
			{
				NPC n = new NPC(result);
				this.npcs.put(n.getID(), n);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.info("Unable to load npcs from database:");
			e.printStackTrace();
			return;
		}
	}
	
	public void loadEvents()
	{
		this.events.clear();
		try
		{
			PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.EVENTS);
			ResultSet result = m.executeQuery();
			while(result.next())
			{
				Event e = new Event(result);
				this.events.put(e.getID(), e);
			}
		}
		catch(Exception e)
		{
			DragonsLairMain.Log.info("Unable to load events from database: " + e.getMessage());
			return;
		}
	}
	
	public NPC getNPCByName(String name)
	{
		for(NPC npc : this.npcs.values())
		{
			if(npc.getName().equals(name))
				return npc;
		}
		return null;
	}
	
	public Dungeon getDungeonByName(String name)
	{
		for(Dungeon d : this.getDungeons().values())
		{
			if(d.getName().equalsIgnoreCase(name))
				return d;
		}
		return null;
	}
	
	public Map<Integer, NPC> getNPCs()
	{
		return this.npcs;
	}
	
	public Map<Integer, Dialog> getDialogs()
	{
		return this.dialogs;
	}
	
	public Map<Integer, Trigger> getTriggers()
	{
		return this.triggers;
	}
	
	public Map<Integer, Event> getEvents()
	{
		return this.events;
	}
	
	public Map<Integer, Dungeon> getDungeons()
	{
		return this.dungeons;
	}
	
	public Map<Integer, Chapter> getChapters()
	{
		return this.chapers;
	}
	
	public Map<Integer, Objective> getObjectives()
	{
		return this.objectives;
	}
}
