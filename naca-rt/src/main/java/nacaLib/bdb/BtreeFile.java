/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.bdb;

import jlib.misc.LineRead;
import jlib.misc.LittleEndingUnsignBinaryBufferStorage;
import jlib.threads.PoolOfThreads;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.exceptions.AbortSessionException;

import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: BtreeFile.java,v 1.24 2007/06/22 15:57:35 u930bm Exp $
 */
public class BtreeFile
{
	private Database bdb = null;
	DatabaseEntry key = null;
	DatabaseEntry data = null;
	private BtreeKeyDescription keyDescription = null;
	public static final int MAX_RECORD_LENGTH = 32768;
	private Cursor cursor = null;
	private int nNbthreadsSort = 0;
	private PoolOfThreads threadsPoolWriter = null;
	private SortedRecordsPoolOfThreadReader threadsPoolReader = null;
	private int nNbMaxRequestAsyncSortPending = 0;
	private MultiThreadedSortAddItemCache multiThreadedSortItemCache = null;
			
	BtreeFile(Database bdb)	//, boolean bCanSortMultiThreads)
	{
		this.bdb = bdb;
		key = new DatabaseEntry();
		data = new DatabaseEntry();
//		if(bCanSortMultiThreads)
//		{
			nNbthreadsSort = BaseResourceManager.getNbThreadsSort();
			if(nNbthreadsSort > 0)
			{
				nNbMaxRequestAsyncSortPending = BaseResourceManager.getNbMaxRequestAsyncSortPending();
				BtreePooledThreadWriterFactory btreeThreadFactory = new BtreePooledThreadWriterFactory();
				
				threadsPoolWriter = new PoolOfThreads(btreeThreadFactory, nNbthreadsSort, nNbMaxRequestAsyncSortPending);
				threadsPoolWriter.startAllThreads();
			}
			multiThreadedSortItemCache = new MultiThreadedSortAddItemCache();
		//}
	}
	
	public void setKeyDescription(BtreeKeyDescription keyDescription)
	{
		this.keyDescription = keyDescription;
		keyDescription.prepare();
	}

