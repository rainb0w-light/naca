/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.blowfish;

/**
  * implementation of the Blowfish encryption algorithm in CBC mode
  * @author Markus Hahn <markus_hahn@gmx.net>
  * @version August 10, 2001
  */

public class BlowfishCBC extends BlowfishECB {


  // here we hold the CBC IV
  long bCIV;


  /**
    * get the current CBC IV (for cipher resets)
    * @return current CBC IV
    */
  public long getCBCIV() 
  {
    return bCIV;
  }

  /**
    * get the current CBC IV (for cipher resets)
    * @param dest wher eto put current CBC IV in network byte ordered array
    */
  public void getCBCIV(byte[] dest) 
  {
    BinConverter.longToByteArray(bCIV, dest, 0);
  }

  /**
    * set the current CBC IV (for cipher resets)
    * @param lNewCBCIV the new CBC IV
    */
  public void setCBCIV(long lNewCBCIV) 
  {
    bCIV = lNewCBCIV;
  }

  /**
    * set the current CBC IV (for cipher resets)
    * @param newCBCIV the new CBC IV  in network byte ordered array
    */
  public void setCBCIV(byte[] newCBCIV) 
  {
    bCIV = BinConverter.byteArrayToLong(newCBCIV, 0);
  }


  /**
    * constructor, stores a zero CBC IV
    * @param bfkey key material, up to MAXKEYLENGTH bytes
    */
  public BlowfishCBC(byte[] bfkey) 
  {
    super(bfkey);

    // store zero CBCB IV
    setCBCIV(0);
  }


  /**
    * constructor
    * @param bfkey key material, up to MAXKEYLENGTH bytes
    * @param lInitCBCIV the CBC IV
    */
  public BlowfishCBC(byte[] bfkey, 
                     long lInitCBCIV) 
  {
    super(bfkey);

    // store the CBCB IV
    setCBCIV(lInitCBCIV);
  }


  /**
    * constructor
    * @param bfkey key material, up to MAXKEYLENGTH bytes
    * @param lInitCBCIV the CBC IV (array with min. BLOCKSIZE bytes)
    */
  public BlowfishCBC(byte[] bfkey, 
                     byte[] initCBCIV) 
  {
    super(bfkey);

    // store the CBCB IV
    setCBCIV(initCBCIV);
  }


  /**
    * cleans up all critical internals,
    * call this if you don't need an instance anymore
    */ 
  public void cleanUp() 
  {
    bCIV = 0;
    super.cleanUp();
  }


  // internal routine to encrypt a block in CBC mode
  private long encryptBlockCBC(long lPlainblock) 
  {
    // chain with the CBC IV
    lPlainblock ^= bCIV;

    // encrypt the block
    lPlainblock = super.encryptBlock(lPlainblock);

    // the encrypted block is the new CBC IV
    return (bCIV = lPlainblock);
  }


  // internal routine to decrypt a block in CBC mode
  private long decryptBlockCBC(long lCipherblock) 
  {
    // save the current block
    long temp = lCipherblock;

    // decrypt the block
    lCipherblock = super.decryptBlock(lCipherblock);

    // dechain the block
    lCipherblock ^= bCIV;

    // set the new CBC IV
    bCIV = temp;

    // return the decrypted block
    return lCipherblock;
  }



  /**
    * encrypts a byte buffer (should be aligned to an 8 byte border) 
    * to another buffer (of the same size or bigger)
    * @param inbuffer buffer with plaintext data
    * @param outbuffer buffer to get the ciphertext data
    */
  public void encrypt(byte[] inbuffer, 
                      byte[] outbuffer) 
  {
    int nLen = inbuffer.length;
    long temp;
    for (int nI = 0; nI < nLen; nI +=8) 
    {
      // encrypt a temporary 64bit block
      temp = BinConverter.byteArrayToLong(inbuffer, nI);
      temp = encryptBlockCBC(temp);
      BinConverter.longToByteArray(temp, outbuffer, nI);
    }
  }



  /**
    * encrypts a byte buffer (should be aligned to an 8 byte border) to itself
    * @param buffer buffer to encrypt
    */
  public void encrypt(byte[] buffer) 
  {

    int nLen = buffer.length;
    long temp;
    for (int nI = 0; nI < nLen; nI +=8) 
    {
      // encrypt a temporary 64bit block
      temp = BinConverter.byteArrayToLong(buffer, nI);
      temp = encryptBlockCBC(temp);
      BinConverter.longToByteArray(temp, buffer, nI);
    }
  }




  /**
    * encrypts an int buffer (should be aligned to an
    * two integer border) to another int buffer (of the same 
    * size or bigger)
    * @param inbuffer buffer with plaintext data
    * @param outBuffer buffer to get the ciphertext data
    */
  public void encrypt(int[] inbuffer, 
                      int[] outbuffer) 
  {
    int nLen = inbuffer.length;
    long temp;
    for (int nI = 0; nI < nLen; nI +=2) 
    {
      // encrypt a temporary 64bit block
      temp = BinConverter.intArrayToLong(inbuffer, nI);
      temp = encryptBlockCBC(temp);
      BinConverter.longToIntArray(temp, outbuffer, nI);
    }
  }


