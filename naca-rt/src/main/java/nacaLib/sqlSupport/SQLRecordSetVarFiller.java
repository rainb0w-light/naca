/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 5 juil. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.sqlSupport;

import jlib.misc.ArrayDyn;
import jlib.misc.ArrayFix;
import jlib.misc.ArrayFixDyn;
import nacaLib.varEx.VarBase;

/**
 * @author u930di
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SQLRecordSetVarFiller
{	
	public SQLRecordSetVarFiller()
	{
		recordSetCacheColTypeType = new RecordSetCacheColTypeType();
	}
	
	void apply(CSQLResultSet resultSet)
	{
		if(arrItem != null)
		{
			int nNbItems = arrItem.size();
			for(int n=0; n<nNbItems; n++)
			{
				SQLRecordSetVarFillerItem item = arrItem.get(n);
				item.apply(resultSet, recordSetCacheColTypeType); 
			}
		}
	}
	
	void addLinkColDestination(int nColSource, VarBase varInto, VarBase varIndicator)
	{
		SQLRecordSetVarFillerItem item = new SQLRecordSetVarFillerItem(nColSource, varInto, varIndicator);
		if(arrItem == null)
			arrItem = new ArrayDyn<SQLRecordSetVarFillerItem>();	// new ArrayList<SQLRecordSetVarFillerItem>();
		arrItem.add(item);
	}
	
	void compress()
	{
		if(arrItem.isDyn())
		{
			int nSize = arrItem.size();
			SQLRecordSetVarFillerItem arr[] = new SQLRecordSetVarFillerItem[nSize];
			arrItem.transferInto(arr);
			
			ArrayFix<SQLRecordSetVarFillerItem> arrFix = new ArrayFix<SQLRecordSetVarFillerItem>(arr);
			arrItem = arrFix;	// replace by a fix one (uning less memory)
		}
	}
	
	RecordSetCacheColTypeType getRecordSetCacheColTypeType()
	{
		return recordSetCacheColTypeType;
	}
	
	public int getNbCol()
	{
		return nNbColResultSet;
	}
	
	public void setNbCol(int nNbColResultSet)
	{
		this.nNbColResultSet = nNbColResultSet;
	}
	
	private int nNbColResultSet = 0;
	
	private ArrayFixDyn<SQLRecordSetVarFillerItem> arrItem = null;
	// ArrayFixDynList<SQLRecordSetVarFillerItem> arrItem = null;
	
	private RecordSetCacheColTypeType recordSetCacheColTypeType = null; 
}
