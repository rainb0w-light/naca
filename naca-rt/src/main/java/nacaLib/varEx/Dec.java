/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 21 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

import nacaLib.tempCache.CStr;
import nacaLib.tempCache.TempCacheLocator;
import jlib.misc.*;


/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Dec extends DecBase
{
	public Dec(Dec dec)
	{
		super();
		lInt = dec.lInt;
		csDec = dec.csDec;
		bPositive = dec.bPositive;
	}
	
	public Dec(long lInt, CStr csDec)
	{
		super();
		setLong(lInt);
		setDecPart(csDec.getAsString());
	}

	public Dec(long lInt, String csDec)
	{
		super(lInt, csDec);
	}
	
	public Dec(String csInt, String csDec)
	{
		super(csInt, csDec);
	}
	
	public void setDecPart(CStr csDec)
	{
		this.csDec = csDec.getAsString();
	}
	
	double getAsDouble()
	{
		String cs = String.valueOf(lInt) + "." + csDec;
		double d = Double.parseDouble(cs);
		if(!bPositive)
			return -d;
		return d;
	}
	
	int getSignedInt()
	{
		if(bPositive)
			return (int)lInt;
		return (int) -lInt;
	}
	
	public long getSignedLong()
	{
		if(bPositive)
			return lInt;
		return -lInt;
	}
	
	int getUnsignedInt()
	{
		return (int)lInt;
	}
	
	String getUnsignedLongAsString()
	{
		String cs = String.valueOf(lInt);
		return cs;
	}

//	String getDecPartAsString()
//	{
//		return getDecPart();
//	}

	String getAsString()
	{
		String cs = "";
		if(isNegative())
			cs = "-";
		long l = getUnsignedLong();
		cs += String.valueOf(l);
		if(!csDec.equals(""))
			cs += "." + csDec;
		return cs;
	}
	
	CStr getAsCStr()
	{
		String s = getAsString();
		CStr cs = TempCacheLocator.getTLSTempCache().getReusableCStr();
		cs.set(s);
		return cs;
	}
	
	public int compare(int n)
	{
		long lThis = getSignedLong();
		long l = n;
		
		if(lThis < l)
			return -1;
		if(lThis == l)
			return 0;
		return 1;
	}
	
	public int compare(long l)
	{
		long lThis = getSignedLong();
		if(lThis < l)
			return -1;
		if(lThis == l)
			return 0;
		return 1;
	}
			
	public int compare(Dec dec2)
	{
		long lSignedInt = getSignedLong();
		long lSignedInt2 = dec2.getSignedLong();
		if(lSignedInt < lSignedInt2)
			return -1;
		if(lSignedInt == lSignedInt2)
		{
			long l1 = getDecAsLong();
			long l2 = dec2.getDecAsLong();
			if(l1 < l2)
				return -1;
			if(l1 == l2)
				return 0;
		}
		return 1;
	}
	
	public long getDecAsLong()
	{
		String csDecPadded = StringUtil.rightPad(this.csDec, 14, '0');
		long l = NumberParser.getAsLong(csDecPadded);
		if(bPositive)
			return l;
		return -l;
	}
	
	public boolean isZero()
	{
		if(lInt == 0 && NumberParser.getAsInt(csDec) == 0)
			return true;
		return false;
	}
	
	@Override
	public String toString()
	{
		return getAsString();
	}
}
