/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.blowfish;


import java.util.*;


// See http://www.zetagrid.net/zeta/doc/src/
/**
  * support class for easy string encryption with the Blowfish algorithm,
  * now in CBC mode with a SHA-1 key setup and correct padding - the
  * purposes of this module is mainly to show a possible implementation 
  * with Blowfish ...
  * @author Markus Hahn <markus_hahn@gmx.net>
  * @version August 10, 2001
  */

public class BlowfishEasy {


  // the Blowfish CBC instance
  BlowfishCBC bfish;

  // one random generator for all simple callers...
  static Random rndGen;

  // ...and created early
  static {
    rndGen = new Random();
  }
 


  /**
    * constructor to set up a string as the key (oversized password will be cut)
    * @param sPassword the password (treated as a real unicode array)
    */
  public BlowfishEasy(String sPassword) 
  {
    // hash down the password to a 160bit key
    SHA1 hasher = new SHA1();
    hasher.update(sPassword);
    hasher.finalize();
    
    // setup the encryptor (use a dummy IV)
    bfish = new BlowfishCBC(hasher.getDigest(), 0);
    hasher.clear();  
  }



  /**
    * encrypts a string (treated in UNICODE) using the
    * standard Java random generator, which isn't that
    * great for creating IVs
    * @param sPlainText string to encrypt
    * @return encrypted string in binhex format
    */
  public String encryptString(String sPlainText) 
  {
    // get the IV
    long cBCIV;
    synchronized (rndGen) 
    {
      cBCIV = rndGen.nextLong();
    }

    // map the call;
    return encStr(sPlainText, cBCIV);
  }



  /**
    * encrypts a string (treated in UNICODE)
    * @param sPlainText string to encrypt
    * @param rndGen random generator (usually a java.security.SecureRandom instance)
    * @return encrypted string in binhex format
    */
  public String encryptString(String sPlainText,
                              Random rndGen) 
  {
    // get the IV
    long cBCIV = rndGen.nextLong();

    // map the call;
    return encStr(sPlainText, cBCIV);
  }



  // internal routine for string encryption

  private String encStr(String sPlainText,
                        long lNewCBCIV) 
  {
    // allocate the buffer (align to the next 8 byte border plus padding)
    int nStrLen = sPlainText.length();
    byte[] buf = new byte [((nStrLen << 1) & 0xfffffff8) + 8];

    // copy all bytes of the string into the buffer (use network byte order)
    int nI; 
    int nPos = 0;
    for (nI = 0; nI < nStrLen; nI++) 
    {
      char actChar = sPlainText.charAt(nI);
      buf[nPos++] = (byte) ((actChar >> 8) & 0x0ff);
      buf[nPos++] = (byte) (actChar & 0x0ff) ;
    }

    // pad the rest with the PKCS5 scheme
    byte ispadVal = (byte)(buf.length - (nStrLen << 1));
    while (nPos < buf.length)
    {
      buf[nPos++] = ispadVal;
    }

    // create the encryptor
    bfish.setCBCIV(lNewCBCIV);

    // encrypt the buffer
    bfish.encrypt(buf);
    
    // return the binhex string
    byte[] newCBCIV = new byte[BlowfishCBC.BLOCKSIZE];
    BinConverter.longToByteArray(lNewCBCIV, 
                                 newCBCIV,
                                 0);

    return BinConverter.bytesToBinHex(newCBCIV, 0, BlowfishCBC.BLOCKSIZE) + 
           BinConverter.bytesToBinHex(buf, 0, buf.length);
  }
  

  /**
    * decrypts a hexbin string (handling is case sensitive)
    * @param sCipherText hexbin string to decrypt
    * @return decrypted string (null equals an error)
    */
  public String decryptString(String sCipherText) 
  {
    // get the number of estimated bytes in the string (cut off broken blocks)
    int nLen = (sCipherText.length() >> 1) & ~7;

    // does the given stuff make sense (at least the CBC IV)?
    if (nLen < BlowfishECB.BLOCKSIZE)
      return null;

    // get the CBC IV
    byte[] cbciv = new byte[BlowfishCBC.BLOCKSIZE];
    int nNumOfBytes = BinConverter.binHexToBytes(sCipherText,
                                                 cbciv,
                                                 0,
                                                 0,
                                                 BlowfishCBC.BLOCKSIZE);
    if (nNumOfBytes < BlowfishCBC.BLOCKSIZE)
      return null;

    // (got it)
    bfish.setCBCIV(cbciv);

    // something left to decrypt?       
    nLen -= BlowfishCBC.BLOCKSIZE;
    if (nLen == 0) 
    {
      return "";
    }

    // get all data bytes now
    byte[] buf = new byte[nLen];

    nNumOfBytes = BinConverter.binHexToBytes(sCipherText,
                                             buf,
                                             BlowfishCBC.BLOCKSIZE * 2,
                                             0,
                                             nLen);

    // we cannot accept broken binhex sequences due to padding
    // and decryption
    if (nNumOfBytes < nLen)
    {
      return null; 
    }

    // decrypt the buffer
    bfish.decrypt(buf);

    // get the last padding byte
    int nPadByte = buf[buf.length - 1] & 0x0ff;

    // ( try to get all information if the padding doesn't seem to be correct)
    if ((nPadByte > 8) || (nPadByte < 0))
    {
      nPadByte = 0; 
    }

    // calculate the real size of this message
    nNumOfBytes -= nPadByte;
    if (nNumOfBytes < 0) 
    {
      return "";
    }

    // success
    return BinConverter.byteArrayToUNCString(buf, 0, nNumOfBytes);
  }


  /**
    * destroys (clears) the encryption engine,
    * after that the instance is not valid anymore
    */
  public void destroy() 
  {
    bfish.cleanUp();
  }

}

     
