/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * @author sly
 *
 */
package nacaLib.mapSupport;

public class MapFieldAttrModified extends MapFieldBaseAttr
{
	MapFieldAttrModified(int nValue, String text)
	{
		super(nValue, text);
	}

	static int getNbBitsEncoding()
	{
		return 2;
	}
	static int getMask()
	{
		return 3 ;
	}

//	public String toString()
//	{
//		return getText();
//	}
//
//	public String getText()
//	{
//		if (getInternalValue() == 0)
//		{
//			return "modified";
//		}
//		else if (getInternalValue() == 1)
//		{
//			return "cleared" ;
//		}
//		else
//		{
//			return "unmodified" ;
//		}
//	}


	public static MapFieldAttrModified MODIFIED = new MapFieldAttrModified(1, "modified");
	public static MapFieldAttrModified TO_BE_MODIFIED = new MapFieldAttrModified(1, "toBeModified");
    // Modified + empty field: CTOJ extension
	public static MapFieldAttrModified CLEARED = new MapFieldAttrModified(2, "cleared");
	public static MapFieldAttrModified UNMODIFIED = new MapFieldAttrModified(3, "unmodified");
	/**
	 * @param nValue
	 * @return
	 */
	public static MapFieldAttrModified Select(int nValue)
	{
		if (nValue == MODIFIED.getInternalValue())
		{
			return MODIFIED ;
		}
		else if (nValue == CLEARED.getInternalValue())
		{
			return CLEARED ;
		}
		else if (nValue == UNMODIFIED.getInternalValue())
		{
			return UNMODIFIED ;
		}
		else if (nValue == TO_BE_MODIFIED.getInternalValue())
		{
			return TO_BE_MODIFIED ;
		}
		else
		{
			return null ;
		}
	}

}
