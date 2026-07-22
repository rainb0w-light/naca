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

public class CEntityOpenFile extends CBaseActionEntity
{
	public enum OpenMode
	{
		INPUT,
		OUTPUT,
		INPUT_OUTPUT,
		APPEND
	}
	public CEntityOpenFile(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	public void setFileDescriptor(CEntityFileDescriptor fd, OpenMode mode)
	{
		eFileDescriptor = fd ;
		eMode = mode;
	}
	protected CEntityFileDescriptor eFileDescriptor = null ;
	protected OpenMode eMode = null ; 

	public CEntityFileDescriptor getFileDescriptor()
	{
		return eFileDescriptor;
	}

	public OpenMode getMode()
	{
		return eMode;
	}

	public boolean isInputMode()
	{
		return eMode == OpenMode.INPUT;
	}

	public boolean isOutputMode()
	{
		return eMode == OpenMode.OUTPUT;
	}

	public boolean isInputOutputMode()
	{
		return eMode == OpenMode.INPUT_OUTPUT;
	}

	public boolean isAppendMode()
	{
		return eMode == OpenMode.APPEND;
	}

}
