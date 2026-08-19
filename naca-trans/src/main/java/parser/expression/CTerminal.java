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
public abstract class CTerminal
{
//  public abstract String GetType() ;
//  public abstract String GetValue() ;

    protected CTerminal()
    {
    }

    /** Executes the get value operation. */
    public abstract String GetValue() ;
    /** Executes the is reference operation. */
    public abstract boolean IsReference() ;
//  public abstract boolean IsOne();
//  public abstract boolean IsMinusOne();


    /** Exports the to. */
    public abstract void ExportTo(Element e, Document root);
    //public abstract void ExportTo(CBaseLanguageExporter e) ;

//  public CIdentifier GetIdentifier()
//  {
//      return null ;
//  }

    /** Executes the get data entity operation. */
    public abstract CDataEntity GetDataEntity(int nLine, CBaseEntityFactory factory) ;

    /** Executes the get data reference operation. */
    public CDataEntity GetDataReference(int nLine, CBaseEntityFactory factory)
    {
        return null;
    }

    /** Executes the is number operation. */
    public abstract boolean IsNumber() ;
}
