/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.batchOOApi;

import jlib.misc.FileEndOfLine;
import nacaLib.varEx.VarBufferPos;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id$
 */
public class WriteBufferExt extends VarBufferPos
{
	private int nVariableRecordWholeLength = -1;
	
	WriteBufferExt(int nSize)
	{
		super(nSize);
	}
	
	void resetCurrentPosition()
	{
		nAbsolutePosition = 0;
	}
	
	public void setVariableRecordWholeLength(int n)
	{
		nVariableRecordWholeLength = n;
	}
	
	public int getVariableRecordWholeLength()
	{
		return nVariableRecordWholeLength;
	}
	
	public int getRecordCurrentPosition()
	{
		return nAbsolutePosition;
	}
	
	void fillWriteAsPicX(String cs, int  nNbCharsToWrite)
	{
		// Fill the buffer using padding as Pic X fields 
		int nLength = 0;
		if(cs != null)
		{
			nLength = cs.length();
			if(nNbCharsToWrite < nLength)
				nLength = nNbCharsToWrite;
			cs.getChars(0, nLength, acBuffer, nAbsolutePosition);
		}
		if(nLength < nNbCharsToWrite)	// Padding with BLANK on the right
		{
			int nNbChars = nNbCharsToWrite-nLength;
			for(int n=0; n<nNbChars; n++)
				acBuffer[nAbsolutePosition + n] = ' ';
		}
	}
	
	String getString(int nSize)
	{
		if (nAbsolutePosition+nSize > acBuffer.length)
			nSize = nAbsolutePosition+nSize - acBuffer.length;
		if(nAbsolutePosition < acBuffer.length)
		{
			String cs = new String(acBuffer, nAbsolutePosition, nSize);			
			return cs ;
		}
		return "";
	}
	
	void advanceCurrentPosition(int nOffset)
	{
		nAbsolutePosition += nOffset;
	}
	
	public byte [] getAsByteArrayWithTrailingLF()
	{
		byte tBytes[] = new byte[nAbsolutePosition];  
		
		for(int n=0; n<nAbsolutePosition; n++)
		{
			tBytes[n] = (byte)acBuffer[n];
		}
		return tBytes;
	}
}