	public boolean internalSortInsertWithRecordIndexAtEnd(byte tbyData[], int nSourceOffset, int nTotalLength, int nNbRecordRead, boolean bVariableLength)  
	{
		if(nNbthreadsSort == 0)	// No thread for sorting
		{
			byte tbyKey[] = keyDescription.fillKeyBuffer(tbyData, 0, nNbRecordRead, bVariableLength);
	
			//LittleEndingUnsignBinaryBufferStorage.writeInt(tbyKey, nNbRecordRead, keyDescription.nKeyLength-4);	// Intel format
			
			data.setData(tbyData, 0, nTotalLength);
			key.setData(tbyKey);
			try
			{
				bdb.put(null, key, data);
				return true;
			}
			catch (DatabaseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		else // Multi threads for sorting
		{
			// Insert real record
			MultiThreadedSortAddItem item = multiThreadedSortItemCache.getUsusedItem();
			if(item == null)
				item = new MultiThreadedSortAddItem(this, tbyData, nSourceOffset, nTotalLength, nNbRecordRead, bVariableLength);
			else
				item.fill(this, tbyData, nSourceOffset, nTotalLength, nNbRecordRead, bVariableLength);
			threadsPoolWriter.enqueue(item);
			return true;
		}
	}
	
	public void asyncAddItemToSort(byte tbyData[], int nTotalLength, int nNbRecordRead, boolean bVariableLength)
	{
		// if only 1 dedicated thread is used for adding an item to sort 
		data.setData(tbyData, 0, nTotalLength);
		byte tbyKey[] = keyDescription.fillNewKeyBuffer(tbyData, nNbRecordRead, bVariableLength);
		key.setData(tbyKey);
		try
		{
			bdb.put(null, key, data);
			//unlock 
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		unlock
	}
	
	public void asyncAddItemToSortByMultiThreads(MultiThreadedSortAddItem multiThreadedSortItem, byte tbyData[])
	{
		// if more than 1 dedicated thread are used for adding an item to sort
				
		DatabaseEntry data = new DatabaseEntry();
		data.setData(tbyData, 0, multiThreadedSortItem.nTotalLength);
		byte tbyKey[] = keyDescription.fillNewKeyBuffer(tbyData, multiThreadedSortItem.nNbRecordRead, multiThreadedSortItem.bVariableLength);
		
		//Dumper.dump("Record read="+multiThreadedSortItem.nNbRecordRead);
		//Dumper.dump(tbyKey);
		
		DatabaseEntry key = new DatabaseEntry();
		key.setData(tbyKey);
		try
		{
			bdb.put(null, key, data);
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		multiThreadedSortItemCache.disposeItemForReuse(multiThreadedSortItem);
	}

	public boolean externalSortInsertWithRecordIndexAtEnd(Environment env, LineRead lineRead, int nNbRecordRead, boolean bFileInEbcdic, boolean bFileInVariableLength)  
	{
		byte tbyData[] = lineRead.getBuffer();
		int nOffset = lineRead.getOffset();
		int nTotalLength = lineRead.getTotalLength();
		
		if(nNbthreadsSort == 0)	// No thread for sorting
		{
			byte tbyKey[] = keyDescription.fillKeyBufferExceptRecordId(lineRead, bFileInVariableLength);	//, bFileInEbcdic);

			LittleEndingUnsignBinaryBufferStorage.writeInt(tbyKey, nNbRecordRead, keyDescription.nKeyLength-4);	// Write record id at the end of the key

			data.setData(tbyData, nOffset, nTotalLength);
			key.setData(tbyKey);
			//Dumper.dump("Record read="+nNbRecordRead);
			//Dumper.dump(tbyKey);
			try
			{
				bdb.put(null, key, data);
				return true;
			}
			catch (DatabaseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		else
		{
			MultiThreadedSortAddItem item = multiThreadedSortItemCache.getUsusedItem();
			if(item == null)
				item = new MultiThreadedSortAddItem(this, tbyData, nOffset, nTotalLength, nNbRecordRead, bFileInVariableLength);
			else
				item.fill(this, tbyData, nOffset, nTotalLength, nNbRecordRead, bFileInVariableLength);
			//MultiThreadedSortAddItem item = new MultiThreadedSortAddItem(this, tbyData, nOffset, nTotalLength, nNbRecordRead, bFileInVariableLength);
			threadsPoolWriter.enqueue(item);
			return true;
		}
	}
	
	public boolean tryLaunchAsyncSortReader()
	{
		if(threadsPoolWriter != null)	// We are using a pool of threads for adding items for sorting; wait until all items have been completly added
		{
			Exception expThrownByPooledThread = threadsPoolWriter.stop();
			if (expThrownByPooledThread != null)    // One of the threads has crashed
			{
				throw new RuntimeException(expThrownByPooledThread);
			}
			
			threadsPoolWriter = null;
			
			// Create a thread pool reader
			BtreePooledThreadReaderFactory btreePooledThreadReaderFactory = new BtreePooledThreadReaderFactory(this);
			
			threadsPoolReader = new SortedRecordsPoolOfThreadReader(btreePooledThreadReaderFactory, nNbMaxRequestAsyncSortPending);
			threadsPoolReader.startAllThreads();
			
			return true;
		}
		return false; 
	}
	
	public byte [] syncGetFirst()
	{	
		try
		{
			cursor = bdb.openCursor(null, null);
			OperationStatus status = cursor.getFirst(key, data, LockMode.DEFAULT);
			if(status == OperationStatus.SUCCESS)
			{
				nNbRecordExported = 1;
				byte tDataWithHeader[] = getDataRead();
				return tDataWithHeader;
			}
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public byte [] syncGetNext()
	{
		try
		{
			if(cursor != null)
			{
				OperationStatus status = cursor.getNext(key, data, LockMode.DEFAULT);
				if(status == OperationStatus.SUCCESS)
				{
					nNbRecordExported++;
					byte tDataWithHeader[] = getDataRead();
					return tDataWithHeader;
				}
			}				
		}
		catch (DatabaseException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public byte [] getDataRead()
	{
		return data.getData();
	}
	
	public byte [] getKeyRead()
	{
		return key.getData();
	}

	void close()
	{
		if(cursor != null)
		{
			try
			{
				cursor.close();
				cursor = null;
			}
			catch (DatabaseException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		if(bdb != null)
		{
			try
			{
				bdb.close();
			}
			catch (DatabaseException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			bdb = null;
		}
	}

	byte[] getData()
	{
		if(data != null)
			return data.getData();
		return null;
	}
	
	byte[] getKey()
	{
		if(key != null)
			return key.getData();
		return null;
	}
		
	public byte[] getNextSortedRecord()
	{
		if(nNbRecordExported == 0)
			tryLaunchAsyncSortReader();
		
		if(threadsPoolReader == null)
		{
			if(nNbRecordExported == 0)
				return syncGetFirst();
			else
				return syncGetNext();
		}
		return threadsPoolReader.getNextSortedRecord();
	}
	
	private int nNbRecordExported = 0;
}
