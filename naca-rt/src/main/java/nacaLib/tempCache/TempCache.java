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
import nacaLib.varEx.CoupleVar;
import nacaLib.varEx.DeclareType9;
import nacaLib.varEx.DeclareTypeCond;
import nacaLib.varEx.DeclareTypeEditInMap;
import nacaLib.varEx.DeclareTypeEditInMapRedefine;
import nacaLib.varEx.DeclareTypeEditInMapRedefineNum;
import nacaLib.varEx.DeclareTypeEditInMapRedefineNumEdited;
import nacaLib.varEx.DeclareTypeFPacSignComp4;
import nacaLib.varEx.DeclareTypeFPacSignIntComp3;
import nacaLib.varEx.DeclareTypeForm;
import nacaLib.varEx.DeclareTypeG;
import nacaLib.varEx.DeclareTypeMapRedefine;
import nacaLib.varEx.DeclareTypeNumEdited;
import nacaLib.varEx.DeclareTypeX;
import nacaLib.varEx.InitializeManagerDouble;
import nacaLib.varEx.InitializeManagerDoubleEdited;
import nacaLib.varEx.InitializeManagerInt;
import nacaLib.varEx.InitializeManagerIntEdited;
import nacaLib.varEx.InitializeManagerLowValue;
import nacaLib.varEx.InitializeManagerNone;
import nacaLib.varEx.InitializeManagerString;
import nacaLib.varEx.InitializeManagerStringEdited;
import nacaLib.varEx.VarBase;
import nacaLib.varEx.VarDefBuffer;
import nacaLib.varEx.VarLevel;
import nacaLib.varEx.VarTypeId;



/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: TempCache.java,v 1.16 2007/03/12 16:55:57 u930di Exp $
 */
public class TempCache
{
    private TempVarManager tempVarManager = null;
    private CStrManager manager = null;
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


    // increment current only every 1000 getAlomostCurrentTime() request
    private static final int INC_ALMOST_CURRENT_TIME_PERIOD = 10000;
    private int nCurrentTimeTryCounter = INC_ALMOST_CURRENT_TIME_PERIOD;

    TempCache()
    {
        tempVarManager = new TempVarManager(VarTypeId.NbTotalVarEditTypes);
        manager = new CStrManager();
        varLevel = new VarLevel();
        declareTypeX = new DeclareTypeX();
    }

    /** Returns the temp var. */
    public CoupleVar getTempVar(int nVarDefTypeId)
    {
        return tempVarManager.getTempCouple(nVarDefTypeId);
    }

    /** Adds the temp var. */
    public CoupleVar addTempVar(int nVarDefTypeId, VarDefBuffer varDefItem, VarBase var)
    {
        setUseTempVar();
        return tempVarManager.addTemp(nVarDefTypeId, varDefItem, var);
    }

    /** Resets the cstr. */
    public void resetCStr()
    {
        manager.reset();
    }

    /** Executes the rewind cstr mapped operation. */
    public void rewindCStrMapped(int n)
    {
        manager.rewindCStrMapped(n);
    }

    /** Resets the temp var index. */
    public void resetTempVarIndex(int nVarTypeId)
    {
        if (getAndResetUseTempVar()) {
            tempVarManager.resetTempIndex(nVarTypeId);
        }
        if (getAndResetUseCStr()) {
            manager.reset();
        }
        if (nCurrentTimeTryCounter-- <= 0) {
            breakCurrentSessionIfTimeout();
        }
    }

    /** Resets the temp var index and forbid reuse. */
    public void resetTempVarIndexAndForbidReuse(VarBase varA)
    {
        if (getAndResetUseTempVar()) {
            tempVarManager.resetTempIndexAndForbidReuse(varA.varTypeId);
        }
        if (getAndResetUseCStr()) {
            manager.reset();
        }
        if (nCurrentTimeTryCounter-- <= 0) {
            breakCurrentSessionIfTimeout();
        }
    }

    /** Resets the temp index. */
    public void resetTempIndex(VarBase... vars)
    {
        if(getAndResetUseTempVar())
        {
            for(VarBase var : vars)
            {
                tempVarManager.resetTempIndex(var.varTypeId);
            }
        }
        if (getAndResetUseCStr()) {
            manager.reset();
        }
        if (nCurrentTimeTryCounter-- <= 0) {
            breakCurrentSessionIfTimeout();
        }
    }

