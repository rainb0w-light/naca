/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.blowfish;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;


/**
 * An OutputStream that encrypts data using the Blowfish algorithm.
 * 30 Mar 2002, fixed bug in flush method
 * @author Dale Anson (danson@germane-software.com), February, 2002
 */
public class BlowfishOutputStream extends OutputStream {

   private OutputStream out;
   private String storedPassphrase;

   private BlowfishCBC cbc;
   private long iv;

   private byte[] inBuffer;
   private byte [] outBuffer;
   private int bytesInBuffer = 0;
   private boolean started = false;

   /**
    * @param passphrase the password to use to encrypt the data
    * @param os the OutputStream to write the data to
    */
   public BlowfishOutputStream( String passphrase, OutputStream os ) {
      storedPassphrase = passphrase;
      out = os;

      // swiped from BlowfishEasy
      // hash down the password to a 160bit key
      SHA1 hasher = new SHA1();
      hasher.update( storedPassphrase );
      hasher.finalize();

      // setup the encryptor (use a dummy IV)
      cbc = new BlowfishCBC( hasher.getDigest(), 0 );
      hasher.clear();

      iv = new Random().nextLong();
      inBuffer = new byte[ BlowfishCBC.BLOCKSIZE ];
      outBuffer = new byte[ BlowfishCBC.BLOCKSIZE ];
   }

   /**
    * Writes the specified byte to this output stream. The general contract for write
    * is that one byte is written to the output stream. The byte to be written is
    * the eight low-order bits of the argument b. The 24 high-order bits of b are
    * ignored.
    * @param b the byte to write
    */
   public void write( int b ) throws IOException {
      // make sure the iv is written to output stream -- this is always the
      // first 8 bytes written out.
      if ( !started ) {
         byte[] ivBytes = new byte[ BlowfishCBC.BLOCKSIZE ];
         BinConverter.longToByteArray( iv, ivBytes, 0 );
         out.write( ivBytes, 0, ivBytes.length );
         cbc.setCBCIV( iv );
         started = true;
      }

      // if buffer isn't full, just store the input
      ++bytesInBuffer;
      if ( bytesInBuffer < inBuffer.length ) {
         inBuffer[ bytesInBuffer - 1 ] = ( byte ) b;
         return ;
      }

      // else this input will fill the buffer
      inBuffer[ bytesInBuffer - 1 ] = ( byte ) b;
      bytesInBuffer = 0;

      // encrypt the buffer
      cbc.encrypt( inBuffer, outBuffer );

      // write the out_buffer to the wrapped output stream
      for ( int i = 0; i < outBuffer.length; i++ ) {
         out.write( outBuffer[ i ] );
      }
      return ;
   }

   /**
    * This method calls flush(), so there is no need to call both.
    * @see java.io.InputStream
    */
   public void close() throws IOException {
      // This output stream always writes out even blocks of 8 bytes. If it
      // happens that the last block does not have 8 bytes, then the block will
      // be padded to have 8 bytes.
      // The last byte is ALWAYS the number of pad bytes and will ALWAYS be a
      // number between 1 and 8, inclusive. If this means adding
      // an extra block just for the pad count, then so be it.
      // Minor correction: 8 isn't the magic number, rather it's BlowfishECB.BLOCKSIZE.
      byte padVal = ( byte ) ( inBuffer.length - bytesInBuffer );
      if ( padVal > 0 ) {
         while ( bytesInBuffer < inBuffer.length ) {
            inBuffer[ bytesInBuffer ] = padVal;
            ++ bytesInBuffer;
         }
         // encrypt the buffer
         cbc.encrypt( inBuffer, outBuffer );
         // write the out_buffer to the wrapped output stream
         for ( int i = 0; i < outBuffer.length; i++ ) {
            out.write( outBuffer[ i ] );
         }
      }
      flush();
      out.close();
      cbc.cleanUp();
      return ;
   }

   /**
    * Flushes this output stream and causes any buffered bytes to be written.
    */
   public void flush() throws IOException {
      out.flush();
      return ;
   }
}
