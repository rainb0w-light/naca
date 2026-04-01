/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.tempCache;

import java.util.Stack;


import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.bdb.BtreeKeyDescription;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.sqlSupport.CSQLStatus;
import nacaLib.varEx.*;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: TempCache.java,v 1.16 2007/03/12 16:55:57 u930di Exp $
 */
public class TempCache
{
	private TempVarManager tempVarManager = null;
	private CStrManager strManager = null;
	private VarLevel varLevel = null;
	private DeclareTypeX declareTypeX = null;
	private DeclareType9 declareType9 = null;
	private DeclareTypeEditInMap declareTypeEditInMap = null;
	private DeclareTypeEditInMapRedefine declareTypeEditInMapRedefine = null;
	private DeclareTypeEditInMapRedefineNum declareTypeEditInMapRedefineNum = null;
	private DeclareTypeEditInMapRedefineNumEdited declareTypeEditInMapRedefineNumEdited = null;
	private DeclareTypeForm declareTypeForm = null;
	private DeclareTypeFPacSignComp4 declareTypeFPacSignComp4 = null;
	private DeclareTypeFPacSignIntComp3 declareTypeFPacSignIntComp3 = null;
	private DeclareTypeG declareTypeG = null;
	private DeclareTypeMapRedefine declareTypeMapRedefine = null;
	private DeclareTypeNumEdited declareTypeNumEdited = null;
	private DeclareTypeCond declareTypeCond = null;
	private InitializeManagerNone initializeManagerNone = null;
	private InitializeManagerInt initializeManagerInt = null;
	private InitializeManagerDouble initializeManagerDouble = null;
	private InitializeManagerString initializeManagerString = null;
	private InitializeManagerIntEdited initializeManagerIntEdited = null;
	private InitializeManagerDoubleEdited initializeManagerDoubleEdited = null;
	private InitializeManagerStringEdited initializeManagerStringEdited = null;
	private InitializeManagerLowValue initializeManagerLowValue = null;
	//private BaseProgramManager programManager = null; 
	private Stack<BaseProgram> stackPrograms = new Stack<BaseProgram>(); 
	private BtreeKeyDescription btreeKeyDescription = null;
	private BaseEnvironment env = null;	
	private String csLastSQLCodeErrorText = null;
	private BaseProgramManager currentBaseProgramManager = null;
	private BaseProgram currentBaseProgram = null;

	
	private static final int INC_ALMOST_CURRENT_TIME_PERIOD = 10000;	// increment current only every 1000 getAlomostCurrentTime() request
	private int nCurrentTimeTryCounter = INC_ALMOST_CURRENT_TIME_PERIOD;
	
	TempCache()
	{
		tempVarManager = new TempVarManager(VarTypeId.NbTotalVarEditTypes);
		strManager = new CStrManager();
		varLevel = new VarLevel();
		declareTypeX = new DeclareTypeX();
	}
	
	public CoupleVar getTempVar(int nVarDefTypeId)
	{
		return tempVarManager.getTempCouple(nVarDefTypeId);
	}
	
	public CoupleVar addTempVar(int nVarDefTypeId, VarDefBuffer varDefItem, VarBase var)
	{
		setUseTempVar();
		return tempVarManager.addTemp(nVarDefTypeId, varDefItem, var);
	}
	
	public void resetCStr()
	{
		strManager.reset();
	}

	public void rewindCStrMapped(int n)
	{
		strManager.rewindCStrMapped(n);
	}

	public void resetTempVarIndex(int nVarTypeId)
	{
		if(getAndResetUseTempVar())
			tempVarManager.resetTempIndex(nVarTypeId);
		if(getAndResetUseCStr())
			strManager.reset();
		if(nCurrentTimeTryCounter-- <= 0)
			breakCurrentSessionIfTimeout();
	}

	public void resetTempVarIndexAndForbidReuse(VarBase varA)
	{
		if(getAndResetUseTempVar())
			tempVarManager.resetTempIndexAndForbidReuse(varA.varTypeId);
		if(getAndResetUseCStr())
			strManager.reset();
		if(nCurrentTimeTryCounter-- <= 0)
			breakCurrentSessionIfTimeout();
	}

	public void resetTempIndex(VarBase... vars)
	{
		if(getAndResetUseTempVar())
		{
			for(VarBase var : vars)
			{
				tempVarManager.resetTempIndex(var.varTypeId);
			}
		}
		if(getAndResetUseCStr())
			strManager.reset();
		if(nCurrentTimeTryCounter-- <= 0)
			breakCurrentSessionIfTimeout();
	}
	
	private void breakCurrentSessionIfTimeout()
	{
		nCurrentTimeTryCounter = INC_ALMOST_CURRENT_TIME_PERIOD;
		if(currentBaseProgramManager != null)
		{
			BaseEnvironment env = currentBaseProgramManager.getEnv();				
			if(env != null)
				env.breakCurrentSessionIfTimeout();
		}
	}
	
