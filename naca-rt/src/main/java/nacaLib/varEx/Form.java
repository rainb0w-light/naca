/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

//import nacaLib.program.Var;
import java.util.Hashtable;
import java.util.SortedSet;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jlib.misc.ArrayDyn;
import jlib.misc.ArrayFix;
import jlib.misc.ArrayFixDyn;
import jlib.misc.AsciiEbcdicConverter;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/*
 * Created on 13 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Form extends Var
{
	public Form(DeclareTypeForm declareTypeForm, String csDeclaredFormName)
	{
		super(declareTypeForm);

		SharedProgramInstanceData sharedProgramInstanceData = declareTypeForm.getProgramManager().getSharedProgramInstanceData();
		sharedProgramInstanceData.addVarDefForm((VarDefForm)varDef);

		this.csDeclaredFormName = csDeclaredFormName;

		//VarLevel varLevelHeader = new VarLevel(declareTypeForm.getProgram(), 2);
	}
	
	protected Form()
	{
		super();
	}
	
	protected VarBase allocCopy()
	{
		Form v = new Form();
		return v;
	}
	
	public void assignBufferExt(VarBuffer bufferSource)
	{
		super.assignBufferExt(bufferSource);
		// Swap type
		if(arrEdits.isDyn())
		{
			int nSize = arrEdits.size();
			EditInMap arr[] = new EditInMap[nSize];
			arrEdits.transferInto(arr);
			
			ArrayFix<EditInMap> fix = new ArrayFix<EditInMap>(arr);
			arrEdits = fix;	// replace by a fix one (uning less memory)
		}
	}
	
	public void set(String cs)
	{
		varDef.write(bufferPos, cs);
	}
	
	public void set(char c)
	{
	}

	protected String getAsLoggableString()
	{
		return "";
	}
	
	VarDefForm getDefForm()
	{
		return (VarDefForm) varDef;
	}
	
	public boolean hasType(VarTypeEnum e)
	{
		return false;
	}
	
	public void encodeToVar(Var varDest)
	{
		((VarDefForm)varDef).encodeToVar(bufferPos, varDest);
	}
	
	public void decodeFromVar(Var varSource)
	{
		((VarDefForm)varDef).decodeFromVar(bufferPos, varSource);
	}
	
	public void decodeFromCharBuffer(InternalCharBuffer charBufferSource)
	{
		((VarDefForm)varDef).decodeFromCharBuffer(bufferPos, charBufferSource);
	}
	
	void addEdit(EditInMap edit)
	{
		arrEdits.add(edit);
	}
	
	public Document getXMLData(String langID, int cursorPosition)
	{
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument() ;
			Element eForm = doc.createElement("form");
			doc.appendChild(eForm);
			eForm.setAttribute("name", csDeclaredFormName) ; 
			eForm.setAttribute("lang", langID) ;
			if (cursorPosition != 0)
			{
				eForm.setAttribute("cursorPosition", Integer.valueOf(cursorPosition).toString()) ;
			}
			for (int i=0; i<arrEdits.size(); i++)
			{
				Edit edit = arrEdits.get(i);
				Element e = edit.exportXML(doc, langID) ;
				eForm.appendChild(e) ;
			}
			return doc ;
		}
		catch (ParserConfigurationException e)
		{
		}
		return null;
	}
	
	public Edit getEdit(String name)
	{
		for (int i=0; i<arrEdits.size(); i++)
		{
			EditInMap e = arrEdits.get(i) ;
			if (e.getDeclaredEditName().equals(name))
			{
				return e ;
			}
		}
		return null ;
	}
	public void ExportFields(SortedSet<Element> setFields, Document doc, String csLangId)
	{
		for (int i=0; i<arrEdits.size(); i++)
		{
			Edit edit = arrEdits.get(i);
			Element e = edit.exportXML(doc, csLangId) ;
			if(e != null)
				setFields.add(e) ;
		}
		
		for (int i=0; i<arrEdits.size(); i++)
		{		
			Edit edit = arrEdits.get(i);
			Element e = edit.exportXML(doc, csLangId) ;
			if(e != null)
				setFields.add(e) ;
		}
	}
//	
/*
//	public Edit GetFieldAt(int nField)
//	{
//		assertIfFalse(nField < arrFields.size()) ;
//		return (Edit) arrFields.get(nField) ;
//	}
*/

	public Edit GetEditAt(int nField)
	{
		if(nField < arrEdits.size())
			return arrEdits.get(nField) ;
		return null; 
	}
	
	public String getDeclaredFormName()
	{
		return csDeclaredFormName;
	}

	public InternalCharBuffer encodeToCharBuffer()
	{
		int nDestLength = varDef.getBodyLength() + varDef.getHeaderLength();
		InternalCharBuffer charBuffer = ((VarDefForm)varDef).encodeToCharBuffer(nDestLength);
		return charBuffer;
	}
	
	public void loadValues(Document xmlData)
	{
		Element eForm = xmlData.getDocumentElement() ;
		NodeList listfields = eForm.getElementsByTagName("field") ;
		Hashtable<String, Element> tabFields = new Hashtable<String, Element>() ;
		int nFields = listfields.getLength() ;
		for (int i=0; i<nFields; i++)
		{
			Element e = (Element) listfields.item(i) ;
			String name = e.getAttribute("name");
			tabFields.put(name, e) ;
		}
		for (int i=0; i<arrEdits.size(); i++)
		{
			EditInMap edit = arrEdits.get(i) ;
			Element eField = tabFields.get(edit.getDeclaredEditName()) ;
			edit.fillWithValue(eField);
		}
	}

