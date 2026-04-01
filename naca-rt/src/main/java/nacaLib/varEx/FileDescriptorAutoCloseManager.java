/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.varEx;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: FileDescriptorAutoCloseManager.java,v 1.1 2006/05/19 06:24:25 cvsadmin Exp $
 */
public class FileDescriptorAutoCloseManager
{
	public FileDescriptorAutoCloseManager()
	{		
	}
	
	public void reportFileDescriptorStatus(FileDescriptor fileDescriptor, FileDescriptorOpenStatus status)
	{
		if(hashFileDescriptor != null && !isisInAutoClose)
		{
			hashFileDescriptor.remove(fileDescriptor);
			hashFileDescriptor.put(fileDescriptor, status);
		}
	}
	
	public void registerFileDescriptor(FileDescriptor fileDescriptor)
	{
		if(hashFileDescriptor == null)
			hashFileDescriptor = new Hashtable<FileDescriptor, FileDescriptorOpenStatus>();
		hashFileDescriptor.put(fileDescriptor, FileDescriptorOpenStatus.CLOSE);
	}
	
	public void autoClose()
	{
		if(hashFileDescriptor != null)
		{
			isisInAutoClose = true;
			Set<Entry<FileDescriptor, FileDescriptorOpenStatus> > entries = hashFileDescriptor.entrySet();
			Iterator<Entry<FileDescriptor, FileDescriptorOpenStatus> > iter = entries.iterator();
			while (iter.hasNext())
			{
				Entry<FileDescriptor, FileDescriptorOpenStatus> entry = iter.next();
				if(entry.getValue() == FileDescriptorOpenStatus.OPEN)
				{
					FileDescriptor fileDescriptor = entry.getKey();
					fileDescriptor.close();
				}
			}
			hashFileDescriptor.clear();
			isisInAutoClose = false;
		}		
	}
	
	private Hashtable<FileDescriptor, FileDescriptorOpenStatus> hashFileDescriptor  = null;
	private boolean isisInAutoClose = false;
}
