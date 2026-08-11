/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author PJD
 *
 */
public class IncWrapper
{
	IncWrapper()
	{
		n = 0;
	}

	void inc()
	{
		n++;
	}

	int get()
	{
		return n;
	}

	private int n;
}
