/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author U930CV
 *
 */
public class InitializeManagerLowValue extends InitializeManager
{
	public void initialize(VarBufferPos buffer, VarDefBuffer varDefBuffer, int nOffset, InitializeCache initializeCache)
	{
		varDefBuffer.writeRepeatingcharAtOffset(buffer, nOffset, '\0') ;
		if(initializeCache != null)
			initializeCache.addItem('\0', buffer.nAbsolutePosition+nOffset, varDefBuffer.nTotalSize);
	}
}
