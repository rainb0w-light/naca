/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fileConverter;

import jlib.misc.DataFileLineReader;
import jlib.misc.LineRead;
import jlib.misc.LittleEndingSignBinaryBufferStorage;
import jlib.misc.NumberParser;
import nacaLib.varEx.FileDescriptor;
import nacaLib.varEx.VarDefEncodingConvertibleManager;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: FileEncodingConverterWithDesc.java,v 1.11 2007/06/09 12:04:22 u930bm Exp $
 */
public class FileEncodingConverterWithDesc extends FileEncodingConverter
{
	private String csDesc = null;
	private VarDefEncodingConvertibleManager encodingManager = null;  
	
	public FileEncodingConverterWithDesc(FileDescriptor fileIn, FileDescriptor fileOut)
	{
		super(fileIn, fileOut);
	}
		
	public boolean execute(String csDesc)
	{
		fileIn.getPhysicalName();
		fileOut.getPhysicalName();
		if (fileIn.isEbcdic() != fileOut.isEbcdic() || bHost)
		{
			return convert(csDesc);
		}
		else
		{
			return copyFile();
		}
	}
	
	private boolean convert(String csDesc)
	{
		if (!csDesc.equals(""))
			fillDesc(csDesc);
		
		boolean bEbcdicIn = fileIn.isEbcdic();
		boolean bEbcdicOut = fileOut.isEbcdic();

		// Read all record form source into structure
		String csFileIn = fileIn.getPhysicalName();
		DataFileLineReader dataFileIn = new DataFileLineReader(csFileIn, 65536, 0);
		boolean bInOpened = dataFileIn.open();
		if(bInOpened)
		{
			fileOut.openOutput();
			boolean bVariableLength = fileIn.isVariableLength();
			if (bHost)
			{
				if (bHeaderEbcdic)
				{
					byte[] tbyHeaderEbcdic = new String("<FileHeader Version=\"1\" Encoding=\"ebcdic\"/>").getBytes();
					fileOut.write(tbyHeaderEbcdic, 0, tbyHeaderEbcdic.length, true);
				}
				
				if (nLengthRecord == 0)
				{
					byte[] tbyHeader4 = new byte[4];
					if (bVariable4)
					{	
						LineRead lineRead = dataFileIn.readBuffer(4, false);
						while (lineRead != null)
						{
							int i1 = lineRead.getBuffer()[lineRead.getOffset()];
							if (i1 < 0) i1 = 256 + i1;
							int i2 = lineRead.getBuffer()[lineRead.getOffset() + 1];
							if (i2 < 0) i2 = 256 + i2;
							int nCurrentRecordLength = (i1 * 256) + i2 - 4;
							
							if (bVariableLength)
							{
								LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader4, nCurrentRecordLength, 0);
								fileOut.write(tbyHeader4, 0, tbyHeader4.length, false);
							}
							
							lineRead = dataFileIn.readBuffer(nCurrentRecordLength, false);
							if (bEbcdicIn && !bEbcdicOut)
								encodingManager.getConvertedBytesEbcdicToAscii(lineRead);
							else if (!bEbcdicIn && bEbcdicOut)
								encodingManager.getConvertedBytesAsciiToEbcdic(lineRead);
							
							fileOut.write(lineRead.getBuffer(), lineRead.getOffset(), lineRead.getBodyLength(), true);
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
							
							if (bVariableLength)
							{
								LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader4, nCurrentRecordLength, 0);
								fileOut.write(tbyHeader4, 0, tbyHeader4.length, false);
							}
							
							lineRead = dataFileIn.readBuffer(nCurrentRecordLength, false);
							if (bEbcdicIn && !bEbcdicOut)
								encodingManager.getConvertedBytesEbcdicToAscii(lineRead);
							else if (!bEbcdicIn && bEbcdicOut)
								encodingManager.getConvertedBytesAsciiToEbcdic(lineRead);
							
							fileOut.write(lineRead.getBuffer(), lineRead.getOffset(), lineRead.getBodyLength(), true);
							lineRead = dataFileIn.readBuffer(3, false);
						}
					}
				}
				else
				{
					LineRead lineRead = dataFileIn.readBuffer(nLengthRecord, false);
					while (lineRead != null)
					{	
						if (bEbcdicIn && !bEbcdicOut)
							encodingManager.getConvertedBytesEbcdicToAscii(lineRead);
						else if (!bEbcdicIn && bEbcdicOut)
							encodingManager.getConvertedBytesAsciiToEbcdic(lineRead);
						fileOut.write(lineRead.getBuffer(), lineRead.getOffset(), lineRead.getBodyLength(), true);
						lineRead = dataFileIn.readBuffer(nLengthRecord, false);
					}
				}
			}
			else
			{
				LineRead lineRead = fileIn.readALine(dataFileIn, null);
				while(lineRead != null)
				{
					if(bVariableLength)
						lineRead.shiftOffset(4);	// Skip record header
	
					if (bEbcdicIn && !bEbcdicOut)
						encodingManager.getConvertedBytesEbcdicToAscii(lineRead);
					else if (!bEbcdicIn && bEbcdicOut)
						encodingManager.getConvertedBytesAsciiToEbcdic(lineRead);
	
					if(bVariableLength)
						lineRead.shiftOffset(-4);
	
					fileOut.writeFrom(lineRead);
					lineRead = fileIn.readALine(dataFileIn, lineRead);
				}
			}
			fileOut.close();
		}
		
		return true;
	}
	
	private void fillDesc(String csDesc)
	{
		csDesc = csDesc;
		encodingManager = new VarDefEncodingConvertibleManager();
		
		while(csDesc != null)
		{
			int nPosition = getChunkAsInt()-1;
			int nLength = getChunkAsInt();
			String csType = getChunk();
						
			if(csType.equalsIgnoreCase("CH"))
				encodingManager.add(nPosition, nLength);
			else if(csType.equalsIgnoreCase("CHB"))
				encodingManager.add(nPosition, nLength, true);
			else if(csType.equalsIgnoreCase("PRINT"))
				encodingManager.add(nPosition, nLength, false, true);
			else if(csType.equalsIgnoreCase("Comp0"))
				encodingManager.add(nPosition, nLength);
			else if(csType.equalsIgnoreCase("Comp0Signed"))
				encodingManager.add(nPosition, nLength-1);
		}
		
		encodingManager.compress();
	}
	
	private int getChunkAsInt()
	{
		String cs = getChunk();
		return NumberParser.getAsInt(cs);
	}
		
	private String getChunk()
	{
		String cs = null;
		int nIndex = csDesc.indexOf(',');
		if(nIndex == -1)
		{
			cs = csDesc;
			cs = cs.trim();
			csDesc = null;
		}
		else
		{
			cs = csDesc.substring(0, nIndex);
			cs = cs.trim();
			csDesc = csDesc.substring(nIndex+1);			
		}
		return cs;
	}
}
