/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.basePrgEnv;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import jlib.log.Log;
import jlib.misc.CurrentDateInfo;
import jlib.misc.JVMReturnCodeManager;
import jlib.misc.ThreadSafeCounter;
import jlib.misc.Time_ms;
import jlib.sql.DbConnectionBase;
import jlib.sql.DbConnectionException;
import jlib.sql.DbConnectionManagerBase;
import jlib.xml.Tag;
import nacaLib.CESM.CESMQueueManager;
import nacaLib.CESM.CESMReturnCode;
import nacaLib.CESM.CESMStartData;
import nacaLib.accounting.AccountingRecordProgram;
import nacaLib.accounting.AccountingRecordTrans;
import nacaLib.accounting.CriteriaEndRunMain;
import nacaLib.base.CJMapObject;
import nacaLib.exceptions.AbortSessionException;
import nacaLib.misc.CCommarea;
import nacaLib.misc.KeyPressed;
import nacaLib.sqlSupport.SQLConnection;
import nacaLib.tempCache.TempCache;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.Var;

import org.w3c.dom.Document;

/** Provides base environment behavior. */
public abstract class BaseEnvironment extends CJMapObject implements SessionEnvironmentRequester
{
    private DbConnectionBase qLConnection = null;
    private DbConnectionManagerBase connectionManager = null;
    protected String csCurrentTransaction = "" ;
    private String csTransaction1stProgram = "";
    private BaseSession baseSession = null;
    private Integer envId = null;
    private static ThreadSafeCounter ms_id = new ThreadSafeCounter();
    private CurrentDateInfo creationDateInfo = null;
    private int sumTransactionsExecTimeMillis = 0;
    private int nNbTransactionsExecuted = 0;
    private boolean isinitialConnectDb = true;  // true if db conection is established before lauchin 1st program
    private FileManager fileManager = null;
    private boolean isexternalConnection = false;
    private boolean issimulateRealEnvironment = false;

    /** Creates a new base environment instance. */
    public BaseEnvironment(BaseSession baseSession, DbConnectionManagerBase connectionManager, BaseResourceManager baseResourceManager)
    {
        this.baseSession = baseSession;
        this.connectionManager = connectionManager ;
        queueManager = new CESMQueueManager(this);

        accountingRecordManager = new AccountingRecordTrans(baseResourceManager);
        envId = ms_id.inc();
        creationDateInfo = new CurrentDateInfo();
        issimulateRealEnvironment = baseResourceManager.getSimulateRealEnvironment();
    }

    public DbConnectionManagerBase getDbConnectionManager()
    {
        return connectionManager;
    }

    public CurrentDateInfo getCreationDateInfo()
    {
        return creationDateInfo;
    }

    public Integer getEnvId()
    {
        return envId;
    }

    public BaseSession getBaseSession()
    {
        return baseSession;
    }

    /** Resets the session. */
    public void resetSession()
    {
        baseSession = null;
    }

    /** Returns the last screen xmldata. */
    public Document getLastScreenXMLData()
    {
        if (baseSession != null) {
            return baseSession.getLastScreenXMLData();
        }
        return null;
    }

    /** Sets the current transaction. */
    public void setCurrentTransaction(String csTransactionID, String csProgramID)
    {
        csCurrentTransaction = csTransactionID ;
        csTransaction1stProgram = csProgramID ;
    }

    public String getCurrentTransaction()
    {
        return csCurrentTransaction ;
    }

    /** Executes the debugremove dbconnection operation. */
    public void DEBUGremoveDBConnection()
    {
        qLConnection = null;
    }

    /** Executes the fill env connection with allocated connection operation. */
    public void fillEnvConnectionWithAllocatedConnection(
        Connection spConnection,
        String csPrefId,
        String csEnv,
        boolean bUseCachedStatements)
    {
        qLConnection = new SQLConnection(spConnection, csPrefId, csEnv, bUseCachedStatements, false, null);
    }

    /** Returns the new sqlconnection. */
    public DbConnectionBase getNewSQLConnection()
    {
        if(connectionManager != null)
        {
            try
            {
                DbConnectionBase newSQLConnection = connectionManager.getNewConnection(
                    csTransaction1stProgram,
                    BaseResourceManager.getUseStatementCache()) ;
                return newSQLConnection;
            }
            catch (DbConnectionException e)
            {
                Log.logImportant("Db connection error: "+e.toString());
            }
        }
        return null;
    }