	public VarLevel getVarLevel()
	{
		return varLevel;
	}
	
	public DeclareTypeX getDeclareTypeX()
	{
		return declareTypeX;
	}
	
	public DeclareType9 getDeclareType9()
	{
		if(declareType9 == null)
			declareType9 = new DeclareType9();
		return declareType9;
	}
	
	public DeclareTypeEditInMap getDeclareTypeEditInMap()
	{
		if(declareTypeEditInMap == null)
			declareTypeEditInMap = new DeclareTypeEditInMap();
		return declareTypeEditInMap;
	}
	
	public DeclareTypeEditInMapRedefine getDeclareTypeEditInMapRedefine()
	{
		if(declareTypeEditInMapRedefine == null)
			declareTypeEditInMapRedefine = new DeclareTypeEditInMapRedefine();
		return declareTypeEditInMapRedefine;
	}
	
	public DeclareTypeEditInMapRedefineNum getDeclareTypeEditInMapRedefineNum()
	{
		if(declareTypeEditInMapRedefineNum == null)
			declareTypeEditInMapRedefineNum = new DeclareTypeEditInMapRedefineNum();
		return declareTypeEditInMapRedefineNum;
	}
	
	public DeclareTypeEditInMapRedefineNumEdited getDeclareTypeEditInMapRedefineNumEdited()
	{
		if(declareTypeEditInMapRedefineNumEdited == null)
			declareTypeEditInMapRedefineNumEdited = new DeclareTypeEditInMapRedefineNumEdited();
		return declareTypeEditInMapRedefineNumEdited;
	}
	
	public DeclareTypeForm getDeclareTypeForm()
	{
		if(declareTypeForm == null)
			declareTypeForm = new DeclareTypeForm();
		return declareTypeForm; 
	}
	
	public DeclareTypeFPacSignComp4 getDeclareTypeFPacSignComp4()
	{
		if(declareTypeFPacSignComp4 == null)
			declareTypeFPacSignComp4 = new DeclareTypeFPacSignComp4();
		return declareTypeFPacSignComp4;
	}

	public DeclareTypeFPacSignIntComp3 getDeclareTypeFPacSignIntComp3()
	{
		if(declareTypeFPacSignIntComp3 == null)
			declareTypeFPacSignIntComp3 = new DeclareTypeFPacSignIntComp3();
		return declareTypeFPacSignIntComp3;
	}
	
	public DeclareTypeG getDeclareTypeG()
	{
		if(declareTypeG == null)
			declareTypeG = new DeclareTypeG();
		return declareTypeG;
	}
	
	public DeclareTypeMapRedefine getDeclareTypeMapRedefine()
	{
		if(declareTypeMapRedefine == null)
			declareTypeMapRedefine = new DeclareTypeMapRedefine();
		return declareTypeMapRedefine;
	}
	
	public DeclareTypeNumEdited getDeclareTypeNumEdited()
	{
		if(declareTypeNumEdited == null)
			declareTypeNumEdited = new DeclareTypeNumEdited();
		return declareTypeNumEdited;
	}
	
	public DeclareTypeCond getDeclareTypeCond()
	{
		if(declareTypeCond == null)
			declareTypeCond = new DeclareTypeCond();
		return declareTypeCond;
	}
	
	public InitializeManagerNone getInitializeManagerNone()
	{
		if(initializeManagerNone == null)
			initializeManagerNone = new InitializeManagerNone();
		return initializeManagerNone;
	}
	
	public InitializeManagerInt getInitializeManagerInt(int n)
	{
		if(initializeManagerInt == null)
			initializeManagerInt = new InitializeManagerInt(n);
		else
			initializeManagerInt.set(n);
		return initializeManagerInt;
	}
	
	public InitializeManagerDouble getInitializeManagerDouble(String cs)
	{
		if(initializeManagerDouble == null)
			initializeManagerDouble = new InitializeManagerDouble(cs);
		else
			initializeManagerDouble.set(cs);
		return initializeManagerDouble;
	}
	
	public InitializeManagerString getInitializeManagerString(String cs)
	{
		if(initializeManagerString == null)
			initializeManagerString = new InitializeManagerString(cs);
		else
			initializeManagerString.set(cs);
		return initializeManagerString;
	}
	
	public InitializeManagerIntEdited getInitializeManagerIntEdited(int n)
	{
		if(initializeManagerIntEdited == null)
			initializeManagerIntEdited = new InitializeManagerIntEdited(n);
		else
			initializeManagerIntEdited.set(n);
		return initializeManagerIntEdited;
	}
	
	public InitializeManagerDoubleEdited getInitializeManagerDoubleEdited(double d)
	{
		if(initializeManagerDoubleEdited == null)
			initializeManagerDoubleEdited = new InitializeManagerDoubleEdited(d);
		else
			initializeManagerDoubleEdited.set(d);		
		return initializeManagerDoubleEdited;
	}
	
