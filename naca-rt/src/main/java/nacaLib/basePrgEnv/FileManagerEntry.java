/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.basePrgEnv;

import jlib.misc.BaseDataFile;
import jlib.misc.DataFileLineReader;
import jlib.misc.DataFileReadWrite;
import jlib.misc.DataFileWrite;
import jlib.misc.EnvironmentVar;
import jlib.misc.JVMReturnCodeManager;
import jlib.misc.LittleEndingSignBinaryBufferStorage;
import jlib.misc.LogicalFileDescriptor;
import jlib.misc.RecordLengthDefinition;
import jlib.misc.StringUtil;
import nacaLib.base.CJMapObject;
import nacaLib.exceptions.CannotOpenFileException;
import nacaLib.exceptions.FileDescriptorNofFoundException;
import nacaLib.exceptions.InputFileNotFoundException;
import nacaLib.exceptions.TooManyCloseFileException;
import nacaLib.varEx.FileDescriptorOpenStatus;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: FileManagerEntry.java,v 1.28 2007/10/25 15:10:27 u930di Exp $
 */
public class FileManagerEntry extends CJMapObject
{
	public BaseDataFile dataFile = null;
	private FileDescriptorOpenStatus fileDescriptorOpenStatus = null;
	private LogicalFileDescriptor logicalFileDescriptor = null;
	private int nNbRecordRead = 0;
	private int nNbRecordWrite = 0;

	public FileManagerEntry()
	{
		nNbRecordRead = 0;
		nNbRecordWrite = 0;
	}
	
	public void setVariableLength()
	{
		logicalFileDescriptor.setVariableLength();
	}
		
	public String getPhysicalName(String csLogicalName, BaseSession baseSession)
	{
		logicalFileDescriptor = null;
		if(baseSession != null && csLogicalName != null)
		{
			LogicalFileDescriptor logicalFileDescriptor = baseSession.getLogicalFileDescriptor(csLogicalName);
			if(logicalFileDescriptor != null)
			{
				logicalFileDescriptor = logicalFileDescriptor;	// Inherit logical file descriptor
			}
			else	// Logical name not already defines
			{			
				String csPhysicalDesc = EnvironmentVar.getParamValue(csLogicalName);
				if(StringUtil.isEmpty(csPhysicalDesc))
					csPhysicalDesc = EnvironmentVar.getParamValue("File_" + csLogicalName);
				if(csPhysicalDesc == null || StringUtil.isEmpty(csPhysicalDesc))
					csPhysicalDesc = csLogicalName;
				logicalFileDescriptor = new LogicalFileDescriptor(csLogicalName, csPhysicalDesc);
				baseSession.putLogicalFileDescriptor(csLogicalName, logicalFileDescriptor);
			}
		}
		if(logicalFileDescriptor != null)
		{
			return logicalFileDescriptor.getPath();
		}

//		Log.logCritical("Environnement or Session ERROR: Logical File \'"+csLogicalName + "\' has no physical definition");
		throw new FileDescriptorNofFoundException(csLogicalName, null);
		//"Environnement or Session ERROR: Logical File \'"+csLogicalName + "\' has no physical definition"
	}
	
	public boolean isDummyFile()
	{
		if(logicalFileDescriptor != null)
			return logicalFileDescriptor.isDummyFile();
		return true;
	}
		
	public void reportFileDescriptorStatus(FileDescriptorOpenStatus status)
	{
		fileDescriptorOpenStatus = status;
	}
	
		
	void autoClose()
	{
		if(fileDescriptorOpenStatus != null)
		{
			if(fileDescriptorOpenStatus != FileDescriptorOpenStatus.CLOSE && dataFile != null)
			{
				fileDescriptorOpenStatus = FileDescriptorOpenStatus.CLOSE;
				dataFile.close();
			}
		}
	}
	
	void autoFlush()
	{
		if(fileDescriptorOpenStatus != null)
		{
			if(fileDescriptorOpenStatus != FileDescriptorOpenStatus.CLOSE && dataFile != null)
			{
				dataFile.flush();
			}
		}
	}
	
	public RecordLengthDefinition getRecordLengthDefinition()
	{
		return logicalFileDescriptor.getRecordLengthDefinition();
	}
	
	public LogicalFileDescriptor getLogicalFileDescriptor()
	{
		return logicalFileDescriptor;
	}
	
