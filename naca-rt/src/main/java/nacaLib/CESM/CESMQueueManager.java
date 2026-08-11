/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.CESM;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.varEx.InternalCharBuffer;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarBase;


/*
 * Created on Oct 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CESMQueueManager extends CJMapObject
{
	public CESMQueueManager(BaseEnvironment env)
	{
		eSMEnv = env;
	}

	private BaseEnvironment eSMEnv = null;
	protected Hashtable<String, CESMTempStorageColl> tabTempQueues = new Hashtable<String, CESMTempStorageColl>() ;
	protected Hashtable<String, CESMTempStorageColl> tabTransientQueues = new Hashtable<String, CESMTempStorageColl>() ;
	private final Set<String> closedTransientQueues = new HashSet<String>();

	public int writeTempQueue(String csQueueName, InternalCharBuffer Data)
	{
		return writeQueue(false, csQueueName, Data);
	}

	public synchronized int writeQueue(boolean transientQueue, String queueName, InternalCharBuffer data)
	{
		if (transientQueue && closedTransientQueues.contains(queueName))
		{
			setReturnCode(CESMReturnCode.QIDERR);
			return 0;
		}
		CESMTempStorageColl tempStorageColl = getOrCreateStorageColl(transientQueue, queueName);
		return tempStorageColl.add(data) ;
	}

	public void writeTempQueue(String csQueueName, InternalCharBuffer varData, int nRewriteItem)
	{
		writeQueue(false, csQueueName, varData, nRewriteItem);
	}

	public synchronized void writeQueue(boolean transientQueue, String queueName,
		InternalCharBuffer data, int rewriteItem)
	{
		if (transientQueue && closedTransientQueues.contains(queueName))
		{
			setReturnCode(CESMReturnCode.QIDERR);
			return;
		}
		CESMTempStorageColl tempStorageColl = getOrCreateStorageColl(transientQueue, queueName);
		if (!tempStorageColl.set(rewriteItem, data)) {
			setReturnCode(CESMReturnCode.ITEMERR) ;
		}
	}

	public void readNextTempQueue(String csQueueName, VarBase varDest)
	{
		readNextQueue(false, csQueueName, varDest);
	}

	public synchronized void readNextQueue(boolean transientQueue, String queueName, VarBase destination)
	{
		if (transientQueue && closedTransientQueues.contains(queueName))
		{
			setReturnCode(CESMReturnCode.QIDERR);
			return;
		}
		CESMTempStorageColl tempStorageColl = getExistingStorageColl(transientQueue, queueName);
		if(tempStorageColl == null)
		{
			setReturnCode(CESMReturnCode.QIDERR) ;
			return ;
		}
		InternalCharBuffer item = tempStorageColl.getNextItem();
		if(item == null)
		{
			setReturnCode(CESMReturnCode.ITEMERR) ;
			return;
		}
		if (item.getBufferSize() > destination.getTotalSize())
		{
			setReturnCode(CESMReturnCode.LENGERR) ;
			return;
		}
		destination.copyBytesFromSourceIntoBody(item);
	}

	public void readIndexedTempQueue(String csQueueName, int nIndex, Var varDest, Var varLength)
	{
		readIndexedQueue(false, csQueueName, nIndex, varDest, varLength);
	}

	public synchronized void readIndexedQueue(boolean transientQueue, String queueName,
		int index, Var destination, Var length)
	{
		if (transientQueue && closedTransientQueues.contains(queueName))
		{
			setReturnCode(CESMReturnCode.QIDERR);
			return;
		}
		CESMTempStorageColl tempStorageColl = getExistingStorageColl(transientQueue, queueName);
		if(tempStorageColl == null)
		{
			setReturnCode(CESMReturnCode.QIDERR) ;
			return ;
		}
		InternalCharBuffer item = tempStorageColl.getIndexedTempQueue(index);
		if(item == null)
		{
			setReturnCode(CESMReturnCode.ITEMERR) ;
			return;
		}
		if (item.getBufferSize() > destination.getTotalSize())
		{
			setReturnCode(CESMReturnCode.LENGERR) ;
			return;
		}
		destination.copyBytesFromSourceIntoBody(item);
		if (length != null)
		{
			length.set(item.getBufferSize());
		}
	}


	public void getNbItems(String csQueueName, Var varDest)
	{
		getNbItems(false, csQueueName, varDest);
	}

	public synchronized void getNbItems(boolean transientQueue, String queueName, Var destination)
	{
		if (transientQueue && closedTransientQueues.contains(queueName))
		{
			setReturnCode(CESMReturnCode.QIDERR);
			return;
		}
		CESMTempStorageColl tempStorageColl = getExistingStorageColl(transientQueue, queueName);
		if(tempStorageColl == null)
		{
			setReturnCode(CESMReturnCode.QIDERR) ;
			return ;
		}
		int n = tempStorageColl.getNbItems();
		destination.set(n);
	}


	public void deleteTempQueue(String csQueueName)
	{
		deleteQueue(false, csQueueName);
	}

	public synchronized void deleteQueue(boolean transientQueue, String queueName)
	{
		Hashtable<String, CESMTempStorageColl> queues = getQueues(transientQueue);
		CESMTempStorageColl tempStorageColl = queues.get(queueName);
		if(tempStorageColl == null)
		{
			setReturnCode(CESMReturnCode.QIDERR) ;
			return ;
		}
		queues.remove(queueName);
	}

	public synchronized void setTransientQueueOpen(String queueName, boolean open)
	{
		if (open)
		{
			closedTransientQueues.remove(queueName);
		}
		else
		{
			closedTransientQueues.add(queueName);
		}
	}

	private Hashtable<String, CESMTempStorageColl> getQueues(boolean transientQueue)
	{
		return transientQueue ? tabTransientQueues : tabTempQueues;
	}

	private CESMTempStorageColl getOrCreateStorageColl(boolean transientQueue, String queueName)
	{
		Hashtable<String, CESMTempStorageColl> queues = getQueues(transientQueue);
		CESMTempStorageColl storage = queues.get(queueName);
		if (storage == null)
		{
			storage = new CESMTempStorageColl();
			queues.put(queueName, storage);
		}
		return storage;
	}

	private CESMTempStorageColl getExistingStorageColl(boolean transientQueue, String queueName)
	{
		return getQueues(transientQueue).get(queueName);
	}

	private void setReturnCode(CESMReturnCode returnCode)
	{
		if (eSMEnv != null)
		{
			eSMEnv.setCommandReturnCode(returnCode);
		}
	}
}
