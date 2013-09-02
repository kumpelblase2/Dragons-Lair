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
	private final Map<Integer, Event> events = new HashMap<Integer, Event>();
	private final Map<Integer, Trigger> triggers = new HashMap<Integer, Trigger>();
	private final Map<Integer, Chapter> chapters = new HashMap<Integer, Chapter>();
	private final Map<Integer, Dialog> dialogs = new HashMap<Integer, Dialog>();
	private final Map<Integer, Dungeon> dungeons = new HashMap<Integer, Dungeon>();
	private final Map<Integer, Objective> objectives = new HashMap<Integer, Objective>();
	private final Map<Integer, NPC> npcs = new HashMap<Integer, NPC>();

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

	void loadDungeons()
	{
		this.dungeons.clear();
		try
		{
			final PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.DUNGEONS);
			final ResultSet result = m.executeQuery();
			while(result.next())
			{
				final Dungeon d = new Dungeon(result);
				this.dungeons.put(d.getID(), d);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.info("Unable to load dungeons from database: " + e.getMessage());
		}
	}

	void loadTriggers()
	{
		this.triggers.clear();
		try
		{
			final PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.TRIGGERS);
			final ResultSet result = m.executeQuery();
			while(result.next())
			{
				final Trigger t = new Trigger(result);
				this.triggers.put(t.getID(), t);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.info("Unable to load triggers from database: " + e.getMessage());
		}
	}

	void loadChapters()
	{
		this.chapters.clear();
		try
		{
			final PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.CHAPTERS);
			final ResultSet result = m.executeQuery();
			while(result.next())
			{
				final Chapter c = new Chapter(result);
				this.chapters.put(c.getID(), c);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.info("Unable to load chapters from database: " + e.getMessage());
		}
	}

	void loadDialogs()
	{
		this.dialogs.clear();
		try
		{
			final PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.DIALOGS);
			final ResultSet result = m.executeQuery();
			while(result.next())
			{
				final Dialog d = new Dialog(result);
				this.dialogs.put(d.getID(), d);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.info("Unable to load dialogs from database: " + e.getMessage());
		}
	}

	void loadObjectives()
	{
		this.objectives.clear();
		try
		{
			final PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.OBJECTIVES);
			final ResultSet result = m.executeQuery();
			while(result.next())
			{
				final Objective o = new Objective(result);
				this.objectives.put(o.getID(), o);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.info("Unable to load objectives from database: " + e.getMessage());
		}
	}

	void loadNPCs()
	{
		this.npcs.clear();
		try
		{
			final PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.NPCS);
			final ResultSet result = m.executeQuery();
			while(result.next())
			{
				final NPC n = new NPC(result);
				this.npcs.put(n.getID(), n);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.info("Unable to load npcs from database:");
			e.printStackTrace();
		}
	}

	void loadEvents()
	{
		this.events.clear();
		try
		{
			final PreparedStatement m = DragonsLairMain.createStatement("SELECT * FROM " + Tables.EVENTS);
			final ResultSet result = m.executeQuery();
			while(result.next())
			{
				final Event e = new Event(result);
				this.events.put(e.getID(), e);
			}
		}
		catch(final Exception e)
		{
			DragonsLairMain.Log.info("Unable to load events from database: " + e.getMessage());
		}
	}

	public NPC getNPCByName(final String name)
	{
		for(final NPC npc : this.npcs.values())
		{
			if(npc.getName().equals(name))
				return npc;
		}

		return null;
	}

	public Dungeon getDungeonByName(final String name)
	{
		for(final Dungeon d : this.getDungeons().values())
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
		return this.chapters;
	}

	public Map<Integer, Objective> getObjectives()
	{
		return this.objectives;
	}
}