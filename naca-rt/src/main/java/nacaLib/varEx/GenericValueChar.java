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
public class GenericValueChar extends GenericValue
{
	GenericValueChar(char c)
	{
		c = c;
	}
	
	String getAsRawString()
	{
		return String.valueOf(c);
	}
	
	String getAsString()
	{
		return String.valueOf(c);
	}
	
	int getAsInt()
	{
		return c;
	}
	
	int getAsUnsignedInt()
	{
		return Math.abs(c);
	}
	
	Dec getAsDec()
	{
		Dec dec = new Dec(c, "");
		return dec;
	}
	
	Dec getAsUnsignedDec()
	{
		Dec dec = new Dec(c, "");
		dec.setUnsigned();
		return dec;
	}
	
	
	double getAsDouble()
	{
		return c;
	}	
	
	private char c;
}
