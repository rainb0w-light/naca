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
 * @version $Id: Comp3Support.java,v 1.2 2007/01/09 14:05:57 u930di Exp $
 */
public class Comp3Support
{
	public static String encodeDecComp3(DecBase decValue, int nNbDigitInteger, int nNbDigitDecimal)
	{
		long lInt = decValue.getUnsignedLong();
		
		String absIntValue = String.valueOf(lInt);
		String sDecValue = decValue.getDecPart();
		
		String s = new String();

		int nNbTotalDigit = nNbDigitInteger + nNbDigitDecimal;
		if((nNbTotalDigit % 2) == 0)
			s = "0";	// Left most 0 to compensate empty leftmost nibble 
		int nStringLength = absIntValue.length();
		while(nStringLength < nNbDigitInteger)
		{
			s = s + '0';
			nStringLength++;
		}
		if(nStringLength > nNbDigitInteger)	// Keeping only rightmostchar form source string
			absIntValue = absIntValue.substring(nStringLength - nNbDigitInteger);
		s = s + absIntValue;
		
		String dec = null;
		nStringLength = sDecValue.length();

		if(nStringLength > nNbDigitDecimal)
			dec = sDecValue.substring(0, nNbDigitDecimal);
		else if(nStringLength == nNbDigitDecimal)
			dec = sDecValue;
		else
		{
			dec = sDecValue;
			while(nStringLength < nNbDigitDecimal)
			{
				dec = dec + '0';
				nStringLength++;
			}						
		}
		if(dec != null && dec.length() != 0)
			s = s + dec;
		return s;
	}	
	
	public static void internalWriteEncodeComp3(byte aBytes[], String cs, boolean bPositive, boolean bSigned)
	{
		int nStringLength = cs.length();
		int n = 0;
		int nByteDest = 0;
		
		char high = 0;
		int nHigh = 0;
		char low = 0;
		int nLow = 0;			
		while(n < nStringLength)
		{
			high = cs.charAt(n);
			nHigh = high - '0';
			n++;
			
			if(n == nStringLength)	// No more digit, but the sign
			{
				if(bSigned)
				{
					if(bPositive)
						nLow = 12;	// C is encoded sign for +
					else
						nLow = 13;	// D is encoded sign for -
				}
				else
					nLow = 15;	// F is encoded sign for usigned
			}
			else
			{
				low = cs.charAt(n);
				nLow = low - '0';
			}

			int nChar = (nHigh * 16) + nLow;
			byte by = (byte)nChar;  
			aBytes[nByteDest] = by;
						 
			n++;
			nByteDest++;
		}
	}
}
