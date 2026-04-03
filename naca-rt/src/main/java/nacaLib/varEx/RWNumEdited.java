/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 7 avr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

import jlib.misc.StringUtil;

/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */



public class RWNumEdited
{
	static boolean isMaskAllZeroSuppression(String csFormat)
	{
		for(int n=0; n<csFormat.length(); n++)
		{
			char c = csFormat.charAt(n);
			if(!(c == 'Z' || c == '.' || c == 'V' || c == ',' || c == '*' || c == '+' || c == '-' || c == 'C' || c == 'R' || c == 'D' || c == 'B'))
				return false;
		}
		return true;
	}
	
	static String internalFormatAndWrite(Dec dec, String csFormat, boolean bBlankWhenZero)
	{
		boolean issignFilled = false;
		if(csFormat == null)
			return "";
		
		int nLgFormat = csFormat.length();
		if(dec.isZero())
		{
			if(isMaskAllZeroSuppression(csFormat))
			{
				return StringUtil.fillString(' ', nLgFormat);
			}
		}
		
		if(nLgFormat == 0)
			return "";
			
		if (bBlankWhenZero && dec.isZero())
		{
			return StringUtil.fillString(' ', nLgFormat);
		}
		
		StringBuffer sDest = new StringBuffer(nLgFormat);
		sDest.setLength(nLgFormat);
		
		String sourceInt = dec.getUnsignedLongAsString();	// varNumberChunk.getAbsIntAsString();
		int nPosSource = sourceInt.length() - 1;
		
		boolean isdoDecPart = false;

		int nDecimalSeparatorFormatPos = Math.max(csFormat.indexOf('.'), csFormat.indexOf('V'));
		if(nDecimalSeparatorFormatPos == -1)	// dot (special insertion char) in format, then we will have a decimal part
			nDecimalSeparatorFormatPos = nLgFormat-1;
		else  
			isdoDecPart = true;
		
		int nPos$ = csFormat.indexOf('$');
		if(nPos$ == -1)
			nPos$ = csFormat.indexOf('\u00A3');
		
		// Integer part
		boolean issuppressLeading0 = false;
		for(int nFormatIndex=nDecimalSeparatorFormatPos; nFormatIndex>=0; nFormatIndex--)	// From right to left for integer part
		{
			char source = getDigitAtPosition(sourceInt, nPosSource);
			char format = csFormat.charAt(nFormatIndex);
			if(format == '9')	// Keep char at the source index current position
			{					
				sDest.setCharAt(nFormatIndex, source);
				nPosSource--;					
			}
			else if(format == 'B')
				sDest.setCharAt(nFormatIndex, ' ');
			else if(format == ' ')
				sDest.setCharAt(nFormatIndex, ' ');
			else if(format == '0' || format == '/' || format == ',' || format == '\'')	// Warning, ',' stands for 1000 separator, not decimal dot !!!
			{
				if(format == '\'')
					sDest.setCharAt(nFormatIndex, ',');
				else
					sDest.setCharAt(nFormatIndex, format);
			}
			else if(format == '$' || format == '\u00A3')
			{
				issuppressLeading0 = true;
				sDest.setCharAt(nFormatIndex, source);
				nPosSource--;	
			}
			else if(format == '+' || format == '-')
			{
				if(nFormatIndex == nDecimalSeparatorFormatPos)	// Last char mask is sign
				{
					issignFilled = true;
					if(format == '+')
					{
					 	if(dec.isNegative())
					 		sDest.setCharAt(nLgFormat-1, '-');
						else
							sDest.setCharAt(nLgFormat-1, '+');
					}
					else if(format == '-')
					{
					 	if(dec.isNegative())
					 		sDest.setCharAt(nLgFormat-1, '-');
						else
							sDest.setCharAt(nLgFormat-1, ' ');
					}
				 	nFormatIndex--;

					issuppressLeading0 = true;
					sDest.setCharAt(nFormatIndex, source);
				}
				else	// leading - ou +
				{
					sDest.setCharAt(nFormatIndex, source);
				}
				nPosSource--;
			}
			else if(format == 'Z' || format == '*')
			{
				issuppressLeading0 = true;
				sDest.setCharAt(nFormatIndex, source);	// 1st pass: recopy the source char; it will be suppressed in next pass if needed
				nPosSource--;
			}				
		}
		
		
		char format = ' ';
		
		int nPosLastSuppress = -1;
		for(int nChar = 0; nChar<nLgFormat && issuppressLeading0; nChar++)
		{
			char source = sDest.charAt(nChar);
			format = csFormat.charAt(nChar);
			if(source == '0')
			{
				if(format == 'Z' || format == '$' || format == '\u00A3' || format == '-' || format == '+')
				{
					sDest.setCharAt(nChar, ' ');
					nPosLastSuppress = nChar;
				}				 
				else if(format == '*')
					sDest.setCharAt(nChar, '*');
			}
			else if(source == ' ' || source == '$' || source == '\u00A3')
			{
			}
			else if(source == '.' || source == ',')	// Only leading 0; replace the , or . by the suppressing char
			{
				if(format == '.' || format == ',')
				{
					if(nChar > 0)
					{
						char previous = sDest.charAt(nChar-1);
						if(previous == ' ')	// we have a previous space
						{
							sDest.setCharAt(nChar, ' ');	// remove comma
							nPosLastSuppress = nChar;
						}
						if(previous == '*')	// we have a previous star
							sDest.setCharAt(nChar, '*');	// remove comma
					}
				}
			}
			else	// Not a leading 0 anymore
			{
				issuppressLeading0 = false;
			}
		}

		if(nPosLastSuppress != -1)
		{
			if(nPos$ != -1)
			{
				char money = csFormat.charAt(nPos$);
				sDest.setCharAt(nPosLastSuppress, money);	// set the money sign
			}
		}
		else if(nPos$ != -1)	// special case where there is no place left for the money sign, but we must set it insted of the forst digit
		{
			char money = csFormat.charAt(nPos$);
			sDest.setCharAt(0, money);	// set the money sign
		}
		
		if(isdoDecPart)	// Fill the decimal part
		{		
			// Second part: Decimal
			String sSourceDecPart = dec.getDecPart();	// String sSourceDecPart = varNumberChunk.getDecString();
			nPosSource = 0;	// Left to right
			for(int nFormatIndex=nDecimalSeparatorFormatPos; nFormatIndex<nLgFormat; nFormatIndex++)	// From left to right for dec part
			{
				format = csFormat.charAt(nFormatIndex);
	
				if(format == '9')	// Keep char at the source index current position
				{
					char source = getDigitAtPosition(sSourceDecPart, nPosSource);
					sDest.setCharAt(nFormatIndex, source);
					nPosSource++;
				}
				else if(format == '.' || format == 'V')	// Insert dot
					sDest.setCharAt(nFormatIndex, '.');
				else if(format == 'B')	// Insert char
					sDest.setCharAt(nFormatIndex, ' ');	
				else if(format == '0' || format == '/' || format == ',')
					sDest.setCharAt(nFormatIndex, format);
				else if(format == 'Z' || format == '*')
				{				
					char source = getDigitAtPosition(sSourceDecPart, nPosSource);
					if(source == '0' && format == '*')
						sDest.setCharAt(nFormatIndex, '*');
					else
						sDest.setCharAt(nFormatIndex, source);
					nPosSource++;
				}
			}				
		}
		
		if(!issignFilled)
		{
			try
			{
				// Fill sign
				format = csFormat.charAt(nLgFormat-1);	// Last char is the sign
				if(format == '+')
				{
					// PJD commented updated because the sign erased the last digit
	//				sDest = sDest.deleteCharAt(nLgFormat-1);	// Delete first char to have the place to set the sign at the last position
	//				if(dec.isNegative())	//	if(varNumberChunk.isNegative())
	//					sDest.append('-');
	//				else
	//					sDest.append('+');
				 	if(dec.isNegative())
				 		sDest.setCharAt(nLgFormat-1, '-');
					else
						sDest.setCharAt(nLgFormat-1, '+');
				}
				else if(format == '-')
				{
					// PJD commented updated because the sign erased the last digit
	//				sDest = sDest.deleteCharAt(nLgFormat-1);	// Delete first char to have the place to set the sign at the last position
	//			 	if(dec.isNegative())	//	if(varNumberChunk.isNegative())
	//					sDest.append('-');
	//				else
	//					sDest.append(' ');
					// PJD: Added
				 	if(dec.isNegative())
				 		sDest.setCharAt(nLgFormat-1, '-');
					else
						sDest.setCharAt(nLgFormat-1, ' ');
				}
				else	// Maybe sign at the begining
				{
					format = csFormat.charAt(0);	// first char is the sign
					if(format == '+' || format == '-')
					{	
						int nPosLastSpace = getLastSpacePosition(sDest.toString(), csFormat);
						if(nPosLastSpace == -1)
							nPosLastSpace = 0;
						if(nPosLastSpace >= 0)
						{
							if(format == '+')
							{				
								if(dec.isNegative())		// if(varNumberChunk.isNegative())
									sDest.setCharAt(nPosLastSpace, '-');
								else
									sDest.setCharAt(nPosLastSpace, '+');
							}
							else if(format == '-')
							{
							 	if(dec.isNegative())	//if(varNumberChunk.isNegative())
									sDest.setCharAt(nPosLastSpace, '-');
								else
									sDest.setCharAt(nPosLastSpace, ' ');
							}
						}
					}
				}		
				char lastFormat;
				// Clean leading - or + or $
				for(int n=0; n<nLgFormat;n++)
				{
					lastFormat = format;
					format = csFormat.charAt(n);
					
					if(format == '-' || format == '+' || format == ',')
					{
						char digit = sDest.charAt(n);
						if(digit == '0' || digit == ',' || digit == '-' || digit == '+' || digit == ' ')
						{
							if(format == ',' && n > 0 && (lastFormat == '+' || lastFormat == '-'))
								format = lastFormat;

							if(format == '+')
							{				
								if(dec.isNegative())		// if(varNumberChunk.isNegative())
									sDest.setCharAt(n, '-');
								else
									sDest.setCharAt(n, '+');
							}
							else if(format == '-')
							{
							 	if(dec.isNegative())	//if(varNumberChunk.isNegative())
									sDest.setCharAt(n, '-');
								else
									sDest.setCharAt(n, ' ');
							}
							if(n > 0)
							{
								char precDigit = sDest.charAt(n-1);
								if(precDigit == '+' || precDigit == '-' || precDigit == ',' || precDigit == ' ')
									sDest.setCharAt(n-1, ' ');
							}
						}
						else
						{
//							char cPrecDigit = sDest.charAt(n-1);
//							if(cPrecDigit == '+' || cPrecDigit == '-' || cPrecDigit == ',' || cPrecDigit == ' ')
//								sDest.setCharAt(n-1, ' ');
							break;
						}
					}
					else
					{
						break;
					}
				}
			}
			catch (Exception e)
			{
			}
		}
		
		String cs = sDest.toString();
		return cs;
	}
	
	static private int getLastSpacePosition(String sDest, String sFormat)
	{
		int nLg = sDest.length();
		for(int n=nLg-1; n>=0; n--)
		{
			char c = sDest.charAt(n);
			char format = sFormat.charAt(n);
			if(c == ' ' && (format == '-' || format == '+'))
			{
				if (n < nLg-1 && sFormat.charAt(n+1) == ',' && sDest.charAt(n+1) == ' ')
				{
					// sign before separator thousand empty
					return n+1;
				}
				return n;
			}
		}
		return -1;
	}
	
	static private char getDigitAtPosition(String csSourceDecPart, int nPosSource)
	{
		if(nPosSource >= 0 && nPosSource < csSourceDecPart.length())
			return csSourceDecPart.charAt(nPosSource);
		return '0';
	}
}
