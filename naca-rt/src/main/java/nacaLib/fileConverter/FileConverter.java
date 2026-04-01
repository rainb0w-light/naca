/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fileConverter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jlib.log.Log;
import jlib.misc.AsciiEbcdicConverter;
import jlib.misc.DataFileLineReader;
import jlib.misc.DataFileWrite;
import jlib.misc.FileEndOfLine;
import jlib.misc.FileSystem;
import jlib.misc.LineRead;
import jlib.misc.LittleEndingSignBinaryBufferStorage;
import jlib.misc.LittleEndingUnsignBinaryBufferStorage;
import jlib.misc.LogicalFileDescriptor;
import nacaLib.varEx.FileDescriptor;

public class FileConverter
{
	private static final byte   AFP_ASCII_5A  			=   (byte)0x5D; // 5A
	private static final byte[] AFP_ASCII_SFI 			= { (byte)0x4C, (byte)0xD3, (byte)0xBA }; // D3EE9B
	private static final byte[] AFP_ASCII_PAGEFORMAT 	= { (byte)0x4C, (byte)0xBF, (byte)0xAD }; // D3ABCA
	private static final byte[] AFP_ASCII_COPYGROUP 	= { (byte)0x4C, (byte)0xBF, (byte)0xF6 }; // D3ABCC
	private static final byte[] AFP_ASCII_SEGMENT 		= { (byte)0x4C, (byte)0xAE, (byte)0x5E }; // D3AF5F	
	
	private static final byte   AFP_EBCDIC_5A  			=   (byte)0x5A;
//	private static final byte[] AFP_EBCDIC_SFI 			= { (byte)0xD3, (byte)0xEE, (byte)0x9B };
//	private static final byte[] AFP_EBCDIC_BOC 			= { (byte)0xD3, (byte)0xA8, (byte)0x92 };
//	private static final byte[] AFP_EBCDIC_OCD 			= { (byte)0xD3, (byte)0xEE, (byte)0x92 };
//	private static final byte[] AFP_EBCDIC_EOC 			= { (byte)0xD3, (byte)0xA9, (byte)0x92 };
//	private static final byte[] AFP_EBCDIC_IOB 			= { (byte)0xD3, (byte)0xAF, (byte)0xC3 };
	
	private FileDescriptor file = null;
	private boolean islist = false;
	private boolean issuppressVariableLength = true;
	private boolean isaddVariableLength = false;
	private boolean iskeepLineFeed = false;
	private String csLineFeedReplace = "";
	private int nLengthRecord = 0;
	private String csPaddingHex = "";
	private byte bytePadding = 0;
	private boolean isconvertInEbcdic = false;
	private boolean isconvertInEbcdicAFP = false;
	private boolean isconvertInEbcdicAFPInfoPrint = false;
	private boolean isconvertInAscii = false;
	private boolean iskeepOutputFile = false;
	private boolean isappendEOF = false;
	private int nLine = 0;
	
	private byte[] tbyHeader2 = new byte[2];
	private byte[] tbyHeader4 = new byte[4];
	
	DataFileWrite fileOutput;

	public FileConverter(FileDescriptor file)
	{
		file = file;
	}
		
