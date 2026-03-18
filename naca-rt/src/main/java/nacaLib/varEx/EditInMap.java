/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 30 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

import jlib.misc.AsciiEbcdicConverter;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.mapSupport.LocalizedString;
import nacaLib.misc.NumberParserDec;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditInMap extends Edit
{ 
	EditInMap(DeclareTypeEditInMap declareTypeEdit)
	{
		super(declareTypeEdit);
		
		declareTypeEdit.getProgramManager().registerEditInMap(this);
				
		declareTypeEdit.registerEditInForm(this);
		
		csDeclaredEditName = declareTypeEdit.csName;
		//varDef.setDefaultFullName(declareTypeEdit.csName);	// For debug puropose only; will be replaced by edit's java variable name
		attrManager = new EditAttributManager();
		
		attrManager.allocAttributes(declareTypeEdit);
		
		//csFormat = declareTypeEdit.csFormat;
	}
	
	void restoreAttributesFromVarDef(VarDefEditInMap varDefEdit)
	{
		attrManager = new EditAttributManager();
	}
	
	protected EditInMap()
	{
		super();
	}
	
	protected VarBase allocCopy()	
	{
		EditInMap v = new EditInMap();
		return v;
	}
	
	public String toString()
	{
		String cs = "Var2Edit ";
		cs += attrManager.toString() + " "; 
		cs += getLoggableValue();
		return cs;
	}
	
	public Edit getAt(Var x)
	{
		return this;
	}

	
	public Edit getAt(int x)
	{
		return this;
	}
	
	public Edit getAt(int y, int x)
	{
		return this;
	}
	
	public Edit getAt(int z, int y, int x)
	{
		return this;
	}
	
	public void set(Var varSource)
	{
		varSource.transferTo(this);	// PJD Var TO EditInMap 
	}
		
	public void set(Edit varSource)
	{
		varSource.transferTo(this);
	}
	
	public void transferTo(Var varDest)
	{
		varDef.transfer(bufferPos, varDest);	// PJD EditInMap TO Var
	}
	
	public void transferTo(Edit varDest)
	{
		varDef.transfer(bufferPos, varDest);
	}
	
	public boolean isEditInMap()
	{
		return true;
	}

	public Edit semanticContext(String csSemanticContext)
	{
		return this;
	}
	
	protected LocalizedString getLocalizedString()
	{
		return attrManager.getLocalizedString();
	}
	
	public String getDeclaredEditName()
	{
		return csDeclaredEditName;
	}	
	


	/* (non-Javadoc)
	 * @see nacaLib.varEx.Edit#setLength(int)
	 */
	
	public Element exportXML(Document doc, String csLangId)
	{
		if(doc != null)
		{
			Element eEdit = doc.createElement("field");
			eEdit.setAttribute("length", ""+getBodyLength()) ;
			eEdit.setAttribute("name" , csDeclaredEditName);
//			String csSemanticContext = varManager.getSemanticContextValue();
//			if(csSemanticContext != null)
//				eEdit.setAttribute("SemanticContext" , csSemanticContext);
				
			String csFieldValue = getString();
			if (attrManager.localizedString != null)
			{
				boolean bDefaultValue = BaseProgram.isAll(csFieldValue, CobolConstant.LowValue.getValue()) ;
				if (bDefaultValue)	// edit not edited yet with no default value
				{
					csFieldValue = attrManager.localizedString.getTextForLanguage(csLangId);
				}
			}
			if (attrManager.csDevelopableMark != null && !attrManager.csDevelopableMark.equals("") && csFieldValue.indexOf(attrManager.csDevelopableMark) != -1)
			{
				String csId = csFieldValue.substring(0, 4);
				eEdit.setAttribute("messageId", csId);
				int nLastIndex = csFieldValue.lastIndexOf(attrManager.csDevelopableMark);
				csFieldValue = csFieldValue.substring(0, nLastIndex);
			}
			if (attrManager.csFormat != null && !attrManager.csFormat.equals(""))
			{
				if (BaseProgram.isNumeric(csFieldValue)) {
					Dec dec = NumberParserDec.getAsDec(csFieldValue);
					csFieldValue = RWNumEdited.internalFormatAndWrite(dec, attrManager.csFormat, false);
				}
			}
			
			if (csFieldValue.indexOf('\0')>=0)
			{
				csFieldValue = csFieldValue.replace('\0', ' ') ;
			}
			eEdit.setAttribute("value", csFieldValue);
			
			if (attrManager.bHasCursor)
			{
				eEdit.setAttribute("cursor", "true");
			}
			attrManager.mapFieldAttribute.exportAllAttributes(eEdit);	

			return eEdit;
		}
		return null ;
	}
	
	public void saveEditAttributesInVarDef()
	{
		VarDefEditInMap varDefEdit = (VarDefEditInMap)varDef;
		varDefEdit.saveAttributeManager(attrManager);
		
		//setSemanticContextValue(attrManager.csSemanticContext);		// Restore original semantic context already saved in attributes 	
	}
	
	public void restoreEditAttributesInVarDef()
	{
		VarDefEditInMap varDefEdit = (VarDefEditInMap)varDef;
		varDefEdit.restoreAttributeManager(attrManager);
		
		//setSemanticContextValue(varDefEdit.attrManager.csSemanticContext);		// Restore original semantic context saved in varDef attributes
	}
	
	EditAttributManager getEditAttributManager()
	{
		return attrManager;
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
		return VarType.VarEditInMap;
	}

	private String csDeclaredEditName = null;
	//private String csFormat = null;
}
