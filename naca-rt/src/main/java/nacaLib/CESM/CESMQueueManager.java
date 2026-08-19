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


/**
 * @author U930CV
 *
 */
public class CESMQueueManager extends CJMapObject
{
    /** Creates a new cesmqueue manager instance. */
    public CESMQueueManager(BaseEnvironment env)
    {
        eSMEnv = env;
    }

    private BaseEnvironment eSMEnv = null;
    protected Hashtable<String, CESMTempStorageColl> tabTempQueues = new Hashtable<String, CESMTempStorageColl>() ;
    protected Hashtable<String, CESMTempStorageColl> tabTransientQueues = new Hashtable<String, CESMTempStorageColl>() ;
    private final Set<String> closedTransientQueues = new HashSet<String>();

    /** Writes the temp queue. */
    public int writeTempQueue(String csQueueName, InternalCharBuffer data)
    {
        return writeQueue(false, csQueueName, data);
    }

    /** Writes the queue. */
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

    /** Writes the temp queue. */
    public void writeTempQueue(String csQueueName, InternalCharBuffer varData, int nRewriteItem)
    {
        writeQueue(false, csQueueName, varData, nRewriteItem);
    }

    /** Writes the queue. */
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

    /** Reads the next temp queue. */
    public void readNextTempQueue(String csQueueName, VarBase varDest)
    {
        readNextQueue(false, csQueueName, varDest);
    }

    /** Reads the next queue. */
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

    /** Reads the indexed temp queue. */
    public void readIndexedTempQueue(String csQueueName, int nIndex, Var varDest, Var varLength)
    {
        readIndexedQueue(false, csQueueName, nIndex, varDest, varLength);
    }

    /** Reads the indexed queue. */
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


    /** Returns the nb items. */
    public void getNbItems(String csQueueName, Var varDest)
    {
        getNbItems(false, csQueueName, varDest);
    }

    /** Returns the nb items. */
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


    /** Executes the delete temp queue operation. */
    public void deleteTempQueue(String csQueueName)
    {
        deleteQueue(false, csQueueName);
    }

    /** Executes the delete queue operation. */
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

    /** Sets the transient queue open. */
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
