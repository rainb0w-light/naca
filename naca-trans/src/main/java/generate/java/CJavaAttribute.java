/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 2 ao�t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate.java;

import generate.*;
import semantic.*;
import utils.*;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CJavaAttribute extends CEntityAttribute
{

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#ignore()
	 */
	public boolean ignore()
	{
		boolean b = super.ignore();
		if (b)
		{
			int n=0 ;
			super.ignore();
		}
		return b;
	}
	/**
	 * @param name
	 * @param cat
	 */
	public CJavaAttribute(int l, String name, CObjectCatalog cat, CBaseLanguageExporter out)
	{
		super(l, name, cat, out);
	}
	
	protected void DoExport()
	{
		if (isblankWhenZero && type.equals("pic9"))
		{
			type = "pic";
			format = "";
			for (int i=0; i < length; i++)
				format += "9";
			if (decimals>0)
			{
				format += ".";
				for (int i=0; i < decimals; i++)
					format += "9";
			}
		}
		String line = "Var " + FormatIdentifier(GetName()) + " = declare.level(77)" ;
		line += "." + type + "(" ;
		if (format.equals(""))
		{
			if (length > 0 || decimals > 0)
			{
				line += length ;
				if (decimals > 0)
				{
					line += "," + decimals ;
				}
			}
		}
		else
		{
			line += "\"" + format + "\"" ;
		}
		line += ")" ;
		if (!comp.equals(""))
		{
			if (comp.equalsIgnoreCase("Comp3"))
			{
				line += ".comp3()" ;
			}
			else if (comp.equalsIgnoreCase("Comp4"))
			{
				line += ".comp()" ;
			}
			else if (comp.equalsIgnoreCase("Comp"))
			{
				line += ".comp()" ;
			}
			else if (comp.equalsIgnoreCase("Comp2"))
			{
				line += ".comp2()" ;
			}
		}
		WriteWord(line) ;
		if (issync)
		{
			WriteWord(".sync()");
		}
		if (value != null)
		{
			String cs = "" ;
			if (isfillWithValue)
			{
				cs = ".valueAll(" ;
			}
			else
			{
				cs = ".value(" ;
			}
			cs += value.ExportReference(getLine());
			WriteWord(cs + ")") ;
		}
		else if (isinitialValueIsSpaces)
		{
			WriteWord(".valueSpaces()") ;
		}
		else if (isinitialValueIsZeros)
		{
			WriteWord(".valueZero()") ;
		}
		else if (isinitialValueIsLowValue)
		{
			WriteWord(".valueLowValue()") ;
		}
		else if (isinitialValueIsHighValue)
		{
			WriteWord(".valueHighValue()") ;
		}
		if (isjustifiedRight)
		{
			WriteWord(".justifyRight()");
		}
		if (isblankWhenZero)
		{
			WriteWord(".blankWhenZero()");
		}
		WriteWord(".var() ;") ;
		WriteEOL() ;
		StartOutputBloc() ;
		ExportChildren();
		EndOutputBloc();
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#ExportReference(semantic.CBaseExporter)
	 */
	public String ExportReference(int nLine)
	{
		String cs = "" ;
		if (of != null)
		{
			cs = of.ExportReference(getLine()) + "." ;
		}
		cs += FormatIdentifier(GetName()) ;
		return cs ;		
	}
	public boolean HasAccessors()
	{
		return false;
	}
	public String ExportWriteAccessorTo(String value)
	{
		// unsued
		return "" ;
	}

	public boolean isValNeeded()
	{
		return true;
	}


	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetDataType()
	 */
	public CDataEntityType GetDataType()
	{
		if (type.equals("picS9") || type.equals("pic9"))
		{
			return CDataEntityType.NUMERIC_VAR ;
		}
		else
		{
			return CDataEntityType.VAR ;
		}
	}

}
