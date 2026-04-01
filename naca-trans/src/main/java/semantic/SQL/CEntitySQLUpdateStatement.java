/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 ao�t 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.SQL;
import java.util.Vector;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

public abstract class CEntitySQLUpdateStatement extends CBaseActionEntity
{
	public CEntitySQLUpdateStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out, String csStatement, Vector<CDataEntity> arrSets, Vector<CDataEntity> arrParameters)
	{
		super(line, cat, out);
		csStatement = csStatement ;
		arrSets = arrSets;
		arrParameters = arrParameters;		
	}
	protected String csStatement = "" ;
	protected Vector<CDataEntity> sets = null;
	protected Vector<CDataEntity> parameters = null;
	public void Clear()
	{
		super.Clear();
		sets.clear() ;
		parameters.clear() ;
	}
	public boolean ignore()
	{
		return false ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		int n = parameters.indexOf(field);
		if (n>=0)
		{
			parameters.get(n).UnRegisterReadingAction(this) ;
			parameters.set(n, var);
			var.RegisterReadingAction(this) ;
			return true ;
		}
		n = sets.indexOf(field);
		if (n>=0)
		{
			sets.get(n).UnRegisterReadingAction(this) ;
			sets.set(n, var);
			var.RegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}
	/**
	 * @param cur
	 */
	public void setCursor(CEntitySQLCursor cur)
	{
		cursor = cur ;
	}
	protected CEntitySQLCursor cursor = null ;
}