/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 23 sept. 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.mapSupport;

import nacaLib.base.*;

import org.w3c.dom.*;

public class MapFieldAttribute extends CJMapObject
{
	private MapFieldAttrProtection protection = null ; //MapFieldAttrProtection.AUTOSKIP;
	private MapFieldAttrIntensity intensity = null ; //MapFieldAttrIntensity.NORMAL;
	private MapFieldAttrModified modified = null ; //MapFieldAttrModified.UNMODIFIED;
	
	// Stand alone field
	private MapFieldAttrColor color = null ; //MapFieldAttrColor.DEFAULT;

	// Stand alone field
	private MapFieldAttrHighlighting highlighting = null ; //MapFieldAttrHighlighting.OFF ;
	
	// Into Encoded bit field
	private MapFieldAttrJustify justify = MapFieldAttrJustify.LEFT;
	private MapFieldAttrFill fill = MapFieldAttrFill.BLANK;
	
	public MapFieldAttribute()
	{
		setJustify(MapFieldAttrJustify.LEFT);
	}
	
	public void resetDefaultValues()
	{
		protection = null ;
		intensity = null ;
		modified = null ;
		color = null ;
		highlighting = null ;
		justify = MapFieldAttrJustify.LEFT;
		fill = MapFieldAttrFill.BLANK;
	}
	
	public void set(MapFieldAttribute att)
	{
		protection = att.protection ;
		intensity = att.intensity ;
		modified = att.modified ;
		color = att.color;
		justify = att.justify;
		fill = att.fill;
		highlighting = att.highlighting;
	}
	
	public MapFieldAttribute duplicate()
	{
		MapFieldAttribute copy = new MapFieldAttribute();
		copy.set(this);
		return copy;
	}
	
	public String getLoggableValue()
	{
		return toString();
	}
	
	public String getSTCheckValue()
	{
		return toString();
	}
	
	public String toString()
	{
		String cs = new String();
		if(protection != null)
			cs += protection.toString() + ";"; 
		if(intensity != null)
			cs += intensity.toString() + ";"; 
		if(modified != null)
			cs += modified.toString() + ";"; 
		if(color != null)
			cs += color.toString() + ";"; 
		if(justify != null)
			cs += justify.toString() + ";"; 
		if(fill != null)
			cs += fill.toString() + ";"; 
		if(highlighting != null)
			cs += highlighting.toString(); 
		return cs;
	}

//	void setLineCol(int nLine, int nCol)	
//	{
//		nCol = nCol;
//		nLine = nLine;
//	}
//	
	public void setProtection(MapFieldAttrProtection Protection)
	{
		protection = Protection;
		if (protection == MapFieldAttrProtection.NUMERIC)
		{
			if (justify == null)
			{
				justify = MapFieldAttrJustify.RIGHT ;
			}
			if (fill == null)
			{
				fill = MapFieldAttrFill.ZERO ;
			}
		}
	}
	
	public MapFieldAttrProtection getProtection()
	{
		return protection;
	}

	// Intensity
	public void setIntensity(MapFieldAttrIntensity Intensity)
	{
		intensity = Intensity;
	}
	
	public MapFieldAttrIntensity getIntensity()
	{
		return intensity;
	} 
	
	// Highlight
	public void setHighlighting(MapFieldAttrHighlighting hl)
	{
		highlighting = hl ;
	}
	
	public MapFieldAttrHighlighting getHighlighting()
	{
		return highlighting; 
	}	
	
	// Attribut modified
	public void setAttrModified(MapFieldAttrModified Modified)
	{
		modified = Modified;
	}

	public MapFieldAttrModified getAttrModified()
	{
		return modified;
	}


	public void setColor(MapFieldAttrColor Color)
	{
		color = Color;
	}
	
	public MapFieldAttrColor getColor()
	{
		return color;
	}
	
	public void exportAllAttributes(Element eEdit)
	{	
		exportColor(eEdit);
		exportIntensity(eEdit);
		exportHighlighting(eEdit);
		exportProtection(eEdit);
		exportModified(eEdit);
	}
	
	private void exportColor(Element eEdit)
	{
		if (color != null)
 		{
			String csFieldColor = color.getText();
			eEdit.setAttribute("color", csFieldColor) ;
 		}
 	}

	private void exportIntensity(Element eEdit)
	{
 		if (intensity != null)
 		{
			String csIntens = intensity.getText();
			eEdit.setAttribute("intensity", csIntens) ;
 		}
	}
	
	private void exportHighlighting(Element eEdit)
	{
		if (highlighting != null)
		{
			String csHL = highlighting.getText();
			eEdit.setAttribute("highlighting", csHL) ;
		}
	}
	 
	private void exportProtection(Element eEdit)
	{
		if (protection != null)
		{
			String csProtect = protection.getText();
			eEdit.setAttribute("protection", csProtect) ;
		}
	}
	
	private void exportModified(Element eEdit)
	{
		if (modified != null)
		{
			if (modified == MapFieldAttrModified.TO_BE_MODIFIED)
			{
				eEdit.setAttribute("modified", "true");
			}
			else
			{
				eEdit.setAttribute("modified", "false");
			}
		}
	}
	




