/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.misc;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: LogicalFileDescriptor.java,v 1.16 2007/10/25 15:13:11 u930di Exp $
 */
public class LogicalFileDescriptor
{
	private boolean bDummyFile = false;
	private String csLogicalName = null;
	private String csPath = null;
	private boolean bExt = false;
	private boolean bEbcdic = false;
	private boolean bVariableLength = false;
	private boolean bVariableLength4BytesLF = false;
	private RecordLengthDefinition recordLengthDefinition = null;
	private int nFileHeaderLength = 0;
	private RecordLengthInfoDefinitionType recordLengthInfoDefinitionType = null;
	
	public LogicalFileDescriptor(String csLogicalName, String csPhysicalDesc)
	{
		csLogicalName = csLogicalName;
		if(BaseDataFile.isNullFile(csPhysicalDesc))	//	if(csLogicalName.equalsIgnoreCase("wrk/nullfile"))
			bDummyFile = true;
		else
		{
			bDummyFile = false;
			fill(csPhysicalDesc);			
		}			
	}
	
	public boolean isDummyFile()
	{
		return bDummyFile;
	}
	
	public boolean isEbcdic()
	{
		return bEbcdic;
	}

	public boolean getExt()
	{
		return bExt;
	}

	public String getPath()
	{
		return csPath;
	}
	
	public RecordLengthDefinition getRecordLengthDefinition()
	{
		return recordLengthDefinition;
	}
	
	public void setRecordLengthDefinition(RecordLengthDefinition recLengthDefSource)
	{
		recordLengthDefinition = recLengthDefSource;
	}
	
	public void fill(String csPhysicalDesc)
	{
		int nIndex = csPhysicalDesc.indexOf(",");
		if(nIndex >= 0)
		{
			csPath = csPhysicalDesc.substring(0, nIndex).trim();
			csPhysicalDesc = csPhysicalDesc.substring(nIndex+1);
			nIndex = csPhysicalDesc.indexOf(",");
			while(nIndex != -1)
			{
				String csWord = csPhysicalDesc.substring(0, nIndex).trim();
				manageOptionalWord(csWord);
			
				csPhysicalDesc = csPhysicalDesc.substring(nIndex+1);
				nIndex = csPhysicalDesc.indexOf(",");
			}
			manageOptionalWord(csPhysicalDesc);
		}
		else
			csPath = csPhysicalDesc.trim();
	}
	
	private void manageOptionalWord(String csWord)
	{
		if(csWord.equalsIgnoreCase("ext"))
			bExt = true;
		else if(csWord.equalsIgnoreCase("ebcdic"))
			bEbcdic = true;
		else if(csWord.equalsIgnoreCase("ascii"))
			bEbcdic = false;	
		else if(csWord.equalsIgnoreCase("fb"))
		{
			bVariableLength = false;
			bVariableLength4BytesLF = false;
			recordLengthInfoDefinitionType = RecordLengthInfoDefinitionType.FileDescriptorDef;
		}		
		else if(csWord.equalsIgnoreCase("vb"))
		{	
			bVariableLength = true;
			bVariableLength4BytesLF = true;
			recordLengthInfoDefinitionType = RecordLengthInfoDefinitionType.FileDescriptorDef;
		}
		else if(csWord.equalsIgnoreCase("vh"))
		{	
			bVariableLength = true;
			bVariableLength4BytesLF = false;
			recordLengthInfoDefinitionType = RecordLengthInfoDefinitionType.FileDescriptorDef;
		}
		else	// Maybe record length if all digits
		{
			if(StringUtil.isAllDigits(csWord))
			{
				recordLengthDefinition = new RecordLengthDefinition(NumberParser.getAsInt(csWord));
				recordLengthInfoDefinitionType = RecordLengthInfoDefinitionType.FileDescriptorDef;
			}			
		}
	}
	
	public void setVariableLength()
	{
		bVariableLength = true;
	}
	
	public boolean isVariableLength()
	{
		return bVariableLength;
	}
	
	public boolean isVariableLength4BytesHeaderWithLF()
	{
		return bVariableLength4BytesLF;
	}
	
	public String toString()
	{
		String cs = "";
		if(csPath != null)
			cs += "Path="+csPath;
		cs += " Ext="+bExt;
		cs += " Ebcdic="+bEbcdic;
		cs += " VariableLength="+bVariableLength;
		cs += " HAs 4 bytes header and LF="+bVariableLength4BytesLF;
		if(recordLengthDefinition != null)
			cs += " RecordLength="+recordLengthDefinition.toString();
		else
			cs += " NoRecordLengthDefined";
		return cs;
	}
	
