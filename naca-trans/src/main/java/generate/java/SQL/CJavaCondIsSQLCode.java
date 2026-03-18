/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 14 janv. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.SQL;

import semantic.SQL.CEntityCondIsSQLCode;
import semantic.expression.CBaseEntityCondition;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCondIsSQLCode extends CEntityCondIsSQLCode
{

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetOppositeCondition()
	 */
	public CBaseEntityCondition GetOppositeCondition()
	{
		CJavaCondIsSQLCode cond = new CJavaCondIsSQLCode() ;
		cond.bIsEqual = ! bIsEqual ;
		cond.nValue = nValue ;
		return cond ;
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#Export()
	 */
	public String Export()
	{
		String value = "" ;
		if (nValue == 0)
		{
			value = "SQLCode.SQL_OK" ;
		}
		else if (nValue == 100)
		{
			value = "SQLCode.SQL_NOT_FOUND" ;
		}
		else if (nValue == -811)
		{
			value = "SQLCode.SQL_MORE_THAN_ONE_ROW" ;
		}
		else if (nValue == -803)
		{
			value = "SQLCode.SQL_DUPLICATE_INDEX_KEY" ;
		}
		else if (nValue == -502)
		{
			value = "SQLCode.SQL_CURSOR_ALREADY_OPENED" ;
		}
		else if (nValue == -501)
		{
			value = "SQLCode.SQL_CURSOR_NOT_OPEN" ;
		}
		else if (nValue == -305)
		{
			value = "SQLCode.SQL_VALUE_NULL" ;
		}
		else
		{
			value += nValue ;
		}
		if (bIsEqual)
		{
			String cs = "isSQLCode(" + value + ")" ;
			return cs ;
		}
		else
		{
			String cs = "isNotSQLCode(" + value + ")" ;
			return cs ;
		}
	}

}
