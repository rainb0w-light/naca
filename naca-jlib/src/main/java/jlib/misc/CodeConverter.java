/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

/** Provides code converter behavior. */
public class CodeConverter
{
    private int size = 0;
    private int[] storedFrom = null;
    private int[] storedTo = null;

    /** Creates a new code converter instance. */
    public CodeConverter(String csConversion)
    {
        if (csConversion == null || csConversion.equals("")) {
            return;
        }

        String[] csSplit = csConversion.split(",");
        size = csSplit.length;

        storedFrom = new int [size];
        storedTo = new int [size];
        for (int n=0; n < size; n++)
        {
            String[] csFromTo = csSplit[n].split("-");
            int nFrom = Integer.valueOf(csFromTo[0]).intValue();
            int nTo = Integer.valueOf(csFromTo[1]).intValue();
            storedFrom[n] = nFrom;
            storedTo[n] = nTo;
        }
    }

    /** Executes the convert operation. */
    public String convert(String csIn)
    {
        if (size == 0) {
            return csIn;
        }

        String csOut = csIn;
        for (int n=0; n < size; n++)
        {
            csOut = csOut.replace((char)storedFrom[n], (char)storedTo[n]);
        }
        return csOut;
    }

//  public byte [] convert(byte tbSource[])
//  {
//      if (ms_nSize == 0)
//          return tbSource;
//
//      int nNbBytes = tbSource.length;
//      if (ms_nSize == 1)
//      {
//          for(int n=0; n<nNbBytes; n++)
//          {
//              int nSource = (int)tbSource[n];
//              if(nSource < 0)
//                  nSource += 256;
//              if(nSource == ms_tFrom[0])
//                  tbSource[n] = (byte)ms_tTo[n];
//          }
//          return tbSource;
//      }
//      else if(ms_nSize == 2)
//      {
//          for(int n=0; n<nNbBytes; n++)
//          {
//              int nSource = (int)tbSource[n];
//              if(nSource < 0)
//                  nSource += 256;
//              if(nSource == ms_tFrom[0])
//              {
//                  tbSource[n] = (byte)ms_tTo[0];
//                  continue;
//              }
//              if(nSource == ms_tFrom[1])
//                  tbSource[n] = (byte)ms_tTo[1];
//          }
//          return tbSource;
//      }
//      else if(ms_nSize == 3)
//      {
//          for(int n=0; n<nNbBytes; n++)
//          {
//              int nSource = (int)tbSource[n];
//              if(nSource < 0)
//                  nSource += 256;
//              if(nSource == ms_tFrom[0])
//              {
//                  tbSource[n] = (byte)ms_tTo[0];
//                  continue;
//              }
//              if(nSource == ms_tFrom[1])
//              {
//                  tbSource[n] = (byte)ms_tTo[1];
//                  continue;
//              }
//              if(nSource == ms_tFrom[2])
//                  tbSource[n] = (byte)ms_tTo[2];
//          }
//          return tbSource;
//      }
//      else
//      {
//          int m=0;
//          for(int n=0; n<nNbBytes; n++)
//          {
//              int nSource = (int)tbSource[n];
//              if(nSource < 0)
//                  nSource += 256;
//
//              for(m=0; m<ms_nSize; m++)
//              {
//                  if(nSource == ms_tFrom[m])
//                  {
//                      tbSource[n] = (byte)ms_tTo[m];
//                      break;
//                  }
//              }
//          }
//          return tbSource;
//      }
//  }
}
