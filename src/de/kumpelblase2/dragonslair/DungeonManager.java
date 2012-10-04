package de.kumpelblase2.dragonslair;

import java.sql.PreparedStatement;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.api.*;
import de.kumpelblase2.dragonslair.api.eventexecutors.EventExecutor;
import de.kumpelblase2.dragonslair.events.EventCallEvent;
import de.kumpelblase2.dragonslair.events.TriggerCallEvent;
import de.kumpelblase2.dragonslair.events.dungeon.DungeonEndEvent;
import de.kumpelblase2.dragonslair.events.dungeon.DungeonStartEvent;
import de.kumpelblase2.dragonslair.map.DLMap;
import de.kumpelblase2.dragonslair.map.MapList;
import de.kumpelblase2.dragonslair.settings.Settings;
import de.kumpelblase2.dragonslair.utilities.EnumChange;
import de.kumpelblase2.npclib.NPCManager;
import de.kumpelblase2.npclib.entity.HumanNPC;

public class DungeonManager
{
	private final Map<String, Dungeon> dungeons = new HashMap<String, Dungeon>();
	private final ConversationFactory cfactory;
	private final NPCManager npcManager;
	private final Settings settings = new Settings();
	private final Map<EventActionType, EventExecutor> executors = new HashMap<EventActionType, EventExecutor>();
	private final Set<ActiveDungeon> activeDungeons = new HashSet<ActiveDungeon>();
	private final PlayerQueue queue = new PlayerQueue();
	private final MapList maps = new MapList();
	private final Map<String, Integer> m_playerDungeons = new HashMap<String, Integer>();
	private final Set<EventMonster> spawnedEntities = Collections.synchronizedSet(new HashSet<EventMonster>());
	private final Map<String, Map<Integer, Map<EntityType, Integer>>> killedMobs = new HashMap<String, Map<Integer, Map<EntityType, Integer>>>();
	private final ItemTracker itemTracker = new ItemTracker();

	public DungeonManager()
	{
		this.cfactory = new ConversationFactory(DragonsLairMain.getInstance());
		this.npcManager = new NPCManager(DragonsLairMain.getInstance());
	}

	public ConversationFactory getConversationFactory()
	{
		return this.cfactory;
	}

	public Map<String, Dungeon> getDungeons()
	{
		return this.dungeons;
	}

	public NPCManager getNPCManager()
	{
		return this.npcManager;
	}

	public Settings getSettings()
	{
		return this.settings;
	}

	public Map<Integer, de.kumpelblase2.npclib.entity.NPC> getSpawnedNPCIDs()
	{
		return this.npcManager.getNPCs();
	}

	public HumanNPC getNPCByName(final String inName)
	{
		final NPC n = DragonsLairMain.getSettings().getNPCByName(inName);
		if(n == null)
			return null;
		return this.npcManager.getNPC(n.getID());
	}

	public HumanNPC getNPCByEntity(final Entity e)
	{
		return this.npcManager.getNPC(this.npcManager.getNPCIdFromEntity(e));
	}

	public de.kumpelblase2.dragonslair.api.NPC getNPCByNPCEntity(final Entity npc)
	{
		return this.getSettings().getNPCs().get(this.npcManager.getNPCIdFromEntity(npc));
	}

