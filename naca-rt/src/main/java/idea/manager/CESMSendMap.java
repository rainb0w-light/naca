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
package idea.manager;

import nacaLib.base.CJMapObject;
import nacaLib.varEx.Var;
import nacaLib.varEx.Form;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CESMSendMap extends CJMapObject
{
    CESMSendMap()
    {
    }

    CESMSendMap setMapName(String csMapName)
    {
        // Find the map which has to name in parameter: To DO
        this.mapName = csMapName.trim() ;
        return this;
    }

    public CESMSendMap mapSet(String csMapName)
    {
        name = csMapName ;
        return this;
    }
    public CESMSendMap mapSet(Var MapName)
    {
        name = MapName.getString() ;
        return this;
    }

    public CESMSendMap from(Form map)
    {
        this.varFrom = map;
        return this;
    }
    public CESMSendMap dataOnlyFrom(Form map)
    {
        this.varFrom = map;
        return this;
    }
    public CESMSendMap dataOnlyFrom(Var map)
    {
        assertIfFalse(map == null);
        // this function may not be called : in this case, a COPY is missing defining a map
        return this;
    }
    public CESMSendMap dataOnlyFrom(Var map, Var length)
    {
        assertIfFalse(map == null);
        // Preserve the generated fluent signature; map lookup remains unsupported.
        return this;
    }
    public CESMSendMap dataFrom(Form map)
    {
        this.varFrom = map;
        return this;
    }
    public CESMSendMap dataFrom(Var map)
    {
        assertIfFalse(map == null);
        // this function may not be called : in this case, a COPY is missing defining a map
        return this;
    }
    public CESMSendMap dataFrom(Var map, Var length)
    {
        assertIfFalse(map == null);
        // this function may not be called : in this case, a COPY is missing defining a map
        return this;
    }
    public CESMSendMap cursor()
    {
        // unsupported
        return this;
    }
    public CESMSendMap alarm()
    {
        // unsupported
        return this;
    }
    public CESMSendMap erase()
    {
//      bErase = true;
        // unsupported
        return this;
    }
//  protected boolean bErase = false ;
    public CESMSendMap freeKB()
    {
        // unsupported
        return this;
    }
    public CESMSendMap accum()
    {
        // Runtime placeholder: preserve the fluent CICS SEND MAP contract.
        return this;
    }
    public CESMSendMap paging()
    {
        // Runtime placeholder: preserve the fluent CICS SEND MAP contract.
        return this;
    }
    public CESMSendMap waitForCompletion()
    {
        // WAIT is represented by waitForCompletion because Object.wait() is final.
        return this;
    }
    public CESMSendMap cursor(Var v)
    {
        nCursorPosition = v.getInt() ;
        return this;
    }
    public int nCursorPosition = 0;


    public Element buildXMLToSend(Document root)
    {
        return null;
    }

    //protected CBaseMap m_BaseMap = null;
    public Form varFrom = null;
    public String mapName = "" ;
    protected String name = "" ;
}
