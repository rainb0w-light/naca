/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.tempCache;


/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: CStrNumber.java,v 1.5 2007/01/11 13:39:04 u930di Exp $
 */
public class CStrNumber extends CStr
{	
	// Char buffer is always reusable
	
	private static final int NB_MAXI_DIGIT=20;
	
	CStrNumber()
	{
		super();
		set(new char[40], 0, 40);
		for(int n=NB_MAXI_DIGIT; n<40; n++)
			acBuffer[n] = ' ';
	}
	
	private void checkBuffer(int nMinBufferLength)
	{
		if(acBuffer == null)
			acBuffer = new char[nMinBufferLength];
		else if(acBuffer.length < nMinBufferLength)
			acBuffer = new char[nMinBufferLength];
	}
	
	public void set(CStr cs, int nReserve)
	{
		nStartPos = 0;
		checkBuffer(cs.nLength + nReserve);
		
		nLength = cs.nLength;
		for(int n=0; n<cs.nLength; n++)
		{
			acBuffer[n] = cs.charAt(n);
		}
	}
	
	public void setAbsoluteValueRightPadded(int nValue, int nRequiredLength)
	{
		nStartPos = NB_MAXI_DIGIT+1;
		nLength = 0;		
		checkBuffer(nRequiredLength + NB_MAXI_DIGIT);
		
		do
		{
			nStartPos--;
			int nDigit = nValue % 10; 
			nValue /= 10; 
			char digit = (char)('0' + nDigit);
			acBuffer[nStartPos] = digit;
			nLength++;
		}
		while(nValue != 0);
		
		// Pad on right with spaces
		int n = NB_MAXI_DIGIT+1;
		while(nLength < nRequiredLength)
		{
			acBuffer[n] = ' ';
			nLength++;
		}
	}
	
//	public void setAbsoluteValueRightPadded(long lValue, int nRequiredLength)
//	{
//		nStartPos = NB_MAXI_DIGIT+1;
//		nLength = 0;		
//		checkBuffer(nRequiredLength + NB_MAXI_DIGIT);
//		
//		do
//		{
//			nStartPos--;
//			int nDigit = (int)(lValue % 10); 
//			lValue /= 10; 
//			char cDigit = (char)('0' + nDigit);
//			acBuffer[nStartPos] = cDigit; 			
//			nLength++;
//		}
//		while(lValue != 0);
//		
//		// Pad on right with spaces
//		int n = NB_MAXI_DIGIT+1;
//		while(nLength < nRequiredLength)
//		{
//			acBuffer[n] = ' ';
//			nLength++;
//		}
//	}
		
	public void valueOf(int nValue)
	{
		int nOffset = 0;
		boolean isnegative = false;
		if(nValue < 0)
		{
			nValue = -nValue;
			isnegative = true;
			nOffset = 1;
		}
		
		nStartPos = NB_MAXI_DIGIT+1+nOffset;
		nLength = 0;
		checkBuffer(40);
		do
		{
			nStartPos--;
			int nDigit = nValue % 10;
			nValue /= 10;
			char digit = (char)('0' + nDigit);
			acBuffer[nStartPos+nOffset] = digit;
			nLength++;
		}
		while(nValue != 0);
		
		if(isnegative)
		{
			acBuffer[nStartPos] = '-';
			nLength++;
		}			
	}
	
	public void valueOf(long lValue)
	{
		int nOffset = 0;
		boolean isnegative = false;
		if(lValue < 0)
		{
			lValue = -lValue;
			isnegative = true;
			nOffset = 1;
		}
		
		nStartPos = NB_MAXI_DIGIT+1+nOffset;
		nLength = 0;
		checkBuffer(40);
		do
		{
			nStartPos--;
			int nDigit = (int)(lValue % 10);
			lValue /= 10;
			char digit = (char)('0' + nDigit);
			acBuffer[nStartPos+nOffset] = digit;
			nLength++;
		}
		while(lValue != 0);
		
		if(isnegative)
		{
			acBuffer[nStartPos] = '-';
			nLength++;
		}
	}
	
