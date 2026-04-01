/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 19 ao�t 04
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

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntitySQLCursorSelectStatement extends CBaseActionEntity
{
	public CEntitySQLCursorSelectStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
		
	}
	public void SetSelect(String csStatement, Vector<CDataEntity> arrParameters, CEntitySQLCursor cur, int nbCol, boolean bWithHold)
	{
		csStatement = csStatement ;
		arrParameters = arrParameters;
		cursor = cur ;
		nbCol = nbCol ;	
		bWithHold = bWithHold ;
	}
	protected int nbCol = 0 ;
	protected String csStatement = "" ;
	protected Vector<CDataEntity> parameters = null;
	protected CEntitySQLCursor cursor = null;
	protected boolean iswithHold = false ;
	public void Clear()
	{
		super.Clear();
		parameters.clear() ;
		cursor = null ;
	}

	public int GetNbColumns()
	{
		return nbCol ;
	}
	public boolean ignore()
	{
		return true ; // the SELECT declaration is ignore at this point, but exported in place of the OPEN statement
	}

	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		int i = parameters.indexOf(field) ;
		if (i>=0 && i< parameters.size())
		{
			parameters.get(i).UnRegisterReadingAction(this) ;
			parameters.set(i, var) ;
			var.RegisterReadingAction(this) ;
			return true ;
		} 
		return false ;
	}


}

