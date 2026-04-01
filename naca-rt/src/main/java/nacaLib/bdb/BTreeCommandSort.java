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

import jlib.log.Log;
import jlib.misc.AsciiEbcdicConverter;
import jlib.misc.DataFileLineReader;
import jlib.misc.DataFileWrite;
import jlib.misc.FileSystem;
import jlib.misc.LineRead;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.FileDescriptor;

import com.sleepycat.je.Environment;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: BTreeCommandSort.java,v 1.37 2008/01/18 13:04:12 u930bm Exp $
 */
public class BTreeCommandSort
{
	private String csTempDir = null;
	
	//private String csFileIn = null;
	//private boolean bFileInEbcdic = false;
	
	private String csFileOut = null;
	
	private BTreeEnv btreeEnv = null;
	private BtreeKeyDescription keyDescription = null;
	
	private DataFileWrite dataFileKeyOut = null;
	//private boolean bCanSortMultiThreads = false;
	
	public BTreeCommandSort()
	{
		//bCanSortMultiThreads = bCanSortMultiThreads;
	}
	
	public void setTempDir(String csTempDir)
	{
		csTempDir = FileSystem.normalizePath(csTempDir);
		FileSystem.createPath(csTempDir);		
	}
	
//	public void setPhysicalInFileName(String csFileIn, boolean bFileInEbcdic)
//	{
//		csFileIn = csFileIn;
//		bFileInEbcdic = bFileInEbcdic;
//	}

	public void setPhysicalOutFile(String csFileOut)
	{
		this.csFileOut = csFileOut;
	}

	public void setFileExportKey(DataFileWrite dataFileKeyOut)
	{
		this.dataFileKeyOut = dataFileKeyOut;
	}
	
	public void setExportKeyFileOut(String csExportKeyFileOut)
	{
		if(csExportKeyFileOut != null)
		{
			dataFileKeyOut = new DataFileWrite(csExportKeyFileOut, false);
			boolean isoutKeyOpened = dataFileKeyOut.open();
			if(!isoutKeyOpened)
			{
				dataFileKeyOut = null;
				Log.logImportant("Cannot create output key file " + csExportKeyFileOut);
			}
		}
	}
	
	public void set(String csTempDir, String csFileOut, String csKeys)
	{
		setTempDir(csTempDir);
		if(csFileOut != null)
			setPhysicalOutFile(csFileOut);
		setKeyDescription(csKeys);	
	}
	
	public void setKeyDescription(String csKeys)
	{
		keyDescription = new BtreeKeyDescription();
		keyDescription.set(csKeys, true);
		TempCacheLocator.getTLSTempCache().setBtreeKeyDescription(keyDescription);
	}
	
	public void setKeyDescription(BtreeKeyDescription keyDescription)
	{
		this.keyDescription = keyDescription;
		TempCacheLocator.getTLSTempCache().setBtreeKeyDescription(keyDescription);
	}
	
	public boolean execute(int nBufferChunkReadAHead, FileDescriptor fileSortIn, FileDescriptor fileSortOut)
	{
		String csFileIn = fileSortIn.getPhysicalName();
		if(fileSortIn.getRecordLengthDefinition() == null)
		{
			fileSortOut.getPhysicalName();
			fileSortIn.inheritSettings(fileSortOut);
		}
		
		boolean isfileInEbcdic = fileSortIn.isEbcdic();
		keyDescription.setFileInEncoding(isfileInEbcdic);
		
		String csBtreeDir = getTempFileName();
		BtreeFile btreeFile = createAndOpenTempBtrieveFile(csBtreeDir);
		if(btreeFile == null)
		{
			throw new RuntimeException("Cannot create btreefile");
		}
		else
		{
			btreeFile.setKeyDescription(keyDescription);
			int nNbRecordRead = importInFile(btreeFile, fileSortIn, nBufferChunkReadAHead, true);
			if(nNbRecordRead >= 0)
				exportToOutFile(btreeFile, false, false);
			closeAndDelete(btreeFile, csBtreeDir);
			if(nNbRecordRead < 0)
				return false;			
		}
		return true;
	}
	
	public String getTempFileName()
	{
		if(csTempDir == null)
			csTempDir = "./";
		String csTempFile = csTempDir + FileSystem.getTempFileName();
		return csTempFile;		
	}
	
	public BtreeFile createAndOpenTempBtrieveFile(String csBtreeDir)
	{
		csBtreeDir = FileSystem.normalizePath(csBtreeDir);
		FileSystem.createPath(csBtreeDir);

		if(connectBtreeEngine(csBtreeDir))
		{
			BtreeFile btreeFile = btreeEnv.createBtreeFile("Btree");	//, bCanSortMultiThreads);	
			return btreeFile; 
		}
		
		return null;			
	}
	
	public void closeAndDelete(BtreeFile btreeFile, String csBtreeDir)
	{
		if(btreeFile != null)
			btreeFile.close();
		
		if(btreeEnv != null)
			btreeEnv.close();
		
		if(csBtreeDir != null)
			FileSystem.DeleteDirAndContent(csBtreeDir);
	}
	
