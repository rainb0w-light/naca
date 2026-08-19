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
public class CNumberTerminal extends CTerminal
{
    /** Creates a new cnumber terminal instance. */
    public CNumberTerminal(String val)
    {
        csValue = val ;
        /*
        if (csValue.indexOf("x") == -1)
        {
            int i = 0;
            for (; i < csValue.length() - 1 && csValue.charAt(i) == '0'; i++);
            if (i > 0)
                csValue = csValue.substring(i);
        }
        */
    }
    /* (non-Javadoc)
     * @see parser.condition.CConditionalTerminal#Export()
     */
//  public String GetType()
//  {
//      return "Number" ;
//  }
//  public String GetValue()
//  {
//      return csValue;
//  }
    String csValue = "" ;
    /* (non-Javadoc)
     * @see parser.expression.CTerminal#ExportTo(org.w3c.dom.Element, org.w3c.dom.Document)
     */
    /** Exports the to. */
    public void ExportTo(Element e, Document root)
    {
        e.setAttribute("Number", csValue)   ;
    }
    /* (non-Javadoc)
     * @see parser.expression.CTerminal#IsReference()
     */
    /** Executes the is reference operation. */
    public boolean IsReference()
    {
        return false;
    }

    /** Executes the is one operation. */
    public boolean IsOne()
    {
        if (Integer.parseInt(csValue) == 1) {
            return true;
        }
        return false;
    }

    /** Executes the is minus one operation. */
    public boolean IsMinusOne()
    {
        if (Integer.parseInt(csValue) == -1) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see parser.expression.CTerminal#ExportTo(semantic.CBaseExporter)
     */
//  public void ExportTo(CBaseLanguageExporter e)
//  {
//      e.WriteWord(csValue) ;
//  }
    /* (non-Javadoc)
     * @see parser.expression.CTerminal#GetValue()
     */
    /** Executes the get value operation. */
    public String GetValue()
    {
        return csValue ;
    }
    /* (non-Javadoc)
     * @see parser.expression.CTerminal#GetDataEntity(semantic.CBaseEntityFactory)
     */
    /** Executes the get data entity operation. */
    public CDataEntity GetDataEntity(int nLine, CBaseEntityFactory factory)
    {
        return factory.NewEntityNumber(csValue);
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        return csValue ;
    }

    /** Executes the is number operation. */
    public boolean IsNumber()
    {
        return true ;
    }
}
