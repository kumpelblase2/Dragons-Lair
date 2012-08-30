package de.kumpelblase2.npclib.entity;

// original provided by Topcat, modified by kumpelblase2
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet18ArmAnimation;
import net.minecraft.server.WorldServer;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.player.SpoutPlayer;
import de.kumpelblase2.dragonslair.DragonsLairMain;
import de.kumpelblase2.dragonslair.tasks.NPCAttackTask;
import de.kumpelblase2.npclib.nms.NPCEntity;

public class HumanNPC extends NPC
{
	private int attackingID = -1;

	public HumanNPC(final NPCEntity npcEntity)
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
		((WorldServer)this.getEntity().world).tracker.a(this.getEntity(), new Packet18ArmAnimation(this.getEntity(), 1));
	}

	public void actAsHurt()
	{
		((WorldServer)this.getEntity().world).tracker.a(this.getEntity(), new Packet18ArmAnimation(this.getEntity(), 2));
	}

	public void setItemInHand(final Material m)
	{
		this.setItemInHand(m, (short)0);
	}

	public void setItemInHand(final Material m, final short damage)
	{
		((HumanEntity)this.getEntity().getBukkitEntity()).setItemInHand(new ItemStack(m, 1, damage));
	}

	public void setName(final String name)
	{
		((NPCEntity)this.getEntity()).name = name;
	}

	public String getName()
	{
		return ((NPCEntity)this.getEntity()).name;
	}

	public PlayerInventory getInventory()
	{
		return ((HumanEntity)this.getEntity().getBukkitEntity()).getInventory();
	}

	public void putInBed(final Location bed)
	{
		this.getEntity().setPosition(bed.getX(), bed.getY(), bed.getZ());
		this.getEntity().a((int)bed.getX(), (int)bed.getY(), (int)bed.getZ());
	}

	public void getOutOfBed()
	{
		((NPCEntity)this.getEntity()).a(true, true, true);
	}

	public void setSneaking()
	{
		this.getEntity().setSneaking(true);
	}

	public SpoutPlayer getSpoutPlayer()
	{
		try
		{
			Class.forName("org.getspout.spout.Spout");
			if(!(this.getEntity().getBukkitEntity() instanceof SpoutCraftPlayer))
				((NPCEntity)this.getEntity()).setBukkitEntity(new SpoutCraftPlayer((CraftServer)Bukkit.getServer(), (EntityPlayer)this.getEntity()));
			return (SpoutPlayer)this.getEntity().getBukkitEntity();
		}
		catch(final ClassNotFoundException e)
		{
			return null;
		}
	}

	public void lookAtPoint(final Location point)
	{
		if(this.getEntity().getBukkitEntity().getWorld() != point.getWorld())
			return;
		final Location npcLoc = ((LivingEntity)this.getEntity().getBukkitEntity()).getEyeLocation();
		final double xDiff = point.getX() - npcLoc.getX();
		final double yDiff = point.getY() - npcLoc.getY();
		final double zDiff = point.getZ() - npcLoc.getZ();
		final double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		final double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
		double newYaw = Math.acos(xDiff / DistanceXZ) * 180 / Math.PI;
		final double newPitch = Math.acos(yDiff / DistanceY) * 180 / Math.PI - 90;
		if(zDiff < 0.0)
			newYaw = newYaw + Math.abs(180 - newYaw) * 2;
		this.setYaw((float)(newYaw - 90));
		this.setPitch((float)newPitch);
	}

	public void setYaw(final float newYaw)
	{
		this.getEntity().yaw = newYaw;
		((EntityPlayer)this.getEntity()).as = newYaw;
	}

	public void setPitch(final float newPitch)
	{
		this.getEntity().pitch = newPitch;
	}

	public void lookAtEntity(final Entity e)
	{
		if(e instanceof Player)
			this.lookAtPoint(((Player)e).getEyeLocation());
		else
			this.lookAtPoint(e.getLocation());
	}

	public boolean setSkin(final String url)
	{
		try
		{
			Class.forName("org.getspout.spout.Spout");
			final SpoutPlayer sp = this.getSpoutPlayer();
			sp.setSkin(url);
			return true;
		}
		catch(final ClassNotFoundException e)
		{
			return false;
		}
	}

	@Override
	public void stopWalking()
	{
		super.stopWalking();
	}

	@Override
	public boolean isWalking()
	{
		return super.isWalking();
	}
}