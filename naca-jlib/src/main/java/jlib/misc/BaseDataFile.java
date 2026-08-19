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
 * @version $Id: BaseDataFile.java,v 1.20 2007/10/25 15:13:11 u930di Exp $
 */
public abstract class BaseDataFile
{
    protected String csName = null;
    private boolean iseOF = false;

    /** Returns whether null file. */
    public static boolean isNullFile(String csFilePhysicalName)
    {
        if (StringUtil.isEmpty(csFilePhysicalName)) {
            return true;
        }
        if (csFilePhysicalName.equalsIgnoreCase("wrk/nullfile")) {
            return true;
        }
        if (csFilePhysicalName.toUpperCase().indexOf("NULLFILE") >= 0) {
            return true;
        }
        return false;
    }


    public void setName(String csName)
    {
        this.csName = csName;
    }

    public String getName()
    {
        return csName;
    }

    public boolean isEOF()
    {
        return iseOF;
    }

    public void setEOF(boolean b)
    {
        iseOF = b;
    }

    //public abstract boolean open();
    /** Executes the open operation. */
    public abstract boolean open(LogicalFileDescriptor logicalFileDescriptor);

    /** Executes the flush operation. */
    public abstract boolean flush();
    /** Executes the close operation. */
    public abstract boolean close();

    /** Returns whether open. */
    public abstract boolean isOpen();

    /** Writes the end of record marker. */
    public abstract void writeEndOfRecordMarker();
    /** Writes the with eol. */
    public abstract void writeWithEOL(byte[] tBytes, int nSize);
    /** Writes the with eol. */
    public abstract void writeWithEOL(LineRead lineRead);

    /** Executes the write operation. */
    public abstract void write(byte[] tBytes);
    /** Executes the write operation. */
    public abstract void write(byte[] tBytes, int nOffset, int nLength);
    /** Writes the record. */
    public abstract void writeRecord(String cs);

    // Read a vairable length line (length is given in record header 4 bytes)
    /** Reads the variable length line. */
    public abstract LineRead readVariableLengthLine(boolean bTryReadNextLF, boolean bHeaderIsInt, LineRead lineOut);
    /** Reads the next unix line. */
    public abstract LineRead readNextUnixLine();
    /** Reads the buffer. */
    public abstract LineRead readBuffer(int nLineLength, boolean bTryReadNextLF);
    /** Executes the read operation. */
    public abstract byte[] read(int nSize);
    /** Reads the end of line marker. */
    public abstract boolean readEndOfLineMarker();
    /** Executes the save position operation. */
    public abstract boolean savePosition(int nMaxReadAheadSize);
    /** Executes the return at saved position operation. */
    public abstract boolean returnAtSavedPosition();

    /** Returns the byte buffer. */
    public abstract byte[] getByteBuffer(int nSize);
    //public abstract byte[] getAlternateByteBuffer(int nSize);

    /** Executes the rewrite operation. */
    public abstract void rewrite(byte[] tBytes, int nOffset, int nLength);
    /** Executes the rewrite with eol operation. */
    public abstract void rewriteWithEOL(byte[] tbyDest, int nSize);
    //public abstract long getFileSize();


    /** Returns whether readable. */
    public abstract boolean isReadable();
    /** Returns whether writable. */
    public abstract boolean isWritable();
    /** Returns whether updateable. */
    public abstract boolean isUpdateable();

    /** Returns the file current position. */
    public abstract long getFileCurrentPosition();
    /** Sets the file current position. */
    public abstract boolean setFileCurrentPosition(long lCurrentPosition);


    public long getLastPosition()
    {
        return lastPosition;
    }

    public void setLastPosition(long l)
    {
        lastPosition = l;
    }

    private long lastPosition = 0;


    /** Executes the unbuffered read ahead line operation. */
    public String unbufferedReadAheadLine(int nMaxReadAheadSize)
    {
        String cs = null;
        if(savePosition(nMaxReadAheadSize))
        {
            byte[] tBytes = read(nMaxReadAheadSize);
            if (tBytes != null)
            {
                for(int nPos=0; nPos<tBytes.length && nPos < nMaxReadAheadSize; nPos++)
                {
                    if(tBytes[nPos] == FileEndOfLine.LF)
                    {
                        cs = new String(tBytes, 0, nPos);
                        break;
                    }
                }
                if (returnAtSavedPosition()) {
                    return cs;
                }
            }
        }
        return null;
    }

    /** Executes the skip file header operation. */
    public int skipFileHeader(String cs)
    {
        // Reread the header, to set current position just after header
        int nHeaderLength = cs.length() + 1;    // Skip header trailing LF
        read(nHeaderLength);
        return nHeaderLength;
    }
}
