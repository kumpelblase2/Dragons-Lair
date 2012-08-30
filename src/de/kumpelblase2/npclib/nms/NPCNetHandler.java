package de.kumpelblase2.npclib.nms;

// original provided by Topcat, modified by kumpelblase2
import net.minecraft.server.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import de.kumpelblase2.npclib.NPCManager;

/**
 * 
 * @author martin
 */
public class NPCNetHandler extends NetServerHandler
{
	public NPCNetHandler(final NPCManager npcManager, final EntityPlayer entityplayer)
	{
		super(npcManager.getServer().getMCServer(), npcManager.getNPCNetworkManager(), entityplayer);
	}

	@Override
	public CraftPlayer getPlayer()
	{
		return new CraftPlayer((CraftServer)Bukkit.getServer(), this.player); // Fake player prevents spout NPEs
	}

	@Override
	public void d()
	{
	};

	@Override
	public void a(final Packet10Flying packet10flying)
	{
	};

	@Override
	public void a(final double d0, final double d1, final double d2, final float f, final float f1)
	{
	};

	@Override
	public void a(final Packet14BlockDig packet14blockdig)
	{
	};

	@Override
	public void a(final Packet15Place packet15place)
	{
	};

	@Override
	public void a(final String s, final Object[] aobject)
	{
	};

	@Override
	public void onUnhandledPacket(final Packet packet)
	{
	};

	@Override
	public void a(final Packet16BlockItemSwitch packet16blockitemswitch)
	{
	};

	@Override
	public void a(final Packet3Chat packet3chat)
	{
	};

	@Override
	public void a(final Packet18ArmAnimation packet18armanimation)
	{
	};

	@Override
	public void a(final Packet19EntityAction packet19entityaction)
	{
	};

	@Override
	public void a(final Packet255KickDisconnect packet255kickdisconnect)
	{
	};

	@Override
	public void sendPacket(final Packet packet)
	{
	};

	@Override
	public void a(final Packet7UseEntity packet7useentity)
	{
	};

	@Override
	public void a(final Packet9Respawn packet9respawn)
	{
	};

	@Override
	public void handleContainerClose(final Packet101CloseWindow packet101closewindow)
	{
	};

	@Override
	public void a(final Packet102WindowClick packet102windowclick)
	{
	};

	@Override
	public void a(final Packet106Transaction packet106transaction)
	{
	};

	@Override
	public int lowPriorityCount()
	{
		return super.lowPriorityCount();
	}

	@Override
	public void a(final Packet130UpdateSign packet130updatesign)
	{
	};
}