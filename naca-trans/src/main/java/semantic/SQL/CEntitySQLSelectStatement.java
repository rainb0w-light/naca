/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 18 août 04
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
	public CEntitySQLSelectStatement(int line, CObjectCatalog cat, CBaseLanguageExporter out, String csStatement, Vector<CDataEntity> arrParameters, Vector<CDataEntity> arrInto, Vector<CDataEntity> arrInd)
	{
		super(line, cat, out);
		csStatement = csStatement ;
		arrParameters = arrParameters;
		arrInto = arrInto;
		arrInd = arrInd ;
	}
	protected String csStatement = "" ;
	protected Vector<CDataEntity> arrParameters = null;
	protected Vector<CDataEntity> arrInto = null;
	protected Vector<CDataEntity> arrInd = null;
	public void Clear()
	{
		super.Clear();
		arrParameters = null ;
		arrInto = null ;
	}
	public boolean ignore()
	{
		return csStatement.equals("") ;
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (arrParameters.contains(data) || arrInto.contains(data))
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
		int n = arrParameters.indexOf(field) ;
		if (n>=0)
		{
			arrParameters.get(n).UnRegisterReadingAction(this) ;
			arrParameters.set(n, var);
			var.RegisterReadingAction(this) ;
			return true ;
		}
		n = arrInto.indexOf(field) ;
		if (n>=0)
		{
			arrInto.get(n).UnRegisterWritingAction(this) ;
			arrInto.set(n, var);
			var.RegisterWritingAction(this) ;
			return true ;
		}
		return false ;
	}

}

