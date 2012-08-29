package de.kumpelblase2.dragonslair.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import de.kumpelblase2.dragonslair.api.Trigger;

public class TriggerCallEvent extends BaseEvent
{
	private final Trigger m_trigger;
	private final Player m_player;
	private boolean m_onCooldown;
	
	public TriggerCallEvent(Trigger t, final Player p, boolean onCD)
	{
		this.m_player = p;
		this.m_trigger = t;
		this.m_onCooldown = onCD;
	}
	
	public void setOnCooldown(boolean onCD)
	{
		this.m_onCooldown = onCD;
	}
	
	public boolean isOnCooldown()
	{
		return this.m_onCooldown;
	}
	
	public Trigger getTrigger()
	{
		return this.m_trigger;
	}
	
	public final Player getPlayer()
	{
		return this.m_player;
	}
	
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
