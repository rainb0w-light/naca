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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DataFileRead extends BaseDataFileBuffered
{
	private BufferedInputStream in = null;
		
	public DataFileRead()
	{
	}
	
	public DataFileRead(String csName)
	{
		csName = csName;
	}
		
//	public boolean open(String csName)
//	{
//		setName(csName);
//		return open();
//	}

	private boolean doOpen()
	{
		try
		{
			in = new BufferedInputStream(new DataInputStream(new FileInputStream(getName())));
			return true;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} 
		return false;
	}
	
	public boolean open(LogicalFileDescriptor logicalFileDescriptor)
	{
		boolean bOpened = doOpen();
		if(bOpened && logicalFileDescriptor != null)
		{
			logicalFileDescriptor.readFileHeader(this);
		}
		return bOpened; 
	}

	public boolean  close()
	{
		try
		{
			if(in != null)
			{
				in.close();
				in = null;
				return true;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean flush()
	{
		return false;
	}

	public boolean isOpen()
	{
		if(in != null)
			return true;
		return false;
	}
	
	public String toString()
	{
		String cs = csName + " (";
		if(isOpen())
		{
			cs += "Open";
			if(in != null)
				cs += " Read";
		}
		else
		{
			cs += "Close";
		}
		cs += ")";
		return cs;
	}
	
	public void write(byte[] tBytes, int nOffset, int nLength)
	{
	}	
	
	public void writeRecord(String cs)
	{
	}

	public void write(byte[] tBytes)
	{
	}
	
	public void writeWithEOL(byte[] tBytes, int nSize)
	{
	}
	
	public void writeWithEOL(LineRead lineRead)
	{
	}
	
	public void writeEndOfRecordMarker()
	{
	}
	
	public boolean readEndOfLineMarker()
	{
		int nByte = 0;
		if(in != null)
		{
			try
			{
				nByte = in.read();
				if(nByte == -1)
				{
					setEOF(true);
					return false;
				}
				if(nByte == FileEndOfLine.LF)
				{
					setEOF(false);
					return true;	// Found EOL
				}
			}
			catch (IOException e)
			{
				setEOF(true);
				return false;
			}
		}
		setEOF(true);
		return false;
	}
		
	public byte[] read(int nSize)
	{
		if(in != null)
		{
			try
			{
				byte byteBuffer[] = getByteBuffer(nSize);
				int nNBytesRead = in.read(byteBuffer, 0, nSize);
				if(nNBytesRead == -1)
					setEOF(true);
				return byteBuffer;
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return null;
	}
	
	public int getUnixRecordLength()
	{
		int n = 0;
		byte[] tVal = new byte[1];
		if(in != null)
		{
			try
			{	
				while(tVal[0] != FileEndOfLine.LF)
				{
					int nNBytesRead = in.read(tVal, 0, 1);
					if(nNBytesRead == -1)
					{
						setEOF(true);
						return n;
					}
					n++;
				}
				return n;
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return n;
	}
	
	public int readUnixLine(byte tBytes[], int nMaxLineSize)
	{
		int n = 0;
		byte[] tVal = new byte[1];
		if(in != null)
		{
			try
			{	
				while(tVal[0] != FileEndOfLine.LF)
				{
					int nNBytesRead = in.read(tVal, 0, 1);
					if(nNBytesRead != -1)
						tBytes[n++] = tVal[0];
					else
					{
						setEOF(true);
						return n;
					}
				}
				return n;
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				setEOF(true);
				return -1;
			}			
		}
		return n;
	}
	
	public int readUnixLine(byte tBytes[], int nOffset, int nMaxLineSize)
	{
		int n = nOffset;
		byte[] tVal = new byte[1];
		if(in != null)
		{
			try
			{	
				while(tVal[0] != FileEndOfLine.LF)
				{
					int nNBytesRead = in.read(tVal, 0, 1);
					if(nNBytesRead != -1)
						tBytes[n++] = tVal[0];
					else
					{
						setEOF(true);
						return n;
					}
				}
				return n;
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return n;
	}
	
	public int readChunk(byte tBytes[], int nNbBytes)
	{
		int n = -1;
		if(in != null && !isEOF())
		{
			try
			{	
				int nNBytesRead = in.read(tBytes, 0, nNbBytes);
				if(nNBytesRead == -1)
					setEOF(true);
				return nNBytesRead;
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return n;
	}
	
	public int readChunk(byte tBytes[], int nOffset, int nNbBytes)
	{
		int n = -1;
		if(in != null && !isEOF())
		{
			try
			{	
				int nNBytesRead = in.read(tBytes, nOffset, nNbBytes);
				if(nNBytesRead == -1)
					setEOF(true);
				return nNBytesRead;
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		return n;
	}
	
	public byte[] readWholeFileAsArray()
	{
		if(in != null)
		{
			int nSize;
			try
			{
				nSize = in.available();
				return read(nSize);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
	}
	
	public LineRead readNextUnixLine()
	{
		// Should use a DataFileLineReader
		return null;
	}
	

	public LineRead readBuffer(int nLength, boolean bTryReadNextLF)
	{
		// Should use a DataFileLineReader
		return null;
	}
	
	public void rewrite(byte[] tBytes, int nOffset, int nLength)
	{
	}
	
	public void rewriteWithEOL(byte[] tbyDest, int nSize)
	{
	}
	
	public boolean isReadable()
	{
		return true;
	}
	
	public boolean isWritable()
	{
		return false;
	}
	
	public boolean isUpdateable()
	{
		return false;
	}
	
	public long getFileCurrentPosition()
	{
		return -1;
	}
	
	public boolean setFileCurrentPosition(long lCurrentPosition)
	{
		return false;
	}
	
	public boolean savePosition(int nMaxReadAheadSize)
	{
		if(in != null && in.markSupported())
		{
			in.mark(nMaxReadAheadSize);
			return true;
		}
		return false;
	}
	
	public boolean returnAtSavedPosition()
	{
		if(in != null && in.markSupported())
		{
			try
			{
				in.reset();
				return true;
			}
			catch (IOException e)
			{
			}
		}
		return false;		
	}
	
	public LineRead readVariableLengthLine(boolean bTryReadNextLF, boolean bHeaderIsInt, LineRead lineOut)	// Read a vairable length line (length is given in record header 4 bytes)
	{
		LineRead recordHeader = readBuffer(4, false);
		if(recordHeader != null)
		{
			int nLength = recordHeader.getAsLittleEndingUnsignBinaryInt();
			
			if(lineOut == null)
				lineOut = new LineRead();
			lineOut.resetAndGaranteeBufferStorage(4 + nLength, 4 + nLength);
			lineOut.append(recordHeader);
	
			LineRead recordBody = readBuffer(nLength, bTryReadNextLF);
			lineOut.append(recordBody);
			
			return lineOut;
		}
		return null;
	}
	

}
