/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 7 juil. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author PJD
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package nacaLib.varEx;

import jlib.misc.IntegerRef;
import jlib.misc.LineRead;
import nacaLib.tempCache.CStr;
import nacaLib.tempCache.TempCacheLocator;

public class VarBufferPos extends VarBuffer
{	
	protected int nAbsolutePosition = 0;	
	private CStr cstr = null;
	
	public VarBufferPos(VarBuffer varBuffer, int nPosition)
	{	
		super(varBuffer);
		nAbsolutePosition = nPosition;
	}
	
	public VarBufferPos(int nSize)
	{
		super(nSize);
		nAbsolutePosition = 0;
	}
	
	public VarBufferPos(char [] acBuffer)
	{
		super(acBuffer);
		nAbsolutePosition = 0;
	}
	
	public void setAsVar(VarBase varBase)
	{	
		shareDataBufferFrom(varBase.bufferPos);
		nAbsolutePosition = varBase.bufferPos.nAbsolutePosition;
	}
	
	void reuse(VarBuffer bufferSource, int nPosition)
	{
		shareDataBufferFrom(bufferSource);
		nAbsolutePosition = nPosition;
	}
	
	
	public String toString()
	{
		String cs = "Buffer @" + nAbsolutePosition + " (Id=" + acBuffer.hashCode() + ")";   
		return cs;
	}
//	
//	public BufChunk getBufChunkAt(int nSize)
//	{
//		if(nSize < 0)
//			nSize = 0;
//		
//		int nMaxSize = acBuffer.length - nAbsolutePosition;
//		if(nSize > nMaxSize)
//			nSize = nMaxSize;
//		BufChunk bufChunk = new BufChunk(this, nAbsolutePosition, nSize);
//		return bufChunk;
//	}
	
	public CStr getBufChunkAt(int nSize)
	{
		if(nSize < 0)
			nSize = 0;
		
		int nMaxSize = acBuffer.length - nAbsolutePosition;
		if(nSize > nMaxSize)
			nSize = nMaxSize;
		CStr cs = TempCacheLocator.getTLSTempCache().getMappedCStr();
		cs.set(acBuffer, nAbsolutePosition, nSize);
		return cs;
	}
	
	public CStr getOwnCStr(int nSize)
	{
		if(cstr == null)
			cstr = new CStr();
		if(nSize < 0)
			nSize = 0;
		
		int nMaxSize = acBuffer.length - nAbsolutePosition;
		if(nSize > nMaxSize)
			nSize = nMaxSize;
		
		cstr.set(acBuffer, nAbsolutePosition, nSize);
		return cstr;
	}
		
//	public BufChunk getBodyBufChunk(VarDefBuffer varDef)
//	{
//		int nBodyLength = varDef.getBodyLength();
//			
//		int nBodyAbsolutePosition = nAbsolutePosition + varDef.getHeaderLength();
//				
//		int nMaxSize = acBuffer.length - nBodyAbsolutePosition;
//		if(nBodyLength > nMaxSize)
//			nBodyLength = nMaxSize;
//		BufChunk bufChunk = new BufChunk(this, nBodyAbsolutePosition, nBodyLength);
//		return bufChunk;
//	}
	
//	public String getBodyBufChunkAsString(VarDefBuffer varDef)
//	{
//		int nBodyLength = varDef.getBodyLength();
//			
//		int nBodyAbsolutePosition = nAbsolutePosition + varDef.getHeaderLength();
//				
//		int nMaxSize = acBuffer.length - nBodyAbsolutePosition;
//		if(nBodyLength > nMaxSize)
//			nBodyLength = nMaxSize;
//		String cs = new String(acBuffer, nBodyAbsolutePosition, nBodyLength);
//		return cs;
//	}
	
