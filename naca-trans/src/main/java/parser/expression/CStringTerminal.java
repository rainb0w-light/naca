/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.expression;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CDataEntity;
import semantic.CBaseEntityFactory;

/**
 * @author U930CV
 *
 */
public class CStringTerminal extends CTerminal
{
    /** Creates a new cstring terminal instance. */
    public CStringTerminal(char[] arr)
    {
        value = arr ;
        str = new String(value);
    }
    /** Creates a new cstring terminal instance. */
    public CStringTerminal(String cs)
    {
        value = cs.toCharArray() ;
        str = cs ;
    }

    protected char[] value = {} ;
    protected String str = "" ;

    /** Exports the to. */
    public void ExportTo(Element e, Document root)
    {
        String cs = new String(value);
        e.setAttribute("String", cs) ;
    }

    /** Executes the is reference operation. */
    public boolean IsReference()
    {
        return false;
    }

    /** Executes the is one operation. */
    public boolean IsOne()
    {
        return false;
    }

    /** Executes the is minus one operation. */
    public boolean IsMinusOne()
    {
        return false;
    }


    /** Executes the get value operation. */
    public String GetValue()
    {
        String cs = new String(value);
        return cs ;
    }

    /** Executes the get data entity operation. */
    public CDataEntity GetDataEntity(int nLine, CBaseEntityFactory factory)
    {
        String val = new String(value) ;
        CDataEntity e = factory.getSpecialConstantValue(val);
        if (e != null)
        {
            return e ;
        }
        else
        {
            return factory.NewEntityString(value);
        }
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        return "\"" + new String(value) + "\"" ;
    }

    /** Executes the is number operation. */
    public boolean IsNumber()
    {
        return false ;
    }
}
