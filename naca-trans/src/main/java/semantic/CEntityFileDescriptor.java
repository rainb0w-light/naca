/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import generate.CBaseLanguageExporter;
import semantic.Verbs.CEntityOpenFile;
import utils.CObjectCatalog;

public abstract class CEntityFileDescriptor extends CBaseLanguageEntity
{
	public CEntityFileDescriptor(int line, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, name, cat, out);
	}

	@Override
	protected void RegisterMySelfToCatalog()
	{
		programCatalog.RegisterFileDescriptor(this) ;
		fileSelect = programCatalog.getFileSelect(GetName()) ;
	}


	protected CEntityFileSelect fileSelect ;


	public String ExportReference(int nLine)
	{
		return FormatIdentifier(GetDisplayName());
	}

	public CDataEntity GetRecord()
	{
		if (!lstChildren.isEmpty() && lstChildren.getFirst() != null)
		{
			CDataEntity le = FindFirstDataEntityAtLevel(1) ;
			if (le != null)
			{
				return le ;
			}
		}
		return null ;
	}

	protected CEntityOpenFile.OpenMode eAccessMode = null ;
	public void setFileAccessType(CEntityOpenFile.OpenMode access)
	{
		eAccessMode = access;		
	}
	public CEntityOpenFile.OpenMode getAccessMode()
	{
		return eAccessMode;
	}

	protected boolean bVariableFile = false ;
	protected CDataEntity recSizeDependingOn = null ;
	protected CDataEntity eOutputBufferInitialValue = null ;
	public void setRecordSizeVariable(boolean variableFile)
	{
		bVariableFile = variableFile ; 
	}
	public void setRecordSizeVariable(CDataEntity depOn)
	{
		bVariableFile = true ; 
		recSizeDependingOn = depOn ;
	}
	public CDataEntity getRecordSizeDepending()
	{
		return recSizeDependingOn;
	}
	public boolean isRecordSizeVariable()
	{
		return bVariableFile ;
	}

	/**
	 * @param e
	 */
	public void setOutputBufferInitialValue(CDataEntity e)
	{
		eOutputBufferInitialValue  = e ;
	}
}
