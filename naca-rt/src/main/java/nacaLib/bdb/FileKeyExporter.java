/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package nacaLib.bdb;

import jlib.log.Log;
import jlib.misc.DataFileLineReader;
import jlib.misc.DataFileWrite;
import jlib.misc.LineRead;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: FileKeyExporter.java,v 1.6 2006/08/11 11:52:07 u930di Exp $
 */
public class FileKeyExporter
{
	private DataFileWrite dataFileKeyOut = null;
	private BtreeKeyDescription keyDescription = null;
	
	public FileKeyExporter(String csKeys, String csExportKeyFileOut, boolean bFileInEbcdic)
	{
		if(csExportKeyFileOut != null)
		{
			dataFileKeyOut = new DataFileWrite(csExportKeyFileOut, false);
			boolean bOutKeyOpened = dataFileKeyOut.open();
			if(!bOutKeyOpened)
			{
				dataFileKeyOut = null;
				Log.logImportant("Cannot create output key file " + csExportKeyFileOut);
			}
		}
		
		setKeyDescription(csKeys, bFileInEbcdic);
	}
	
	private void setKeyDescription(String csKeys, boolean bFileInEbcdic)
	{
		keyDescription = new BtreeKeyDescription();
		keyDescription.set(csKeys, false);
		keyDescription.prepare();
		keyDescription.setFileInEncoding(bFileInEbcdic);
	}
	
	public void execute(String csFileIn, int nBufferChunkReadAHead)
	{		
		int nNbRecordRead = 0;
		DataFileLineReader dataFileIn = new DataFileLineReader(csFileIn, nBufferChunkReadAHead, 0);
		boolean bInOpened = dataFileIn.open();
		if(bInOpened)
		{
			// doesn't manage variable length files 
			boolean b = true;
			LineRead lineRead = dataFileIn.readNextUnixLine();
			while(lineRead != null && b == true)
			{
				byte tbKey[] = keyDescription.fillKeyBufferIncludingRecordId(lineRead, false);	//, false);
				dataFileKeyOut.writeWithEOL(tbKey, tbKey.length);

				lineRead = dataFileIn.readNextUnixLine();
				nNbRecordRead++;
			}
			dataFileKeyOut.close();
			dataFileIn.close();
		}		
		Log.logNormal("" + nNbRecordRead + " records read file from " + csFileIn);
	}
}