    /** Returns the sqlconnection. */
    public DbConnectionBase getSQLConnection()
    {
        if(qLConnection == null && connectionManager != null)
        {
            try
            {
                qLConnection = connectionManager.getConnection(
                    csTransaction1stProgram,
                    csProgramParent,
                    BaseResourceManager.getUseStatementCache()) ;
            }
            catch (DbConnectionException e)
            {
                Log.logImportant("Db connection error: "+e.toString());

                //JVMReturnCodeManager.exitJVM(8);  // No connection provided: Do not exit as it kills tomcat !
            }
        }
        return qLConnection;
    }

    /** Executes the abort trans when invalid db connection operation. */
    public boolean abortTransWhenInvalidDbConnection()
    {
        if(!hasSQLConnection())
        {
            endRunTransaction(CriteriaEndRunMain.Abort);
            JVMReturnCodeManager.exitJVM(8);    // No connection provided
            return false;
        }
        return true;
    }

    /** Returns whether s sqlconnection. */
    public boolean hasSQLConnection()
    {
        if (qLConnection == null) {
            return false;
        }
        return true;
    }

    /** Executes the release sqlconnection operation. */
    public void releaseSQLConnection()
    {
        if (!isexternalConnection)   // Release only internal connection
            {
                if (qLConnection != null)
                {
                    if (connectionManager != null) {
                        connectionManager.returnConnection(qLConnection);
                    }
                }
            } else {
            isexternalConnection = false;   // Not an external connection (reset status for next reuse of the environment)

            // The environment has no knowledge anymore of the connection
        }
        qLConnection = null;
    }

    private void getTempCacheFromStack()
    {
        tempCache = TempCacheLocator.setTempCache();
    }

    /** Executes the return temp cache to stack operation. */
    public void returnTempCacheToStack()
    {
        if (tempCache != null)
        {
            TempCacheLocator.relaseTempCache();
            tempCache = null;
        }
    }

    private TempCache tempCache = null;

    /**
     *
     */
    public SQLException commitSQL()
    {
        if(!isexternalConnection)
        {
            if (qLConnection != null)
            {
                return qLConnection.commitWithException() ;
            }
        }
        return null;
    }

    /**
     *
     */
    public SQLException rollbackSQL()
    {
        if(!isexternalConnection)
        {
            if (qLConnection != null)
            {
                return qLConnection.rollBackWithException() ;
            }
        }
        return null;
    }

    public String getNextProgramToLoad()
    {
        return csNextProgramToLoad ;
    }

    /** Sets the next program to load. */
    public void setNextProgramToLoad(String csProgramId)
    {
        csNextProgramToLoad = csProgramId.trim() ;
        csProgramParent = null;
    }

    /** Sets the next program to load. */
    public void setNextProgramToLoad(String csProgramId, String csProgramParent)
    {
        csNextProgramToLoad = csProgramId.trim() ;
        this.csProgramParent = csProgramParent;
    }

    protected void deQueueProgram()
    {
        if (!qPrograms.isEmpty()) {
            csNextProgramToLoad = (String) qPrograms.remove();
        } else {
            csNextProgramToLoad = "";
        }
        commarea = null ;

    }

    /** Executes the do enqueue program operation. */
    public void doEnqueueProgram(String csProg)
    {
        qPrograms.add(csProg);
    }

    private String csNextProgramToLoad = "" ;
    private String csProgramParent = null;
    private Queue qPrograms = new SynchronousQueue() ;

    private CCommarea commarea = null ;

    public CCommarea getCommarea()
    {
        return commarea ;
    }

    public void setCommarea(CCommarea commarea)
    {
        this.commarea = commarea ;
    }

    /** Resets the new transaction. */
    public void resetNewTransaction()
    {
        doResetNewTransaction();
    }

    protected void doResetNewTransaction()
    {
        resetDateTime() ;
        setNextProgramToLoad("");
    }

    private Date startTime = new Date() ;

