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

public abstract class BaseSession
{
	protected BaseResourceManager baseResourceManager = null;
	private boolean bUseJmx = true;	
	
	public abstract void fillCurrentUserInfo(CurrentUserInfo currentUserInfo);
	public abstract Document getLastScreenXMLData();
	
	public BaseSession(BaseResourceManager baseResourceManager)
	{
		this.baseResourceManager = baseResourceManager;
		bUseJmx = this.baseResourceManager.getUsingJmx();
//		if(bUseJmx)
//			JmxGeneralStat.incNbSession();
	}
	
//	public void finalize()
//	{
//		if(bUseJmx)
//			JmxGeneralStat.decNbSession();
//	}

	public BaseResourceManager getBaseResourceManager() 
	{
		return baseResourceManager;
	}
	
	public abstract BaseEnvironment createEnvironment(DbConnectionManagerBase connectionManager);
	public abstract String getType();
	
	
	public abstract void RunProgram(BaseProgramLoader seq);
	
	public abstract void setHelpPage(Document doc);
	
	
//	public void addBatchFile(String csLogicalName, String csPath, boolean bEbcdicFile,  boolean bExt, int nLength)
//	{
//		LogicalFileDescriptor fd = new LogicalFileDescriptor(csPath, bEbcdicFile,  bExt, nLength); 
//		if(m_hashLogicalFileDescriptors == null)
//			m_hashLogicalFileDescriptors = new Hashtable<String, LogicalFileDescriptor>(); 
//		m_hashLogicalFileDescriptors.put(csLogicalName, fd);
//	}
	
	public LogicalFileDescriptor getLogicalFileDescriptor(String csLogicalName)
	{
		if(hashLogicalFileDescriptors != null)
			return hashLogicalFileDescriptors.get(csLogicalName);
		return null;
	}
	
	public void putLogicalFileDescriptor(String csLogicalName, LogicalFileDescriptor logicalFileDescriptor)
	{
		if(hashLogicalFileDescriptors == null)
			hashLogicalFileDescriptors = new Hashtable<String, LogicalFileDescriptor>(); 
		hashLogicalFileDescriptors.put(csLogicalName, logicalFileDescriptor);
	}

	public void removeLogicalFileDescriptor(String csLogicalName)
	{
		if(hashLogicalFileDescriptors != null)
			hashLogicalFileDescriptors.remove(csLogicalName);
	}

	private Hashtable<String, LogicalFileDescriptor> hashLogicalFileDescriptors = null;

	public void addBatchInfo(String csInfo, String csValue)
	{
		if(hashLogicalJobInfo == null)
			hashLogicalJobInfo = new Hashtable<String, String>(); 
		hashLogicalJobInfo.put(csInfo, csValue);
	}

	public String getLogicalJobInfo(String csInfo)
	{
		if(hashLogicalJobInfo != null)
			return hashLogicalJobInfo.get(csInfo);
		return "";
	}

	private Hashtable<String, String> hashLogicalJobInfo = null;
	
	public boolean isAsync()
	{
		return bAsync;
	}

	protected void setAsync(boolean b)
	{
		this.bAsync = b;
	}

	private boolean bAsync = false;

	public String getDynamicAllocationInfo(String csKey)
	{
		if(hashDynamicAllocationInfo != null)
			return hashDynamicAllocationInfo.get(csKey);
		return null;
	}

	public void addDynamicAllocationInfo(String csKey, String csValue)
	{
		if(hashDynamicAllocationInfo == null)
			hashDynamicAllocationInfo = new Hashtable<String, String>(); 
		hashDynamicAllocationInfo.put(csKey, csValue);
	}
	
	public void resetDynamicAllocationInfo()
	{
		hashDynamicAllocationInfo.clear();
	}

	private Hashtable<String, String> hashDynamicAllocationInfo = null;
	
	private static int ms_LastDynamicAllocationID = 0 ;
	public String getNextDynamicAllocationID()
	{
		int n = ms_LastDynamicAllocationID++;
		return "" + (n/100)%10 + (n/10)%10 + (n)%10 ;
	}
	
	private int nNetwork_ms = 0;
	public int getNetwork_ms()
	{
		return nNetwork_ms;
	}
	public void setNetwork_ms(int nNetwork_ms)
	{
		this.nNetwork_ms = nNetwork_ms;
	}
	
	public Object getSpecialObject(String csKey)
	{
		if(hashSpecialObject != null)
			return hashSpecialObject.get(csKey);
		return null;
	}

	public void addSpecialObject(String csKey, Object object)
	{
		if(hashSpecialObject == null)
			hashSpecialObject = new Hashtable<String, Object>(); 
		hashSpecialObject.put(csKey, object);
	}
	
	public void removeSpecialObject(String csKey)
	{
		if(hashSpecialObject != null)
			hashSpecialObject.remove(csKey);
	}
	
	public void resetSpecialObject()
	{
		hashSpecialObject.clear();
	}

	private Hashtable<String, Object> hashSpecialObject = null;
}