package de.kumpelblase2.dragonslair.api;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QueuedPlayer
{
	private String dungeon;
	private String player;
	
	public QueuedPlayer(String dungeon, Player p)
	{
		this.dungeon = dungeon;
		this.player = p.getName();
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof String)
		{
			return this.player.equals(object);
		}
		else if(object instanceof Player)
		{
			return this.player.equals(((Player)object).getName());
		}
		
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
}
