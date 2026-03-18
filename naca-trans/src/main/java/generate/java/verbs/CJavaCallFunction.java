/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java.verbs;


import generate.CBaseLanguageExporter;
import generate.java.CJavaUnknownReference;
import semantic.CEntityProcedure;
import semantic.CEntityProcedureSection;
import semantic.Verbs.CEntityCallFunction;
import utils.CObjectCatalog;
import utils.Transcoder;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaCallFunction extends CEntityCallFunction
{

	/**
	 * @param cat
	 * @param out
	 * @param ref
	 */
	public CJavaCallFunction(int l, CObjectCatalog cat, CBaseLanguageExporter out, String ref, String csRefThru, CEntityProcedureSection section)
	{
		super(l, cat, out, ref, csRefThru, section);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#DoExport()
	 */
	protected void DoExport()
	{
		if (refRepetitions != null)
		{
			String index = "loop_index" ;
			while (programCatalog.IsExistingDataEntity(index, ""))
			{
				index += "$" ;
			}
			CJavaUnknownReference ref = new CJavaUnknownReference(getLine(), GetName(), programCatalog, null) ;
			programCatalog.RegisterDataEntity(index, ref) ;
			String cs = "for (int "+index+"=0; isLess("+index+", " + refRepetitions.ExportReference(getLine()) + "); "+index+"++) {"  ;
			WriteLine(cs) ;
			StartOutputBloc() ;
		}
		if (referenceThru != null)
		{
			CEntityProcedure e = reference.getProcedure() ;
			CEntityProcedure eThru = referenceThru.getProcedure() ;
			String line = "performThrough(" + e.ExportReference(getLine()) + ", " +eThru.ExportReference(getLine())+ ") ;" ;
			WriteLine(line);
		}
		else if (reference != null)
		{
			CEntityProcedure e = reference.getProcedure() ;
			if (e!=null)
			{
				String line = "perform(" + e.ExportReference(getLine()) +") ;" ;
				WriteLine(line);
			}
			else
			{
				Transcoder.logError(getLine(), "Unbound reference/identity");
				WriteLine("perform([UNDEFINED]) ; ");
			}
		}
		else
		{
			ExportChildren();
		}
		if (refRepetitions != null)
		{
			EndOutputBloc() ;
			WriteLine("}") ;
		}
	}

}