    /** Resets the date time. */
    public void resetDateTime()
    {
        startTime = new Date() ;
    }

    /** Returns the time. */
    public String getTime()
    {
        SimpleDateFormat formater = new SimpleDateFormat("'0'HHmmss");
        String cs = formater.format(startTime);
        return cs ;
    }

    /** Returns the date. */
    public String getDate()
    {
        SimpleDateFormat formater  ;
        //Calendar cal = Calendar.getInstance() ;
        formater = new SimpleDateFormat("'01'yyDDD");
        String cs = formater.format(startTime);
        return cs ;
    }

    /** Returns whether s output. */
    public boolean hasOutput()
    {
        return false;
    }

    /** Executes the register output operation. */
    public void RegisterOutput()
    {
    }

    private Tag tagConfig = null ;


    /** Executes the init operation. */
    public void Init(Tag tagCESMConfig)
    {
        configInit(tagCESMConfig);
    }

    protected void configInit(Tag tagCESMConfig)
    {
        if (tagCESMConfig != null) {
            tagConfig = tagCESMConfig.getChild("Config");
        }
    }

    public String getLanguageCode()
    {
        return "";
    }

    /** Returns whether linux. */
    public boolean isLinux()
    {
        String linux = getConfigOption("StartBatchLinux");
        return Boolean.parseBoolean(linux);
    }
    /**
     * @param string
     * @return
     */
    public String getConfigOption(String string)
    {
        if (tagConfig != null)
        {
            return tagConfig.getVal(string);
        }
        return "";
    }

    public String getUserLanguageId()
    {
        return "";
    }

    public String getProfitCenter()
    {
        return "";
    }

    public String getCmpSession()
    {
        return "";
    }

    public String getUserId()
    {
        return "";
    }

    public String getUserLdapId()
    {
        return "";
    }


    /** Creates the cesmmanager. */
    public abstract BaseCESMManager createCESMManager();
    /** Returns the session. */
    public abstract BaseSession getSession();


    protected String csLastCommandCode = "" ;
    public String getLastCommandCode()
    {
        return csLastCommandCode ;
    }

    public void setLastCommandCode(String string)
    {
        csLastCommandCode = string ;
    }


    private CESMReturnCode lastCommandReturnCode = CESMReturnCode.NORMAL ;
    public CESMReturnCode getLastCommandReturnCode()
    {
        return lastCommandReturnCode ;
    }

    public void setCommandReturnCode(CESMReturnCode cs)
    {
        lastCommandReturnCode = cs ;
    }


    private CESMQueueManager queueManager = null;
    public CESMQueueManager getQueueManager()
    {
        return queueManager;
    }

    public String getTerminalID()
    {
        return csTermID ;
    }
    protected String csTermID = "" ;


    private Queue qData = new SynchronousQueue() ;

    /** Executes the enqueue program operation. */
    public void enqueueProgram(String csTransID, CESMStartData data)
    {
        doEnqueueProgram(csTransID);
        enqueueData(data);
    }

    /** Executes the enqueue data operation. */
    public void enqueueData(CESMStartData data)
    {
        qData.add(data) ;
    }


    /** Executes the get enqueued data operation. */
    public CESMStartData GetEnqueuedData()
    {
            if (qData.isEmpty())
            {
                return null ;
            }
            CESMStartData v = (CESMStartData)qData.remove() ;
            return v ;
    }

    /** Executes the start asynchronous program operation. */
    public void StartAsynchronousProgram(String transID, String csProgramParent, CESMStartData data, int intervalTimeSeconds)
    {
        BaseProgramLoader.StartAsynchronousProgram(transID, csProgramParent, data, intervalTimeSeconds);
    }

    /**
     * @return
     */
    public String getApplicationCredentials()
    {
        return csApplicationCredentials ;
    }
    /** Resets the application credentials. */
    public void resetApplicationCredentials(String cs)
    {
        csApplicationCredentials = cs ;
    }
    protected String csApplicationCredentials = "" ;



    protected char [] acTCTTUA = new char [1024];
    public char [] getTCTUA()
    {
        return acTCTTUA ;
    }


    protected char [] acTWA = new char [1024];
    public char [] getTWA()
    {
        return acTWA ;
    }