	public boolean execute(String csParameter)
	{
		if (csParameter != null && !csParameter.equals(""))
		{
			String csParameterUpper = csParameter.toUpperCase();
			if (csParameterUpper.indexOf("LIST") != -1)
			{
				islist = true;
			}
			if (csParameterUpper.indexOf("KEEPVARIABLELENGTH") != -1)
			{
				issuppressVariableLength = false;
			}
			if (csParameterUpper.indexOf("ADDVARIABLELENGTH") != -1)
			{
				issuppressVariableLength = false;
				isaddVariableLength = true;
				iskeepLineFeed = true;
				csLineFeedReplace = "\n";
			}
			if (csParameterUpper.indexOf("REPLACELINEFEED={") != -1)
			{
				iskeepLineFeed = false;
				int nPos = csParameterUpper.indexOf("REPLACELINEFEED={") + 17;
				int nPosEnd = csParameterUpper.indexOf("}", nPos);
				csLineFeedReplace = csParameter.substring(nPos, nPosEnd);
			}
			else if (csParameterUpper.indexOf("KEEPLINEFEED") != -1)
			{
				iskeepLineFeed = true;
				csLineFeedReplace = "\n";
			}
			if (csParameterUpper.indexOf("RECORDLENGTH={") != -1)
			{
				int nPos = csParameterUpper.indexOf("RECORDLENGTH={") + 14;
				int nPosEnd = csParameterUpper.indexOf("}", nPos);
				nLengthRecord = new Integer(csParameter.substring(nPos, nPosEnd)).intValue();
				if (csParameterUpper.indexOf("PADDINGHEX={") != -1)
				{
					nPos = csParameterUpper.indexOf("PADDINGHEX={") + 12;
					nPosEnd = csParameterUpper.indexOf("}", nPos);
					csPaddingHex = csParameter.substring(nPos, nPosEnd);
					String csDigit = "0x" + csPaddingHex.charAt(0) + csPaddingHex.charAt(1);
					int nVal = Integer.decode(csDigit).intValue();
					bytePadding = (byte)nVal;
				}
			}
			if (csParameterUpper.indexOf("CONVERTINEBCDICAFPINFOPRINT") != -1)
			{
				isconvertInEbcdicAFPInfoPrint = true;
			}
			else if (csParameterUpper.indexOf("CONVERTINEBCDICAFP") != -1)
			{
				isconvertInEbcdicAFP = true;
			}
			else if (csParameterUpper.indexOf("CONVERTINEBCDIC") != -1)
			{
				isconvertInEbcdic = true;
			}
			else if (csParameterUpper.indexOf("CONVERTINASCII") != -1)
			{
				isconvertInAscii = true;
			}
			if (csParameterUpper.indexOf("KEEPOUTPUTFILE") != -1)
			{
				iskeepOutputFile = true;
			}
			if (csParameterUpper.indexOf("APPENDEOF") != -1)
			{
				isappendEOF = true;
			}
		}
		
		if (isaddVariableLength)
			System.out.println("FileConverter: Add variable length");
		else
			if (issuppressVariableLength)
				System.out.println("FileConverter: Suppress variable length");
			else
				System.out.println("FileConverter: Keep variable length");
		
		if (!iskeepLineFeed)
			System.out.println("FileConverter: Replace line feed by : \"" + csLineFeedReplace + "\"");		
		if (nLengthRecord != 0)
		{	
			System.out.println("FileConverter: Length record : " + nLengthRecord);
			if (bytePadding == 0)
				System.out.println("FileConverter: Padding with low-value");
			else
				System.out.println("FileConverter: Padding with hex(" + csPaddingHex + ")");
		}
		if (isconvertInEbcdicAFP)
			System.out.println("FileConverter: Convert in ebcdic for AFP file");
		if (isconvertInEbcdicAFPInfoPrint)
			System.out.println("FileConverter: Convert in ebcdic for AFP file InfoPrint Manager");
		if (isappendEOF)
			System.out.println("FileConverter: Add character End Of File");
		
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
					convert(csFile);
				}
				dataFileList.close();
			}
		}
		else
		{
			String csFile = file.getPhysicalName();
			convert(csFile);
		}

		return true;
	}
	
	private boolean convert(String csFile)
	{
		if (isappendEOF)
		{
			try
			{
				File fileOut = new File(csFile);
				int length = (int)fileOut.length();
				if (length != 0)
				{
					byte[] tbyFileOut = FileSystem.getBytesFromFile(fileOut);
					if (tbyFileOut[length - 1] != FileEndOfLine.LF)
					{
						BufferedWriter out = new BufferedWriter(new FileWriter(fileOut, true));
						out.write(FileEndOfLine.LF);
				        out.close();
				        System.out.println("FileConverter: Character End Of File added");
					}
					else
					{
						System.out.println("FileConverter: Character End Of File already exists");
					}
				}
			}
			catch (IOException e)
			{
			}
		}
		DataFileLineReader dataFileIn = new DataFileLineReader(csFile, 65536, 0);
		LogicalFileDescriptor logicalFileDescriptor = new LogicalFileDescriptor("", csFile);
		if(logicalFileDescriptor != null)
		{
			boolean isinOpened = dataFileIn.open(logicalFileDescriptor);
			if(isinOpened)
			{
				if (isaddVariableLength)
				{
					fileOutputOpen(csFile);
					LineRead lineRead = dataFileIn.readNextUnixLine();
					if (lineRead != null)
					{
						if (lineRead.getAsLittleEndingUnsignBinaryInt() == lineRead.getBodyLength() - 4)
						{
							dataFileIn.close();
							System.out.println("FileConverter: File " + csFile + " already converted");
							return false;
						}
						if (nLengthRecord != 0)
							LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader4, nLengthRecord, 0);
						
						while (lineRead != null)
						{
							if (nLengthRecord == 0)
								LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader4, lineRead.getBodyLength(), 0);
							fileOutput.write(tbyHeader4);
							convertNext(dataFileIn, lineRead);
							lineRead = dataFileIn.readNextUnixLine();
						}
					}
					dataFileIn.close();
					fileOutputClose(csFile);
					return true;
				}
				else
				{
					if (!logicalFileDescriptor.isLengthInfoDefined())
					{
						logicalFileDescriptor.tryAutoDetermineRecordLength(dataFileIn);
					}
					fileOutputOpen(csFile);
					if (logicalFileDescriptor.isVariableLength())
					{
						byte[] tbyHeader = new byte[4];
						if (nLengthRecord != 0)
							LittleEndingSignBinaryBufferStorage.writeInt(tbyHeader, nLengthRecord, 0);
						LineRead lineHeader = dataFileIn.readBuffer(4, false);
						while (lineHeader != null)
						{
							int nLengthExcludingHeader = lineHeader.getAsLittleEndingUnsignBinaryInt();							
							if (!issuppressVariableLength)
							{
								if (nLengthRecord == 0)
									fileOutput.write(lineHeader.getBuffer(), 0, lineHeader.getBodyLength());
								else
									fileOutput.write(tbyHeader);
							}
							LineRead lineRead = dataFileIn.readBuffer(nLengthExcludingHeader, true);
							convertNext(dataFileIn, lineRead);
							lineHeader = dataFileIn.readBuffer(4, false);
						}
					}
					else
					{
						if (logicalFileDescriptor.getRecordLengthDefinition() != null)
						{	
							int length = logicalFileDescriptor.getRecordLengthDefinition().getRecordLength();
							LineRead lineRead = dataFileIn.readBuffer(length, true);
							while (lineRead != null)
							{
								convertNext(dataFileIn, lineRead);
								lineRead = dataFileIn.readBuffer(length, true);
							}
						}
					}
					dataFileIn.close();
					fileOutputClose(csFile);
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void convertNext(DataFileLineReader dataFileIn, LineRead lineRead)
	{
		nLine++;
		byte[] arrByteValue = lineRead.getBufferCopy();
		int nLengthLine = lineRead.getBodyLength();

		if (isconvertInEbcdicAFP || isconvertInEbcdicAFPInfoPrint)
		{
			if (nLengthLine > 6 && arrByteValue[0] == AFP_ASCII_5A)
			{
				if (isSpecialAfp(arrByteValue, AFP_ASCII_COPYGROUP) || isSpecialAfp(arrByteValue, AFP_ASCII_PAGEFORMAT))
				{
					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 0, 1);
					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 3, 3);					
					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 9, 8);
					if (nLengthLine > 17)
					{
						if (isconvertInEbcdicAFP)
						{
							if (nLengthLine > 19)
							{
								arrByteValue[17] = arrByteValue[nLengthLine - 2];
								arrByteValue[18] = arrByteValue[nLengthLine - 1];
								nLengthLine = 19;
							}	
							AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 17, nLengthLine - 17);
						}
						else
						{
							nLengthLine = 17;
						}
					}
					int nLengthAFP = LittleEndingUnsignBinaryBufferStorage.readShort(arrByteValue, 1);
					if (nLengthAFP != 16)
					{
						LittleEndingUnsignBinaryBufferStorage.writeUnsignedShort(arrByteValue, 16, 1);
						Log.logDebug("FileConverter: Change the record x'5a' length copygroup/pageformat at line " + nLine + " for document " + dataFileIn.getName());
					}
				}
				else if (isSpecialAfp(arrByteValue, AFP_ASCII_SEGMENT))
				{
					
					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 0, 1);
					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 3, 3);
					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 9, 8);
					if (nLengthLine > 23)
					{
						if (isconvertInEbcdicAFP)
						{
							if (nLengthLine > 25)
							{
								arrByteValue[23] = arrByteValue[nLengthLine - 2];
								arrByteValue[24] = arrByteValue[nLengthLine - 1];
								nLengthLine = 25;
							}
							AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 23, nLengthLine - 23);
						}
						else
						{
							nLengthLine = 23;
						}
					}
					int nLengthAFPSegment = LittleEndingUnsignBinaryBufferStorage.readShort(arrByteValue, 1);
					if (nLengthAFPSegment != 22)
					{
						LittleEndingUnsignBinaryBufferStorage.writeUnsignedShort(arrByteValue, 22, 1);
						Log.logDebug("FileConverter: Change the record x'5a' length segment at 22 in line " + nLine + " for document " + dataFileIn.getName());
					}
				}
				else if (isSpecialAfp(arrByteValue, AFP_ASCII_SFI))
				{
					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 0, 1);
					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 3, 3);
					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 9, nLengthLine - 9);
				}
				else
				{
					Log.logCritical("FileConverter: No transformation ebcdic for the record x'5a' in line " + nLine + " for document " + dataFileIn.getName());
				}
			}
			else if (nLengthLine > 6 && arrByteValue[0] == AFP_EBCDIC_5A)
			{
//				if (isSpecialAfp(arrByteValue, AFP_EBCDIC_SFI) ||
//					isSpecialAfp(arrByteValue, AFP_EBCDIC_BOC) ||
//					isSpecialAfp(arrByteValue, AFP_EBCDIC_OCD) ||
//					isSpecialAfp(arrByteValue, AFP_EBCDIC_EOC) ||
//					isSpecialAfp(arrByteValue, AFP_EBCDIC_IOB))
//				{
					// Pas convertir le record Formattage texte des routines PSF --> d�j� en ebcdic
					if (isconvertInEbcdicAFP)
					{
						// Convertir les 2 derniers bytes pour a2p
						AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, nLengthLine - 2, 2);
					}
