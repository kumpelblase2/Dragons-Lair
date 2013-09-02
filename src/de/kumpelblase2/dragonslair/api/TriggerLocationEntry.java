package de.kumpelblase2.dragonslair.api;

import java.util.*;
import org.bukkit.Location;

public class TriggerLocationEntry
{
	private final Map<TriggerType, Set<Trigger>> triggers = new HashMap<TriggerType, Set<Trigger>>();
	private final Location location;

	public TriggerLocationEntry(final Location loc)
	{
		this.location = loc;
	}

	public boolean isLocation(final Location loc)
	{
		return this.location.getBlockX() == loc.getBlockX() && this.location.getBlockY() == loc.getBlockY() && this.location.getBlockZ() == loc.getBlockZ();
	}

	public Map<TriggerType, Set<Trigger>> getTriggers()
	{
		return this.triggers;
	}

	public void addTrigger(final Trigger t)
	{
		final Set<Trigger> temp = new HashSet<Trigger>();
		temp.add(t);
		this.triggers.put(t.getType(), temp);
	}

	public Set<Trigger> getTriggersForType(final TriggerType type)
	{
		final Set<Trigger> triggers = this.triggers.get(type);
		if(triggers != null)
			return triggers;
		else
			return new HashSet<Trigger>();
	}

	@Override
	public boolean equals(final Object o)
	{
		if(!(o instanceof Location))
			return false;

		final Location loc = (Location)o;
		return this.location.getWorld().getName().equals(loc.getWorld().getName()) && this.location.getBlockX() == loc.getBlockX() && this.location.getBlockY() == loc.getBlockY() && this.location.getBlockZ() == loc.getBlockZ();
	}

	@Override
	public int hashCode()
	{
		return location.hashCode();
	}
}