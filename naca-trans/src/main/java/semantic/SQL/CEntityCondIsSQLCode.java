/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.SQL;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CUnitaryEntityCondition;

/**
 * @author sly
 *
 */
public class CEntityCondIsSQLCode extends CUnitaryEntityCondition
{
	protected boolean isisEqual = true ;
	protected int nValue = 0 ;

	public void setIsEqual(int n)
	{
		isisEqual = true ;
		nValue = n ;
	}
	public void setIsNotEqual(int n)
	{
		isisEqual = false ;
		nValue = n ;
	}


	public int GetPriorityLevel()
	{
		return 7;
	}

	public CBaseEntityCondition GetOppositeCondition()
	{
		CEntityCondIsSQLCode cond = new CEntityCondIsSQLCode() ;
		cond.isisEqual = !isisEqual ;
		cond.nValue = nValue ;
		return cond ;
	}

	/* (non-Javadoc)
	 * @see semantic.expression.CBaseEntityCondition#GetSpecialCondition(java.lang.String, semantic.CBaseEntityFactory)
	 */
	public CBaseEntityCondition GetSpecialConditionReplacing(String val, CBaseEntityFactory fact, CDataEntity replace)
	{
		// nothing
		return null;
	}

	public boolean ignore()
	{
		return false;
	}
	public boolean isBinaryCondition()
	{
		return true;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveCondIsSQLCodeEntity). They expose the already-resolved semantic
	// state (which SQLCODE value is tested, and whether the test is negated);
	// formatting the runtime condition call is done by the template, never here.

	/** True when the condition is negated (SQL-CODE <> n): renders isNotSQLCode. */
	public boolean isOpposite()
	{
		return !isisEqual ;
	}

	/**
	 * The DB2 SQLCODE constant name for well-known codes (SQL_OK, SQL_NOT_FOUND,
	 * SQL_MORE_THAN_ONE_ROW, SQL_DUPLICATE_INDEX_KEY, SQL_CURSOR_ALREADY_OPENED,
	 * SQL_CURSOR_NOT_OPEN, SQL_VALUE_NULL), or null for any other code, which the
	 * template renders as a raw numeric literal.
	 */
	public String getSqlCodeConstantName()
	{
		switch (nValue)
		{
			case 0:
				return "SQL_OK" ;
			case 100:
				return "SQL_NOT_FOUND" ;
			case -811:
				return "SQL_MORE_THAN_ONE_ROW" ;
			case -803:
				return "SQL_DUPLICATE_INDEX_KEY" ;
			case -502:
				return "SQL_CURSOR_ALREADY_OPENED" ;
			case -501:
				return "SQL_CURSOR_NOT_OPEN" ;
			case -305:
				return "SQL_VALUE_NULL" ;
			default:
				return null ;
		}
	}

	/** The raw tested SQLCODE value (rendered as a literal for unmapped codes). */
	public int getSqlCodeNumber()
	{
		return nValue ;
	}
}
