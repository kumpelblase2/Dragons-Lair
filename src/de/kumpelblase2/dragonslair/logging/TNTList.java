package de.kumpelblase2.dragonslair.logging;

import java.util.*;
import org.bukkit.Location;

public class TNTList
{
	private final Set<TNTEntry> tntEntries = new HashSet<TNTEntry>();

	public class TNTEntry
	{
		private final Location loc;
		private final String dungeon;

		public TNTEntry(final Location l, final String d)
		{
			this.loc = l;
			this.dungeon = d;
		}

		public Location getLocation()
		{
			return this.loc;
		}

		public String getDungeon()
		{
			return this.dungeon;
		}

		@Override
		public boolean equals(final Object o)
		{
			if(o instanceof String)
				return this.dungeon.equals(o);
			else
			{
				Location l;
				if(o instanceof TNTEntry)
					l = ((TNTEntry)o).getLocation();
				else if(o instanceof Location)
					l = (Location)o;
				else
					return false;

				return l.getWorld().getName().equals(this.loc.getWorld().getName()) && l.getBlockX() == this.loc.getBlockX() && l.getBlockY() == this.loc.getBlockY() && l.getBlockZ() == this.loc.getBlockZ();
			}
		}

		public int hashCode()
		{
			return this.loc.hashCode();
		}
	}

	public void addEntry(final String dungeon, final Location l)
	{
		this.tntEntries.add(new TNTEntry(l, dungeon));
	}

	public TNTEntry getEntry(final Location l)
	{
		for(final TNTEntry t : this.tntEntries)
		{
			if(t.equals(l))
				return t;
		}

		return null;
	}

	public boolean hasEntry(final Location l)
	{
		return this.getEntry(l) != null;
	}

	public void removeEntry(final Location l)
	{
		final Iterator<TNTEntry> it = this.tntEntries.iterator();
		while(it.hasNext())
		{
			final TNTEntry e = it.next();
			if(e.equals(l))
			{
				this.tntEntries.remove(e);
				return;
			}
		}
	}

	public boolean hasEntries()
	{
		return this.tntEntries.size() > 0;
	}

	public Set<TNTEntry> getEntriesForDungeon(final String dungeon)
	{
		final Set<TNTEntry> subSet = new HashSet<TNTEntry>();
		for(final TNTEntry e : this.tntEntries)
		{
			if(e.equals(dungeon))
				subSet.add(e);
		}

		return subSet;
	}
}