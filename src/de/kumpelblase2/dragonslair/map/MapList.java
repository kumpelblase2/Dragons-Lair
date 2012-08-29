package de.kumpelblase2.dragonslair.map;

import java.util.*;
import org.bukkit.entity.Player;

public class MapList
{
	private final Set<MapEntry> maps = new HashSet<MapEntry>();
	
	public DLMap getMapOfPlayer(Player p)
	{
		if(maps.size() == 0)
			return null;
		
		for(MapEntry map : maps)
		{
			if(map.equals(p))
				return map.getMap();
		}
		return null;
	}
	
	public void addMap(Player p, DLMap map)
	{
		this.maps.add(new MapEntry(map));
	}
	
	public void removeMap(Player p)
	{
		Iterator<MapEntry> maplist = this.maps.iterator();
		while(maplist.hasNext())
		{
			MapEntry map = maplist.next();
			if(map.equals(p.getName()))
			{
				map.clear();
				maplist.remove();
				return;
			}
		}
	}
}
