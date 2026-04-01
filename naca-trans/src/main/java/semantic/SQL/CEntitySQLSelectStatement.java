/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 ao�t 04
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
public abstract class CEntitySQLSelectStatement extends CBaseActionEntity
{
	public CEntitySQLSelectStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out, String csStatement, Vector<CDataEntity> parameters, Vector<CDataEntity> into, Vector<CDataEntity> ind)
	{
		super(line, cat, out);
		csStatement = csStatement ;
		parameters = parameters;
		into = into;
		ind = ind;
	}
	protected String csStatement = "" ;
	protected Vector<CDataEntity> parameters = null;
	protected Vector<CDataEntity> into = null;
	protected Vector<CDataEntity> ind = null;
	public void Clear()
	{
		super.Clear();
		parameters = null ;
		into = null ;
	}
	public boolean ignore()
	{
		return csStatement.equals("") ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (parameters.contains(data) || into.contains(data))
		{
			data.UnRegisterReadingAction(this) ;
			data.UnRegisterWritingAction(this) ;
			csStatement = "" ;
			return true ;
		}
		return false ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		int n = parameters.indexOf(field) ;
		if (n>=0)
		{
			parameters.get(n).UnRegisterReadingAction(this) ;
			parameters.set(n, var);
			var.RegisterReadingAction(this) ;
			return true ;
		}
		n = into.indexOf(field) ;
		if (n>=0)
		{
			into.get(n).UnRegisterWritingAction(this) ;
			into.set(n, var);
			var.RegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}

}

