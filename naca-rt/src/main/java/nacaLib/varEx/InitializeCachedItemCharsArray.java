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
 * @version $Id$
 */
public class InitializeCachedItemCharsArray extends InitializeCachedItem
{
	InitializeCachedItemCharsArray(char tChars[], int nPosition)
	{
		this.tChars = tChars;
		this.nTemplatePosition = nPosition;
	}
	
	void apply(int nBaseAbsolutePosition, VarBufferPos varBufferPos, int nCurrentAbsolutePosition)	//, int nOffset)
	{
		int nSize = tChars.length;
		//int nPosDest = nOffset + nCurrentAbsolutePosition;
		
		int nOffsetOrigin = nTemplatePosition - nBaseAbsolutePosition;
		nCurrentAbsolutePosition += nOffsetOrigin; 
		
		for(int n=0; n<nSize; n++, nCurrentAbsolutePosition++)
		{
			varBufferPos.acBuffer[nCurrentAbsolutePosition] = tChars[n]; 
		}
	}
	
	private char [] tChars;
	private int nTemplatePosition = 0;
}
