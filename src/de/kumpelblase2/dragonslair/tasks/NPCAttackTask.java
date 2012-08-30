package de.kumpelblase2.dragonslair.tasks;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import de.kumpelblase2.dragonslair.utilities.InventoryUtilities;
import de.kumpelblase2.npclib.entity.HumanNPC;

public class NPCAttackTask implements Runnable
{
	private final HumanNPC npc;
	private final LivingEntity target;
	private long lastAttack = 0;

	public NPCAttackTask(final HumanNPC inNPC, final LivingEntity inTarget)
	{
		this.target = inTarget;
		this.npc = inNPC;
	}

	@Override
	public void run()
	{
		if(this.npc == null)
			return;
		if(this.target == null || this.npc.getBukkitEntity().isDead())
		{
			this.npc.stopAttacking();
			return;
		}
		final Location l = this.npc.getBukkitEntity().getLocation();
		if(!l.getWorld().getName().equals(this.target.getLocation().getWorld().getName()))
		{
			this.npc.stopAttacking();
			return;
		}
		final double squared = l.distanceSquared(this.target.getLocation());
		if(squared > 4 && squared < 100)
		{
			if(this.target instanceof Player)
			{
				final Player p = (Player)this.target;
				if(p.isFlying())
				{
					final Location playerloc = p.getLocation();
					final Block under = playerloc.getWorld().getBlockAt(playerloc.getBlockX(), playerloc.getBlockY() - 2, playerloc.getBlockZ());
					if(under == null || under.getType() == Material.AIR)
					{
						this.npc.stopAttacking();
						return;
					}
				}
			}
			this.npc.walkTo(this.target.getLocation());
		}
		else if(squared > 100)
		{
			this.npc.stopAttacking();
			return;
		}
		else
		{
			this.npc.stopWalking();
			if(System.currentTimeMillis() - this.lastAttack > 1000)
			{
				this.lastAttack = System.currentTimeMillis();
				this.npc.lookAtEntity(this.target);
				this.npc.animateArmSwing();
				this.target.damage(InventoryUtilities.getDamageFromItem(this.npc.getInventory().getItemInHand()), this.npc.getBukkitEntity());
			}
		}
	}
}
