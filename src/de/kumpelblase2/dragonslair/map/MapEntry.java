package de.kumpelblase2.dragonslair.map;

import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class MapEntry
{
	private DLMap map;
	
	public MapEntry(DLMap map)
	{
		this.map = map;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(object instanceof Player)
		{
			return this.map.getPlayer().getName().equals(((Player)object).getName());
		}
		else if(object instanceof DLMap)
		{
			return this.map.equals((DLMap)object);
		}
		else if(object instanceof String)
		{
			return this.map.getPlayer().getName().equals((String)object);
		}
		return false;
	}
	
	public Player getPlayer()
	{
		return this.map.getPlayer();
	}
	
	public DLMap getMap()
	{
		return this.map;
	}

	public void clear()
	{
		for(ItemStack item : this.map.getPlayer().getInventory().all(Material.MAP).values())
		{
			MapView view = Bukkit.getMap(item.getDurability());
			if(view != null)
			{
				Iterator<MapRenderer> renderers = view.getRenderers().iterator();
				while(renderers.hasNext())
				{
					MapRenderer rend = renderers.next();
					if(rend instanceof DLMapRenderer)
						renderers.remove();
				}
			}
		}
		this.map = null;
	}
}
