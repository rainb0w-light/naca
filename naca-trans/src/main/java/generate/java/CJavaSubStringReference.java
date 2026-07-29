/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 6 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java;

import generate.CBaseLanguageExporter;
import semantic.CSubStringAttributReference;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaSubStringReference extends CSubStringAttributReference
{

	/**
	 * @param l
	 * @param cat
	 * @param out
	 */
	public CJavaSubStringReference(int l, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, cat);
		setLanguageExporter(out);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#ExportReference(semantic.CBaseLanguageExporter)
	 */
	public String ExportReference(int nLine)
	{
//		if (reference.HasAccessors())
//		{
			String cs = "subString(" + reference.ExportReference(getLine()) ;
			cs += ", " + generate.LegacyExpressionRenderer.render(start);
			if(length != null)
				cs += ", " + generate.LegacyExpressionRenderer.render(length);
			cs += ")" ;
			return cs ;
//		}
//		else
//		{
//			String cs = reference.ExportReference(getLine()) ;
//			cs += ".subString(" + generate.LegacyExpressionRenderer.render(start) + ", " + generate.LegacyExpressionRenderer.render(length) + ")" ;
//			return cs ;
//		}		
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#HasAccessors()
	 */
	public boolean HasAccessors()
	{
		return true;
	}
	protected void DoExport()
	{
		// unused
	}
	public String ExportWriteAccessorTo(String value)
	{
		String cs = reference.ExportReference(getLine()) ;
		cs = "setSubString(" + cs + ", " + generate.LegacyExpressionRenderer.render(start);
		if (length != null)
			cs += ", " + generate.LegacyExpressionRenderer.render(length);
		cs += ", " + value ;
		cs += ") ;" ;
		return cs ;		
	}
	public boolean isValNeeded()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetDataType()
	 */
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.VAR ;
	}



}
