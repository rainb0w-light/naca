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

import java.util.ArrayList;

import nacaLib.varEx.SortKeySegmentDefinition;

import jlib.misc.ArrayDyn;
import jlib.misc.ArrayFix;
import jlib.misc.ArrayFixDyn;
import jlib.misc.LineRead;
import jlib.misc.LittleEndingUnsignBinaryBufferStorage;
import jlib.misc.NumberParser;
import jlib.misc.StringUtil;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: BtreeKeyDescription.java,v 1.20 2007/02/01 12:46:49 u930di Exp $
 */
public class BtreeKeyDescription
{	
	private String csKeys = null;
	private ArrayFixDyn<BtreeKeySegment> arrKeySegment = new ArrayDyn<BtreeKeySegment>();
	private byte[] tbyKey = null;
	int nKeyLength = 0;
	private int nKeyPositionInKey = 0;
	boolean bFileInEbcdic = false;
	
	public BtreeKeyDescription()
	{		
		nKeyPositionInKey = 0;
	}
	
	boolean set(String csKeys, boolean bAddSegmentRecordId)
	{
		this.csKeys = csKeys.trim();
		if(this.csKeys.startsWith("("))
			this.csKeys = this.csKeys.substring(1);
		if(this.csKeys.endsWith(")"))
			this.csKeys = this.csKeys.substring(0, this.csKeys.length()-1);
		
		nKeyPositionInKey = 0;
		while(!StringUtil.isEmpty(this.csKeys))
		{
			int nKeyPositionInData = getChunkAsInt()-1;
			int nKeyLength = getChunkAsInt();
			String csType = getChunk();
			String csOrder = getChunk();
			boolean bAscending = true;
			if(!csOrder.equalsIgnoreCase("A"))
				bAscending = false;
			
			BtreeKeySegment seg = null;
			if(csType.equalsIgnoreCase("CH"))
				seg = new BtreeKeySegmentAlphaNum(nKeyPositionInData, nKeyPositionInKey, nKeyLength, bAscending);
			else if(csType.equalsIgnoreCase("PD"))	// packed
				seg = new BtreeKeySegmentComp3(nKeyPositionInData, nKeyPositionInKey, nKeyLength, bAscending);
			else if(csType.equalsIgnoreCase("C4"))	// Binary
				seg = new BtreeKeySegmentBinary(nKeyPositionInData, nKeyPositionInKey, nKeyLength, bAscending);
			else if(csType.equalsIgnoreCase("BI"))	// Binary or packed
				seg = new BtreeKeySegmentUnsignedBinaryOrPacked(nKeyPositionInData, nKeyPositionInKey, nKeyLength, bAscending);
			else if(csType.equalsIgnoreCase("FI"))
				seg = new BtreeKeySegmentSignBinary(nKeyPositionInData, nKeyPositionInKey, nKeyLength, bAscending);

			nKeyPositionInKey += nKeyLength;
			if(seg != null)
				arrKeySegment.add(seg);
		}
		
		if(bAddSegmentRecordId)
			addRecordIdKeySegment();
		return true;
	}
	
	public void addRecordIdKeySegment()
	{		
		BtreeKeySegment segRecordId = new BtreeKeySegmentBinary(0, nKeyPositionInKey, 4, true);	// Binary ascending
		arrKeySegment.add(segRecordId);		
		nKeyPositionInKey += 4;
		
		// Compress
		int nSize = arrKeySegment.size();
		BtreeKeySegment arr[] = new BtreeKeySegment[nSize];
		arrKeySegment.transferInto(arr);
		ArrayFix<BtreeKeySegment> arrFix = new ArrayFix<BtreeKeySegment>(arr);
		arrKeySegment = arrFix;	// replace by a fix one (uning less memory)
	}
	
	public void addSegmentDefinition(SortKeySegmentDefinition keySegmentDefinition)
	{
		int nKeyPositionInData = keySegmentDefinition.getBufferStartPosKey();
		int nBufferLength = keySegmentDefinition.getBufferLengthKey();
		BtreeSegmentKeyTypeFactory btreeSegmentKeyTypeFactory = keySegmentDefinition.getSegmentKeyType();
		BtreeKeySegment btreeKeySegment = btreeSegmentKeyTypeFactory.make(nKeyPositionInData, nKeyPositionInKey, nBufferLength, keySegmentDefinition.bAscending);

		arrKeySegment.add(btreeKeySegment);
		nKeyPositionInKey += nBufferLength;
	}	
	
	private int getChunkAsInt()
	{
		String cs = getChunk();
		return NumberParser.getAsInt(cs);
	}
	
