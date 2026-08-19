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
package nacaLib.programStructure;

import nacaLib.basePrgEnv.BaseProgram;
import nacaLib.varEx.DataSection;
import nacaLib.varEx.DataSectionType;

/** Provides data section linkage behavior. */
public class DataSectionLinkage extends DataSection
{
    /** Creates a new data section linkage instance. */
    public DataSectionLinkage(BaseProgram prg)
    {
        super(prg, DataSectionType.Linkage);
    }

//  public void registerVar(Var var)
//  {
//      int nLevel = var.varManager.getLevel();
//      if(nLevel == 1 || nLevel == 77)
//      {
//          arrMappableVars.add(var);
//      }
//  }

//  public void unregisterVar(Var var)
//  {
//      arrMappableVars.remove(var);
//  }

//  private ArrayList arrMappableVars = new ArrayList();    // Array of all var that can be mapped on call
}
