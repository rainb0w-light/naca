/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;


/**
 * @author U930DI
 *
 */
public abstract class GenericValue
{
	GenericValue()
	{
	}

	abstract String getAsString();
	abstract String getAsRawString();
	abstract int getAsInt();
	abstract int getAsUnsignedInt();
	abstract Dec getAsDec();
	abstract Dec getAsUnsignedDec();
	abstract double getAsDouble();
}
