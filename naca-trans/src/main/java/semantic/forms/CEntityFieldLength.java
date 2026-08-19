/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */


package semantic.forms;


import java.util.Vector;
import lexer.Cobol.CCobolConstantList;
import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityCondition;
import utils.CObjectCatalog;




/** Provides centity field length behavior. */
public class CEntityFieldLength extends CBaseEntityFieldAttribute
{
    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetSpecialAssignment(semantic.CDataEntity, semantic.CBaseEntityFactory, int)
     */
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
    {
        CEntityFieldAttributeReference ref = factory.NewEntityFieldAttributeReference(reference) ;
        CEntitySetCursor eSet = factory.NewEntitySetCursor(l, ref) ;
        eSet.SetReference(term) ;
        term.RegisterReadingAction(eSet) ;
        ref.RegisterWritingAction(eSet) ;
        return eSet ;
    }
    /** Creates a new centity field length instance. */
    public CEntityFieldLength(int l, String name, CObjectCatalog cat, CDataEntity owner)
    {
        super(l, name, cat, CEntityFieldAttributeType.LENGTH, owner) ;
    }
    /* (non-Javadoc)
     * @see semantic.CDataEntity#GetDataType()
     */
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.FIELD ;
    }
    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        return true ;
    }
    public boolean isValNeeded()
    {
        return false ;
    }
    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
     */
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
    {
        CEntityFieldAttributeReference ref = factory.NewEntityFieldAttributeReference(reference) ;
        String v = term.GetValue() ;
        int n = 0 ;
        try {
            n = Integer.parseInt(v) ;
        } catch (NumberFormatException e){}
        if (v.equals("-1"))
        {
            CEntitySetCursor eSet = factory.NewEntitySetCursor(l, ref) ;
            ref.RegisterWritingAction(eSet) ;
            return eSet ;
        }
        else if (v.equals("1") || n>0)
        {
            CEntitySetAttribute eSet = factory.NewEntitySetAttribute(l, ref) ;
            eSet.SetModified() ;
            ref.RegisterWritingAction(eSet) ;
            return eSet ;
        }
        else if (v.equals("0") || v.equals(CCobolConstantList.ZERO.name) || v.equals(CCobolConstantList.ZEROS.name)
            || v.equals(CCobolConstantList.ZEROES.name))
        {
            CEntitySetCursor eSet = factory.NewEntitySetCursor(l, ref) ;
            eSet.removeCursor() ;
            ref.RegisterWritingAction(eSet) ;
            return eSet ;
//          CEntitySetConstant eSet = factory.NewEntitySetConstant(l);
//          eSet.SetToZero(refField) ;
//          refField.RegisterWritingAction(eSet) ;
//          return eSet ;
        }
        return null;
    }
    /** Executes the get special condition operation. */
    public CBaseEntityCondition GetSpecialCondition(
        int nLine,
        String value,
        CBaseEntityCondition.EConditionType type,
        CBaseEntityFactory factory)
    {
        boolean iszero = false ;
        if (value.equals("0") || value.equalsIgnoreCase("ZERO") || value.equalsIgnoreCase("ZEROS") || value.equalsIgnoreCase("ZEROES"))
        {
            iszero = true ;
        }
        if (type == CBaseEntityCondition.EConditionType.IS_GREATER_THAN && iszero)
        {
            CEntityIsFieldModified e = factory.NewEntityIsFieldModified();
            e.SetIsModified(reference);
            reference.RegisterVarTesting(e) ;
            return e ;
        }
        if (!iszero && Integer.parseInt(value)==-1)
        {
            CEntityIsFieldCursor e = factory.NewEntityIsFieldCursor() ;
            if (type == CBaseEntityCondition.EConditionType.IS_EQUAL)
            {
                e.SetHasCursor(reference) ;
            }
            else
            {
                e.SetHasNotCursor(reference) ;
            }
            reference.RegisterVarTesting(e) ;
            return e ;
        }
        return null ;
    }
    /** Executes the get array reference operation. */
    public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
    {
        CDataEntity e = reference.GetArrayReference(v, factory) ;
        return factory.NewEntityFieldLengh(getLine(), "", e);
    };
}
