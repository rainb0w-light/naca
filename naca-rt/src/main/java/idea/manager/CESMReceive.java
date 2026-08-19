/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.manager;

import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.CESM.CESMReturnCode;
import nacaLib.misc.KeyPressed;
import nacaLib.varEx.Var;
import nacaLib.varEx.Form;
import nacaLib.tempCache.TempCacheLocator;

import org.w3c.dom.Document;

/**
 * @author U930DI
 *
 */



public class CESMReceive extends CJMapObject
{
    CESMReceive(Document loader, BaseEnvironment env)
    {
        this.xmlData = loader;
        this.env = env;
    }

    CESMReceive setMap(String mapName)
    {
        BaseProgramManager manager = TempCacheLocator.getTLSTempCache().getProgramManager();
        if (manager != null)
        {
            Var implicitMap = manager.findVariable(mapName + "I");
            if (implicitMap != null)
            {
                into(implicitMap);
            }
        }
        return this;
    }


    /** Executes the into operation. */
    public CESMReceive into(Form var)
    {
        this.mapInto = var;
        receiveData() ;
        return this;
    }
    /** Executes the into operation. */
    public CESMReceive into(Var var)
    {
        assertIfFalse(var == null) ;
        SymbolicBmsMapAdapter.receive(xmlData, var);
        if (xmlData != null)
        {
            String key = xmlData.getDocumentElement().getAttribute("keypressed");
            env.setKeyPressed(KeyPressed.getKey(key));
        }
        return this;
    }

    void receiveData()
    {
        if(xmlData != null)
        {
//          for(int n=0; n<mapInto.arrForms.size(); n++)
//          {
//              Form f = (CForm) mapInto.arrForms.get(n);
                mapInto.loadValues(xmlData);
                String k = xmlData.getDocumentElement().getAttribute("keypressed") ;
                env.setKeyPressed(KeyPressed.getKey(k));
    //      }
        }
    }

    private Form mapInto = null;
    //private String mapName = "";
    private Document xmlData = null;
    private BaseEnvironment env = null ;

    /** Executes the map set operation. */
    public CESMReceive mapSet(String string)
    {
        // nothing to do with mapset...
        return this ;
    }
    /** Executes the map set operation. */
    public CESMReceive mapSet(Var name)
    {
        // nothing to do with mapset...
        return this ;
    }

    /** Writes the RECEIVE completion RESP value. */
    public CESMReceive resp(Var value)
    {
        value.set(CESMReturnCode.NORMAL.getCondition());
        return this;
    }

    /** Writes the RECEIVE completion RESP2 value. */
    public CESMReceive resp2(Var value)
    {
        value.set(0);
        return this;
    }
}
