/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.basePrgEnv;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import jlib.log.Log;
import jlib.misc.DateUtil;
import jlib.misc.Time_ms;
import nacaLib.CESM.CESMLink;
import nacaLib.CESM.CESMQueueManager;
import nacaLib.CESM.CESMReadQueue;
import nacaLib.CESM.CESMReturnCode;
import nacaLib.CESM.CESMStart;
import nacaLib.CESM.CESMWriteQueue;
import nacaLib.CESM.CESMXctl;
import nacaLib.base.CJMapObject;
import nacaLib.exceptions.AbortSessionException;
import nacaLib.exceptions.CESMAbendException;
import nacaLib.exceptions.CESMReturnException;
import nacaLib.misc.CCESMFakeMethodContainer;
import nacaLib.misc.CCommarea;
import nacaLib.misc.Pointer;
import nacaLib.program.CESMCommandCode;
import nacaLib.program.CJMapRunnable;
import nacaLib.program.Paragraph;
import nacaLib.program.Section;
import nacaLib.varEx.Form;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarAndEdit;

public class BaseCESMManager extends CJMapObject
{
    protected BaseEnvironment cESMEnv = null ;

    public BaseCESMManager(BaseEnvironment env)
    {
        cESMEnv = env;
    }

    public BaseEnvironment getEnvironment()
    {
        return cESMEnv ;
    }

    public void returnTrans(String csTransaction, Var v1, VarAndEdit len)
    {
        int l = len.getInt();
        returnTrans(csTransaction, v1, l, true);
    }
    public void returnTrans(Class cl, Var v1, VarAndEdit len)
    {
        returnTrans(cl.getName(), v1, len.getInt(), false);
    }
    public void returnTrans(String csTransaction, Form f1, VarAndEdit len)
    {
        returnTrans(csTransaction, f1, len.getInt(), true);
    }
    public void returnTrans(Class cl, Form f1, VarAndEdit len)
    {
        returnTrans(cl.getName(), f1, len.getInt(), false);
    }
    public void returnTrans(VarAndEdit varTransaction, Var v1, VarAndEdit len)
    {
        returnTrans(varTransaction.getString(), v1, len.getInt(), true);
    }
    public void returnTrans(VarAndEdit varTransaction, Var v1)
    {
        returnTrans(varTransaction.getString(), v1, v1.getLength(), true);
    }
    public void returnTrans(VarAndEdit varTransaction, Form form1, VarAndEdit len)
    {
        returnTrans(varTransaction.getString(), form1, len.getInt(), true);
    }
    public void returnTrans(Class cl, Var v1)
    {
        returnTrans(cl.getName(), v1, v1.getLength(), false);
    }
    public void returnTrans(String csTransaction, Var v1)
    {
        returnTrans(csTransaction, v1, v1.getLength(), true);
    }
    public void returnTrans(String csTransaction, Var v1, int length)
    {
        returnTrans(csTransaction, v1, length, true);
    }
    public void returnTrans(Class cl, Form form)
    {
        returnTrans(cl.getName(), form, false);
    }
    public void returnTrans(String csTransaction, Form form)
    {
        returnTrans(csTransaction, form, true);
    }

    public void returnTrans()
    {
        if(isLogCESM)
            Log.logDebug("returnTrans");
        cESMEnv.setLastCommandCode(CESMCommandCode.RETURN);
        cESMEnv.setNextProgramToLoad("");
        cESMEnv.setCommarea(null);
        CESMReturnException excp = new CESMReturnException();
        throw excp;
    }
    private void returnTrans(String csProgramId, Form form, boolean bResolveProgram)
    {
        if (bResolveProgram)
            csProgramId = BaseProgramLoader.ResolveTransID(csProgramId);

        if(isLogCESM)
            Log.logDebug("returnTrans program="+csProgramId+" Form="+form.getLoggableValue());
        cESMEnv.setLastCommandCode(CESMCommandCode.RETURN);
        cESMEnv.setNextProgramToLoad(csProgramId) ;
        CCommarea comm = new CCommarea() ;
        comm.setVarPassedByValue(form);
        cESMEnv.setCommarea(comm);
        CESMReturnException excp = new CESMReturnException();
        throw excp;
    }
    private void returnTrans(String csProgramId, Var v1, int length, boolean bResolveProgram)
    {
        if (bResolveProgram)
            csProgramId = BaseProgramLoader.ResolveTransID(csProgramId);
        if (length > v1.getLength())
            length = v1.getLength();

        if(isLogCESM)
            Log.logDebug("returnTrans program="+csProgramId+ " Var="+v1.getLoggableValue());
        cESMEnv.setLastCommandCode(CESMCommandCode.RETURN) ;
        cESMEnv.setNextProgramToLoad(csProgramId) ;
        CCommarea comm = new CCommarea() ;
        comm.setVarPassedByValue(v1, length);
        cESMEnv.setCommarea(comm);
        CESMReturnException excp = new CESMReturnException();
        throw excp;
    }

