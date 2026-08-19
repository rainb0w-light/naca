/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author PJD
 *
*/

package nacaLib.varEx;

import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.program.CopyReplacing;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;


/** Provides var declaration behavior. */
public class VarDeclaration extends ParamDeclaration
{
    private VarLevel varLevel = null;

    /** Creates a new var declaration instance. */
    public VarDeclaration(BaseProgram prg)
    {
        super(prg);
    }

    /** Creates a new var declaration instance. */
    public VarDeclaration(BaseProgram prg, CopyReplacing copyReplacing)
    {
        super(prg);
        this.copyReplacing = copyReplacing;
    }

    /** Executes the level operation. */
    public VarLevel level(int nLevel)
    {
        short level = (short)nLevel;
        if(level != 77) // Level 77 is assimiled to a level 1, but cannot be a parent
        {
            if (copyReplacing != null) {
                level = (short) copyReplacing.getReplacedLevel(nLevel);
            }
        }

        if (level == 1) {
            program.getProgramManager().checkWorkingStorageSection();
        }

        if(level == 1 || level == 77)
        {
            program.getProgramManager().setCurrentMapRedefine(null);
        }

        return varLevel(level);
    }

    /** Executes the variable operation. */
    public VarLevel variable()
    {
        return varLevel(77);
    }

    private VarLevel varLevel(int nLevel)
    {
        TempCache tempCache = TempCacheLocator.getTLSTempCache();
        VarLevel varLevel = tempCache.getVarLevel();
        varLevel.set(program, nLevel);
        return varLevel;
    }

    /** Executes the index operation. */
    public Var index()
    {
        return new VarInternalInt();
    }

    /** Executes the bool operation. */
    public Var bool()
    {
        return new VarInternalBool();
    }



    /** Executes the condition operation. */
    public DeclareTypeCond condition()
    {
        DeclareTypeCond declareTypeCond = TempCacheLocator.getTLSTempCache().getDeclareTypeCond();
        declareTypeCond.set(program);
        return declareTypeCond;
    }



    /** Executes the replacing operation. */
    public CopyReplacing replacing(int nOld, int nNew)
    {
        CopyReplacing copyReplacing = new CopyReplacing(nOld, nNew);
        return copyReplacing;
    }

    private CopyReplacing copyReplacing = null;
}
