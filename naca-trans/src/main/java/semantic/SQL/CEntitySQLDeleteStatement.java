/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 août 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.util.Vector;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

public abstract class CEntitySQLDeleteStatement extends CBaseActionEntity
{
	public CEntitySQLDeleteStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out, String csStatement, Vector<CDataEntity> arrParameters)
	{
		super(line, cat, out);
		csStatement = csStatement ;
		arrParameters = arrParameters;
	}
	protected String csStatement = "" ;
	protected Vector<CDataEntity> arrParameters = null;
	public void Clear()
	{
		super.Clear();
		arrParameters.clear();
	}
	public boolean ignore()
	{
		return false ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		int n = arrParameters.indexOf(field);
		if (n>=0)
		{
			arrParameters.get(n).UnRegisterReadingAction(this) ;
			arrParameters.set(n, var);
			var.RegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}
	/**
	 * @param cursor
	 */
	public void setCursor(CEntitySQLCursor cursor)
	{
		cursor = cursor ;
	}
	protected CEntitySQLCursor cursor = null ;
}