	private String getChunk()
	{
		String cs = null;
		int nIndex = csKeys.indexOf(',');
		if(nIndex == -1)
		{
			cs = csKeys;
			cs = cs.trim();
			csKeys = null;
		}
		else
		{
			cs = csKeys.substring(0, nIndex);
			cs = cs.trim();
			csKeys = csKeys.substring(nIndex+1);			
		}
		return cs;
	}	
	
	void prepare()
	{
		nKeyLength = 0;
		int nNbSegments = arrKeySegment.size();
		for(int n=0; n<nNbSegments; n++)
		{
			nKeyLength += arrKeySegment.get(n).getLength();
		}
		tbyKey = new byte[nKeyLength];
	}
	
	byte[] fillKeyBufferExceptRecordId(LineRead lineRead, boolean bFileInVariableLength)	//, boolean bFileInEbcdic)
	{
		return fillKeyBuffer(lineRead, 1, bFileInVariableLength);	//, bFileInEbcdic);
	}
		
	byte[] fillKeyBufferIncludingRecordId(LineRead lineRead, boolean bFileInVariableLength)	//, boolean bConvertKeyToAscii)
	{
		return fillKeyBuffer(lineRead, 0, bFileInVariableLength);	//, bConvertKeyToAscii);
	}
	
	private byte[] fillKeyBuffer(LineRead lineRead, int nNbSegmentToExclude, boolean bFileInVariableLength)	//, boolean bConvertKeyToAscii)
	{
		int nOffset = lineRead.getOffset();
//		if(bFileInVariableLength)	// exclude record header from the key
//			nOffset += 4;	// Skip record header
		byte tbyData[] = lineRead.getBuffer();

		int nNbSegments = arrKeySegment.size();
		for(int n=0; n<nNbSegments-nNbSegmentToExclude; n++)
		{
			BtreeKeySegment btreeKeySegment = arrKeySegment.get(n);
			btreeKeySegment.appendKeySegmentData(tbyData, nOffset, tbyKey);	//, bConvertKeyToAscii);
		}
		return tbyKey;
	}

	
	byte[] fillKeyBuffer(byte tbyData[], int nOffset, int nNbRecordRead, boolean bFileInVariableLength)
	{
		int nPos = 0;
		if(bFileInVariableLength)	// exclude record header from the key
			nOffset += 4;	// Skip record header
		
		int nNbSegments = arrKeySegment.size();
		for(int n=0; n<nNbSegments-1; n++)	// Do not append last segment = record id
		{
			BtreeKeySegment btreeKeySegment = arrKeySegment.get(n);
			nPos = btreeKeySegment.appendKeySegmentData(tbyData, nOffset, tbyKey);	//, false);
		}

		if(nPos <= nKeyLength-4)
			LittleEndingUnsignBinaryBufferStorage.writeInt(tbyKey, nNbRecordRead, nPos);	// Add the record id in Intel format
		
		return tbyKey;
	}
	
	byte[] fillNewKeyBuffer(byte tbyData[], int nNbRecordRead, boolean bFileInVariableLength)
	{
		int nOffset = 0;
		byte[] tbyKey = new byte[nKeyLength]; 
		int nPos = 0;
		if(bFileInVariableLength)	// exclude record header from the key
			nOffset += 4;	// Skip record header
		
		int nNbSegments = arrKeySegment.size();
		for(int n=0; n<nNbSegments-1; n++)	// Do not append last segment = record id
		{
			BtreeKeySegment btreeKeySegment = arrKeySegment.get(n);
			nPos = btreeKeySegment.appendKeySegmentData(tbyData, nOffset, tbyKey);	//, false);
		}

		if(nPos <= nKeyLength-4)
			LittleEndingUnsignBinaryBufferStorage.writeInt(tbyKey, nNbRecordRead, nPos);	// Add the record id in Intel format
		
		return tbyKey;
	}
	
	int compare(Object d1, Object d2)
	{
        byte[] tby1 = (byte[])d1;
        byte[] tby2 = (byte[])d2;
        int nNbSegments = arrKeySegment.size();
		for(int n=0; n<nNbSegments; n++)
		{			
			BtreeKeySegment btreeKeySegment = arrKeySegment.get(n);
			int nCompare = btreeKeySegment.compare(tby1, tby2);
			if(nCompare != 0)
				return nCompare; 
		}
		return 0;
	}
	
	public void setFileInEncoding(boolean bFileInEbcdic)
	{
		for(int n=0; n<arrKeySegment.size(); n++)
		{			
			BtreeKeySegment btreeKeySegment = arrKeySegment.get(n);
			btreeKeySegment.setFileInEncoding(bFileInEbcdic);
		}
	}
	
}
