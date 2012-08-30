package de.kumpelblase2.npclib;

// original provided by Topcat, modified by kumpelblase2
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
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.tasks.NPCRotationTask;
import de.kumpelblase2.npclib.entity.HumanNPC;
import de.kumpelblase2.npclib.entity.NPC;
import de.kumpelblase2.npclib.nms.*;

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

	public NPCManager(final JavaPlugin plugin)
	{
		try
		{
			this.server = BServer.getInstance();
			this.npcNetworkManager = new NPCNetworkManager();
			NPCManager.plugin = plugin;
			this.taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
			{
				@Override
				public void run()
				{
					final HashSet<Integer> toRemove = new HashSet<Integer>();
					for(final Integer i : NPCManager.this.npcs.keySet())
					{
						final Entity j = NPCManager.this.npcs.get(i).getEntity();
						j.z();
						if(j.dead)
							toRemove.add(i);
					}
					for(final Integer n : toRemove)
					{
						((HumanNPC)NPCManager.this.npcs.get(n)).stopAttacking();
						Bukkit.getScheduler().cancelTask(NPCManager.this.npcRotationTasks.get(n));
						NPCManager.this.npcRotationTasks.remove(n);
						NPCManager.this.npcs.remove(n);
					}
				}
			}, 1L, 1L);
			Bukkit.getServer().getPluginManager().registerEvents(new SL(), plugin);
			Bukkit.getServer().getPluginManager().registerEvents(new WL(), plugin);
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
	}

	public BWorld getBWorld(final World world)
	{
		BWorld bworld = this.bworlds.get(world);
		if(bworld != null)
			return bworld;
		bworld = new BWorld(world);
		this.bworlds.put(world, bworld);
		return bworld;
	}

	private class SL implements Listener
	{
		@EventHandler
		public void onPluginDisable(final PluginDisableEvent event)
		{
			if(event.getPlugin() == plugin)
			{
				NPCManager.this.despawnAll();
				Bukkit.getServer().getScheduler().cancelTask(NPCManager.this.taskid);
			}
		}
	}
	private class WL implements Listener
	{
		@EventHandler
		public void onChunkLoad(final ChunkLoadEvent event)
		{
			for(final NPC npc : NPCManager.this.npcs.values())
				if(npc != null && event.getChunk() == npc.getBukkitEntity().getLocation().getBlock().getChunk())
				{
					final BWorld world = NPCManager.this.getBWorld(event.getWorld());
					world.getWorldServer().addEntity(npc.getEntity());
				}
		}
	}

	public NPC spawnHumanNPC(final de.kumpelblase2.dragonslair.api.NPC inNpc)
	{
		final Location l = inNpc.getLocation();
		String name = inNpc.getName();
		if(l.getWorld() == null)
		{
			DragonsLairMain.Log.info("Unable to spawn NPC '" + name + "' because the world doesn't exist.");
			return null;
		}
		boolean found = false;
		for(final World worlds : Bukkit.getWorlds())
			if(worlds.getName().equals(l.getWorld().getName()))
			{
				found = true;
				break;
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
		if(name.length() > 16)
		{ // Check and nag if name is too long, spawn NPC anyway with shortened name.
			final String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		final BWorld world = this.getBWorld(l.getWorld());
		final NPCEntity npcEntity = new NPCEntity(this, world, name, new ItemInWorldManager(world.getWorldServer()));
		npcEntity.setPositionRotation(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch());
		world.getWorldServer().addEntity(npcEntity); // the right way
		final NPC npc = new HumanNPC(npcEntity);
		final Integer id = inNpc.getID();
		this.npcs.put(id, npc);
		final NPCRotationTask task = new NPCRotationTask(id);
		this.npcRotationTasks.put(id, Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonsLairMain.getInstance(), task, 2 * 20L, 2 * 20L));
		DragonsLairMain.getDungeonManager().getSpawnedNPCIDs().put(id, npc);
		this.spawnedIDs.add(id);
		return npc;
	}

	public boolean despawnById(final Integer id)
	{
		final NPC npc = this.npcs.get(id);
		if(npc != null)
		{
			Bukkit.getScheduler().cancelTask(this.npcRotationTasks.get(id));
			this.npcRotationTasks.remove(id);
			this.npcs.remove(id);
			npc.removeFromWorld();
			this.spawnedIDs.remove(id);
			return true;
		}
		return false;
	}

	public void despawnHumanByName(String npcName)
	{
		if(npcName.length() > 16)
			npcName = npcName.substring(0, 16); // Ensure you can still despawn
		final HashSet<Integer> toRemove = new HashSet<Integer>();
		for(final Integer n : this.npcs.keySet())
		{
			final NPC npc = this.npcs.get(n);
			if(npc instanceof HumanNPC)
				if(npc != null && ((HumanNPC)npc).getName().equals(npcName))
				{
					toRemove.add(n);
					npc.removeFromWorld();
				}
		}
		for(final Integer n : toRemove)
		{
			Bukkit.getScheduler().cancelTask(this.npcRotationTasks.get(n));
			this.npcRotationTasks.remove(n);
			this.npcs.remove(n);
			this.spawnedIDs.remove(n);
		}
	}

	public void despawnAll()
	{
		for(final Entry<Integer, NPC> entry : this.npcs.entrySet())
		{
			if(entry.getValue() != null)
				entry.getValue().removeFromWorld();
			Bukkit.getScheduler().cancelTask(this.npcRotationTasks.get(entry.getKey()));
		}
		this.npcRotationTasks.clear();
		this.npcs.clear();
		this.spawnedIDs.clear();
	}

	public NPC getNPC(final String id)
	{
		return this.npcs.get(id);
	}

	public boolean isNPC(final org.bukkit.entity.Entity e)
	{
		return ((CraftEntity)e).getHandle() instanceof NPCEntity;
	}

	public List<NPC> getHumanNPCByName(final String name)
	{
		final List<NPC> ret = new ArrayList<NPC>();
		final Collection<NPC> i = this.npcs.values();
		for(final NPC e : i)
			if(e instanceof HumanNPC)
				if(((HumanNPC)e).getName().equalsIgnoreCase(name))
					ret.add(e);
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

	public Integer getNPCIdFromEntity(final org.bukkit.entity.Entity e)
	{
		if(e instanceof HumanEntity)
			for(final Integer i : this.npcs.keySet())
				if(this.npcs.get(i).getBukkitEntity().getEntityId() == ((HumanEntity)e).getEntityId())
					return i;
		return null;
	}

	public void rename(final String id, String name)
	{
		if(name.length() > 16)
		{ // Check and nag if name is too long, spawn NPC anyway with shortened name.
			final String tmp = name.substring(0, 16);
			this.server.getLogger().log(Level.WARNING, "NPCs can't have names longer than 16 characters,");
			this.server.getLogger().log(Level.WARNING, name + " has been shortened to " + tmp);
			name = tmp;
		}
		final HumanNPC npc = (HumanNPC)this.getNPC(id);
		npc.setName(name);
		final BWorld b = this.getBWorld(npc.getBukkitEntity().getLocation().getWorld());
		final WorldServer s = b.getWorldServer();
		try
		{
			Method m = s.getClass().getDeclaredMethod("d", new Class[] { Entity.class });
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
			m = s.getClass().getDeclaredMethod("c", new Class[] { Entity.class });
			m.setAccessible(true);
			m.invoke(s, npc.getEntity());
		}
		catch(final Exception ex)
		{
			ex.printStackTrace();
		}
		s.everyoneSleeping();
	}

	public BServer getServer()
	{
		return this.server;
	}

	public NPCNetworkManager getNPCNetworkManager()
	{
		return this.npcNetworkManager;
	}

	public HumanNPC getNPC(final Integer id)
	{
		return (HumanNPC)this.npcs.get(id);
	}
}