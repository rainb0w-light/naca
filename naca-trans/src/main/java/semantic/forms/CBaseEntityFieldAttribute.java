/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
 *
 */
package semantic.forms;

import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CBaseDataReference;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public abstract class CBaseEntityFieldAttribute extends CBaseDataReference
{
    /** Provides centity field attribute type behavior. */
    public static class CEntityFieldAttributeType
    {
        public static CEntityFieldAttributeType LENGTH = new CEntityFieldAttributeType() ;
        public static CEntityFieldAttributeType HIGHLIGHT = new CEntityFieldAttributeType() ;
        public static CEntityFieldAttributeType COLOR = new CEntityFieldAttributeType() ;
        public static CEntityFieldAttributeType FLAG = new CEntityFieldAttributeType() ;
//      public static CEntityFieldAttributeType PROTECTED = new CEntityFieldAttributeType() ;
        public static CEntityFieldAttributeType VALIDATION = new CEntityFieldAttributeType() ;
        public static CEntityFieldAttributeType ATTRIBUTE = new CEntityFieldAttributeType() ;
        public static CEntityFieldAttributeType DATA = new CEntityFieldAttributeType() ;
        //public static CEntityFieldAttributeType DATAO = new CEntityFieldAttributeType() ;
    }
    /**
     * @param name
     * @param cat
     */
    protected CBaseEntityFieldAttribute(int l, String name, CObjectCatalog cat, CEntityFieldAttributeType type, CDataEntity owner)
    {
        super(l, name, cat);
        this.type = type ;
        reference = owner ;
        parent = owner ;
    }
    protected CEntityFieldAttributeType type = null ;

    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear() ;
        reference = null ;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
     */
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
    {
        return null;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#GetSpecialAssignment(semantic.CBaseDataEntity)
     */
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
    {
        return null;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }


    /* (non-Javadoc)
     * @see semantic.CBaseLanguageEntity#RegisterMySelfToCatalog()
     */
    protected void RegisterMySelfToCatalog()
    {
        programCatalog.RegisterDataEntity(GetName(), this) ;
//      programCatalog.RegisterDataEntity("S" + GetName(), this) ;
    }
    /** Executes the get constant value operation. */
    public String GetConstantValue()
    {
        return "" ;
    }

}
