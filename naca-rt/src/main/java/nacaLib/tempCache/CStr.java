/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.tempCache;

import java.math.BigDecimal;

import jlib.misc.AsciiEbcdicConverter;
import nacaLib.varEx.Pic9Comp3BufferSupport;


/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: CStr.java,v 1.10 2007/01/23 15:04:18 u930di Exp $
 */
public class CStr
{
	//CStrManager m_manager = null;
	
	public CStr()
	{
	}

	public void resetMinimalSize(int n)
	{
		nLength = 0;
		nStartPos = 0;
		if(acBuffer != null)
			if(acBuffer.length > n)
				return;
		set(new char[n], 0, 0);
	}
		
	public void set(char acBuffer[], int nStartPos, int nLength)
	{
		this.acBuffer = acBuffer;
		this.nStartPos = nStartPos;
		this.nLength = nLength;
	}
	
	public void set(String cs)
	{
		if(cs != null)
		{
			nLength = cs.length();
			nStartPos = 0;
			acBuffer = cs.toCharArray();
		}
		else
		{
			nLength = 0;
			nStartPos = 0;
			acBuffer = null;
		}
	}

	public void removeLeft(int nNbChar)
	{
		nStartPos += +nNbChar;
		nLength -= nNbChar;
	}
	
	public void insert(int nPosition, char c)
	{
		int nNbCharRight = nLength-nPosition;
		for(int n=nNbCharRight-2; n>=0; n--)
		{
			acBuffer[nStartPos + n+1] = acBuffer[nStartPos + n];
		}
		acBuffer[nPosition] = c;
	}

	
	public int length()
	{
		return nLength;
	}
	
	public char charAt(int n)
	{
		return acBuffer[n + nStartPos]; 
	}
	
	public void setCharAt(int nPosition, char cDigit)
	{
		acBuffer[nStartPos + nPosition] = cDigit;
	}
	
	public void setLength(int n)
	{
		nLength = n;
	}
	
	public void append(char c)
	{
		acBuffer[nStartPos + nLength] = c;
		nLength++;		
	}
	
	public void append(CStr csInt)
	{
		for(int n=0; n<csInt.length(); n++)
		{
			char c = csInt.charAt(n);
			acBuffer[nStartPos + nLength] = c;
			nLength++;
		}
	}
	
	public void guaranteeMinialSize(int nMinimalSize)
	{
		if(acBuffer.length < nMinimalSize)
		{
			char acNewBuffer[] = new char [nMinimalSize];
			for(int n=0; n<acBuffer.length; n++)
			{
				acNewBuffer[n] = acBuffer[n]; 
			}
			acBuffer = acNewBuffer;
		}
	}
	
	public void selfSubstring(int nLeftPos)
	{
		nStartPos += nLeftPos;
		nLength -= nLeftPos;
	}
	
//	public void selfTrimLeftRight()
//	{
//		int n = 0;
//		for(; n<nLength; n++)
//		{
//			char c = acBuffer[n + nStartPos];
//			if(!Character.isWhitespace(c))
//				break;
//		}
//		nStartPos += n;
//		nLength -= n;
//		
//		while(nLength >= 0)
//		{
//			char c = acBuffer[nStartPos + nLength - 1];
//			if(!Character.isWhitespace(c))
//				break;
//			nLength--;
//		}
//	}
	
	public String toString()
	{
		return "\"" + new String(acBuffer, nStartPos, nLength) + "\"";
	}
	