//				}
//				else
//				{
//					AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 0, nLengthLine);
//				}
			}
			else if (nLengthLine > 4 && arrByteValue[0] == '#' &&
								    arrByteValue[1] == '3' &&
								    arrByteValue[2] == '0' &&
								    arrByteValue[3] == '0' &&
								    arrByteValue[4] == '#')
			{
				// Pas convertir le record #300# pour InfoPrint
			}
			else
			{
				AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 0, nLengthLine);
			}
		}
		else if (isconvertInEbcdic)
		{
			AsciiEbcdicConverter.swapByteAsciiToEbcdic(arrByteValue, 0, nLengthLine);
		}
		else if (isconvertInAscii)
		{
			AsciiEbcdicConverter.swapByteEbcdicToAscii(arrByteValue, 0, nLengthLine);
		}
		
		if (isconvertInEbcdicAFPInfoPrint)
		{
			// Ajouter la longueur sur 2 bytes du record
			LittleEndingSignBinaryBufferStorage.writeShort(tbyHeader2, (short)nLengthLine, 0);
			fileOutput.write(tbyHeader2);
		}

		if (nLengthRecord == 0)
		{	
			fileOutput.write(arrByteValue, 0, nLengthLine);
		}	
		else
		{
			if (nLengthLine >= nLengthRecord)
			{
				fileOutput.write(arrByteValue, 0, nLengthRecord);
			}
			else
			{
				fileOutput.write(arrByteValue, 0, nLengthLine);
				byte[] tbyFill = new byte[nLengthRecord - nLengthLine];
				for (int i=0 ; i < tbyFill.length; i++)
					tbyFill[i] = bytePadding;
				fileOutput.write(tbyFill);
			}
		}

		if (!csLineFeedReplace.equals(""))
		{
			fileOutput.write(csLineFeedReplace.getBytes(), 0, csLineFeedReplace.length());
		}
	}
	
	private boolean isSpecialAfp(byte[] arrByteValue, byte[] arrToCheck)
	{
		if (arrByteValue[3] == arrToCheck[0] && arrByteValue[4] == arrToCheck[1] && arrByteValue[5] == arrToCheck[2])
			return true;
		else
			return false;
	}

	private void fileOutputOpen(String csFile)
	{
		fileOutput = new DataFileWrite(csFile + ".conv", false);
		fileOutput.open();
	}
	
	private void fileOutputClose(String csFile)
	{
		fileOutput.close();
		if (iskeepOutputFile)
		{
			System.out.println("FileConverter: File " + csFile + " converted in file " + csFile + ".conv");
		}
		else
		{	
			FileSystem.moveOrCopy(csFile + ".conv", csFile);
			System.out.println("FileConverter: File " + csFile + " converted");
		}
	}
}