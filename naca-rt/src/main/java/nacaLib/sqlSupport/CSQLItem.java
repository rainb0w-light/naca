/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/*
 * Created on 12 janv. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nacaLib.sqlSupport;

import nacaLib.varEx.VarAndEdit;
import nacaLib.varEx.VarBase;
import nacaLib.varEx.VarEnumerator;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CSQLItem
{	
	protected VarAndEdit var = null;
	protected String csValue = null;

	public CSQLItem(VarAndEdit var)
	{
		var = var;
	}
	public void set(VarAndEdit var)
	{
		var = var;
		csValue = null;
	}


	public CSQLItem(int nValue)
	{
		csValue = String.valueOf(nValue);
	}
	public void set(int nValue)
	{
		var = null;
		csValue = String.valueOf(nValue);
	}

	public CSQLItem(double dValue)
	{
		csValue = String.valueOf(dValue);
	}
	public void set(double dValue)
	{
		var = null;
		csValue = String.valueOf(dValue);
	}

	public CSQLItem(String cs)
	{
		csValue = cs;
	}	
	public void set(String cs)
	{
		var = null;
		csValue = cs;
	}
	
	public String getValue()
	{
		if(var != null)
		{
			if(isLongVarCharVarHolder())
			{
				VarEnumerator e = new VarEnumerator(var.getProgramManager(), var); 
				VarBase varChildLength = e.getFirstVarChild();
				VarBase varChildText = e.getNextVarChild();

				//int nLength = varChildLength.
				int nLength = varChildLength.getInt();
				//String csValue = varChildText.getDottedSignedString();
				String csValue = varChildText.getDottedSignedStringAsSQLCol();
				if(nLength < csValue.length())
					csValue = csValue.substring(0, nLength);
				return csValue;
			}
			return var.getDottedSignedStringAsSQLCol();
		}
		return csValue;
	}
	
	public String getDebugValue()
	{
		String cs = getValue();
		byte t[] = cs.getBytes();
		for(int n=0; n<t.length; n++)
		{
			byte b = t[n];
			if(b == 0)
				t[n] = '$';
		}
		cs = new String(t);
		return cs;
	}
	
	private boolean isLongVarCharVarHolder()	// Indicates if the var contains a long varchar structure
	{
		return var.getVarDef().isLongVarCharVarStructure();
	}

	public CSQLItemType getType()
	{
		if(var != null)
		{
			return var.getSQLType(); 
//			
//			if (var.hasType(VarTypeEnum.Type9))
//			{
//				return CSQLItemType.SQL_TYPE_INTEGER;
//			}
//			else if (var.hasType(VarTypeEnum.TypeX) 
//				|| var.hasType(VarTypeEnum.TypeEditedAlphaNum)
//				|| var.hasType(VarTypeEnum.TypeEditedNum)
//				|| var.hasType(VarTypeEnum.TypeFieldEdit)
//				|| var.hasType(VarTypeEnum.TypeGroup))
//			{
//				return CSQLItemType.SQL_TYPE_STRING ;
//			}
//			else
//			{
//				return CSQLItemType.SQL_TYPE_NONE;
//			}
		}
		return null;
	}

	/**
	 * @return
	 */
	public int getIntValue()
	{
		if(var != null)
			return var.getInt() ;
		return 0;
	}
	
	public long getLongValue()
	{
		if(var != null)
			return var.getLong() ;
		return 0L;
	}
}