	public void decodeComp3String(CStr s, int nNbDigitInteger)
	{
		nStartPos = 0;
		nLength = 0;
		checkBuffer(40);
		
		boolean isevenNumberOfDigits = false;
		if((nNbDigitInteger % 2) == 0)
			isevenNumberOfDigits = true;
		
		int nChar = 0;
		int nBibble = 0;
		char c = 0;
		int nLg = s.length();
		for(int nIndex=0; nIndex<nLg; nIndex++)
		{
			nChar = s.charAt(nIndex);
			nBibble = nChar / 16;
			c = (char)(nBibble + '0');
			append(c);
	
			if(nIndex == nLg-1)	// No sign in right nibble
			{
				nBibble = nChar % 16;
			}
			else
			{
				nBibble = nChar % 16;
				c = (char)(nBibble + '0');
				append(c);  
			}
		}
		if(isevenNumberOfDigits)
		{
			// Remove leading 0 that was there as a placeholder due to the even number of digits + sign -> implies an odd number of nibbles; the leading compensated that odd number
			removeLeft(1);
		}
	}
	
	public void decodeSignComp3String(CStr s, int nNbDigitInteger)
	{
		nStartPos = 0;
		nLength = 0;
		checkBuffer(40);
		
		boolean isevenNumberOfDigits = false;
		if((nNbDigitInteger % 2) == 0)
			isevenNumberOfDigits = true;
		
		int nChar = 0;
		int nBibble = 0;
		char c = 0;
		int nLg = s.length();
		for(int nIndex=0; nIndex<nLg; nIndex++)
		{
			nChar = s.charAt(nIndex);
			nBibble = nChar / 16;
			c = (char)(nBibble + '0');
			append(c);
	
			if(nIndex == nLg-1)	// No sign in right nibble
			{
				nBibble = nChar % 16;
				if(nBibble == 12)	// +
					append('+');
				else
					append('-');
			}
			else
			{
				nBibble = nChar % 16;
				c = (char)(nBibble + '0');
				append(c);
			}
		}
		if(isevenNumberOfDigits)
		{
			// Remove leading 0 that was there as a placeholder due to the even number of digits + sign -> implies an odd number of nibbles; the leading compensated that odd number
			removeLeft(1);		
		}
	}
	
	public void getAsAbsoluteIntComp0String(int nValue, int nNbDigitInteger)
	{
		if(nValue < 0)
			nValue = -nValue;
		checkBuffer(40);

		nStartPos = NB_MAXI_DIGIT+1;
		nLength = 0;
		do
		{
			nStartPos--;
			int nDigit = (nValue % 10);
			nValue /= 10;
			char digit = (char)('0' + nDigit);
			acBuffer[nStartPos] = digit;
			nLength++;
		}
		while(nValue != 0 && nLength <= nNbDigitInteger);
		
		while(nLength != nNbDigitInteger)// Padding with '0' on the left
		{
			nStartPos--;
			acBuffer[nStartPos] = '0';
			nLength++;
		}
	}
	
	public void getAsAbsoluteIntComp0StringAsLong(long lValue, int nNbDigitInteger)
	{
		if(lValue < 0)
			lValue = -lValue;
		checkBuffer(40);

		nStartPos = NB_MAXI_DIGIT+1;
		nLength = 0;
		do
		{
			nStartPos--;
			int nDigit = (int)(lValue % 10);
			lValue /= 10;
			char digit = (char)('0' + nDigit);
			acBuffer[nStartPos] = digit;
			nLength++;
		}
		while(lValue != 0 && nLength <= nNbDigitInteger);
		
		while(nLength != nNbDigitInteger)// Padding with '0' on the left
		{
			nStartPos--;
			acBuffer[nStartPos] = '0';
			nLength++;
		}
	}
}
