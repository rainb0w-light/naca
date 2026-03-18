/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fpacPrgEnv;

import jlib.log.Log;
import jlib.misc.FileEndOfLine;
import jlib.misc.LineRead;
import jlib.misc.RecordLengthDefinition;
import nacaLib.varEx.BaseFileDescriptor;
import nacaLib.varEx.RecordDescriptorAtEnd;
import nacaLib.varEx.Var;
import nacaLib.varEx.VarBuffer;


public class FPacFileDescriptor extends BaseFileDescriptor
{	
	private FPacRecordFiller fPacRecordFillerInput = null;
	private FPacRecordFiller fPacRecordFillerOutput = null;
	private final static int MAX_RECORD_LENGTH = 32768;
	
	private byte tBytes[] = null;
	private char acBuffer[] = null;
	private VarBuffer varBuffer = null; 
	private FPacVarManager fpacVarManager = null;
	RecordLengthDefinition forcedRecordLengthDefinition = null;
	private int nLastReadRecordLength = -1;
	
	public FPacFileDescriptor(FPacProgram program, String csLogicalName)
	{
		super(program.getProgramManager().getEnv(), csLogicalName);
		init(program);
	}
	
	void setRecordFillers(FPacRecordFiller FPacRecordFillerInput, FPacRecordFiller FPacRecordFillerOutput)
	{
		fPacRecordFillerInput = FPacRecordFillerInput;
		fPacRecordFillerOutput = FPacRecordFillerOutput;
	}
	
	public FPacFileDescriptor openOutput()
	{
		super.openOutput();
		
		fillOutputBuffer();		
		return this;
	}
	
	public FPacFileDescriptor openInput()
	{
		super.openInput();
		return this;
	}
	
	
	public FPacFileDescriptor openInputOutput()
	{
		super.openInputOutput();
		return this;
	}
		
	public void variableLength()
	{
		fileManagerEntry.setVariableLength();
	}

	private void init(FPacProgram program)
	{
		tBytes = new byte [MAX_RECORD_LENGTH];
		
		acBuffer = new char [MAX_RECORD_LENGTH];
		varBuffer = new VarBuffer(acBuffer);
		
		fpacVarManager = new FPacVarManager(program);
	}
	
	private void fillInputBuffer()
	{
		if(fPacRecordFillerInput != null)
			fPacRecordFillerInput.fillBuffer(acBuffer);
	}

	private void fillOutputBuffer()
	{
		if(fPacRecordFillerOutput != null)
			fPacRecordFillerOutput.fillBuffer(acBuffer);
	}

	public RecordDescriptorAtEnd read()
	{		
		fillInputBuffer();
		nLastReadRecordLength = -1;
		
		if(fileManagerEntry.isVariableLength())	 // Variable size record
		{
			long lLastHeaderStartPosition = fileManagerEntry.dataFile.getFileCurrentPosition();	// Keep header start position
			LineRead header = fileManagerEntry.dataFile.readBuffer(4, false);		// Read header
			if(header != null)
			{
				int nLengthExcludingHeader = header.getAsLittleEndingUnsignBinaryInt();	// Length in header doesn't count the header itself
				int nHeaderLength = varBuffer.setFromLineRead(header, 0);			// write the record after the record length at the beginning; it includes the length itself
				LineRead lineRead = fileManagerEntry.dataFile.readBuffer(nLengthExcludingHeader, true);		// Read including trailing LF
				fileManagerEntry.dataFile.setLastPosition(lLastHeaderStartPosition);	// Save current position at the header start
				if(lineRead != null)
				{
					nLastReadRecordLength = varBuffer.setFromLineRead(lineRead, 4) + nHeaderLength;
					incNbRecordRead();
				}				
			}
		}
		else		// Constant record size
		{
			int nRecordLength = 0;
			if(forcedRecordLengthDefinition == null)
				nRecordLength = getRecordLength(null);				
			else
				nRecordLength = forcedRecordLengthDefinition.getRecordLength();
			
			LineRead lineRead = null;
			if(nRecordLength > 0)
				lineRead = fileManagerEntry.dataFile.readBuffer(nRecordLength, true);
			else
				lineRead = fileManagerEntry.dataFile.readNextUnixLine();

			if(lineRead != null)
			{
				nLastReadRecordLength = varBuffer.setFromLineRead(lineRead, 0);	// Record length does not includes LF !
				incNbRecordRead();
			}
		}
		
		if(fileManagerEntry.dataFile.isEOF())
			return RecordDescriptorAtEnd.End;
		return RecordDescriptorAtEnd.NotEnd;
	}
	
