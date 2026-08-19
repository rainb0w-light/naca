/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

/**
 * @author U930DI
 *
 */
public class CallParamByLength extends CCallParam
{
    public CallParamByLength(VarAndEdit var)
    {
        if (var != null) {
            nLength = var.getTotalSize();
        }
    }

    public int getParamLength()
    {
        return nLength;
    }

    public void MapOn(Var varLinkageSection)
    {
        varLinkageSection.set(nLength); // Copy the value of the argument provided by the caller
    }


    private int nLength = 0;
}
