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

//import org.w3c.dom.Element;


//import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaFormContainer extends CEntityResourceFormContainer
{

	/**
	 * @param name
	 * @param cat
	 * @param exp
	 */
	public CJavaFormContainer(int l, String name, CObjectCatalog cat, CBaseLanguageExporter lexp, boolean bSave)
	{
		super(l, name, cat, bSave);
		generate.LegacyLanguageRenderer.bind(this, lexp);
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseEntity#DoExport()
	 */
//	public Element DoXMLExport()
//	{
//		Element eForms = generate.LegacyLanguageRenderer.output(this).CreateRoot("Forms") ;
//		eForms.setAttribute("Name", GetName()) ;
//
//		for (int i=0; i<arrForm.size(); i++)
//		{
//			CEntityResourceForm e = (CEntityResourceForm)arrForm.get(i) ;
//			Element el = e.DoXMLExport() ;
//			eForms.appendChild(el) ;
//		}
//		Element eStrings = resStrings.Export(eForms, generate.LegacyLanguageRenderer.output(this).GetDocument()) ;
//		return eForms ;
//	}
	public String ExportReference(int nLine)
	{
		//e.generate.LegacyLanguageRenderer.writeWord(this, GetName()) ;
		//return generate.LegacyDataRenderer.renderReference(m_Form, getLine());
		return generate.LegacyLanguageRenderer.formatIdentifier(this, GetName());
	}
	public boolean HasAccessors()
	{
		return false;
	}
	protected void DoExport()
	{
		String name = GetName();
//		if (!bSaveCopy)
//		{
			generate.LegacyLanguageRenderer.writeEol(this) ;
			generate.LegacyLanguageRenderer.writeLine(this, "import nacaLib.mapSupport.* ;") ;
			generate.LegacyLanguageRenderer.writeLine(this, "import nacaLib.varEx.* ;") ;
			generate.LegacyLanguageRenderer.writeLine(this, "import nacaLib.program.* ;") ;
			generate.LegacyLanguageRenderer.writeLine(this, "import nacaLib.basePrgEnv.* ;") ;
			generate.LegacyLanguageRenderer.writeEol(this) ;
//		}
		generate.LegacyLanguageRenderer.writeLine(this, "class "+name+" extends Map {") ;
		generate.LegacyLanguageRenderer.startBlock(this) ;

		generate.LegacyLanguageRenderer.writeLine(this, "static "+name+" Copy(BaseProgram program) {");
		generate.LegacyLanguageRenderer.startBlock(this) ;
		generate.LegacyLanguageRenderer.writeLine(this, "return new "+name+"(program);");
		generate.LegacyLanguageRenderer.endBlock(this);
		generate.LegacyLanguageRenderer.writeLine(this, "}");

		generate.LegacyLanguageRenderer.writeLine(this, "static "+name+" Copy(BaseProgram program, CopyReplacing rep)  {");
		generate.LegacyLanguageRenderer.startBlock(this) ;
		generate.LegacyLanguageRenderer.writeLine(this, "Assert(\"Unimplemented replacing for MAPs\") ;");
		generate.LegacyLanguageRenderer.writeLine(this, "return null ;");
		generate.LegacyLanguageRenderer.endBlock(this);
		generate.LegacyLanguageRenderer.writeLine(this, "}");

		generate.LegacyLanguageRenderer.writeLine(this, ""+name+"(BaseProgram program) {");
		generate.LegacyLanguageRenderer.startBlock(this) ;
		generate.LegacyLanguageRenderer.writeLine(this, "super(program);");
		generate.LegacyLanguageRenderer.endBlock(this);
		generate.LegacyLanguageRenderer.writeLine(this, "}");
		generate.LegacyLanguageRenderer.writeLine(this, "");

//		for (int j=0;j<nbStrings; j++)
//		{
//			Element eString = (Element)lstString.item(j);
//			String strname = eString.getAttribute("Name") ;
//			String cs = "LocalizedString " + generate.LegacyLanguageRenderer.formatIdentifier(this, strname) + " = localizedString()";
//			generate.LegacyLanguageRenderer.writeWord(this, cs);
//			NodeList lstLang = eString.getElementsByTagName("LocalizedText") ;
//			int nbLang = lstLang.getLength() ;
//			for (int k=0; k<nbLang; k++)
//			{
//				cs = "" ;
//				Element e = (Element)lstLang.item(k) ;
//				String text = e.getAttribute("Text");
//				String lang = e.getAttribute("LangID");
//				cs += ".text(\""+lang+"\", \""+text+"\")" ;
//				generate.LegacyLanguageRenderer.writeWord(this, cs) ;
//			}
//			generate.LegacyLanguageRenderer.writeWord(this, ";");
//			generate.LegacyLanguageRenderer.writeEol(this);
//		}

		int nbForms = arrForm.size() ;
		for (int i=0; i<nbForms; i++)
		{
			CEntityResourceForm eForm = arrForm.get(i) ;
			generate.LegacyLanguageRenderer.invokeExport(eForm) ;
//			String formname = generate.LegacyLanguageRenderer.formatIdentifier(this, eForm.GetName()) ;
////			String sizeCol = eForm.getAttribute("SizeCol");
////			String sizeLine = eForm.getAttribute("SizeLine");
//			generate.LegacyLanguageRenderer.writeLine(this, "Form " + formname + " = form() ;") ;
//
//			generate.LegacyLanguageRenderer.startBlock(this) ;
//			Vector lstFields = eForm.GetListOfChildren() ;
//			int nbFields = lstFields.size() ;
//			for (int j=0;j<nbFields; j++)
//			{
//				CEntityResourceField eField = (CEntityResourceField)lstFields.get(j);
//				String cs = GetLineForField(eField) ;
//				generate.LegacyLanguageRenderer.writeLine(this, cs);
//			}
//			NodeList lstLabels = eForm.getElementsByTagName("Label") ;
//			int nbLabels = lstLabels.getLength() ;
//			for (int j=0;j<nbLabels; j++)
//			{
//				Element eField = (Element)lstLabels.item(j);
//				String cs = GetLineForLabel(eField) ;
//				generate.LegacyLanguageRenderer.writeLine(this, cs);
//			}
//
//			generate.LegacyLanguageRenderer.endBlock(this) ;
		}
		generate.LegacyLanguageRenderer.writeLine(this, "");


		generate.LegacyLanguageRenderer.endBlock(this) ;
		generate.LegacyLanguageRenderer.writeLine(this, "}") ;
		generate.LegacyLanguageRenderer.writeLine(this, "");
		generate.LegacyLanguageRenderer.writeLine(this, "");
//		if (savCopy != null)
//		{
//			generate.LegacyLanguageRenderer.startExport(savCopy) ;
//		}
	}
	public boolean IsNeedDeclarationInClass()
	{
		return true ;
	}
	public String ExportWriteAccessorTo(String value)
	{
		// unused
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
		if (owner != null)
		{// these are class names, not identifiers, so DO NOT FORMAT like others identifiers
			return owner.GetTypeDecl() + "." + GetName().replace('-', '_');
		}
		else
		{
			return GetName().replace('-', '_');
		}
	}


}
