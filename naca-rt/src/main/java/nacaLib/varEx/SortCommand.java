/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.log.Log;
import jlib.misc.DataFileWrite;
import jlib.misc.LittleEndingSignBinaryBufferStorage;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.bdb.BTreeCommandSort;
import nacaLib.bdb.BtreeFile;
import nacaLib.bdb.BtreeKeyDescription;
import nacaLib.program.*;

public class SortCommand
{
	private SortDescriptor sortDescriptorDeclared = null;
	
	public SortCommand(BaseProgramManager programManager, SortDescriptor sortDescriptorDeclared)
	{
		this.programManager = programManager;
		this.sortDescriptorDeclared = sortDescriptorDeclared;
	}
	
	public SortCommand exportKey(String csExportKeyFile)
	{
		dataFileKeyOut = new DataFileWrite(csExportKeyFile, false);
		boolean bOutKeyOpened = dataFileKeyOut.open();
		if(!bOutKeyOpened)
		{
			dataFileKeyOut = null;
			Log.logImportant("Cannot create output key file " + csExportKeyFile);
		}
		return this;
	}
	
	public SortCommand ascKey(Var var)
	{
		SortKeySegmentDefinition keySegment = new SortKeySegmentDefinition(var, true);
		btreeKeyDescription.addSegmentDefinition(keySegment);
		return this;
	}

	public SortCommand descKey(Var var)
	{
		SortKeySegmentDefinition keySegment = new SortKeySegmentDefinition(var, false);
		btreeKeyDescription.addSegmentDefinition(keySegment);
		return this;
	}
	
	public SortCommand using(FileDescriptor fileDescIn)
	{
		this.fileDescIn = fileDescIn;
		return this;
	}

	public SortCommand giving(FileDescriptor fileDescOut)
	{
		this.fileDescOut = fileDescOut;
		return this;
	}

	public SortCommand usingInput(Paragraph paraInputMin, Paragraph paraInputMax)
	{
		this.paraInputMin = paraInputMin;
		this.paraInputMax = paraInputMax;
		sectionInput = null;
		return this;
	}
	
	public SortCommand usingInput(Paragraph paraInput)
	{
		this.paraInputMin = paraInput;
		this.paraInputMax = null;
		this.sectionInput = null;
		return this;
	}

	public SortCommand usingInput(Section section)
	{
		this.paraInputMin = null;
		this.paraInputMax = null;
		this.sectionInput = section;
		return this;
	}
	
	
	public SortCommand usingOutput(Paragraph paraOutputMin, Paragraph paraOutputMax)
	{
		this.paraOutputMin = paraOutputMin;
		this.paraOutputMax = paraOutputMax;
		this.sectionOutput = null;
		return this;
	}

	public SortCommand usingOutput(Paragraph paraOutput)
	{
		this.paraOutputMin = paraOutput;
		this.paraOutputMax = paraOutput;
		this.sectionOutput = null;
		return this;
	}
	
	public SortCommand usingOutput(Section secOutput)
	{
		this.paraOutputMin = null;
		this.paraOutputMax = null;
		this.sectionOutput = secOutput;
		return this;
	}
	
	public void exec()
	{
		nNbRecordImported = 0;
		
		btreeKeyDescription.addRecordIdKeySegment();
				
		btreeCommandSort = new BTreeCommandSort();
		btreeCommandSort.setTempDir(BaseResourceManager.getTempDir());
		
		boolean bInputIsFile = false;
		boolean bEbcdicIn = false;
		// Input
		if(fileDescIn != null)	// read form input file
		{
			//String csFileNameIn = fileDescIn.getPhysicalName();
			//bEbcdicIn = fileDescIn.isEbcdic();
			bInputIsFile = true;
			
			btreeKeyDescription.setFileInEncoding(bEbcdicIn);

			//btreeCommandSort.setPhysicalInFileName(csFileNameIn, bEbcdicIn);
			btreeCommandSort.setKeyDescription(btreeKeyDescription);
			
			csBtrieveFileName = btreeCommandSort.getTempFileName();
			btreeFile = btreeCommandSort.createAndOpenTempBtrieveFile(csBtrieveFileName);
			if (btreeFile == null)
			{
				throw new RuntimeException("Cannot create btreefile");
			}
			else
			{
				btreeFile.setKeyDescription(btreeKeyDescription);
				nNbRecordImported = btreeCommandSort.importInFile(btreeFile, fileDescIn, BaseResourceManager.getFileLineReaderBufferSize(), false);
			}
		}
		else if(sectionInput != null)	// Read from section
		{
			btreeKeyDescription.setFileInEncoding(false);	// Source = code: Always ascii
			
			SortParagHandler sortParagHandler = new SortParagHandler(this);  
			programManager.setCurrentSortCommand(sortParagHandler);
			programManager.perform(sectionInput);
			programManager.setCurrentSortCommand(null);
		}
		else if(paraInputMax != null)	// Read from interval of paragraph code
		{
			btreeKeyDescription.setFileInEncoding(false);	// Source = code: Always ascii
			
			SortParagHandler sortParagHandler = new SortParagHandler(this);  
			programManager.setCurrentSortCommand(sortParagHandler);
			programManager.performThrough(paraInputMin, paraInputMax);
			programManager.setCurrentSortCommand(null);
		}
		else	// Read from paragraph code
		{
			btreeKeyDescription.setFileInEncoding(false);	// Source = code: Always ascii
			
			SortParagHandler sortParagHandler = new SortParagHandler(this);  
			programManager.setCurrentSortCommand(sortParagHandler);
			programManager.perform(paraInputMin);
			programManager.setCurrentSortCommand(null);
		}

		// Output
		if(fileDescOut != null)	// Output to file
		{
			String csFileNameOut = fileDescOut.getPhysicalName();
			boolean bEbcdicOut = fileDescOut.isEbcdic();
			boolean bMustSwapByteEncodingOnOutput = false;
			if(bInputIsFile && bEbcdicOut != bEbcdicIn)
				bMustSwapByteEncodingOnOutput = true;
			btreeCommandSort.setPhysicalOutFile(csFileNameOut);
			btreeCommandSort.setFileExportKey(dataFileKeyOut);
			btreeCommandSort.exportToOutFile(btreeFile, bMustSwapByteEncodingOnOutput, bEbcdicOut);
		}
		else if(sectionOutput != null)	// Output to section
		{
			SortParagHandler sortParagHandler = new SortParagHandler(this);  
			programManager.setCurrentSortCommand(sortParagHandler);
			programManager.perform(sectionOutput);
			programManager.setCurrentSortCommand(null);
		}
		else	// Output to interval of paragraphs
		{
			SortParagHandler sortParagHandler = new SortParagHandler(this);  
			programManager.setCurrentSortCommand(sortParagHandler);
			programManager.performThrough(paraOutputMin, paraOutputMax);
			programManager.setCurrentSortCommand(null);
		}
		
		btreeCommandSort.closeAndDelete(btreeFile, csBtrieveFileName);
		
		if(dataFileKeyOut != null)	// Must export key file
			dataFileKeyOut.close();
	}
	
