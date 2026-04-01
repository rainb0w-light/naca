/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
package nacaLib.sqlSupport;

import nacaLib.varEx.VarBase;


public class SQLRecordSetVarFillerItem
{
	SQLRecordSetVarFillerItem(int nColSource, VarBase varInto, VarBase varIndicator)
	{
		this.nColSource = nColSource;
		this.varInto = varInto;
		this.varIndicator = varIndicator;
	}
	
	void apply(CSQLResultSet resultSet)
	{
		String csValue = resultSet.getColValueAsString(nColSource+1, varInto);
		if(varInto != null)
			varInto.set(csValue);
		//System.out.println("SQLRecordSetVarFillerItem::apply varInto="+varInto.toString());
	
		if(varIndicator != null)
		{
			if(resultSet.bNull)
				varIndicator.set(-1);	// The col is SQL NULL
			else
				varIndicator.set(0);	// The col is not sql null
		}
		if (resultSet.bNull && varIndicator == null)
		{
			resultSet.bNullError = true;
		}
	}
	
	int nColSource = 0;
	VarBase varInto = null;
	VarBase varIndicator = null;
}

*/
package nacaLib.sqlSupport;

import nacaLib.varEx.VarBase;

public class SQLRecordSetVarFillerItem
{
	SQLRecordSetVarFillerItem(int nColSource, VarBase varInto, VarBase varIndicator)
	{
		this.nColSource = nColSource;
		this.varInto = varInto;
		this.varIndicator = varIndicator;
	}
	
	void apply(CSQLResultSet resultSet, RecordSetCacheColTypeType recordSetCacheColTypeType)
	{
		if(varInto != null)
		{
			boolean isnull = resultSet.fillColValue(nColSource, varInto, recordSetCacheColTypeType);
			if(varIndicator == null)
			{
				if(!isnull)
					return ;
				resultSet.bNullError = true;
				return;
			}
			if(isnull)
				varIndicator.varDef.write(varIndicator.bufferPos, -1);	//set(-1);	// The col is SQL NULL
			else
				varIndicator.varDef.write(varIndicator.bufferPos, 0);	//varIndicator.set(0);	// The col is not sql null
		}
	}
	
//	void apply(CSQLResultSet resultSet)
//	{
//		String csValue = resultSet.getColValueAsString(nColSource+1, varInto);
//		if(varInto != null)
//			varInto.set(csValue);
//	
//		if(varIndicator != null)
//		{
//			if(resultSet.bNull)
//				varIndicator.set(-1);	// The col is SQL NULL
//			else
//				varIndicator.set(0);	// The col is not sql null
//		}
//		if (resultSet.bNull && varIndicator == null)
//		{
//			resultSet.bNullError = true;
//		}
//	}


	
	private int nColSource = 0;
	private VarBase varInto = null;
	private VarBase varIndicator = null;
}
