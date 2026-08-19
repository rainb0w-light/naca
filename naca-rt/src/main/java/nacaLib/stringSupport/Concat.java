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
import nacaLib.misc.KeyPressed;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarAndEdit;


import java.util.ArrayList;

/** Provides concat behavior. */
public class Concat
{
    /** Creates a new concat instance. */
    public Concat()
    {
    }

    /** Executes the concat operation. */
    public Concat concat(VarAndEdit var)
    {
        String cs = var.getString();
        return concat(cs);
    }
    /** Executes the concat operation. */
    public Concat concat(KeyPressed key)
    {
        if (key != null)
        {
            String cs = key.getValue() ;
            return concat(cs);
        }
        return this ;
    }

    /** Executes the concat operation. */
    public Concat concat(String cs)
    {
        arrChunks.add(cs);
        return this;
    }

    /** Executes the concat delimited decimal point operation. */
    public Concat concatDelimitedDecimalPoint(Var var)
    {
        String cs = var.getString();
        return concatDelimitedDecimalPoint(cs);
    }

    /** Executes the concat delimited decimal point operation. */
    public Concat concatDelimitedDecimalPoint(String cs)
    {
        int nPos = cs.lastIndexOf('.');
        if (nPos != -1) {
            cs = cs.substring(0, nPos);
        }
        arrChunks.add(cs);
        return this;
    }

    /** Executes the concat delimited by spaces operation. */
    public Concat concatDelimitedBySpaces(Var var)
    {
        String cs = var.getString();
        return concatDelimitedBySpaces(cs);
    }

    /** Executes the concat delimited by spaces operation. */
    public Concat concatDelimitedBySpaces(String cs)
    {
        int nSpacePos = cs.indexOf(' ');
        if (nSpacePos != -1) {
            cs = cs.substring(0, nSpacePos);
        }
        arrChunks.add(cs);
        return this;
    }

    /** Executes the concat delimited by size operation. */
    public Concat concatDelimitedBySize(Var var)
    {
        String cs = var.getString();
        return concatDelimitedBySize(cs);
    }

    /** Executes the concat delimited by size operation. */
    public Concat concatDelimitedBySize(String cs)
    {
        arrChunks.add(cs);
        return this;
    }

    /** Executes the concat delimited by operation. */
    public Concat concatDelimitedBy(Var var)
    {
        String cs = var.getString();
        return concatDelimitedBy(cs);
    }

    /** Executes the concat delimited by operation. */
    public Concat concatDelimitedBy(String cs)
    {
        cs = cs.trim();
        arrChunks.add(cs);
        return this;
    }

    /** Executes the concat delimited by operation. */
    public Concat concatDelimitedBy(Var var, String csDelimiter)
    {
        String cs = var.getString();
        return concatDelimitedBy(cs, csDelimiter);
    }

    /** Executes the concat delimited by operation. */
    public Concat concatDelimitedBy(Var var, Var varDelimiter)
    {
        String cs = var.getString();
        return concatDelimitedBy(cs, varDelimiter.getString());
    }

    /** Executes the concat delimited by operation. */
    public Concat concatDelimitedBy(String cs, String csDelimiter)
    {
        int nPos = cs.indexOf(csDelimiter);
        if (nPos != -1) {
            cs = cs.substring(0, nPos);
        }
        arrChunks.add(cs);
        return this;
    }

    /** Executes the with pointer operation. */
    public Concat withPointer(Var varPointer)
    {
        this.varPointer = varPointer;
        return this;
    }

    /** Executes the into operation. */
    public ConcatTo into(VarAndEdit var)
    {
        String csOut = null;

        if(varPointer != null)
        {
            String csVar = var.getString();
            int nInitPosition = varPointer.getInt() -1;
            String csInitial = csVar.substring(0, nInitPosition);
            csOut = csInitial + getString();
        } else {
            csOut = getString();
        }

//      int nNbItems = arrChunks.size();
//      for(int n=0; n<nNbItems; n++)
//      {
//          String cs = arrChunks.get(n);
//          csOut = csOut.concat(cs);
//      }

        //var.set(csOut);
        var.setStringAtPosition(csOut, 0, csOut.length());
        if(varPointer != null)  // Set the pointer to the string length
        {
            int nOutLg = csOut.length();
            int nVarLength = var.getBodySize();
            int nLg = Math.min(nVarLength, nOutLg);

            varPointer.set(nLg+1);

            if (nOutLg > nVarLength)
            {
                isfailed = true;
            }
        }

        ConcatTo concatTo = new ConcatTo(this);
        return concatTo;
    }

    /** Returns the string. */
    public String getString()
    {
        String csOut = new String();
        int nNbItems = arrChunks.size();
        for(int n=0; n<nNbItems; n++)
        {
            String cs = arrChunks.get(n);
            csOut += cs;    // + csOut.concat(cs);
        }
        return csOut;
    }

    boolean failed()
    {
        return isfailed;
    }

    Var varPointer = null;
    ArrayList<String> arrChunks = new ArrayList<String>();
    boolean isfailed = false;
}
