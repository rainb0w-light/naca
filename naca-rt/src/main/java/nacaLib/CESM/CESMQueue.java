/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.CESM;

import java.util.Queue;
import java.util.concurrent.SynchronousQueue;

import nacaLib.base.CJMapObject;
import nacaLib.varEx.Var;

/**
 * @author U930CV
 *
 */
public class CESMQueue extends CJMapObject
{
	protected Queue queue = new SynchronousQueue();
	protected int nbElements = 0;
	protected int nbLastRead = 0 ;

	public int Add(Var data)
	{
		queue.add(data);
		nbElements ++ ;
		return nbElements ;
	}

	public int length()
	{
		return nbElements ;
	}

	public int read(Var data)
	{
		if (nbLastRead < nbElements)
		{
			Var e = (Var)queue.remove() ;
			data.set(e) ;
			nbLastRead ++ ;
			return nbLastRead ;
		}
		return 0;
	}
}
