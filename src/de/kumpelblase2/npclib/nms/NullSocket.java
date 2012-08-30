package de.kumpelblase2.npclib.nms;

// original provided by Topcat, modified by kumpelblase2
import java.io.*;
import java.net.Socket;

/**
 * 
 * @author martin
 */
public class NullSocket extends Socket
{
	@Override
	public InputStream getInputStream()
	{
		final byte[] buf = new byte[5];
		return new ByteArrayInputStream(buf);
	}

	@Override
	public OutputStream getOutputStream()
	{
		return new ByteArrayOutputStream();
	}
}