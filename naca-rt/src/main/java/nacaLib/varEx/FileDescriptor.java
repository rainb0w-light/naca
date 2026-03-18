/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.misc.BaseDataFile;
import jlib.misc.BaseDataFileBuffered;
import jlib.misc.EnvironmentVar;
import jlib.misc.LineRead;
import jlib.misc.LittleEndingSignBinaryBufferStorage;
import jlib.misc.LogicalFileDescriptor;
import jlib.misc.RecordLengthDefinition;
import jlib.misc.StringUtil;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.BaseSession;
import nacaLib.batchOOApi.WriteBufferExt;

public class FileDescriptor extends BaseFileDescriptor
{
	private static final int PAGE_LINES = 60;
	private VarDefEncodingConvertibleManagerContainer varDefEncodingConvertibleManagerContainer = null;
	private byte[] tbyHeader = null;
	private Var status;
	private int count;
	
	public FileDescriptor(String csLogicalName)
	{
		super(null, csLogicalName);
	}
	
	public FileDescriptor(String csLogicalName, BaseSession session)
	{
		super(null, csLogicalName);
		setSession(session);
	}
	
	public FileDescriptor(BaseEnvironment env, String csLogicalName)
	{
		super(env, csLogicalName);
	}
		
	public static boolean isExistingFileDescriptor(String csLogicalName, BaseSession baseSession)
	{
		if(baseSession != null && csLogicalName != null)
		{
			LogicalFileDescriptor logicalFileDescriptor = baseSession.getLogicalFileDescriptor(csLogicalName);
			if(logicalFileDescriptor != null)
			{
				return true;
			}
			else	// Logical name not already defines
			{
			
				String csPhysicalDesc = EnvironmentVar.getParamValue(csLogicalName);
				if(StringUtil.isEmpty(csPhysicalDesc))
					csPhysicalDesc = EnvironmentVar.getParamValue("File_" + csLogicalName);
				if(csPhysicalDesc != null && !StringUtil.isEmpty(csPhysicalDesc))
					return true;
			}
		}
		return false;
	}	
	
	public FileDescriptor status(Var status)
	{
		this.status = status;
		return this;
	}
	
	public void inheritSettings(FileDescriptor fileDescSource)
	{
		fileManagerEntry.inheritSettings(fileDescSource.fileManagerEntry);
	}

	public boolean isEbcdic()
	{
		return fileManagerEntry.isEbcdic();
	}
	
	public boolean isVariableLength()
	{
		return fileManagerEntry.isVariableLength();
	}
	
	public boolean isVariableLength4BytesHeaderWithLF()
	{
		return fileManagerEntry.isVariableLength4BytesHeaderWithLF();
	}
	
	public RecordLengthDefinition getRecordLengthDefinition()
	{
		return fileManagerEntry.getRecordLengthDefinition();
	}

	public FileDescriptor lengthDependingOn(Var varLengthDependingOn)
	{
		setVarLengthDependingOn(varLengthDependingOn);
		return this;
	}
	
	public void write()
	{
		writeFrom(varLevel01, false);
	}
	
	public void writeAfter(int after)
	{
		after(after);
		writeFrom(varLevel01, false);
	}

	private void after(int after)
	{
		if (after < 0)
		{
			after = (PAGE_LINES - (count % PAGE_LINES)) % PAGE_LINES;
			after++;
		}
		after--;
		for (int i = 0; i < after; i++)
		{
			fileManagerEntry.dataFile.writeWithEOL(new byte[0], 0);
			incNbRecordWrite();
		}
	}
	
	public void writeFrom(VarBase varWorking)
	{
		writeFrom(varWorking, false);
	}
	
	public void rewrite()
	{
		writeFrom(varLevel01, true);
	}
	
	public void rewriteFrom(VarBase varWorking)
	{
		writeFrom(varWorking, true);
	}
	
