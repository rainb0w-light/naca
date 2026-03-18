/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 20 août 04
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements.SQL;

/**
 * @author U930DI
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CSQLTableColDescriptor
{
	public CSQLTableColDescriptor()
	{
	}
	
	public void SetName(String csName)
	{
		csName = csName;
	}
	
	public String GetName()
	{
		return csName;
	}
	
	void SetLength(int n)
	{
		nLength = n;
		bLengthSet = true;
	}	

	void SetDecimal(int n)
	{
		nDecimal = n;
		bDecimalSet = true;
	}
	
	public boolean HasSize()
	{
		return bLengthSet;
	}
	
	public String GetSizes()
	{
		if(bLengthSet)
		{
			if(bDecimalSet)
				return String.valueOf(nLength) + ", " + String.valueOf(nDecimal);
			return String.valueOf(nLength);
		}
		return "";
	}
	
	void SetType(String csType)
	{
		csType = csType;
	}
	
	public String GetType()
	{
		return csType;
	} 
	
	void SetNull(boolean b)
	{
		bNull = b;
	}		
	
	public boolean IsNull()
	{
		return bNull;
	}
	
	private int nLength = 0;
	private int nDecimal = 0;
	private boolean bDecimalSet = false;
	private boolean bLengthSet = false;
	private String csType = "";
	private String csName = "";
	private boolean bNull = false;

}
