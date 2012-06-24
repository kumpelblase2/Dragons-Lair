package com.topcat.npclib.nms;

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
		byte[] buf = new byte[5];
		return new ByteArrayInputStream(buf);
	}

	@Override
	public OutputStream getOutputStream()
	{
		return new ByteArrayOutputStream();
	}

}