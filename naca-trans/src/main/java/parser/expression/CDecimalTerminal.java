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
 * @author sly
 *
 */
public class CDecimalTerminal extends CTerminal
{

    public CDecimalTerminal(String intval, String decval)
    {
        csIntVal = intval ;
        csDecVal = decval ;
    }

    protected String csIntVal = "" ;
    protected String csDecVal = "" ;
    /* (non-Javadoc)
     * @see parser.expression.CTerminal#ExportTo(org.w3c.dom.Element, org.w3c.dom.Document)
     */
    public void ExportTo(Element e, Document root)
    {
        e.setAttribute("Decimal", csIntVal + "," + csDecVal) ;
    }

    /* (non-Javadoc)
     * @see parser.expression.CTerminal#IsReference()
     */
    public boolean IsReference()
    {
        return false;
    }

    public boolean IsOne()
    {
        return false;
    }

    public boolean IsMinusOne()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see parser.expression.CTerminal#ExportTo(semantic.CBaseExporter)
     */
//  public void ExportTo(CBaseLanguageExporter e)
//  {
//      e.WriteWord(csIntVal + "." + csDecVal) ;
//  }

    /* (non-Javadoc)
     * @see parser.expression.CTerminal#GetValue()
     */
    public String GetValue()
    {
        return csIntVal + "." + csDecVal ;
    }

    /* (non-Javadoc)
     * @see parser.expression.CTerminal#GetDataEntity(semantic.CBaseEntityFactory)
     */
    public CDataEntity GetDataEntity(int nLine, CBaseEntityFactory factory)
    {
        return factory.NewEntityNumber(csIntVal + "." + csDecVal);
    }
    public String toString()
    {
        return csIntVal +"."+ csDecVal ;
    }

    public boolean IsNumber()
    {
        return true ;
    }
}
