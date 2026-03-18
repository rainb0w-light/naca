/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
/**
 * 
 */
package jlib.sql;

import java.sql.Types;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: ColDescriptionInfo.java,v 1.1 2006/08/30 15:31:22 u930di Exp $
 */
public class ColDescriptionInfo
{
	public String csColName = null;
	public int nTypeId = 0;
	public int nPrecision = 0;
	public int nScale = 0;
	
	public ColDescriptionInfo()
	{
	}
	
	String getColName()
	{
		return csColName;
	}
	
	public int getPrecision()
	{
		return nPrecision;
	}

	public int getScale()
	{
		return nScale;
	}
	
	public BaseDbColDefinition makeDbColDefinition()
	{
		BaseDbColDefinition dbColDef = null;
		if(nTypeId == Types.CHAR)
		{
			dbColDef = new DbColDefinitionChar(this);
		}
		else if(nTypeId == Types.DECIMAL)	// Unsupported type in jlib, but supported in nacaRT
		{
			dbColDef = new DbColDefinitionDecimal(this);
		}			
		else if(nTypeId == Types.TIME)
		{
			dbColDef = new DbColDefinitionTime(this);
		}
		else if(nTypeId == Types.TIMESTAMP)
		{
			dbColDef = new DbColDefinitionTimestamp(this);
		}
		else if(nTypeId == Types.DATE)
		{
			dbColDef = new DbColDefinitionDate(this);
		}
		else if(nTypeId == Types.VARCHAR)
		{
			dbColDef = new DbColDefinitionVarchar(this);
		}
		else if(nTypeId == Types.LONGVARCHAR)
		{
			dbColDef = new DbColDefinitionLongVarchar(this);
		}
		else if(nTypeId == Types.SMALLINT)
		{
			dbColDef = new DbColDefinitionSmallint(this);
		}
		else if(nTypeId == Types.INTEGER)
		{
			dbColDef = new DbColDefinitionInteger(this);
		}
		else if(nTypeId == Types.DOUBLE)
		{
			dbColDef = new DbColDefinitionDouble(this);
		}

		return dbColDef;
	}
	
	public String toString()
	{
		return csColName + ";" + nTypeId + ";" + nPrecision + ";" + nScale; 
	}
	
}
