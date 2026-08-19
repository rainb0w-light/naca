/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import nacaLib.mapSupport.LocalizedString;
import nacaLib.mapSupport.MapFieldAttrColor;
import nacaLib.mapSupport.MapFieldAttrFill;
import nacaLib.mapSupport.MapFieldAttrHighlighting;
import nacaLib.mapSupport.MapFieldAttrIntensity;
import nacaLib.mapSupport.MapFieldAttrJustify;
import nacaLib.mapSupport.MapFieldAttrModified;
import nacaLib.mapSupport.MapFieldAttrProtection;
import nacaLib.mapSupport.MapFieldAttribute;
import nacaLib.mapSupport.MapFieldFlag;

/**
 * @author PJD
 *
 */
public class EditAttributManager
{
    EditAttributManager()
    {
        int n = 0;
    }

    void allocAttributes(DeclareTypeEditInMap declareTypeEdit)
    {
        mapFieldAttribute = declareTypeEdit.mapFieldAttribute.duplicate();
        localizedString = declareTypeEdit.localizedString;
        setCursor(declareTypeEdit.ishasCursor);
        csDevelopableMark = declareTypeEdit.csDevelopableMark;
        csFormat = declareTypeEdit.csFormat;
        //csSemanticContext = declareTypeEdit.csSemanticContextValue;
    }

    void initialize()
    {
        ishasCursor = false ;
        mapFieldAttribute.initialize();
        flag.reset() ;
    }

    void copyInto(EditAttributManager attrManagerDest)
    {
        attrManagerDest.mapFieldAttribute = mapFieldAttribute.duplicate();
        attrManagerDest.localizedString = localizedString;  // Not copied; keep the original value as it is never modified
        attrManagerDest.ishasCursor = ishasCursor;
        attrManagerDest.csDevelopableMark = csDevelopableMark;
        attrManagerDest.csFormat = csFormat;
        attrManagerDest.flag = flag.duplicate();
//      if(csSemanticContext != null)
//          attrManagerDest.csSemanticContext = new String(csSemanticContext);
//      else
//          attrManagerDest.csSemanticContext = null;
    }

    /** Returns a string representation of this value. */
    public String toString()
    {
        String cs;
        if (mapFieldAttribute != null) {
            cs = mapFieldAttribute.getLoggableValue();
        } else {
            cs = "NoMapFieldAtribute ";
        }
        return cs;
    }

    /** Executes the attrib operation. */
    public void attrib(MapFieldAttrModified modified)
    {
        setModified(modified);
    }

    /** Sets the modified. */
    public void setModified(MapFieldAttrModified modified)
    {
        mapFieldAttribute.setAttrModified(modified);
    }

    // Color
    /** Executes the color operation. */
    public void color(MapFieldAttrColor color)
    {
        mapFieldAttribute.setColor(color) ;
    }

    // Highligth
    public MapFieldAttrHighlighting getHighlighting()
    {
        return mapFieldAttribute.getHighlighting();
    }

    /** Executes the high lighting operation. */
    public void highLighting(MapFieldAttrHighlighting hl)
    {
        mapFieldAttribute.setHighlighting(hl) ;
    }

    /** Executes the intensity operation. */
    public void intensity(MapFieldAttrIntensity intensity)
    {
        mapFieldAttribute.setIntensity(intensity);

    }

    /** Executes the protection operation. */
    public void protection(MapFieldAttrProtection protection)
    {
        mapFieldAttribute.setProtection(protection);
    }

    /** Sets the modified. */
    public void setModified()
    {
        mapFieldAttribute.setAttrModified(MapFieldAttrModified.MODIFIED);
    }

    /** Sets the unmodified. */
    public void setUnmodified()
    {
        mapFieldAttribute.setAttrModified(MapFieldAttrModified.UNMODIFIED);
    }

    /** Sets the cleared. */
    public void setCleared()
    {
        mapFieldAttribute.setAttrModified(MapFieldAttrModified.CLEARED);
    }