	public CStr getBodyCStr(VarDefBuffer varDef)
	{
		int nBodyLength = varDef.getBodyLength();
			
		int nBodyAbsolutePosition = nAbsolutePosition + varDef.getHeaderLength();
				
		int nMaxSize = acBuffer.length - nBodyAbsolutePosition;
		if(nBodyLength > nMaxSize)
			nBodyLength = nMaxSize;
		//CStr cs = new CStr();
		CStr cs = TempCacheLocator.getTLSTempCache().getMappedCStr();
		cs.set(acBuffer, nBodyAbsolutePosition, nBodyLength);
		return cs;
	}
	
//	public BufChunk getBodyBufChunk(ComparisonMode mode, VarDefBuffer varDef)
//	{
//		int nBodyLength = varDef.getBodyLength();
//			
//		int nBodyAbsolutePosition = nAbsolutePosition + varDef.getHeaderLength();
//				
//		int nMaxSize = acBuffer.length - nBodyAbsolutePosition;
//		if(nBodyLength > nMaxSize)
//			nBodyLength = nMaxSize;
//		BufChunk bufChunk = new BufChunk(this, nBodyAbsolutePosition, nBodyLength);
//		if(mode == ComparisonMode.Ebcdic)
//		{
//			if(varDef.isConvertibleInEbcdic())
//			{
//				bufChunk.setEbcdic();
//			}
//		}
//		return bufChunk;
//	}
	
//	public BufChunk getBodyBufChunkAtAbsolutePosition(ComparisonMode mode, IntegerRef iAbsolutePosition, VarDefBuffer varDef)
//	{
//		int nBodyLength = varDef.getBodyLength();
//		
//		int nAbsolutePosition = iAbsolutePosition.get();
//			
//		int nBodyAbsolutePosition = nAbsolutePosition + varDef.getHeaderLength();
//				
//		int nMaxSize = acBuffer.length - nBodyAbsolutePosition;
//		if(nBodyLength > nMaxSize)
//			nBodyLength = nMaxSize;
//		BufChunk bufChunk = new BufChunk(this, nBodyAbsolutePosition, nBodyLength, true, true);
//		if(mode == ComparisonMode.Ebcdic)
//		{
//			if(varDef.isConvertibleInEbcdic())
//			{
//				bufChunk.setEbcdic();
//			}
//		}
//		iAbsolutePosition.inc(nBodyLength);
//		return bufChunk;
//	}
	
	public CStr getBodyCStrAtAbsolutePosition(ComparisonMode mode, IntegerRef iAbsolutePosition, VarDefBuffer varDef)
	{
		int nBodyLength = varDef.getBodyLength();
		
		int nAbsolutePosition = iAbsolutePosition.get();
			
		int nBodyAbsolutePosition = nAbsolutePosition + varDef.getHeaderLength();
				
		int nMaxSize = acBuffer.length - nBodyAbsolutePosition;
		if(nBodyLength > nMaxSize)
			nBodyLength = nMaxSize;
		CStr cs = TempCacheLocator.getTLSTempCache().getMappedCStr();
		cs.set(acBuffer, nBodyAbsolutePosition, nBodyLength);
		CStr csDuplicate = cs.duplicate();
		
		
		if(mode == ComparisonMode.Ebcdic)
		{
			if(varDef.isConvertibleInEbcdic())
			{
				csDuplicate.setEbcdic();
			}
		}
		iAbsolutePosition.inc(nBodyLength);
		return csDuplicate;
	}

	char[] getByteArray(VarBase var, int nLength)
	{
		int nPosSource = nAbsolutePosition;
		char[] tc = new char[nLength];
		for(int nOffset=0; nOffset<nLength; nOffset++)
		{
			tc[nOffset] = acBuffer[nPosSource++];			
		}
		return tc;
	}
	
//	void setByteArray(byte[] tBytes)
//	{
//		if(tBytes != null)
//		{
//			int nLength = tBytes.length;
//			for(int nOffset=0; nOffset<nLength; nOffset++)
//			{
//				acBuffer[nAbsolutePosition + nOffset] = (char)tBytes[nOffset];			
//			}
//		}
//	}

	void importFromByteArray(byte[] tBySource, int nLengthDest, int nLengthSource)
	{
		int nPosDest = nAbsolutePosition;
		int nLength = Math.min(nLengthSource, nLengthDest);
		if(tBySource != null)
		{
			for(int nOffset=0; nOffset<nLength; nOffset++)
			{
				int n = tBySource[nOffset];
				if(n < 0)
					n += 256;
				acBuffer[nPosDest++] = (char)n;			
			}
		}
	}
		
	void setByteArray(byte[] tBytesSource, int nOffsetSource, int nLength)
	{	
		int nPosDest = nAbsolutePosition;
		if(tBytesSource != null)
		{
			for(int n=0; n<nLength; n++)
			{
				int nSource = tBytesSource[nOffsetSource + n];
				if(nSource < 0)
					nSource += 256;
				acBuffer[nPosDest++] = (char)nSource;
			}
		}
	}
	
