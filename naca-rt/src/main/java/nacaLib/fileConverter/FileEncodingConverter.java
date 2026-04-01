/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.fileConverter;

import jlib.misc.FileSystem;
import nacaLib.varEx.FileDescriptor;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: FileEncodingConverter.java,v 1.4 2007/06/09 12:04:22 u930bm Exp $
 */
public class FileEncodingConverter
{
	protected FileDescriptor fileIn = null;
	protected FileDescriptor fileOut = null;
	
	protected boolean ishost = false;
	protected int nLengthRecord = 0;
	protected boolean isvariable4 = false;
	protected boolean isheaderEbcdic = false;

	public FileEncodingConverter(FileDescriptor fileIn, FileDescriptor fileOut)
	{
		fileIn = fileIn;
		fileOut = fileOut;
	}
	
	public void setHost(String csParameter)
	{
		ishost = true;
		String csParameterUpper = csParameter.toUpperCase();
		if (csParameterUpper.indexOf("RECORDLENGTH={") != -1)
		{
			int nPos = csParameterUpper.indexOf("RECORDLENGTH={") + 14;
			int nPosEnd = csParameterUpper.indexOf("}", nPos);
			nLengthRecord = Integer.valueOf(csParameter.substring(nPos, nPosEnd)).intValue();				
		}
		if (csParameterUpper.indexOf("VARIABLE4") != -1)
		{
			isvariable4 = true;
		}
		if (csParameterUpper.indexOf("HEADEREBCDIC") != -1)
		{
			isheaderEbcdic = true;
		}
		System.out.println("FileEncodingConverter: Converting Host file");
		if (nLengthRecord == 0)
			System.out.println("FileEncodingConverter: Length record determined by header");
		else	
			System.out.println("FileEncodingConverter: Length record : " + nLengthRecord);
		if (isheaderEbcdic)
			System.out.println("FileEncodingConverter: Add header ebcdic");
	}

	protected boolean copyFile()
	{
		return FileSystem.copy(fileIn.getPhysicalName(), fileOut.getPhysicalName());
	}
}
