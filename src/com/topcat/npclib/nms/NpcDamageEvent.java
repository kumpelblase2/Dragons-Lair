package com.topcat.npclib.nms;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NpcDamageEvent extends EntityDamageByEntityEvent
{
	public NpcDamageEvent(Entity damager, Entity damagee, DamageCause cause, int damage)
	{
		super(damager, damagee, cause, damage);
	}
}
