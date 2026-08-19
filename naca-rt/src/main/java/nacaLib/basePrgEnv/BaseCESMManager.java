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

/** Provides base cesmmanager behavior. */
public class BaseCESMManager extends CJMapObject
{
    protected BaseEnvironment cESMEnv = null ;

    /** Creates a new base cesmmanager instance. */
    public BaseCESMManager(BaseEnvironment env)
    {
        cESMEnv = env;
    }

    public BaseEnvironment getEnvironment()
    {
        return cESMEnv ;
    }

    /** Executes the return trans operation. */
    public void returnTrans(String csTransaction, Var v1, VarAndEdit len)
    {
        int l = len.getInt();
        returnTrans(csTransaction, v1, l, true);
    }
    /** Executes the return trans operation. */
    public void returnTrans(Class cl, Var v1, VarAndEdit len)
    {
        returnTrans(cl.getName(), v1, len.getInt(), false);
    }
    /** Executes the return trans operation. */
    public void returnTrans(String csTransaction, Form f1, VarAndEdit len)
    {
        returnTrans(csTransaction, f1, len.getInt(), true);
    }
    /** Executes the return trans operation. */
    public void returnTrans(Class cl, Form f1, VarAndEdit len)
    {
        returnTrans(cl.getName(), f1, len.getInt(), false);
    }
    /** Executes the return trans operation. */
    public void returnTrans(VarAndEdit varTransaction, Var v1, VarAndEdit len)
    {
        returnTrans(varTransaction.getString(), v1, len.getInt(), true);
    }
    /** Executes the return trans operation. */
    public void returnTrans(VarAndEdit varTransaction, Var v1)
    {
        returnTrans(varTransaction.getString(), v1, v1.getLength(), true);
    }
    /** Executes the return trans operation. */
    public void returnTrans(VarAndEdit varTransaction, Form form1, VarAndEdit len)
    {
        returnTrans(varTransaction.getString(), form1, len.getInt(), true);
    }
    /** Executes the return trans operation. */
    public void returnTrans(Class cl, Var v1)
    {
        returnTrans(cl.getName(), v1, v1.getLength(), false);
    }
    /** Executes the return trans operation. */
    public void returnTrans(String csTransaction, Var v1)
    {
        returnTrans(csTransaction, v1, v1.getLength(), true);
    }
    /** Executes the return trans operation. */
    public void returnTrans(String csTransaction, Var v1, int length)
    {
        returnTrans(csTransaction, v1, length, true);
    }
    /** Executes the return trans operation. */
    public void returnTrans(Class cl, Form form)
    {
        returnTrans(cl.getName(), form, false);
    }
    /** Executes the return trans operation. */
    public void returnTrans(String csTransaction, Form form)
    {
        returnTrans(csTransaction, form, true);
    }

    /** Executes the return trans operation. */
    public void returnTrans()
    {
        if (isLogCESM) {
            Log.logDebug("returnTrans");
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.RETURN);
        cESMEnv.setNextProgramToLoad("");
        cESMEnv.setCommarea(null);
        CESMReturnException excp = new CESMReturnException();
        throw excp;
    }
    private void returnTrans(String csProgramId, Form form, boolean bResolveProgram)
    {
        if (bResolveProgram) {
            csProgramId = BaseProgramLoader.ResolveTransID(csProgramId);
        }

        if (isLogCESM) {
            Log.logDebug("returnTrans program=" + csProgramId + " Form=" + form.getLoggableValue());
        }
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
        if (bResolveProgram) {
            csProgramId = BaseProgramLoader.ResolveTransID(csProgramId);
        }
        if (length > v1.getLength()) {
            length = v1.getLength();
        }

        if (isLogCESM) {
            Log.logDebug("returnTrans program=" + csProgramId + " Var=" + v1.getLoggableValue());
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.RETURN) ;
        cESMEnv.setNextProgramToLoad(csProgramId) ;
        CCommarea comm = new CCommarea() ;
        comm.setVarPassedByValue(v1, length);
        cESMEnv.setCommarea(comm);
        CESMReturnException excp = new CESMReturnException();
        throw excp;
    }

