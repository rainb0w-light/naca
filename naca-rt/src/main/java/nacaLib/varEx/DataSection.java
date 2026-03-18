/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import java.util.ArrayList;

import jlib.log.Log;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

public class DataSection extends CJMapObject
{	
	private DataSectionType dataSectionType = null;
	
	public DataSection(BaseProgram prg, DataSectionType dataSectionType)
	{
		this.dataSectionType = dataSectionType;
		this.prg = prg;
		buffer = new VarBuffer();
	}
	
	public void createRootVarOfSection()
	{
		if(dataSectionType == DataSectionType.Working)
			createRootVar(prg, "WS");
		else if(dataSectionType == DataSectionType.Linkage)
			createRootVar(prg, "LS");
		else if(dataSectionType == DataSectionType.File)
			createRootVar(prg, "File");
		pushLevel(rootVar.getVarDef());
	}
	
	private void createRootVar(BaseProgram prg, String csSuffix)
	{
		BaseProgramManager pm = prg.getProgramManager();
		TempCache tempCache = TempCacheLocator.getTLSTempCache();
		VarLevel varlevel = tempCache.getVarLevel();
		varlevel.set(prg, (short)0);
		
		DeclareTypeG declareTypeG = tempCache.getDeclareTypeG();
		declareTypeG.set(varlevel);
		
		rootVar = new VarGroup(declareTypeG);
		if(rootVar.varDef != null)
		{
			pm.getSharedProgramInstanceData().setVarFullName(rootVar.getVarDef().getId(), "%InternalRoot_" + csSuffix + "%");
		}
	}

	
	public BaseProgram getProgram()
	{
		return prg;
	}
	
	public void pushLevel(VarDefBuffer varDef)
	{
		CLevel level = new CLevel(varDef, varDef.getLevel());
		if(stackLevel == null)
			stackLevel = new StackLevel(); 
		stackLevel.push(level);
	}
	
	public VarDefBuffer getVarDefAtParentLevel(int nLevel)
	{
		VarDefBuffer varDefParent = null;
		if(nLevel == 77)	// Level 77 do not change the stack, but are parented by the root var
			varDefParent = rootVar.getVarDef();
		else if(stackLevel != null)
		{
			CLevel level = stackLevel.getParentLevel(nLevel);
			if(level != null)
				varDefParent = level.getVarDef();
		}
		return varDefParent;
	}

	private StackLevel stackLevel = null;
	
	Var getRootVar()
	{
		return rootVar;
	}
	
	public VarBuffer computeStorage(boolean bFirstInstance)
	{
		stackLevel = null;
		
		SharedProgramInstanceData sharedProgramInstanceData = prg.getProgramManager().getSharedProgramInstanceData();
		int nBufferSize = 0;
		if(rootVar != null)
		{
			if(bFirstInstance)	// The var defs of the catalog have not been already computed: 1st absolute run; the 2nd, 3rd... run of a program alreday loaded have access to the catalog of varDef: No need to recompute var def again.
			{
				VarDefBuffer varDefBuffer = rootVar.getVarDef();
				
				varDefBuffer.assignEditInMapRedefine();
				nBufferSize = varDefBuffer.calcSize();
				varDefBuffer.calcPositionsIntoBuffer(sharedProgramInstanceData);	// No var used in map redefines
				varDefBuffer.calcOccursOwners();
			}
			else
				nBufferSize = rootVar.getTotalSize();
		}
		
		buffer.allocBufferStorage(nBufferSize);
		return buffer;
	}
		
	public void fillWorkingInitialValues(SharedProgramInstanceData sharedProgramInstanceData)
	{			
		TempCache cache = TempCacheLocator.getTLSTempCache();
		if(cache != null && rootVar != null)
			rootVar.getVarDef().fillInitialValueAndClearUnusedMembers(cache, sharedProgramInstanceData, buffer);
	}
	
	public void dumpRootVar(String csSectionName)
	{
		if(IsSTCheck)
		{
			if(rootVar != null)
			{
				Log.logFineDebug("dumpSTCheck:" + this.prg.getSimpleName() + " " + csSectionName);
				rootVar.getVarDef().dumpToSTCheck(prg.getProgramManager());
			}
		}
	}
	
	public void mapCallParameters(ArrayList<CCallParam> arrCallerCallParam, ArrayList<Var> arrDeclaredCallArg)
	{		
		if(arrDeclaredCallArg != null)
		{
			for(int n=0; n<arrDeclaredCallArg.size(); n++)
			{
				Var varLinkageSection = getDeclaredCallArgAtIndex(arrDeclaredCallArg, n);
				varLinkageSection.fill(CobolConstant.LowValue);
			}
		}
			 
		if(arrCallerCallParam != null && arrDeclaredCallArg != null)
		{
			int nNbArg = arrCallerCallParam.size();
			for(int nArg=0; nArg<nNbArg; nArg++)
			{
				CCallParam callParam = (CCallParam) arrCallerCallParam.get(nArg);
				 
				Var varLinkageSection = getDeclaredCallArgAtIndex(arrDeclaredCallArg, nArg);
				if(varLinkageSection != null)
				{
					callParam.MapOn(varLinkageSection);
				}
			}
		}
	}

	private Var getDeclaredCallArgAtIndex(ArrayList arrDeclaredCallArg, int nIndex)
	{
		if(nIndex >= 0 && nIndex < arrDeclaredCallArg.size())
			return (Var)arrDeclaredCallArg.get(nIndex);
		return null;		
	}
	
	protected BaseProgram prg = null;
	public Var rootVar = null;
	public VarBuffer buffer = null;
}
