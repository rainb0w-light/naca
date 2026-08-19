/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import jlib.misc.AsciiEbcdicConverter;
import jlib.misc.FileEndOfLine;
import jlib.misc.LineRead;


/**
 * @author PJD
 *
 */
public class VarBuffer extends InternalCharBuffer
{
    /** Creates a new var buffer instance. */
    public VarBuffer()
    {
    }

    VarBuffer(VarBuffer varBufferMaster)
    {
        super();
        shareDataBufferFrom(varBufferMaster);
    }

    /** Creates a new var buffer instance. */
    public VarBuffer(char [] acBuffer)
    {
        super(acBuffer);
    }

    /** Creates a new var buffer instance. */
    public VarBuffer(int nSize)
    {
        super(nSize);
    }

//  public VarBase getVarFullName(int nId)
//  {
//      return getProgramManager().getVarFullName(nId);
//  }

//  public VarBase  getVarFullName(VarDefBuffer varDef)
//  {
//      String csName = varDef.getFullName(getProgramManager().getSharedProgramInstanceData());
//      return getProgramManager().getVarFullName(csName);

//      int nId = varDef.getId();
//      return getProgramManager().getVarFullName(nId);
//  }

//  public VarBase getVarFullNameUpperCase(String csName)
//  {
//      String csNameUpperCase = csName.toUpperCase();
//      return getProgramManager().getVarFullNameUpperCase(csNameUpperCase);
//  }

//  public VarBase getVarFullNameUpperCase(VarDefBuffer varDef)
//  {
//      String csNameUpperCase = varDef.getFullNameUpperCase();
//      return getProgramManager().getVarFullNameUpperCase(csNameUpperCase);
//  }


//  public void addMapAssociatedSemanticContext(Edit edit, String csSemanticContext)
//  {
//      if(arrMapAssociatedSemanticContext == null)
//          arrMapAssociatedSemanticContext = new ArrayList();
//      CEditSemanticContextMapAssoc EditSemanticContextMapAssoc = new CEditSemanticContextMapAssoc(edit, csSemanticContext);
//      arrMapAssociatedSemanticContext.add(EditSemanticContextMapAssoc);
//  }

    /** Returns a string representation of this value. */
    public String toString()
    {
        return acBuffer.toString();
    }


//  public int writeCopy(int nPositionDest, int nPositionSource, int nNbCharsToCopy)
//  {
//      for(int n=0; n<nNbCharsToCopy; n++, nPositionSource++, nPositionDest++)
//      {
//          acBuffer[nPositionDest] = acBuffer[nPositionSource];
//      }
//      return nPositionDest;
//  }
//
    /** Executes the copy bytes from source operation. */
    public int copyBytesFromSource(int nPositionDest, InternalCharBuffer source, int nPositionSource, int nNbCharsToCopy)
    {
        for(int n=0; n<nNbCharsToCopy; n++)
        {
            acBuffer[nPositionDest++] = source.acBuffer[nPositionSource++];
        }
        return nPositionDest;
    }

//  public void copyInternalData(int nPositionDest, VarBufferPos Source, int nNbCharsToCopy)
//  {
//      int nPositionSource = Source.nAbsolutePosition;
//      for(int n=0; n<nNbCharsToCopy; n++)
//      {
//          acBuffer[nPositionDest++] = Source.acBuffer[nPositionSource++];
//      }
//  }

    /** Executes the copy bytes from source operation. */
    public void copyBytesFromSource(int nPositionDest, InternalCharBuffer sourceCharBuffer)
    {
        int nNbCharsToCopy = sourceCharBuffer.acBuffer.length;
        for(int nSource=0; nSource<nNbCharsToCopy; nSource++, nPositionDest++)
        {
            acBuffer[nPositionDest] = sourceCharBuffer.acBuffer[nSource];
        }
    }
//
//  public void setWithNoConvertEbcdicToUnicode(byte tBytesSource[], int nLength)
//  {
//      for(int n=0; n<nLength; n++)
//      {
//          int nByte = tBytesSource[n];
//          if(nByte < 0)
//              nByte += 256;
//          acBuffer[n] = (char)nByte;
//      }
//  }

//  public void setWithNoConvertEbcdicToUnicodeAtOffsetDest(byte tBytesSource[], int nOffsetDest, int nLength)
//  {
//      for(int n=0; n<nLength; n++)
//      {
//          int nByte = tBytesSource[n];
//          if(nByte < 0)
//              nByte += 256;
//          acBuffer[nOffsetDest+n] = (char)nByte;
//      }
//  }

    /** Sets the from line read. */
    public int setFromLineRead(LineRead lineRead, int nOffsetDest)
    {
        int nSourceOffset = lineRead.getOffset();
        int nSourceLength = lineRead.getTotalLength();
        byte bufSource[] = lineRead.getBuffer();

        for(int n=0; n<nSourceLength; n++)
        {
            int nByte = bufSource[nSourceOffset + n];
            if (nByte < 0) {
                nByte += 256;
            }
            acBuffer[nOffsetDest + n] = (char) nByte;
        }
        if(nSourceLength > 0)
        {
            if (acBuffer[nOffsetDest + nSourceLength - 1] == FileEndOfLine.LF) {
                return nSourceLength - 1; // Return length excluding ending LF
            }
        }
        return nSourceLength;
    }

    /** Returns the first end of line position. */
    public int getFirstEndOfLinePosition(byte byEOL)
    {
        for(int n=0; n<acBuffer.length; n++)
        {
            byte b = (byte)acBuffer[n];
            if (b == byEOL) {
                return n;
            }
        }
        return acBuffer.length-1;
    }

//  public char [] getCharArray()
//  {
//      return acBuffer;
//  }


    /** Executes the dump hexa operation. */
    public void dumpHexa(int nPosition, int nLength)
    {
        System.out.println("dumpHexa from position=" + nPosition + ", length="+nLength);
        String cs = "" + nPosition + ": ";
        int n=0;
        while(n<nLength)
        {
            char c = acBuffer[n+nPosition];
            String csHexa = AsciiEbcdicConverter.getHexaValue(c);
            cs += "0x" + csHexa + " ";
            n++;
            if((n % 8) == 0)
            {
                System.out.println(cs);
                cs = "" + n + nPosition + ": ";
            }
        }
        if ((n % 8) != 0) {
            System.out.println(cs);
        }
    }

}