	public boolean executeEvent(final Event e, final Player p)
	{
		if(e == null)
			return false;
		final ActiveDungeon ad = (p != null ? this.getDungeonOfPlayer(p.getName()) : null);
		final String name = (ad == null) ? "_GENERAL_" : ad.getInfo().getName();
		final boolean onCD = this.isOnCooldown(name, e);
		final EventCallEvent event = new EventCallEvent(e, p, onCD);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled())
		{
			if(event.isOnCooldown() != onCD)
				if(event.isOnCooldown())
					this.addCooldown(name, e);
				else
					this.removeCooldown(name, e);
			return true;
		}
		DragonsLairMain.debugLog("Executing event with id '" + e.getID() + "' for player '" + (p != null ? p.getName() : "") + "'");
		if(this.executors.containsKey(e.getActionType()) && !this.isOnCooldown(name, e))
		{
			final boolean result = this.executors.get(e.getActionType()).executeEvent(e, p);
			if(result)
				this.addCooldown(name, e);
			return result;
		}
		return false;
	}

	public void callTrigger(final Trigger t, final Player p)
	{
		if(t == null)
			return;
		final ActiveDungeon ad = (p == null) ? null : this.getDungeonOfPlayer(p.getName());
		final String name = (ad == null) ? "_GENERAL_" : ad.getInfo().getName();
		final boolean onCD = this.isOnCooldown(name, t);
		final TriggerCallEvent event = new TriggerCallEvent(t, p, onCD);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled())
		{
			if(event.isOnCooldown() != onCD)
				if(!event.isOnCooldown())
					this.removeCooldown(name, t);
				else
					this.addCooldown(name, t);
			return;
		}
		if(event.isOnCooldown())
			return;
		int delay = 0;
		if(t.getOption("delay") != null)
			try
			{
				delay = Integer.parseInt(t.getOption("delay"));
			}
			catch(final Exception e)
			{
				DragonsLairMain.Log.info("Unable to parse delay for trigger: " + t.getOption("delay"));
			}
		DragonsLairMain.debugLog("Executing trigger with id '" + t.getID() + "' by player '" + (p != null ? p.getName() : "") + "'");
		if(delay > 0)
			Bukkit.getScheduler().scheduleSyncDelayedTask(DragonsLairMain.getInstance(), new Runnable()
			{
				@Override
				public void run()
				{
					final List<Integer> ids = t.getEventIDs();
					for(final Integer eid : ids)
					{
						if(eid == 0)
							continue;
						final Event e = DragonsLairMain.getSettings().getEvents().get(eid);
						if(e == null)
							continue;
						if(e.getOption("delay") != null)
							Bukkit.getScheduler().scheduleSyncDelayedTask(DragonsLairMain.getInstance(), new Runnable()
							{
								@Override
								public void run()
								{
									DragonsLairMain.getDungeonManager().executeEvent(e, p);
								}
							}, Integer.parseInt(e.getOption("delay")) * 20L);
						else
							DragonsLairMain.getDungeonManager().executeEvent(e, p);
					}
					t.use(name);
				}
			}, delay * 20);
		else
		{
			final List<Integer> ids = t.getEventIDs();
			for(final Integer eid : ids)
			{
				if(eid == 0)
					continue;
				final Event e = DragonsLairMain.getSettings().getEvents().get(eid);
				if(e == null)
					continue;
				if(e.getOption("delay") != null)
					Bukkit.getScheduler().scheduleSyncDelayedTask(DragonsLairMain.getInstance(), new Runnable()
					{
						@Override
						public void run()
						{
							DragonsLairMain.getDungeonManager().executeEvent(e, p);
						}
					}, Integer.parseInt(e.getOption("delay")) * 20L);
				else
					DragonsLairMain.getDungeonManager().executeEvent(e, p);
			}
			t.use(name);
		}
	}

	public void setEventExecutor(final EventActionType action, final EventExecutor executor)
	{
		this.executors.put(action, executor);
	}

	public void spawnNPC(final String name)
	{
		final NPC npc = DragonsLairMain.getSettings().getNPCByName(name);
		this.spawnNPC(npc);
	}

	public void spawnNPC(final Integer id)
	{
		final NPC npc = DragonsLairMain.getSettings().getNPCs().get(id);
		if(npc == null)
		{
			DragonsLairMain.Log.warning("Unable to spawn NPC with id " + id + ".");
			return;
		}
		this.spawnNPC(npc);
	}

	public void spawnNPC(final NPC npc)
	{
		final HumanNPC hnpc = (HumanNPC)this.getNPCManager().spawnHumanNPC(npc);
		if(hnpc != null)
		{
			DragonsLairMain.debugLog("Spawning NPC with id '" + npc.getID() + "'");
			if(npc.getSkin() != null && !npc.getSkin().equals(""))
				hnpc.setSkin(npc.getSkin());
			hnpc.setItemInHand(npc.getHeldItem());
			hnpc.getInventory().setArmorContents(npc.getArmorParts());
		}
	}

	public boolean despawnNPC(final String name)
	{
		final NPC n = DragonsLairMain.getSettings().getNPCByName(name);
		if(n == null)
			return false;
		return this.despawnNPC(n.getID());
	}

	public boolean despawnNPC(final Integer id)
	{
		DragonsLairMain.debugLog("Despawning NPC with id '" + id + "'");
		return this.npcManager.despawnById(id);
	}

	public void spawnNPCs()
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(DragonsLairMain.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				for(final NPC n : DragonsLairMain.getSettings().getNPCs().values())
					if(n.shouldSpawnAtBeginning())
						DragonsLairMain.getDungeonManager().spawnNPC(n);
			}
		});
	}

	public ActiveDungeon startDungeon(final int id, final String[] players)
	{
		final Dungeon d = this.getSettings().getDungeons().get(id);
		final DungeonStartEvent event = new DungeonStartEvent(d);
		Bukkit.getPluginManager().callEvent(event);
		if(event.isCancelled())
			return null;
		final ActiveDungeon ad = new ActiveDungeon(d, Party.getPartyOfPlayers(players, id));
		this.activeDungeons.add(ad);
		for(final String p : players)
			this.m_playerDungeons.put(p, ad.getInfo().getID());
		ad.giveMaps();
		ad.sendMessage(ad.getInfo().getStartingMessage());
		ad.reloadProgress();
		DragonsLairMain.debugLog("Started dungeon '" + d.getName() + "'");
		return ad;
	}

	public ActiveDungeon startDungeon(final String name)
	{
		if(this.isDungeonStarted(name))
			return null;
		final Dungeon d = this.getSettings().getDungeonByName(name);
		if(d == null)
			return null;
		return this.queue.start(d);
	}

	public ActiveDungeon startDungeon(final int id)
	{
		return this.startDungeon(this.getSettings().getDungeons().get(id).getName());
	}

	public boolean hasRegistered(final String player)
	{
		return this.queue.isInQueue(Bukkit.getPlayer(player));
	}

	public void stopDungeon(final int id)
	{
		this.stopDungeon(id, true);
	}

	public void stopDungeon(final int id, final boolean save)
	{
		final Iterator<ActiveDungeon> dungeons = this.activeDungeons.iterator();
		while(dungeons.hasNext())
		{
			final ActiveDungeon ad = dungeons.next();
			if(ad.getInfo().getID() == id)
			{
				final DungeonEndEvent event = new DungeonEndEvent(ad);
				Bukkit.getPluginManager().callEvent(event);
				DragonsLairMain.debugLog("Stopped dungeon '" + ad.getInfo().getName() + "'");
				for(final String p : ad.getCurrentParty().getMembers())
				{
					this.maps.removeMap(Bukkit.getPlayer(p));
					this.m_playerDungeons.remove(p);
				}
				ad.stop(save);
				this.clearMobs(ad.getInfo().getID());
				this.killedMobs.remove(ad.getInfo().getName());
				dungeons.remove();
				return;
			}
		}
	}

	public void stopDungeon(final String name)
	{
		this.stopDungeon(name, true);
	}

	public void stopDungeon(final String name, final boolean save)
	{
		int id = -1;
		for(final ActiveDungeon d : this.activeDungeons)
			if(d.getInfo().getName().equals(name))
			{
				id = d.getInfo().getID();
				break;
			}
		if(id != -1)
			this.stopDungeon(id, save);
	}

	public ActiveDungeon getDungeonOfPlayer(final String name)
	{
		if(this.m_playerDungeons.containsKey(name))
		{
			final int id = this.m_playerDungeons.get(name);
			for(final ActiveDungeon ad : this.activeDungeons)
				if(ad.getInfo().getID() == id)
					return ad;
		}
		return null;
	}

	public Set<ActiveDungeon> getActiveDungeons()
	{
		return this.activeDungeons;
	}

	public PlayerQueue getQueue()
	{
		return this.queue;
	}

	public boolean isDungeonStarted(final String name)
	{
		for(final ActiveDungeon d : this.activeDungeons)
			if(d.getInfo().getName().equalsIgnoreCase(name))
				return true;
		return false;
	}

	public void queuePlayer(final String name, final String dungeon)
	{
		this.queue.queuePlayer(dungeon, Bukkit.getPlayer(name));
		final Dungeon d = this.settings.getDungeonByName(dungeon);
		if(this.queue.hasEnoughPeople(d))
		{
			final List<QueuedPlayer> players = this.queue.getQueueForDungeon(d);
			final String readyMessage = d.getPartyReadyMessage();
			for(final QueuedPlayer player : players)
				player.getPlayer().sendMessage(readyMessage);
		}
	}

	public void queuePlayer(final String name, final int id)
	{
		this.queuePlayer(name, this.getSettings().getDungeons().get(id).getName());
	}

	public void addMapHolder(final Player player)
	{
		if(player == null)
			return;
		DragonsLairMain.debugLog("Giving dungeon map to '" + player.getName() + "'");
		this.maps.addMap(player, new DLMap(player));
	}

	public DLMap getMapOfPlayer(final Player p)
	{
		return this.maps.getMapOfPlayer(p);
	}

	public void stopDungeons()
	{
		for(final ActiveDungeon ad : this.activeDungeons)
			ad.stop(true);
		this.activeDungeons.clear();
	}

	public void addCooldown(final String name, final Trigger t)
	{
		t.use(name);
	}

	public boolean isOnCooldown(final String name, final Trigger t)
	{
		return t.canUse(name);
	}

	public void addCooldown(final String name, final Event e)
	{
		e.use(name);
	}

	public boolean isOnCooldown(final String name, final Event e)
	{
		return e.canUse(name);
	}

	public void removeCooldown(final String name, final Trigger t)
	{
		t.removeCooldown(name);
	}

	public void removeCooldown(final String name, final Event e)
	{
		e.removeCooldown(name);
	}

	public Set<EventMonster> getSpawnedMobs()
	{
		return this.spawnedEntities;
	}

	public Event getEventFromMob(final LivingEntity entity)
	{
		final EventMonster mob = this.getEventMonsterByEntity(entity);
		return (mob == null ? null : mob.getEvent());
	}

	public EventMonster getEventMonsterByEntity(final LivingEntity entity)
	{
		for(final EventMonster mob : this.spawnedEntities)
			if(mob.isMob(entity))
				return mob;
		return null;
	}

	public void clearMobs(final int id)
	{
		final Iterator<EventMonster> mobs = this.spawnedEntities.iterator();
		while(mobs.hasNext())
		{
			final EventMonster mob = mobs.next();
			if(mob.getDungeon().getInfo().getID() != id)
				continue;
			mob.getMonster().remove();
			mobs.remove();
		}
	}

	public Map<Integer, Map<EntityType, Integer>> getKills(final ActiveDungeon ad)
	{
		return this.killedMobs.get(ad.getInfo().getName());
	}

	public int addMobKill(final ActiveDungeon ad, final LivingEntity entity, final Event e)
	{
		final Iterator<EventMonster> spawned = this.spawnedEntities.iterator();
		while(spawned.hasNext())
		{
			final EventMonster mob = spawned.next();
			if(mob.isMob(entity))
			{
				spawned.remove();
				break;
			}
		}
		Map<Integer, Map<EntityType, Integer>> mobKills = this.getKills(ad);
		if(mobKills != null)
		{
			Map<EntityType, Integer> killed = mobKills.get(e.getID());
			if(killed != null)
			{
				Integer amount = killed.get(entity.getType());
				amount = (amount == null) ? 1 : amount + 1;
				killed.put(entity.getType(), amount);
				return amount;
			}
			else
			{
				killed = new HashMap<EntityType, Integer>();
				killed.put(entity.getType(), 1);
				mobKills.put(e.getID(), killed);
				return 1;
			}
		}
		else
		{
			mobKills = new HashMap<Integer, Map<EntityType, Integer>>();
			final Map<EntityType, Integer> killed = new HashMap<EntityType, Integer>();
			killed.put(entity.getType(), 1);
			mobKills.put(e.getID(), killed);
			return 1;
		}
	}

	public void saveCooldowns()
	{
		for(final Event e : this.getSettings().getEvents().values())
			e.save();
		for(final Trigger t : this.getSettings().getTriggers().values())
			t.save();
	}

	public void registerEventType(final String type, final String[] requiredOptions, final String[] optionalOptions, final boolean addToDb)
	{
		EnumChange.addEnum(EventActionType.class, type, new Class<?>[0], new Object[0]);
		EnumChange.addEnum(EventActionOptions.class, type, new Class<?>[] { String[].class, String[].class }, new Object[] { requiredOptions, optionalOptions });
		if(addToDb)
			try
			{
				if(DragonsLairMain.getInstance().getConfig().getString("db.type").equals("sqlite"))
					return;
				final PreparedStatement st = DragonsLairMain.createStatement("ALTER TABLE `events` CHANGE COLUMN `event_action_type` `event_action_type` enum(?)");
				final StringBuilder sb = new StringBuilder();
				for(final EventActionType ctype : EventActionType.values())
					sb.append("'" + ctype.toString().toLowerCase() + "',");
				if(sb.length() > 1)
					sb.substring(0, sb.length() - 1);
				st.setString(1, sb.toString());
			}
			catch(final Exception e)
			{
				DragonsLairMain.Log.warning("Unable to register type at database:");
				DragonsLairMain.Log.warning(e.getMessage());
			}
	}

	public void removeEventType(final String type, final boolean removeFromDb)
	{
		EnumChange.removeEnum(EventActionType.class, type);
		if(removeFromDb)
			try
			{
				if(DragonsLairMain.getInstance().getConfig().getString("db.type").equals("sqlite"))
					return;
				final PreparedStatement st = DragonsLairMain.createStatement("ALTER TABLE `events` CHANGE COLUMN `event_action_type` `event_action_type` enum(?)");
				final StringBuilder sb = new StringBuilder();
				for(final EventActionType ctype : EventActionType.values())
					if(!ctype.toString().equalsIgnoreCase(type))
						sb.append("'" + ctype.toString().toLowerCase() + "',");
				if(sb.length() > 1)
					sb.substring(0, sb.length() - 1);
				st.setString(1, sb.toString());
			}
			catch(final Exception e)
			{
				DragonsLairMain.Log.warning("Unable to remove type at database:");
				DragonsLairMain.Log.warning(e.getMessage());
			}
	}

	public void registerTriggerType(final String type, final String[] requiredOptions, final String[] optionalOptions, final boolean addToDb)
	{
		EnumChange.addEnum(TriggerType.class, type, new Class<?>[0], new Object[0]);
		EnumChange.addEnum(TriggerTypeOptions.class, type, new Class<?>[] { String[].class, String[].class }, new Object[] { requiredOptions, optionalOptions });
		if(addToDb)
			try
			{
				if(DragonsLairMain.getInstance().getConfig().getString("db.type").equals("sqlite"))
					return;
				final PreparedStatement st = DragonsLairMain.createStatement("ALTER TABLE `triggers` CHANGE COLUMN `trigger_type` `trigger_type` enum(?)");
				final StringBuilder sb = new StringBuilder();
				for(final TriggerType ctype : TriggerType.values())
					sb.append("'" + ctype.toString().toLowerCase() + "',");
				if(sb.length() > 1)
					sb.substring(0, sb.length() - 1);
				st.setString(1, sb.toString());
			}
			catch(final Exception e)
			{
				DragonsLairMain.Log.warning("Unable to register type at database:");
				DragonsLairMain.Log.warning(e.getMessage());
			}
	}

	public void removeTriggerType(final String type, final boolean removeFromDb)
	{
		EnumChange.removeEnum(TriggerType.class, type);
		if(removeFromDb)
			try
			{
				if(DragonsLairMain.getInstance().getConfig().getString("db.type").equals("sqlite"))
					return;
				final PreparedStatement st = DragonsLairMain.createStatement("ALTER TABLE `triggers` CHANGE COLUMN `trigger_type` `trigger_type` enum(?)");
				final StringBuilder sb = new StringBuilder();
				for(final TriggerType ctype : TriggerType.values())
					if(!ctype.toString().equalsIgnoreCase(type))
						sb.append("'" + ctype.toString().toLowerCase() + "',");
				if(sb.length() > 1)
					sb.substring(0, sb.length() - 1);
				st.setString(1, sb.toString());
			}
			catch(final Exception e)
			{
				DragonsLairMain.Log.warning("Unable to remove type at database:");
				DragonsLairMain.Log.warning(e.getMessage());
			}
	}

	public ActiveDungeon getActiveDungeonByName(final String name)
	{
		for(final ActiveDungeon ad : this.activeDungeons)
			if(ad.getInfo().getName().equals(name))
				return ad;
		return null;
	}

	public ActiveDungeon getActiveDungeonByID(final Integer id)
	{
		final Dungeon d = DragonsLairMain.getSettings().getDungeons().get(id);
		if(d == null)
			return null;
		return this.getActiveDungeonByName(d.getName());
	}

	public HumanNPC getNPCByID(final Integer npcID)
	{
		return this.npcManager.getNPC(npcID);
	}

	public boolean despawnNPC(final NPC n)
	{
		return this.despawnNPC(n.getID());
	}

	public void removeMapHolder(final Player dead)
	{
		this.maps.removeMap(dead);
	}

	public ItemTracker getItemTracker()
	{
		return this.itemTracker;
	}
}
