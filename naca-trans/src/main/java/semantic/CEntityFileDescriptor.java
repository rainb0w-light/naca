/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import semantic.Verbs.CEntityOpenFile;
import utils.CObjectCatalog;

public class CEntityFileDescriptor extends CBaseLanguageEntity
{
	public CEntityFileDescriptor(int line, String name, CObjectCatalog cat)
	{
		super(line, name, cat);
	}

	@Override
	protected void RegisterMySelfToCatalog()
	{
		programCatalog.RegisterFileDescriptor(this) ;
		fileSelect = programCatalog.getFileSelect(GetName()) ;
	}


	protected CEntityFileSelect fileSelect ;


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

	protected boolean isvariableFile = false ;
	protected CDataEntity recSizeDependingOn = null ;
	protected CDataEntity eOutputBufferInitialValue = null ;
	public void setRecordSizeVariable(boolean variableFile)
	{
		isvariableFile = variableFile ;
	}
	public void setRecordSizeVariable(CDataEntity depOn)
	{
		isvariableFile = true ;
		recSizeDependingOn = depOn ;
	}
	public CDataEntity getRecordSizeDepending()
	{
		return recSizeDependingOn;
	}
	public boolean isRecordSizeVariable()
	{
		return isvariableFile;
	}

	/**
	 * @param e
	 */
	public void setOutputBufferInitialValue(CDataEntity e)
	{
		eOutputBufferInitialValue  = e ;
	}

	/**
	 * Optional FPac output-buffer filler captured during semantic analysis.
	 * The backend decides how that value participates in a file declaration.
	 */
	public CDataEntity getOutputBufferInitialValue()
	{
		return eOutputBufferInitialValue;
	}

	/**
	 * The SELECT file-name data entity (rendered in the REFERENCE role), or null
	 * when there is no resolvable file name — the backend then falls back to the
	 * quoted display name, exactly as the direct generator does (which discards a
	 * file name whose reference does not resolve, i.e. an unknown reference).
	 */
	public CDataEntity getFileName()
	{
		if (fileSelect == null)
		{
			return null;
		}
		CDataEntity fileName = fileSelect.GetFileName();
		return fileName instanceof CEntityUnknownReference ? null : fileName;
	}

	/**
	 * The SELECT FILE STATUS data entity (rendered in the REFERENCE role), or
	 * null when no FILE STATUS clause was declared.
	 */
	public CDataEntity getFileStatus()
	{
		return fileSelect == null ? null : fileSelect.getFileStatus();
	}

	/**
	 * Bean accessor for the raw display name; the Java backend supplies the
	 * quoting when it is used as the {@code declare.file("...")} fallback literal.
	 */
	public String getDisplayName()
	{
		return GetDisplayName();
	}

	/** Raw logical descriptor name as written by the source dialect. */
	public String getSourceName()
	{
		return GetName();
	}
}
