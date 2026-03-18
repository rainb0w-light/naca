/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.classLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class JarItemEntry
{
	JarItemEntry(ZipEntry zipEntry)
	{
		zipEntry = zipEntry; 
	}
	
	public int getSize()
	{
		if(zipEntry != null)
			return (int) zipEntry.getSize();
		return 0;
	}
	
	public String getName()
	{
		if(zipEntry != null)
			return zipEntry.getName();
		return null;
	}
	
	public ZipEntry getZipEntry()
	{
		return zipEntry;
	}
	
	public byte[] loadBytes(JarEntries jarEntries)
	{
		return loadBytes(jarEntries.getZipFile());
	}
	
	public byte[] loadBytes(ZipFile zipFile)
	{
		try
		{
			InputStream inputStream = zipFile.getInputStream(zipEntry);
			if(inputStream != null)
			{
		    	int nSize= (int) zipEntry.getSize();	// -1 means unknown size.
		    	if (nSize != -1)
		    	{
			    	byte[] tb = new byte[nSize];
			    	int rb = 0;
			    	int nChunk = 0;
			    	while ((nSize - rb) > 0)
			    	{
						nChunk = inputStream.read(tb, rb, nSize - rb);
			    		if (nChunk == -1)
			    		{
			    			break;
			    		}
			    		rb += nChunk;
			    	}
			    	return tb;
		    	}
		    	inputStream.close();
			}
		}
		catch (IOException e)
		{
		}
		return null;
	}

	
	private ZipEntry zipEntry = null;
}