    /** Executes the abend operation. */
    public void abend()
    {
        if (isLogCESM) {
            Log.logDebug("abend");
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.ABEND);
        CESMAbendException e = new CESMAbendException("none");
        throw e;
    }

    /** Executes the abend operation. */
    public void abend(VarAndEdit v)
    {
        abend(v.getString());
    }
    /** Executes the abend operation. */
    public void abend(String cs)
    {
        if (isLogCESM) {
            Log.logDebug("abend");
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.ABEND) ;
        CESMAbendException e = new CESMAbendException(cs);
        throw e ;
    }

    /** Returns the address of tctua. */
    public BaseCESMManager getAddressOfTCTUA(Pointer p)
    {
        if (isLogCESM) {
            Log.logDebug("getAddressOfTCTUA");
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.GET_ADDRESS) ;
        //p.addressOf.varManager.redefinesAs(cESMEnv.getTCTUA());

        char [] acTCTUA = cESMEnv.getTCTUA();
        p.addressOf.setCustomBuffer(acTCTUA);

        return this;
    }

    /** Returns the address of twa. */
    public BaseCESMManager getAddressOfTWA(Pointer p)
    {
        if (isLogCESM) {
            Log.logDebug("getAddressOfTCTUA");
        }
        // p.addressOf.varManager.redefinesAs(cESMEnv.getTWA());
        char [] acTWA = cESMEnv.getTWA();
        p.addressOf.setCustomBuffer(acTWA);
        //p.addressOf.varManager.manageRedefines();

        return this;
    }

    /** Returns the address of cwa. */
    public BaseCESMManager getAddressOfCWA(Pointer p)
    {
        if (isLogCESM) {
            Log.logDebug("getAddressOfTCTUA");
        }
        //p.addressOf.varManager.redefinesAs(cESMEnv.getCWA());
        char [] acCWA = cESMEnv.getCWA();
        p.addressOf.setCustomBuffer(acCWA);

        return this;
    }

    /** Executes the assign operation. */
    public CCESMFakeMethodContainer assign()
    {
        return new CCESMFakeMethodContainer(cESMEnv) ;
    }

    /** Executes the ignore condition operation. */
    public BaseCESMManager ignoreCondition(String string)
    {
        if (isLogCESM) {
            Log.logDebug("ignoreCondition " + string);
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.IGNORE) ;
        tabConditionHandles.remove(string);
        return this ;
    }
    /** Executes the unhandle condition operation. */
    public BaseCESMManager unhandleCondition(String string)
    {
        if (isLogCESM) {
            Log.logDebug("unhandleCondition" + string);
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.HANDLE) ;
        tabConditionHandles.remove(string);
        return this ;
    }
    /** Executes the handle condition operation. */
    public BaseCESMManager handleCondition(String string, Paragraph par)
    {
        return handleCondition(string, (CJMapRunnable) par);
    }
    /** Executes the handle condition operation. */
    public BaseCESMManager handleCondition(String string, Section par)
    {
        return handleCondition(string, (CJMapRunnable) par);
    }
    /** Executes the handle condition operation. */
    public BaseCESMManager handleCondition(String string, CJMapRunnable target)
    {
        if (isLogCESM) {
            Log.logDebug("handleCondition" + string);
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.HANDLE) ;
        tabConditionHandles.put(string, target);
        return this ;
    }
    protected Hashtable<String, CJMapRunnable> tabConditionHandles = new Hashtable<String, CJMapRunnable>();

    public String getLastCommandReturnCode()
    {
        return cESMEnv.getLastCommandReturnCode().getCode();
    }

    /** Returns the condition occured. */
    public int getConditionOccured()
    {
        int n = cESMEnv.getLastCommandReturnCode().getCondition() ;
        if (isLogCESM) {
            Log.logDebug("getConditionOccured value=" + n);
        }
        return n;
    }

    /** Sets the condition occured. */
    public void setConditionOccured(int n)
    {
        if (isLogCESM) {
            Log.logDebug("setConditionOccured value=" + n);
        }
        cESMEnv.setCommandReturnCode(CESMReturnCode.Select(n)) ;
    }

