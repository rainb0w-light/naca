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
import nacaLib.varEx.Var;

public class CESMReadQueue extends CJMapObject
{
	protected boolean istransient = false ;
	protected String name = "" ;
	protected CESMQueueManager manager = null;
	protected int nRecordPosition = 0;

	public CESMReadQueue(boolean bTransient, String name, CESMQueueManager manager)
	{		
		bTransient = bTransient ;
		name = name ;
		manager = manager ;
	}
	
	public CESMReadQueue nextInto(Var tsZone, Var tsLong)
	{
		manager.readNextTempQueue(name, tsZone) ;
		return this;
	}

	public CESMReadQueue nextInto(Var tsZone)
	{
		manager.readNextTempQueue(name, tsZone) ;
		return this;
	}
	
	public CESMReadQueue itemInto(int nIndex, Var varItem)	
	{
		manager.readIndexedTempQueue(name, nIndex, varItem, null) ;
		return this ;
	}
	
	public CESMReadQueue itemInto(Var varIndex, Var varItem)	
	{
		int nIndex = varIndex.getInt();
		manager.readIndexedTempQueue(name, nIndex, varItem, null) ;
		return this ;
	}
	public CESMReadQueue itemInto(Var varIndex, Var varItem, Var varLength)	
	{
		int nIndex = varIndex.getInt();
		manager.readIndexedTempQueue(name, nIndex, varItem, varLength) ;
		return this ;
	}
	
	public CESMReadQueue numItem(Var varNbItems)	// Get the number of items in the collection
	{
		manager.getNbItems(name, varNbItems);
		return this ;
	}
}