	public void setJustify(MapFieldAttrJustify Justify)
	{
		justify = Justify;
		if (fill == null)
		{
			if (justify == MapFieldAttrJustify.LEFT)
			{
				fill = MapFieldAttrFill.BLANK ;
			}
			else if (justify == MapFieldAttrJustify.RIGHT)
			{
				fill = MapFieldAttrFill.ZERO ;
			}
		}
	}

	public void setFill(MapFieldAttrFill Fill)
	{
		fill = Fill;
		if (justify == null)
		{
			if (fill == MapFieldAttrFill.BLANK)
			{
				justify = MapFieldAttrJustify.LEFT ;
			}
			else if (fill == MapFieldAttrFill.ZERO)
			{
				justify = MapFieldAttrJustify.RIGHT ;
			}
		} 
	}
	
	public boolean isFillZero()
	{
		if(fill != null && fill.isFillZero())
			return true;
		return false;
	}
	
	public boolean isFillBlank()
	{
		if(fill != null)
		{
			if(fill.isFillBlank())
				return true;
			else
				return false;
		}
		return true;	// By default
	} 
	
	public boolean isJustifyRight()
	{
		if(justify != null && justify.isJustifyRight())
			return true;
		return false;
	}
	
	public boolean isJustifyLeft()
	{
		if(justify != null)
		{
			if(justify.isJustifyLeft())
				return true;
			else
				return false;
		}
		return true;	// by default
	}
	
	public int getEncodedValue()
	{
		int nEncodedValue = 0;
		
		nEncodedValue = nEncodedValue << MapFieldAttrProtection.getNbBitsEncoding();
		if (protection != null)
			nEncodedValue |= protection.getBitEncoding();

		nEncodedValue = nEncodedValue << MapFieldAttrIntensity.getNbBitsEncoding();
		if (intensity != null)
			nEncodedValue |= intensity.getBitEncoding();

		nEncodedValue = nEncodedValue << MapFieldAttrModified.getNbBitsEncoding();
		if (modified != null)
			nEncodedValue |= modified.getBitEncoding();
		
		nEncodedValue = nEncodedValue << MapFieldAttrColor.getNbBitsEncoding();
		if (color != null)
			nEncodedValue |= color.getBitEncoding();

		nEncodedValue = nEncodedValue << MapFieldAttrHighlighting.getNbBitsEncoding();
		if (highlighting != null)
			nEncodedValue |= highlighting.getBitEncoding();
				
		nEncodedValue = nEncodedValue << MapFieldAttrJustify.getNbBitsEncoding();
		if (justify != null)
			nEncodedValue |= justify.getBitEncoding();

		nEncodedValue = nEncodedValue << MapFieldAttrFill.getNbBitsEncoding();
		if (fill != null)
			nEncodedValue |= fill.getBitEncoding();
		
		return nEncodedValue;
	}
	
	public void setEncodedValue(int nEncodedValue)
	{
		int nValue;
		
		nValue = nEncodedValue & MapFieldAttrFill.getMask();
		fill = MapFieldAttrFill.Select(nValue);		
		nEncodedValue = nEncodedValue >> MapFieldAttrFill.getNbBitsEncoding();

		nValue = nEncodedValue & MapFieldAttrJustify.getMask();
		justify = MapFieldAttrJustify.Select(nValue);		
		nEncodedValue = nEncodedValue >> MapFieldAttrJustify.getNbBitsEncoding();

		nValue = nEncodedValue & MapFieldAttrHighlighting.getMask();
		highlighting = MapFieldAttrHighlighting.Select(nValue);		
		nEncodedValue = nEncodedValue >> MapFieldAttrHighlighting.getNbBitsEncoding();

		nValue = nEncodedValue & MapFieldAttrColor.getMask();
		color = MapFieldAttrColor.Select(nValue);		
		nEncodedValue = nEncodedValue >> MapFieldAttrColor.getNbBitsEncoding();		

		nValue = nEncodedValue & MapFieldAttrModified.getMask();
		modified = MapFieldAttrModified.Select(nValue);		
		nEncodedValue = nEncodedValue >> MapFieldAttrModified.getNbBitsEncoding();		

		nValue = nEncodedValue & MapFieldAttrIntensity.getMask();
		intensity = MapFieldAttrIntensity.Select(nValue);		
		nEncodedValue = nEncodedValue >> MapFieldAttrIntensity.getNbBitsEncoding();

		nValue = nEncodedValue & MapFieldAttrProtection.getMask();
		protection = MapFieldAttrProtection.Select(nValue);		
		nEncodedValue = nEncodedValue >> MapFieldAttrProtection.getNbBitsEncoding();
	}

	public void initialize()
	{
		protection = null ; //MapFieldAttrProtection.AUTOSKIP;
		intensity = null ; //MapFieldAttrIntensity.NORMAL;
		modified = null ; //MapFieldAttrModified.UNMODIFIED;
		color = null ; //vMapFieldAttrColor.DEFAULT;
		highlighting = null ; //MapFieldAttrHighlighting.OFF ;
//		justify = MapFieldAttrJustify.LEFT;
//		fill = MapFieldAttrFill.BLANK;
	}
}

