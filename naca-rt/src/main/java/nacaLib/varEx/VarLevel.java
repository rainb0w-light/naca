/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author U930DI
 *
 */
package nacaLib.varEx;

import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;

/** Provides var level behavior. */
public class VarLevel extends CJMapObject
{
    private CInitialValue initialValue = null;
    private boolean isjustifyRight = false;
    private VarDefBase varDefRedefineOrigin = null;
    private OccursDefBase occursDef = null;
    private BaseProgram program = null;
    private short level = 0;
    private boolean isvariableLength = false;

    /** Creates a new var level instance. */
    public VarLevel()
    {
    }

    /** Executes the set operation. */
    public void set(BaseProgram program, int nLevel)
    {
        initialValue = null;
        isjustifyRight = false;
        varDefRedefineOrigin = null;
        occursDef = null;

        this.program = program;
        level = (short)nLevel;
        isvariableLength = false;
    }

    /** Executes the var operation. */
    public VarGroup var()   // Creates a group
    {
        DeclareTypeG declareTypeG = TempCacheLocator.getTLSTempCache().getDeclareTypeG();
        declareTypeG.set(this);
        if (isvariableLength) {
            declareTypeG.setVariableLengthDeclaration();
        }
        VarGroup var2G = new VarGroup(declareTypeG);
        return var2G;
    }

    /** Executes the filler operation. */
    public Var filler()
    {
        DeclareTypeG declareTypeG = TempCacheLocator.getTLSTempCache().getDeclareTypeG();
        declareTypeG.set(this);
        VarGroup var2G = new VarGroup(declareTypeG);
        var2G.declareAsFiller();
        return var2G;
    }

    /** Executes the pic x operation. */
    public DeclareTypeX picX()
    {
        return picX(1);
    }

    /** Executes the comp1 operation. */
    public DeclareTypeX comp1()
    {
        // PJD to be implemented...
        return null;
    }

    /** Executes the comp2 operation. */
    public DeclareTypeX comp2()
    {
        // PJD to be implemented...
        return null;
    }

    /** Executes the pic x operation. */
    public DeclareTypeX picX(int nLength)
    {
        DeclareTypeX declareTypeX = TempCacheLocator.getTLSTempCache().getDeclareTypeX();
        declareTypeX.set(this, nLength);
        if (isvariableLength) {
            declareTypeX.setVariableLengthDeclaration();
        }

        //DeclareTypeX varLevelX = new DeclareTypeX(this, nLength);
        return declareTypeX;
    }

    /** Executes the pic operation. */
    public DeclareTypeNumEdited pic(String csFormat)
    {
        // Should identify either pic9(csFormat) or picX(csFormat);
        return pic9(csFormat);
    }

    /** Executes the pic9 operation. */
    public DeclareType9 pic9(int nNbDigitInteger)
    {
        return pic9Define(false, nNbDigitInteger, 0);
    }

    /** Executes the pic9 operation. */
    public DeclareType9 pic9(int nNbDigitInteger, int nNbDigitDecimal)
    {
        return pic9Define(false, nNbDigitInteger, nNbDigitDecimal);
    }

    /** Executes the pic s9 operation. */
    public DeclareType9 picS9(int nNbDigitInteger)
    {
        return pic9Define(true, nNbDigitInteger, 0);
    }

    /** Executes the pic s9 operation. */
    public DeclareType9 picS9(int nNbDigitInteger, int nNbDigitDecimal)
    {
        return pic9Define(true, nNbDigitInteger, nNbDigitDecimal);
    }

    private DeclareType9 pic9Define(boolean bSigned, int nNbDigitInteger, int nNbDigitDecimal)
    {
        DeclareType9 declareType9 = TempCacheLocator.getTLSTempCache().getDeclareType9();
        declareType9.set(this, bSigned, nNbDigitInteger, nNbDigitDecimal);
        //DeclareType9 varLevel9 = new DeclareType9(this, bSigned, nNbDigitInteger, nNbDigitDecimal);
        return declareType9;
    }

