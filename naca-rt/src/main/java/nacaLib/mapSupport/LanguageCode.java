/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.mapSupport;

import nacaLib.base.CJMapObject;

/**
 * @author U930CV
 *
 */
public class LanguageCode extends CJMapObject
{
	public static String FR = "FR" ;
	public static String DE = "DE" ;
	public static String IT = "IT" ;
	public static String EN = "EN" ;
	public static String getLanguageCode(int n)
	{
		switch (n)
		{
			case 1:
				return DE ;
			case 2:
				return FR ;
			case 3:
				return IT ;
			case 4:
				return EN ;
			default:
				return null ;
		}
	}
}
