/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import java.util.Vector;

import generate.CBaseLanguageExporter;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import semantic.CEntityProcedure;
import utils.CObjectCatalog;

public abstract class CEntitySort extends CBaseActionEntity
{

	public CEntitySort(int line, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(line, cat, out);
	}

	public void setFileDesriptor(CEntityFileDescriptor fileDesc)
	{
		fileDescriptor = fileDesc ;
	}
	
	protected CEntityFileDescriptor fileDescriptor = null ;
	protected class CEntitySortKey
	{
		public CDataEntity key = null;
		public boolean bAscending = false ;
	}
	protected Vector<CEntitySortKey> sortKey = new Vector<CEntitySortKey>() ;
	
	public void AddKey(boolean ascending, CDataEntity key)
	{
		CEntitySortKey sk = new CEntitySortKey() ;
		sk.bAscending = ascending ;
		sk.key = key ;
		sortKey.add(sk) ;
	}

	protected CEntityFileDescriptor fdInputFile = null ;
	protected CEntityFileDescriptor fdOutputFile = null ;
	protected CEntityProcedure pInputProcedure = null ;
	protected CEntityProcedure pOutputProcedure = null ;
	
	public void setInputFile(CEntityFileDescriptor input)
	{
		fdInputFile = input ;		
	}

	public void setInputProcedure(CEntityProcedure proc)
	{
		pInputProcedure = proc ;
	}

	public void setOutputFile(CEntityFileDescriptor output)
	{
		fdOutputFile = output ;
	}

	public void setOutputProcedure(CEntityProcedure proc)
	{
		pOutputProcedure = proc ;
	}

	protected String csOutputProcedureName = null ;
	public void setOutputProcedure(String string)
	{
		csOutputProcedureName = string ;	
	}
	
	protected String csInputProcedureName = null ;
	public void setInputProcedure(String string)
	{
		csInputProcedureName = string ;	
	}
	
	
	


}
