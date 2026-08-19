/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.expression;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import utils.Transcoder;

/**
 * @author U930CV
 *
 */
public class CIdentifierTerminal extends CTerminal
{
    /* (non-Javadoc)
     * @see parser.condition.CConditionalTerminal#Export()
     */
    /** Creates a new cidentifier terminal instance. */
    public CIdentifierTerminal(CIdentifier id)
    {
        identifier = id;
    }

//  public String GetType()
//  {
//      return "Identifier" ;
//  }
//  public String GetValue()
//  {
//      return identifier.Export() ;
//  }
    CIdentifier identifier = null ;

    /* (non-Javadoc)
     * @see parser.expression.CTerminal#ExportTo(org.w3c.dom.Element, org.w3c.dom.Document)
     */
    /** Exports the to. */
    public void ExportTo(Element e, Document root)
    {
        if (identifier != null)
        {
            identifier.ExportTo(e, root) ;
        }
    }

    /* (non-Javadoc)
     * @see parser.expression.CTerminal#IsReference()
     */
    /** Executes the is reference operation. */
    public boolean IsReference()
    {
        return true;
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

    /** Executes the get identifier operation. */
    public CIdentifier GetIdentifier()
    {
        return identifier ;
    }

    /* (non-Javadoc)
     * @see parser.expression.CTerminal#GetValue()
     */
    /** Executes the get value operation. */
    public String GetValue()
    {
        return "" ;
    }

    /* (non-Javadoc)
     * @see parser.expression.CTerminal#GetDataEntity(semantic.CBaseEntityFactory)
     */
    /** Executes the get data entity operation. */
    public CDataEntity GetDataEntity(int nLine, CBaseEntityFactory factory)
    {
        CDataEntity e = identifier.GetDataReference(nLine, factory);
        if (e == null)
        {
            Transcoder.logError(nLine, "ERROR : identifier not found : "+identifier.GetName());
            int n = 0 ;
        }
        return e ;
    }
    /** Returns a string representation of this value. */
    public String toString()
    {
        return identifier.toString() ;
    }

    /** Executes the get data reference operation. */
    public CDataEntity GetDataReference(int nLine, CBaseEntityFactory factory)
    {
        return identifier.GetDataReference(nLine, factory);
    }

    /** Executes the is number operation. */
    public boolean IsNumber()
    {
        return false ;
    }
}
