package com.topcat.npclib.entity;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.WorldServer;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.player.SpoutPlayer;
import com.topcat.npclib.nms.NPCEntity;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.tasks.NPCAttackTask;

public class HumanNPC extends NPC
{

	private int attackingID = -1;
	
	public HumanNPC(NPCEntity npcEntity)
	{
		super(npcEntity);
	}
	
	public void startAttacking(final LivingEntity e)
	{
		if(this.attackingID != -1)
			this.stopAttacking();
		
		this.attackingID = Bukkit.getScheduler().scheduleSyncRepeatingTask(DragonsLairMain.getInstance(), new NPCAttackTask(this, e), 0, 10L);
	}
	
	public void stopAttacking()
	{
		if(this.attackingID != -1)
			Bukkit.getScheduler().cancelTask(this.attackingID);
	}
	
	public void animateArmSwing()
	{
		((WorldServer) getEntity().world).tracker.a(getEntity(), new Packet18ArmAnimation(getEntity(), 1));
	}

	public void actAsHurt()
	{
		((WorldServer) getEntity().world).tracker.a(getEntity(), new Packet18ArmAnimation(getEntity(), 2));
	}

	public void setItemInHand(Material m)
	{
		setItemInHand(m, (short) 0);
	}

	public void setItemInHand(Material m, short damage) 
	{
		((HumanEntity) getEntity().getBukkitEntity()).setItemInHand(new ItemStack(m, 1, damage));
	}

	public void setName(String name)
	{
		((NPCEntity) getEntity()).name = name;
	}

	public String getName()
	{
		return ((NPCEntity) getEntity()).name;
	}

	public PlayerInventory getInventory()
	{
		return ((HumanEntity) getEntity().getBukkitEntity()).getInventory();
	}

	public void putInBed(Location bed)
	{
		getEntity().setPosition(bed.getX(), bed.getY(), bed.getZ());
		getEntity().a((int) bed.getX(), (int) bed.getY(), (int) bed.getZ());
	}

	public void getOutOfBed()
	{
		((NPCEntity) getEntity()).a(true, true, true);
	}

	public void setSneaking()
	{
		getEntity().setSneak(true);
	}

	public SpoutPlayer getSpoutPlayer()
	{
		try {
			Class.forName("org.getspout.spout.Spout");
			if (!(getEntity().getBukkitEntity() instanceof SpoutCraftPlayer))
				((NPCEntity) getEntity()).setBukkitEntity(new SpoutCraftPlayer((CraftServer)Bukkit.getServer(), (EntityPlayer)getEntity()));

			return (SpoutPlayer) getEntity().getBukkitEntity();
		}
		catch (ClassNotFoundException e)
		{
			return null;
		}
	}

	public void lookAtPoint(Location point)
	{
		if (getEntity().getBukkitEntity().getWorld() != point.getWorld())
			return;
		
		Location npcLoc = ((LivingEntity)getEntity().getBukkitEntity()).getEyeLocation();
		double xDiff = point.getX() - npcLoc.getX();
		double yDiff = point.getY() - npcLoc.getY();
		double zDiff = point.getZ() - npcLoc.getZ();
		double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
		double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
		double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
		if (zDiff < 0.0)
			newYaw = newYaw + Math.abs(180 - newYaw) * 2;
		
		this.setYaw((float)(newYaw - 90));
		this.setPitch((float)newPitch);
	}
	
	public void setYaw(float newYaw)
	{
		getEntity().yaw = newYaw;
		((EntityPlayer)getEntity()).X = newYaw;
	}
	
	public void setPitch(float newPitch)
	{
		getEntity().pitch = newPitch;
	}
	
	public void lookAtEntity(Entity e)
	{
		if(e instanceof Player)
			this.lookAtPoint(((Player)e).getEyeLocation());
		else
			this.lookAtPoint(e.getLocation());
	}
	
	public boolean setSkin(String url)
	{
		try
		{
			Class.forName("org.getspout.spout.Spout");
			SpoutPlayer sp = this.getSpoutPlayer();
			sp.setSkin(url);
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
		
	}

	public void stopWalking()
	{
		super.stopWalking();
	}

	public boolean isWalking()
	{
		return super.isWalking();
	}
}