	public String getAsString()
	{
		return new String(acBuffer, nStartPos, nLength);
	}
	
	
	public boolean isOnlyAlphabetic()
	{		
		int nMax = nStartPos+nLength;
		for(int n=nStartPos; n<nMax; n++)
		{
			char c = acBuffer[n];
			if(!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || c == ' '))
				return false;  
		}
		return true;
	}
	
	public boolean isOnlyNumeric()
	{		
		int nMax = nStartPos+nLength;
		for(int n=nStartPos; n<nMax; n++)
		{
			char c = acBuffer[n];
			if(!((c >= '0' && c <= '9') || c == '+' || c == '-' ))
				return false;  
		}
		return true;
	}

	public boolean isOnlyNumericComp0(boolean bSigned, boolean bDec)
	{		
		int nNbDec = 0;
		int nMax = nStartPos+nLength;
		for(int n=nStartPos; n<nMax-1; n++)
		{
			char c = acBuffer[n];
			if(c == '.')
				nNbDec++;
			else if(c < '0' || c > '9')
				return false;  
		}
		
		if((bDec && (nNbDec == 0 || nNbDec == 1)) || !bDec)	// Maximum 1 . for decimals
		{
			char c = acBuffer[nMax-1];
			if(bSigned)
			{
				if((c >= (char)0xC0 && c <= (char)0xC9) || (c >= (char)0xD0 && c <= (char)0xD9))   
					return true;
				return false;
			}
			else
			{
				if(c < '0' || c > '9')
					return false;
				return true;				
			}
		}
		return false;
	}
	
	public boolean isOnlyNumericComp3(boolean bSigned)
	{		
		int nMax = nStartPos+nLength;
		for(int n=nStartPos; n<nMax-1; n++)
		{
			int nByte = acBuffer[n];
			int nHigh = (nByte & 0x00F0) >> 4;
			int nLow = nByte & 0x000F;	
			if(nHigh >= 10 || nLow >= 10)
				return false;
		}
		int nByte = acBuffer[nMax-1];
		int nHigh = (nByte & 0x00F0) >> 4;
		int nLow = nByte & 0x000F;
		if(nHigh >= 10)
			return false;
		if(bSigned && Pic9Comp3BufferSupport.isValidSign(nLow))
			return true;
		if(!bSigned && Pic9Comp3BufferSupport.isValidUnsign(nLow))
			return true;
		return false;
	}
	
	public boolean isOnlyNumericComp0SignLeading(boolean bDec)
	{
		int nNbDec = 0;
		
		char c = acBuffer[nStartPos];
		if(c != '-' && c != '+')
			return false;

		int nMax = nStartPos+nLength;
		for(int n=nStartPos+1; n<nMax; n++)
		{
			c = acBuffer[n];
			if(c == '.')
				nNbDec++;
			else if(c < '0' || c > '9')
				return false;
		}
		if(bDec && (nNbDec == 0 || nNbDec == 1))	// Maximum 1 . for decimals
			return true;
		return false;
	}
	
	public boolean isOnlyNumericComp0SignTrailing(boolean bDec)
	{
		int nNbDec = 0;
		
		int nMax = nStartPos+nLength;
		for(int n=nStartPos; n<nMax-1; n++)
		{
			char c = acBuffer[n];
			if(c == '.')
				nNbDec++;
			else if(c < '0' || c > '9')
				return false;  
		}
		
		if(bDec && (nNbDec == 0 || nNbDec == 1))	// Maximum 1 . for decimals
		{
			char c = acBuffer[nMax-1];
			if(c != '-' && c != '+')
				return false;
			return true;
		}
		return false;
	}
	
	public int getAsInt()
	{
		if(nLength == 0)
			return 0;
				
		int nValue = 0;
		int nSource = nStartPos;
		int nMax = nLength + nStartPos;
		boolean bNegative = false;
		while(nSource < nMax)
		{
			char c = acBuffer[nSource++];
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
			else if(c == '+');
			else if(c == ' ');
			else if(c == '.')
				break;
			else if (nSource == nStartPos) // first char is not a digit
				return 0 ;
		}
		if(bNegative)
			return -nValue;
		return nValue;	
	}
	
	public int getAsUnsignedInt()
	{
		if(nLength == 0)
			return 0;
				
		int nValue = 0;
		int nSource = nStartPos;
		int nMax = nStartPos + nLength;
		while(nSource < nMax)
		{
			char c = acBuffer[nSource++];
			if(c >= '0' && c <= '9')
			{
				nValue = 10 * nValue + (c - '0');
				continue;
			}
			else if(c == '+');
			else if(c == ' ');
			else if(c == '.')
				break;
			else if (nSource == nStartPos) // first char is not a digit
				return 0 ;
		}
		return nValue;	
	}
	
	public long getAsLong()
	{
		if(nLength == 0)
			return 0;
				
		long lValue = 0;
		boolean bNegative = false;
		int nSource = nStartPos;
		int nMax = nLength+nSource;
		while(nSource < nMax)
		{
			char c = acBuffer[nSource++];
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
			else if(c == '+');
			else if(c == ' ');
			else if(c == '.')
				break;
			else if (nSource == nStartPos) // first char is not a digit
				return 0 ;
		}
		if(bNegative)
			return -lValue;
		return lValue;	
	}
	
	public BigDecimal makeBigDecimal()
	{
		return new BigDecimal(acBuffer , nStartPos, nLength);
	}
	
	public CStr duplicate()
	{
		CStr csCopy = new CStr();
		int nBufferLength = acBuffer.length;
		csCopy.set(new char[nBufferLength], nStartPos, nLength);
		for(int n=0; n<nBufferLength; n++)
		{
			csCopy.acBuffer[n] = acBuffer[n]; 
		}
		return csCopy;		
	}
	
	public void setEbcdic()
	{
		for(int n=0; n<nLength; n++)
		{
			char cAscii = acBuffer[n + nStartPos];
			char cEbcdic1 = AsciiEbcdicConverter.getEbcdicChar(cAscii);
			acBuffer[nStartPos + n] = cEbcdic1;
		}
	}

	
	protected char [] acBuffer;
	protected int nStartPos = 0;
	protected int nLength = 0;
}
