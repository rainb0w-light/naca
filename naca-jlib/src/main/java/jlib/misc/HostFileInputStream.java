/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class HostFileInputStream extends InputStream
{
	protected InputStream stream = null ;
	protected boolean bHeaderVariable = false;
	protected int nLength = 0;
	protected int[] record = null;
	protected int nCurrentRecordRead = 0;
	private byte[] tbyHeader = new byte[4];
	
	public HostFileInputStream(InputStream is, String csFormat, int nLength)
	{
		stream = is;
		if (csFormat != null && csFormat.equals("VB")) {
			bHeaderVariable = true;
		}
		nLength = nLength;
	}

	public int read() throws IOException
	{
		if (record == null)
		{
			if (stream.available() == 0)
			{
				return -1;
			}
			Vector<Integer> v = new Vector<Integer>();
			if (bHeaderVariable)
			{
				tbyHeader[0] = (byte)stream.read();
				tbyHeader[1] = (byte)stream.read();
				tbyHeader[2] = (byte)stream.read();
				tbyHeader[3] = (byte)stream.read();
				int nLengthExcludingHeader = LittleEndingSignBinaryBufferStorage.readInt(tbyHeader, 0);
				for (int i=0; i < nLengthExcludingHeader; i++)
				{
					v.add(stream.read());
				}
				stream.read();
			}
			else
			{
				if (nLength == 0)
				{
					int b = stream.read();
					while (b != '\n' && b != -1)
					{
						v.add(b) ;
						b = stream.read();
					}
				}
				else
				{
					for (int i=0; i < nLength; i++)
					{
						v.add(stream.read());
					}
					stream.read();
				}
			}
			
			record = new int[v.size()+3];
			if (stream.available() == 0)
			{
				record[0] = 64;
			}
			else
			{
				record[0] = 128;
			}
			record[1] = v.size() / 256; 
			record[2] = v.size() % 256;
			for (int i=0; i<v.size(); i++)
			{
				record[i+3] = v.get(i);
			}
			nCurrentRecordRead = 0;
		}
		
		int b = record[nCurrentRecordRead];
		nCurrentRecordRead++;
		if (nCurrentRecordRead == record.length)
		{
			record = null;
			nCurrentRecordRead = 0;
		}
		
		return b;
	}

	public int available() throws IOException
	{
		return stream.available() + record.length-nCurrentRecordRead ;
	}

	public void close() throws IOException
	{
		stream.close() ;
	}
}