	public InitializeManagerStringEdited getInitializeManagerStringEdited()
	{
		if(initializeManagerStringEdited == null)
			initializeManagerStringEdited = new InitializeManagerStringEdited();
		return initializeManagerStringEdited;
	}
	
	public InitializeManagerLowValue getInitializeManagerLowValue()
	{
		if(initializeManagerLowValue == null)
			initializeManagerLowValue = new InitializeManagerLowValue();
		return initializeManagerLowValue;
	}
	
	public SharedProgramInstanceData getSharedProgramInstanceData()
	{
		BaseProgramManager pm = getProgramManager();
		if(pm != null)
			return pm.getSharedProgramInstanceData();
		return null;
	}
	
	public BaseProgramManager getProgramManager()
	{
		//BaseProgram prg = m_stackPrograms.peek();
		//return prg.getProgramManager();
		return currentBaseProgramManager;
	}
	
	public BaseProgram popCurrentProgram()
	{
		BaseProgram prg = null;
		currentBaseProgramManager = null;
		if (!stackPrograms.empty())
		{
			prg = stackPrograms.pop();
			if (!stackPrograms.empty())
			{
				currentBaseProgramManager = stackPrograms.peek().getProgramManager();
				if(currentBaseProgramManager != null)
					currentBaseProgram = currentBaseProgramManager.getProgram();
				else
					currentBaseProgram = null;
				
			}
		}	
		return prg;
	}

	public void pushCurrentProgram(BaseProgram prg)
	{
		if(prg != null)
		{
			stackPrograms.push(prg);
			currentBaseProgramManager = prg.getProgramManager();
			currentBaseProgram = prg;
		}
		else
		{
			currentBaseProgramManager = null;
			currentBaseProgram = null;
		}
	}
	
//	public void setCurrentBaseProgramManagerForPreloadOnly(BaseProgramManager baseProgramManager)
//	{
//		currentBaseProgramManager = baseProgramManager;
//		if(currentBaseProgramManager != null)
//			currentBaseProgram = currentBaseProgramManager.getProgram();
//		else
//			currentBaseProgram = null;
//	}
	
	public void resetStackProgram()
	{	 
		while(!stackPrograms.empty())
		{
			stackPrograms.pop();
		}
		currentBaseProgramManager = null;
		currentBaseProgram = null;
		csLastSQLCodeErrorText = "";
	}

	public CStr getReusableCStr()
	{
		setUseCStr();
		return strManager.getReusable();
	}

	public CStr getMappedCStr()
	{
		setUseCStr();
		return strManager.getMapped();
	}

	public CStrNumber getCStrNumber()
	{
		setUseCStr();
		return strManager.getNumber();
	}

	public CStrString getCStrString()
	{
		setUseCStr();
		return strManager.getString();
	}
	
	public BtreeKeyDescription getBtreeKeyDescription()
	{
		return btreeKeyDescription;
	}
	
	public void setBtreeKeyDescription(BtreeKeyDescription btreeKeyDescription)
	{
		btreeKeyDescription = btreeKeyDescription;
	}
	
	public void setCurrentEnv(BaseEnvironment env)
	{
		env = env;
	}
	
	public BaseEnvironment getCurrentEnv()
	{
		return env;
	}
	
	public void setUseTempVar()
	{
		if(!isusedTempVar)
		{
			isusedTempVar = true;
			if(currentBaseProgram != null)
				currentBaseProgram.setUseTempVar();
		}
	}
	
	public void setUseCStr()
	{
		if(!isusedCStr)
		{
			isusedCStr = true;
			if(currentBaseProgram != null)
				currentBaseProgram.setUseCStr();
		}
	}
	
	public boolean getAndResetUseTempVar()
	{
		if(isusedTempVar)
		{
			isusedTempVar = false;
			if(currentBaseProgram != null)
				currentBaseProgram.resetUseTempVar();
			return true;
		}
		return false;
	}
	
	public boolean getAndResetUseCStr()
	{
		if(isusedCStr)
		{
			isusedCStr = false;
			if(currentBaseProgram != null)
				currentBaseProgram.resetUseCStr();
			return true;
		}
		return false;
	}
	
	public String getLastSQLCodeErrorText()
	{	
		return csLastSQLCodeErrorText;
	}
	
	public void fillLastSQLCodeErrorText(CSQLStatus sqlStatus)
	{
		StringBuffer sb = sqlStatus.getAsStringBuffer();
		sb.append("  | From program=");
		sb.append(currentBaseProgramManager.getProgramName());
		csLastSQLCodeErrorText = sb.toString();
	}
	
	private boolean isusedTempVar = false;
	private boolean isusedCStr = false;
}
