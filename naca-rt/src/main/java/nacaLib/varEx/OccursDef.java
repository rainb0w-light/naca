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

/** Provides occurs def behavior. */
public class OccursDef extends OccursDefBase
{
    /** Creates a new occurs def instance. */
    public OccursDef(int n)
    {
        this.nNbOccurs = n;
    }

    public int getNbOccurs()
    {
        return nNbOccurs;
    }

    public Var getRecordDependingVar()
    {
        return null;
    }

    /** Executes the prepare auto removal operation. */
    public void prepareAutoRemoval()
    {
    }

    int nNbOccurs = -1; // Undefined
}
