package de.kumpelblase2.dragonslair.api;

import java.util.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class PlayerQueue
{
	private Set<QueuedPlayer> players = new HashSet<QueuedPlayer>();
	
	public ActiveDungeon start(Dungeon dungeon)
	{
		List<String> startingPlayers = new ArrayList<String>();
		Iterator<QueuedPlayer> it = players.iterator();
		while(it.hasNext())
		{
			QueuedPlayer player = it.next();
			if(player.getDungeon().equals(dungeon.getName()))
			{
				if(dungeon.getMaxPlayers() != 0 && startingPlayers.size() <= dungeon.getMaxPlayers())
					break;
				
				startingPlayers.add(player.getPlayer().getName());
				it.remove();
			}
		}
		
		return DragonsLairMain.getInstance().getDungeonManager().startDungeon(dungeon.getID(), startingPlayers.toArray(new String[0]));
	}
	
	public void queuePlayer(String dungeon, Player p)
	{
		if(this.isInQueue(p))
			return;
		
		this.players.add(new QueuedPlayer(dungeon, p));
	}
	
	public boolean isInQueue(Player p)
	{
		return players.contains(p);
	}
}