    /** Executes the start browse data set operation. */
    public CCESMFakeMethodContainer startBrowseDataSet(String wsFichier)
    {
        throw unsupported("STARTBR DATASET");
    }

    /** Executes the start browse data set operation. */
    public CCESMFakeMethodContainer startBrowseDataSet(Var wsFichier)
    {
        return startBrowseDataSet(wsFichier.getString());
    }

    /** Reads the next data set. */
    public CCESMFakeMethodContainer readNextDataSet(Var resFichier)
    {
        return readNextDataSet(resFichier.getString());
    }

    /** Reads the next data set. */
    public CCESMFakeMethodContainer readNextDataSet(String resFichier)
    {
        throw unsupported("READNEXT DATASET");
    }

    /** Reads the previous data set. */
    public CCESMFakeMethodContainer readPreviousDataSet(Var resFichier)
    {
        return readPreviousDataSet(resFichier.getString());
    }

    /** Returns the config. */
    public String getConfig(String string)
    {
        return cESMEnv.getConfigOption(string) ;
    }

    /** Returns the sqlenvironment. */
    public String getSQLEnvironment()
    {
        if (isLogCESM) {
            Log.logDebug("getSQLEnvironment");
        }
        return cESMEnv.getSQLConnection().getEnvironmentPrefix() ;
    }

    /** Executes the delay interval operation. */
    public void delayInterval(Var delay)
    {
        // delay uses format HHMMSS
        int nNextTimeS = DateUtil.getNbSecondsFromHour(delay.getInt());
        long waitTimeMs = nNextTimeS * 1000;
        cESMEnv.offsetMaxTimeLimit(waitTimeMs);
        Time_ms.wait_ms(waitTimeMs);
    }
    /** Executes the delay seconds operation. */
    public void delaySeconds(Var delay)
    {
        long waitTimeMs = delay.getLong() * 1000;
        cESMEnv.offsetMaxTimeLimit(waitTimeMs);
        Time_ms.wait_ms(waitTimeMs);
    }

    /** Returns whether s credentials. */
    public boolean hasCredentials()
    {
        return !cESMEnv.getApplicationCredentials().equals("");
    }
    /** Returns the declared user id. */
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

    /** Executes the en q operation. */
    public CCESMFakeMethodContainer enQ(Var enqsycr, int i)
    {
        return enQ(resourceName(enqsycr, i));
    }

    /** Executes the en q operation. */
    public CCESMFakeMethodContainer enQ(Var enqsycr)
    {
        return enQ(resourceName(enqsycr, enqsycr.getLength()));
    }

    /** Executes the de q operation. */
    public CCESMFakeMethodContainer deQ(Var enqsycr, int i)
    {
        return deQ(resourceName(enqsycr, i));
    }

    /** Executes the de q operation. */
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


    /** Sets the tdqueue closed. */
    public CCESMFakeMethodContainer setTDQueueClosed(String string)
    {
        cESMEnv.getQueueManager().setTransientQueueOpen(string, false);
        setNormalReturnCode();
        return new CCESMFakeMethodContainer() ;
    }
    /** Sets the tdqueue closed. */
    public CCESMFakeMethodContainer setTDQueueClosed(Var queue)
    {
        return setTDQueueClosed(queue.getString()) ;
    }
    /** Sets the tdqueue open. */
    public CCESMFakeMethodContainer setTDQueueOpen(String string)
    {
        cESMEnv.getQueueManager().setTransientQueueOpen(string, true);
        setNormalReturnCode();
        return new CCESMFakeMethodContainer() ;
    }
    /** Sets the tdqueue open. */
    public CCESMFakeMethodContainer setTDQueueOpen(Var queue)
    {
        return setTDQueueOpen(queue.getString()) ;
    }

