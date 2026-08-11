/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityNoAction extends CBaseActionEntity
{

	/**
	 * @param line
	 * @param cat
	 */
	public CEntityNoAction(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	public boolean ignore()
	{
		return true ;
	}

	public boolean IgnoreVariable(CDataEntity data)
	{
		data.UnRegisterReadingAction(this) ;
		data.UnRegisterWritingAction(this) ;
		return true ;
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		field.UnRegisterReadingAction(this) ;
		field.UnRegisterWritingAction(this) ;
		return true ;
	}

}