	private void writeFrom(VarBase varFrom, boolean bRewriteMode)
	{
		if(fileManagerEntry.isDummyFile())
			return ;

		VarBase varLevel01 = this.varLevel01;
		if(varLevel01 == null)
			varLevel01 = varFrom;
		
		int nRecordSize = varLevel01.getTotalSize();
		int nVarFromSize = nRecordSize; 
		int nMinSize = nRecordSize;
		int nMaxSize = nRecordSize;
		
		if(varLevel01 != varFrom)
		{
			nVarFromSize = varFrom.getTotalSize();			
			if(nRecordSize <= nVarFromSize)
			{
				nMinSize = nRecordSize;
				nMaxSize = nVarFromSize;
			}
			else
			{
				varLevel01.fill(CobolConstant.LowValue);
				nMinSize = nVarFromSize;
				nMaxSize = nRecordSize;
			}
		}
		
		// Move bytes of working into record, up to record length
		byte tbyFilebuffer[] = fileManagerEntry.dataFile.getByteBuffer(nMaxSize);
		varFrom.exportToByteArray(tbyFilebuffer, nVarFromSize);
		if (varLevel01 != varFrom)
			varLevel01.setFromByteArray(tbyFilebuffer, 0, nMinSize);	// Used when record buffer is longer than working buffer; we must keep the right part of the record at the initialized values
		
		if(fileManagerEntry.isEbcdic())	// Must convert string chunks
		{
			if(varDefEncodingConvertibleManagerContainer == null)
				varDefEncodingConvertibleManagerContainer = new VarDefEncodingConvertibleManagerContainer();
			varDefEncodingConvertibleManagerContainer.getConvertedBytesAsciiToEbcdic(varLevel01, tbyFilebuffer, nMaxSize);
		}
		else
		{
			varLevel01.exportToByteArray(tbyFilebuffer, nRecordSize);
		}
		

		// Write varLevel01 
		if(fileManagerEntry.isVariableLength())
		{
			int nRecordLength = getRecordLength(varLevel01);	// Measure record length
			// write record header
			if(tbyHeader == null)
				tbyHeader = new byte[4];
			LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader, nRecordLength, 0);	// DO not include header length in header !
			if(bRewriteMode)
				fileManagerEntry.dataFile.rewrite(tbyHeader, 0, 4);
			else
				fileManagerEntry.dataFile.write(tbyHeader, 0, 4);
			
			fileManagerEntry.dataFile.writeWithEOL(tbyFilebuffer, nRecordLength);
			incNbRecordWrite();
		}
		else
		{
			if(bRewriteMode)
				fileManagerEntry.dataFile.rewriteWithEOL(tbyFilebuffer, nRecordSize);
			else
				fileManagerEntry.dataFile.writeWithEOL(tbyFilebuffer, nRecordSize);
			incNbRecordWrite();
		}
		if(status != null)
			status.set("00");
	}
	
	@Override
	protected void incNbRecordWrite() {
		count++;
		super.incNbRecordWrite();
	}
	
	public byte [] getWriteBuffer(int nMaxSize)
	{
		byte tbyFilebuffer[] = fileManagerEntry.dataFile.getByteBuffer(nMaxSize);
		return tbyFilebuffer;
	}
	
	public void writeFrom(LineRead lineRead)
	{
		fileManagerEntry.dataFile.writeWithEOL(lineRead);
	}
	
	public RecordDescriptorAtEnd read()
	{
		return readInto(varLevel01);
	}
	
	private void convertEbcdicToAsciiAndWrite(LineRead lineRead, Var varDest)
	{
		if(varDefEncodingConvertibleManagerContainer == null)
			varDefEncodingConvertibleManagerContainer = new VarDefEncodingConvertibleManagerContainer();

		if(varLevel01 != varDest)
			varDefEncodingConvertibleManagerContainer.getEncodingManagerConvertAndWrite(lineRead, varDest);
		else
			varDefEncodingConvertibleManagerContainer.getEncodingManagerConvertAndWrite(lineRead, varLevel01);
	}
	
	private void convertEbcdicToAsciiAndWrite(LineRead lineRead)
	{
		if(varDefEncodingConvertibleManagerContainer == null)
			varDefEncodingConvertibleManagerContainer = new VarDefEncodingConvertibleManagerContainer();

		varDefEncodingConvertibleManagerContainer.getEncodingManagerConvertAndWrite(lineRead, varLevel01);
	}

	public RecordDescriptorAtEnd readInto(Var varDest)
	{
		if(fileManagerEntry.isDummyFile())
			return RecordDescriptorAtEnd.End;

		if(hasVarVariableLengthMarker())
		{
			long lLastHeaderStartPosition = fileManagerEntry.dataFile.getFileCurrentPosition();	// Keep header start position
			LineRead header = fileManagerEntry.dataFile.readBuffer(4, false);		// Read header
			if(header != null)
			{				
				int nLengthExcludingHeader = header.getAsLittleEndingUnsignBinaryInt();	// Length in header doesn't count the header itself
				LineRead lineRead = fileManagerEntry.dataFile.readBuffer(nLengthExcludingHeader, true);		// Read record body, including trailing LF
				fileManagerEntry.dataFile.setLastPosition(lLastHeaderStartPosition);	// Save current position at the header start
				if(lineRead != null)
				{
					fillInto(lineRead, varDest);
					int nVariableRecordLength = getVariableRecordLength(nLengthExcludingHeader);
					fillVarLengthDependingOn(nVariableRecordLength);
					return RecordDescriptorAtEnd.NotEnd;
				}
			}
			return RecordDescriptorAtEnd.End;
		}
		else
		{
			int nRecordLength = getRecordLength(varLevel01);
			LineRead lineRead;
			if(nRecordLength > 0)
			{
				lineRead = fileManagerEntry.dataFile.readBuffer(nRecordLength, true);	// PJD TO UNCOMMENT 
				//lineRead = ((DataFileLineReader)fileManagerEntry.dataFile).readDirect(nRecordLength);
			}
				
			else
				lineRead = fileManagerEntry.dataFile.readNextUnixLine();				
			if(lineRead != null)
			{
				fillInto(lineRead, varDest);
				return RecordDescriptorAtEnd.NotEnd;
			}
			return RecordDescriptorAtEnd.End;
		}
	}
	
	private void fillInto(LineRead lineRead, Var varDest)
	{
		incNbRecordRead();
		if (varLevel01 != varDest)
		{
			if (fileManagerEntry.isEbcdic())
				fillInto2DestEbcdic(lineRead, varDest);
			else
				varDest.setFromLineRead2DestWithFilling(lineRead, varLevel01);
		}
		else
		{			
			// varLevel01 == varDest: Not a readInto()
			if (fileManagerEntry.isEbcdic())
			{
				varDest.fill(CobolConstant.LowValue);
				convertEbcdicToAsciiAndWrite(lineRead);
			}
			else
			{
				int nRecordSize = varLevel01.getTotalSize();
				int nNbByteWritten = varLevel01.setFromLineRead(lineRead);
				if(nRecordSize > nNbByteWritten) 
					varDest.fillEndOfRecord(nNbByteWritten, nRecordSize);
			}
		}
	}
	
	private void fillInto2DestEbcdic(LineRead lineRead, Var varDest)
	{
		varDest.fill(CobolConstant.LowValue);
		varLevel01.fill(CobolConstant.LowValue);			

		int nRecordSize = varLevel01.getTotalSize();
		int nDestSize = varDest.getTotalSize();
		if (nRecordSize > nDestSize)
			nRecordSize = nDestSize;

		convertEbcdicToAsciiAndWrite(lineRead, varDest);
		
		byte tbyFilebuffer[] = fileManagerEntry.dataFile.getByteBuffer(nRecordSize);
		varDest.exportToByteArray(tbyFilebuffer, nRecordSize);
		varLevel01.setFromByteArray(tbyFilebuffer, 0, nRecordSize);
	}
	
