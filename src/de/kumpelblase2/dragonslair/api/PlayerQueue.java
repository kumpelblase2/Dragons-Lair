package de.kumpelblase2.dragonslair.api;

import java.util.*;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.DragonsLairMain;

public class PlayerQueue
{
	private final List<QueuedPlayer> players = new ArrayList<QueuedPlayer>();
	
	public ActiveDungeon start(Dungeon dungeon)
	{
		if(!this.hasEnoughPeople(dungeon))
			return null;
		
		List<String> startingPlayers = new ArrayList<String>();
		Iterator<QueuedPlayer> it = players.iterator();
		while(it.hasNext())
		{
			QueuedPlayer player = it.next();
			if(player.getDungeon().equals(dungeon.getName()))
			{
				if(dungeon.getMaxPlayers() != 0 && startingPlayers.size() > dungeon.getMaxPlayers())
					break;
				
				startingPlayers.add(player.getPlayer().getName());
				it.remove();
			}
		}

		return DragonsLairMain.getDungeonManager().startDungeon(dungeon.getID(), startingPlayers.toArray(new String[0]));
	}
	
	public boolean hasEnoughPeople(Dungeon dungeon)
	{
		return this.getQueueForDungeon(dungeon).size() >= dungeon.getMinPlayers();
	}
	
	public List<QueuedPlayer> getQueueForDungeon(Dungeon dungeon)
	{
		List<QueuedPlayer> tempList = new ArrayList<QueuedPlayer>();
		for(QueuedPlayer player : this.players)
		{
			if(player.getDungeon().equals(dungeon.getName()))
				tempList.add(player);
		}
		return tempList;
	}
	
	public void queuePlayer(String dungeon, Player p)
	{
		if(p == null)
			return;
		
		if(this.isInQueue(p))
			return;
		
		DragonsLairMain.debugLog("Queueing player '" + p.getName() + "' for dungeon '" + dungeon + "'");
		this.players.add(new QueuedPlayer(dungeon, p));
	}
	
	public boolean isInQueue(Player p)
	{
		if(p == null)
			return false;
		
		for(QueuedPlayer qp : this.players)
		{
			if(qp.equals(p))
				return true;
		}
		return false;
	}
}
