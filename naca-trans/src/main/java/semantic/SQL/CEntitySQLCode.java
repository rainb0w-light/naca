/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jan 10, 2005
 *
 */
package semantic.SQL;

import java.util.Vector;

import parser.expression.CExpression;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CBaseEntityCondition.EConditionType;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntitySQLCode extends CDataEntity
{
	protected CBaseEntityExpression eHistoryItem = null ;
	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public CEntitySQLCode(String name, CObjectCatalog cat)
	{
		super(0, name, cat);
	}
	public CEntitySQLCode(String name, CObjectCatalog cat, CBaseEntityExpression eHistoryItem)
	{
		super(0, name, cat);
		this.eHistoryItem = eHistoryItem ;
	}
	/* (non-Javadoc)
	 * @see semantic.CDataEntity#GetDataType()
	 */
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.CONSTANT ;
	}
	/* (non-Javadoc)
     * @see semantic.CDataEntity#GetSpecialCondition(java.lang.String, semantic.expression.CBaseEntityCondition.ConditionType,
     * semantic.CBaseEntityFactory)
	 */
	public CBaseEntityCondition GetSpecialCondition(int nLine, String value, EConditionType type, CBaseEntityFactory factory)
	{
		if (eHistoryItem!=null) return null;
		int n =0 ;
		try
		{
			if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES")) {
				n = 0 ;
			}
			else
			{
				n = Integer.parseInt(value) ;
			}
		}
		catch (NumberFormatException e)
		{
			return null ;
		}
		if (type == EConditionType.IS_EQUAL)
		{
			CEntityCondIsSQLCode isCode = factory.NewEntityCondIsSQLCode() ;
			isCode.setIsEqual(n) ;
			return isCode ;
		}
		else if (type == EConditionType.IS_DIFFERENT)
		{
			CEntityCondIsSQLCode isCode = factory.NewEntityCondIsSQLCode() ;
			isCode.setIsNotEqual(n) ;
			return isCode ;
		}
		return null ;
	}

	/* (non-Javadoc)
	 * @see semantic.CDataEntity#GetArrayReference(java.util.Vector, semantic.CBaseEntityFactory)
	 */
	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
	{
//		 http://publib.boulder.ibm.com/infocenter/iseries/v5r3/ic2924/index.htm?info/db2/rbafzmstfielddescsqlca.htm
		CExpression expr = (CExpression)v.get(0);
		CBaseEntityExpression exp = expr.AnalyseExpression(factory);
		return factory.NewEntitySQLCode("", exp) ;
	}

	// ==================== ST4 Template Accessors ====================
	// Read-only getters for the recursive ST4 assembler (template
	// recursiveSQLCodeEntity). They expose the already-resolved semantic state
	// (the optional SQL diagnostic history-item expression); formatting the
	// getSQLCode()/getSQLDiagnosticCode(...) reference is done by the template,
	// never here.

	/**
	 * The optional SQL diagnostic history-item expression (the SQLERRD(n) index),
	 * or null for the plain SQLCODE reference. When present, the template renders
	 * getSQLDiagnosticCode(&lt;historyItem&gt;); otherwise getSQLCode().
	 */
	public CBaseEntityExpression getHistoryItem()
	{
		return eHistoryItem ;
	}

	/* (non-Javadoc)
	 * @see semantic.CDataEntity#HasAccessors()
	 */
	public boolean HasAccessors()
	{
		return true;
	}
	/* (non-Javadoc)
	 * @see semantic.CDataEntity#GetConstantValue()
	 */
	public String GetConstantValue()
	{
		return "" ;
	}
	public boolean isValNeeded()
	{
		return false;
	}
}
