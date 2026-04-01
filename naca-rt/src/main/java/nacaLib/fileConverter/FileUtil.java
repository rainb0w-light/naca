/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fileConverter;

import java.nio.ByteBuffer;

import jlib.misc.DataFileLineReader;
import jlib.misc.DataFileWrite;
import jlib.misc.FileSystem;
import jlib.misc.LineRead;
import jlib.misc.LittleEndingSignBinaryBufferStorage;
import jlib.misc.LogicalFileDescriptor;
import nacaLib.varEx.FileDescriptor;

public class FileUtil
{
	private FileDescriptor file = null;
	
	private int nSequencer = 0;
	private int nCommandNext = -1;
	
	private boolean iscount = false;
	private boolean isreplace = false;
	private boolean isdelete = false;
	private boolean isextract = false;
	
	private boolean islist = false;
	
	private int nLineBegin = 0;
	private int nLineEnd = 0;
	private int nLast = 0;
	private int nColBegin = 0;
	private int nColEnd = 0;
	private String csValue = null;
	private String csValueHex = null;
	private String csValueNew = null;
	private String csValueHexNew = null;
	private ByteBuffer[] byteValue;
	private byte[] byteValueNew;
	
	private boolean iskeepOutputFile = false;
	private boolean isdebug = false;
	
	private int nLine = 0;
	private int nLineCount = 0;
	
	DataFileWrite fileOutput;

	public FileUtil(FileDescriptor file)
	{
		file = file;
	}
		
