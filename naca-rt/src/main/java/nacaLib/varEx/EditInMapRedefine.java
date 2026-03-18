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
import nacaLib.basePrgEnv.BaseProgramLoader;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.programPool.SharedProgramInstanceDataCatalog;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EditInMapRedefine extends Edit
{
	EditInMapRedefine(DeclareTypeBase declareTypeBase)	//DeclareTypeEditInMapRedefine declareTypeEditInMapRedefine)
	{
		//super(declareTypeEditInMapRedefine);
		super(declareTypeBase);
	}
			
	protected EditInMapRedefine()
	{
		super();
	}
	
	protected VarBase allocCopy()
	{
		EditInMapRedefine v = new EditInMapRedefine();
		return v;
	}
	
	public String toString()
	{		
		String cs = "Var2Edit ";
		if(attrManager != null)
			cs += attrManager.toString() + " ";
		else
			cs += "(No attributes) ";
		cs += getLoggableValue();
		return cs;
	}
	
//	public Var getChildAt(int n)
//	{
//		int nNChildren = varDef.getNbChildren();
//		if(n < nNChildren)
//		{
//			VarDefBuffer varDefChild = varDef.getChild(n);
//			if(varDefChild != null)
//			{
//				Var varChild = (Var)bufferPos.getProgramManager().getVarFullName(varDefChild);
//				return varChild;
//			}
//		}	
//		return null;
//	}
		
	
	public Edit getAt(Var x)
	{
		int n = x.getInt();
		return getAt(n);
	}
	
	public EditInMapRedefine allocOccursedItem(VarDefBuffer varDefItem)
	{ 
		EditInMapRedefine vItem = new EditInMapRedefine();
		vItem.varDef = varDefItem;
		
		int nOffset = bufferPos.nAbsolutePosition - varDef.nDefaultAbsolutePosition;
		vItem.bufferPos = new VarBufferPos(bufferPos, varDefItem.nDefaultAbsolutePosition + nOffset);
		vItem.varTypeId = varDefItem.getTypeId();
		
		//assertIfFalse(vItem.bufferPos.getProgramManager() == bufferPos.getProgramManager());
		
		vItem.attrManager = vItem.getEditAttributManager();
		return vItem;		
	}
	
	public Edit getEditAt(int x)
	{
		return getAt(x);
	}
	
	public Edit getEditAt(int x, int y)
	{
		return getAt(x, y);
	}
	
	public Edit getEditAt(int x, int y, int z)
	{
		return getAt(x, y, z);
	}


//	public Edit getAt(int x)
//	{
//		
//		VarDefBuffer varDefItem = varDef.getAt(x);
//		if(varDefItem == null)
//			return this;
//		Edit editItem = allocOccursedItem(varDefItem);
//		return editItem;
//	}
	
	public Edit getAt(int x)
	{
		TempCache cache = TempCacheLocator.getTLSTempCache();
		if(cache != null)
		{
			int nTypeId = varDef.getTypeId();
			//CoupleVar<Edit> coupleEditGetAt = cache.getTempEdit(nTypeId);
			CoupleVar coupleEditGetAt = cache.getTempVar(nTypeId);
			if(coupleEditGetAt != null)
			{
			  	// Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
				varDef.checkIndexes(x-1);
				
				int nAbsStart = varDef.getAbsStart(x-1);
				int nDebugIndex = VarDefBase.makeDebugIndex(x);
				varDef.adjustSetting(coupleEditGetAt.varDefBuffer, nAbsStart, nDebugIndex, 1, varDef.varDefParent);
				
				if(coupleEditGetAt.variable == null)
					coupleEditGetAt.variable = allocOccursedItem(coupleEditGetAt.varDefBuffer);
								
				adjust(coupleEditGetAt.varDefBuffer, (Edit)coupleEditGetAt.variable);
				return (Edit)coupleEditGetAt.variable;
			}
			VarDefBuffer varDefGetAt = varDef.getAt(x);
			if(varDefGetAt == null)
				return this;
			Edit editGetAt = allocOccursedItem(varDefGetAt);
			cache.addTempVar(nTypeId, varDefGetAt, editGetAt);
			
			return editGetAt;
		}

		VarDefBuffer varDefItem = varDef.getAt(x);
		if(varDefItem == null)
			return this;
		Edit editItem = allocOccursedItem(varDefItem);
		return editItem;
	}
	
	private void adjust(VarDefBuffer varDefGetAt, Edit editGetAt)
	{
		// Fill varGetAt with custom setting of this 
		editGetAt.varDef = varDefGetAt;
		
		int nOffset = bufferPos.nAbsolutePosition - varDef.nDefaultAbsolutePosition;
		editGetAt.bufferPos.shareDataBufferFrom(bufferPos);		
		
		editGetAt.bufferPos.nAbsolutePosition = varDefGetAt.nDefaultAbsolutePosition + nOffset;
		
		editGetAt.attrManager = editGetAt.getEditAttributManager();
	}
	
//
//	public Edit getAt(int x, int y)
//	{
//		VarDefBuffer varDefItem = varDef.getAt(x, y);
//		if(varDefItem == null)
//			return this;
//		Edit editItem = allocOccursedItem(varDefItem);
//		return editItem;
//	}
	
	
	public Edit getAt(int x, int y)
	{
		TempCache cache = TempCacheLocator.getTLSTempCache();
		if(cache != null)
		{
			int nTypeId = varDef.getTypeId();
			CoupleVar coupleEditGetAt = cache.getTempVar(nTypeId);
			if(coupleEditGetAt != null)
			{
				varDef.checkIndexes(y-1, x-1);
			  	// Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
				int nAbsStart = varDef.getAbsStart(y-1, x-1);
				int n = 0;
				int nDebugIndex = VarDefBase.makeDebugIndex(x, y);
				varDef.adjustSetting(coupleEditGetAt.varDefBuffer, nAbsStart, nDebugIndex, 2, varDef.varDefParent);
				
				if(coupleEditGetAt.variable == null)
					coupleEditGetAt.variable = allocOccursedItem(coupleEditGetAt.varDefBuffer);
					
				
				adjust(coupleEditGetAt.varDefBuffer, (Edit)coupleEditGetAt.variable);
				//Edit oldEdit = getAtOld(x, y);
				return (Edit)coupleEditGetAt.variable;
			}
			VarDefBuffer varDefGetAt = varDef.getAt(x, y);
			if(varDefGetAt == null)
				return this;
			Edit editGetAt = allocOccursedItem(varDefGetAt);
			cache.addTempVar(nTypeId, varDefGetAt, editGetAt);
			
			return editGetAt;
		}

		VarDefBuffer varDefItem = varDef.getAt(x, y);
		if(varDefItem == null)
			return this;
		Edit editItem = allocOccursedItem(varDefItem);
		return editItem;
	}
	
//	public Edit getAt(int x, int y, int z)
//	{
//		VarDefBuffer varDefItem = varDef.getAt(x, y, z);
//		if(varDefItem == null)
//			return this;
//		Edit editItem = allocOccursedItem(varDefItem);
//		return editItem;
//	}
	
	public Edit getAt(int x, int y, int z)
	{
		TempCache cache = TempCacheLocator.getTLSTempCache();
		if(cache != null)
		{
			int nTypeId = varDef.getTypeId();
			CoupleVar coupleEditGetAt = cache.getTempVar(nTypeId);
			if(coupleEditGetAt != null)
			{
				varDef.checkIndexes(z-1, y-1, x-1);
			  	// Adjust varDefGetAt to varDef.getAt(x); It is already created in the correct type
				int nAbsStart = varDef.getAbsStart(z-1, y-1, x-1);
				int nDebugIndex = VarDefBase.makeDebugIndex(x, y, z);
				varDef.adjustSetting(coupleEditGetAt.varDefBuffer, nAbsStart, nDebugIndex, 3, varDef.varDefParent);
				
				if(coupleEditGetAt.variable == null)
					coupleEditGetAt.variable = allocOccursedItem(coupleEditGetAt.varDefBuffer);
				
				adjust(coupleEditGetAt.varDefBuffer, (Edit)coupleEditGetAt.variable);
				//Edit oldEdit = getAtOld(x, y);
				return (Edit)coupleEditGetAt.variable;
			}
			VarDefBuffer varDefGetAt = varDef.getAt(x, y, z);
			if(varDefGetAt == null)
				return this;
			Edit editGetAt = allocOccursedItem(varDefGetAt);
			cache.addTempVar(nTypeId, varDefGetAt, editGetAt);
			
			return editGetAt;
		}

		VarDefBuffer varDefItem = varDef.getAt(x, y, z);
		if(varDefItem == null)
			return this;
		Edit editItem = allocOccursedItem(varDefItem);
		return editItem;
	}
	
	public void set(Var varSource)
	{
		varSource.transferTo(this);		
	}	
	
	public void set(Edit varSource)
	{
		varSource.transferTo(this);
	}
	
	public void transferTo(Var varDest)
	{		
		varDef.transfer(bufferPos, varDest);		
	}

	public void transferTo(Edit varDest)
	{
		varDef.transfer(bufferPos, varDest);
	}
	
	public boolean isEditInMap()
	{
		return false;
	}
	
	public Element exportXML(Document doc, String csLangId)
	{
		return null;
	}

	EditAttributManager getEditAttributManager()
	{
		BaseProgramManager programManager = TempCacheLocator.getTLSTempCache().getProgramManager(); 
		
		VarDefBuffer varDefEditInMapOrigin = varDef.getVarDefEditInMapOrigin();
		//if(varDefEditInMapOrigin != null)
		{
			VarBase varEditInMap = programManager.getVarFullName(varDefEditInMapOrigin);
			EditAttributManager editAttrManager = varEditInMap.getEditAttributManager();
			attrManager = editAttrManager;
			return attrManager;
		}
//		else
//		{
//			logSevereErrorGetEditAttributManager("getEditAttributManager: SEVERE ERROR");
//		}
//		return null;
	}
	
//	private void logSevereErrorGetEditAttributManager(String csTitle)
//	{	
//		String csSimpleName = TempCacheLocator.getTLSTempCache().getProgramManager().getProgramName();
//		
//		StringBuffer sbText = new StringBuffer(); 
//		sbText.append("In program " + csSimpleName + "\r\n");
//		sbText.append("It will crash\r\n"); 
//		sbText.append("Could not find The variable getVarDefEditInMapOrigin() for current varDef\r\n");
//		sbText.append("Current varDefId="+varDef.getId()+" / varDef solvedId="+varDef.getIdSolvedDim()+"\r\n");
//		
//		sbText.append("\r\n");
//		
//		SharedProgramInstanceData sharedProgramInstanceData = SharedProgramInstanceDataCatalog.getSharedProgramInstanceData(csSimpleName);
//		if(sharedProgramInstanceData != null)
//		{		
//			sbText.append("\r\nsharedProgramInstanceData:\r\n");
//			String cs = sharedProgramInstanceData.dumpAll();
//			sbText.append(cs);
//		}
//		else
//			sbText.append("\r\nERROR: sharedProgramInstanceData == null !!!\r\n");
//		
//		String csText = sbText.toString(); 
//		BaseProgramLoader.logMail(csTitle, csText);
//	}

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
		return VarType.VarEditInMapRedefine;
	}
}
