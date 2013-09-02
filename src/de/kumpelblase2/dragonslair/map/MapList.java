package de.kumpelblase2.dragonslair.map;

import java.util.*;
import org.bukkit.entity.Player;

public class MapList
{
	private final Set<MapEntry> maps = new HashSet<MapEntry>();

	public DLMap getMapOfPlayer(final Player p)
	{
		if(this.maps.size() == 0)
			return null;

		for(final MapEntry map : this.maps)
		{
			if(map.is(p))
				return map.getMap();
		}

		return null;
	}

	public void addMap(final DLMap map)
	{
		this.maps.add(new MapEntry(map));
	}

	public void removeMap(final Player p)
	{
		final Iterator<MapEntry> maplist = this.maps.iterator();
		while(maplist.hasNext())
		{
			final MapEntry map = maplist.next();
			if(map.is(p.getName()))
			{
				map.clear();
				maplist.remove();
				return;
			}
		}
	}
}