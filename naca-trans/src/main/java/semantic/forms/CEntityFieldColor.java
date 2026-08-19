/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import java.util.Vector;
import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CBaseEntityCondition;
import semantic.expression.CUnitaryEntityCondition;
import utils.CObjectCatalog;
import utils.NacaTransAssertException;


/**
 * @author sly
 *
 */
public class CEntityFieldColor extends CBaseEntityFieldAttribute
{
    /** Provides cfield color behavior. */
    public static class CFieldColor
    {
        protected CFieldColor(String s) {
            text = s ;
        }
        public String text="" ;
        public static CFieldColor RED = new CFieldColor("RED") ;
        public static CFieldColor YELLOW = new CFieldColor("YELLOW") ;
        public static CFieldColor GREEN = new CFieldColor("GREEN") ;
        public static CFieldColor BLUE = new CFieldColor("BLUE") ;
        public static CFieldColor PINK = new CFieldColor("PINK") ;
        public static CFieldColor TURQUOISE = new CFieldColor("TURQUOISE") ;
        public static CFieldColor NEUTRAL = new CFieldColor("NEUTRAL") ;
        /** Executes the which color operation. */
        public static CFieldColor WhichColor(String col)
        {
            if (col.equals(""))
            {
                return null ; //default color
            }
            else if (col.equals("1"))
            {
                return CFieldColor.BLUE ;
            }
            else if (col.equals("2"))
            {
                return CFieldColor.RED ;
            }
            else if (col.equals("3"))
            {
                return CFieldColor.PINK ;
            }
            else if (col.equals("4"))
            {
                return CFieldColor.GREEN ;
            }
            else if (col.equals("5"))
            {
                return CFieldColor.TURQUOISE ;
            }
            else if (col.equals("6"))
            {
                return CFieldColor.YELLOW ;
            }
            else if (col.equals("7"))
            {
                return CFieldColor.NEUTRAL;
            }
            else
            {
                return null ;
            }
        }
    }

    /** Creates a new centity field color instance. */
    public CEntityFieldColor(int l, String name, CObjectCatalog cat, CDataEntity owner)
    {
        super(l, name, cat, CEntityFieldAttributeType.COLOR, owner) ;
    }
    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetDataType()
     */
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.FIELD_ATTRIBUTE ;
    }
    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return true ;
    }
    public boolean isValNeeded()
    {
        return true ;
    }
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
    {
        String col = term.GetValue() ;
        CEntityFieldAttributeReference ref = factory.NewEntityFieldAttributeReference(reference) ;
        CEntitySetColor eSet = factory.NewEntitySetColor(l, ref);
        color = CFieldColor.WhichColor(col) ;
        eSet.SetColor(color) ;
        ref.RegisterWritingAction(eSet) ;
        return eSet;
    }
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
    {
        if (term.GetDataType() == CDataEntity.CDataEntityType.FIELD_ATTRIBUTE)
        {
            CEntityFieldAttributeReference ref = factory.NewEntityFieldAttributeReference(reference) ;
            CEntitySetColor eSet = factory.NewEntitySetColor(l, ref);
            eSet.SetColor(term) ;
            ref.RegisterWritingAction(eSet) ;
            term.RegisterReadingAction(eSet) ;
            return eSet;
        }
        else
        {
            return null ;
        }
    }

    protected CFieldColor color = null ;
    /** Executes the get array reference operation. */
    public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
    {
        CDataEntity e = reference.GetArrayReference(v, factory) ;
        return factory.NewEntityFieldColor(getLine(), "", e);
    };
    /** Executes the get special condition operation. */
    public CBaseEntityCondition GetSpecialCondition(
        int nLine,
        String value,
        CBaseEntityCondition.EConditionType type,
        CBaseEntityFactory factory)
    {
        return CEntityFieldColor.GetSpecialCondition(nLine, value, reference, factory, type) ;
    }
    static CUnitaryEntityCondition GetSpecialCondition(
        int nLine,
        String value,
        CDataEntity ref,
        CBaseEntityFactory factory,
        CBaseEntityCondition.EConditionType type)
    {
        CFieldColor col = CFieldColor.WhichColor(value);
        if (col == null)
        {
            return null ;
        }
        else
        {
            CEntityIsFieldColor eCond = factory.NewEntityIsFieldColor() ;
            eCond.IsColor(col, ref);
            ref.RegisterVarTesting(eCond) ;
            if (type == CBaseEntityCondition.EConditionType.IS_DIFFERENT)
            {
                eCond.SetOpposite();
            }
            else if (type != CBaseEntityCondition.EConditionType.IS_EQUAL)
            {
                throw new NacaTransAssertException("Unexpecting condition type in CEntityFieldColor") ;
            }
            return eCond ;
        }
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
}
