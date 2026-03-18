/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 19 mars 2005
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
public class GenericValueInt extends GenericValue
{
	GenericValueInt(int n)
	{
		n = n;
	}
	
	String getAsRawString()
	{
		return String.valueOf(n);
	}
	
	String getAsString()
	{
		return String.valueOf(n);
	}

	
	int getAsInt()
	{
		return n;
	}
	
	int getAsUnsignedInt()
	{
		if(n < 0)
			return -n;
		return n;
	}
	
	Dec getAsDec()
	{
		Dec dec = new Dec(n, "");
		return dec;
	}
	
	Dec getAsUnsignedDec()
	{
		Dec dec = getAsDec();
		dec.setPositive(true);
		return dec;
	}
	
	double getAsDouble()
	{
		return n;
	}	
	
	private int n;
}
