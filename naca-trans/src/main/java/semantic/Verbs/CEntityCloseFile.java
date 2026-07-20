/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

public class CEntityCloseFile extends CBaseActionEntity
{

	public CEntityCloseFile(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	public void setFileDescriptor(CEntityFileDescriptor fd)
	{
		eFileDescriptor = fd ;
	}
	protected CEntityFileDescriptor eFileDescriptor = null ;

	public CEntityFileDescriptor getFileDescriptor()
	{
		return eFileDescriptor;
	}
}
