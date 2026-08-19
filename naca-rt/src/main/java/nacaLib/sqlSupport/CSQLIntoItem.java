/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sqlSupport;

// PJD ROWID Support:import oracle.sql.ROWID;
import jlib.log.Log;
import nacaLib.base.CJMapObject;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarAndEdit;

/**
 * @author U930DI
 *
 */

public class CSQLIntoItem extends CJMapObject
{
    /** Creates a new csqlinto item instance. */
    public CSQLIntoItem(VarAndEdit varInto, Var varIndicator)
    {
        this.varInto = varInto;
        this.varIndicator = varIndicator;
    }
    /** Executes the set operation. */
    public void set(VarAndEdit varInto, Var varIndicator)
    {
        this.varInto = varInto;
        this.varIndicator = varIndicator;
    }


//  public CSQLIntoItem()
//  {
//      varIndicator = null;
//  }

    /** Sets the col value. */
    public void setColValue(String csValue, boolean bNull)  //, String csSemanticContext)
    {
        if(varInto != null)
        {
            varInto.varDef.write(varInto.bufferPos, csValue);   //varInto.set(csValue);

            //Sytem.out.println("setColValue: varInto="+varInto.toString());
            //varInto.setSemanticContextValue(csSemanticContext);
        }
        if(varIndicator != null)
        {
            if (bNull) {
                varIndicator.set(-1);   // The col is SQL NULL
            } else {
                varIndicator.set(0);    // The col is not sql null
            }
        }
        if (isLogSql) {
            Log.logDebug("sql into filling:" + getLoggableValue());
        }
    }

    /** Sets the col value null. */
    public void setColValueNull(boolean bNull)
    {
        if(varIndicator != null)
        {
            if (bNull) {
                varIndicator.set(-1);   // The col is SQL NULL
            } else {
                varIndicator.set(0);    // The col is not sql null
            }
        }
    }

    // PJD ROWID Support:
    /*
    public void setColValue(ROWID rowId)
    {
        m_RowId = rowId;
    }
    */

    /** Returns the indicator null. */
    public boolean getIndicatorNull()
    {
        if(varIndicator != null)
        {
            int n = varIndicator.getInt();
            if (n == -1) {
                return true;    // SQL NULL
            }
        }
        return false;
    }

    public VarAndEdit getVarInto()
    {
        return varInto;
    }

    public Var getVarIndicator()
    {
        return varIndicator;
    }

    /** Returns the loggable value. */
    public String getLoggableValue()
    {
        if(varInto != null)
        {
            if (varIndicator != null) {
                return "into=" + varInto.getLoggableValue() + " Indicator=" + varIndicator.getLoggableValue();
            } else {
                return "into=" + varInto.getLoggableValue() + " IndicatorNull";
            }
        }
        return "into=Null";
    }

    /** Returns the unique hashed id. */
    public long getUniqueHashedId()
    {
        long l = 0;
        if (varInto != null) {
            l = varInto.getId();
        }
        if(varIndicator != null)
        {
            l *= 32678;
            l += varIndicator.getId();
        }
        return l;
    }

    // PJD ROWID Support:
    /*
    public ROWID getRowId()
    {
        return m_RowId;
    }
    */

    private VarAndEdit varInto = null;
    private Var varIndicator = null;
    // PJD ROWID Support:private oracle.sql.ROWID m_RowId;
}