    /** Executes the redefines operation. */
    public VarLevel redefines(Edit varEditRedefineOrigin)
    {
        varDefRedefineOrigin = varEditRedefineOrigin.getVarDef();
        return this;
    }

    /** Executes the redefines operation. */
    public VarLevel redefines(Var varRedefineOrigin)
    {
        varDefRedefineOrigin = varRedefineOrigin.getVarDef();
        return this;
    }

    /** Executes the occurs operation. */
    public VarLevel occurs(int nNbOccurs)
    {
        BaseProgramManager pm = getProgramManager();
        if (pm.isFirstInstance()) {    // || pm.isLinkageSectionCurrent())
            occursDef = new OccursDef(nNbOccurs);
        }
        return this;
    }

    /** Executes the occurs operation. */
    public VarLevel occurs(Var varOccurs)
    {
        BaseProgramManager pm = getProgramManager();
        if (pm.isFirstInstance()) {    // || pm.isLinkageSectionCurrent())
            occursDef = new OccursDefVar(varOccurs);
        }
        return this;
    }

    /** Executes the occurs depending operation. */
    public VarLevel occursDepending(int nNbOccurs, Var varOccurs)
    {
        BaseProgramManager pm = getProgramManager();
        if(pm.isFirstInstance())    // || pm.isLinkageSectionCurrent())
        {
            if (varOccurs.isBufferComputed()) {
                occurs(varOccurs);
            } else {
                occurs(nNbOccurs);
            }
        }
        isvariableLength = true;
        return this;
    }

    /** Executes the occurs depending record operation. */
    public VarLevel occursDependingRecord(int nNbOccurs, Var varOccurs)
    {
        BaseProgramManager pm = getProgramManager();
        if(pm.isFirstInstance())
        {
            occursDef = new OccursDefRecordDependingVar(nNbOccurs, varOccurs);
        }
        return this;
    }

//  private String removeCharAtPos(String csFormat, int n)
//  {
//      int nLg = csFormat.length();
//      csFormat = csFormat.substring(0, n) + csFormat.substring(n+1, nLg-1);   // Remove the char
//      return csFormat;
//  }

    /** Executes the pic9 operation. */
    public DeclareTypeNumEdited pic9(String csFormat)
    {
        DeclareTypeNumEdited declareTypeNumEdited = TempCacheLocator.getTLSTempCache().getDeclareTypeNumEdited();
        declareTypeNumEdited.set(this, csFormat);
        return declareTypeNumEdited;
    }

    // Screen resource management: No .var() to add
    // Map redefine management
    /** Executes the redefines map operation. */
    public MapRedefine redefinesMap(Form formRedefineOrigin)
    {
        DeclareTypeMapRedefine declareTypeMapRedefine = TempCacheLocator.getTLSTempCache().getDeclareTypeMapRedefine();
        declareTypeMapRedefine.set(this, formRedefineOrigin);

        MapRedefine var2MapRedefine = new MapRedefine(declareTypeMapRedefine);
        return var2MapRedefine;
    }

    /** Executes the justify right operation. */
    public VarLevel justifyRight()  // Edit in a map redefine
    {
        isjustifyRight = true;
        return this;
    }

    boolean getJustifyRight()
    {
        return isjustifyRight;
    }


    /** Executes the edit operation. */
    public Edit edit()  // Edit in a map redefine
    {
        TempCache tempCache = TempCacheLocator.getTLSTempCache();
        DeclareTypeEditInMapRedefine declareTypeEditInMapRedefine = tempCache.getDeclareTypeEditInMapRedefine();
        declareTypeEditInMapRedefine.set(this);
        EditInMapRedefine var2Edit = new EditInMapRedefine(declareTypeEditInMapRedefine);
        return var2Edit;
    }

    /** Executes the edit skip operation. */
    public Edit editSkip()
    {
        return editSkip(1);
    }

