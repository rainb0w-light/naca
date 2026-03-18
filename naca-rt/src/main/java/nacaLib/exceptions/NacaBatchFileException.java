/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.exceptions;

import jlib.misc.LogicalFileDescriptor;

public class NacaBatchFileException extends NacaRTException
 {
	private static final long serialVersionUID = 1L;
 	private String csFileName = null;
 	private String csLogicalFileDescriptor = null;
 	private String csExceptionName = null;
 	
 	public NacaBatchFileException(String csExceptionName, String csFileName, LogicalFileDescriptor logicalFileDescriptor)
 	{
 		csExceptionName = csExceptionName;
 		csFileName = csFileName;
 		if(logicalFileDescriptor != null)
 			csLogicalFileDescriptor = logicalFileDescriptor.toString();
 		else
 			csLogicalFileDescriptor = "<EMPTY>";
 	}
 	
 	public String getMessage()
 	{
 		String cs = csExceptionName + "; LogicalName=" + csFileName + "; PhysicalDescription=" + csLogicalFileDescriptor;
 		return cs;
 	}
}
