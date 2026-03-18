/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.misc;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: MarkerFile.java,v 1.1 2008/03/18 13:30:26 u930di Exp $
 */
public class MarkerFile
{
	private String csMakerPath = null;
	private FileLock outLock = null;
	private BufferedOutputStream out = null;
	
	public MarkerFile(String csMakerPath)
	{
		csMakerPath = csMakerPath;
	}
	
	public boolean exclusiveLockFile()
	{
		try
		{
			FileOutputStream fileOutput = new FileOutputStream(csMakerPath, false);
			out = new BufferedOutputStream(new DataOutputStream(fileOutput));
			FileChannel outChannel = fileOutput.getChannel();
			try
			{
				outLock = outChannel.lock();
			}
			catch(IOException e)
			{
				return false;	
			}
			return true;
		}
		catch (FileNotFoundException e)
		{
			//Logger.error("Marker file " + csMakerPath + " doesn't exists and thus cannot be exclivilly locked !");
			return false;
		} 
	}
	
	public boolean unlockFile()
	{
		try
		{
			if(out != null)
			{
				if(outLock != null)
				{
					outLock.release();
					outLock = null;
				}
				out.close();
				out = null;
				return true;
			}
		}
		catch (IOException e)
		{
			return false;
		}
		return false;
	}
}
