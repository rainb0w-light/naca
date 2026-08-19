/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityStringConcat extends CBaseActionEntity
{
    /** Provides concat item behavior. */
    public static final class ConcatItem
    {
        private CDataEntity value;
        private CDataEntity delimiter;

        private ConcatItem(CDataEntity value, CDataEntity delimiter)
        {
            this.value = value;
            this.delimiter = delimiter;
        }

        public CDataEntity getValue()
        {
            return value;
        }

        public CDataEntity getDelimiter()
        {
            return delimiter;
        }
    }

    /* (non-Javadoc)
     * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
     */
    @Override
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (eVariable == field)
        {
            eVariable = var ;
            field.UnRegisterWritingAction(this) ;
            var.RegisterWritingAction(this) ;
            return true ;
        }
        boolean replaced = false;
        for (ConcatItem item : items)
        {
            if (item.value == field)
            {
                item.value = var;
                replaced = true;
            }
            if (item.delimiter == field)
            {
                item.delimiter = var;
                replaced = true;
            }
        }
        if (eStartIndex == field)
        {
            eStartIndex = var;
            replaced = true;
        }
        if (replaced)
        {
            field.UnRegisterReadingAction(this);
            var.RegisterReadingAction(this);
            return true;
        }
        return false ;
    }
    /**
     * @param line
     * @param cat
     */
    public CEntityStringConcat(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    protected List<ConcatItem> items = new ArrayList<ConcatItem>() ;
    protected CDataEntity eVariable = null ;
    protected CDataEntity eStartIndex = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        items.clear();
        eStartIndex = null ;
        eVariable = null;
    }
    /** Sets the variable. */
    public void SetVariable(CDataEntity e)
    {
        eVariable = e ;
    }
    /** Sets the variable. */
    public void SetVariable(CDataEntity e, CDataEntity s)
    {
        eVariable = e ;
        eStartIndex = s ;
    }
    /** Adds the item. */
    public void AddItem(CDataEntity eItem, CDataEntity eUntil)
    {
        items.add(new ConcatItem(eItem, eUntil));
    }
    /** Adds the item. */
    public void AddItem(CDataEntity eItem)
    {
        items.add(new ConcatItem(eItem, null));
    }
    public List<ConcatItem> getConcatItems()
    {
        return Collections.unmodifiableList(items);
    }
    public CDataEntity getDestination()
    {
        return eVariable;
    }
    public CDataEntity getStartIndex()
    {
        return eStartIndex;
    }
    public boolean getHasOverflowHandler()
    {
        return !lstChildren.isEmpty();
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        boolean ignore = eVariable.ignore();
        return ignore ;
    }
}
