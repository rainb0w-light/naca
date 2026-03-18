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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class DataFileReadWrite extends BaseDataFileBuffered
{
	private RandomAccessFile rw = null;
	private byte t1Byte[] = new byte[1];
	private LineRead lineRead = null;
	private final static int ms_nMaxRecordLength = 65536;
	private long lSavedPosition = -1;
	
	public DataFileReadWrite()
	{
	}
	
	public DataFileReadWrite(String csName)
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
			rw = new RandomAccessFile(getName(), "rw");
			lineRead = new LineRead();
			initLineRead();
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
	
	private void initLineRead()
	{
		lineRead.resetAndGaranteeBufferStorage(ms_nMaxRecordLength, ms_nMaxRecordLength);
	}

	public boolean close()
	{
		try
		{
			if(rw != null)
			{
				rw.close();
				rw = null;
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
		try
		{
			if(rw != null)
			{		
				rw.getFD().sync();
				return true;
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return false;
	}

	public boolean isOpen()
	{
		if(rw != null)
			return true;
		return false;
	}
	
	public String toString()
	{
		String cs = csName + " (";
		if(isOpen())
		{
			cs += "Open RW";
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
		if(tBytes != null)
		{
			if(rw != null)
			{
				try
				{
					rw.write(tBytes, nOffset, nLength);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
	}	
	
	public void writeRecord(String cs)
	{
		int nLg = cs.length();
		if(rw != null)
		{
			try
			{
				rw.write(cs.getBytes(), 0, nLg);
				rw.write((char)FileEndOfLine.LF);
			}
			catch (IOException e)
			{	
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	public void write(byte[] tBytes)
	{
		if(tBytes != null)
		{
			if(rw != null)
			{
				try
				{
					rw.write(tBytes, 0, tBytes.length);
				}
				catch (IOException e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	public void writeWithEOL(byte[] tBytes, int nSize)
	{
		if(tBytes != null)
		{
			if(rw != null)
			{
				try
				{
					if(nSize+1 < tBytes.length)
					{
						tBytes[nSize] = FileEndOfLine.LF;
						rw.write(tBytes, 0, nSize+1);
					}
					else
					{
						rw.write(tBytes, 0, nSize);
						rw.write(FileEndOfLine.LF);
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
			}
		}
	}
	
	public void writeWithEOL(LineRead lineRead)
	{
		if(rw != null)
		{
			try
			{
				rw.write(lineRead.getBuffer(), lineRead.getOffset(), lineRead.getTotalLength());
				rw.write(FileEndOfLine.LF);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	public void writeEndOfRecordMarker()
	{
		if(rw != null)
		{
			try
			{
				rw.write(FileEndOfLine.LF);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	public boolean readEndOfLineMarker()
	{
		getFileCurrentPosition();
		int nByte = 0;
		if(rw != null)
		{
			try
			{
				nByte = rw.read();
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
		//getFileCurrentPosition();
		if(rw != null)
		{
			try
			{
				byte byteBuffer[] = getByteBuffer(nSize);
				int nNBytesRead = rw.read(byteBuffer, 0, nSize);
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
	
//	public int getUnixRecordLength()
//	{
//		getCurrentPosition();
//		int n = 0;
//		byte[] tVal = new byte[1];
//		if(rw != null)
//		{
//			try
//			{	
//				while(tVal[0] != FileEndOfLine.LF)
//				{
//					int nNBytesRead = rw.read(tVal, 0, 1);
//					if(nNBytesRead == -1)
//					{
//						setEOF(true);
//						return n;
//					}
//					n++;
//				}
//				return n;
//			}
//			catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}			
//		}
//		return n;
//	}
	
	private int readUnixLine(byte tBytes[], int nMaxLineSize)
	{
		if(rw != null)
		{
			try
			{
				int n = 0;
				t1Byte[0] = 0;
				while(t1Byte[0] != FileEndOfLine.LF)
				{
					int nNBytesRead = rw.read(t1Byte, 0, 1);
					if(nNBytesRead != -1)
						tBytes[n++] = t1Byte[0];
					else
					{
						setEOF(true);
						return n;
					}
				}
				return n - 1;
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				setEOF(true);
				return -1;
			}			
		}
		return 0;
	}
	
//	public int readUnixLine(byte tBytes[], int nOffset, int nMaxLineSize)
//	{
//		getCurrentPosition();
//		int n = nOffset;
//		byte[] tVal = new byte[1];
//		if(rw != null)
//		{
//			try
//			{	
//				while(tVal[0] != FileEndOfLine.LF)
//				{
//					int nNBytesRead = rw.read(tVal, 0, 1);
//					if(nNBytesRead != -1)
//						tBytes[n++] = tVal[0];
//					else
//					{
//						setEOF(true);
//						return n;
//					}
//				}
//				return n;
//			}
//			catch (IOException e)
//			{
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}			
//		}
//		return n;
//	}
	
	public int readChunk(byte tBytes[], int nNbBytes)
	{
		getFileCurrentPosition();
		int n = -1;
		if(rw != null && !isEOF())
		{
			try
			{	
				int nNBytesRead = rw.read(tBytes, 0, nNbBytes);
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
		getFileCurrentPosition();
		int n = -1;
		if(rw != null && !isEOF())
		{
			try
			{	
				int nNBytesRead = rw.read(tBytes, nOffset, nNbBytes);
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
	
	public LineRead readNextUnixLine()
	{
		if(rw == null)
			return null;

		long lLastPosition = getFileCurrentPosition();
		setLastPosition(lLastPosition);
		
		initLineRead();
			
		int nDataLength = readUnixLine(lineRead.getBuffer(), ms_nMaxRecordLength);
		if(nDataLength > 0)
		{
			lineRead.setDataLengthStartingAt0(nDataLength);
			return lineRead;
		}
		return null;
	}	
	
	public LineRead readBuffer(int nLength, boolean bTryReadNextLF)
	{		
		if(rw != null)
		{
			long lLastPosition = getFileCurrentPosition();
			setLastPosition(lLastPosition);
			
			int nFullLength = nLength;
			if(bTryReadNextLF)
				nFullLength++;
	
			initLineRead();
			int nLengthRead = readChunk(lineRead.getBuffer(), 0, nFullLength);
			if(nLengthRead >= 0)
			{
				lineRead.setDataLengthStartingAt0(nLengthRead);
				if(bTryReadNextLF)
					lineRead.manageTrailingLF();
				return lineRead;
			}
		}
		return null;
	}

	
	public void rewrite(byte[] tBytes, int nOffset, int nLength)
	{
		long lLastPosition = getLastPosition();
		setFileCurrentPosition(lLastPosition);
		write(tBytes, nOffset, nLength);
	}
	
	public void rewriteWithEOL(byte[] tbyDest, int nSize)
	{
		long lLastPosition = getLastPosition();
		setFileCurrentPosition(lLastPosition);
		writeWithEOL(tbyDest, nSize);
	}
	
	public boolean isReadable()
	{
		return true;
	}
	
	public boolean isWritable()
	{
		return true;
	}
	
	public boolean isUpdateable()
	{
		return true;
	}

	
	public long getFileCurrentPosition()
	{
		try
		{
			long lPos = rw.getFilePointer();
			return lPos; 
		}
		catch (IOException e)
		{
			return -1;
		}
	}

	public boolean setFileCurrentPosition(long lCurrentPosition)
	{
		try
		{
			rw.seek(lCurrentPosition);
			return true;
		}
		catch (IOException e)
		{
		}
		return false;
	}
	
	public boolean savePosition(int nMaxReadAheadSize)
	{
		lSavedPosition = getFileCurrentPosition();
		if(lSavedPosition >= 0)
			return true;
		return false;
	}
	
	public boolean returnAtSavedPosition()
	{
		if(lSavedPosition >= 0)
			return setFileCurrentPosition(lSavedPosition);
		return false;
	}
	
	public LineRead readVariableLengthLine(boolean bTryReadNextLF, boolean bHeaderIsInt, LineRead lineOut)	// Read a vairable length line (length is given in record header 4 bytes)
	{
		LineRead recordHeader = readBuffer(4, false);
		if(recordHeader != null)
		{
			int nLength = 0;
			if(bHeaderIsInt)
				nLength = recordHeader.getAsLittleEndingUnsignBinaryInt();
			else
				nLength = recordHeader.getAsLittleEndingUnsignBinaryShort();
			
			if(lineOut == null)
				lineOut = new LineRead();
			lineOut.resetAndGaranteeBufferStorage(4 + nLength + 1, 4 + nLength + 1);
			lineOut.append(recordHeader);
	
			LineRead recordBody = readBuffer(nLength, bTryReadNextLF);
			lineOut.append(recordBody);
			
			return lineOut;
		}
		return null;
	}
	
	public boolean setFileLength(long lFileLength)
	{
		try
		{
			rw.setLength(lFileLength);
			return true;
		}
		catch (IOException e)
		{
		}
		return false;
	}
	
	public boolean shinkFileAtCurrentPosition()
	{
		try
		{
			long lPos = rw.getFilePointer();				
			rw.setLength(lPos);
			return true;
		}
		catch (IOException e)
		{
		}
		return false;
	}
}