	public boolean execute(String csParameter)
	{
		String csParameterUpper = csParameter.toUpperCase();
		if (csParameterUpper.indexOf("LIST") != -1)
		{
			islist = true;
		}		
		if (csParameterUpper.indexOf("KEEPOUTPUTFILE") != -1)
		{
			iskeepOutputFile = true;
		}		
		if (csParameterUpper.indexOf("DEBUG") != -1)
		{
			isdebug = true;
		}

		int nCount = csParameterUpper.indexOf("COUNT");
		int nReplace = csParameterUpper.indexOf("REPLACE");
		int nDelete = csParameterUpper.indexOf("DELETE");
		int nExtract = csParameterUpper.indexOf("EXTRACT");
		
		if (nReplace == -1 && nDelete == -1 && nExtract == - 1)
		{
			if (nCount == -1)
			{
				System.out.println("FileUtil: No commands found");
				return false;
			}
		}
		else if (nCount != -1)
		{
			System.out.println("FileUtil: Command count and others not compatible");
			return false;
		}
		
		int nPosStart = -1;
		while (true)
		{
			iscount = false;
			isreplace = false;
			isdelete = false;
			isextract = false;
			
			nLineBegin = 0;
			nLineEnd = 0;
			nLast = 0;
			nColBegin = 0;
			nColEnd = 0;
			csValue = null;
			csValueHex = null;
			csValueNew = null;
			csValueHexNew = null;
			byteValue = null;
			byteValueNew = null;
			
			if (nCount != -1 && (nReplace == -1 || nCount < nReplace) && (nDelete == -1 || nCount < nDelete) && (nExtract == -1 || nCount < nExtract))
			{
				iscount = true;
				nPosStart = nCount + 1;
			}
			else if (nReplace != -1 && (nCount == -1 || nReplace < nCount) && (nDelete == -1 || nReplace < nDelete) && (nExtract == -1 || nReplace < nExtract))
			{
				isreplace = true;
				nPosStart = nReplace + 1;
			}
			else if (nDelete != -1 && (nCount == -1 || nDelete < nCount) && (nReplace == -1 || nDelete < nReplace) && (nExtract == -1 || nDelete < nExtract))
			{
				isdelete = true;
				nPosStart = nDelete + 1;
			}
			else
			{
				isextract = true;
				nPosStart = nExtract + 1;
			}
			
			nReplace = csParameterUpper.indexOf("REPLACE", nPosStart);
			nDelete = csParameterUpper.indexOf("DELETE", nPosStart);
			nExtract = csParameterUpper.indexOf("EXTRACT", nPosStart);
			
			if (nReplace != -1 && (nDelete == -1 || nReplace < nDelete) && (nExtract == -1 || nReplace < nExtract))
				nCommandNext = nReplace;
			else if (nDelete != -1 && (nReplace == -1 || nDelete < nReplace) && (nExtract == -1 || nDelete < nExtract))
				nCommandNext = nDelete;
			else if (nExtract != -1 && (nReplace == -1 || nExtract < nReplace) && (nDelete == -1 || nExtract < nDelete))
				nCommandNext = nExtract;
			else
				nCommandNext = -1;
			
			String csParameterPart = csParameter;
			if (nCommandNext == -1)
				csParameterPart = csParameterPart.substring(nPosStart);
			else
				csParameterPart = csParameterPart.substring(nPosStart, nCommandNext);
			String csParameterPartUpper = csParameterPart.toUpperCase();
			
			if (csParameterPartUpper.indexOf("VALUE=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("VALUE=") + 6;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				if (nPosEnd == -1)
					csValue = csParameterPart.substring(nPos);
				else
					csValue = csParameterPart.substring(nPos, nPosEnd);
				String values[] = csValue.split("#or#");
				byteValue = new ByteBuffer[values.length];
				for (int i = 0; i < values.length; i++)
				{
					byteValue[i] = ByteBuffer.wrap(values[i].getBytes());
				}	
			}
			if (csParameterPartUpper.indexOf("VALUEHEX=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("VALUEHEX=") + 9;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				if (nPosEnd == -1)
					csValueHex = csParameterPart.substring(nPos);
				else
					csValueHex = csParameterPart.substring(nPos, nPosEnd);
				String values[] = csValueHex.split("#or#");
				byteValue = new ByteBuffer[values.length];
				for (int i = 0; i < values.length; i++)
				{
					byteValue[i] = ByteBuffer.wrap(hexToBytes(values[i]));
				}
			}
			if (csParameterPartUpper.indexOf("VALUENEW=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("VALUENEW=") + 9;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				if (nPosEnd == -1)
					csValueNew = csParameterPart.substring(nPos);
				else
					csValueNew = csParameterPart.substring(nPos, nPosEnd);
				byteValueNew = csValueNew.getBytes();
			}
			if (csParameterPartUpper.indexOf("VALUEHEXNEW=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("VALUEHEXNEW=") + 12;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				if (nPosEnd == -1)
					csValueHexNew = csParameterPart.substring(nPos);
				else
					csValueHexNew = csParameterPart.substring(nPos, nPosEnd);
				byteValueNew = hexToBytes(csValueHexNew);
			}
			
			if (csParameterPartUpper.indexOf("LINE=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("LINE=") + 5;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				if (nPosEnd == -1)
					nLineBegin = Integer.valueOf(csParameterPart.substring(nPos)).intValue();
				else
					nLineBegin = Integer.valueOf(csParameterPart.substring(nPos, nPosEnd)).intValue();
			}		
			if (csParameterPartUpper.indexOf("LINEEND=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("LINEEND=") + 8;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				if (nPosEnd == -1)
					nLineEnd = Integer.valueOf(csParameterPart.substring(nPos)).intValue();
				else
					nLineEnd = Integer.valueOf(csParameterPart.substring(nPos, nPosEnd)).intValue();
			}

			if (csParameterPartUpper.indexOf("FIRST=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("FIRST=") + 6;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				int nFirst = 0;
				if (nPosEnd == -1)
					nFirst = Integer.valueOf(csParameterPart.substring(nPos)).intValue();
				else
					nFirst = Integer.valueOf(csParameterPart.substring(nPos, nPosEnd)).intValue();
				nLineBegin = 0;
				nLineEnd = nFirst - 1;
			}
			if (csParameterPartUpper.indexOf("LAST=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("LAST=") + 5;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				if (nPosEnd == -1)
					nLast = Integer.valueOf(csParameterPart.substring(nPos)).intValue();
				else
					nLast = Integer.valueOf(csParameterPart.substring(nPos, nPosEnd)).intValue();
			}

			if (csParameterPartUpper.indexOf("COL=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("COL=") + 4;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				if (nPosEnd == -1)
					nColBegin = Integer.valueOf(csParameterPart.substring(nPos)).intValue();
				else
					nColBegin = Integer.valueOf(csParameterPart.substring(nPos, nPosEnd)).intValue();
			}			
			if (csParameterPartUpper.indexOf("COLEND=") != -1)
			{
				int nPos = csParameterPartUpper.indexOf("COLEND=") + 7;
				int nPosEnd = csParameterPartUpper.indexOf(",", nPos);
				if (nPosEnd == -1)
					nColEnd = Integer.valueOf(csParameterPart.substring(nPos)).intValue();
				else
					nColEnd = Integer.valueOf(csParameterPart.substring(nPos, nPosEnd)).intValue();
			}
			
			if (isreplace)
			{
				if (csValue != null || csValueNew != null)
				{
					if (csValue == null)
					{
						System.out.println("FileUtil: Replace all chars by \"" + csValueNew + "\"");
					}
					else
					{
						System.out.println("FileUtil: Replace \"" + csValue + "\" by \"" + csValueNew + "\"");
					}
				}
				else if (csValueHex != null || csValueHexNew != null)
				{
					if (csValueHex == null)
					{
						System.out.println("FileUtil: Replace all chars by hex \"" + csValueHexNew + "\"");
					}
					else
					{
						System.out.println("FileUtil: Replace hex \"" + csValueHex + "\" by \"" + csValueHexNew + "\"");
					}
				}
				else
				{
					// error
				}
			}
			else if (isdelete)
			{
				if (csValue != null)
					System.out.println("FileUtil: Delete when record contains \"" + csValue + "\"");
				else if (csValueHex != null)
					System.out.println("FileUtil: Delete when record contains hex \"" + csValueHex + "\"");
				else
					System.out.println("FileUtil: Delete");
			}
			else if (isextract)
			{
				if (csValue != null)
					System.out.println("FileUtil: Extract when record contains \"" + csValue + "\"");
				else if (csValueHex != null)
					System.out.println("FileUtil: Extract when record contains hex \"" + csValueHex + "\"");
				else
					System.out.println("FileUtil: Extract");
			}
			else if (iscount)
			{
				System.out.println("FileUtil: Count");
			}
			else
			{
				System.out.println("FileUtil: No treatment");
				return false;
			}

			if (nLineBegin != 0 || nLineEnd != 0)
			{
				if (nLineBegin == 0)
					System.out.println("FileUtil: From begin of file to line " + nLineEnd);
				else if (nLineEnd == 0)
					System.out.println("FileUtil: From line " + nLineBegin + " to end of file");
				else
					System.out.println("FileUtil: From line " + nLineBegin + " to line " + nLineEnd);
			}
			if (nColBegin != 0 || nColEnd != 0)
			{
				if (nColBegin == 0)
					System.out.println("FileUtil: From begin of record to column " + nColEnd);
				else if (nColEnd == 0)
					System.out.println("FileUtil: From column " + nColBegin + " to end of record");
				else
					System.out.println("FileUtil: From column " + nColBegin + " to column " + nColEnd);
			}
			
			try
			{
				if (islist)
				{
					String csFileList = file.getPhysicalName();
					DataFileLineReader dataFileList = new DataFileLineReader(csFileList, 65536, 0);
					boolean isconvOpened = dataFileList.open();
					if(isconvOpened)
					{
						LineRead lineRead;
						while((lineRead = file.readALine(dataFileList, null)) != null)
						{
							String csFile = lineRead.getChunkAsString().trim();
							util(csFile);
						}
						dataFileList.close();
					}
				}
				else
				{
					String csFile = file.getPhysicalName();
					util(csFile);
				}
			}
			catch(Exception ex)
			{
				System.out.println("FileUtil: Error in line " + nLine);
				throw new RuntimeException(ex);
			}
			
			if (nCommandNext == -1)
				break;
			
			nSequencer++;
		}
		
		if (islist)
		{
			String csFileList = file.getPhysicalName();
			DataFileLineReader dataFileList = new DataFileLineReader(csFileList, 65536, 0);
			boolean isconvOpened = dataFileList.open();
			if(isconvOpened)
			{
				LineRead lineRead;
				while((lineRead = file.readALine(dataFileList, null)) != null)
				{
					String csFile = lineRead.getChunkAsString().trim();
					fileEnd(csFile);
				}
				dataFileList.close();
			}
		}
		else
		{
			String csFile = file.getPhysicalName();
			fileEnd(csFile);
		}

		return true;
	}

	private boolean util(String csFile)
	{
		nLine = 0;
		String csFileIn = csFile;
		if (nSequencer != 0)
			csFileIn += ".util." + (nSequencer - 1);
		DataFileLineReader dataFileIn = new DataFileLineReader(csFileIn, 65536, 0);
		LogicalFileDescriptor logicalFileDescriptor = new LogicalFileDescriptor("", csFileIn);
		if(logicalFileDescriptor != null)
		{
			boolean isinOpened = dataFileIn.open(logicalFileDescriptor);
			if(isinOpened)
			{	
				if (!logicalFileDescriptor.isLengthInfoDefined())
				{
					logicalFileDescriptor.tryAutoDetermineRecordLength(dataFileIn);
				}
				if (!iscount)
					fileOutputOpen(csFile);
				if (logicalFileDescriptor.isVariableLength())
				{
					if (nLast != 0)
					{
						LineRead lineHeader = dataFileIn.readBuffer(4, false);
						while (lineHeader != null)
						{
							nLine++;
							int nLengthExcludingHeader = lineHeader.getAsLittleEndingUnsignBinaryInt();
							LineRead lineRead = dataFileIn.readBuffer(nLengthExcludingHeader, true);
							lineHeader = dataFileIn.readBuffer(4, false);
						}
						nLineBegin = nLine - nLast + 1;
						if (nLineBegin < 0)
							nLineBegin = 0;
						nLineEnd = nLine;
						nLine = 0;
						dataFileIn.close();
						dataFileIn.open(logicalFileDescriptor);
					}
					byte[] tbyHeader = new byte[4];
					LineRead lineHeader = dataFileIn.readBuffer(4, false);
					while (lineHeader != null)
					{
						int nLengthExcludingHeader = lineHeader.getAsLittleEndingUnsignBinaryInt();
						LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader, nLengthExcludingHeader, 0);
						LineRead lineRead = dataFileIn.readBuffer(nLengthExcludingHeader, true);
						if (utilNext(dataFileIn, lineRead, tbyHeader))
							break;
						lineHeader = dataFileIn.readBuffer(4, false);
					}
				}
				else
				{
					if (logicalFileDescriptor.getRecordLengthDefinition() != null)
					{	
						int length = logicalFileDescriptor.getRecordLengthDefinition().getRecordLength();
						if (nLast != 0)
						{
							LineRead lineRead = dataFileIn.readBuffer(length, true);
							while (lineRead != null)
							{
								nLine++;
								lineRead = dataFileIn.readBuffer(length, true);
							}
							nLineBegin = nLine - nLast + 1;
							if (nLineBegin < 0)
								nLineBegin = 0;
							nLineEnd = nLine;
							nLine = 0;
							dataFileIn.close();
							dataFileIn.open(logicalFileDescriptor);
						}
						
						LineRead lineRead = dataFileIn.readBuffer(length, true);
						while (lineRead != null)
						{
							if (utilNext(dataFileIn, lineRead, null))
								break;
							lineRead = dataFileIn.readBuffer(length, true);
						}
					}
				}
				dataFileIn.close();
				if (iscount)
					System.out.println("FileUtil: Number of lines " + nLineCount);
				else
					fileOutputClose(csFile);
				return true;
			}
		}
		
		return false;
	}

	private byte[] hexToBytes(String csHex)
	{
		byte[] byteValue = new byte[csHex.length()/2];
		
		for (int i=0, j=0; i < csHex.length(); j++)
		{
			String csDigit = "0x" + csHex.charAt(i++) + csHex.charAt(i++);
			int nVal = Integer.decode(csDigit).intValue();
			byteValue[j] = (byte)nVal;
		}
		
		return byteValue;
	}

	private boolean utilNext(DataFileLineReader dataFileIn, LineRead lineRead, byte[] tbyHeader)
	{
		boolean isstop = false;
		nLine++;
		byte[] byteData = lineRead.getBufferCopy();
		int nLengthLine = lineRead.getBodyLength();
		
		boolean iswrite = false;
		
		if (isreplace)
		{
			iswrite = true;
			if (nLine >= nLineBegin && (nLineEnd == 0 || nLine <= nLineEnd))
			{
				if (replaceValue(byteData, nLengthLine))
					if (isdebug)
						System.out.println("FileUtil: Line " + nLine + " replaced");
			}
		}
		else if (isdelete)
		{
			if (nLine >= nLineBegin && (nLineEnd == 0 || nLine <= nLineEnd))
			{
				if (byteValue != null)
				{	
					if (!existsValue(byteData, nLengthLine))
						iswrite = true;
				}
			}
			else
			{
				iswrite = true;
			}
			if (isdebug && !iswrite)
				System.out.println("FileUtil: Line " + nLine + " deleted");
		}
		else if (isextract)
		{
			if (nLine >= nLineBegin && (nLineEnd == 0 || nLine <= nLineEnd))
			{
				if (byteValue == null)
					iswrite = true;
				else
					if (existsValue(byteData, nLengthLine))
						iswrite = true;
			}
			else
			{
				if (nLine >= nLineBegin)
					isstop = true;
			}
			if (isdebug && iswrite)
				System.out.println("FileUtil: Line " + nLine + " extracted");
		}
		else if (iscount)
		{
			if (nLine >= nLineBegin && (nLineEnd == 0 || nLine <= nLineEnd))
			{
				if (byteValue == null)
					iswrite = true;
				else
					if (existsValue(byteData, nLengthLine))
						iswrite = true;
			}
			else
			{
				if (nLine >= nLineBegin)
					isstop = true;
			}
			if (iswrite)
			{
				nLineCount++;
				iswrite = false;
				if (isdebug)
					System.out.println("FileUtil: Line " + nLine + " counted");
			}	
		}
		
		if (iswrite)
		{
			if (tbyHeader != null)				
				fileOutput.write(tbyHeader);
			fileOutput.write(byteData, 0, nLengthLine);
			fileOutput.writeEndOfRecordMarker();
		}
		return isstop;
	}

	private boolean replaceValue(byte[] arrByteData, int nLengthLine)
	{
		boolean isreplaced = false;
		int nBegin = 0;
		if (nColBegin != 0)
			nBegin = nColBegin - 1;
		int nEnd = nLengthLine;
		if (nColEnd != 0 && nColEnd < nEnd)
			nEnd = nColEnd;		
		for ( ;nBegin < nEnd; )
		{
			boolean isequal = checkValue(arrByteData, nBegin);
			if (isequal)
			{
				isreplaced = true;
				for (int i = 0; i < byteValueNew.length; i++)
				{
					arrByteData[nBegin + i] = byteValueNew[i];
				}
				nBegin += byteValueNew.length;
			}
			else
			{
				nBegin++;
			}
		}
		return isreplaced;
	}
	
	private boolean existsValue(byte[] arrByteData, int nLengthLine)
	{
		int nBegin = 0;
		if (nColBegin != 0)
			nBegin = nColBegin - 1;
		int nEnd = nLengthLine;
		if (nColEnd != 0 && nColEnd < nEnd)
			nEnd = nColEnd;		
		for ( ;nBegin < nEnd; nBegin++)
		{
			boolean isequal = checkValue(arrByteData, nBegin);
			if (isequal)
				return true;			
		}
		return false;
	}
	
	private boolean checkValue(byte[] arrByteData, int nBegin)
	{
		if (byteValue == null) return true;
		
		for (int i = 0; i < byteValue.length; i++)
		{
			boolean isequal = true;
			byte[] bytes = byteValue[i].array();
			if (nBegin + bytes.length > arrByteData.length)
			{
				isequal = false;
			}
			else
			{
				for (int j=0; j < bytes.length; j++)
				{
					if (arrByteData[nBegin + j] != bytes[j])
					{
						isequal = false;
						break;
					}
				}
			}
			if (isequal)
				return true;
		}		
		return false;
	}

	private void fileOutputOpen(String csFile)
	{
		if (nCommandNext == -1)
			fileOutput = new DataFileWrite(csFile + ".util", false);
		else	
			fileOutput = new DataFileWrite(csFile + ".util." + nSequencer, false);
		fileOutput.open();
	}
	
	private void fileOutputClose(String csFile)
	{
		fileOutput.close();
		if (nSequencer != 0)
		{
			csFile += ".util." + (nSequencer - 1);
			FileSystem.delete(csFile);
		}
	}
	
	private void fileEnd(String csFile)
	{
		if (iskeepOutputFile)
		{	
			System.out.println("FileUtil: File " + csFile + " treated in file " + csFile + ".util");
		}	
		else
		{
			FileSystem.moveOrCopy(csFile + ".util", csFile);
			System.out.println("FileUtil: File " + csFile + " treated");
		}
	}
}