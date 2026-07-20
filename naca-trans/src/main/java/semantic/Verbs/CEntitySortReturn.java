/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;

import semantic.CBaseActionEntity;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityFileDescriptor;
import utils.CObjectCatalog;

public class CEntitySortReturn extends CBaseActionEntity
{

	public CEntitySortReturn(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}

	protected CEntityFileDescriptor eFileDesc = null ;
	public void setDataReference(CEntityFileDescriptor ref)
	{
		eFileDesc = ref ;	
	}
	protected CDataEntity eDataInto = null;
	public void setDataReference(CEntityFileDescriptor ref, CDataEntity into)
	{
		eDataInto = into ;
		eFileDesc = ref ;
	}
	
	protected CBaseLanguageEntity blocAtEnd = null ;
	protected CBaseLanguageEntity blocNotAtEnd = null ;
	
	public void SetAtEndBloc(CBaseLanguageEntity le)
	{
		blocAtEnd = le ;
	}

	public void SetNotAtEndBloc(CBaseLanguageEntity le)
	{
		blocNotAtEnd = le ;
	}

	public CEntityFileDescriptor getFileDesc() {
		return eFileDesc;
	}
	public CDataEntity getDataInto() {
		return eDataInto;
	}
	public CBaseLanguageEntity getAtEndBloc() {
		return blocAtEnd;
	}
	public CBaseLanguageEntity getNotAtEndBloc() {
		return blocNotAtEnd;
	}
}
