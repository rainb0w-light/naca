/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.blowfish;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;


/**
 * An InputStream that reads from a Blowfish encrypted file.
 * @author Dale Anson (danson@germane-software.com), February, 2002
 */
public class BlowfishInputStream extends InputStream {

   private PushbackInputStream in;
   private String storedPassphrase;

   private BlowfishCBC cbc;
   //private long _iv;

   private byte[] inBuffer;
   private int bytesRead = 0;
   private int bufferIndex = 0;
   private boolean started = false;

   /**
    * @param passphrase the passphrase that was used to encrypt the original data.
    * @param is the input stream from which bytes will be read
    */
   public BlowfishInputStream(String passphrase, InputStream is) {
      storedPassphrase = passphrase;
      in = new PushbackInputStream(new BufferedInputStream(is));

      // hash down the password to a 160bit key
      SHA1 hasher = new SHA1();
      hasher.update(storedPassphrase);
      hasher.finalize();

      // setup the encryptor (use a dummy IV)
      cbc = new BlowfishCBC(hasher.getDigest(), 0);
      hasher.clear();

      // create the input buffer
      inBuffer = new byte[BlowfishCBC.BLOCKSIZE];
   }

   /**
    * Reads the next byte of data from this input stream. The value byte is returned
    * as an int in the range 0 to 255. If no byte is available because the end of the
    * stream has been reached, the value -1 is returned. This method blocks until
    * input data is available, the end of the stream is detected, or an exception is
    * thrown.
    * @return the next byte of data or -1 if the end of the stream has been reached.
    */
   public int read() throws IOException {
      if ( !started ) {
         decryptBuffer();     // load the iv
         if ( bytesRead < BlowfishCBC.BLOCKSIZE ) {
            return -1;
         }
         decryptBuffer();     // load the input buffer
         if ( bytesRead == -1 ) {
            return -1;
         }
         bufferIndex = 0;
      }

      // check that all bytes from input stream have been returned
      if ( bytesRead < inBuffer.length && bufferIndex == bytesRead ) {
         return -1;
      }

      // check if all bytes in buffer have been returned, if so,
      // need to refill the buffer
      if ( bufferIndex == bytesRead ) {
         decryptBuffer();
         if ( bytesRead == -1 ) {
            return -1;
         }
         bufferIndex = 0;
      }

      // return the next byte from the buffer
      int rtn = inBuffer[bufferIndex] & 0xff;
      ++bufferIndex;
      return rtn;
   }

   /**
    * Reads enough bytes from the underlying input stream to decrypt, and then
    * decrypts it.
    */
   private void decryptBuffer() throws IOException {
      bytesRead = in.read(inBuffer, 0, inBuffer.length);
      if ( bytesRead == -1 ) {
         return;
      }

      if ( !started ) {
          // did the entire CBC IV get read?
          if (bytesRead < inBuffer.length) {
              return;
          }
         // set the CBC IV, it is the first 8 bytes of the input stream
         long iv = BinConverter.byteArrayToLong(inBuffer, 0);
         cbc.setCBCIV(iv);
         started = true;
         return;
      }

      // decrypt the buffer
      cbc.decrypt(inBuffer);

      // check for last block -- if the original data did not fit exactly into
      // an 8 byte block, the block was padded with enough bytes to fill the
      // block, then encrypted. The last byte is ALWAYS the number of pad bytes,
      // that means the last block could be eight 8's, which is all padding.
      int end = in.read();
      if ( end == -1 ) {
         // all done
         int padCount = inBuffer[inBuffer.length - 1];
         if ( padCount > inBuffer.length || padCount < 1 ) {
            // the last byte wasn't a number, so it must be good data
            return;
         }
         else {
            // adjust bytes read to reflect the number of 'good' bytes
            bytesRead = inBuffer.length - padCount;
            if ( bytesRead == 0 ) {
               bytesRead = -1;
            }
         }
      }
      else {
         in.unread(end);
      }
   }

   /**
    * @see java.io.InputStream
    */
   public boolean markSupported() {
      return in.markSupported();
   }

   /**
    * @see java.io.InputStream
    */
   public void mark(int readlimit) {
      in.mark(readlimit);
   }

   /**
    * @see java.io.InputStream
    */
   public int available() throws IOException {
      return in.available();
   }

   /**
    * @see java.io.InputStream
    */
   public void close() throws IOException {
      in.close();
      cbc.cleanUp();
      return;
   }
}
