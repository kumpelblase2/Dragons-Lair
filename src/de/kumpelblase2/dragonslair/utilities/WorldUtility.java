package de.kumpelblase2.dragonslair.utilities;

import java.util.List;
import org.bukkit.*;
import org.bukkit.entity.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class WorldUtility
{
	public static LivingEntity getNearestEntity(Location loc, List<Entity> entites, List<EntityType> excluded)
	{
		if(entites.size() == 0)
			return null;
		
		double current = Double.POSITIVE_INFINITY;
		LivingEntity nearest = null;
		
		for(Entity e : entites)
		{
			if(!(e instanceof LivingEntity) || excluded.contains(e.getType()))
				continue;
			
			if(e instanceof Player && DragonsLairMain.getDungeonManager().getNPCManager().isNPC(e))
				continue;
			
			double squared = e.getLocation().distanceSquared(loc);
			if(squared < current && squared < 1024D)
			{
				current = squared;
				nearest = (LivingEntity)e;
			}
		}
		return nearest;
	}
	
	public static void enhancedTelepot(Entity entity, Location to)
	{
		World w = to.getWorld();
		Chunk ch = w.getChunkAt(to);
		
		if(ch.isLoaded())
		{
			w.refreshChunk(ch.getX(), ch.getZ());
		}
		else
		{
			w.loadChunk(ch);
		}
		entity.teleport(to);
	}
	
	public static String locationToString(Location loc)
	{
		return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
	}
	
	public static Location stringToLocation(String locationstring)
	{
		try
		{
			String[] splitt = locationstring.split(":");
			String world = splitt[0];
			int x = Integer.parseInt(splitt[1]);
			int y = Integer.parseInt(splitt[2]);
			int z = Integer.parseInt(splitt[3]);
			if(splitt.length == 6)
			{
				float pitch = Float.parseFloat(splitt[5]);
				float yaw = Float.parseFloat(splitt[4]);
				return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
			}
			else
			{
				return new Location(Bukkit.getWorld(world), x, y, z);
			}
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
