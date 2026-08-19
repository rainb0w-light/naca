/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.manager;

import idea.onlinePrgEnv.OnlineEnvironment;
import jlib.log.Log;
import nacaLib.CESM.CESMStartData;
import nacaLib.basePrgEnv.BaseCESMManager;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.program.CESMCommandCode;
import nacaLib.varEx.Var;


/** Provides cesmmanager behavior. */
public class CESMManager extends BaseCESMManager
{
    /** Creates a new cesmmanager instance. */
    public CESMManager(BaseEnvironment env)
    {
        super(env);
    }


    /** Executes the send map operation. */
    public CESMSendMap sendMap(Var varMapToSend)
    {
        if (isLogCESM) {
            Log.logDebug("sendMap Var=" + varMapToSend.getLoggableValue());
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.SEND_MAP) ;
        CESMSendMap order = new CESMSendMap();
        ((OnlineEnvironment)cESMEnv).addMapOrder(order) ;
        order.setMapName(varMapToSend.getString());
        return order;
    }

    /** Executes the send map operation. */
    public CESMSendMap sendMap(String csMapToSend)
    {
        if (isLogCESM) {
            Log.logDebug("sendMap String=" + csMapToSend);
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.SEND_MAP) ;
        CESMSendMap order = new CESMSendMap();
        ((OnlineEnvironment)cESMEnv).addMapOrder(order) ;
        order.setMapName(csMapToSend);
        return order;
    }

    /** Executes the receive map operation. */
    public CESMReceive receiveMap(String mapName)
    {
        if (isLogCESM) {
            Log.logDebug("receiveMap String=" + mapName);
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.RECEIVE_MAP) ;
        //XMLUtil.ExportXML(cESMEnv.getXMLData(), "DataReceived.xml");
        //cESMEnv.recordInput() ;
        CESMReceive order = new CESMReceive(cESMEnv.getXMLData(), cESMEnv);
        order.setMap(mapName);
        return order;
    }
    /** Executes the receive map operation. */
    public CESMReceive receiveMap(Var mapToReceive)
    {
        if (isLogCESM) {
            Log.logDebug("receiveMap Var=" + mapToReceive.getLoggableValue());
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.RECEIVE_MAP) ;
        //XMLUtil.ExportXML(cESMEnv.getXMLData(), "DataReceived.xml");
        //cESMEnv.recordInput() ;
        CESMReceive order = new CESMReceive(cESMEnv.getXMLData(), cESMEnv);
        order.setMap(mapToReceive.getString());
        return order;
    }

    /** Sends plain text to the cloud terminal response boundary. */
    public CESMSendText sendText(Var text)
    {
        return sendText(text.getString());
    }

    /** Sends plain text to the cloud terminal response boundary. */
    public CESMSendText sendText(String text)
    {
        cESMEnv.setLastCommandCode(CESMCommandCode.SEND_TEXT);
        return new CESMSendText(cESMEnv, text);
    }

    /** Executes the retrieve into operation. */
    public void retrieveInto(Var varDest, Var longfrom)
    {
        if (isLogCESM) {
            Log.logDebug("retrieveInto to=" + varDest.getLoggableValue() + " from=" + longfrom.getLoggableValue());
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.RETRIEVE) ;
        CESMStartData data = cESMEnv.GetEnqueuedData();
        if (data != null)
        {
            int nDestLength = data.getLength();
            varDest.getBuffer().copyBytesFromSource(varDest.getAbsolutePosition(), data.getCharBuffer(), 0, nDestLength);
            longfrom.set(nDestLength);
        }
    }

    /** Executes the retrieve into operation. */
    public void retrieveInto(Var varDest)
    {
        if (isLogCESM) {
            Log.logDebug("retrieveInto to=" + varDest.getLoggableValue());
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.RETRIEVE) ;
        CESMStartData data = cESMEnv.GetEnqueuedData();
        if (data != null)
        {
            int nDestLength = data.getLength();
            varDest.getBuffer().copyBytesFromSource(varDest.getAbsolutePosition(), data.getCharBuffer(), 0, nDestLength);
        }
    }

    /** Executes the retrieve set operation. */
    public void retrieveSet(Var pointer)
    {
        // TODO(quality-governance): implement pointer-based RETRIEVE; preserve generated contract.
    }

    /** Executes the retrieve set operation. */
    public void retrieveSet(Var pointer, Var length)
    {
        // TODO(quality-governance): implement pointer-based RETRIEVE; preserve generated contract.
    }
}