    protected char [] acCWA = new char [1024];
    public char [] getCWA()
    {
        return acCWA ;
    }

    public Document getXMLData()
    {
        return null;
    }

    protected KeyPressed keyPressed = null ;
    /** Executes the get key pressed operation. */
    public KeyPressed GetKeyPressed()
    {
        if (keyPressed != null)
        {
            return keyPressed;
        }
        else
        {
            return null ;
        }
    }

    /** Resets the key pressed. */
    public void resetKeyPressed()
    {
        keyPressed = null ;
    }

    /** Sets the key pressed. */
    public void setKeyPressed(Var v)
    {
        keyPressed = KeyPressed.getKey(v);
        assertIfNull(keyPressed);
    }

    public void setKeyPressed(KeyPressed keyPressed)
    {
        this.keyPressed = keyPressed;
//      assertIfNull(keyPressed);
    }

    // Accounting
    public void setInitialConnectDb(boolean bInitialConnectDb)
    {
        this.isinitialConnectDb = bInitialConnectDb;
    }

    /** Sets the external db connection. */
    public void setExternalDbConnection(DbConnectionBase dbConnection)
    {
        if(dbConnection != null)    // Provide an external db connection by caller
        {
            isinitialConnectDb = false;
            qLConnection = dbConnection;
            isexternalConnection = true;
        }
        else    // No db connection provided: It means that nacaRT must estblish itslef the connection
        {
            isinitialConnectDb = true;
        }
    }

    /** Executes the start run transaction operation. */
    public boolean startRunTransaction()
    {
        if(issimulateRealEnvironment)
        {
            abStopProcessing.set(false);
            getTempCacheFromStack();
            accountingRecordManager.startRunTransaction(csCurrentTransaction);
            startSessionRequest(csCurrentTransaction);

            TransThreadManager.startTransaction(this);
            return true;
        }

        boolean isstarted = true;
        abStopProcessing.set(false);

        getTempCacheFromStack();

        if(isinitialConnectDb)
        {
            qLConnection = null;
            DbConnectionBase con = getSQLConnection();  // Establish a sql connection before lauching 1st program
            if (con == null) {
                isstarted = false;
            }
        }

        if(isstarted)
        {
            accountingRecordManager.startRunTransaction(csCurrentTransaction);
            startSessionRequest(csCurrentTransaction);

            TransThreadManager.startTransaction(this);
        }
        return isstarted;
    }

    /** Executes the end run transaction operation. */
    public void endRunTransaction(CriteriaEndRunMain criteria)
    {
        if(accountingRecordManager != null)
        {
            accountingRecordManager.endRunTransaction(csCurrentTransaction, criteria);
        }
        endSessionRequest();
        TransThreadManager.endTransaction(this);
    }

    void startRunProgram(String csProgramName)
    {
        if (!accountingRecordManager.isFilled()) {
            accountingRecordManager.setSessionPub2000Info(getSession(), getProfitCenter(), getUserId());
        }

        AccountingRecordProgram accountingRecord = accountingRecordManager.createNewAccountingRecord(csCurrentTransaction, csTermID);
        accountingRecord.beginRunProgram(csProgramName);
    }

    void endRunProgram(CriteriaEndRunMain criteria)
    {
        accountingRecordManager.endRunProgram(criteria);
    }

    public AccountingRecordTrans getAccountingRecordManager()
    {
        return accountingRecordManager;
    }

    private AccountingRecordTrans accountingRecordManager = null;


    // Anti-loop management
    private void startSessionRequest(String csCurrentTransaction)
    {
        dateStart.setNow();
        sessionRequestEndBeforeMillis = BaseResourceManager.getSessionRequestEndTimeLimit(csCurrentTransaction);
        envStatus = EnvironmentStatus.RUNNING;
    }

    void offsetMaxTimeLimit(long lOffsetMs)
    {
        sessionRequestEndBeforeMillis += lOffsetMs;
    }

    private void endSessionRequest()
    {
        nNbTransactionsExecuted++;
        dateEnd.setNow();
        sessionRequestEndBeforeMillis = 0; // No running
        envStatus = EnvironmentStatus.STOPPED;
        sumTransactionsExecTimeMillis += (int)getStartRunTime().getTimeOffset_ms(getEndRunTime());
    }

