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
 * @version $Id: MonoThreadedSortAddItem.java,v 1.1 2006/11/29 09:31:30 u930di Exp $
 */
public class MonoThreadedSortAddItem extends ThreadPoolRequest
{
	private BtreeFile btreeFile = null;
	private byte tbyData[] = null;
	private int nTotalLength;
	private int nNbRecordRead;
	private boolean bVariableLength;
	
	MonoThreadedSortAddItem(BtreeFile btreeFile, byte tbyData[], int nTotalLength, int nNbRecordRead, boolean bVariableLength)
	{
		super(false);
		
		btreeFile = btreeFile;
		tbyData = new byte[nTotalLength];
		for(int n=0; n<nTotalLength; n++)
		{
			tbyData[n] = tbyData[n];
		}
		nTotalLength = nTotalLength;
		nNbRecordRead = nNbRecordRead;
		bVariableLength = bVariableLength;
	}
	
	public void execute()
	{
		btreeFile.asyncAddItemToSort(tbyData, nTotalLength, nNbRecordRead, bVariableLength);
	}
}
