package de.kumpelblase2.dragonslair.utilities;

import java.util.List;
import org.bukkit.*;
import org.bukkit.entity.*;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public final class WorldUtility
{
	public static LivingEntity getNearestEntity(final Location loc, final List<Entity> entites, final List<EntityType> excluded)
	{
		if(entites.size() == 0)
			return null;
		double current = Double.POSITIVE_INFINITY;
		LivingEntity nearest = null;
		for(final Entity e : entites)
		{
			if(!(e instanceof LivingEntity) || excluded.contains(e.getType()))
				continue;
			if(e instanceof Player && DragonsLairMain.getDungeonManager().getNPCManager().isRemoteEntity((LivingEntity)e))
				continue;
			final double squared = e.getLocation().distanceSquared(loc);
			if(squared < current && squared < 1024D)
			{
				current = squared;
				nearest = (LivingEntity)e;
			}
		}
		return nearest;
	}

	public static void enhancedTelepot(final Entity entity, final Location to)
	{
		final World w = to.getWorld();
		final Chunk ch = w.getChunkAt(to);
		if(ch.isLoaded())
			w.refreshChunk(ch.getX(), ch.getZ());
		else
			w.loadChunk(ch);
		entity.teleport(to);
	}

	public static String locationToString(final Location loc)
	{
		return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
	}

	public static Location stringToLocation(final String locationstring)
	{
		try
		{
			final String[] splitt = locationstring.split(":");
			final String world = splitt[0];
			final int x = Integer.parseInt(splitt[1]);
			final int y = Integer.parseInt(splitt[2]);
			final int z = Integer.parseInt(splitt[3]);
			if(splitt.length == 6)
			{
				final float pitch = Float.parseFloat(splitt[5]);
				final float yaw = Float.parseFloat(splitt[4]);
				return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
			}
			else
				return new Location(Bukkit.getWorld(world), x, y, z);
		}
		catch(final Exception e)
		{
			return null;
		}
	}
}