    /** Executes the edit skip operation. */
    public Edit editSkip(int nNbItemToSkip)
    {
        for(int n=0; n<nNbItemToSkip; n++)
        {
            edit();
        }
        return null;
    }

    /** Executes the edit occurs operation. */
    public Edit editOccurs(int nNbOccurs, String csName)
    {
        // remonter au dernier precedent de nivwau >= niveau courant
        // si c'est un edit occurs; il faut completer son tableau d'items
        BaseProgramManager pm = getProgramManager();

        if (pm.isFirstInstance()) {
            occursDef = new OccursDef(nNbOccurs);
        }

        Edit varEdit = edit();

        if(pm.isFirstInstance())
        {
            this.getProgramManager().getSharedProgramInstanceData().setVarFullName(varEdit.getVarDef().getId(), csName);
            //varEdit.varDef.setFullName(csName);
        }

        return varEdit;
    }

    BaseProgramManager getProgramManager()
    {
        return program.getProgramManager();
    }

    BaseProgram getProgram()
    {
        return program;
    }

    public short getLevel()
    {
        return level;
    }

    public VarDefBase getVarDefRedefineOrigin()
    {
        return varDefRedefineOrigin;
    }

    public OccursDefBase getOccursDef()
    {
        return occursDef;
    }

    /** Executes the value operation. */
    public VarLevelGroup value(String cs)
    {
        BaseProgramManager pm = getProgramManager();
        if (pm.isFirstInstance()) {
            initialValue = new CInitialValue(cs, false);
        }
        VarLevelGroup varLevelGroup = new VarLevelGroup(this);
        return varLevelGroup;
    }

    /** Executes the value all operation. */
    public VarLevelGroup valueAll(char c)
    {
        BaseProgramManager pm = getProgramManager();
        if (pm.isFirstInstance()) {
            initialValue = new CInitialValue(c, true);
        }
        VarLevelGroup varLevelGroup = new VarLevelGroup(this);
        return varLevelGroup;
    }

    /** Executes the value all operation. */
    public VarLevelGroup valueAll(String cs)
    {
        BaseProgramManager pm = getProgramManager();
        if (pm.isFirstInstance()) {
            initialValue = new CInitialValue(cs, true);
        }
        VarLevelGroup varLevelGroup = new VarLevelGroup(this);
        return varLevelGroup;
    }

    /** Executes the value spaces operation. */
    public VarLevelGroup valueSpaces()
    {
        BaseProgramManager pm = getProgramManager();
        if (pm.isFirstInstance()) {
            initialValue = new CInitialValue(CobolConstant.Space.getValue(), true);
        }
        VarLevelGroup varLevelGroup = new VarLevelGroup(this);
        return varLevelGroup;
    }

    /** Executes the value zero operation. */
    public VarLevelGroup valueZero()
    {
        BaseProgramManager pm = getProgramManager();
        if (pm.isFirstInstance()) {
            initialValue = new CInitialValue(CobolConstant.Zero.getValue(), true);
        }
        VarLevelGroup varLevelGroup = new VarLevelGroup(this);
        return varLevelGroup;
    }

    /** Executes the value high value operation. */
    public VarLevelGroup valueHighValue()
    {
        BaseProgramManager pm = getProgramManager();
        if (pm.isFirstInstance()) {
            initialValue = new CInitialValue(CobolConstant.HighValue.getValue(), true);
        }
        VarLevelGroup varLevelGroup = new VarLevelGroup(this);
        return varLevelGroup;
    }

    /** Executes the value low value operation. */
    public VarLevelGroup valueLowValue()
    {
        BaseProgramManager pm = getProgramManager();
        if (pm.isFirstInstance()) {
            initialValue = new CInitialValue(CobolConstant.LowValue.getValue(), true);
        }
        VarLevelGroup varLevelGroup = new VarLevelGroup(this);
        return varLevelGroup;
    }

    CInitialValue getInitialValue()
    {
        return initialValue;
    }

    /** Executes the variable length operation. */
    public VarLevel variableLength()
    {
        isvariableLength = true;
        return this;
    }
}
