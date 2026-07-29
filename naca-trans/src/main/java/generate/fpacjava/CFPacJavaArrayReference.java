/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package generate.fpacjava;

import generate.CBaseLanguageExporter;
import semantic.CEntityArrayReference;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

public class CFPacJavaArrayReference extends CEntityArrayReference
{

	public CFPacJavaArrayReference(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat);
		generate.LegacyLanguageRenderer.bind(this, out);
	}

	@Override
	public CDataEntityType GetDataType()
	{
		return reference.GetDataType() ;
	}

	public String ExportReference(int nLine)
	{
		String cs = "" ;
		for (CBaseEntityExpression exp : arrIndexes)
		{
			if (cs.equals(""))
			{
				cs = generate.LegacyDataRenderer.renderReference(reference, getLine()) + "(" + generate.LegacyDataRenderer.renderReference(exp, getLine()) ;
			}
			else
			{
				cs += ", " + generate.LegacyDataRenderer.renderReference(exp, getLine()) ;
			}
		}
		cs += ")" ;
		return cs ;
		
	}

	@Override
	public boolean HasAccessors()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public String ExportWriteAccessorTo(String value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValNeeded()
	{
		// TODO Auto-generated method stub
		return false;
	}
	protected void DoExport()
	{
		// TODO Auto-generated method stub

	}

}
