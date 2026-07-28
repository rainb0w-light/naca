/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Oct 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;

import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntitySQLCursor extends CDataEntity
{
	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public CEntitySQLCursor(String name, CObjectCatalog cat)
	{
		super(0, name, cat);
		programCatalog.RegisterSQLCursor(this);
		if (!name.equals(this.GetName()))
		{
			programCatalog.RegisterSQLCursor(name, this);
		}
	}

	public boolean HasAccessors()
	{
		return false;
	}
	public String ExportWriteAccessorTo(String value)
	{
		return "" ;
	}

	// The pure semantic cursor entity is terminal: the retired CJavaSQLCursor
	// direct backend's behaviors live below as read-only implementations. Stage
	// 2 renders it through the recursive ST4 assembler via the dataReferenceEntity
	// reference binding (semantic.SQL.CEntitySQLCursor in
	// semantic-runtime-bindings.properties), which reads only entity.* properties.

	@Override
	public CDataEntityType GetDataType()
	{
		return null;
	}

	@Override
	public boolean isValNeeded()
	{
		return false;
	}

	/**
	 * Legacy direct-path compatibility value: the formatted Java identifier of
	 * the cursor name (byte-for-byte what the retired CJavaSQLCursor emitted).
	 * The recursive ST4 assembler never calls this — templates read
	 * {@code <entity.formattedName>} through the dataReferenceEntity binding.
	 */
	@Override
	public String ExportReference(int nLine)
	{
		return FormatIdentifier(GetName());
	}

	public void SetSelect(CEntitySQLCursorSelectStatement eSQL)
	{
		select = eSQL ;
	}
	
	protected CEntitySQLCursorSelectStatement select = null ;
	protected CDataEntity variableStatement = null ;

	public int GetNbColumns()
	{
		if (select == null)
			return 0 ;
		return select.GetNbColumns() ;
	}
	public boolean ignore()
	{
		return false ;
	}
	public String GetConstantValue()
	{
		return "" ;
	}

	/**
	 * 
	 */
	public CEntitySQLCursorSelectStatement getSelect()
	{
		return select ;
	}

	/**
	 * @param var
	 */
	public void setVariableStatement(CDataEntity var)
	{
		variableStatement = var ;
	}
	public CDataEntity getVariableStatement()
	{
		return variableStatement ;
	}

}