	private void doWrite()
	{		
		if(!fileManagerEntry.isVariableLength())		// Constant record size
		{
			int nRecordLength = getRecordLength(null);
			if(nRecordLength == 0)
			{
				if(fileManagerEntry.dataFile.isUpdateable())
					nRecordLength = nLastReadRecordLength;
			}
			
			if(nRecordLength >= 0)
			{
				fillBuffer(varBuffer.acBuffer, 0, nRecordLength);
				write(tBytes, 0, nRecordLength, true);
				incNbRecordWrite();
			}
			else
			{
				Log.logCritical("FPacFileDescriptor::File: Cannot write record because No length defined for fixed length FPac file " + getLogicalName());
			}
		}
		else
		{
			if(fileManagerEntry.dataFile.isUpdateable())	// rewrite, not write 
			{
				fillBuffer(varBuffer.acBuffer, 0, nLastReadRecordLength);
				write(tBytes, 0, nLastReadRecordLength, true);
				incNbRecordWrite();
			}
			else	// Use header to get record length 
			{
				int nRecordLength = varBuffer.getIntAt(0);	// Read record length encoded in the 4 leading bytes; it doesn't includes the record header itself, nor the trailing LF
				int nTotalRecordLength = nRecordLength + 4;
				fillBuffer(varBuffer.acBuffer, 0, nTotalRecordLength);
				write(tBytes, 0, nTotalRecordLength, true);
				incNbRecordWrite();
			}
		}
		
		fillOutputBuffer();
	}
	
	public void write()
	{		
		if(fileManagerEntry.dataFile.isWritable())
		{
			if(fileManagerEntry.dataFile.isReadable())	// File open in update mode
			{
				long l = fileManagerEntry.dataFile.getLastPosition();
				fileManagerEntry.dataFile.setFileCurrentPosition(l);
			}
			doWrite();
		}
	}
	
	private void fillBuffer(char tcSourceBuffer[], int nSourceOffset, int nRecordLength)
	{
		for(int n=0; n<nRecordLength; n++)
		{
			tBytes[n] = (byte)tcSourceBuffer[n + nSourceOffset];
		}
	}
	
	public FPacVarManager getFPacVarManager()
	{
		return fpacVarManager;
	}
	
	public VarBuffer getVarBuffer()
	{
		return varBuffer;
	}
	
	Var createFPacVarAlphaNum(int nAbsolutePosition1Based, int nNbDigitsInteger)
	{
		return getFPacVarManager().createFPacVarAlphaNum(varBuffer, nAbsolutePosition1Based, nNbDigitsInteger);
	}
	
	Var createFPacVarRaw(int nAbsolutePosition1Based, int nNbDigitsInteger)
	{
		return getFPacVarManager().createFPacVarRaw(varBuffer, nAbsolutePosition1Based, nNbDigitsInteger);
	}

	Var createFPacVarNumIntSignComp3(int nAbsolutePosition1Based, int nBufferLength)
	{
		return getFPacVarManager().createFPacVarNumIntSignComp3(varBuffer, nAbsolutePosition1Based, nBufferLength);
	}
	
	Var createFPacVarNumSignComp4(int nAbsolutePosition1Based, int nBufferLength)
	{
		return getFPacVarManager().createFPacVarNumSignComp4(varBuffer, nAbsolutePosition1Based, nBufferLength);
	}	

	public void setRecordLengthForced(int nRecordLengthForced)
	{
		forcedRecordLengthDefinition = new RecordLengthDefinition(nRecordLengthForced);
	}
	
	public String toString()
	{
		if(fileManagerEntry != null)
			return fileManagerEntry.toString();
		return "No File Manager";
	}
}
