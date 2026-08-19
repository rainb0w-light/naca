/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.Verbs;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author U930CV
 *
 */
public class CEntityReplace extends CBaseActionEntity
{
    protected static class CReplaceMode
    {
        public static CReplaceMode ALL = new CReplaceMode();
        public static CReplaceMode LEADING = new CReplaceMode();
        public static CReplaceMode FIRST = new CReplaceMode();
    }
    protected static class CReplaceType
    {
        public static CReplaceType CUSTOM = new CReplaceType() ;
        public static CReplaceType SPACES = new CReplaceType() ;
        public static CReplaceType ZEROS = new CReplaceType() ;
        public static CReplaceType LOW_VALUES = new CReplaceType() ;
        public static CReplaceType HIGH_VALUES = new CReplaceType() ;
    }
    protected class CReplaceItem
    {
        public CReplaceMode mode = null ;
        public CReplaceType replaceDataType = null ;
        public CDataEntity replaceData = null ;
        public CReplaceType dataType = null ;
        public CDataEntity data = null ;
    }
    /**
     * @param line
     * @param cat
     */
    public CEntityReplace(int line, CObjectCatalog cat)
    {
        super(line, cat);
    }

    protected CDataEntity variable = null ;
    protected Vector<CReplaceItem> itemsToReplace = new Vector<CReplaceItem>() ;
    private CReplaceItem curItem = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        variable = null ;
        itemsToReplace.clear() ;
        if (curItem != null)
        {
            curItem.data = null ;
            curItem.dataType = null ;
            curItem.replaceData = null ;
            curItem = null ;
        }
    }

    /** Sets the replace. */
    public void SetReplace(CDataEntity e)
    {
        variable = e ;
    }
    /** Adds the replace leading. */
    public void AddReplaceLeading()
    {
        curItem = new CReplaceItem() ;
        curItem.mode = CReplaceMode.LEADING;
    }
    /** Adds the replace all. */
    public void AddReplaceAll()
    {
        curItem = new CReplaceItem() ;
        curItem.mode = CReplaceMode.ALL;
    }
    /** Adds the replace first. */
    public void AddReplaceFirst()
    {
        curItem = new CReplaceItem() ;
        curItem.mode = CReplaceMode.FIRST;
    }
    /** Executes the replace spaces operation. */
    public void ReplaceSpaces()
    {
        curItem.replaceDataType = CReplaceType.SPACES ;
        curItem.replaceData = null ;
    }
    /** Executes the replace zeros operation. */
    public void ReplaceZeros()
    {
        curItem.replaceDataType = CReplaceType.ZEROS ;
        curItem.replaceData = null ;
    }
    /** Executes the replace low values operation. */
    public void ReplaceLowValues()
    {
        curItem.replaceDataType = CReplaceType.LOW_VALUES ;
        curItem.replaceData = null ;
    }
    /** Executes the replace high values operation. */
    public void ReplaceHighValues()
    {
        curItem.replaceDataType = CReplaceType.HIGH_VALUES;
        curItem.replaceData = null ;
    }
    /** Executes the by spaces operation. */
    public void BySpaces()
    {
        curItem.dataType = CReplaceType.SPACES ;
        curItem.data = null ;
        itemsToReplace.add(curItem) ;
        curItem = null ;
    }
    /** Executes the by zeros operation. */
    public void ByZeros()
    {
        curItem.dataType = CReplaceType.ZEROS ;
        curItem.data = null ;
        itemsToReplace.add(curItem) ;
        curItem = null ;
    }
    /** Executes the by low values operation. */
    public void ByLowValues()
    {
        curItem.dataType = CReplaceType.LOW_VALUES ;
        curItem.data = null ;
        itemsToReplace.add(curItem) ;
        curItem = null ;
    }
    /** Executes the by high values operation. */
    public void ByHighValues()
    {
        curItem.dataType = CReplaceType.HIGH_VALUES ;
        curItem.data = null ;
        itemsToReplace.add(curItem) ;
        curItem = null ;
    }
    /** Executes the replace data operation. */
    public void ReplaceData(CDataEntity e)
    {
        curItem.replaceDataType = CReplaceType.CUSTOM ;
        curItem.replaceData = e ;
    }
    /** Executes the by data operation. */
    public void ByData(CDataEntity e)
    {
        curItem.dataType = CReplaceType.CUSTOM ;
        curItem.data = e ;
        itemsToReplace.add(curItem) ;
        curItem = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return variable.ignore();
    }

    /* (non-Javadoc)
     * @see semantic.CBaseActionEntity#ReplaceVariable(semantic.CDataEntity, semantic.CDataEntity)
     */
    @Override
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (variable == field)
        {
            variable = var ;
            field.UnRegisterWritingAction(this) ;
            var.RegisterWritingAction(this) ;
            return true ;
        }
        return false ;
    }

    public CDataEntity getVariable() {
        return variable;
    }
    /** Provides replace item model behavior. */
    public static class ReplaceItemModel {
        private final CReplaceMode mode;
        private final CReplaceType replaceDataType;
        private final CDataEntity replaceData;
        private final CReplaceType dataType;
        private final CDataEntity data;
        /** Creates a new replace item model instance. */
        public ReplaceItemModel(CReplaceMode mode, CReplaceType replaceDataType,
            CDataEntity replaceData, CReplaceType dataType, CDataEntity data) {
            this.mode = mode;
            this.replaceDataType = replaceDataType;
            this.replaceData = replaceData;
            this.dataType = dataType;
            this.data = data;
        }
        /** Returns the mode name. */
        public String getModeName() {
            if (mode == CReplaceMode.ALL) {
                return "all";
            }
            if (mode == CReplaceMode.FIRST) {
                return "first";
            }
            if (mode == CReplaceMode.LEADING) {
                return "leading";
            }
            return "";
        }
        public String getReplaceMethodName() {
            return typeName(replaceDataType, false);
        }
        public String getByMethodName() {
            return typeName(dataType, true);
        }
        private static String typeName(CReplaceType t, boolean by) {
            if (t == CReplaceType.SPACES) {
                return "Spaces";
            }
            if (t == CReplaceType.ZEROS) {
                return by ? "Zero" : "Zeros";
            }
            if (t == CReplaceType.LOW_VALUES) {
                return "LowValues";
            }
            if (t == CReplaceType.HIGH_VALUES) {
                return "HighValues";
            }
            return "";
        }
        public CDataEntity getReplaceData() {
            return replaceData;
        }
        public CDataEntity getData() {
            return data;
        }
    }
    /** Returns the replace items. */
    public List<ReplaceItemModel> getReplaceItems() {
        List<ReplaceItemModel> result = new ArrayList<>();
        for (CReplaceItem item : itemsToReplace) {
            result.add(new ReplaceItemModel(
                item.mode, item.replaceDataType, item.replaceData, item.dataType, item.data));
        }
        return result;
    }
}
