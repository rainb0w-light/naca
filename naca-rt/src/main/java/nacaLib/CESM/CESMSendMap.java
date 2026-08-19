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
package nacaLib.CESM;

import nacaLib.base.CJMapObject;
import nacaLib.varEx.Var;
import nacaLib.varEx.Form;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Provides cesmsend map behavior. */
public class CESMSendMap extends CJMapObject
{
    CESMSendMap()
    {
    }

    CESMSendMap setMapName(String csMapName)
    {
        // Find the map which has to name in parameter: To DO
        mapName = csMapName.trim() ;
        return this;
    }

    /** Executes the map set operation. */
    public CESMSendMap mapSet(String csMapName)
    {
        name = csMapName ;
        return this;
    }
    /** Executes the map set operation. */
    public CESMSendMap mapSet(Var newMapName)
    {
        name = newMapName.getString() ;
        return this;
    }

    /** Executes the from operation. */
    public CESMSendMap from(Form map)
    {
        varFrom = map;
        return this;
    }
    /** Executes the data only from operation. */
    public CESMSendMap dataOnlyFrom(Form map)
    {
        varFrom = map;
        return this;
    }
    /** Executes the data only from operation. */
    public CESMSendMap dataOnlyFrom(Var map)
    {
        assertIfFalse(map == null);
        // this function may not be called : in this case, a COPY is missing defining a map
        return this;
    }
    /** Executes the data from operation. */
    public CESMSendMap dataFrom(Form map)
    {
        varFrom = map;
        return this;
    }
    /** Executes the data from operation. */
    public CESMSendMap dataFrom(Var map)
    {
        assertIfFalse(map == null);
        // this function may not be called : in this case, a COPY is missing defining a map
        return this;
    }
    /** Executes the data from operation. */
    public CESMSendMap dataFrom(Var map, Var length)
    {
        assertIfFalse(map == null);
        // this function may not be called : in this case, a COPY is missing defining a map
        return this;
    }
    /** Executes the cursor operation. */
    public CESMSendMap cursor()
    {
        // unsupported
        return this;
    }
    /** Executes the alarm operation. */
    public CESMSendMap alarm()
    {
        // unsupported
        return this;
    }
    /** Executes the erase operation. */
    public CESMSendMap erase()
    {
//      bErase = true;
        // unsupported
        return this;
    }
//  protected boolean bErase = false ;
    /** Executes the free kb operation. */
    public CESMSendMap freeKB()
    {
        // unsupported
        return this;
    }
    /** Executes the cursor operation. */
    public CESMSendMap cursor(Var v)
    {
        nCursorPosition = v.getInt() ;
        return this;
    }
    protected int nCursorPosition = 0;


    /** Builds the xmlto send. */
    public Element buildXMLToSend(Document root)
    {
        return null;
    }

    //protected CBaseMap m_BaseMap = null;
    protected Form varFrom = null;
    protected String mapName = "" ;
    protected String name = "" ;
}
