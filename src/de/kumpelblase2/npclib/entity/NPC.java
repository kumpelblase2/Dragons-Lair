package de.kumpelblase2.npclib.entity;

// original provided by Topcat, modified by kumpelblase2
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerMoveEvent;
import de.kumpelblase2.npclib.NPCManager;
import de.kumpelblase2.npclib.nms.NPCEntity;
import de.kumpelblase2.npclib.pathing.*;

public class NPC
{
	private final Entity entity;
	private NPCPathFinder path;
	private Iterator<Node> pathIterator;
	private Node last;
	private NPCPath runningPath;
	private int taskid;
	private Runnable onFail;

	public NPC(final Entity entity)
	{
		this.entity = entity;
	}

	public Entity getEntity()
	{
		return this.entity;
	}

	public void removeFromWorld()
	{
		try
		{
			this.entity.world.removeEntity(this.entity);
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
	}

	public org.bukkit.entity.Entity getBukkitEntity()
	{
		return this.entity.getBukkitEntity();
	}

	public void moveTo(final Location l)
	{
		this.getBukkitEntity().teleport(l);
	}

	public void pathFindTo(final Location l, final PathReturn callback)
	{
		this.pathFindTo(l, 3000, callback);
	}

	public void pathFindTo(final Location l, final int maxIterations, final PathReturn callback)
	{
		if(this.path != null)
			this.path.cancel = true;
		if(l.getWorld() != this.getBukkitEntity().getWorld())
		{
			final ArrayList<Node> pathList = new ArrayList<Node>();
			pathList.add(new Node(l.getBlock()));
			callback.run(new NPCPath(null, pathList, l));
		}
		else
		{
			this.path = new NPCPathFinder(this.getBukkitEntity().getLocation(), l, maxIterations, callback);
			this.path.start();
		}
	}

	public void walkTo(final Location l)
	{
		this.walkTo(l, 3000);
	}

	public void walkTo(final Location l, final int maxIterations)
	{
		this.pathFindTo(l, maxIterations, new PathReturn()
		{
			@Override
			public void run(final NPCPath path)
			{
				NPC.this.usePath(path, new Runnable()
				{
					@Override
					public void run()
					{
						NPC.this.walkTo(l, maxIterations);
					}
				});
			}
		});
	}

	public void usePath(final NPCPath path)
	{
		this.usePath(path, new Runnable()
		{
			@Override
			public void run()
			{
				NPC.this.walkTo(NPC.this.runningPath.getEnd(), 3000);
			}
		});
	}

	public void usePath(final NPCPath path, final Runnable onFail)
	{
		if(this.taskid == 0)
			this.taskid = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(NPCManager.plugin, new Runnable()
			{
				@Override
				public void run()
				{
					NPC.this.pathStep();
				}
			}, 6L, 6L);
		this.pathIterator = path.getPath().iterator();
		this.runningPath = path;
		this.onFail = onFail;
	}

	private void pathStep()
	{
		if(this.pathIterator.hasNext())
		{
			final Node n = this.pathIterator.next();
			if(n.b.getWorld() != this.getBukkitEntity().getWorld())
				this.getBukkitEntity().teleport(n.b.getLocation());
			else
			{
				float angle = this.getEntity().yaw;
				float look = this.getEntity().pitch;
				if(this.last == null || this.runningPath.checkPath(n, this.last, true))
				{
					if(this.last != null)
					{
						angle = (float)Math.toDegrees(Math.atan2(this.last.b.getX() - n.b.getX(), n.b.getZ() - this.last.b.getZ()));
						look = (float)(Math.toDegrees(Math.asin(this.last.b.getY() - n.b.getY())) / 2);
					}
					final PlayerMoveEvent event = new PlayerMoveEvent(((NPCEntity)this.getEntity()).getBukkitEntity(), this.getEntity().getBukkitEntity().getLocation(), n.b.getLocation());
					Bukkit.getPluginManager().callEvent(event);
					if(event.isCancelled())
					{
						this.path.cancel = true;
						return;
					}
					this.getEntity().setPositionRotation(n.b.getX() + 0.5, n.b.getY(), n.b.getZ() + 0.5, angle, look);
					((EntityPlayer)this.getEntity()).as = angle;
				}
				else
					this.onFail.run();
			}
			this.last = n;
		}
		else
		{
			this.getEntity().setPositionRotation(this.runningPath.getEnd().getX(), this.runningPath.getEnd().getY(), this.runningPath.getEnd().getZ(), this.runningPath.getEnd().getYaw(), this.runningPath.getEnd().getPitch());
			((EntityPlayer)this.getEntity()).as = this.runningPath.getEnd().getYaw();
			Bukkit.getServer().getScheduler().cancelTask(this.taskid);
			this.taskid = 0;
		}
	}

	public void stopWalking()
	{
		if(this.path != null)
		{
			this.path.cancel = true;
			Bukkit.getServer().getScheduler().cancelTask(this.taskid);
			this.taskid = 0;
		}
	}

	public boolean isWalking()
	{
		return (this.pathIterator != null && this.pathIterator.hasNext()) && this.path != null && !this.path.cancel;
	}
}