    /** Writes the transiant queue. */
    public CESMWriteQueue writeTransiantQueue(String name)
    {
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL);
        return new CESMWriteQueue(true, name, cESMEnv.getQueueManager()) ;
    }
    /** Writes the transiant queue. */
    public CESMWriteQueue writeTransiantQueue(Var name)
    {
        return writeTransiantQueue(name.getString()) ;
    }
    /** Writes the transiant queue. */
    public CESMWriteQueue writeTransiantQueue(String name, Var rewriteItem)
    {
        return writeTransiantQueue(name).rewrite(rewriteItem.getInt()) ;
    }
    /** Writes the transiant queue. */
    public CESMWriteQueue writeTransiantQueue(Var name, Var rewriteItem)
    {
        return writeTransiantQueue(name.getString(), rewriteItem) ;
    }
    /** Writes the transiant queue. */
    public CESMWriteQueue writeTransiantQueue(String name, int rewriteItem)
    {
        return writeTransiantQueue(name).rewrite(rewriteItem) ;
    }
    /** Writes the transiant queue. */
    public CESMWriteQueue writeTransiantQueue(Var name, int rewriteItem)
    {
        return writeTransiantQueue(name.getString(), rewriteItem) ;
    }

    /** Returns the main. */
    public CCESMFakeMethodContainer getMain()
    {
        throw unsupported("GETMAIN without a storage target");
    }

    /** Returns the current day. */
    public String getCurrentDay()
    {
        Calendar calendar = Calendar.getInstance() ;
        int day = calendar.get(Calendar.DAY_OF_MONTH) ;
        String cs = "" + (day/10) + (day%10) ;
        return cs ;
    }

    /** Returns the current date. */
    public Calendar getCurrentDate()
    {
        Calendar calendar = Calendar.getInstance() ;
        return calendar;
    }

    /** Returns the current month. */
    public String getCurrentMonth()
    {
        Calendar calendar = Calendar.getInstance() ;
        int n = calendar.get(Calendar.MONTH) +1 ;
        String cs = "" + (n/10) + (n%10) ;
        return cs ;
    }

    /** Returns the current short year. */
    public String getCurrentShortYear()
    {
        Calendar calendar = Calendar.getInstance() ;
        int n = calendar.get(Calendar.YEAR) ;
        String cs = "" + ((n%100)/10) + (n%10) ;
        return cs ;
    }

    /** Executes the ask time operation. */
    public void askTime()
    {
        if (isLogCESM) {
            Log.logDebug("askTime");
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.ASKTIME) ;
        cESMEnv.resetDateTime() ;
    }

    /** Executes the inquire operation. */
    public CCESMFakeMethodContainer inquire()
    {
        if (isLogCESM) {
            Log.logDebug("inquire");
        }
        throw unsupported("INQUIRE without PROGRAM and TRANSACTION resolution");
    }

    /** Reads the temp queue. */
    public CESMReadQueue readTempQueue(Var varName)
    {
        return readTempQueue(varName.getString());
    }
    /** Reads the temp queue. */
    public CESMReadQueue readTempQueue(String csName)
    {
        if (isLogCESM) {
            Log.logDebug("readTempQueue " + csName);
        }

        cESMEnv.setLastCommandCode(CESMCommandCode.READ_TEMPQUEUE);
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;

        return new CESMReadQueue(false, csName, cESMEnv.getQueueManager());
    }

    /** Reads the transiant queue. */
    public CESMReadQueue readTransiantQueue(Var name)
    {
        return readTransiantQueue(name.getString());
    }

    /** Reads the transiant queue. */
    public CESMReadQueue readTransiantQueue(String name)
    {
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL);
        return new CESMReadQueue(true, name, cESMEnv.getQueueManager());
    }

    /** Executes the delete temp queue operation. */
    public CCESMFakeMethodContainer deleteTempQueue(Var varName)
    {
        deleteTempQueue(varName.getString());
        return new CCESMFakeMethodContainer();
    }
    /** Executes the delete temp queue operation. */
    public void deleteTempQueue(String csName)
    {
        if (isLogCESM) {
            Log.logDebug("deleteTempQueue " + csName);
        }

        cESMEnv.setLastCommandCode(CESMCommandCode.DELETE_TEMPQUEUE);
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;

        CESMQueueManager queueManager = cESMEnv.getQueueManager();
        queueManager.deleteTempQueue(csName);
    }

    /** Executes the delete transiant queue operation. */
    public CCESMFakeMethodContainer deleteTransiantQueue(Var varName)
    {
        return deleteTransiantQueue(varName.getString());
    }

    /** Executes the delete transiant queue operation. */
    public CCESMFakeMethodContainer deleteTransiantQueue(String queueName)
    {
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL);
        cESMEnv.getQueueManager().deleteQueue(true, queueName);
        return new CCESMFakeMethodContainer();
    }


    /** Writes the temp queue. */
    public CESMWriteQueue writeTempQueue(Var varName)
    {
        return writeTempQueue(varName.getString());
    }
    /** Writes the temp queue. */
    public CESMWriteQueue writeTempQueue(String csName)
    {
        if (isLogCESM) {
            Log.logDebug("writeTempQueue " + csName);
        }

        cESMEnv.setLastCommandCode(CESMCommandCode.WRITE_TEMPQUEUE);
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;

        return new CESMWriteQueue(false, csName, cESMEnv.getQueueManager());
    }

    /** Writes the temp queue. */
    public CESMWriteQueue writeTempQueue(Var tsNom, Var reWriteItem)
    {
        if (isLogCESM) {
            Log.logDebug("writeTempQueue " + tsNom.getLoggableValue());
        }

        cESMEnv.setLastCommandCode(CESMCommandCode.WRITE_TEMPQUEUE);
        cESMEnv.setCommandReturnCode(CESMReturnCode.NORMAL) ;

        String name = tsNom.getString();
        CESMWriteQueue writeorder = new CESMWriteQueue(false, name, cESMEnv.getQueueManager());
        int item = reWriteItem.getInt() ;
        writeorder.rewrite(item) ;
        return writeorder ;
    }

    /** Writes the temp queue. */
    public CESMWriteQueue writeTempQueue(String name, Var rewriteItem)
    {
        return writeTempQueue(name).rewrite(rewriteItem.getInt()) ;
    }

    /** Writes the temp queue. */
    public CESMWriteQueue writeTempQueue(String name, int rewriteItem)
    {
        return writeTempQueue(name).rewrite(rewriteItem) ;
    }

    /** Writes the temp queue. */
    public CESMWriteQueue writeTempQueue(Var name, int rewriteItem)
    {
        return writeTempQueue(name.getString(), rewriteItem) ;
    }

    /** Reads the data set. */
    public CCESMFakeMethodContainer readDataSet(Var var)
    {
        return readDataSet(var.getString());
    }
    /** Reads the data set. */
    public CCESMFakeMethodContainer readDataSet(String string)
    {
        throw unsupported("READ DATASET");
    }

    /** Reads the file. */
    public CCESMFakeMethodContainer readFile(Var name)
    {
        return readFile(name.getString());
    }

    /** Reads the file. */
    public CCESMFakeMethodContainer readFile(String name)
    {
        throw unsupported("READ FILE");
    }

    /** Reads the previous data set. */
    public CCESMFakeMethodContainer readPreviousDataSet(String name)
    {
        throw unsupported("READPREV DATASET");
    }

    /** Reads the previous file. */
    public CCESMFakeMethodContainer readPreviousFile(Var name)
    {
        return readPreviousFile(name.getString());
    }

    /** Reads the previous file. */
    public CCESMFakeMethodContainer readPreviousFile(String name)
    {
        throw unsupported("READPREV FILE");
    }

    /** Reads the next file. */
    public CCESMFakeMethodContainer readNextFile(Var name)
    {
        return readNextFile(name.getString());
    }

    /** Reads the next file. */
    public CCESMFakeMethodContainer readNextFile(String name)
    {
        throw unsupported("READNEXT FILE");
    }

    /** Writes the data set. */
    public CCESMFakeMethodContainer writeDataSet(Var var)
    {
        return writeDataSet(var.getString());
    }
    /** Writes the data set. */
    public CCESMFakeMethodContainer writeDataSet(String string)
    {
        throw unsupported("WRITE DATASET");
    }

    /** Writes the file. */
    public CCESMFakeMethodContainer writeFile(Var file)
    {
        return writeFile(file.getString());
    }

    /** Writes the file. */
    public CCESMFakeMethodContainer writeFile(String file)
    {
        throw unsupported("WRITE FILE");
    }

    /** Executes the re write data set operation. */
    public CCESMFakeMethodContainer reWriteDataSet(String string)
    {
        throw unsupported("REWRITE DATASET");
    }

    /** Executes the re write data set operation. */
    public CCESMFakeMethodContainer reWriteDataSet(Var var)
    {
        return reWriteDataSet(var.getString());
    }

    /** Executes the re write file operation. */
    public CCESMFakeMethodContainer reWriteFile(String name)
    {
        throw unsupported("REWRITE FILE");
    }

    /** Executes the re write file operation. */
    public CCESMFakeMethodContainer reWriteFile(Var name)
    {
        return reWriteFile(name.getString());
    }

    /** Executes the start operation. */
    public CESMStart start(String csTransaction)
    {
        return start(csTransaction, true);
    }
    /** Executes the start operation. */
    public CESMStart start(Var varTransaction)
    {
        return start(varTransaction.getString(), true);
    }
    /** Executes the start operation. */
    public CESMStart start(Class cl)
    {
        return start(cl.getName(), false);
    }
    private CESMStart start(String csProgramId, boolean bResolveProgram)
    {
        if (bResolveProgram) {
            csProgramId = BaseProgramLoader.ResolveTransID(csProgramId);
        }

        if (isLogCESM) {
            Log.logDebug("start " + csProgramId);
        }
        cESMEnv.setCommarea(null);
        return new CESMStart(csProgramId, cESMEnv);
    }

    /** Executes the sync point rollback operation. */
    public void syncPointRollback()
    {
        if(cESMEnv.hasSQLConnection())
        {
            if (isLogCESM) {
                Log.logDebug("syncPointRollback");
            }
            cESMEnv.rollbackSQL();
        }
        else
        {
            if (isLogCESM) {
                Log.logDebug("syncPointRollback: Nothing to do: No connection opened");
            }
        }
    }

    /** Executes the sync point commit operation. */
    public void syncPointCommit()
    {
        if(cESMEnv.hasSQLConnection())
        {
            if (isLogCESM) {
                Log.logDebug("syncPointCommit");
            }
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
            if (isLogCESM) {
                Log.logDebug("syncPointCommit: Nothing to do: No connection opened");
            }
        }
    }

    /** Executes the link operation. */
    public CESMLink link(Var varProgram)
    {
        return link(varProgram.getString().trim());
    }
    /** Executes the link operation. */
    public CESMLink link(Class cl)
    {
        return link(cl.getName());
    }
    /** Executes the link operation. */
    public CESMLink link(String csProgramName)
    {
        if (isLogCESM) {
            Log.logDebug("link " + csProgramName);
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.LINK );
        cESMEnv.setCommarea(null);
        return new CESMLink(cESMEnv, csProgramName);
    }

    /** Executes the xctl operation. */
    public CESMXctl xctl(Class cl)
    {
        return xctl(cl.getName());
    }
    /** Executes the xctl operation. */
    public CESMXctl xctl(Var varProgram)
    {
        return xctl(varProgram.getString());
    }
    /** Executes the xctl operation. */
    public CESMXctl xctl(String csProgram)
    {
        if (isLogCESM) {
            Log.logDebug("xctl " + csProgram);
        }
        cESMEnv.setLastCommandCode(CESMCommandCode.XCTL);
        cESMEnv.setCommarea(null);
        return new CESMXctl(cESMEnv, csProgram);
    }

    public String getLastCommandCode()
    {
        return cESMEnv.getLastCommandCode() ;
    }
    /** Executes the handle aid operation. */
    public BaseCESMManager handleAID(String cond, CJMapRunnable target)
    {
        if (target == null)
        {
            throw new IllegalArgumentException("CICS HANDLE AID target must not be null");
        }
        aidHandlers.put(normalizeCondition(cond), target);
        return this;
    }

    /** Executes the unhandle aid operation. */
    public BaseCESMManager unhandleAID(String cond)
    {
        aidHandlers.remove(normalizeCondition(cond));
        return this;
    }

    /** Returns the aidhandler. */
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
