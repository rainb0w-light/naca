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

import nacaLib.varEx.CoupleVar;
import nacaLib.varEx.VarBase;
import nacaLib.varEx.VarDefBuffer;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: TempVarManager.java,v 1.2 2006/08/04 12:17:32 u930di Exp $
 */
//public class TempVarManager<T>
//{
//  private TempVarTypeManager<T> tarrTemp[] = null;
//
//  public TempVarManager(int nNbTypes)
//  {
//      tarrTemp = new TempVarTypeManager[nNbTypes];
//      for(int n=0; n<nNbTypes; n++)
//      {
//          tarrTemp[n] = new TempVarTypeManager();
//      }
//  }
//
//  public CoupleVar<T> getTempCouple(int nVarDefTypeId)
//  {
//      return tarrTemp[nVarDefTypeId].getTempCoupleVar();
//  }
//
//  public CoupleVar addTemp(int nVarDefTypeId, VarDefBuffer varDefItem, T var)
//  {
//      return tarrTemp[nVarDefTypeId].addTempVar(varDefItem, var);
//  }
//
//  public void resetTempIndex(int nVarDefTypeId)
//  {
//      tarrTemp[nVarDefTypeId].reset();
//  }
//}

public class TempVarManager
{
    private TempVarTypeManager tarrTemp[] = null;

    /** Creates a new temp var manager instance. */
    public TempVarManager(int nNbTypes)
    {
        tarrTemp = new TempVarTypeManager[nNbTypes];
        for(int n=0; n<nNbTypes; n++)
        {
            tarrTemp[n] = new TempVarTypeManager();
        }
    }

    /** Returns the temp couple. */
    public CoupleVar getTempCouple(int nVarDefTypeId)
    {
        return tarrTemp[nVarDefTypeId].getTempCoupleVar();
    }

    /** Adds the temp. */
    public CoupleVar addTemp(int nVarDefTypeId, VarDefBuffer varDefItem, VarBase var)
    {
        return tarrTemp[nVarDefTypeId].addTempVar(varDefItem, var);
    }

    /** Resets the temp index. */
    public void resetTempIndex(int nVarDefTypeId)
    {
        tarrTemp[nVarDefTypeId].reset();
    }

    /** Resets the temp index and forbid reuse. */
    public void resetTempIndexAndForbidReuse(int nVarDefTypeId)
    {
        tarrTemp[nVarDefTypeId].resetAndForbidReuse();
    }

}
