package de.kumpelblase2.dragonslair.api.eventexecutors;

import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.api.Event;

public interface EventExecutor
{
	public boolean executeEvent(Event e, Player p);
}
