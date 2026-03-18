/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import generate.CBaseLanguageExporter;
import utils.CEntityHierarchy;
import utils.CObjectCatalog;

public class CEntityValueReference extends CBaseDataReference
{


	public CEntityValueReference(CObjectCatalog cat, CBaseLanguageExporter out, CDataEntity ref)
	{
		super(0, "", cat, out);
		reference = ref ;
	}

	@Override
	public CDataEntityType GetDataType()
	{
		return reference.GetDataType();
	}

	@Override
	public String ExportReference(int nLine)
	{
		return reference.ExportReference(getLine()) ;
	}

	@Override
	public boolean HasAccessors()
	{
		return reference.HasAccessors();
	}

	@Override
	public String ExportWriteAccessorTo(String value)
	{
		return reference.ExportWriteAccessorTo(value);
	}

	@Override
	public boolean isValNeeded()
	{
		return reference.isValNeeded() ;
	}

	@Override
	public String GetConstantValue()
	{
		return reference.GetConstantValue() ;
	}

	@Override
	protected void DoExport()
	{
		reference.DoExport() ;
	}
	public String GetName()
	{
		return reference.GetName() ;
	}
	
	@Override
	public CBaseLanguageEntity GetParent()
	{
		return reference.GetParent() ;
	}
	
	@Override
	public CEntityHierarchy GetHierarchy()
	{
		return reference.GetHierarchy() ;
	}
}
