/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.basePrgEnv;

import java.util.Hashtable;

import jlib.misc.LogicalFileDescriptor;
import jlib.sql.DbConnectionManagerBase;

import org.w3c.dom.Document;

/** Provides base session behavior. */
public abstract class BaseSession
{
    protected BaseResourceManager baseResourceManager = null;
    private boolean bUseJmx = true;

    /** Executes the fill current user info operation. */
    public abstract void fillCurrentUserInfo(CurrentUserInfo currentUserInfo);
    /** Returns the last screen xmldata. */
    public abstract Document getLastScreenXMLData();

    /** Creates a new base session instance. */
    public BaseSession(BaseResourceManager baseResourceManager)
    {
        this.baseResourceManager = baseResourceManager;
        bUseJmx = this.baseResourceManager.getUsingJmx();
//      if(bUseJmx)
//          JmxGeneralStat.incNbSession();
    }

//  public void finalize()
//  {
//      if(bUseJmx)
//          JmxGeneralStat.decNbSession();
//  }

    public BaseResourceManager getBaseResourceManager()
    {
        return baseResourceManager;
    }

    /** Creates the environment. */
    public abstract BaseEnvironment createEnvironment(DbConnectionManagerBase connectionManager);
    /** Returns the type. */
    public abstract String getType();


    /** Executes the run program operation. */
    public abstract void RunProgram(BaseProgramLoader seq);

    /** Sets the help page. */
    public abstract void setHelpPage(Document doc);


//  public void addBatchFile(String csLogicalName, String csPath, boolean bEbcdicFile,  boolean bExt, int nLength)
//  {
//      LogicalFileDescriptor fd = new LogicalFileDescriptor(csPath, bEbcdicFile,  bExt, nLength);
//      if(m_hashLogicalFileDescriptors == null)
//          m_hashLogicalFileDescriptors = new Hashtable<String, LogicalFileDescriptor>();
//      m_hashLogicalFileDescriptors.put(csLogicalName, fd);
//  }

    /** Returns the logical file descriptor. */
    public LogicalFileDescriptor getLogicalFileDescriptor(String csLogicalName)
    {
        if (hashLogicalFileDescriptors != null) {
            return hashLogicalFileDescriptors.get(csLogicalName);
        }
        return null;
    }

    /** Executes the put logical file descriptor operation. */
    public void putLogicalFileDescriptor(String csLogicalName, LogicalFileDescriptor logicalFileDescriptor)
    {
        if (hashLogicalFileDescriptors == null) {
            hashLogicalFileDescriptors = new Hashtable<String, LogicalFileDescriptor>();
        }
        hashLogicalFileDescriptors.put(csLogicalName, logicalFileDescriptor);
    }

    /** Removes the logical file descriptor. */
    public void removeLogicalFileDescriptor(String csLogicalName)
    {
        if (hashLogicalFileDescriptors != null) {
            hashLogicalFileDescriptors.remove(csLogicalName);
        }
    }

    private Hashtable<String, LogicalFileDescriptor> hashLogicalFileDescriptors = null;

    /** Adds the batch info. */
    public void addBatchInfo(String csInfo, String csValue)
    {
        if (hashLogicalJobInfo == null) {
            hashLogicalJobInfo = new Hashtable<String, String>();
        }
        hashLogicalJobInfo.put(csInfo, csValue);
    }

    /** Returns the logical job info. */
    public String getLogicalJobInfo(String csInfo)
    {
        if (hashLogicalJobInfo != null) {
            return hashLogicalJobInfo.get(csInfo);
        }
        return "";
    }

    private Hashtable<String, String> hashLogicalJobInfo = null;

    public boolean isAsync()
    {
        return isasync;
    }

    protected void setAsync(boolean b)
    {
        this.isasync = b;
    }

    private boolean isasync = false;

    /** Returns the dynamic allocation info. */
    public String getDynamicAllocationInfo(String csKey)
    {
        if (hashDynamicAllocationInfo != null) {
            return hashDynamicAllocationInfo.get(csKey);
        }
        return null;
    }

    /** Adds the dynamic allocation info. */
    public void addDynamicAllocationInfo(String csKey, String csValue)
    {
        if (hashDynamicAllocationInfo == null) {
            hashDynamicAllocationInfo = new Hashtable<String, String>();
        }
        hashDynamicAllocationInfo.put(csKey, csValue);
    }

    /** Resets the dynamic allocation info. */
    public void resetDynamicAllocationInfo()
    {
        hashDynamicAllocationInfo.clear();
    }

    private Hashtable<String, String> hashDynamicAllocationInfo = null;

    private static int ms_LastDynamicAllocationID = 0 ;
    /** Returns the next dynamic allocation id. */
    public String getNextDynamicAllocationID()
    {
        int n = ms_LastDynamicAllocationID++;
        return "" + (n/100)%10 + (n/10)%10 + (n)%10 ;
    }

    private int networkMillis = 0;
    public int getNetwork_ms()
    {
        return networkMillis;
    }
    public void setNetwork_ms(int nNetworkMs)
    {
        this.networkMillis = nNetworkMs;
    }

    /** Returns the special object. */
    public Object getSpecialObject(String csKey)
    {
        if (hashSpecialObject != null) {
            return hashSpecialObject.get(csKey);
        }
        return null;
    }

    /** Adds the special object. */
    public void addSpecialObject(String csKey, Object object)
    {
        if (hashSpecialObject == null) {
            hashSpecialObject = new Hashtable<String, Object>();
        }
        hashSpecialObject.put(csKey, object);
    }

    /** Removes the special object. */
    public void removeSpecialObject(String csKey)
    {
        if (hashSpecialObject != null) {
            hashSpecialObject.remove(csKey);
        }
    }

    /** Resets the special object. */
    public void resetSpecialObject()
    {
        hashSpecialObject.clear();
    }

    private Hashtable<String, Object> hashSpecialObject = null;
}
