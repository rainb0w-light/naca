/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.jmxMBean;

public class CheckUpAndRunning implements CheckUpAndRunningMBean
{
	public boolean isUp()
	{
		if(isinc)
			nbUp++;
		else
			nbUp--;
		return true;
	}
	
	public int getNbCheckUp()
	{
		return nbUp;
	}

	public boolean getInc()
	{
		return isinc;
	}
	
	public void setInc(boolean b)
	{
		isinc = b;
	}

	private int nbUp = 0;
	private boolean isinc = true;
}


