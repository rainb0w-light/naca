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
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.programPool.SharedProgramInstanceData;
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
        this.mapName = csMapName.trim() ;
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
        this.varFrom = map;
        return this;
    }
    /** Executes the data only from operation. */
    public CESMSendMap dataOnlyFrom(Form map)
    {
        this.varFrom = map;
        return this;
    }
    /** Executes the data only from operation. */
    public CESMSendMap dataOnlyFrom(Var map)
    {
        assertIfFalse(map == null);
        captureSymbolicFrom(map);
        return this;
    }
    /** Executes the data only from operation. */
    public CESMSendMap dataOnlyFrom(Var map, Var length)
    {
        assertIfFalse(map == null);
        captureSymbolicFrom(map);
        return this;
    }
    /** Executes the data from operation. */
    public CESMSendMap dataFrom(Form map)
    {
        this.varFrom = map;
        return this;
    }
    /** Executes the data from operation. */
    public CESMSendMap dataFrom(Var map)
    {
        assertIfFalse(map == null);
        captureSymbolicFrom(map);
        return this;
    }
    /** Executes the data from operation. */
    public CESMSendMap dataFrom(Var map, Var length)
    {
        assertIfFalse(map == null);
        captureSymbolicFrom(map);
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
    /** Executes the accum operation. */
    public CESMSendMap accum()
    {
        // Runtime placeholder: preserve the fluent CICS SEND MAP contract.
        return this;
    }
    /** Executes the paging operation. */
    public CESMSendMap paging()
    {
        // Runtime placeholder: preserve the fluent CICS SEND MAP contract.
        return this;
    }
    /** Executes the wait for completion operation. */
    public CESMSendMap waitForCompletion()
    {
        // WAIT is represented by waitForCompletion because Object.wait() is final.
        return this;
    }
    /** Executes the cursor operation. */
    public CESMSendMap cursor(Var v)
    {
        nCursorPosition = v.getInt() ;
        return this;
    }
    public int nCursorPosition = 0;


    /** Builds the xmlto send. */
    public Element buildXMLToSend(Document root)
    {
        return null;
    }

    //protected CBaseMap m_BaseMap = null;
    public Form varFrom = null;
    public Var symbolicFrom = null;
    private BaseProgramManager symbolicProgramManager = null;
    private SharedProgramInstanceData symbolicSharedData = null;
    public String mapName = "" ;
    protected String name = "" ;

    public Document buildSymbolicXML(String language)
    {
        return SymbolicBmsMapAdapter.send(symbolicFrom, symbolicProgramManager,
            symbolicSharedData, mapName, language, nCursorPosition);
    }

    private void captureSymbolicFrom(Var map)
    {
        symbolicFrom = map;
        symbolicProgramManager = map.getProgramManager();
        symbolicSharedData = map.getSharedProgramInstanceData();
    }
}
