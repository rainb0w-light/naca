/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.sqlSupport;

/**
 * @author u930di
 *
 */
public class CSQLItemType
{
	public static CSQLItemType SQL_TYPE_NONE = new CSQLItemType() ;
	public static CSQLItemType SQL_TYPE_STRING = new CSQLItemType() ;
	public static CSQLItemType SQL_TYPE_INTEGER = new CSQLItemType() ;
	public static CSQLItemType SQL_TYPE_LONG_INTEGER = new CSQLItemType() ;
	public static CSQLItemType SQL_TYPE_DOUBLE = new CSQLItemType() ;

	CSQLItemType()
	{
	}
}
