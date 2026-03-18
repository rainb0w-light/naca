/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.bdb;

import jlib.threads.ThreadPoolRequest;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: MultiThreadedSortAddItem.java,v 1.3 2007/02/06 11:20:36 u930di Exp $
 */
public class MultiThreadedSortAddItem extends ThreadPoolRequest
{
	private BtreeFile btreeFile = null;
	private byte tbyData[] = null;
	int nTotalLength;
	int nNbRecordRead;
	boolean bVariableLength;
	
	MultiThreadedSortAddItem(BtreeFile btreeFile, byte tbyData[], int nSourceOffset, int nTotalLength, int nNbRecordRead, boolean bVariableLength)
	{
		super(false);
		
		btreeFile = btreeFile;
		tbyData = new byte[nTotalLength];
		for(int n=0; n<nTotalLength; n++)
		{
			tbyData[n] = tbyData[nSourceOffset++];
		}
		nTotalLength = nTotalLength;
		nNbRecordRead = nNbRecordRead;
		bVariableLength = bVariableLength;
	}
	
	void fill(BtreeFile btreeFile, byte tbyData[], int nSourceOffset, int nTotalLength, int nNbRecordRead, boolean bVariableLength)
	{
		btreeFile = btreeFile;

		if(tbyData.length < nTotalLength)
			tbyData = new byte[nTotalLength];
		
		for(int n=0; n<nTotalLength; n++)
			tbyData[n] = tbyData[nSourceOffset++];
		
		nTotalLength = nTotalLength;
		nNbRecordRead = nNbRecordRead;
		bVariableLength = bVariableLength;
	}
	
	public void execute()
	{
		btreeFile.asyncAddItemToSortByMultiThreads(this, tbyData);
	}
}
