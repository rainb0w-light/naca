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
package generate.java.forms;

import generate.CBaseLanguageExporter;



//import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceForm;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaForm extends CEntityResourceForm
{

	/**
	 * @param name
	 * @param cat
	 * @param exp
	 */
	public CJavaForm(int l, String name, CObjectCatalog cat, CBaseLanguageExporter lexp, boolean bSave)
	{
		super(l, name, cat, bSave);
		generate.LegacyLanguageRenderer.bind(this, lexp);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseEntity#DoExport()
	 */
//	public Element DoXMLExport(Document doc)
//	{
//		Element eForm = doc.createElement("form") ;
//		// set attributes 
//		eForm.setAttribute("name", parent.GetName()) ;
//		eForm.setAttribute("sizecol", ""+nSizeCol);		
//		eForm.setAttribute("sizeline", ""+nSizeLine);
//		if (!csCustomSubmitMethod.equals(""))
//		{
//			eForm.setAttribute("customSubmit", csCustomSubmitMethod) ;
//		}				
//		
//		// add fields
////		for(int nCurField = 0 ; nCurField<arrFields.size(); nCurField++) 
////		{
////			CEntityResourceField field = (CEntityResourceField)arrFields.get(nCurField);
////			Element eField = field.DoXMLExport() ;
////			eForm.appendChild(eField) ;
////		}
//		return eForm;		
//	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#ExportReference(semantic.CBaseLanguageExporter)
	 */
	public String ExportReference(int nLine)
	{
		String cs = "" ;
		if (of != null)
		{
			cs = generate.LegacyDataRenderer.renderReference(of, getLine()) + "." ;
		}
		cs += generate.LegacyLanguageRenderer.formatIdentifier(this, GetName()) ;
		return cs ;		
	}
	public boolean HasAccessors()
	{
		return false;
	}
	protected void DoExport()
	{
		String name = generate.LegacyLanguageRenderer.formatIdentifier(this, GetName()) ;
		String formname = generate.LegacyLanguageRenderer.formatIdentifier(this, csResourceName);
		generate.LegacyLanguageRenderer.writeLine(this, "Form " + name + " = declare.form(\""+formname+"\", "+nSizeLine+", "+nSizeCol+") ;") ;
			
		generate.LegacyLanguageRenderer.startBlock(this) ;
		int nbFields = arrFields.size() ;
		for (int j=0;j<nbFields; j++)
		{
			CEntityResourceField eField = (CEntityResourceField)arrFields.get(j);
//			String cs = GetLineForField(eField) ;
//			generate.LegacyLanguageRenderer.writeLine(this, cs);
			generate.LegacyLanguageRenderer.invokeExport(eField) ;
		} 
		generate.LegacyLanguageRenderer.endBlock(this) ;
	}
	public String ExportWriteAccessorTo(String value)
	{
		// unsued		
		return "" ;
	}
	public boolean isValNeeded()
	{
		return false;
	}


	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetDataType()
	 */
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FORM ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseExternalEntity#GetTypeDecl()
	 */
	public String GetTypeDecl()
	{
		return ""; // unsued
	}

}
