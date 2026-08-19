/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;


/** Provides data file write behavior. */
public class DataFileWrite extends BaseDataFileBuffered
{
    private BufferedOutputStream out = null;
    private FileLock outLock = null;
    private boolean ismustWriteFileHeader = false;

    /** Creates a new data file write instance. */
    public DataFileWrite(String csName, boolean bMustWriteFileHeader)
    {
        setName(csName);
        this.ismustWriteFileHeader = bMustWriteFileHeader;
    }

    /** Executes the open operation. */
    public boolean open(String csName)
    {
        setName(csName);
        return open(false);
    }

    /** Executes the open operation. */
    public boolean open()
    {
        return open(false);
    }

    /** Executes the open in append operation. */
    public boolean openInAppend(String csName)
    {
        setName(csName);
        return open(true);
    }

    /** Executes the open in append operation. */
    public boolean openInAppend(LogicalFileDescriptor logicalFileDescriptor)
    {
        return open(true, logicalFileDescriptor);
    }

    /** Executes the open operation. */
    public boolean open(boolean bAppend)
    {
        try
        {
            FileOutputStream fileOutput = new FileOutputStream(getName(), bAppend);
            out = new BufferedOutputStream(new DataOutputStream(fileOutput));
            FileChannel outChannel = fileOutput.getChannel();
            try
            {
                outLock = outChannel.lock();
            }
            catch(IOException e)
            {
                e.printStackTrace();
                return false;
            }
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
        return open(false, logicalFileDescriptor);
    }

    private boolean open(boolean bAppend, LogicalFileDescriptor logicalFileDescriptor)
    {
        boolean isopened = open(bAppend);
        if(isopened && logicalFileDescriptor != null)
        {
            if (bAppend) { // append something to the file: Read it's header as the file must already exists
                logicalFileDescriptor.readFileHeader(this);
            } else    // Create a new file
                {
                    if (ismustWriteFileHeader) {   // open the file in output not append with writing file header
                        logicalFileDescriptor.writeFileHeader(this);
                    }
                }
        }
        return isopened;
    }

    /** Executes the close operation. */
    public boolean close()
    {
        try
        {
            if(out != null)
            {
                if(outLock != null)
                {
                    outLock.release();
                    outLock = null;
                }
                out.close();
                out = null;
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
        try
        {
            if(out != null)
            {
                out.flush();
                return true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /** Returns whether open. */
    public boolean isOpen()
    {
        if (out != null) {
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
            if (out != null) {
                cs += " Write";
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
        if(tBytes != null)
        {
            if(out != null)
            {
                try
                {
                    out.write(tBytes, nOffset, nLength);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /** Writes the record. */
    public void writeRecord(String cs)
    {
        int nLg = cs.length();
        if(out != null)
        {
            try
            {
                out.write(cs.getBytes(), 0, nLg);
                out.write((char)FileEndOfLine.LF);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /** Executes the write operation. */
    public void write(byte[] tBytes)
    {
        if(tBytes != null)
        {
            if(out != null)
            {
                try
                {
                    out.write(tBytes, 0, tBytes.length);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /** Writes the with eol. */
    public void writeWithEOL(byte[] tBytes, int nSize)
    {
        if(tBytes != null)
        {
            if(out != null)
            {
                try
                {
                    if(nSize+1 < tBytes.length)
                    {
                        tBytes[nSize] = FileEndOfLine.LF;
                        out.write(tBytes, 0, nSize+1);
                    }
                    else
                    {
                        out.write(tBytes, 0, nSize);
                        out.write(FileEndOfLine.LF);
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

    /** Writes the with eol. */
    public void writeWithEOL(LineRead lineRead)
    {
        if(out != null)
        {
            try
            {
                out.write(lineRead.getBuffer(), lineRead.getOffset(), lineRead.getTotalLength());
                out.write(FileEndOfLine.LF);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /** Writes the end of record marker. */
    public void writeEndOfRecordMarker()
    {
        if(out != null)
        {
            try
            {
                out.write(FileEndOfLine.LF);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /** Reads the end of line marker. */
    public boolean readEndOfLineMarker()
    {
        return false;
    }

    /** Executes the read operation. */
    public byte[] read(int nSize)
    {
        return null;
    }

    public int getUnixRecordLength()
    {
        return 0;
    }

    /** Reads the unix line. */
    public int readUnixLine(byte tBytes[], int nMaxLineSize)
    {
        return 0;
    }

    /** Reads the unix line. */
    public int readUnixLine(byte tBytes[], int nOffset, int nMaxLineSize)
    {
        return 0;
    }

    /** Reads the chunk. */
    public int readChunk(byte tBytes[], int nNbBytes)
    {
        return 0;
    }

    /** Reads the chunk. */
    public int readChunk(byte tBytes[], int nOffset, int nNbBytes)
    {
        return 0;
    }

    /** Reads the whole file as array. */
    public byte[] readWholeFileAsArray()
    {
        return null;
    }

    /** Reads the next unix line. */
    public LineRead readNextUnixLine()
    {
        return null;
    }

    /** Reads the buffer. */
    public LineRead readBuffer(int nLength, boolean bTryReadNextLF)
    {
        return null;
    }

    /** Executes the rewrite operation. */
    public void rewrite(byte[] tBytes, int nOffset, int nLength)
    {
        //write(tBytes, nOffset, nLength);
    }

    /** Executes the rewrite with eol operation. */
    public void rewriteWithEOL(byte[] tbyDest, int nSize)
    {
        //rewriteWithEOL(tbyDest, nSize);
    }

    public boolean isReadable()
    {
        return false;
    }

    public boolean isWritable()
    {
        return true;
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
        return false;
    }

    /** Executes the return at saved position operation. */
    public boolean returnAtSavedPosition()
    {
        return false;
    }

    /** Executes the unbuffered read ahead line operation. */
    public String unbufferedReadAheadLine(int nMaxReadAheadSize)
    {
        return null;
    }

    // Read a vairable length line (length is given in record header 4 bytes)
    /** Reads the variable length line. */
    public LineRead readVariableLengthLine(boolean bTryReadNextLF, boolean bHeaderIsInt, LineRead lineOut)
    {
        return null;
    }
}