	public String getName()
	{
		String cs = "";
		if(csLogicalName != null)
		{
			cs = csLogicalName;
			cs += getPathName();
		}
		else
		{
			cs = "UnkownLogicalName";
			cs += getPathName();
		}		
		return cs;
	}
	
	private String getPathName()
	{
		if (bDummyFile)
			return " (Dummy file) ";
		if(csPath != null)
			return " (" + csPath + ") ";
		return " (Unkown physical path) ";
	}
	
	public boolean writeFileHeader(BaseDataFile dataFile)
	{
		if(dataFile != null && dataFile.isOpen() && dataFile.isWritable() && !dataFile.isUpdateable())	// Do not write header for files in rewrite mode
		{
			String csFileHeader = getAsFileHeaderString();
			dataFile.writeRecord(csFileHeader);
			return true;
		}
		return false;
	}
	
	private String getAsFileHeaderString()
	{
		String cs = null;
		if(bEbcdic)
			cs = "<FileHeader Version=\"1\" Encoding=\"ebcdic\" ";
		else
			cs = "<FileHeader Version=\"1\" Encoding=\"ascii\" ";
		
		if(bVariableLength)
		{
			if(bVariableLength4BytesLF)
				cs += "Length=\"VB\"";
			else
				cs += "Length=\"VH\"";
		}
		else
		{	
			if(recordLengthDefinition != null)
				cs += "Length=\"" + recordLengthDefinition.toString() + "\"";
			else
				cs += "Length=\"Unknown\"";
		}
		cs += "/>";
		return cs;
	}
	
