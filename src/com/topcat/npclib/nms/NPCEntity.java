package com.topcat.npclib.nms;

import net.minecraft.server.*;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import com.topcat.npclib.NPCManager;
import de.kumpelblase2.dragonslair.DragonsLairMain;

/**
 *
 * @author martin
 */
public class NPCEntity extends EntityPlayer
{
	private int lastTargetId;
	private long lastBounceTick;
	private int lastBounceId;

	public NPCEntity(NPCManager npcManager, BWorld world, String s, ItemInWorldManager itemInWorldManager)
	{
		super(npcManager.getServer().getMCServer(), world.getWorldServer(), s, itemInWorldManager);

		itemInWorldManager.b(EnumGamemode.SURVIVAL);

		netServerHandler = new NPCNetHandler(npcManager, this);
		lastTargetId = -1;
		lastBounceId = -1;
		lastBounceTick = 0;
		fauxSleeping = true;
	}

	public void setBukkitEntity(org.bukkit.entity.Entity entity)
	{
		bukkitEntity = entity;
	}

	@Override
	public boolean c(EntityHuman entity)
	{
		EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_RIGHTCLICKED);
		CraftServer server = ((WorldServer) world).getServer();
		server.getPluginManager().callEvent(event);

		return super.c(entity);
	}

	@Override
	public void b_(EntityHuman entity)
	{		
		if ((lastBounceId != entity.id || System.currentTimeMillis() - lastBounceTick > 1000) && entity.getBukkitEntity().getLocation().distanceSquared(getBukkitEntity().getLocation()) <= 1)
		{
			EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
			lastBounceId = entity.id;
		}
		
		if (lastTargetId == -1 || lastTargetId != entity.id)
		{
			EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.CLOSEST_PLAYER);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);
			lastTargetId = entity.id;
		}

		super.b_(entity);
	}

	@Override
	public void c(Entity entity)
	{
		if (lastBounceId != entity.id || System.currentTimeMillis() - lastBounceTick > 1000)
		{
			EntityTargetEvent event = new NpcEntityTargetEvent(getBukkitEntity(), entity.getBukkitEntity(), NpcEntityTargetEvent.NpcTargetReason.NPC_BOUNCED);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);

			lastBounceTick = System.currentTimeMillis();
		}

		lastBounceId = entity.id;

		super.c(entity);
	}
	
	@Override
	protected int c(DamageSource damage, int i)
	{
		if(damage.getEntity() instanceof EntityPlayer)
		{
			EntityDamageByEntityEvent event = new NpcDamageEvent(getBukkitEntity(), damage.getEntity().getBukkitEntity(), DamageCause.ENTITY_ATTACK, i);
			CraftServer server = ((WorldServer) world).getServer();
			server.getPluginManager().callEvent(event);
			
			if(event.isCancelled())
				return 0;
			
			i = event.getDamage();
		}
		
		if(DragonsLairMain.getSettings().getNPCByName(this.name).isInvincible())
			i = 0;

		return super.c(damage, i);
	}

	@Override
	public void move(double arg0, double arg1, double arg2) {
		setPosition(arg0, arg1, arg2);
	}

}