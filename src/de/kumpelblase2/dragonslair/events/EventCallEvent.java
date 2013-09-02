package de.kumpelblase2.dragonslair.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.api.Event;

public class EventCallEvent extends BaseEvent
{
	private final Player m_player;
	private final Event m_event;
	private boolean m_onCooldown;
	private static final HandlerList handlers = new HandlerList();

	public EventCallEvent(final Event e, final Player p, final boolean onCD)
	{
		this.m_player = p;
		this.m_event = e;
		this.m_onCooldown = onCD;
	}

	public void setOnCooldown(final boolean onCD)
	{
		this.m_onCooldown = onCD;
	}

	public boolean isOnCooldown()
	{
		return this.m_onCooldown;
	}

	public Event getEvent()
	{
		return this.m_event;
	}

	public final Player getPlayer()
	{
		return this.m_player;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}
}