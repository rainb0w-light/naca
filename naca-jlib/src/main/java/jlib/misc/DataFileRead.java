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

/** Provides data file read behavior. */
public class DataFileRead extends BaseDataFileBuffered
{
    private BufferedInputStream in = null;

    /** Creates a new data file read instance. */
    public DataFileRead()
    {
    }

    /** Creates a new data file read instance. */
    public DataFileRead(String csName)
    {
        this.csName = csName;
    }

//  public boolean open(String csName)
//  {
//      setName(csName);
//      return open();
//  }

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

    /** Executes the open operation. */
    public boolean open(LogicalFileDescriptor logicalFileDescriptor)
    {
        boolean isopened = doOpen();
        if(isopened && logicalFileDescriptor != null)
        {
            logicalFileDescriptor.readFileHeader(this);
        }
        return isopened;
    }

    /** Executes the close operation. */
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

    /** Executes the flush operation. */
    public boolean flush()
    {
        return false;
    }

    /** Returns whether open. */
    public boolean isOpen()
    {
        if (in != null) {
            return true;
        }
        return false;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        String cs = csName + " (";
        if(isOpen())
        {
            cs += "Open";
            if (in != null) {
                cs += " Read";
            }
        }
        else
        {
            cs += "Close";
        }
        cs += ")";
        return cs;
    }

    /** Executes the write operation. */
    public void write(byte[] tBytes, int nOffset, int nLength)
    {
    }

    /** Writes the record. */
    public void writeRecord(String cs)
    {
    }

    /** Executes the write operation. */
    public void write(byte[] tBytes)
    {
    }

    /** Writes the with eol. */
    public void writeWithEOL(byte[] tBytes, int nSize)
    {
    }

    /** Writes the with eol. */
    public void writeWithEOL(LineRead lineRead)
    {
    }

    /** Writes the end of record marker. */
    public void writeEndOfRecordMarker()
    {
    }

    /** Reads the end of line marker. */
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
                    return true;    // Found EOL
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

    /** Executes the read operation. */
    public byte[] read(int nSize)
    {
        if(in != null)
        {
            try
            {
                byte byteBuffer[] = getByteBuffer(nSize);
                int nNBytesRead = in.read(byteBuffer, 0, nSize);
                if (nNBytesRead == -1) {
                    setEOF(true);
                }
                return byteBuffer;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    /** Returns the unix record length. */
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
                e.printStackTrace();
            }
        }
        return n;
    }

    /** Reads the unix line. */
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
                    if (nNBytesRead != -1) {
                        tBytes[n++] = tVal[0];
                    } else
                    {
                        setEOF(true);
                        return n;
                    }
                }
                return n;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                setEOF(true);
                return -1;
            }
        }
        return n;
    }

    /** Reads the unix line. */
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
                    if (nNBytesRead != -1) {
                        tBytes[n++] = tVal[0];
                    } else
                    {
                        setEOF(true);
                        return n;
                    }
                }
                return n;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return n;
    }

    /** Reads the chunk. */
    public int readChunk(byte tBytes[], int nNbBytes)
    {
        int n = -1;
        if(in != null && !isEOF())
        {
            try
            {
                int nNBytesRead = in.read(tBytes, 0, nNbBytes);
                if (nNBytesRead == -1) {
                    setEOF(true);
                }
                return nNBytesRead;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return n;
    }

    /** Reads the chunk. */
    public int readChunk(byte tBytes[], int nOffset, int nNbBytes)
    {
        int n = -1;
        if(in != null && !isEOF())
        {
            try
            {
                int nNBytesRead = in.read(tBytes, nOffset, nNbBytes);
                if (nNBytesRead == -1) {
                    setEOF(true);
                }
                return nNBytesRead;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return n;
    }

    /** Reads the whole file as array. */
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
                e.printStackTrace();
            }

        }
        return null;
    }

    /** Reads the next unix line. */
    public LineRead readNextUnixLine()
    {
        // Should use a DataFileLineReader
        return null;
    }


    /** Reads the buffer. */
    public LineRead readBuffer(int nLength, boolean bTryReadNextLF)
    {
        // Should use a DataFileLineReader
        return null;
    }

    /** Executes the rewrite operation. */
    public void rewrite(byte[] tBytes, int nOffset, int nLength)
    {
    }

    /** Executes the rewrite with eol operation. */
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

    /** Sets the file current position. */
    public boolean setFileCurrentPosition(long lCurrentPosition)
    {
        return false;
    }

    /** Executes the save position operation. */
    public boolean savePosition(int nMaxReadAheadSize)
    {
        if(in != null && in.markSupported())
        {
            in.mark(nMaxReadAheadSize);
            return true;
        }
        return false;
    }

    /** Executes the return at saved position operation. */
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

    // Read a vairable length line (length is given in record header 4 bytes)
    /** Reads the variable length line. */
    public LineRead readVariableLengthLine(boolean bTryReadNextLF, boolean bHeaderIsInt, LineRead lineOut)
    {
        LineRead recordHeader = readBuffer(4, false);
        if(recordHeader != null)
        {
            int nLength = recordHeader.getAsLittleEndingUnsignBinaryInt();

            if (lineOut == null) {
                lineOut = new LineRead();
            }
            lineOut.resetAndGaranteeBufferStorage(4 + nLength, 4 + nLength);
            lineOut.append(recordHeader);

            LineRead recordBody = readBuffer(nLength, bTryReadNextLF);
            lineOut.append(recordBody);

            return lineOut;
        }
        return null;
    }


}
