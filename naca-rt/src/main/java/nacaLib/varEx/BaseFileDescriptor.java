/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.misc.BaseDataFile;
import jlib.misc.RecordLengthDefinition;
import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.basePrgEnv.FileManagerEntry;

public abstract class BaseFileDescriptor extends CJMapObject
{	
	protected BaseSession baseSession = null;
	protected FileManagerEntry fileManagerEntry = null; 
	protected String csLogicalName = null;
	protected Var varLevel01 = null;
	private Var varVariableLengthMarker = null;
	private Var varLengthDependingOn = null;	// Variable declared by a FileDescriptorDepending: it gives the variable whose value give the length of the dynamic part of the record
	private int nSizeConstantRecordLength = 0;
	private int nSizeOccursDependingOn = 1;
	
	BaseFileDescriptor()
	{
	}
	
	public BaseFileDescriptor(BaseEnvironment env, String csLogicalName)
	{
		csLogicalName = csLogicalName;
		if(env != null)
			fileManagerEntry = env.getFileManagerEntry(csLogicalName);
		else
			fileManagerEntry = new FileManagerEntry();
	}
		
	public void restoreFileManagerEntry(FileManagerEntry fileManagerEntry)
	{
		fileManagerEntry = fileManagerEntry;
	}

	public String getLogicalName()
	{
		return csLogicalName;
	}
	
	public void setRecordStruct(Var varLevel01)
	{
		this.varLevel01 = varLevel01;
	}
	
	public void setVarVariableLengthMarker(Var var)
	{
		varVariableLengthMarker = var;
	}
	
	private void computeSizeConstantRecordLength()
	{
		if(varVariableLengthMarker != null && varLevel01 != null)
		{
			int nPosFixRecordStart = varLevel01.getAbsolutePosition();
			int nPosVariableRecordStart = varVariableLengthMarker.getAbsolutePosition(); 
			nSizeConstantRecordLength = nPosVariableRecordStart - nPosFixRecordStart;
			nSizeOccursDependingOn = varVariableLengthMarker.getAt(1).getTotalSize();
		}
		else
		{	
			nSizeConstantRecordLength = 0;
			nSizeOccursDependingOn = 1;
		}	
	}
	
	int getConstantRecordSize()
	{
		return nSizeConstantRecordLength;
	}
	
	int getOccursDependingOnRecordSize()
	{
		return nSizeOccursDependingOn;
	}
		
	int getTotalRecordSize()
	{
		if(varLevel01 != null)
			return varLevel01.getTotalSize();
		return 0;
	}
	
	int getVariableRecordLength(int nTotalRecordLength)
	{
		return (nTotalRecordLength - nSizeConstantRecordLength) / getOccursDependingOnRecordSize();
	}
		
	protected int getRecordLength(VarBase varSource)
	{
		if(varLengthDependingOn != null)
		{
			return getConstantRecordSize() + (varLengthDependingOn.getInt() * getOccursDependingOnRecordSize());
		}

		if (fileManagerEntry != null)
		{	
			RecordLengthDefinition recordLengthDefinition = fileManagerEntry.getRecordLengthDefinition();
			if(recordLengthDefinition != null)	// No record length defined by the FileDescriptor
				return recordLengthDefinition.getRecordLength();
		}
		if(varSource != null)
		{
			return varSource.getTotalSize();	// Get record length from structure
		}
		return 0;
	}
	
	public boolean hasVarVariableLengthMarker()
	{
		if(varVariableLengthMarker != null || varLengthDependingOn != null)
			return true;
		return false;
	}
	
	void fillVarLengthDependingOn(int nVariableRecordLength)
	{
		if(varLengthDependingOn != null)
			varLengthDependingOn.set(nVariableRecordLength);
	}
	
	protected void setVarLengthDependingOn(Var varLengthDependingOn)
	{
		varLengthDependingOn = varLengthDependingOn;
	}
	
	public BaseFileDescriptor openOutputNoFileHeaderWrite()
	{
		return doOpenOutput(false);
	}
	
	public BaseFileDescriptor openOutput()
	{
		return doOpenOutput(true);
	}
	
	private BaseFileDescriptor doOpenOutput(boolean bCanAuthoriseFileHeaderWrite)
	{
		boolean bVariableLength = false;
		if(hasVarVariableLengthMarker())
			bVariableLength = true;
		
		boolean bOpened = fileManagerEntry.doOpenOutput(csLogicalName, baseSession, bVariableLength, bCanAuthoriseFileHeaderWrite);
		if(bOpened)
			return this;
		return null;
	}	
	
	public BaseFileDescriptor openInputOutput()
	{
		boolean bVariableLength = false;
		if(hasVarVariableLengthMarker())
			bVariableLength = true;

		boolean bOpened = fileManagerEntry.doOpenInputOutput(csLogicalName, baseSession, bVariableLength);
		if(bOpened)
			return this;
		return null;
	}
	
	public BaseFileDescriptor openInput()
	{
		boolean bVariableLength = false;
		if(hasVarVariableLengthMarker())
			bVariableLength = true;
		
		boolean bOpened = fileManagerEntry.doOpenInput(csLogicalName, baseSession, bVariableLength);
		if(bOpened)
			return this;
		return null;
	}
	
	public BaseDataFile getBaseDataFile()
	{
		if(fileManagerEntry != null)
			if(fileManagerEntry.dataFile != null)
				return fileManagerEntry.dataFile;
		return null;
	}
	

	public BaseFileDescriptor openExtend()
	{
		boolean bVariableLength = false;
		if(hasVarVariableLengthMarker())
			bVariableLength = true;
		
		boolean bOpened = fileManagerEntry.doOpenExtend(csLogicalName, baseSession, bVariableLength);
		if(bOpened)
			return this;
		return null;
	}
	
	public void close()
	{
		boolean b = fileManagerEntry.doClose(csLogicalName, baseSession);
		if(b)
			fileManagerEntry.reportFileDescriptorStatus(FileDescriptorOpenStatus.CLOSE);
	}
	
	public void write(byte[] tBytes, int nOffset, int nLength, boolean bWriteEndOfRecordMarker)
	{
		fileManagerEntry.dataFile.write(tBytes, nOffset, nLength);
		if(bWriteEndOfRecordMarker)
			fileManagerEntry.dataFile.writeEndOfRecordMarker();			
	}
	
	public void setSession(BaseSession baseSession)
	{
		baseSession = baseSession;
		computeSizeConstantRecordLength();
	}
		
	public String getPhysicalName()
	{
		return fileManagerEntry.getPhysicalName(csLogicalName, baseSession);
	}
	
	public String getEbcdic()
	{
		return fileManagerEntry.getPhysicalName(csLogicalName, baseSession);
	}
	
	public boolean isEbcdic()
	{
		return fileManagerEntry.isEbcdic();
	}
		
	public BaseDataFile getDataFile()
	{
		if(fileManagerEntry != null)
			return fileManagerEntry.getDataFile();
		return null;
	}
	
	protected void incNbRecordRead()
	{
		if(fileManagerEntry != null)
			fileManagerEntry.incNbRecordRead();
	}

	
	protected void incNbRecordWrite()
	{
		if(fileManagerEntry != null)
			fileManagerEntry.incNbRecordWrite();
	}
	
	public boolean isEOF()
	{
		if(fileManagerEntry != null)
			return fileManagerEntry.isEOF();
		return true;
	}
}

