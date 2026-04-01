/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on Oct 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.CESM;

import nacaLib.base.*;
import nacaLib.varEx.*;

public class CESMWriteQueue extends CJMapObject
{
	protected boolean istransient = false ;
	protected String name = "" ;
	protected CESMQueueManager manager = null;
	protected int nItemPosition = 0 ;
	
	public CESMWriteQueue(boolean istransient, String name, CESMQueueManager manager)
	{
		istransient = istransient;
		name = name ;
		manager = manager ;
	}
	
	public CESMWriteQueue from(Var varSource, Var tsLong)
	{
		return from(varSource, tsLong.getInt());
	}
	public CESMWriteQueue from(Var varSource, int tsLong)
	{
		if (tsLong > varSource.getLength())
		{
			tsLong = varSource.getLength();
		}
		InternalCharBuffer charBufferCopy = varSource.exportToCharBuffer(tsLong);
		if (manager != null)
		{
			if (isrewrite)
			{
				manager.writeTempQueue(name, charBufferCopy, nItemPosition - 1) ;
			}
			else
			{
				nItemPosition = manager.writeTempQueue(name, charBufferCopy) ;
			}
		}
		return this;
	}
	
	public CESMWriteQueue from(Var varSource)
	{
		InternalCharBuffer charBufferCopy = varSource.exportToCharBuffer();
		if (manager != null)
		{
			if (isrewrite)
			{
				manager.writeTempQueue(name, charBufferCopy, nItemPosition - 1) ;
			}
			else
			{
				nItemPosition = manager.writeTempQueue(name, charBufferCopy) ;
			}
		}
		return this;
	}

	public CESMWriteQueue item(Var tsItem)
	{
		tsItem.set(nItemPosition) ;
		return this ;
	}

//	public CESMWriteQueue main()
//	{
//		// unsupported
//		return this ;
//	}

	public CESMWriteQueue rewrite(int item)
	{
		isrewrite = true ;
		nItemPosition = item ;
		return this ;
	}
	protected boolean isrewrite = false ;
	/**
	 * 
	 */
//	public CESMWriteQueue auxiliary()
//	{
//		return this ;
//	}
}


