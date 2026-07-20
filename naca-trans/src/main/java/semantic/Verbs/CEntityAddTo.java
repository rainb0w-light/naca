/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 9 aoï¿½t 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.Verbs;


import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CBaseLanguageEntity;
import semantic.CDataEntity;
import semantic.CEntityEnvironmentVariable;
import semantic.CEntityValueReference;
import semantic.CSubStringAttributReference;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CEntityAddTo extends CBaseActionEntity
{

	/* (non-Javadoc)
	 * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
	 */
	@Override
	public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
	{
		if (dest.contains(field))
		{
			int pos = dest.indexOf(field) ;
			dest.set(pos, var) ;
			field.UnRegisterWritingAction(this) ;
			var.RegisterWritingAction(this) ;
			return true ;
		}
		if (values.contains(field))
		{
			int pos = values.indexOf(field) ;
			values.set(pos, var) ;
			field.UnRegisterReadingAction(this) ;
			var.RegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}

	/**
	 * @param line
	 * @param cat
	 * @param out
	 */
	public CEntityAddTo(int line, CObjectCatalog cat)
	{
		super(line, cat);
	}
	
	public void SetAddDest(CDataEntity dest)
	{
		dest.RegisterWritingAction(this);
		this.dest.add(dest);
	}
	public void SetAddValue(CDataEntity val)
	{
		val.RegisterReadingAction(this);
		values.add(val);
	}
	public void SetRounded(boolean b)
	{
		isrounded = b ;
	}
	protected Vector<CDataEntity> values = new Vector<CDataEntity>() ;
	protected Vector<CDataEntity> dest = new Vector<CDataEntity>() ;
	protected boolean isrounded = false ;
	public void Clear()
	{
		super.Clear();
		values.clear() ;
		dest.clear();
	}
	public boolean ignore()
	{
		boolean ignore = true ;
		for (int i = 0; i< dest.size(); i++)
		{
			CDataEntity e = dest.get(i);
			ignore &= e.ignore() ; 
		}
		if (ignore)
		{
			return ignore ;
		}
		ignore = true ;
		for (int i = 0; i< values.size(); i++)
		{
			CDataEntity e = values.get(i);
			ignore &= e.ignore() ; 
		}
		return ignore ; 
	}
	public boolean IgnoreVariable(CDataEntity data)
	{
		if (dest.contains(data))
		{
			dest.remove(data);
			data.UnRegisterWritingAction(this) ;
			return true ;
		}
		if (values.contains(data))
		{
			values.remove(data) ;
			data.UnRegisterReadingAction(this) ;
			return true ;
		}
		return false ;
	}

	// ==================== ST4 Template Accessors ====================

	public Vector<CDataEntity> getValues()
	{
		return values;
	}

	public Vector<CDataEntity> getDestinations()
	{
		return dest;
	}

	public boolean isRounded()
	{
		return isrounded;
	}

	public boolean hasSingleValue()
	{
		return values.size() == 1;
	}
	public boolean isSingleValue()
	{
		return hasSingleValue();
	}

	public CDataEntity getSingleValue()
	{
		if (values.size() == 1)
		{
			return values.get(0);
		}
		return null;
	}

	public CDataEntity getSingleDestination()
	{
		if (dest.size() == 1)
		{
			return dest.get(0);
		}
		return null;
	}
	public CEntityAddValueTree getCombinedValueTree()
	{
		if (values.size() < 2)
		{
			return null;
		}
		Object left = values.get(0);
		for (int index = 1; index < values.size(); index++)
		{
			left = new CEntityAddValueTree(left, values.get(index), programCatalog);
		}
		return (CEntityAddValueTree) left;
	}

	public boolean isIncrement()
	{
		return isSingleConstant("1");
	}

	public boolean isDecrement()
	{
		return isSingleConstant("-1");
	}

	public boolean hasNoValues()
	{
		return values.isEmpty();
	}
	public boolean isNoValues()
	{
		return hasNoValues();
	}
	public boolean hasMultipleValues()
	{
		return values.size() > 1;
	}
	public boolean isMultipleValues()
	{
		return hasMultipleValues();
	}
	public boolean singleDestinationHasAccessors()
	{
		return getSingleDestination() != null && getSingleDestination().HasAccessors();
	}
	public boolean isSingleDestinationHasAccessors()
	{
		return singleDestinationHasAccessors();
	}
	private CDataEntity unwrappedSingleDestination()
	{
		CDataEntity destination = getSingleDestination();
		while (destination instanceof CEntityValueReference)
		{
			destination = ((CEntityValueReference) destination).getReference();
		}
		return destination;
	}
	public boolean singleDestinationIsSubstring()
	{
		return unwrappedSingleDestination() instanceof CSubStringAttributReference;
	}
	public boolean isSingleDestinationSubstring()
	{
		return singleDestinationIsSubstring();
	}
	public boolean singleDestinationIsEnvironmentVariable()
	{
		return unwrappedSingleDestination() instanceof CEntityEnvironmentVariable;
	}
	public boolean isSingleDestinationEnvironmentVariable()
	{
		return singleDestinationIsEnvironmentVariable();
	}
	public CDataEntity getSingleDestinationReference()
	{
		CDataEntity destination = unwrappedSingleDestination();
		return destination instanceof CSubStringAttributReference
			? ((CSubStringAttributReference) destination).getReference() : null;
	}
	public CDataEntity getSingleDestinationStart()
	{
		CDataEntity destination = unwrappedSingleDestination();
		return destination instanceof CSubStringAttributReference
			? ((CSubStringAttributReference) destination).getStart() : null;
	}
	public CDataEntity getSingleDestinationLength()
	{
		CDataEntity destination = unwrappedSingleDestination();
		return destination instanceof CSubStringAttributReference
			? ((CSubStringAttributReference) destination).getLength() : null;
	}
	public String getSingleDestinationWriteAccessor()
	{
		CDataEntity destination = unwrappedSingleDestination();
		return destination instanceof CEntityEnvironmentVariable
			? ((CEntityEnvironmentVariable) destination).getWriteAccessor() : null;
	}

	private boolean isSingleConstant(String value)
	{
		CDataEntity singleValue = getSingleValue();
		return singleValue != null && value.equals(singleValue.GetConstantValue());
	}

}