//	public void initialize()
//	{
//		InitializeManager initializeManagerManager = TempCacheLocator.getTLSTempCache().getInitializeManagerLowValue();
//		varDef.initializeItemAndChildren(bufferPos, initializeManagerManager, 0);
//		for (int i=0; i<arrEdits.size(); i++)
//		{
//			EditInMap edit = arrEdits.get(i) ;
//			edit.initializeAttributes();
//		}
//	}
	
	public void initialize(InitializeCache initializeCache)
	{
		if(initializeCache != null && initializeCache.isFilled())	// initializeCache may be null 
		{
			//varDef.initializeUsingCache(bufferPos, initializeCache);
			initializeCache.applyItems(bufferPos, bufferPos.nAbsolutePosition);
		}
		else	
		{
			TempCache tempCache = TempCacheLocator.getTLSTempCache();
			InitializeManager initializeManagerManager = tempCache.getInitializeManagerLowValue();
			
			varDef.initializeItemAndChildren(bufferPos, initializeManagerManager, 0, initializeCache);
			
			if(initializeCache != null)
				initializeCache.setFilledAndcompress(bufferPos.nAbsolutePosition);
		}
		
		for (int i=0; i<arrEdits.size(); i++)
		{
			EditInMap edit = arrEdits.get(i) ;
			edit.initializeAttributes();
		}
	}

	
	
	public int compareTo(int nValue)
	{
		int nVarValue = getInt();
		return nVarValue - nValue;
	}
	
	
	public int compareTo(double dValue)
	{
		double varValue = getDouble();
		double d = varValue - dValue;
		if(d < -0.00001)	//Consider epsilon precision at 10 e-5 
			return -1;
		else if(d > 0.00001)	//Consider epsilon precision at 10 e-5
			return 1;
		return 0;			
	} 
	

	protected byte[] convertUnicodeToEbcdic(char[] tChars)
	{
		return AsciiEbcdicConverter.noConvertUnicodeToEbcdic(tChars);
	}
	
	protected char[] convertEbcdicToUnicode(byte[] tBytes)
	{
		return AsciiEbcdicConverter.noConvertEbcdicToUnicode(tBytes);
	}
	
	
	public VarType getVarType()
	{
		return VarType.VarForm;
	}

	private ArrayFixDyn<EditInMap> arrEdits = new ArrayDyn<EditInMap>();	// Array of VarEditInMap
	private String csDeclaredFormName = null;
}
