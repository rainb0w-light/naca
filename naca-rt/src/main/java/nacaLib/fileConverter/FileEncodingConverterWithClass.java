/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fileConverter;

import jlib.log.Log;
import jlib.misc.DataFileLineReader;
import jlib.misc.LineRead;
import jlib.misc.LittleEndingSignBinaryBufferStorage;
import nacaLib.basePrgEnv.BaseProgramManager;
import nacaLib.basePrgEnv.BaseResourceManager;
import nacaLib.classLoad.CustomClassDynLoaderFactory;
import nacaLib.tempCache.TempCacheLocator;
import nacaLib.varEx.*;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: FileEncodingConverterWithClass.java,v 1.10 2007/06/09 12:04:22 u930bm Exp $
 */
public class FileEncodingConverterWithClass extends FileEncodingConverter
{
	public FileEncodingConverterWithClass(FileDescriptor fileIn, FileDescriptor fileOut)
	{
		super(fileIn, fileOut);
	}
	
	public boolean execute(String csCopyClass)
	{
		fileIn.getPhysicalName();
		fileOut.getPhysicalName();
		if(fileIn.isEbcdic() != fileOut.isEbcdic() || ishost)
		{
			return convert(csCopyClass);
		}
		else
		{
			return copyFile();
		}
	}

	private boolean convert(String csCopyClass)
	{
		boolean bEbcdicIn = fileIn.isEbcdic();
		boolean bEbcdicOut = fileOut.isEbcdic();

		BaseResourceManager.initCopyConverterClassLoader();
		
		TempCacheLocator.setTempCache();	// Init TLS
		ConverterProgram converterProgram = new ConverterProgram();
		Object obj = CopyConverterClassLoader.getInstance(csCopyClass, CustomClassDynLoaderFactory.getInstance(), converterProgram);
		if(obj == null)
		{
			Log.logCritical("Cannot load Copy class " + csCopyClass);
			return false;
		}
			
		// Compute the working
		BaseProgramManager converterProgramManager = converterProgram.getProgramManager();
		converterProgramManager.prepareCall(null, converterProgram, null, null, true);
		
		VarBase varRoot = converterProgram.getProgramManager().getRoot();
		VarDefEncodingConvertibleManagerContainer varDefEncodingConvertibleManagerContainer = new VarDefEncodingConvertibleManagerContainer();
		
		// Read all record form source into structure
		String csFileIn = fileIn.getPhysicalName();
		DataFileLineReader dataFileIn = new DataFileLineReader(csFileIn, 65536, 0);
		boolean isinOpened = dataFileIn.open();
		if(isinOpened)
		{
			fileOut.openOutput();
			boolean isvariableLength = fileIn.isVariableLength();
			if (ishost)
			{
				if (isheaderEbcdic)
				{
					byte[] tbyHeaderEbcdic = new String("<FileHeader Version=\"1\" Encoding=\"ebcdic\"/>").getBytes();
					fileOut.write(tbyHeaderEbcdic, 0, tbyHeaderEbcdic.length, true);
				}

				if (nLengthRecord == 0)
				{
					byte[] tbyHeader4 = new byte[4];
					if (isvariable4)
					{	
						LineRead lineRead = dataFileIn.readBuffer(4, false);
						while (lineRead != null)
						{
							int i1 = lineRead.getBuffer()[lineRead.getOffset()];
							if (i1 < 0) i1 = 256 + i1;
							int i2 = lineRead.getBuffer()[lineRead.getOffset() + 1];
							if (i2 < 0) i2 = 256 + i2;
							int nCurrentRecordLength = (i1 * 256) + i2 - 4;
							
							if (isvariableLength)
							{
								LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader4, nCurrentRecordLength, 0);
								fileOut.write(tbyHeader4, 0, tbyHeader4.length, false);
							}
							
							lineRead = dataFileIn.readBuffer(nCurrentRecordLength, false);
							if (bEbcdicIn && !bEbcdicOut)
								varDefEncodingConvertibleManagerContainer.getEncodingManagerConvertAndWrite(lineRead, varRoot);
							else
								varRoot.setFromLineRead(lineRead);
							fileOut.writeFrom(varRoot);
							fileOut.getBaseDataFile().writeEndOfRecordMarker();
							lineRead = dataFileIn.readBuffer(4, false);
						}
					}
					else
					{
						LineRead lineRead = dataFileIn.readBuffer(3, false);
						while (lineRead != null)
						{
							int i1 = lineRead.getBuffer()[lineRead.getOffset() + 1];
							int i2 = lineRead.getBuffer()[lineRead.getOffset() + 2];
							int nCurrentRecordLength = i1 * 256 + i2;
							
							if (isvariableLength)
							{
								LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader4, nCurrentRecordLength, 0);
								fileOut.write(tbyHeader4, 0, tbyHeader4.length, false);
							}
							
							lineRead = dataFileIn.readBuffer(nCurrentRecordLength, false);
							if (bEbcdicIn && !bEbcdicOut)
								varDefEncodingConvertibleManagerContainer.getEncodingManagerConvertAndWrite(lineRead, varRoot);
							else
								varRoot.setFromLineRead(lineRead);
							fileOut.writeFrom(varRoot);
							fileOut.getBaseDataFile().writeEndOfRecordMarker();
							lineRead = dataFileIn.readBuffer(3, false);
						}
					}
				}
				else
				{
					LineRead lineRead = dataFileIn.readBuffer(nLengthRecord, false);
					while (lineRead != null)
					{
						if (bEbcdicIn && !bEbcdicOut)	// Must convert string chunks to ascii
							varDefEncodingConvertibleManagerContainer.getEncodingManagerConvertAndWrite(lineRead, varRoot);
						else	// !bEbcdicIn && bEbcdicOut
							varRoot.setFromLineRead(lineRead);
						fileOut.writeFrom(varRoot);
						fileOut.getBaseDataFile().writeEndOfRecordMarker();
						lineRead = dataFileIn.readBuffer(nLengthRecord, false);
					}
				}
			}
			else
			{
				LineRead lineRead = fileIn.readALine(dataFileIn, null);
				while(lineRead != null)
				{
					if(isvariableLength)
						lineRead.shiftOffset(4);	// Skip record header
					
					if (bEbcdicIn && !bEbcdicOut)	// Must convert string chunks to ascii
						varDefEncodingConvertibleManagerContainer.getEncodingManagerConvertAndWrite(lineRead, varRoot);
					else	// !bEbcdicIn && bEbcdicOut
						varRoot.setFromLineRead(lineRead);
	
					if(isvariableLength)
						lineRead.shiftOffset(-4);
	
					fileOut.writeFrom(varRoot);
					lineRead = fileIn.readALine(dataFileIn, lineRead);
				}
			}	
			fileOut.close();
			dataFileIn.close();
		}
		
		return true;
	}
}