    private void breakCurrentSessionIfTimeout()
    {
        nCurrentTimeTryCounter = INC_ALMOST_CURRENT_TIME_PERIOD;
        if(currentBaseProgramManager != null)
        {
            BaseEnvironment env = currentBaseProgramManager.getEnv();
            if (env != null) {
                env.breakCurrentSessionIfTimeout();
            }
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

    /** Returns the declare type9. */
    public DeclareType9 getDeclareType9()
    {
        if (declareType9 == null) {
            declareType9 = new DeclareType9();
        }
        return declareType9;
    }

    /** Returns the declare type edit in map. */
    public DeclareTypeEditInMap getDeclareTypeEditInMap()
    {
        if (declareTypeEditInMap == null) {
            declareTypeEditInMap = new DeclareTypeEditInMap();
        }
        return declareTypeEditInMap;
    }

    /** Returns the declare type edit in map redefine. */
    public DeclareTypeEditInMapRedefine getDeclareTypeEditInMapRedefine()
    {
        if (declareTypeEditInMapRedefine == null) {
            declareTypeEditInMapRedefine = new DeclareTypeEditInMapRedefine();
        }
        return declareTypeEditInMapRedefine;
    }

    /** Returns the declare type edit in map redefine num. */
    public DeclareTypeEditInMapRedefineNum getDeclareTypeEditInMapRedefineNum()
    {
        if (declareTypeEditInMapRedefineNum == null) {
            declareTypeEditInMapRedefineNum = new DeclareTypeEditInMapRedefineNum();
        }
        return declareTypeEditInMapRedefineNum;
    }

    /** Returns the declare type edit in map redefine num edited. */
    public DeclareTypeEditInMapRedefineNumEdited getDeclareTypeEditInMapRedefineNumEdited()
    {
        if (declareTypeEditInMapRedefineNumEdited == null) {
            declareTypeEditInMapRedefineNumEdited = new DeclareTypeEditInMapRedefineNumEdited();
        }
        return declareTypeEditInMapRedefineNumEdited;
    }

    /** Returns the declare type form. */
    public DeclareTypeForm getDeclareTypeForm()
    {
        if (declareTypeForm == null) {
            declareTypeForm = new DeclareTypeForm();
        }
        return declareTypeForm;
    }

    /** Returns the declare type fpac sign comp4. */
    public DeclareTypeFPacSignComp4 getDeclareTypeFPacSignComp4()
    {
        if (declareTypeFPacSignComp4 == null) {
            declareTypeFPacSignComp4 = new DeclareTypeFPacSignComp4();
        }
        return declareTypeFPacSignComp4;
    }

    /** Returns the declare type fpac sign int comp3. */
    public DeclareTypeFPacSignIntComp3 getDeclareTypeFPacSignIntComp3()
    {
        if (declareTypeFPacSignIntComp3 == null) {
            declareTypeFPacSignIntComp3 = new DeclareTypeFPacSignIntComp3();
        }
        return declareTypeFPacSignIntComp3;
    }

    /** Returns the declare type g. */
    public DeclareTypeG getDeclareTypeG()
    {
        if (declareTypeG == null) {
            declareTypeG = new DeclareTypeG();
        }
        return declareTypeG;
    }

    /** Returns the declare type map redefine. */
    public DeclareTypeMapRedefine getDeclareTypeMapRedefine()
    {
        if (declareTypeMapRedefine == null) {
            declareTypeMapRedefine = new DeclareTypeMapRedefine();
        }
        return declareTypeMapRedefine;
    }

    /** Returns the declare type num edited. */
    public DeclareTypeNumEdited getDeclareTypeNumEdited()
    {
        if (declareTypeNumEdited == null) {
            declareTypeNumEdited = new DeclareTypeNumEdited();
        }
        return declareTypeNumEdited;
    }

    /** Returns the declare type cond. */
    public DeclareTypeCond getDeclareTypeCond()
    {
        if (declareTypeCond == null) {
            declareTypeCond = new DeclareTypeCond();
        }
        return declareTypeCond;
    }

    /** Returns the initialize manager none. */
    public InitializeManagerNone getInitializeManagerNone()
    {
        if (initializeManagerNone == null) {
            initializeManagerNone = new InitializeManagerNone();
        }
        return initializeManagerNone;
    }

    /** Returns the initialize manager int. */
    public InitializeManagerInt getInitializeManagerInt(int n)
    {
        if (initializeManagerInt == null) {
            initializeManagerInt = new InitializeManagerInt(n);
        } else {
            initializeManagerInt.set(n);
        }
        return initializeManagerInt;
    }

    /** Returns the initialize manager double. */
    public InitializeManagerDouble getInitializeManagerDouble(String cs)
    {
        if (initializeManagerDouble == null) {
            initializeManagerDouble = new InitializeManagerDouble(cs);
        } else {
            initializeManagerDouble.set(cs);
        }
        return initializeManagerDouble;
    }

    /** Returns the initialize manager string. */
    public InitializeManagerString getInitializeManagerString(String cs)
    {
        if (initializeManagerString == null) {
            initializeManagerString = new InitializeManagerString(cs);
        } else {
            initializeManagerString.set(cs);
        }
        return initializeManagerString;
    }

    /** Returns the initialize manager int edited. */
    public InitializeManagerIntEdited getInitializeManagerIntEdited(int n)
    {
        if (initializeManagerIntEdited == null) {
            initializeManagerIntEdited = new InitializeManagerIntEdited(n);
        } else {
            initializeManagerIntEdited.set(n);
        }
        return initializeManagerIntEdited;
    }

    /** Returns the initialize manager double edited. */
    public InitializeManagerDoubleEdited getInitializeManagerDoubleEdited(double d)
    {
        if (initializeManagerDoubleEdited == null) {
            initializeManagerDoubleEdited = new InitializeManagerDoubleEdited(d);
        } else {
            initializeManagerDoubleEdited.set(d);
        }
        return initializeManagerDoubleEdited;
    }

    /** Returns the initialize manager string edited. */
    public InitializeManagerStringEdited getInitializeManagerStringEdited()
    {
        if (initializeManagerStringEdited == null) {
            initializeManagerStringEdited = new InitializeManagerStringEdited();
        }
        return initializeManagerStringEdited;
    }

    /** Returns the initialize manager low value. */
    public InitializeManagerLowValue getInitializeManagerLowValue()
    {
        if (initializeManagerLowValue == null) {
            initializeManagerLowValue = new InitializeManagerLowValue();
        }
        return initializeManagerLowValue;
    }

    /** Returns the shared program instance data. */
    public SharedProgramInstanceData getSharedProgramInstanceData()
    {
        BaseProgramManager pm = getProgramManager();
        if (pm != null) {
            return pm.getSharedProgramInstanceData();
        }
        return null;
    }

    public BaseProgramManager getProgramManager()
    {
        //BaseProgram prg = m_stackPrograms.peek();
        //return prg.getProgramManager();
        return currentBaseProgramManager;
    }

    /** Executes the pop current program operation. */
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
                if (currentBaseProgramManager != null) {
                    currentBaseProgram = currentBaseProgramManager.getProgram();
                } else {
                    currentBaseProgram = null;
                }

            }
        }
        return prg;
    }

    /** Executes the push current program operation. */
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