	protected void release(Var varRecord)	// A record is given by a paragraph for btrieve importation
	{
		int nTotalLength = 0;

		boolean bVariableLength = false;
		if(sortDescriptorDeclared != null)
		{
			bVariableLength = sortDescriptorDeclared.hasVarVariableLengthMarker();	// The sort descriptor has a variable length marker: The data is of variable length
			nTotalLength = sortDescriptorDeclared.getRecordLength(varRecord);
			if(bVariableLength)
				nTotalLength += 4;	//Reserve space for record header; it will be stored in the data to sort
		}
		else	
			nTotalLength = varRecord.getLength();
		
		if(nNbRecordImported == 0)
		{
			btreeCommandSort.setKeyDescription(btreeKeyDescription);
			csBtrieveFileName = btreeCommandSort.getTempFileName();	
			btreeFile = btreeCommandSort.createAndOpenTempBtrieveFile(csBtrieveFileName);
			if (btreeFile == null)
			{
				throw new RuntimeException("Cannot create btreefile");
			}
			else
			{
				btreeFile.setKeyDescription(btreeKeyDescription);
			}
		}
		if(btreeFile != null)
		{				
			checkBytebuffer(nTotalLength);
			
//			if(debugCheckSpecialBytes(tBytesDataRelease, nTotalLength))
//			{
//				int nDebugf = 0;
//			}
			
			if(!bVariableLength)
				varRecord.exportToByteArray(tBytesDataRelease, nTotalLength);
			else
			{
				LittleEndingSignBinaryBufferStorage.writeInt(tBytesDataRelease, 0, nTotalLength-4);					
				varRecord.exportToByteArray(tBytesDataRelease, 4, nTotalLength-4);
			}
				
			boolean b = btreeFile.internalSortInsertWithRecordIndexAtEnd(tBytesDataRelease, 0, nTotalLength, nNbRecordImported, bVariableLength);
			nNbRecordImported++;
		}		
	}
	
	// To remove
//	private boolean debugCheckSpecialBytes(byte tSource[], int nSourceLength)
//	{
//		for(int n=0; n<nSourceLength-4; n++)
//		{
//			if(tSource[n] == 0x10 && tSource[n+1] == 0x2d && tSource[n+2] == 0x26 && tSource[n+3] == 0x0c)
//			{
//				return true;
//			}			
//		}
//		return false;
//	}
	
	private void checkBytebuffer(int nLength)
	{
		if(tBytesDataRelease == null || tBytesDataRelease.length < nLength)
			tBytesDataRelease = new byte[nLength];
	}
	
	protected RecordDescriptorAtEnd returnSort(SortDescriptor sortDescriptor)
	{
		if(btreeFile != null)
		{
			byte tDataWithHeader[] = btreeFile.getNextSortedRecord();

			if(tDataWithHeader != null)
			{
				if(dataFileKeyOut != null)	// Must export key file; Not usable in multithread mode
				{
					byte tBytesKey[] = btreeFile.getKeyRead();
					dataFileKeyOut.writeWithEOL(tBytesKey, tBytesKey.length);
				}
				
				sortDescriptor.fillRecord(tDataWithHeader);
				return RecordDescriptorAtEnd.NotEnd;
			}
		}
		return RecordDescriptorAtEnd.End;
	}
	
	private BaseProgramManager programManager = null;
	private FileDescriptor fileDescIn = null;
	private FileDescriptor fileDescOut = null;
	private BtreeKeyDescription btreeKeyDescription = new BtreeKeyDescription(); 
	private Section sectionInput = null;
	private Paragraph paraInputMin = null;
	private Paragraph paraInputMax = null;
	private Section sectionOutput = null;
	private Paragraph paraOutputMin = null;
	private Paragraph paraOutputMax = null;
	
	private BTreeCommandSort btreeCommandSort = null;
	private BtreeFile btreeFile = null;

	private int nNbRecordImported = 0;
	private String csBtrieveFileName = null;
	private byte[] tBytesDataRelease = null;
	private DataFileWrite dataFileKeyOut = null;
}
