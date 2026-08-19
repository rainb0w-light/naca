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

/** Provides map field attr protection behavior. */
public class MapFieldAttrProtection extends MapFieldBaseAttr
{
    private MapFieldAttrProtection(int nValue, String text)
    {
        super(nValue, text);
    }

    static int getNbBitsEncoding()
    {
        return 3;
    }

    static int getMask()
    {
        return 7;
    }

//  public String toString()
//  {
//      return getText();
//  }
//
//  String getText()
//  {
//      if(getInternalValue() == 0)
//          return "autoskip";
//      else if(getInternalValue() == 1)
//          return "unprotected";
//      else if(getInternalValue() == 2)
//          return "numeric";
//      return "protected";
//  }

    // Encoded in 2 bits
    public static MapFieldAttrProtection AUTOSKIP = new MapFieldAttrProtection(1, "autoskip");
    public static MapFieldAttrProtection UNPROTECTED = new MapFieldAttrProtection(2, "unprotected");
    public static MapFieldAttrProtection NUMERIC = new MapFieldAttrProtection(3, "numeric");
    public static MapFieldAttrProtection PROTECTED = new MapFieldAttrProtection(4, "protected");
    /**
     * @param nValue
     * @return
     */
    public static MapFieldAttrProtection Select(int nValue)
    {
        if (nValue == UNPROTECTED.getInternalValue())
        {
            return UNPROTECTED ;
        }
        else if (nValue == NUMERIC.getInternalValue())
        {
            return NUMERIC ;
        }
        else if (nValue == PROTECTED.getInternalValue())
        {
            return PROTECTED ;
        }
        else if (nValue == AUTOSKIP.getInternalValue())
        {
            return AUTOSKIP ;
        }
        else
        {
            return null ;
        }
    }
}
