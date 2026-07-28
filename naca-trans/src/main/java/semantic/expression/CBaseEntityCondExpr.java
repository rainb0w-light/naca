/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.expression;

import semantic.CDataEntity;

public abstract class CBaseEntityCondExpr extends CDataEntity
{
//	public CBaseEntityCondExpr(int nLine)
//	{
//		super(nLine);
//	}

	@Override
	public String ExportReference(int nLine)
	{
		return Export();
	}
	@Override
	public String ExportWriteAccessorTo(String value)
	{
		return null;
	}

	@Override
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.EXPRESSION ;
	}
	@Override
	public boolean HasAccessors()
	{
		return false;
	}
	@Override
	public boolean isValNeeded()
	{
		return false;
	}
	public CBaseEntityCondExpr()
	{
		super(0, "", null, null);
	}
	protected void RegisterMySelfToCatalog()
	{
		// unused
	}
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		// 
		return false ;
	}
	/**
	 * Legacy direct-generation string protocol. Concrete target backends
	 * (the CJava* subclasses under generate/) override it with their code
	 * generation. Pure semantic conditions/expressions carry no string logic:
	 * they are rendered by the recursive ST4 assembler (JavaTemplateAssembler),
	 * so this default fails closed if the legacy protocol ever reaches one.
	 */
	public String Export()
	{
		throw new UnsupportedOperationException(
			getClass().getName()
				+ " is a pure semantic entity rendered by the recursive ST4"
				+ " assembler; the legacy direct-export protocol is not supported");
	}

	private String cachedCodeString = null;

	@Override
	public String getCodeString()
	{
		if (cachedCodeString != null) return cachedCodeString;
		cachedCodeString = Export();
		return cachedCodeString;
	}

	protected void DoExport()
	{
		String cs = Export() ;
		WriteWord(cs);
	}
	public CBaseEntityCondition getAsCondition()
	{
		return null;
	}
	
	@Override
	public String GetConstantValue()
	{
		return null;
	}
}
