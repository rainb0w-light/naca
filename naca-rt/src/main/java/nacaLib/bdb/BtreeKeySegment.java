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


/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: BtreeKeySegment.java,v 1.14 2007/01/09 14:41:39 u930di Exp $
 */
public abstract class BtreeKeySegment
{
	protected int nKeyPositionInData = 0;
	protected int nKeyPosition = 0;
	protected int nKeyLength = 0;
	protected boolean bAscending = true;	// Ascending
	protected boolean bFileInEbcdic = false;
	
	public BtreeKeySegment(int nKeyPositionInData, int nKeyPositionInKey, int nKeyLength, boolean bAscending)
	{
		this.nKeyPositionInData = nKeyPositionInData;
		nKeyPosition = nKeyPositionInKey;
		this.nKeyLength = nKeyLength;
		this.bAscending = bAscending;
	}

	public void setDescending()
	{
		bAscending = false;
	}
	
	public void setAscending()
	{
		bAscending = true;
	}
	
	int getLength()
	{
		return nKeyLength; 
	}
		
	protected int appendKeySegmentData(byte tbyData[], int nOffset, byte tbyKey[])	//, boolean bConvertKeyToAscii)
	{
		int nDest = nKeyPosition;
		int nSource = nKeyPositionInData + nOffset;
		for(int n=0; n<nKeyLength; n++, nDest++, nSource++)
		{				
			tbyKey[nDest] = tbyData[nSource];
		}
		return nKeyPosition + nKeyLength;
	}
	
	void setFileInEncoding(boolean bFileInEbcdic)
	{
		this.bFileInEbcdic = bFileInEbcdic;
	}
	
	abstract int compare(byte tby1[], byte tby2[]);
}
