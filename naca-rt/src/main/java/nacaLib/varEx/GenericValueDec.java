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



/**
 * @author U930DI
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GenericValueDec extends GenericValue
{
	GenericValueDec(Dec dec)
	{
		dec = dec;
	}
	
	GenericValueDec(int nInt, String csDec)
	{
		dec = new Dec(nInt, csDec);
	}
	
	String getAsRawString()
	{
		String cs = dec.getAsString();
		return cs;
	}
	
	String getAsString()
	{
		return getAsRawString();
	}
	
	int getAsInt()
	{
		return dec.getSignedInt();
	}
	
	int getAsUnsignedInt()
	{
		return dec.getUnsignedInt();
	}

	Dec getAsDec()
	{
		return dec;
	}
		
	Dec getAsUnsignedDec()
	{
		if(dec.isNegative())
		{
			Dec dec = new Dec(this.dec);
			dec.setPositive(true);
			return dec;
		}
		return dec;
	}
	
	
	double getAsDouble()
	{
		return dec.getAsDouble(); 
	}	

	
	private Dec dec = null;
}