	public boolean doOpenExtend(String csLogicalName, BaseSession baseSession, boolean bVariableLength)
	{
		boolean bOpened = false;
		if(checkCanOpen())
		{
			getPhysicalName(csLogicalName, baseSession);
			if(isDummyFile())
				return true;
			
			if(bVariableLength)
				setVariableLength();
			
			DataFileWrite dataFile = new DataFileWrite(logicalFileDescriptor.getPath(), false);
			dataFile = dataFile;
			bOpened = dataFile.openInAppend(logicalFileDescriptor);			
			if(!bOpened)
			{
				JVMReturnCodeManager.setExitCode(8);
				CannotOpenFileException e = new CannotOpenFileException(csLogicalName, logicalFileDescriptor);
				throw(e);
			}			
			reportFileDescriptorStatus(FileDescriptorOpenStatus.OPEN);
		}
		return bOpened;
	}
	
	public boolean doOpenOutput(String csLogicalName, BaseSession baseSession, boolean bVariableLength, boolean bCanAuthoriseFileHeaderWrite)
	{
		boolean bOpened = false;
		if(checkCanOpen())
		{			
			String csPhysicalFileName = getPhysicalName(csLogicalName, baseSession);
			
			if(logicalFileDescriptor.getExt())	// Force extend mode
				return doOpenExtend(csLogicalName, baseSession, bVariableLength);
			
			if(isDummyFile())	// The logical name is dummy: 
				return true;
			
			if(BaseDataFile.isNullFile(csPhysicalFileName))
				bOpened = true;	// Physical outout file is null: Simulte a correct open
			else
			{	
				if(bVariableLength)
					setVariableLength();				
				
				boolean bMustWriteFileHeader = false;
				//if(bCanAuthoriseFileHeaderWrite)
				//	bMustWriteFileHeader = BaseResourceManager.getMustWriteFileHeader(); 
				dataFile = new DataFileWrite(logicalFileDescriptor.getPath(), bMustWriteFileHeader);
				bOpened = dataFile.open(logicalFileDescriptor);
			}			
			if(!bOpened)
			{
				JVMReturnCodeManager.setExitCode(8);
				CannotOpenFileException e = new CannotOpenFileException(csLogicalName, logicalFileDescriptor);
				throw(e);
			}			
			reportFileDescriptorStatus(FileDescriptorOpenStatus.OPEN);
				
			String csDdname = baseSession.getDynamicAllocationInfo("DDNAME");
			if (csDdname != null && csDdname.equals(csLogicalName))
			{
				if (baseSession.getDynamicAllocationInfo("SYSOUT") != null)
				{
					// InfoPrint Manager
					StringBuffer sb = new StringBuffer();
					// StringUtil.rightPad(jobId, 8, ' ')
					sb.append("#300#");
					sb.append("PPSSSCCCAANNYYYYMMDDHHMMSSC");
					sb.append(StringUtil.leftPad(baseSession.getDynamicAllocationInfo("COPIES"), 3, '0'));  // Nb copies supplémentaires
					sb.append(StringUtil.rightPad(baseSession.getDynamicAllocationInfo("SYSOUT").substring(9), 4, ' '));  // No de formulaire
					sb.append(StringUtil.rightPad(baseSession.getDynamicAllocationInfo("DEST"), 8, ' '));  // Nom imprimante
					sb.append(StringUtil.rightPad(baseSession.getDynamicAllocationInfo("SYSOUT").substring(0, 1), 1, ' '));  // Classe impression
					sb.append(StringUtil.rightPad(baseSession.getDynamicAllocationInfo("BURST"), 1, ' '));  // Burst
					sb.append(StringUtil.rightPad("", 4, ' '));  // Flash
					sb.append(StringUtil.rightPad(baseSession.getDynamicAllocationInfo("CHARS"), 16, ' ')); // Chars
					sb.append(StringUtil.rightPad(baseSession.getDynamicAllocationInfo("PAGEDEF"), 6, ' '));  // Pagedef
					sb.append(StringUtil.rightPad(baseSession.getDynamicAllocationInfo("FORMDEF"), 6, ' '));  // Formdef
					sb.append(StringUtil.rightPad(baseSession.getDynamicAllocationInfo("HOLD"), 1, ' '));  // Hold
					sb.append(StringUtil.leftPad(baseSession.getDynamicAllocationInfo("PRTY"), 2, '0'));  // Priority
					if (isVariableLength())
					{
						byte[] tbyHeader = new byte[4];
						LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader, sb.length(), 0);
						dataFile.write(tbyHeader, 0, 4);
					}
					dataFile.writeRecord(sb.toString());
				}
				baseSession.resetDynamicAllocationInfo();
			}
		}
		return bOpened;
	}
	
	public boolean doOpenInput(String csLogicalName, BaseSession baseSession, boolean bVariableLength)
	{		
		boolean bOpened = false;
		if(checkCanOpen())
		{
			getPhysicalName(csLogicalName, baseSession);
			if(isDummyFile())
				return true;
			
			if(bVariableLength)
				setVariableLength();
			
			dataFile = new DataFileLineReader(logicalFileDescriptor.getPath(), 65536, 0);
			bOpened = dataFile.open(logicalFileDescriptor);
			if(!bOpened)
			{				
				JVMReturnCodeManager.setExitCode(8);
				InputFileNotFoundException e = new InputFileNotFoundException(csLogicalName, logicalFileDescriptor);
				throw(e);
			}
			reportFileDescriptorStatus(FileDescriptorOpenStatus.OPEN);
		}
		else
		{
			JVMReturnCodeManager.setExitCode(8);
			CannotOpenFileException e = new CannotOpenFileException(csLogicalName, logicalFileDescriptor);
			throw(e);
		}
		return bOpened;
	}
	
	public boolean doOpenInputOutput(String csLogicalName, BaseSession baseSession, boolean bVariableLength)
	{
		boolean bOpened = false;
		if(checkCanOpen())
		{
			getPhysicalName(csLogicalName, baseSession);
			if(isDummyFile())
				return true;
			
			if(bVariableLength)
				setVariableLength();
			
			dataFile = new DataFileReadWrite(logicalFileDescriptor.getPath());
			bOpened = dataFile.open(logicalFileDescriptor);
			if(!bOpened)
			{
				JVMReturnCodeManager.setExitCode(8);
				InputFileNotFoundException e = new InputFileNotFoundException(csLogicalName, logicalFileDescriptor);
				throw(e);
			}
			reportFileDescriptorStatus(FileDescriptorOpenStatus.OPEN);
		}
		return bOpened;
	}
	
	public boolean doClose(String csLogicalName, BaseSession baseSession)
	{
		if(isDummyFile())
			return true;
		
		if(checkCanClose())
		{
			dataFile.close();
			dataFile = null;
			baseSession.removeLogicalFileDescriptor(csLogicalName);
			return true;
		}
		
		TooManyCloseFileException e = new TooManyCloseFileException();
		throw e;
	}
	
	private boolean checkCanOpen()
	{
		if(dataFile == null)
			return true;
		return false;
	}
	
	private boolean checkCanClose()
	{
		if(dataFile != null && dataFile.isOpen())
			return true;
		return false;
	}
	
	public boolean isEbcdic()
	{
		return logicalFileDescriptor.isEbcdic();
	}
	
	public boolean isVariableLength()
	{
		return logicalFileDescriptor.isVariableLength();
	}
	
	public boolean isVariableLength4BytesHeaderWithLF()
	{
		return logicalFileDescriptor.isVariableLength4BytesHeaderWithLF();
	}
	
	public BaseDataFile getDataFile()
	{
		return dataFile; 
	}
	
	public String toString()
	{
		if(logicalFileDescriptor != null)
			return logicalFileDescriptor.toString();
		return "Unknown LogicalFileDescriptor";
	}
	
	public void inheritSettings(FileManagerEntry source)
	{
		logicalFileDescriptor.inheritSettings(source.logicalFileDescriptor);
	}
	
	public void incNbRecordRead()
	{
		nNbRecordRead++;
	}

	public void incNbRecordWrite()
	{
		nNbRecordWrite++;
	}
	
	public String dumpRWStat()
	{
		String cs;
		if(logicalFileDescriptor != null)
			cs = logicalFileDescriptor.getName();
		else
			cs = "Unknown logicalFileDescriptor ";
		cs += "Read=" + nNbRecordRead + " / Write=" + nNbRecordWrite; 
		return cs;
	}

	public boolean isEOF()
	{
		if(dataFile != null)
			return dataFile.isEOF();
		return true;
	}
}