//  public void setCurrentBaseProgramManagerForPreloadOnly(BaseProgramManager baseProgramManager)
//  {
//      currentBaseProgramManager = baseProgramManager;
//      if(currentBaseProgramManager != null)
//          currentBaseProgram = currentBaseProgramManager.getProgram();
//      else
//          currentBaseProgram = null;
//  }

    /** Resets the stack program. */
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

    /** Returns the reusable cstr. */
    public CStr getReusableCStr()
    {
        setUseCStr();
        return manager.getReusable();
    }

    /** Returns the mapped cstr. */
    public CStr getMappedCStr()
    {
        setUseCStr();
        return manager.getMapped();
    }

    /** Returns the cstr number. */
    public CStrNumber getCStrNumber()
    {
        setUseCStr();
        return manager.getNumber();
    }

    /** Returns the cstr string. */
    public CStrString getCStrString()
    {
        setUseCStr();
        return manager.getString();
    }

    public BtreeKeyDescription getBtreeKeyDescription()
    {
        return btreeKeyDescription;
    }

    public void setBtreeKeyDescription(BtreeKeyDescription btreeKeyDescription)
    {
        this.btreeKeyDescription = btreeKeyDescription;
    }

    public void setCurrentEnv(BaseEnvironment env)
    {
        this.env = env;
    }

    public BaseEnvironment getCurrentEnv()
    {
        return env;
    }

    /** Sets the use temp var. */
    public void setUseTempVar()
    {
        if(!isusedTempVar)
        {
            isusedTempVar = true;
            if (currentBaseProgram != null) {
                currentBaseProgram.setUseTempVar();
            }
        }
    }

    /** Sets the use cstr. */
    public void setUseCStr()
    {
        if(!isusedCStr)
        {
            isusedCStr = true;
            if (currentBaseProgram != null) {
                currentBaseProgram.setUseCStr();
            }
        }
    }

    /** Returns the and reset use temp var. */
    public boolean getAndResetUseTempVar()
    {
        if(isusedTempVar)
        {
            isusedTempVar = false;
            if (currentBaseProgram != null) {
                currentBaseProgram.resetUseTempVar();
            }
            return true;
        }
        return false;
    }

    /** Returns the and reset use cstr. */
    public boolean getAndResetUseCStr()
    {
        if(isusedCStr)
        {
            isusedCStr = false;
            if (currentBaseProgram != null) {
                currentBaseProgram.resetUseCStr();
            }
            return true;
        }
        return false;
    }

    public String getLastSQLCodeErrorText()
    {
        return csLastSQLCodeErrorText;
    }

    /** Executes the fill last sqlcode error text operation. */
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