    public void abend()
    {
        if(isLogCESM)
            Log.logDebug("abend");
        cESMEnv.setLastCommandCode(CESMCommandCode.ABEND);
        CESMAbendException e = new CESMAbendException("none");
        throw e;
    }

    public void abend(VarAndEdit v)
    {
        abend(v.getString());
    }
    public void abend(String cs)
    {
        if(isLogCESM)
            Log.logDebug("abend");
        cESMEnv.setLastCommandCode(CESMCommandCode.ABEND) ;
        CESMAbendException e = new CESMAbendException(cs);
        throw e ;
    }

    public BaseCESMManager getAddressOfTCTUA(Pointer p)
    {
        if(isLogCESM)
            Log.logDebug("getAddressOfTCTUA");
        cESMEnv.setLastCommandCode(CESMCommandCode.GET_ADDRESS) ;
        //p.addressOf.varManager.redefinesAs(cESMEnv.getTCTUA());

        char [] acTCTUA = cESMEnv.getTCTUA();
        p.addressOf.setCustomBuffer(acTCTUA);

        return this;
    }

    public BaseCESMManager getAddressOfTWA(Pointer p)
    {
        if(isLogCESM)
            Log.logDebug("getAddressOfTCTUA");
        // p.addressOf.varManager.redefinesAs(cESMEnv.getTWA());
        char [] acTWA = cESMEnv.getTWA();
        p.addressOf.setCustomBuffer(acTWA);
        //p.addressOf.varManager.manageRedefines();

        return this;
    }

    public BaseCESMManager getAddressOfCWA(Pointer p)
    {
        if(isLogCESM)
            Log.logDebug("getAddressOfTCTUA");
        //p.addressOf.varManager.redefinesAs(cESMEnv.getCWA());
        char [] acCWA = cESMEnv.getCWA();
        p.addressOf.setCustomBuffer(acCWA);

        return this;
    }

    public CCESMFakeMethodContainer assign()
    {
        return new CCESMFakeMethodContainer(cESMEnv) ;
    }

    public BaseCESMManager ignoreCondition(String string)
    {
        if(isLogCESM)
            Log.logDebug("ignoreCondition "+string);
        cESMEnv.setLastCommandCode(CESMCommandCode.IGNORE) ;
        tabConditionHandles.remove(string);
        return this ;
    }
    public BaseCESMManager unhandleCondition(String string)
    {
        if(isLogCESM)
            Log.logDebug("unhandleCondition"+string);
        cESMEnv.setLastCommandCode(CESMCommandCode.HANDLE) ;
        tabConditionHandles.remove(string);
        return this ;
    }
    public BaseCESMManager handleCondition(String string, Paragraph par)
    {
        return handleCondition(string, (CJMapRunnable) par);
    }
    public BaseCESMManager handleCondition(String string, Section par)
    {
        return handleCondition(string, (CJMapRunnable) par);
    }
    public BaseCESMManager handleCondition(String string, CJMapRunnable target)
    {
        if(isLogCESM)
            Log.logDebug("handleCondition"+string);
        cESMEnv.setLastCommandCode(CESMCommandCode.HANDLE) ;
        tabConditionHandles.put(string, target);
        return this ;
    }
    protected Hashtable<String, CJMapRunnable> tabConditionHandles = new Hashtable<String, CJMapRunnable>();

    public String getLastCommandReturnCode()
    {
        return cESMEnv.getLastCommandReturnCode().getCode();
    }