	private boolean connectBtreeEngine(String csDir)
	{
		if(btreeEnv == null)
		{
			btreeEnv = new BTreeEnv();
			boolean b = btreeEnv.initEngine(csDir);
			return b;
		}
		return true;
	}
	
	public int importInFile(BtreeFile btreeFile, FileDescriptor fileSortIn, int nBufferChunkReadAHead, boolean bExternalSort)
	{
		int nNbRecordRead = 0;
		String csFileIn = fileSortIn.getPhysicalName();
		DataFileLineReader dataFileIn = new DataFileLineReader(csFileIn, nBufferChunkReadAHead, 0);
		boolean isinOpened = dataFileIn.open();
		if(isinOpened)
		{
			fileSortIn.tryAutoDetermineRecordLengthIfRequired(dataFileIn);
			
			boolean isfileInVariableLength = fileSortIn.hasVarVariableLengthMarker();
			boolean  b = true;
			boolean isfileInEbcdic = fileSortIn.isEbcdic();
			LineRead lineRead = fileSortIn.readALine(dataFileIn, null);
			Environment env = btreeEnv.getEnv();
			while(lineRead != null && b == true)
			{
				b = btreeFile.externalSortInsertWithRecordIndexAtEnd(env, lineRead, nNbRecordRead, isfileInEbcdic, isfileInVariableLength);
				nNbRecordRead++;
				lineRead = fileSortIn.readALine(dataFileIn, lineRead);
			}
			
			dataFileIn.close();
			Log.logNormal("" + nNbRecordRead + " records imported into btree file from " + csFileIn);
		}
		else
		{
			Log.logCritical("Could not open file " + csFileIn);
			return -1;
		}
		return nNbRecordRead;
	}
//	
//	public int importInFile(BtreeFile btreeFile, int nBufferChunkReadAHead)
//	{
//		int nNbRecordRead = 0;
//		DataFileLineReader dataFileIn = new DataFileLineReader(csFileIn, nBufferChunkReadAHead, 0);
//		boolean bInOpened = dataFileIn.open();
//		if(bInOpened)
//		{
//			boolean b = true;
//			LineRead lineRead = dataFileIn.readNextUnixLine();
//			while(lineRead != null && b == true)
//			{
////				if(bFileInEbcdic)
////					AsciiEbcdicConverter.swapByteEbcdicToAscii(lineRead.getBuffer(), lineRead.getOffset(), lineRead.getTotalLength());
//				
//				//String cs = lineRead.getChunkAsString();
//				
//				b = btreeFile.externalSortInsertWithRecordIndexAtEnd(btreeEnv.getEnv(), lineRead, nNbRecordRead, bFileInEbcdic);
//				lineRead = dataFileIn.readNextUnixLine();
//				nNbRecordRead++;
//			}
//			dataFileIn.close();
//		}		
//		Log.logCritical("" + nNbRecordRead + " records imported into btree file from " + csFileIn);
//	
//		return nNbRecordRead;
//	}
	
	public int exportToOutFile(BtreeFile btreeFile, boolean bMustSwapByteEncodingOnOutput, boolean bToEbcdic)
	{
		int nNbRecordWrite = 0;
		boolean bMustWriteFileHeader = false;
		//boolean bMustWriteFileHeader = BaseResourceManager.getMustWriteFileHeader();
		DataFileWrite dataFileOut = new DataFileWrite(csFileOut, bMustWriteFileHeader);
		boolean isoutOpened = dataFileOut.open();
		if(isoutOpened)
		{
			if(btreeFile != null)	// We have a sorted file to write on output
			{
				//btreeFile.tryLaunchAsyncSortReader();
				//byte tBytesData[] = btreeFile.syncGetFirst();
				
				byte tBytesData[] = btreeFile.getNextSortedRecord();
				while(tBytesData != null)
				{
					if(dataFileKeyOut != null)	// Must export key file
					{
						byte tbyKey[] = keyDescription.fillKeyBuffer(tBytesData, 0, nNbRecordWrite, false);
						dataFileKeyOut.writeWithEOL(tbyKey, tbyKey.length-4);
					}
					int nRecordLengthWithoutHeader = tBytesData.length;
					if(bMustSwapByteEncodingOnOutput)
					{
						if(bToEbcdic)
							AsciiEbcdicConverter.swapByteAsciiToEbcdic(tBytesData, 0, nRecordLengthWithoutHeader);
						else
							AsciiEbcdicConverter.swapByteEbcdicToAscii(tBytesData, 0, nRecordLengthWithoutHeader);						
					}
					dataFileOut.write(tBytesData, 0, nRecordLengthWithoutHeader);
					dataFileOut.writeEndOfRecordMarker();
					
					//tBytesData = btreeFile.syncGetNext();
					tBytesData = btreeFile.getNextSortedRecord();
					
					nNbRecordWrite++;
				}
				if(dataFileKeyOut != null)
				{
					dataFileKeyOut.close();
					
					// Check key out file 
					//boolean b = Dumper.isFileRecordsOrdered(dataFileKeyOut.getName(), true);
					dataFileKeyOut = null;
				}
			}
			dataFileOut.close();
		}
		Log.logNormal("" + nNbRecordWrite + " records exported from btree file into " + csFileOut);
		return nNbRecordWrite;
	}
}