    void requestStopProcessing()
    {
        envStatus = EnvironmentStatus.STOP_REQUESTED;
        abStopProcessing.set(true);
    }

    CurrentDateInfo getStartRunTime()
    {
        return dateStart;
    }

    CurrentDateInfo getEndRunTime()
    {
        return dateEnd;
    }

    int getLastTransactionExecTime_ms()
    {
        if (!isRunning()) {
            return (int) getStartRunTime().getTimeOffset_ms(getEndRunTime());
        }
        return (int)getStartRunTime().getTimeOffsetFromNow_ms();
    }

    int getSumTransactionsExecTime_ms()
    {
        if (isRunning()) {
            return (int) getStartRunTime().getTimeOffset_ms(getEndRunTime()) + sumTransactionsExecTimeMillis;
        }
        return (int)sumTransactionsExecTimeMillis;
    }

    int getNbTransactionsExecuted()
    {
        return nNbTransactionsExecuted;
    }

    String getStatusAsString()
    {
        return envStatus.getString();
    }

    boolean isRunning()
    {
        return envStatus.isRunning();
    }

    int getRunningTime_ms()
    {
        CurrentDateInfo now = new CurrentDateInfo();
        int n = (int)(now.getTimeInMillis() - dateStart.getTimeInMillis());
        return n;
    }

    /** Executes the break current session if timeout operation. */
    public void breakCurrentSessionIfTimeout()
    {
        if(abStopProcessing.get())  // Forced stop
        {
            AbortSessionException exp = new AbortSessionException() ;
            exp.reason = new Error("SessionForcedStop");
            exp.programName = null;  // register current program that throws the exception.
            throw exp ;
        }

        if(sessionRequestEndBeforeMillis != 0)
        {
            long almostCurrentTimeMs = Time_ms.getCurrentTime_ms();
            if(almostCurrentTimeMs > sessionRequestEndBeforeMillis)
            {
                AbortSessionException exp = new AbortSessionException() ;
                exp.reason = new Error("SessionTimeoutInternal");
                exp.programName = null;  // register current program that throws the exception.
                throw exp ;
            }
        }
    }

    boolean canManageThreadMBean()
    {
        if (accountingRecordManager != null) {
            return true;
        }
        return false;
    }

    /** Returns the society code. */
    public String getSocietyCode()
    {
        String cs = "   ";
        return cs ;
    }

    /** Returns the application. */
    public String getApplication()
    {
        String cs = "  ";
        return cs ;
    }

    /** Returns the file manager entry. */
    public FileManagerEntry getFileManagerEntry(String csLogicalName)
    {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager.getFileManagerEntry(csLogicalName);
    }

    /** Executes the auto close open file operation. */
    public void autoCloseOpenFile()
    {
        if (fileManager != null) {
            fileManager.autoCloseOpenFile();
        }
    }

    /** Executes the auto flush open file operation. */
    public void autoFlushOpenFile()
    {
        if (fileManager != null) {
            fileManager.autoFlushOpenFile();
        }
    }

    /** Executes the cleanup on exception catched operation. */
    public void cleanupOnExceptionCatched()
    {
        rollbackSQL() ;
        releaseSQLConnection() ;
        autoCloseOpenFile();
        returnTempCacheToStack();
    }

    private static int ms_LastJobBatchID = 0 ;
    /** Returns the next job batch id. */
    public static String getNextJobBatchID()
    {
        int n = ms_LastJobBatchID ++ ;
        return "" + (n/1000)%10 + (n/100)%10 + (n/10)%10 + (n)%10 ;
    }

    private AtomicBoolean abStopProcessing = new AtomicBoolean(false);
    private long sessionRequestEndBeforeMillis = 0;
    private CurrentDateInfo dateStart = new CurrentDateInfo();
    private CurrentDateInfo dateEnd  = new CurrentDateInfo();
    private EnvironmentStatus envStatus = EnvironmentStatus.UNKNOWN;
    private String display;

    public void setDisplay(String display)
    {
        this.display = display;
    }

    public String getDisplayValue()
    {
        return System.getProperty(display, "");
    }
}
