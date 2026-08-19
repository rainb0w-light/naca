/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;


import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CEntityEnvironmentVariable;
import semantic.CEntityValueReference;
import semantic.CSubStringAttributReference;
import utils.CObjectCatalog;

/**
 * @author sly
 *
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

    /** Sets the add dest. */
    public void SetAddDest(CDataEntity dest)
    {
        dest.RegisterWritingAction(this);
        this.dest.add(dest);
    }
    /** Sets the add value. */
    public void SetAddValue(CDataEntity val)
    {
        val.RegisterReadingAction(this);
        values.add(val);
    }
    /** Sets the rounded. */
    public void SetRounded(boolean b)
    {
        isrounded = b ;
    }
    protected Vector<CDataEntity> values = new Vector<CDataEntity>() ;
    protected Vector<CDataEntity> dest = new Vector<CDataEntity>() ;
    protected boolean isrounded = false ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        values.clear() ;
        dest.clear();
    }
    /** Executes the ignore operation. */
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
    /** Executes the ignore variable operation. */
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

    /** Returns whether s single value. */
    public boolean hasSingleValue()
    {
        return values.size() == 1;
    }
    public boolean isSingleValue()
    {
        return hasSingleValue();
    }

    /** Returns the single value. */
    public CDataEntity getSingleValue()
    {
        if (values.size() == 1)
        {
            return values.get(0);
        }
        return null;
    }

    /** Returns the single destination. */
    public CDataEntity getSingleDestination()
    {
        if (dest.size() == 1)
        {
            return dest.get(0);
        }
        return null;
    }
    /** Returns the combined value tree. */
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

    /** Returns whether s no values. */
    public boolean hasNoValues()
    {
        return values.isEmpty();
    }
    public boolean isNoValues()
    {
        return hasNoValues();
    }
    /** Returns whether s multiple values. */
    public boolean hasMultipleValues()
    {
        return values.size() > 1;
    }
    public boolean isMultipleValues()
    {
        return hasMultipleValues();
    }
    /** Executes the single destination has accessors operation. */
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
    /** Executes the single destination is substring operation. */
    public boolean singleDestinationIsSubstring()
    {
        return unwrappedSingleDestination() instanceof CSubStringAttributReference;
    }
    public boolean isSingleDestinationSubstring()
    {
        return singleDestinationIsSubstring();
    }
    /** Executes the single destination is environment variable operation. */
    public boolean singleDestinationIsEnvironmentVariable()
    {
        return unwrappedSingleDestination() instanceof CEntityEnvironmentVariable;
    }
    public boolean isSingleDestinationEnvironmentVariable()
    {
        return singleDestinationIsEnvironmentVariable();
    }
    /** Returns the single destination reference. */
    public CDataEntity getSingleDestinationReference()
    {
        CDataEntity destination = unwrappedSingleDestination();
        return destination instanceof CSubStringAttributReference
            ? ((CSubStringAttributReference) destination).getReference() : null;
    }
    /** Returns the single destination start. */
    public CDataEntity getSingleDestinationStart()
    {
        CDataEntity destination = unwrappedSingleDestination();
        return destination instanceof CSubStringAttributReference
            ? ((CSubStringAttributReference) destination).getStart() : null;
    }
    /** Returns the single destination length. */
    public CDataEntity getSingleDestinationLength()
    {
        CDataEntity destination = unwrappedSingleDestination();
        return destination instanceof CSubStringAttributReference
            ? ((CSubStringAttributReference) destination).getLength() : null;
    }
    /** Returns the single destination write accessor. */
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
