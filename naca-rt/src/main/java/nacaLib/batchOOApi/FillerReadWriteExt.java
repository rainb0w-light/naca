/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.batchOOApi;

import jlib.misc.NumberParser;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.varEx.Pic9Comp3BufferSupport;
import nacaLib.varEx.RWNumIntComp0;

public class FillerReadWriteExt
{
	private WriteBufferExt bufferExt = null;
	private ModeReadWriteExt currentModeReadWriteExt = ModeReadWriteExt.Unknown;
	private int nVariableChunkLength = -1;
	private int nPositionEndFixedRecordChunk = -1;
	
	public WriteBufferExt getBuffer()
	{
		return bufferExt;
	}
	
	public void setMode(ModeReadWriteExt mode)
	{
		this.currentModeReadWriteExt = mode;
	}
	
	public int getVariableChunkLength()
	{
		return nVariableChunkLength;
	}
	
	public void setVariableChunkLength(int nVariableChunkLength)
	{
		this.nVariableChunkLength = nVariableChunkLength;
	}
	
	public void markEndFixedRecordChunk()
	{
		nPositionEndFixedRecordChunk = bufferExt.getRecordCurrentPosition();
		if(currentModeReadWriteExt == ModeReadWriteExt.Read)
		{			
			nVariableChunkLength = bufferExt.getVariableRecordWholeLength() - nPositionEndFixedRecordChunk;
		}
	}
	
	public void allocOrResetBufferExt()
	{
		if(bufferExt == null)
		{
			int nBufSize = BaseResourceManager.getFileLineReaderBufferSize();		
			bufferExt = new WriteBufferExt(nBufSize);
		}
		else
			bufferExt.resetCurrentPosition();
	}

	public String fill(String csValue, int nLength)
	{
		if(currentModeReadWriteExt == ModeReadWriteExt.Read)
		{
			csValue = bufferExt.getString(nLength);
		}
		else if(currentModeReadWriteExt == ModeReadWriteExt.Write)
		{
			bufferExt.fillWriteAsPicX(csValue, nLength);
		}
		bufferExt.advanceCurrentPosition(nLength);
		return csValue;
	}
	
	public int fillComp0Unsigned(int nValue, int nLength)
	{
		if(currentModeReadWriteExt == ModeReadWriteExt.Read)
		{
			String cs = bufferExt.getString(nLength);
			nValue = NumberParser.getAsUnsignedInt(cs);
		}
		else if(currentModeReadWriteExt == ModeReadWriteExt.Write)
		{
			RWNumIntComp0.setFromRightToLeft(bufferExt, 0, nValue, nLength, nLength);			
		}
		bufferExt.advanceCurrentPosition(nLength);
		return nValue;
	}
	
	public int fillComp3Unsigned(int nValue, int nLength)
	{
		if(currentModeReadWriteExt == ModeReadWriteExt.Read)
		{	
			nValue = Pic9Comp3BufferSupport.getAsUnsignedInt(bufferExt, nLength);
		}
		else if(currentModeReadWriteExt == ModeReadWriteExt.Write)
		{
			Pic9Comp3BufferSupport.setFromRightToLeftUnsigned(bufferExt, nLength, nLength, nValue);			
		}
		int nNbBytesWritten = (nLength / 2) + 1; 
		bufferExt.advanceCurrentPosition(nNbBytesWritten);
		return nValue;
	}
	
	public int fillComp3Signed(int nValue, int nLength)
	{
		if(currentModeReadWriteExt == ModeReadWriteExt.Read)
		{
			nValue = Pic9Comp3BufferSupport.getAsInt(bufferExt, nLength);
		}
		else if(currentModeReadWriteExt == ModeReadWriteExt.Write)
		{
			Pic9Comp3BufferSupport.setFromRightToLeftSigned(bufferExt, nLength, nLength, nValue);			
		}
		int nNbBytesWritten = (nLength / 2) + 1;
		bufferExt.advanceCurrentPosition(nNbBytesWritten);
		return nValue;
	}
}
