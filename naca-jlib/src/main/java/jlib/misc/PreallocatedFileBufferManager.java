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

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: PreallocatedFileBufferManager.java,v 1.1 2006/07/18 12:27:44 u930di Exp $
 */
public class PreallocatedFileBufferManager
{
	private byte[] tBytes = null;
	private int nOverheadSize = 1000;
	
	PreallocatedFileBufferManager()
	{
		nOverheadSize = 1000;
	}
	
	PreallocatedFileBufferManager(int nOverheadSize)
	{
		nOverheadSize = nOverheadSize;
	}
	
	protected byte[] checkBuffer(int nSize)
	{
		if(tBytes == null || tBytes.length < nSize)
			tBytes = new byte[nSize + nOverheadSize];
		return tBytes;
	}
	
	public byte[] getByteBuffer(int nSize)
	{
		checkBuffer(nSize);
		return tBytes;
	}
	
	byte[] getBytes()
	{
		return tBytes; 
	}
}
