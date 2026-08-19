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

public class OccursDef extends OccursDefBase
{
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

    public void prepareAutoRemoval()
    {
    }

    int nNbOccurs = -1; // Undefined
}
