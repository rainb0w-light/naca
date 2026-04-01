/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

public class HostFileOuputStream extends OutputStream
{
	protected OutputStream stream = null ;
	protected boolean isheaderVariable = false;
	protected int nCurrentRecordLength = 0;
	protected int nCurrentRecordWritten = 0;
	protected Vector<Integer> recordHeader = new Vector<Integer>();
	private byte[] tbyHeader = new byte[4];
	
	public HostFileOuputStream(OutputStream stream, String csFormat, boolean bHeaderEbcdic)
	{
		stream = stream ;
		if (csFormat != null && csFormat.equals("VB")) {
			isheaderVariable = true;
		}
		if (bHeaderEbcdic) {
			try
			{
				stream.write(new String("<FileHeader Version=\"1\" Encoding=\"ebcdic\"/>").getBytes());
				FileSystem.WriteEOL(stream);
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void write(int arg0) throws IOException
	{
		if  (nCurrentRecordLength == 0)
		{
			recordHeader.add(arg0 >= 0 ? arg0 : 256 + arg0);
			
			if (recordHeader.size() >= 3)
			{
				int i1 = recordHeader.get(1);
				int i2 = recordHeader.get(2);
				nCurrentRecordLength = i1 * 256 + i2;
				nCurrentRecordWritten = 0;
				recordHeader.clear();
				
				if (isheaderVariable)
				{
					LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader, nCurrentRecordLength, 0);
					stream.write(tbyHeader);
				}
				
				if (nCurrentRecordLength == 0)
					FileSystem.WriteEOL(stream);
			}
		}
		else
		{
			stream.write(arg0);
			nCurrentRecordWritten++;
			if (nCurrentRecordWritten == nCurrentRecordLength)
			{
				FileSystem.WriteEOL(stream);
				nCurrentRecordLength = 0;
			}
		}
	}

	public void close() throws IOException
	{
		stream.close() ;
	}

	public void flush() throws IOException
	{
		stream.flush()  ;
	}
}