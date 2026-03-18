/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 10 févr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package semantic.expression;

import generate.CBaseLanguageExporter;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class CEntityConcat extends CBaseEntityFunction
{

	/* (non-Javadoc)
	 * @see semantic.CBaseDataReference#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity, boolean)
	 */
	@Override
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var, boolean bRead)
	{
		boolean b = super.ReplaceVariable(field, var, bRead);
		if (dataRef2 == field)
		{
			dataRef2 = var ;
			field.UnRegisterReadReference(this) ;
			var.RegisterReadReference(this) ;
			return true ;
		}
		return b ;
	}
	/**
	 * @param cat
	 * @param out
	 * @param data
	 */
	public CEntityConcat(CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity data1, CDataEntity data2)
	{
		super(cat, out, data1);
		dataRef2 = data2 ;
		dataRef2.RegisterReadReference(this) ;
	}
	
	protected CDataEntity dataRef2 = null ;
	public void Clear()
	{
		super.Clear() ;
		dataRef2 = null ;
	}


}
