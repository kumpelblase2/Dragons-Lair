package de.kumpelblase2.dragonslair.api;

import java.util.*;
import org.bukkit.Location;

public class TriggerLocationEntry
{
	private Map<TriggerType, Set<Trigger>> triggers = new HashMap<TriggerType, Set<Trigger>>();
	private Location location;
	
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
		this.triggers.put(t.getType(), new HashSet<Trigger>());
		this.triggers.get(t.getType()).add(t);
	}
	
	public Set<Trigger> getTriggersForType(TriggerType type)
	{
		return this.triggers.get(type);
	}
	
	public boolean equals(Location loc)
	{
		return this.location.getWorld().getName().equals(loc.getWorld().getName()) && this.location.getBlockX() == loc.getBlockX() && this.location.getBlockY() == loc.getBlockY() && this.location.getBlockZ() == loc.getBlockZ();
	}
}
