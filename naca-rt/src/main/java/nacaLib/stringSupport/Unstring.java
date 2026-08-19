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
package nacaLib.stringSupport;
// import nacaLib.base.*;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarAndEdit;

/** Provides unstring behavior. */
public class Unstring
{
    /** Creates a new unstring instance. */
    public Unstring(VarAndEdit varSource)
    {
        unstringManager = new UnstringManager(varSource);
    }

    /** Executes the delimited by operation. */
    public Unstring delimitedBy(VarAndEdit varDelimiter)
    {
        return delimitedBy(varDelimiter.getString());
    }

    /** Executes the delimited by operation. */
    public Unstring delimitedBy(String csDelimiter)
    {
        UnstringDelimiter delimiter = new UnstringDelimiter(csDelimiter, false);
        unstringManager.delimiters.add(delimiter);
        return this;
    }

    /** Executes the delimited by all operation. */
    public Unstring delimitedByAll(Var varDelimiter)
    {
        return delimitedByAll(varDelimiter.getString());
    }

    /** Executes the delimited by all operation. */
    public Unstring delimitedByAll(String csDelimiter)
    {
        UnstringDelimiter delimiter = new UnstringDelimiter(csDelimiter, true);
        unstringManager.delimiters.add(delimiter);
        return this;
    }

    /** Executes the tallying operation. */
    public Unstring tallying(Var varTallying)
    {
        unstringManager.tallying(varTallying);
        return this;
    }

    /** Executes the to operation. */
    public UnstringToManager to(Var varDelimiterDest, Var varDelimiterIn, Var varCountDest)
    {
        UnstringToManager unstringToManager = new UnstringToManager(unstringManager);
        unstringManager.doInto(varDelimiterDest, varDelimiterIn, varCountDest);
        return unstringToManager;
    }

    /** Executes the to operation. */
    public UnstringToManager to(Var varDelimiterDest)
    {
        UnstringToManager unstringToManager = new UnstringToManager(unstringManager);
        unstringManager.doInto(varDelimiterDest, null, null);
        return unstringToManager;
    }

//  public boolean failed()
//  {
//      if(unstringManager == null)
//          return true;
//      return unstringManager.failed();
//  }
//
//  public boolean notFailed()
//  {
//      if(unstringManager == null)
//          return false;
//      return !unstringManager.failed();
//  }

    /** Executes the with pointer operation. */
    public Unstring withPointer(Var varPointer)
    {
        unstringManager.withPointer(varPointer);
        return this ;
    }

    private UnstringManager unstringManager = null;
}
