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
 * @version $Id: LineRead.java,v 1.11 2007/10/25 15:13:11 u930di Exp $
 */
public class LineRead
{
	private byte tbLine[] = null; 
	private int nTotalLength = 0;
	private int nBodyLength = 0;
	private int nOffset = 0;
	
	LineRead()
	{
	}
	
	void resetAndGaranteeBufferStorage(int nMinBufferStorageLength, int nBufferStorageLengthToAlloc)
	{
		if(tbLine == null || tbLine.length < nMinBufferStorageLength)
		{
			tbLine = new byte[nBufferStorageLengthToAlloc];
		}
		nTotalLength = 0;
		nBodyLength = 0;
		nOffset = 0;
	}
	
	int readAndConvertHeaderVHToVBMode()
	{
		// Header read in VH mode are hh ll 00 00; and the length 0xhhll includes the header itself
		// Header read in VB mode are hh xx yy ll; and the length 0xhhxxyyll does not include the header itself
		int nLength = getAsLittleEndingUnsignBinaryShort();
		nLength -= 4;	// Header in VB mode do not include header length
		LittleEndingUnsignBinaryBufferStorage.writeInt(tbLine, nLength, nOffset);
		return nLength;
	}
	
	void append(LineRead lineSource)
	{
		int nSourceOffset = lineSource.getOffset();
		int nSourceLength = lineSource.getTotalLength();
		fill(lineSource.getBuffer(), nSourceOffset, nSourceLength, nTotalLength);
		nTotalLength += nSourceLength;
		nBodyLength += nSourceLength;
	}
	
	private void fill(byte tReadBytes[], int nSourceOffset, int nSourceLength, int nOffsetDest)
	{
		for(int n=0; n<nSourceLength; n++)
		{
			tbLine[nOffsetDest++] = tReadBytes[nSourceOffset++];
		}
	}
	
	public void shiftOffset(int nShiftLength)
	{
		nTotalLength -= nShiftLength;
		nBodyLength -= nShiftLength;
		nOffset += nShiftLength;
	}
	
	void set(byte tReadBytesAHead[], int nBodyFirstPositionInReadAHead, int nBodyLength, int nHeaderLength)
	{
		tbLine = tReadBytesAHead;
		nOffset = nBodyFirstPositionInReadAHead - nHeaderLength;
		nBodyLength = nBodyLength ;		
		nTotalLength = nBodyLength + nHeaderLength;
		int nDest = nOffset; 
		for(int n=0; n<nHeaderLength; n++)
		{
			tbLine[nDest++] = 0;
		}
	}
	
	public String getChunkAsString()
	{
		String cs = new String(tbLine, nOffset, nTotalLength);
		return cs;
	}
	
	public byte [] getBuffer()
	{
		return tbLine;
	}
	
	public byte [] getBufferCopy()
	{
		byte by[] = new byte[nTotalLength];
		int nSource = nOffset;
		for(int n=0; n<nTotalLength; n++)
		{
			by[n] = tbLine[nSource++];
		}
		return by;
	}
	
	public int getOffset()
	{
		return nOffset; 
	}
	
	public int getTotalLength()
	{
		return nTotalLength; 
	}
	
	public int getBodyLength()
	{
		return nBodyLength;
	}
	
	public int getBufferLength()
	{
		return tbLine.length;
	}
	
	public boolean manageTrailingLF()
	{
		if(tbLine[nOffset+nTotalLength-1] == 0x0A)
		{
			nBodyLength--;	// Do not use the trailing LF; just consume it
			nTotalLength--;
			return true;
		}		
		return false;
	}
	
	public boolean isTrailingLF()
	{
		// PJD Next line was if(tbLine[nOffset+nTotalLength-1] == 0x0A); 
		// the -1 is wrong as manageTrailingLF() must have already been called previously on this LineReadObject, so nTotalLength was decremented.
		if(tbLine[nOffset+nTotalLength] == 0x0A)	 
			return true;
		return false;
	}
	
	public int getAsLittleEndingUnsignBinaryInt()
	{
		if(nBodyLength >= 4)
			return (int)LittleEndingUnsignBinaryBufferStorage.readInt(tbLine, nOffset);
		return -1;	// Error
	}
	
	public int getAsLittleEndingUnsignBinaryShort()
	{
		if(nBodyLength >= 2)
			return (int)LittleEndingUnsignBinaryBufferStorage.readShort(tbLine, nOffset);
		return -1;	// Error
	}
	
	public void setDataLengthStartingAt0(int nLength)
	{
		nBodyLength = nLength;
		nTotalLength = nLength;
		nOffset = 0;
	}
}
