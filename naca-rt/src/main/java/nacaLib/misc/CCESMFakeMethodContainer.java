/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author sly
 *
 */
package nacaLib.misc;

import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.varEx.Var;

/**
 * Compatibility fluent result used by generated CICS statements.
 *
 * <p>The historical class name is retained for source and binary migration,
 * but environment-backed operations perform their runtime side effects here.
 */
public class CCESMFakeMethodContainer
{
    private final BaseEnvironment environment;

    /** Creates a new ccesmfake method container instance. */
    public CCESMFakeMethodContainer()
    {
        this(null);
    }

    /** Creates a new ccesmfake method container instance. */
    public CCESMFakeMethodContainer(BaseEnvironment environment)
    {
        this.environment = environment;
    }

    /** Executes the a bcode operation. */
    public CCESMFakeMethodContainer aBCode(Var m)
    {
        return this ;
    }
    /** Executes the a bcode operation. */
    public CCESMFakeMethodContainer aBCode(String m)
    {
        return this ;
    }
    /** Executes the comm area operation. */
    public CCESMFakeMethodContainer commArea(Var v)
    {
        return this ;
    }
    /** Executes the comm area operation. */
    public CCESMFakeMethodContainer commArea(Var v, int n, int m)
    {
        return this ;
    }
    /** Executes the comm area operation. */
    public CCESMFakeMethodContainer commArea(Var v, Var n, int m)
    {
        return this ;
    }
    /*public CCESMFakeMethodContainer  allLowValues()
    {
        return this ;
    }
    public CCESMFakeMethodContainer  bySpaces()
    {
        return this ;
    }
    public CCESMFakeMethodContainer  leadingSpaces()
    {
        return this ;
    }
    public CCESMFakeMethodContainer  by(int n)
    {
        return this ;
    }
    public CCESMFakeMethodContainer  by(String n)
    {
        return this ;
    }
    public CCESMFakeMethodContainer  by(Var n)
    {
        return this ;
    }*/
    /** Executes the program operation. */
    public CCESMFakeMethodContainer  program(Var v)
    {
        return this ;
    }
    /** Executes the transaction operation. */
    public CCESMFakeMethodContainer  transaction(Var v)
    {
        return this ;
    }
    /** Executes the transaction operation. */
    public CCESMFakeMethodContainer  transaction(String v)
    {
        return this ;
    }
    /** Executes the from operation. */
    public CCESMFakeMethodContainer  from(Var v)
    {
        return this ;
    }
    /** Executes the from operation. */
    public CCESMFakeMethodContainer  from(Var v, Var len)
    {
        return this ;
    }
    /** Executes the from operation. */
    public CCESMFakeMethodContainer  from(Var v, int len)
    {
        return this ;
    }
    /** Executes the rec idfield operation. */
    public CCESMFakeMethodContainer  recIDField(Var v)
    {
        return this ;
    }
    /** Executes the into operation. */
    public CCESMFakeMethodContainer  into(Var v)
    {
        return this ;
    }
    /** Executes the length operation. */
    public CCESMFakeMethodContainer length(Var v)
    {
        return this;
    }
    /** Executes the length operation. */
    public CCESMFakeMethodContainer length(int value)
    {
        return this;
    }
    /** Executes the key length operation. */
    public CCESMFakeMethodContainer keyLength(Var v)
    {
        return this;
    }
    /** Executes the update operation. */
    public CCESMFakeMethodContainer update()
    {
        return this;
    }
    /** Reads the item. */
    public CCESMFakeMethodContainer  readItem(Var v)
    {
        return this ;
    }
    /** Executes the g teq operation. */
    public CCESMFakeMethodContainer  gTEQ()
    {
        return this ;
    }
    /** Writes the item. */
    public CCESMFakeMethodContainer  writeItem(Var v)
    {
        return this ;
    }
    /** Executes the main operation. */
    public CCESMFakeMethodContainer  main()
    {
        return this ;
    }
    /** Executes the term id operation. */
    public CCESMFakeMethodContainer  termID(String s)
    {
        return this ;
    }
    /** Executes the all operation. */
    public CCESMFakeMethodContainer all(String string)
    {
        return this ;
    }
    /** Executes the tctualeng operation. */
    public CCESMFakeMethodContainer TCTUALENG(Var tctualong)
    {
        requireEnvironment("ASSIGN TCTUALENG");
        tctualong.set(environment.getTCTUA().length);
        return this ;
    }
    /*public CCESMFakeMethodContainer concat(Var var)
    {
        return this ;
    }
    public CCESMFakeMethodContainer concat(String s)
    {
        return this ;
    }*/
    /** Executes the to operation. */
    public CCESMFakeMethodContainer to(Var var)
    {
        return this ;
    }
    /** Executes the to operation. */
    public CCESMFakeMethodContainer to(Var var, Var len)
    {
        return this ;
    }
    /** Executes the key length operation. */
    public CCESMFakeMethodContainer keyLength(int i)
    {
        return this ;
    }
    /** Executes the equal operation. */
    public CCESMFakeMethodContainer equal()
    {
        return this ;
    }
/*  public CCESMFakeMethodContainer first(String string)
    {
        return this ;
    }*/
    /*
    public CCESMFakeMethodContainer concatDelimitedBy(Var w_Travail, String string)
    {
        return this ;
    }
    public CCESMFakeMethodContainer delimitedBy(String string)
    {
        return this ;
    }
    public CCESMFakeMethodContainer delimitedByAll(String string)
    {
        return this ;
    }
    */
    /** Executes the applid operation. */
    public CCESMFakeMethodContainer APPLID(Var applid)
    {
        requireEnvironment("ASSIGN APPLID");
        String applicationId = environment.getConfigOption("APPLID");
        if (applicationId.isEmpty())
        {
            applicationId = environment.getConfigOption("ApplicationId");
        }
        applid.set(applicationId);
        return this ;
    }
    /** Executes the sys id operation. */
    public CCESMFakeMethodContainer sysID(Var sysID)
    {
        throw new UnsupportedOperationException(
            "Remote CICS SYSID operations require a configured transport backend");
    }
    /** Executes the count all operation. */
    public CCESMFakeMethodContainer countAll(String string, Var nbClass)
    {
        return this ;
    }

    private void requireEnvironment(String operation)
    {
        if (environment == null)
        {
            throw new IllegalStateException(operation + " requires a CICS environment");
        }
    }
}
