package de.kumpelblase2.dragonslair.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QueuedPlayer
{
	private final String dungeon;
	private final String player;

	public QueuedPlayer(final String dungeon, final Player p)
	{
		this.dungeon = dungeon;
		this.player = p.getName();
	}

	public boolean isPlayer(final Object object)
	{
		if(object instanceof String)
			return this.player.equals(object);
		else if(object instanceof Player)
			return this.player.equals(((Player)object).getName());
		return false;
	}

	public Player getPlayer()
	{
		return Bukkit.getPlayer(this.player);
	}

	public String getDungeon()
	{
		return this.dungeon;
	}
	
	public int hashCode()
	{
		return this.dungeon.length() + this.player.length();
	}
}