    public int getConditionOccured()
    {
        int n = cESMEnv.getLastCommandReturnCode().getCondition() ;
        if(isLogCESM)
            Log.logDebug("getConditionOccured value="+n);
        return n;
    }

    public void setConditionOccured(int n)
    {
        if(isLogCESM)
            Log.logDebug("setConditionOccured value="+n);
        cESMEnv.setCommandReturnCode(CESMReturnCode.Select(n)) ;
    }

    public CCESMFakeMethodContainer startBrowseDataSet(String ws_Fichier)
    {
        throw unsupported("STARTBR DATASET");
    }

    public CCESMFakeMethodContainer startBrowseDataSet(Var ws_Fichier)
    {
        return startBrowseDataSet(ws_Fichier.getString());
    }

    public CCESMFakeMethodContainer readNextDataSet(Var res_Fichier)
    {
        return readNextDataSet(res_Fichier.getString());
    }

    public CCESMFakeMethodContainer readNextDataSet(String res_Fichier)
    {
        throw unsupported("READNEXT DATASET");
    }

    public CCESMFakeMethodContainer readPreviousDataSet(Var res_Fichier)
    {
        return readPreviousDataSet(res_Fichier.getString());
    }

    public String getConfig(String string)
    {
        return cESMEnv.getConfigOption(string) ;
    }

    public String getSQLEnvironment()
    {
        if(isLogCESM)
            Log.logDebug("getSQLEnvironment");
        return cESMEnv.getSQLConnection().getEnvironmentPrefix() ;
    }

    public void delayInterval(Var delay)
    {
        // delay uses format HHMMSS
        int nNextTime_s = DateUtil.getNbSecondsFromHour(delay.getInt());
        long waitTime_ms = nNextTime_s * 1000;
        cESMEnv.offsetMaxTimeLimit(waitTime_ms);
        Time_ms.wait_ms(waitTime_ms);
    }
    public void delaySeconds(Var delay)
    {
        long waitTime_ms = delay.getLong() * 1000;
        cESMEnv.offsetMaxTimeLimit(waitTime_ms);
        Time_ms.wait_ms(waitTime_ms);
    }

    public boolean hasCredentials()
    {
        return !cESMEnv.getApplicationCredentials().equals("");
    }
    public String getDeclaredUserId()
    {
        if (cESMEnv.getApplicationCredentials().length() > 7)
        {
            return cESMEnv.getApplicationCredentials().substring(5, 8);
        }
        else
        {
            return cESMEnv.getApplicationCredentials().substring(5, 7);
        }
    }
    public String getDeclaredCompany()
    {
        return cESMEnv.getApplicationCredentials().substring(0, 2) ;
    }
    public String getDeclaredAgency()
    {
        return cESMEnv.getApplicationCredentials().substring(2, 5) ;
    }

    public CCESMFakeMethodContainer enQ(Var enqsycr, int i)
    {
        return enQ(resourceName(enqsycr, i));
    }

    public CCESMFakeMethodContainer enQ(Var enqsycr)
    {
        return enQ(resourceName(enqsycr, enqsycr.getLength()));
    }

    public CCESMFakeMethodContainer deQ(Var enqsycr, int i)
    {
        return deQ(resourceName(enqsycr, i));
    }

    public CCESMFakeMethodContainer deQ(Var enqsycr)
    {
        return deQ(resourceName(enqsycr, enqsycr.getLength()));
    }

    CCESMFakeMethodContainer enQ(String resource)
    {
        RESOURCE_LOCKS.computeIfAbsent(resource, ignored -> new ReentrantLock(true)).lock();
        setNormalReturnCode();
        return new CCESMFakeMethodContainer();
    }

    CCESMFakeMethodContainer deQ(String resource)
    {
        ReentrantLock lock = RESOURCE_LOCKS.get(resource);
        if (lock == null || !lock.isHeldByCurrentThread())
        {
            throw new IllegalStateException("CICS DEQ without matching ENQ for resource " + resource);
        }
        lock.unlock();
        setNormalReturnCode();
        return new CCESMFakeMethodContainer();
    }


