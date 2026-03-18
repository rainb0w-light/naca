/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 14 avr. 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package nacaLib.varEx;

import nacaLib.mapSupport.*;

/**
 * @author PJD
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
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
		setCursor(declareTypeEdit.bHasCursor);
		csDevelopableMark = declareTypeEdit.csDevelopableMark;
		csFormat = declareTypeEdit.csFormat;
		//csSemanticContext = declareTypeEdit.csSemanticContextValue;
	}
	
	void initialize()
	{
		bHasCursor = false ;
		mapFieldAttribute.initialize();
		flag.reset() ;
	}
	
	void copyInto(EditAttributManager attrManagerDest)
	{
		attrManagerDest.mapFieldAttribute = mapFieldAttribute.duplicate();
		attrManagerDest.localizedString = localizedString;	// Not copied; keep the original value as it is never modified
		attrManagerDest.bHasCursor = bHasCursor;
		attrManagerDest.csDevelopableMark = csDevelopableMark;
		attrManagerDest.csFormat = csFormat;
		attrManagerDest.flag = flag.duplicate();
//		if(csSemanticContext != null)
//			attrManagerDest.csSemanticContext = new String(csSemanticContext);
//		else
//			attrManagerDest.csSemanticContext = null;
	}

	public String toString()
	{
		String cs;
		if(mapFieldAttribute != null)
			cs = mapFieldAttribute.getLoggableValue();
		else
			cs = "NoMapFieldAtribute ";
		return cs;
	}
	
	public void attrib(MapFieldAttrModified Modified)
	{
		setModified(Modified);
	}
	
	public void setModified(MapFieldAttrModified Modified)
	{
		mapFieldAttribute.setAttrModified(Modified);
	}

	// Color
	public void color(MapFieldAttrColor color)
	{
		mapFieldAttribute.setColor(color) ;
	}

	// Highligth
	public MapFieldAttrHighlighting getHighlighting()
	{
		return mapFieldAttribute.getHighlighting();
	}
	
	public void highLighting(MapFieldAttrHighlighting hl)
	{
		mapFieldAttribute.setHighlighting(hl) ;
	}
	
	public void intensity(MapFieldAttrIntensity intensity)
	{
		mapFieldAttribute.setIntensity(intensity);
	
	}
		
	public void protection(MapFieldAttrProtection protection)
	{
		mapFieldAttribute.setProtection(protection);
	}

	public void setModified()
	{
		mapFieldAttribute.setAttrModified(MapFieldAttrModified.MODIFIED);
	}
	
	public void setUnmodified()
	{
		mapFieldAttribute.setAttrModified(MapFieldAttrModified.UNMODIFIED);
	}

	public void setCleared()
	{
		mapFieldAttribute.setAttrModified(MapFieldAttrModified.CLEARED);
	}
	
	public boolean isModified()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrModified attrModified = mapFieldAttribute.getAttrModified();
			return attrModified == MapFieldAttrModified.MODIFIED  || attrModified == MapFieldAttrModified.TO_BE_MODIFIED;
		}
		return false ;
	}
	
	
	public boolean isUnmodified()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrModified attrModified = mapFieldAttribute.getAttrModified();
			return attrModified == MapFieldAttrModified.UNMODIFIED ;
		}
		return false ;
	}	
	
	public boolean isCleared()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrModified attrModified = mapFieldAttribute.getAttrModified();
			return attrModified == MapFieldAttrModified.CLEARED ;
		}
		return false ;
	}	
		
	public void justify(MapFieldAttrJustify justify)
	{
		mapFieldAttribute.setJustify(justify) ;
	}
	
	public void justifyFill(MapFieldAttrFill fill)
	{
		mapFieldAttribute.setFill(fill) ;
	}
	
		
	public void setFlag(String cs)
	{
		if(flag == null)
			flag = new MapFieldFlag();
		flag.set(cs);		
	}

	public void resetFlag()
	{
		if(flag == null)
			flag = new MapFieldFlag();
		flag.reset();		
	}

	public boolean isFlag(String cs)
	{
		if(flag != null)
			return flag.isFlag(cs);
		return false;
	}

	// Protection
	public boolean isAutoSkip()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrProtection attrProtection = mapFieldAttribute.getProtection();
			return attrProtection == MapFieldAttrProtection.AUTOSKIP;
		}
		return false ;
	}
	
	public boolean isDark()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrIntensity attr = mapFieldAttribute.getIntensity();
			return attr == MapFieldAttrIntensity.DARK;
		}
		return false ;
	}
	
	public boolean isProtected()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrProtection attrProtection = mapFieldAttribute.getProtection();
			return attrProtection == MapFieldAttrProtection.PROTECTED;
		}
		return false ;
	}
	
	public boolean isNumericProtected()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrProtection attrProtection = mapFieldAttribute.getProtection();
			return attrProtection == MapFieldAttrProtection.NUMERIC;
		}
		return false ;
	}

	public boolean isUnprotected()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrProtection attrProtection = mapFieldAttribute.getProtection();
			return attrProtection == MapFieldAttrProtection.UNPROTECTED;
		}
		return false ;
	}
		
	public boolean isColored(MapFieldAttrColor col)
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrColor color = mapFieldAttribute.getColor();
			return color == col ;
		}
		return false ;
	}

	public boolean isUnderlined()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrHighlighting highlighting = mapFieldAttribute.getHighlighting();
			return highlighting == MapFieldAttrHighlighting.UNDERLINE ;
		}
		return false ;
	}

	public boolean isReverse()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrHighlighting highlighting = mapFieldAttribute.getHighlighting();
			return highlighting == MapFieldAttrHighlighting.REVERSE ;
		}
		return false ;
	}
	
	public boolean IsAttribute(MapFieldAttrIntensity intensity)
	{
		if (mapFieldAttribute != null)
		{
			return mapFieldAttribute.getIntensity() == intensity;
		}
		return false ;
	}

	public boolean IsAttribute(MapFieldAttrProtection protection)
	{
		if (mapFieldAttribute != null)
		{
			return mapFieldAttribute.getProtection() == protection;
		}
		return false ;
	}
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
	
	public void setAttribute(MapFieldAttribute att)
	{
		mapFieldAttribute.set(att) ;
	}

	public int getEncodedAttr()
	{
		int n = mapFieldAttribute.getEncodedValue();
		return n;
	}
	
	public void setEncodedAttr(int n)
	{
		mapFieldAttribute.setEncodedValue(n);
	}
	
	public void setCursor(boolean b)
	{
		bHasCursor = b;	
	}
	
	public boolean hasCursor()
	{
		return bHasCursor ;
	}

	public String getFlag()
	{
		if(flag != null)
			return flag.get();
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

	
	public boolean isHighlightNormal()
	{
		if (mapFieldAttribute != null)
		{
			MapFieldAttrHighlighting highlighting = mapFieldAttribute.getHighlighting();
			return highlighting == MapFieldAttrHighlighting.OFF ;
		}
		return true ;
	}
	
	public void setAttributes(int n)
	{
	}
	
	MapFieldAttribute getMapFieldAttribute()
	{
		return mapFieldAttribute;
	}
	
	int getAttributeEncodedValue()	// Will use 4 char position
	{
		return mapFieldAttribute.getEncodedValue();	// Will use 4 char position
	}
	
	void setAttributeEncodedValue(int nAttrEncoded)	// Will use 4 char position
	{
		mapFieldAttribute.setEncodedValue(nAttrEncoded);
	}

	
	char getEncodedFlag()	// Will use 4 char position
	{
		char cFlag = flag.getEncodedValue();	// Will use 1 char
		return cFlag;
	}
	
	void setEncodedFlag(char cFlag)	// Will use 4 char position
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
	LocalizedString localizedString = null;		// Encoded in commarea
	String csDevelopableMark = null;
	String csFormat = null;
	boolean bHasCursor = false ;
	//String csSemanticContext = null;
}
