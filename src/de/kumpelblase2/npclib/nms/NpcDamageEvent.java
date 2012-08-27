package de.kumpelblase2.npclib.nms;
//original provided by Topcat, modified by kumpelblase2

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NpcDamageEvent extends EntityDamageByEntityEvent
{
	public NpcDamageEvent(Entity damager, Entity damagee, DamageCause cause, int damage)
	{
		super(damager, damagee, cause, damage);
	}
}