    public CCESMFakeMethodContainer setTDQueueClosed(String string)
    {
        cESMEnv.getQueueManager().setTransientQueueOpen(string, false);
        setNormalReturnCode();
        return new CCESMFakeMethodContainer() ;
    }
    public CCESMFakeMethodContainer setTDQueueClosed(Var queue)
    {
        return setTDQueueClosed(queue.getString()) ;
    }
    public CCESMFakeMethodContainer setTDQueueOpen(String string)
    {
        cESMEnv.getQueueManager().setTransientQueueOpen(string, true);
        setNormalReturnCode();
        return new CCESMFakeMethodContainer() ;
    }
    public CCESMFakeMethodContainer setTDQueueOpen(Var queue)
    {
        return setTDQueueOpen(queue.getString()) ;
    }

    public CESMWriteQueue writeTransiantQueue(String name)
    {
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL);
        return new CESMWriteQueue(true, name, cESMEnv.getQueueManager()) ;
    }
    public CESMWriteQueue writeTransiantQueue(Var name)
    {
        return writeTransiantQueue(name.getString()) ;
    }
    public CESMWriteQueue writeTransiantQueue(String name, Var rewriteItem)
    {
        return writeTransiantQueue(name).rewrite(rewriteItem.getInt()) ;
    }
    public CESMWriteQueue writeTransiantQueue(Var name, Var rewriteItem)
    {
        return writeTransiantQueue(name.getString(), rewriteItem) ;
    }
    public CESMWriteQueue writeTransiantQueue(String name, int rewriteItem)
    {
        return writeTransiantQueue(name).rewrite(rewriteItem) ;
    }
    public CESMWriteQueue writeTransiantQueue(Var name, int rewriteItem)
    {
        return writeTransiantQueue(name.getString(), rewriteItem) ;
    }

    public CCESMFakeMethodContainer getMain()
    {
        throw unsupported("GETMAIN without a storage target");
    }

    public String getCurrentDay()
    {
        Calendar calendar = Calendar.getInstance() ;
        int day = calendar.get(Calendar.DAY_OF_MONTH) ;
        String cs = "" + (day/10) + (day%10) ;
        return cs ;
    }

    public Calendar getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance() ;
        return calendar;
    }

    public String getCurrentMonth()
    {
        Calendar calendar = Calendar.getInstance() ;
        int n = calendar.get(Calendar.MONTH) +1 ;
        String cs = "" + (n/10) + (n%10) ;
        return cs ;
    }

    public String getCurrentShortYear()
    {
        Calendar calendar = Calendar.getInstance() ;
        int n = calendar.get(Calendar.YEAR) ;
        String cs = "" + ((n%100)/10) + (n%10) ;
        return cs ;
    }

    public void askTime()
    {
        if(isLogCESM)
            Log.logDebug("askTime");
        cESMEnv.setLastCommandCode(CESMCommandCode.ASKTIME) ;
        cESMEnv.resetDateTime() ;
    }

    public CCESMFakeMethodContainer inquire()
    {
        if(isLogCESM)
            Log.logDebug("inquire");
        throw unsupported("INQUIRE without PROGRAM and TRANSACTION resolution");
    }

    public CESMReadQueue readTempQueue(Var varName)
    {
        return readTempQueue(varName.getString());
    }
    public CESMReadQueue readTempQueue(String csName)
    {
        if(isLogCESM)
            Log.logDebug("readTempQueue "+csName);

        cESMEnv.setLastCommandCode(CESMCommandCode.READ_TEMPQUEUE);
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;

        return new CESMReadQueue(false, csName, cESMEnv.getQueueManager());
    }

    public CESMReadQueue readTransiantQueue(Var name)
    {
        return readTransiantQueue(name.getString());
    }

    public CESMReadQueue readTransiantQueue(String name)
    {
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL);
        return new CESMReadQueue(true, name, cESMEnv.getQueueManager());
    }

    public CCESMFakeMethodContainer deleteTempQueue(Var varName)
    {
        deleteTempQueue(varName.getString());
        return new CCESMFakeMethodContainer();
    }
    public void deleteTempQueue(String csName)
    {
        if(isLogCESM)
            Log.logDebug("deleteTempQueue "+csName);

        cESMEnv.setLastCommandCode(CESMCommandCode.DELETE_TEMPQUEUE);
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;

        CESMQueueManager queueManager = cESMEnv.getQueueManager();
        queueManager.deleteTempQueue(csName);
    }

    public CCESMFakeMethodContainer deleteTransiantQueue(Var varName)
    {
        return deleteTransiantQueue(varName.getString());
    }

    public CCESMFakeMethodContainer deleteTransiantQueue(String queueName)
    {
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL);
        cESMEnv.getQueueManager().deleteQueue(true, queueName);
        return new CCESMFakeMethodContainer();
    }


    public CESMWriteQueue writeTempQueue(Var varName)
    {
        return writeTempQueue(varName.getString());
    }
    public CESMWriteQueue writeTempQueue(String csName)
    {
        if(isLogCESM)
            Log.logDebug("writeTempQueue "+csName);

        cESMEnv.setLastCommandCode(CESMCommandCode.WRITE_TEMPQUEUE);
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;

        return new CESMWriteQueue(false, csName, cESMEnv.getQueueManager());
    }

    public CESMWriteQueue writeTempQueue(Var tsNom, Var reWriteItem)
    {
        if(isLogCESM)
            Log.logDebug("writeTempQueue "+tsNom.getLoggableValue());

        cESMEnv.setLastCommandCode(CESMCommandCode.WRITE_TEMPQUEUE);
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;

        String name = tsNom.getString();
        CESMWriteQueue writeorder = new CESMWriteQueue(false, name, cESMEnv.getQueueManager());
        int item = reWriteItem.getInt() ;
        writeorder.rewrite(item) ;
        return writeorder ;
    }

    public CESMWriteQueue writeTempQueue(String name, Var rewriteItem)
    {
        return writeTempQueue(name).rewrite(rewriteItem.getInt()) ;
    }

    public CESMWriteQueue writeTempQueue(String name, int rewriteItem)
    {
        return writeTempQueue(name).rewrite(rewriteItem) ;
    }

    public CESMWriteQueue writeTempQueue(Var name, int rewriteItem)
    {
        return writeTempQueue(name.getString(), rewriteItem) ;
    }

    public CCESMFakeMethodContainer readDataSet(Var var)
    {
        return readDataSet(var.getString());
    }
    public CCESMFakeMethodContainer readDataSet(String string)
    {
        throw unsupported("READ DATASET");
    }

    public CCESMFakeMethodContainer readFile(Var name)
    {
        return readFile(name.getString());
    }

    public CCESMFakeMethodContainer readFile(String name)
    {
        throw unsupported("READ FILE");
    }

    public CCESMFakeMethodContainer readPreviousDataSet(String name)
    {
        throw unsupported("READPREV DATASET");
    }

    public CCESMFakeMethodContainer readPreviousFile(Var name)
    {
        return readPreviousFile(name.getString());
    }

    public CCESMFakeMethodContainer readPreviousFile(String name)
    {
        throw unsupported("READPREV FILE");
    }

    public CCESMFakeMethodContainer readNextFile(Var name)
    {
        return readNextFile(name.getString());
    }

    public CCESMFakeMethodContainer readNextFile(String name)
    {
        throw unsupported("READNEXT FILE");
    }

    public CCESMFakeMethodContainer writeDataSet(Var var)
    {
        return writeDataSet(var.getString());
    }
    public CCESMFakeMethodContainer writeDataSet(String string)
    {
        throw unsupported("WRITE DATASET");
    }

    public CCESMFakeMethodContainer writeFile(Var file)
    {
        return writeFile(file.getString());
    }

    public CCESMFakeMethodContainer writeFile(String file)
    {
        throw unsupported("WRITE FILE");
    }

    public CCESMFakeMethodContainer reWriteDataSet(String string)
    {
        throw unsupported("REWRITE DATASET");
    }

    public CCESMFakeMethodContainer reWriteDataSet(Var var)
    {
        return reWriteDataSet(var.getString());
    }

    public CCESMFakeMethodContainer reWriteFile(String name)
    {
        throw unsupported("REWRITE FILE");
    }

    public CCESMFakeMethodContainer reWriteFile(Var name)
    {
        return reWriteFile(name.getString());
    }

    public CESMStart start(String csTransaction)
    {
        return start(csTransaction, true);
    }
    public CESMStart start(Var varTransaction)
    {
        return start(varTransaction.getString(), true);
    }
    public CESMStart start(Class cl)
    {
        return start(cl.getName(), false);
    }
    private CESMStart start(String csProgramId, boolean bResolveProgram)
    {
        if (bResolveProgram)
            csProgramId = BaseProgramLoader.ResolveTransID(csProgramId);

        if(isLogCESM)
            Log.logDebug("start "+csProgramId);
        cESMEnv.setCommarea(null);
        return new CESMStart(csProgramId, cESMEnv);
    }

    public void syncPointRollback()
    {
        if(cESMEnv.hasSQLConnection())
        {
            if(isLogCESM)
                Log.logDebug("syncPointRollback");
            cESMEnv.rollbackSQL();
        }
        else
        {
            if(isLogCESM)
                Log.logDebug("syncPointRollback: Nothing to do: No connection opened");
        }
    }

    public void syncPointCommit()
    {
        if(cESMEnv.hasSQLConnection())
        {
            if(isLogCESM)
                Log.logDebug("syncPointCommit");
            SQLException e = cESMEnv.commitSQL();
            if(e != null)
            {
                AbortSessionException exp = new AbortSessionException() ;
                exp.reason = new Error("Problem with syncPointCommit");
                exp.programName = null;
                throw exp ;
            }
        }
        else
        {
            if(isLogCESM)
                Log.logDebug("syncPointCommit: Nothing to do: No connection opened");
        }
    }

    public CESMLink link(Var varProgram)
    {
        return link(varProgram.getString().trim());
    }
    public CESMLink link(Class cl)
    {
        return link(cl.getName());
    }
    public CESMLink link(String csProgramName)
    {
        if(isLogCESM)
            Log.logDebug("link "+csProgramName);
        cESMEnv.setLastCommandCode(CESMCommandCode.LINK );
        cESMEnv.setCommarea(null);
        return new CESMLink(cESMEnv, csProgramName);
    }

    public CESMXctl xctl(Class cl)
    {
        return xctl(cl.getName());
    }
    public CESMXctl xctl(Var varProgram)
    {
        return xctl(varProgram.getString());
    }
    public CESMXctl xctl(String csProgram)
    {
        if(isLogCESM)
            Log.logDebug("xctl "+csProgram);
        cESMEnv.setLastCommandCode(CESMCommandCode.XCTL);
        cESMEnv.setCommarea(null);
        return new CESMXctl(cESMEnv, csProgram);
    }

    public String getLastCommandCode()
    {
        return cESMEnv.getLastCommandCode() ;
    }
    public BaseCESMManager handleAID(String cond, CJMapRunnable target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("CICS HANDLE AID target must not be null");
        }
        aidHandlers.put(normalizeCondition(cond), target);
        return this;
    }

    public BaseCESMManager unhandleAID(String cond)
    {
        aidHandlers.remove(normalizeCondition(cond));
        return this;
    }

    public CJMapRunnable getAIDHandler(String condition)
    {
        return aidHandlers.get(normalizeCondition(condition));
    }

    private void setNormalReturnCode()
    {
        if (cESMEnv != null)
        {
            cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL);
        }
    }

    private static String resourceName(Var resource, int length)
    {
        String value = resource.getString();
        int boundedLength = Math.max(0, Math.min(length, value.length()));
        return value.substring(0, boundedLength);
    }

    private static String normalizeCondition(String condition)
    {
        if (condition == null || condition.trim().isEmpty())
        {
            throw new IllegalArgumentException("CICS condition must not be blank");
        }
        return condition.trim().toUpperCase(Locale.ROOT);
    }

    private static UnsupportedOperationException unsupported(String operation)
    {
        return new UnsupportedOperationException(
            "CICS " + operation + " has no configured NacaRT backend");
    }

    private static final ConcurrentMap<String, ReentrantLock> RESOURCE_LOCKS =
        new ConcurrentHashMap<String, ReentrantLock>();
    private final Hashtable<String, CJMapRunnable> aidHandlers =
        new Hashtable<String, CJMapRunnable>();
}
