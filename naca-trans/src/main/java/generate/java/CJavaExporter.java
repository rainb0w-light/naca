/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 3 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java;

import generate.CBaseLanguageExporter;

import java.io.*;

//import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Hashtable;

import parser.CGlobalCommentContainer;

import semantic.expression.CBaseEntityCondition;
import utils.Transcoder;
import utils.COriginalLisiting;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaExporter extends CBaseLanguageExporter
{
	public CJavaExporter(COriginalLisiting cat, String file, CGlobalCommentContainer commCont, boolean isresources)
	{
		super(cat, commCont);
		//output = new PrintStream(out) ;
		fileName = file ;
		isresources = isresources;
		InitReservedWords() ;
	}
	
	public CJavaExporter(CBaseLanguageExporter exporter, String file)
	{
		super(exporter);
		fileName = file ;
		InitReservedWords() ;
	}

	protected Hashtable tabReservedWords = new Hashtable() ;
	/**
	 * 
	 */
	private void InitReservedWords()
	{
		tabReservedWords.put("new", "new");
		tabReservedWords.put("long", "long");
		tabReservedWords.put("char", "char");
		tabReservedWords.put("enum", "enum");
		tabReservedWords.put("int", "int");
		tabReservedWords.put("double", "double");
		tabReservedWords.put("for", "for");
		tabReservedWords.put("string", "String");
		tabReservedWords.put("switch", "switch");
		tabReservedWords.put("interface", "interface");
	}

	public void DoWriteLine(String line)
	{
		if (output == null)
		{
			CreateFile() ;
		}
		output.println(indent + line) ;
	}
	/**
	 * 
	 */
	private void CreateFile()
	{
		File f = new File(fileName);
		if (f.exists() && f.isFile())
		{
			isfileExisting = true ;
			csTempFileName = GenereTempFileName(fileName) ;
			f = new File(csTempFileName) ;

		}
		try
		{
			//OutputStream out = new FileOutputStream(cs);
			output = new PrintStream(f, "ISO-8859-1") ;
		} 
		catch (FileNotFoundException e)
		{
			Transcoder.logError("Can't create file " + f.getAbsolutePath()) ;
			return ;
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}	
	}

	protected PrintStream output ;
	protected String fileName = "" ;
	protected boolean isresources = false ;
	protected boolean isfileExisting = false ;
	protected String csTempFileName = "" ;

	public void GenereJavaCode(Element root)
	{
		String name = root.getAttribute("Name") ;
		WriteLine("class "+name+" extends Map {") ;
		StartBloc() ;

		WriteLine("static "+name+" Copy(Program program) {");
		StartBloc() ;
		WriteLine("return new "+name+"(program);");
		EndBloc();
		WriteLine("}");
		WriteLine(""+name+"(Program program) {");
		StartBloc() ;
		WriteLine("super(program);");
		EndBloc();
		WriteLine("}");
		WriteLine("");

		NodeList liststring = root.getElementsByTagName("String") ;
		int nbStrings = liststring.getLength() ;
		for (int j=0;j<nbStrings; j++)
		{
			Element eString = (Element) liststring.item(j);
			String strname = eString.getAttribute("Name") ;
			String cs = "LocalizedString " + FormatIdentifier(strname) + " = localizedString()";
			WriteWord(cs);
			NodeList listlang = eString.getElementsByTagName("LocalizedText") ;
			int nbLang = listlang.getLength() ;
			for (int k=0; k<nbLang; k++)
			{
				cs = "" ;
				Element e = (Element) listlang.item(k) ;
				String text = e.getAttribute("Text");
				String lang = e.getAttribute("LangID");
				cs += ".text(\""+lang+"\", \""+text+"\")" ;
				WriteWord(cs) ;
			}			
			WriteWord(";");
			WriteEOL();
		} 

		NodeList lstForms = root.getElementsByTagName("Form") ;
		int nbForms = lstForms.getLength() ;
		for (int i=0; i<nbForms; i++)
		{
			Element eForm = (Element)lstForms.item(i) ;
			String formname = FormatIdentifier(eForm.getAttribute("Name")) ;
			String sizeCol = eForm.getAttribute("SizeCol");
			String sizeLine = eForm.getAttribute("SizeLine");
			WriteLine("Form " + formname + " = form("+sizeLine+", "+sizeCol+") ;") ;
			
			StartBloc() ;
			NodeList listfields = eForm.getElementsByTagName("EntryField") ;
			int nbFields = listfields.getLength() ;
			for (int j=0;j<nbFields; j++)
			{
				Element eField = (Element) listfields.item(j);
				String cs = GetLineForField(eField) ;
				WriteLine(cs);
			} 
			NodeList listlabels = eForm.getElementsByTagName("Label") ;
			int nbLabels = listlabels.getLength() ;
			for (int j=0;j<nbLabels; j++)
			{
				Element eField = (Element) listlabels.item(j);
				String cs = GetLineForLabel(eField) ;
				WriteLine(cs);
			} 

			EndBloc() ;
		}
		WriteLine("");

		EndBloc() ;
		WriteLine("}") ;
	
//		
//		String names = name + "S" ;
//		WriteLine("// save MAP");
//		WriteLine("class " + names + " extends Map {");
//		StartBloc();
//
//		WriteLine("static "+names+" Copy(Program program) {");
//		StartBloc() ;
//		WriteLine("return new "+names+"(program);");
//		EndBloc();
//		WriteLine("}");
//		WriteLine(""+names+"(Program program) {");
//		StartBloc() ;
//		WriteLine("super(program);");
//		EndBloc();
//		WriteLine("}");
//
//
//		for (int i=0; i<nbForms; i++)
//		{
//			Element eForm = (Element)lstForms.item(i) ;
//			String formname = FormatIdentifier(eForm.getAttribute("Name")+"S") ;
//			String sizeCol = eForm.getAttribute("SizeCol");
//			String sizeLine = eForm.getAttribute("SizeLine");
//			WriteLine("Form " + formname + " = form("+sizeLine+", "+sizeCol+") ;") ;
//			
//			StartBloc() ;
//			NodeList lstFields = eForm.getElementsByTagName("EntryField") ;
//			int nbFields = lstFields.getLength() ;
//			for (int j=0;j<nbFields; j++)
//			{
//				Element eField = (Element)lstFields.item(j);
//				String cs = GetLineForFieldS(eField) ;
//				WriteLine(cs);
//			} 
//			EndBloc() ;
//		}
//		EndBloc() ;
//		WriteLine("}");
		closeOutput() ;
	}
	private String GetLineForField(Element eField)
	{
		String fieldname = FormatIdentifier(eField.getAttribute("Name")) ;
		String length = eField.getAttribute("Length");
		String col = eField.getAttribute("Col");
		String line = eField.getAttribute("Line");
		if (!fieldname.equals(""))
		{
			String cs = "MapField " + fieldname + " = edit("+line+", "+col+", "+length+")" ;
			cs += GetOptionsForField(eField) ;
			return cs ;
		}
		return "" ;
	}

	private String GetLineForLabel(Element eField)
	{
		String fieldname = FormatIdentifier(eField.getAttribute("Name")) ;
		String length = eField.getAttribute("Length");
		String col = eField.getAttribute("Col");
		String line = eField.getAttribute("Line");
		if (!fieldname.equals(""))
		{
			String cs = "MapField " + fieldname + " = label("+line+", "+col+", "+length+")" ;
			cs += GetOptionsForField(eField) ;
			return cs ;
		}
		return "" ;
	}
	private String GetOptionsForField(Element eField)
	{
		String val = eField.getAttribute("InitialValue");
		String color = eField.getAttribute("Color");
		String highlight = eField.getAttribute("HighLight");
		String cs = "" ;
		if (!val.equals(""))
		{
			cs += ".localizedString("+FormatIdentifier(val)+")";
		}
		if (!color.equals(""))
		{
			cs += ".color(MapFieldAttrColor."+color+")";
		}
		if (!highlight.equals(""))
		{
			cs += ".highLighting(MapFieldAttrHighlighting."+highlight+")" ;
		}
		NodeList listatt = eField.getElementsByTagName("Attribute") ;
		int nbAtt = listatt.getLength() ;
		for (int k=0; k<nbAtt; k++)
		{
			Element e = (Element) listatt.item(k) ;
			String v = e.getAttribute("Value");
			if (v.equals("ASKIP"))
			{
				cs += ".protection(MapFieldAttrProtection.AUTOSKIP)" ;
			}
			else if (v.equals("UNPROT"))
			{
				cs += ".protection(MapFieldAttrProtection.UNPROTECTED)" ;
			}
			else if (v.equals("PROT"))
			{
				cs += ".protection(MapFieldAttrProtection.PROTECTED)" ;
			}
			else if (v.equals("NUM"))
			{
				cs += ".protection(MapFieldAttrProtection.NUMERIC)" ;
			}
			else if (v.equals("NORM"))
			{
				cs += ".intensity(MapFieldAttrIntensity.NORMAL)" ;
			}
			else if (v.equals("BRT"))
			{
				cs += ".intensity(MapFieldAttrIntensity.BRIGHT)" ;
			}
			else if (v.equals("DRK"))
			{
				cs += ".intensity(MapFieldAttrIntensity.DARK)" ;
			}
			else if (v.equals("FSET"))
			{
				cs += ".attrib(MapFieldAttrModified.MODIFIED)" ;
			}
			else if (v.equals("IC"))
			{
				cs += ".setCursor(true)" ;
			}
			else
			{
				cs += ".attrib(\""+v+"\")" ;
			}
		}			
		NodeList listjst = eField.getElementsByTagName("Justify") ;
		int nbJst = listjst.getLength() ;
		for (int k=0; k<nbJst; k++)
		{
			Element e = (Element) listjst.item(k) ;
			String v = e.getAttribute("Value");
			if (v.equals("BLANK") || v.equals("ZERO"))
			{
				cs += ".justifyFill(MapFieldAttrFill."+v+")" ;
			}
			else
			{
				cs += ".justify(MapFieldAttrJustify."+v+")" ;
			}
		}		
		cs += ";";
		return cs ;
	}

	private String GetLineForFieldS(Element eField)
	{
		String fieldname = FormatIdentifier("S" + eField.getAttribute("Name")) ;
		String length = eField.getAttribute("Length");
		String col = eField.getAttribute("Col");
		String line = eField.getAttribute("Line");
		String val = eField.getAttribute("InitialValue");
		String color = eField.getAttribute("Color");
		String highlight = eField.getAttribute("HighLight");
		if (!fieldname.equals(""))
		{
			String cs = "MapField " + fieldname + " = edit(\""+fieldname+"\", "+line+", "+col+", "+length+")" ;
//			if (!val.equals(""))
//			{
//				cs += ".localizedString("+FormatIdentifier(val)+")";
//			}
			cs += ";";
			return cs ;
		}
		return "" ;
	}

	public void CloseBracket()
	{
		WriteWord(")") ;
	}
	public void OpenBracket()
	{
		WriteWord("(") ;
	}
	
	public static String ExportChildCondition(int parentLevel, CBaseEntityCondition condChild)
	{
		if (condChild == null)
		{
			return "[UNDEFINED]";
		}
		int childLevel = condChild.GetPriorityLevel() ;
		if ((parentLevel == 2 && childLevel ==1) || (parentLevel == 1 && childLevel == 2) || parentLevel > childLevel)
		{ // 1 and 2 are 'AND' and 'OR'
			return "(" + condChild.Export() + ")" ;
		}
		else
		{
			return condChild.Export() ;
		}
	}
	
	public String FormatIdentifier(String id)
	{
		String cs = id.toLowerCase() ;
		cs = cs.replace('_', '$') ;
		String out = "" ;
		int pos = cs.indexOf('-') ;
		while (pos != -1)
		{
			out += cs.substring(0, pos) + "_" ;
			char c = cs.charAt(pos+1) ;
			if (c == '-')
			{
				cs = cs.substring(pos+1) ; 
				pos = 0 ;
			}
			else
			{
				c=Character.toUpperCase(c) ;
				out += c ;
				cs = cs.substring(pos+2) ; 
				pos = cs.indexOf('-') ;
			}
		}
		out += cs ;
		if (out.length()>0)
		{
			if (Character.isDigit(out.charAt(0)))
			{
				out = "$" + out ;
			}
		}
		out = out.replace('#', '$') ;
		if (tabReservedWords.containsKey(out.toLowerCase()))
		{
			out += "$" ;
		}
		return out ;
	}

	/**
	 * 
	 */
	protected void doCloseOutput()
	{
		output.close() ;
		if (isfileExisting)
		{
			File newF = new File(csTempFileName) ;
			File file = new File(fileName) ;
			file.delete() ;
			newF.renameTo(file) ;
		}
	}

	@Override
	public String getOutputDir()
	{
		File f = new File(fileName);
		return f.getParent() +  "/" ;
	}
	
	@Override
	public boolean isResources()
	{
		return isresources;
	}

}