	void setByteArray(byte[] tSourceBytes, int nSourceOffset, int nSourceLength, VarBufferPos buf2, int nDest2Length)
	{	
		int nPosSource = nSourceOffset;
		int nPosDest1 = nAbsolutePosition;
		int nPosDest2 = buf2.nAbsolutePosition;
		if(tSourceBytes != null)
		{
			for(int n=0; n<nSourceLength; n++)
			{
				int nByte = tSourceBytes[nPosSource++];
				if(nByte < 0)
					nByte += 256;
				
				char c = (char)nByte;
				acBuffer[nPosDest1++] = c;
				if(n < nDest2Length)
					buf2.acBuffer[nPosDest2++] = c;
			}
		}
	}
	
//	void fillNull(int nOffsetDest, int nLength)
//	{
//		int m = nAbsolutePosition + nOffsetDest;
//		for(int n=0; n<nLength; n++, m++)
//		{			
//			acBuffer[m] = 0;
//		}
//	}
	
//	void fillWithSameByteAtOffset(byte by, int nOffset, int nNbOccurences)
//	{
//		int nSource = by;
//		if(nSource < 0)
//			nSource += 256;
//		char c  = (char)nSource;
//
//		for(int n=0; n<nNbOccurences; n++)
//			acBuffer[nAbsolutePosition + nOffset + n] = c;
//	}
	
	void exportIntoByteArray(byte tByDest[], int nLengthDest, int nLengthSource)
	{
		int nLength = Math.min(nLengthSource, nLengthDest);
		int n = nAbsolutePosition;
		for(int nPos=0; nPos<nLength; nPos++)
		{
			tByDest[nPos] = (byte) acBuffer[n++];
		}
	}
	
	char [] getAsCharArray(int nOffset, int nLength)
	{
		char tcDest[] = new char [nLength];
		
		int n = nOffset + nAbsolutePosition;
		for(int nPos=0; nPos<nLength; nPos++)
		{
			//tcDest[nPos] = acBuffer[n + nPos];
			tcDest[nPos] = acBuffer[n++];
		}
		return tcDest;
	}
	
	void fillBlankComp3AtOffset(int nTotalSize, int nOffset)
	{
		int nPos = nAbsolutePosition + nOffset;
		for(int n=0; n<=nTotalSize; n++)
		{
			acBuffer[nPos++] = 0; 
		}
	}
		
	void fillZeroesComp0AtOffset(int nTotalSize, int nOffset)
	{
		int nPos = nAbsolutePosition + nOffset;
		for(int n=0; n<nTotalSize; n++)
		{
			acBuffer[nPos++] = '0'; 
		}
	}
	
	void fillBlankComp0AtOffset(int nTotalSize, int nOffset)
	{
		int nPos = nAbsolutePosition + nOffset;
		for(int n=0; n<nTotalSize; n++)
		{
			acBuffer[nPos++] = ' '; 
		}
	}
	
//	void setBufferByteAtOffset(int nCharPos, byte byHighValue, byte byLowValue)
//	{
//		char cChar = (char)((byHighValue * 16) + byLowValue);
//		acBuffer[nCharPos] = cChar;
//	}
	
//	void setBufferByteAtOffset(int nCharPos, int nByteIndex, byte byValue)
//	{
//		char c = acBuffer[nCharPos];
//		if(nByteIndex == 1)
//		{
//			acBuffer[nAbsolutePosition+nCharPos] &= 0xFFF0;
//			acBuffer[nAbsolutePosition+nCharPos] |= byValue;
//		}
//		else
//		{
//			acBuffer[nAbsolutePosition+nCharPos] &= 0x000F;
//			acBuffer[nAbsolutePosition+nCharPos] |= (byValue << 4);			
//		}			
//	}
	
	void addNibbleAtOffset(int nOffset, int nNibblePos, byte byValue)
	{		
		int nCharPos = nNibblePos / 2;
		int nNibbleIndex = nNibblePos % 2;
		int nIndex = nAbsolutePosition+nOffset+nCharPos;
		if(nNibbleIndex == 1)	// char is 16 bits in 4 nibbles: 0000 0000 nible0 nibble1
		{
			acBuffer[nIndex] &= 0xFFF0;
			acBuffer[nIndex] |= byValue;
		}
		else
		{
			acBuffer[nIndex] &= 0x000F;
			acBuffer[nIndex] |= (byValue << 4);			
		}
	}
	
//	char getCharAtOffset(int nCharPos)
//	{
//		char c = acBuffer[nAbsolutePosition+nCharPos];
//		return c;
//	}
	
//	void setCharAtOffset(int nCharPos, char c)
//	{
//		acBuffer[nAbsolutePosition+nCharPos] = c;
//	}
	
	int getAsInt(int nSize)
	{
		if(nSize < 0)
			nSize = 0;
		
		int nMaxSize = acBuffer.length - nAbsolutePosition;
		if(nSize > nMaxSize)
			nSize = nMaxSize;
		
		return getAsInt(nAbsolutePosition, nSize);
	}
	
