/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.misc.IntegerRef;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.programPool.SharedProgramInstanceData;
import nacaLib.tempCache.CStr;
import nacaLib.tempCache.TempCacheLocator;


/**
 * @author PJD
 *
 */
public abstract class VarAndEdit extends VarBase
{
    /** Creates a new var and edit instance. */
    public VarAndEdit(DeclareTypeBase declareTypeBase)
    {
        super(declareTypeBase);
    }

    protected VarAndEdit()
    {
        super();
    }

    /** Returns the stcheck value. */
    public String getSTCheckValue()
    {
        SharedProgramInstanceData sharedProgramInstanceData = getSharedProgramInstanceData();

        if(bufferPos != null)
        {
            CStr cstr = bufferPos.getOwnCStr(varDef.getLength());
            String csValue = cstr.getAsString();
            String cs = varDef.toDump(sharedProgramInstanceData) + "={\"" + csValue + "\"} ";
            //cstr.resetManagerCache();
            return cs;
        }
        else
        {
            String cs = varDef.toDump(sharedProgramInstanceData) + " NO BUFFER !";
            return cs;
        }
    }

    /** Executes the compare to operation. */
    public abstract int compareTo(ComparisonMode mode, VarAndEdit var2);
    /** Executes the compare to operation. */
    public abstract int compareTo(int nValue);
    /** Executes the compare to operation. */
    public abstract int compareTo(double dValue);
    /** Executes the compare to operation. */
    public abstract int compareTo(ComparisonMode mode, String cs);

    /** Executes the set operation. */
    public abstract void set(CobolConstantZero cst);
    /** Executes the set operation. */
    public abstract void set(CobolConstantSpace cst);
    /** Executes the set operation. */
    public abstract void set(CobolConstantHighValue cst);
    /** Executes the set operation. */
    public abstract void set(CobolConstantLowValue cst);
    /** Sets the string at position. */
    public abstract void setStringAtPosition(String csValue, int nOffsetPosition, int nNbChar);
    /** Sets the and fill. */
    public abstract void setAndFill(String csValue);

    /** Executes the digits operation. */
    public String digits()
    {
        return varDef.digits(bufferPos);
    }

    /** Sets the repeating char at offset from start. */
    public void setRepeatingCharAtOffsetFromStart(CobolConstantZero cst, int nOffsetPosition, int nNbChar)  // Fill with a 0 base index)
    {
        varDef.write(bufferPos, cst, nOffsetPosition, nNbChar);
    }

    /** Sets the repeating char at offset from start. */
    public void setRepeatingCharAtOffsetFromStart(CobolConstantSpace cst, int nOffsetPosition, int nNbChar)
    {
        varDef.write(bufferPos, cst, nOffsetPosition, nNbChar);
    }

    /** Sets the repeating char at offset from start. */
    public void setRepeatingCharAtOffsetFromStart(CobolConstantHighValue cst, int nOffsetPosition, int nNbChar)
    {
        varDef.write(bufferPos, cst, nOffsetPosition, nNbChar);
    }

    /** Sets the repeating char at offset from start. */
    public void setRepeatingCharAtOffsetFromStart(CobolConstantLowValue cst, int nOffsetPosition, int nNbChar)
    {
        varDef.write(bufferPos, cst, nOffsetPosition, nNbChar);
    }

    public boolean isNumeric()
    {
        return varDef.isNumeric(bufferPos);
    }

    public boolean isAlphabetic()
    {
        return varDef.isAlphabetic(bufferPos);
    }

    public int getBodySize()
    {
        return varDef.getBodyLength() ;
    }

    /** Returns the unprefix named var child. */
    public VarBase getUnprefixNamedVarChild(BaseProgramManager programManager, String csColName, IntegerRef rnChildIndex)
    {
        VarDefBase varDefChild = varDef.getUnprefixNamedChild(programManager.getSharedProgramInstanceData(), csColName, rnChildIndex);
        if(varDefChild != null)
        {
            // VarBase varChild =
            // bufferPos.getVarFullName(varDefChild.getFullName(bufferPos.getProgramManager().getSharedProgramInstanceData()));
            VarBase varChild = programManager.getVarFullName(varDefChild.getId());
            return varChild;
        }
        return null;
    }

    /** Returns the un dollar unprefix named child. */
    public VarBase getUnDollarUnprefixNamedChild(BaseProgramManager programManager, String csColName, IntegerRef rnChildIndex)
    {
        VarDefBase varDefChild = varDef.getUnDollarUnprefixNamedChild(
            programManager.getSharedProgramInstanceData(),
            csColName,
            rnChildIndex);
        if(varDefChild != null)
        {
            // VarBase varChild =
            // bufferPos.getVarFullName(varDefChild.getFullName(bufferPos.getProgramManager().getSharedProgramInstanceData()));
            VarBase varChild = programManager.getVarFullName(varDefChild.getId());
            return varChild;
        }
        return null;
    }


    /** Returns the var child at. */
    public Var getVarChildAt(int n)
    {
        n--;    // given as 1-based
        int nNChildren = varDef.getNbChildren();
        if(n <= nNChildren)
        {
            VarDefBuffer varDefChild = varDef.getChild(n);
            if(varDefChild != null)
            {
                BaseProgramManager programManager = TempCacheLocator.getTLSTempCache().getProgramManager();
                VarBase varChild = programManager.getVarFullName(varDefChild);
                if (!varChild.isEdit()) {
                    return (Var) varChild;
                }
            }
        }
        return null;
    }

    /** Returns the edit child at. */
    public Edit getEditChildAt(int n)
    {
        n--;    // given as 1-based
        int nNChildren = varDef.getNbChildren();
        if(n <= nNChildren)
        {
            VarDefBuffer varDefChild = varDef.getChild(n);
            if(varDefChild != null)
            {
                BaseProgramManager programManager = TempCacheLocator.getTLSTempCache().getProgramManager();
                VarBase varChild = programManager.getVarFullName(varDefChild);
                if(varChild.isEdit())
                {
                    Edit editChild = (Edit)varChild;
                    return editChild;
                }
            }
        }
        return null;
    }

    void inheritSemanticContext(VarBase varSource)
    {
        String csSemanticValue = varSource.getSemanticContextValue();
        setSemanticContextValue(csSemanticValue);
    }

    /**
     * @return
     */
    public int getNbOccurs()
    {
        return varDef.getNbOccurs() ;
    }

}