  /**
    * encrypts an integer buffer (should be aligned to an
    * @param buffer buffer to encrypt
    */
  public void encrypt(int[] buffer) 
  {
    int nLen = buffer.length;
    long temp;
    for (int nI = 0; nI < nLen; nI +=2) 
    {
      // encrypt a temporary 64bit block
      temp = BinConverter.intArrayToLong(buffer, nI);
      temp = encryptBlockCBC(temp);
      BinConverter.longToIntArray(temp, buffer, nI);
    }
  }



  /**
    * encrypts a long buffer to another long buffer (of the same size or bigger)
    * @param inbuffer buffer with plaintext data
    * @param outbuffer buffer to get the ciphertext data
    */
  public void encrypt(long[] inbuffer, 
                      long[] outbuffer) 
  {
    int nLen = inbuffer.length;
    for (int nI = 0; nI < nLen; nI++)
    {
      outbuffer[nI] = encryptBlockCBC(inbuffer[nI]);
    }
  }



  /**
    * encrypts a long buffer to itself
    * @param buffer buffer to encrypt
    */
  public void encrypt(long[] buffer) 
  {
    int nLen = buffer.length;
    for (int nI = 0; nI < nLen; nI++) 
    {
      buffer[nI] = encryptBlockCBC(buffer[nI]);
    }
  }



  /**
    * decrypts a byte buffer (should be aligned to an 8 byte border) 
    * to another buffer (of the same size or bigger)
    * @param inbuffer buffer with ciphertext data
    * @param outBuffer buffer to get the plaintext data
    */
  public void decrypt(byte[] inbuffer, 
                      byte[] outbuffer) 
  {
    int nLen = inbuffer.length;
    long temp;
    for (int nI = 0; nI < nLen; nI +=8) 
    {
      // decrypt a temporary 64bit block
      temp = BinConverter.byteArrayToLong(inbuffer, nI);
      temp = decryptBlockCBC(temp);
      BinConverter.longToByteArray(temp, outbuffer, nI);
    }
  }



  /**
    * decrypts a byte buffer (should be aligned to an 8 byte border) to itself
    * @param buffer buffer to decrypt
    */
  public void  decrypt(byte[] buffer) 
  {
    int nLen = buffer.length;
    long temp;
    for (int nI = 0; nI < nLen; nI +=8) 
    {
      // decrypt over a temporary 64bit block
      temp = BinConverter.byteArrayToLong(buffer, nI);
      temp = decryptBlockCBC(temp);
      BinConverter.longToByteArray(temp, buffer, nI);
    }
  }




  /**
    * decrypts an integer buffer (should be aligned to an
    * two integer border) to another int buffer (of the same size or bigger)
    * @param inbuffer buffer with ciphertext data
    * @param outbuffer buffer to get the plaintext data
    */
  public void decrypt(int[] inbuffer, 
                      int[] outbuffer) 
  {

    int nLen = inbuffer.length;
    long temp;
    for (int nI = 0; nI < nLen; nI +=2) 
    {
      // decrypt a temporary 64bit block
      temp = BinConverter.intArrayToLong(inbuffer, nI);
      temp = decryptBlockCBC(temp);
      BinConverter.longToIntArray(temp, outbuffer, nI);
    }
  }


  /**
    * decrypts an int buffer (should be aligned to a
    * two integer border) 
    * @param buffer buffer to decrypt
    */
  public void decrypt(int[] buffer) 
  {
    int nLen = buffer.length;
    long temp;
    for (int nI = 0; nI < nLen; nI +=2) 
    {
      // decrypt a temporary 64bit block
      temp = BinConverter.intArrayToLong(buffer, nI);
      temp = decryptBlockCBC(temp);
      BinConverter.longToIntArray(temp, buffer, nI);
    }
  }



  /**
    * decrypts a long buffer to another long buffer (of the same size or bigger)
    * @param inbuffer buffer with ciphertext data
    * @param outbuffer buffer to get the plaintext data
    */
  public void decrypt(long[] inbuffer, 
                      long[] outbuffer) 
  {
    int nLen = inbuffer.length;
    for (int nI = 0; nI < nLen; nI++)
    {
      outbuffer[nI] = decryptBlockCBC(inbuffer[nI]);
    }
  }



  /**
    * decrypts a long buffer to itself
    * @param buffer buffer to decrypt
    */
  public void decrypt(long[] buffer) 
  {
    int nLen = buffer.length;
    for (int nI = 0; nI < nLen; nI++)
    { 
      buffer[nI] = decryptBlockCBC(buffer[nI]);
    }
  }

}   
