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
public class InitializeCachedItemRepeatingChar extends InitializeCachedItem
{
	InitializeCachedItemRepeatingChar(char pad, int nPosition, int nNbchars)
	{
		this.pad = pad;
		this.nNbchars = nNbchars;
		this.nTemplatePosition = nPosition;
	}
	
	void apply(int nBaseAbsolutePosition, VarBufferPos varBufferPos, int nCurrentAbsolutePosition)	//, int nOffset)
	{
		int nOffsetOrigin = nTemplatePosition - nBaseAbsolutePosition;
		nCurrentAbsolutePosition += nOffsetOrigin; 
		for(int n=0; n<nNbchars; n++)
		{
			varBufferPos.acBuffer[nCurrentAbsolutePosition++] = pad;
		}
	}
	
	private char pad;
	private int nTemplatePosition = 0;
	private int nNbchars = 0;
}