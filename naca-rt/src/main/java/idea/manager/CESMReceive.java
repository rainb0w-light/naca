/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.manager;

import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.misc.KeyPressed;
import nacaLib.varEx.Var;
import nacaLib.varEx.Form;

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
    //  mapName = mapName;
        return this;
    }


    /** Executes the into operation. */
    public void into(Form var)
    {
        this.mapInto = var;
        receiveData() ;
        //return this;
    }
    /** Executes the into operation. */
    public void into(Var var)
    {
        // if this function is called, that means a COPY is missing with the map defined in it
        assertIfFalse(var == null) ;
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
}