//	private void fillInto(LineRead lineRead, Var varDest)
//	{
//		varDest.fill(CobolConstant.LowValue);
//		if (varLevel01 != varDest)
//			varLevel01.fill(CobolConstant.LowValue);			
//		
//		int nRecordSize = varLevel01.getTotalSize();
//		if (varLevel01 != varDest)
//		{
//			int nDestSize = varDest.getTotalSize();
//			if (nRecordSize > nDestSize)
//				nRecordSize = nDestSize;
//		}
//
//		if (fileManagerEntry.isEbcdic())
//			convertEbcdicToAsciiAndWrite(lineRead, varDest);
//		else
//			noConvertEbcdicToAsciiAndWrite(lineRead, varDest);
//
//		if (varLevel01 != varDest)
//		{
//			byte tbyFilebuffer[] = fileManagerEntry.dataFile.getByteBuffer(nRecordSize);
//			varDest.exportToByteArray(tbyFilebuffer, nRecordSize);
//			varLevel01.setFromByteArray(tbyFilebuffer, 0, nRecordSize);
//		}
//	}


	public String toString()
	{
		if(fileManagerEntry != null)
		{
			String cs = fileManagerEntry.toString();
			return cs + " mapped on " + varLevel01.toString();
		}
		return "Unknown FileManagerEntry";
	}
	
	public LineRead readALine(BaseDataFileBuffered dataFileIn, LineRead lastLineRead)
	{
		if(fileManagerEntry.isDummyFile())
			return null;

		if(isVariableLength())
		{
			boolean bReadLF = isVariableLength4BytesHeaderWithLF();
			boolean bHeader4Bytes = isVariableLength4BytesHeaderWithLF();
			lastLineRead = dataFileIn.readVariableLengthLine(bReadLF, bHeader4Bytes, lastLineRead);	// Read a vairable length line (length is given in record header 4 bytes)
		}
		else
		{
			RecordLengthDefinition recordLengthDefinition = getRecordLengthDefinition();
			if(recordLengthDefinition == null)	// No record length defined by the input file descriptor
				lastLineRead = dataFileIn.readNextUnixLine();
			else
			{
				int nRecordLength = recordLengthDefinition.getRecordLength();
				lastLineRead = dataFileIn.readBuffer(nRecordLength, true);
			}
		}
		return lastLineRead;
	}
	
	public LogicalFileDescriptor getLogicalFileDescriptor()
	{
		if(fileManagerEntry != null)
			return fileManagerEntry.getLogicalFileDescriptor();
		return null;
	}
	
	public void tryAutoDetermineRecordLengthIfRequired(BaseDataFile dataFileIn)
	{
		// the return value is a flag that indicates if we have a valid file position on output 
		if(isVariableLength())
			return ;	// We are a variable length file: no need to try to autodetermine record length; file position is valid
		if(getRecordLengthDefinition() != null)
			return ;	// we have the record definition: no need to try to autodetermine record length; file position is valid
		
		// We must try to autodetermine record length
		LogicalFileDescriptor logicalFileDescriptor = getLogicalFileDescriptor();
		if(logicalFileDescriptor != null)
			logicalFileDescriptor.tryAutoDetermineRecordLength(dataFileIn);
	}
	
	// New OO API support 
	public void write(WriteBufferExt writeBufferExt, boolean bForcedVariableLenght)
	{
		if(fileManagerEntry.isVariableLength() || bForcedVariableLenght)
		{
			int nRecordLength = writeBufferExt.getRecordCurrentPosition();	// Measure record length
			// write record header
			if(tbyHeader == null)
				tbyHeader = new byte[4];
			LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader, nRecordLength, 0);	// DO not include header length in header !
			fileManagerEntry.dataFile.write(tbyHeader, 0, 4);
			
			byte tbyFilebuffer[] = writeBufferExt.getAsByteArrayWithTrailingLF();
			fileManagerEntry.dataFile.writeWithEOL(tbyFilebuffer, tbyFilebuffer.length);

			incNbRecordWrite();
		}
		else
		{
			byte tbyFilebuffer[] = writeBufferExt.getAsByteArrayWithTrailingLF();
			fileManagerEntry.dataFile.writeWithEOL(tbyFilebuffer, tbyFilebuffer.length);
			incNbRecordWrite();
		}
	}
	
	public void rewrite(WriteBufferExt writeBufferExt)
	{
		byte tbyFilebuffer[] = writeBufferExt.getAsByteArrayWithTrailingLF();
		fileManagerEntry.dataFile.rewriteWithEOL(tbyFilebuffer, tbyFilebuffer.length);
		incNbRecordWrite();
	}
				
	public boolean read(WriteBufferExt writeExt)
	{
		if(fileManagerEntry.isDummyFile())
			return false;

		if(isVariableLength())
		{
			long lLastHeaderStartPosition = fileManagerEntry.dataFile.getFileCurrentPosition();	// Keep header start position
			LineRead header = fileManagerEntry.dataFile.readBuffer(4, false);		// Read header
			if(header != null)
			{				
				int nLengthExcludingHeader = header.getAsLittleEndingUnsignBinaryInt();	// Length in header doesn't count the header itself
				LineRead lineRead = fileManagerEntry.dataFile.readBuffer(nLengthExcludingHeader, true);		// Read record body, including trailing LF
				fileManagerEntry.dataFile.setLastPosition(lLastHeaderStartPosition);	// Save current position at the header start
				if(lineRead != null)
				{
					writeExt.setFromLineRead(lineRead, 0);	
					int n = getVariableRecordLength(nLengthExcludingHeader);
					writeExt.setVariableRecordWholeLength(n);
					return true;
				}
			}
			return false;
		}
		else
		{
			if(fileManagerEntry.dataFile.isEOF())
				return false;
			int nRecordLength = getRecordLength(null);
			LineRead lineRead = null;
			if(nRecordLength > 0)
				lineRead = fileManagerEntry.dataFile.readBuffer(nRecordLength, true);	// PJD TO UNCOMMENT 
			else
				lineRead = fileManagerEntry.dataFile.readNextUnixLine();
			if(lineRead != null)
			{
				writeExt.setFromLineRead(lineRead, 0);	
				incNbRecordRead();
				return true;
			}
			return false;
		}
	}
}