    /** Returns whether modified. */
    public boolean isModified()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrModified attrModified = mapFieldAttribute.getAttrModified();
            return attrModified == MapFieldAttrModified.MODIFIED  || attrModified == MapFieldAttrModified.TO_BE_MODIFIED;
        }
        return false ;
    }


    /** Returns whether unmodified. */
    public boolean isUnmodified()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrModified attrModified = mapFieldAttribute.getAttrModified();
            return attrModified == MapFieldAttrModified.UNMODIFIED ;
        }
        return false ;
    }

    /** Returns whether cleared. */
    public boolean isCleared()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrModified attrModified = mapFieldAttribute.getAttrModified();
            return attrModified == MapFieldAttrModified.CLEARED ;
        }
        return false ;
    }

    /** Executes the justify operation. */
    public void justify(MapFieldAttrJustify justify)
    {
        mapFieldAttribute.setJustify(justify) ;
    }

    /** Executes the justify fill operation. */
    public void justifyFill(MapFieldAttrFill fill)
    {
        mapFieldAttribute.setFill(fill) ;
    }


    /** Sets the flag. */
    public void setFlag(String cs)
    {
        if (flag == null) {
            flag = new MapFieldFlag();
        }
        flag.set(cs);
    }

    /** Resets the flag. */
    public void resetFlag()
    {
        if (flag == null) {
            flag = new MapFieldFlag();
        }
        flag.reset();
    }

    /** Returns whether flag. */
    public boolean isFlag(String cs)
    {
        if (flag != null) {
            return flag.isFlag(cs);
        }
        return false;
    }

    // Protection
    /** Returns whether auto skip. */
    public boolean isAutoSkip()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrProtection attrProtection = mapFieldAttribute.getProtection();
            return attrProtection == MapFieldAttrProtection.AUTOSKIP;
        }
        return false ;
    }

    /** Returns whether dark. */
    public boolean isDark()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrIntensity attr = mapFieldAttribute.getIntensity();
            return attr == MapFieldAttrIntensity.DARK;
        }
        return false ;
    }

    /** Returns whether protected. */
    public boolean isProtected()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrProtection attrProtection = mapFieldAttribute.getProtection();
            return attrProtection == MapFieldAttrProtection.PROTECTED;
        }
        return false ;
    }

    /** Returns whether numeric protected. */
    public boolean isNumericProtected()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrProtection attrProtection = mapFieldAttribute.getProtection();
            return attrProtection == MapFieldAttrProtection.NUMERIC;
        }
        return false ;
    }

    /** Returns whether unprotected. */
    public boolean isUnprotected()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrProtection attrProtection = mapFieldAttribute.getProtection();
            return attrProtection == MapFieldAttrProtection.UNPROTECTED;
        }
        return false ;
    }

    /** Returns whether colored. */
    public boolean isColored(MapFieldAttrColor col)
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrColor color = mapFieldAttribute.getColor();
            return color == col ;
        }
        return false ;
    }

    /** Returns whether underlined. */
    public boolean isUnderlined()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrHighlighting highlighting = mapFieldAttribute.getHighlighting();
            return highlighting == MapFieldAttrHighlighting.UNDERLINE ;
        }
        return false ;
    }

    /** Returns whether reverse. */
    public boolean isReverse()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrHighlighting highlighting = mapFieldAttribute.getHighlighting();
            return highlighting == MapFieldAttrHighlighting.REVERSE ;
        }
        return false ;
    }

    /** Executes the is attribute operation. */
    public boolean IsAttribute(MapFieldAttrIntensity intensity)
    {
        if (mapFieldAttribute != null)
        {
            return mapFieldAttribute.getIntensity() == intensity;
        }
        return false ;
    }

    /** Executes the is attribute operation. */
    public boolean IsAttribute(MapFieldAttrProtection protection)
    {
        if (mapFieldAttribute != null)
        {
            return mapFieldAttribute.getProtection() == protection;
        }
        return false ;
    }
    /** Executes the is highlighting operation. */
    public boolean IsHighlighting(MapFieldAttrHighlighting highlighting)
    {
        if (mapFieldAttribute != null)
        {
            return mapFieldAttribute.getHighlighting() == highlighting;
        }
        return false ;
    }

    public MapFieldAttribute getAttribute()
    {
        return mapFieldAttribute ;
    }

    /** Sets the attribute. */
    public void setAttribute(MapFieldAttribute att)
    {
        mapFieldAttribute.set(att) ;
    }

    /** Returns the encoded attr. */
    public int getEncodedAttr()
    {
        int n = mapFieldAttribute.getEncodedValue();
        return n;
    }

    /** Sets the encoded attr. */
    public void setEncodedAttr(int n)
    {
        mapFieldAttribute.setEncodedValue(n);
    }

    public void setCursor(boolean b)
    {
        ishasCursor = b;
    }

    /** Returns whether s cursor. */
    public boolean hasCursor()
    {
        return ishasCursor;
    }

    /** Returns the flag. */
    public String getFlag()
    {
        if (flag != null) {
            return flag.get();
        }
        return "" ;
    }

        /**
     * @return
     */
    public boolean isFlagSet()
    {
        return flag != null && flag.isSet() ;
    }

    /**
     * @return
     */
    public MapFieldAttrColor getColor()
    {
        return mapFieldAttribute.getColor() ;
    }


    /** Returns whether highlight normal. */
    public boolean isHighlightNormal()
    {
        if (mapFieldAttribute != null)
        {
            MapFieldAttrHighlighting highlighting = mapFieldAttribute.getHighlighting();
            return highlighting == MapFieldAttrHighlighting.OFF ;
        }
        return true ;
    }

    /** Sets the attributes. */
    public void setAttributes(int n)
    {
    }

    MapFieldAttribute getMapFieldAttribute()
    {
        return mapFieldAttribute;
    }

    int getAttributeEncodedValue()  // Will use 4 char position
    {
        return mapFieldAttribute.getEncodedValue(); // Will use 4 char position
    }

    void setAttributeEncodedValue(int nAttrEncoded) // Will use 4 char position
    {
        mapFieldAttribute.setEncodedValue(nAttrEncoded);
    }


    char getEncodedFlag()   // Will use 4 char position
    {
        char flag = this.flag.getEncodedValue();    // Will use 1 char
        return flag;
    }

    void setEncodedFlag(char cFlag) // Will use 4 char position
    {
        flag.setEncodedValue(cFlag);
    }

    void setDevelopableMark(String cs)
    {
        csDevelopableMark = cs;
    }

    void setFormat(String cs)
    {
        csFormat = cs;
    }

    LocalizedString getLocalizedString()
    {
        return localizedString;
    }


    boolean isFillBlank()
    {
        return mapFieldAttribute.isFillBlank();
    }

    boolean isFillZero()
    {
        return mapFieldAttribute.isFillZero();
    }

    boolean isJustifyLeft()
    {
        return mapFieldAttribute.isJustifyLeft();
    }

    boolean isJustifyRight()
    {
        return mapFieldAttribute.isJustifyRight();
    }


    MapFieldAttribute mapFieldAttribute = null;
    MapFieldFlag flag = new MapFieldFlag();
    LocalizedString localizedString = null;     // Encoded in commarea
    String csDevelopableMark = null;
    String csFormat = null;
    boolean ishasCursor = false ;
    //String csSemanticContext = null;
}