	int getAsInt(int nAbsolutePosition, int nTotalSize)
	{
		if(nTotalSize == 0)
			return 0;
		int nValue = 0;
		int n = nAbsolutePosition;
		int nMax = nAbsolutePosition + nTotalSize;
		boolean bNegative = false;
		while(n < nMax)
		{
			char c = acBuffer[n++];
			if(c >= '0' && c <= '9')
			{
				nValue = 10 * nValue + (c - '0');
				continue;
			}
			else if(c == '-')
			{
				bNegative = true;
				continue;
			}
			else if(c == '.')
				break;
		}
		if(bNegative)
			return -nValue;
		return nValue;	
	}
	
	int getAsUnsignedInt(int nSize)
	{
		if(nSize < 0)
			nSize = 0;
		
		int nMaxSize = acBuffer.length - nAbsolutePosition;
		if(nSize > nMaxSize)
			nSize = nMaxSize;
		
		return getAsUnsignedInt(nAbsolutePosition, nSize);
	}
	
	int getAsUnsignedInt(int nAbsolutePosition, int nTotalSize)
	{
		if(nTotalSize == 0)
			return 0;
		int nValue = 0;
		int n = nAbsolutePosition;
		int nMax = nAbsolutePosition + nTotalSize;
		while(n < nMax)
		{
			char c = acBuffer[n++];
			if(c >= '0' && c <= '9')
			{
				nValue = 10 * nValue + (c - '0');
				continue;
			}
			else if(c == '.')
				break;
		}
		return nValue;	
	}
	
	long getAsLong(int nSize)
	{
		if(nSize < 0)
			nSize = 0;
		
		int nMaxSize = acBuffer.length - nAbsolutePosition;
		if(nSize > nMaxSize)
			nSize = nMaxSize;
		
		return getAsLong(nAbsolutePosition, nSize);
	}
	
	public long getAsLong(int nAbsolutePosition, int nTotalSize)
	{
		if(nTotalSize == 0)
			return 0;
		
		long lValue = 0;
		int n = nAbsolutePosition;
		int nMax = nAbsolutePosition+nTotalSize;
		boolean bNegative = false;
		while(n < nMax)
		{
			char c = acBuffer[n++];
			if(c >= '0' && c <= '9')
			{
				lValue = 10 * lValue + (c - '0');
				continue;
			}
			else if(c == '-')
			{
				bNegative = true;
				continue;
			}			
			else if(c == '.')
				break;
		}
		if(bNegative)
			return -lValue;
		return lValue;	
	}
		
	long getAsUnsignedLong(int nSize)
	{
		if(nSize < 0)
			nSize = 0;
		
		int nMaxSize = acBuffer.length - nAbsolutePosition;
		if(nSize > nMaxSize)
			nSize = nMaxSize;
		
		return getAsUnsignedLong(nAbsolutePosition, nSize);
	}
	
	public long getAsUnsignedLong(int nAbsolutePosition, int nTotalSize)
	{
		if(nTotalSize == 0)
			return 0;
		
		long lValue = 0;
		int n = nAbsolutePosition;
		int nMax = nAbsolutePosition+nTotalSize;
		while(n < nMax)
		{
			char c = acBuffer[n++];
			if(c >= '0' && c <= '9')
			{
				lValue = 10 * lValue + (c - '0');
				continue;
			}
			else if(c == '-')
			{
				continue;
			}			
			else if(c == '.')
				break;
		}
		return lValue;	
	}
	
	public void copy(int nNbCharToCopy, VarBufferPos varBufPosSource)
	{
		copyBytes(nAbsolutePosition, nNbCharToCopy, varBufPosSource.nAbsolutePosition, varBufPosSource);
	}
	
	void restore(int nOldAbsolutePosition, char acOldBuffer[])	//, boolean bShared)
	{
		nAbsolutePosition = nOldAbsolutePosition;
		acBuffer = acOldBuffer;
	}
	
	
	// Experimental performance code
	
//	private int nLastChecksum = 0;
//	public int nLastValue = 0;
//
//	public void resetLastChecksum()
//	{
//		nLastChecksum = 0;
//	}
//	
//	public void setChecksum(int n)
//	{
//		nLastChecksum = n;
//	}
//	
//	public boolean isLastChecksumValid(int nTotalSize)
//	{
//		int nChecksum = 0;
//		for(int n=0; n<nTotalSize; n++)
//		{
//			nChecksum += acBuffer[n+nAbsolutePosition];
//		}
//		if(nChecksum == nLastChecksum)
//			return true;
//		nLastChecksum = nChecksum;
//		return false;
//	}
}



class CEditSemanticContextMapAssoc
{
	CEditSemanticContextMapAssoc(Edit edit, String csSemantiContext)
	{
		edit = edit;
		csSemantiContext = csSemantiContext;
	}
	Edit edit = null; 
	String csSemantiContext = null;
}

