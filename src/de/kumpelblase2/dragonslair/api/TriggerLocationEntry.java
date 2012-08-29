package de.kumpelblase2.dragonslair.api;

import java.util.*;
import org.bukkit.Location;

public class TriggerLocationEntry
{
	private final Map<TriggerType, Set<Trigger>> triggers = new HashMap<TriggerType, Set<Trigger>>();
	private final Location location;
	
	public TriggerLocationEntry(Location loc)
	{
		this.location = loc;
	}
	
	public boolean isLocation(Location loc)
	{
		return this.location.getBlockX() == loc.getBlockX() && this.location.getBlockY() == loc.getBlockY() && this.location.getBlockZ() == loc.getBlockZ();
	}
	
	public Map<TriggerType, Set<Trigger>> getTriggers()
	{
		return this.triggers;
	}
	
	public void addTrigger(Trigger t)
	{
		Set<Trigger> temp = new HashSet<Trigger>();
		temp.add(t);
		this.triggers.put(t.getType(), temp);
	}
	
	public Set<Trigger> getTriggersForType(TriggerType type)
	{
		Set<Trigger> triggers = this.triggers.get(type);
		if(triggers != null)
			return triggers;
		else
			return new HashSet<Trigger>();
	}
	
	public boolean equals(Object o)
	{
		if(!(o instanceof Location))
			return false;
		
		Location loc = (Location)o;
		
		return this.location.getWorld().getName().equals(loc.getWorld().getName()) && this.location.getBlockX() == loc.getBlockX() && this.location.getBlockY() == loc.getBlockY() && this.location.getBlockZ() == loc.getBlockZ();
	}
}
