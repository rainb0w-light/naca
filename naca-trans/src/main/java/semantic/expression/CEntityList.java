/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 23 mars 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package semantic.expression;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CEntityList extends CDataEntity
{

	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public CEntityList(String name, CObjectCatalog cat)
	{
		super(0, name, cat);
	}

	/* (non-Javadoc)
	 * @see semantic.CDataEntity#GetDataType()
	 */
	public CDataEntityType GetDataType()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see semantic.CDataEntity#HasAccessors()
	 */
	public boolean HasAccessors()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see semantic.CDataEntity#ExportWriteAccessorTo(java.lang.String)
	 */
	public String ExportWriteAccessorTo(String value)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see semantic.CDataEntity#GetConstantValue()
	 */
	public String GetConstantValue()
	{
		return null;
	}
	
	public void AddData(CDataEntity e)
	{
		data.add(e) ;
	}

	public List<CDataEntity> getData()
	{
		return Collections.unmodifiableList(data);
	}
	public boolean isEmpty()
	{
		return data.isEmpty();
	}
	private CDataEntityType firstType()
	{
		return data.isEmpty() ? null : data.get(0).GetDataType();
	}
	public boolean isStringElements()
	{
		return firstType() == CDataEntityType.STRING;
	}
	public boolean isFieldElements()
	{
		return firstType() == CDataEntityType.FIELD;
	}
	public boolean isNumberElements()
	{
		return firstType() == CDataEntityType.NUMBER;
	}

	
	public boolean ignore()
	{
		return data.isEmpty() ;
	}

	public boolean isValNeeded()
	{
		return true;
	}
	protected Vector<CDataEntity> data = new Vector<CDataEntity>();
}
