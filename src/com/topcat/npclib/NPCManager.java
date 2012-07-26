package com.topcat.npclib;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.topcat.npclib.entity.HumanNPC;
import com.topcat.npclib.entity.NPC;
import com.topcat.npclib.nms.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.tasks.NPCRotationTask;

/**
 *
 * @author martin
 */
public class NPCManager
{
	private final Map<Integer, NPC> npcs = Collections.synchronizedMap(new HashMap<Integer, NPC>());
	private final Map<Integer, Integer> npcRotationTasks = new HashMap<Integer, Integer>();
	private BServer server;
	private int taskid;
	private final Map<World, BWorld> bworlds = new HashMap<World, BWorld>();
	private NPCNetworkManager npcNetworkManager;
	public static JavaPlugin plugin;
	private final Set<Integer> spawnedIDs = Collections.synchronizedSet(new HashSet<Integer>());

	public NPCManager(JavaPlugin plugin)
	{
		server = BServer.getInstance();
		npcNetworkManager = new NPCNetworkManager();
		NPCManager.plugin = plugin;
		taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
			{
				@Override
				public void run()
				{
					HashSet<Integer> toRemove = new HashSet<Integer>();
					for (Integer i : npcs.keySet())
					{
						Entity j = npcs.get(i).getEntity();
						j.aA();
						if (j.dead)
							toRemove.add(i);
					}
					
					for (Integer n : toRemove)
					{
						((HumanNPC)npcs.get(n)).stopAttacking();
						npcs.remove(n);
						Bukkit.getScheduler().cancelTask(npcRotationTasks.get(n));
						npcRotationTasks.remove(n);
						
					}
				}
			}, 1L, 1L);
		Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);
		Bukkit.getServer().getPluginManager().registerEvents(new WL(), plugin);
	}

	public BWorld getBWorld(World world)
	{
		BWorld bworld = bworlds.get(world);
		if (bworld != null)
			return bworld;

		bworld = new BWorld(world);
		bworlds.put(world, bworld);
		return bworld;
	}

	private class SL implements Listener
	{
		@SuppressWarnings("unused")
		@EventHandler
		public void onPluginDisable(PluginDisableEvent event)
		{
			if (event.getPlugin() == plugin)
			{
				despawnAll();
				Bukkit.getServer().getScheduler().cancelTask(taskid);
			}
		}
	}

	private class WL implements Listener
	{
		@SuppressWarnings("unused")
		@EventHandler
		public void onChunkLoad(ChunkLoadEvent event)
		{
			for (NPC npc : npcs.values())
			{
				if (npc != null && event.getChunk() == npc.getBukkitEntity().getLocation().getBlock().getChunk())
				{
					BWorld world = getBWorld(event.getWorld());
					world.getWorldServer().addEntity(npc.getEntity());
				}
			}
		}
	}

	public NPC spawnHumanNPC(de.kumpelblase2.dragonslair.api.NPC inNpc)
	{
		Location l = inNpc.getLocation();
		String name = inNpc.getName();
		if(l.getWorld() == null)
		{
			DragonsLairMain.Log.info("Unable to spawn NPC '" + name + "' because the world doesn't exist.");
			return null;
		}
		
		boolean found = false;
		for(World worlds : Bukkit.getWorlds())
		{
			if(worlds.getName().equals(l.getWorld().getName()))
			{
				found = true;
				break;
			}
		}
		
		if(!found)
		{
			DragonsLairMain.Log.info("Unable to spawn NPC '" + name + "' because the world doesn't exist.");
			return null;
		}
		
		if(this.npcs.containsKey(inNpc.getID()))
		{
			DragonsLairMain.Log.warning("Unable to spawn the same NPC twice.");
			return null;
		}
		
		if (name.length() > 16)
		{ // Check and nag if name is too long, spawn NPC anyway with shortened name.
			String tmp = name.substring(0, 16);
			server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		
		BWorld world = getBWorld(l.getWorld());
		NPCEntity npcEntity = new NPCEntity(this, world, name, new ItemInWorldManager(world.getWorldServer()));
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		world.getWorldServer().addEntity(npcEntity); //the right way
		NPC npc = new HumanNPC(npcEntity);
		Integer id = inNpc.getID();
		npcs.put(id, npc);
		NPCRotationTask task = new NPCRotationTask(id);
		this.npcRotationTasks.put(id, Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonsLairMain.getInstance(), task, 2 * 20L, 2 * 20L));
		DragonsLairMain.getDungeonManager().getSpawnedNPCIDs().put(id, npc);
		this.spawnedIDs.add(id);
		return npc;
	}

	public boolean despawnById(Integer id)
	{
		NPC npc = npcs.get(id);
		if (npc != null)
		{
			Bukkit.getScheduler().cancelTask(this.npcRotationTasks.get(id));
			this.npcRotationTasks.remove(id);
			npcs.remove(id);
			npc.removeFromWorld();
			this.spawnedIDs.remove(id);
			return true;
		}
		return false;
	}

	public void despawnHumanByName(String npcName)
	{
		if (npcName.length() > 16)
			npcName = npcName.substring(0, 16); //Ensure you can still despawn
		
		HashSet<Integer> toRemove = new HashSet<Integer>();
		for (Integer n : npcs.keySet())
		{
			NPC npc = npcs.get(n);
			if (npc instanceof HumanNPC)
			{
				if (npc != null && ((HumanNPC) npc).getName().equals(npcName))
				{
					toRemove.add(n);
					npc.removeFromWorld();
				}
			}
		}
		
		for (Integer n : toRemove)
		{
			Bukkit.getScheduler().cancelTask(this.npcRotationTasks.get(n));
			this.npcRotationTasks.remove(n);
			npcs.remove(n);
			this.spawnedIDs.remove(n);
		}
	}

	public void despawnAll()
	{
		for (Entry<Integer, NPC> entry : npcs.entrySet())
		{
			if (entry.getValue() != null)
				entry.getValue().removeFromWorld();

			Bukkit.getScheduler().cancelTask(this.npcRotationTasks.get(entry.getKey()));
		}
		this.npcRotationTasks.clear();
		npcs.clear();
		this.spawnedIDs.clear();
	}

	public NPC getNPC(String id)
	{
		return npcs.get(id);
	}

	public boolean isNPC(org.bukkit.entity.Entity e)
	{
		return ((CraftEntity)e).getHandle() instanceof NPCEntity;
	}

	public List<NPC> getHumanNPCByName(String name)
	{
		List<NPC> ret = new ArrayList<NPC>();
		Collection<NPC> i = npcs.values();
		for (NPC e : i)
		{
			if (e instanceof HumanNPC)
			{
				if (((HumanNPC) e).getName().equalsIgnoreCase(name))
					ret.add(e);
			}
		}
		return ret;
	}

	public Map<Integer, NPC> getNPCs()
	{
		return this.npcs;
	}
	
	public Set<Integer> getSpawnedNPCIDs()
	{
		return this.spawnedIDs;
	}

	public Integer getNPCIdFromEntity(org.bukkit.entity.Entity e)
	{
		if (e instanceof HumanEntity)
		{
			for (Integer i : npcs.keySet())
			{
				if (npcs.get(i).getBukkitEntity().getEntityId() == ((HumanEntity) e).getEntityId())
					return i;
			}
		}
		return null;
	}

	public void rename(String id, String name)
	{
		if (name.length() > 16)
		{ // Check and nag if name is too long, spawn NPC anyway with shortened name.
			String tmp = name.substring(0, 16);
			server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		
		HumanNPC npc = (HumanNPC) getNPC(id);
		npc.setName(name);
		BWorld b = getBWorld(npc.getBukkitEntity().getLocation().getWorld());
		WorldServer s = b.getWorldServer();
		try
		{
			Method m = s.getClass().getDeclaredMethod("d", new Class[] {Entity.class});
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
			m = s.getClass().getDeclaredMethod("c", new Class[] {Entity.class});
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		s.everyoneSleeping();
	}

	public BServer getServer()
	{
		return server;
	}

	public NPCNetworkManager getNPCNetworkManager()
	{
		return npcNetworkManager;
	}

	public HumanNPC getNPC(Integer id)
	{
		return (HumanNPC)this.npcs.get(id);
	}

}