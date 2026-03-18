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

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: InternalCharBufferCompressedBackup.java,v 1.1 2006/04/19 09:53:08 cvsadmin Exp $
 */
public class InternalCharBufferCompressedBackup
{
	public InternalCharBufferCompressedBackup(InternalCharBuffer internalCharBufferSource)
	{
		copyFrom(internalCharBufferSource);
	}
	
	private void copyFrom(InternalCharBuffer internalCharBufferSource)
	{
		abBuffer = new byte[internalCharBufferSource.acBuffer.length];
		for(int n=0; n<abBuffer.length; n++)
		{
			abBuffer[n] = (byte)internalCharBufferSource.acBuffer[n];
		}
	}
	
	public int getBufferSize()
	{
		if(abBuffer != null)
			return abBuffer.length;
		return 0;
	}
	
	public void prepareAutoRemoval()
	{
		abBuffer = null;
	}
	
	
	public byte abBuffer[] = null;
}
