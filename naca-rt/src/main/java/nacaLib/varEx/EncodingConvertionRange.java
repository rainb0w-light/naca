/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.misc.AsciiEbcdicConverter;
import jlib.misc.LineRead;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: EncodingConvertionRange.java,v 1.15 2007/10/29 11:08:24 u930bm Exp $
 */
public class EncodingConvertionRange
{
	private static final byte BLANK_EBCDIC = (byte)0x40;
	private static final byte BLANK_ASCII = (byte)0x20;
	
	private int nPosition = 0;
	private int nLength = 0;
	private boolean bConvertOnlyIfBlank = false;
	private boolean bConvertPrint = false;
	
	public int set(int nPosition, int nLength)
	{
		this.nPosition = nPosition;
		this.nLength = nLength;
		return nPosition + nLength;  
	}
	
	public void setConvertOnlyIfBlank(boolean bConvertOnlyIfBlank)
	{
		this.bConvertOnlyIfBlank = bConvertOnlyIfBlank;
	}
	
	public void setConvertPrint(boolean bConvertPrint)
	{
		this.bConvertPrint = bConvertPrint;
	}
	
	boolean endsJustBefore(int nPosition)
	{
		if(nPosition + nLength == nPosition)
			return true;
		return false;
	}
	
	public int append(int nLength)
	{
		nLength += nLength;
		return nPosition + nLength;
	}
	
	public void convertEbcdicToAscii(VarBase varDest, int nLastPosToConvert)
	{
		int nLength = this.nLength;
		int nLastPos = nPosition + nLength -1;
		if(nLastPos > nLastPosToConvert)
			nLength = nLastPosToConvert - nPosition; 
		if(nLength > 0)
			varDest.bufferPos.convertEbcdicToAscii(nPosition, nLength);
	}
	
	public void convertEbcdicToAscii(byte tbyDest[], int nOffsetDest, int nMaxLengthDest)
	{	
		int nLength = Math.min(nMaxLengthDest, this.nLength);
		swapByteEbcdicToAscii(tbyDest, nPosition-nOffsetDest, nLength);	
	}
	public void convertAsciiToEbcdic(byte tbyDest[], int nOffsetDest, int nMaxLengthDest)
	{
		int nLength = Math.min(nMaxLengthDest, this.nLength);
		swapByteAsciiToEbcdic(tbyDest, nPosition-nOffsetDest, nLength);	
	}
	
	public void convertEbcdicToAscii(LineRead lineRead)
	{
		int nLength = Math.min(lineRead.getTotalLength() - nPosition, this.nLength);
		swapByteEbcdicToAscii(lineRead.getBuffer(), lineRead.getOffset()+nPosition, nLength);	
	}
	public void convertAsciiToEbcdic(LineRead lineRead)
	{
		int nLength = Math.min(lineRead.getTotalLength() - nPosition, this.nLength);
		swapByteAsciiToEbcdic(lineRead.getBuffer(), lineRead.getOffset()+nPosition, nLength);	
	}
	
	public int getPosition()
	{
		return nPosition;
	}
	
	public boolean isConvertOnlyIfBlank()
	{
		return bConvertOnlyIfBlank;
	}
	
	public boolean isConvertPrint()
	{
		return bConvertPrint;
	}
	
	private void swapByteEbcdicToAscii(byte tBytesData[], int nOffset, int nLength)
	{
		if (bConvertOnlyIfBlank)
			if (!isAll(tBytesData, nOffset, nLength, BLANK_EBCDIC)) return;
		if (bConvertPrint)
			AsciiEbcdicConverter.swapByteEbcdicToAsciiPrintAFP(tBytesData, nOffset, nLength);
		else
			AsciiEbcdicConverter.swapByteEbcdicToAscii(tBytesData, nOffset, nLength);
	}

	private void swapByteAsciiToEbcdic(byte tBytesData[], int nOffset, int nLength)
	{
		if (bConvertOnlyIfBlank)
			if (!isAll(tBytesData, nOffset, nLength, BLANK_ASCII)) return;
		if (bConvertPrint)
			AsciiEbcdicConverter.swapByteAsciiToEbcdicPrintAFP(tBytesData, nOffset, nLength);
		else
			AsciiEbcdicConverter.swapByteAsciiToEbcdic(tBytesData, nOffset, nLength);
	}

	private boolean isAll(byte tBytesData[], int nOffset, int nLength, byte byPattern)
	{
		for(int n=0; n<nLength; n++)
		{
			if (tBytesData[n+nOffset] != byPattern)
				return false; 
		}
		return true;
	}
}