	private boolean setFromFileHeaderString(String cs)
	{
		if(cs.startsWith("<FileHeader") && cs.endsWith("/>"))
		{
			String csVersion = StringUtil.getUncotedParameterValue(cs, "Version");
			if(csVersion != null)
			{
				if(!StringUtil.isEmpty(csVersion))
				{
					int nVersion = NumberParser.getAsInt(csVersion);
					if(nVersion == 1)
					{
						String csEncoding = StringUtil.getUncotedParameterValue(cs, "Encoding");
						if(!StringUtil.isEmpty(csEncoding))
						{
							if(csEncoding.equalsIgnoreCase("ebcdic"))
								bEbcdic = true;
							if(csEncoding.equalsIgnoreCase("ascii"))
								bEbcdic = false;
						}
						
						String csLength = StringUtil.getUncotedParameterValue(cs, "Length");
						if(!StringUtil.isEmpty(csLength))
						{
							if(csLength.equalsIgnoreCase("VB"))
							{
								bVariableLength = true;
								bVariableLength4BytesLF = true;
								recordLengthInfoDefinitionType = RecordLengthInfoDefinitionType.FileHeaderDef;
								recordLengthDefinition = null;
							}
							else if(csLength.equalsIgnoreCase("VH"))
							{
								bVariableLength = true;
								bVariableLength4BytesLF = false;
								recordLengthInfoDefinitionType = RecordLengthInfoDefinitionType.FileHeaderDef;
								recordLengthDefinition = null;
							}
							else if(StringUtil.isAllDigits(csLength))
							{
								bVariableLength = false;
								recordLengthInfoDefinitionType = RecordLengthInfoDefinitionType.FileHeaderDef;
								int nLength = NumberParser.getAsInt(csLength);
								recordLengthDefinition = new RecordLengthDefinition(nLength);
							}
						}
					}
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean readFileHeader(BaseDataFile dataFile)
	{
		if(dataFile != null && dataFile.isOpen() && dataFile.isReadable())
		{
			String cs = dataFile.unbufferedReadAheadLine(100);
			if(cs != null)
			{
				boolean bFileHeaderFound = setFromFileHeaderString(cs);
				if(bFileHeaderFound)
				{
					nFileHeaderLength = dataFile.skipFileHeader(cs);
					return true;
				}
			}
			// else	// We are already atr the beginning of the file
			//{
			//	Do nothing
			//}
		}
		return false;
	}
	
	public void inheritSettings(LogicalFileDescriptor logicalFileDescriptorSource)
	{
		RecordLengthDefinition recLengthDefSource = logicalFileDescriptorSource.getRecordLengthDefinition();
		setRecordLengthDefinition(recLengthDefSource);
		
		bVariableLength = logicalFileDescriptorSource.bVariableLength;
		bVariableLength4BytesLF = logicalFileDescriptorSource.bVariableLength4BytesLF;
		recordLengthInfoDefinitionType = logicalFileDescriptorSource.recordLengthInfoDefinitionType;
	}
	
	public int getFileHeaderLength()
	{
		return nFileHeaderLength;
	}
	
	public RecordLengthInfoDefinitionType recordLengthInfoDefitionType()
	{
		return recordLengthInfoDefinitionType;
	}
	
	public boolean isLengthInfoDefined()
	{
		if(recordLengthInfoDefinitionType != null)
			return true;
		return false;
	}
	
	public boolean tryAutoDetermineRecordLength(BaseDataFile dataFile)
	{
		if(!isLengthInfoDefined())	// File header must have already been tried to read
		{
			if(dataFile.savePosition(65536 * 10))
			{
				boolean b = tryDetermineVariableLengthRecord(dataFile);
				if(!b)
				{
					dataFile.returnAtSavedPosition();
					b = tryDetermineFixedLengthRecord(dataFile);
				}
				
				dataFile.returnAtSavedPosition();
				return b;
			}
		}	
		return true;
	}
	
	private boolean tryDetermineVariableLengthRecord(BaseDataFile dataFile)
	{		
		// the bVariableLength4BytesLF is not supported in autodermination
		try
		{
			int nNbRecordHeaderOk = 0;
			int nNbRecordControled = 0;
			int nNbRecordHeaderChecked = 0;
			int nNbRecordHeaderNotOk = 0;
			for(; nNbRecordHeaderChecked<3; nNbRecordHeaderChecked++)	// Check on 3 records if there is a LF at offset nLength + 1
			{
				LineRead recordHeader = dataFile.readBuffer(4, false);
				if(recordHeader != null)
				{
					nNbRecordControled++;
					int nLength = recordHeader.getAsLittleEndingUnsignBinaryInt();
					if(nLength < 65536)
					{	
						LineRead recordBody = dataFile.readBuffer(nLength, true);
						if(recordBody != null)
						{
							if(recordBody.isTrailingLF())
								nNbRecordHeaderOk++;
							else
								nNbRecordHeaderNotOk++;
						}
					}
				}
			}
			if(nNbRecordHeaderOk == nNbRecordControled && nNbRecordHeaderOk > 0 && nNbRecordHeaderNotOk == 0)	// All record cheked are ok, even if less than 3 records in the file !
			{
				recordLengthInfoDefinitionType = RecordLengthInfoDefinitionType.AutoDetermination;
				bVariableLength = true;  
				bVariableLength4BytesLF = true;
				return true;
			}
		}
		catch(Exception e)
		{
		}
		return false;
	}


	private boolean tryDetermineFixedLengthRecord(BaseDataFile dataFile)
	{	
		int tnRecordLengthFound[] = new int[10];
		int tnRecordLengthQty[] = new int[10];
		int nNbRecordRead=0;
		int nNbQtyfound = 0;
		LineRead lastLineRead = dataFile.readNextUnixLine();
		for(; nNbRecordRead<10 && lastLineRead != null; nNbRecordRead++)
		{
			boolean b = false;
			int nLength = lastLineRead.getBodyLength();
			for(int n=0; n<nNbQtyfound; n++)
			{
				if(tnRecordLengthFound[n] == nLength)
				{
					tnRecordLengthQty[n]++;
					b = true;
					break;
				}
			}
			if(!b)
			{
				tnRecordLengthFound[nNbQtyfound] = nLength;
				tnRecordLengthQty[nNbQtyfound] = 1;
				nNbQtyfound++;
			}

			lastLineRead = dataFile.readNextUnixLine();
		}
		
		int nMaxQty = -1;
		int nLengthFound = -1;
		for(int n=0; n<nNbQtyfound; n++)
		{
			if(tnRecordLengthQty[n] > nMaxQty)
			{
				nMaxQty = tnRecordLengthQty[n];
				nLengthFound = tnRecordLengthFound[n];
			}
		}
		dataFile.setEOF(false);
		
		if(nLengthFound != -1)
		{
			recordLengthInfoDefinitionType = RecordLengthInfoDefinitionType.AutoDetermination;
			bVariableLength = false;  
			bVariableLength4BytesLF = false;
			recordLengthDefinition = new RecordLengthDefinition(nLengthFound);	// Length found included trailing LF
		}
		if (nNbQtyfound == 1)
			return true;
		else
			return false;
	}

}
