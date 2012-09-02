package de.kumpelblase2.npclib.nms;

// original provided by Topcat, modified by kumpelblase2
import java.io.IOException;
import java.lang.reflect.Field;
import net.minecraft.server.*;

/**
 * 
 * @author martin
 */
public class NPCNetworkManager extends NetworkManager
{
	public NPCNetworkManager() throws IOException
	{
		super(new NullSocket(), "NPC Manager", new NetHandler()
		{
			@Override
			public boolean a()
			{
				return true;
			}
		}, null);
		try
		{
			final Field f = NetworkManager.class.getDeclaredField("m");
			f.setAccessible(true);
			f.set(this, false);
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void a(final NetHandler nethandler)
	{
	}

	@Override
	public void queue(final Packet packet)
	{
	}

	@Override
	public void a(final String s, final Object... aobject)
	{
	}

	@Override
	public void a()
	{
	}
}