/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author u930di
 *
 */
public class CoupleIdValue
{
	CoupleIdValue(int nId, String csValue)
	{
		this.nId = nId;
		this.csValue = csValue;
	}

	public int nId;
	public String csValue;
}
