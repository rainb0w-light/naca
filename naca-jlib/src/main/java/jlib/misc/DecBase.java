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

import java.math.BigDecimal;


/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: DecBase.java,v 1.4 2007/01/23 08:27:08 u930bm Exp $
 */
public class DecBase
{
	protected String csDec;
	protected long lInt;
	protected boolean bPositive;
	
	public DecBase()
	{
	}
	
	public DecBase(String csInt, String csDec)
	{
		long lInt = NumberParser.getAsLong(csInt);
		setLong(lInt);
		this.csDec = csDec;
	}
	
	public DecBase(long lInt, String csDec)
	{
		setLong(lInt);
		setDecPart(csDec);
	}
	
	public void setLong(long lInt)
	{
		if(lInt >= 0)
		{
			this.lInt = lInt;
			bPositive = true;
		}
		else
		{
			this.lInt = -lInt;
			bPositive = false;
		}
	}
	
	public void setPositive(boolean bPositive)
	{
		this.bPositive = bPositive;
	}

	public void setNegativeForced()
	{
		bPositive = false;
	}

	public boolean isNegative()
	{
		return !bPositive;
	}

	public boolean isPositive()
	{
		return bPositive;
	}

	public void setUnsigned()
	{
		bPositive = true;
	}

	public void setDecPart(String csDec)
	{
		this.csDec = csDec;
	}
	
	public long getUnsignedLong()
	{
		return lInt;
	}
	
	public long getSignedLong()
	{
		if(bPositive)
			return lInt;
		return -lInt;
	}

	
	public String getDecPart()
	{
		return csDec;
	}
	
	public int getLeftMostDigitOfDecPartAsInt(int nNbDigits)
	{		
		if(csDec.length() <= 0)
			return 0;
		
		int n=0;
		int nDec = 0;
		int nDecLength = csDec.length();
		while(n < nDecLength && n < nNbDigits)
		{
			nDec *= 10;
			char c = csDec.charAt(n);
			nDec += c - '0';
			n++;
		}
		while (n < nNbDigits)
		{
			nDec *= 10;
			n++;
		}
		return nDec;
	}
	
	public static DecBase toDec(BigDecimal bd)
	{
		boolean ispositive = true;

		if(bd.signum() < 0)
			ispositive = false;
			
		String sValue = bd.abs().unscaledValue().toString();
		int nScale = bd.scale();
		if(sValue.length() > nScale)
		{
			String sInt = sValue.substring(0, sValue.length()-nScale);
			String sDec = sValue.substring(sValue.length()-nScale);
			DecBase dec = new DecBase(sInt, sDec);
			dec.setPositive(ispositive);
			return dec;
		}
		else
		{
			String sDec = new String();
			int nNbLeadingZeros = nScale - sValue.length();
			for(int n=0; n<nNbLeadingZeros; n++)
			{
				sDec = sDec + "0";
			}
			sDec = sDec + sValue;
			
			DecBase dec = new DecBase(0, sDec); 
			dec.setPositive(ispositive);
			return dec;
		}
	}
	
	public String toString()
	{
		String cs;
		if(isNegative())
			cs = "Negative; Int=";
		else
			cs = "Positive; Int=";
		cs += lInt;
		cs += "; Decimal="+csDec;
		return cs;
	}
}
