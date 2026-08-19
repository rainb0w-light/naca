/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import parser.expression.CStringTerminal;
import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntitySetAttribute extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntitySetAttribute(int line, CObjectCatalog cat, CDataEntity field)
    {
        super(line, cat);
        refField = field ;
    }

    /*
     * Pure read-only getters consumed by the recursive ST4 assembly contract
     * (semantic.forms.CEntitySetAttribute -> recursiveSetAttributeEntity),
     * preserved from the retired direct backend generate.java.forms.CJavaSetAttribute
     * whose DoExport selected the emitted OnlineProgram.moveAttribute(<attr>, <field>)
     * calls from exactly these slots. A set attribute value emits the single
     * moveAttribute(<value>, <field>) and stops (legacy early return); otherwise up
     * to three calls are emitted, one per attribute group, each selecting its
     * nacaLib.mapSupport constant with the legacy else-if precedence: protection
     * (AUTOSKIP / NUMERIC / PROTECTED / UNPROTECTED), intensity (BRIGHT / DARK /
     * NORMAL) and modified (MODIFIED / UNMODIFIED). No output protocol lives here;
     * the getters only expose the already-resolved semantic state (the constant
     * names are static naca-rt field references, never lowering) and the assembler
     * renders the field and value data references recursively.
     */
    public CDataEntity getField()
    {
        return refField ;
    }
    public CDataEntity getAttributeValue()
    {
        return attributeValue ;
    }
    /** Returns the protection constant. */
    public String getProtectionConstant()
    {
        if (isautoSkip)
        {
            return "MapFieldAttrProtection.AUTOSKIP" ;
        }
        if (isnumeric)
        {
            return "MapFieldAttrProtection.NUMERIC" ;
        }
        if (isprotected)
        {
            return "MapFieldAttrProtection.PROTECTED" ;
        }
        if (isunProtected)
        {
            return "MapFieldAttrProtection.UNPROTECTED" ;
        }
        return null ;
    }
    /** Returns the intensity constant. */
    public String getIntensityConstant()
    {
        if (isbright)
        {
            return "MapFieldAttrIntensity.BRIGHT" ;
        }
        if (isdark)
        {
            return "MapFieldAttrIntensity.DARK" ;
        }
        if (isnormal)
        {
            return "MapFieldAttrIntensity.NORMAL" ;
        }
        return null ;
    }
    /** Returns the modified constant. */
    public String getModifiedConstant()
    {
        if (bModified)
        {
            return "MapFieldAttrModified.MODIFIED" ;
        }
        if (isunmodified)
        {
            return "MapFieldAttrModified.UNMODIFIED" ;
        }
        return null ;
    }

    protected CDataEntity refField = null ;

    /** Sets the bright. */
    public void SetBright()
    {
        isbright = true ;
    }
    protected boolean isbright = false ;

    /** Sets the modified. */
    public void SetModified()
    {
        bModified = true ;
    }
    protected boolean bModified = false ;

    /** Sets the numeric. */
    public void SetNumeric()
    {
        isnumeric = true ;
    }
    protected boolean isnumeric = false ;

    /** Sets the protected. */
    public void SetProtected()
    {
        isprotected = true ;
    }
    protected boolean isprotected = false ;

    /** Sets the unprotected. */
    public void SetUnprotected()
    {
        isunProtected = true ;
    }
    protected boolean isunProtected = false ;

    /** Sets the auto skip. */
    public void SetAutoSkip()
    {
        isautoSkip = true ;
    }
    protected boolean isautoSkip = false ;

    /** Sets the normal. */
    public void SetNormal()
    {
        isnormal = true ;
    }
    protected boolean isnormal = false ;

    /** Sets the unmodified. */
    public void SetUnmodified()
    {
        isunmodified = true;
    }
    protected boolean isunmodified = false ;

    /** Sets the dark. */
    public void SetDark()
    {
        isdark = true ;
    }
    protected boolean isdark = false ;

    /** Sets the attribute. */
    public void SetAttribute(CDataEntity entity)
    {
        attributeValue = entity ;
    }
    protected CDataEntity attributeValue = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        attributeValue = null ;
        refField = null ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        if (refField == null || refField.ignore())
        {
            return true ;
        }
        else if (attributeValue != null && attributeValue.ignore())
        {
            return true ;
        }
        return false ;
    }
    /** Executes the ignore variable operation. */
    public boolean IgnoreVariable(CDataEntity data)
    {
        if (data == refField)
        {
            refField = null ;
            data.UnRegisterWritingAction(this) ;
            return true ;
        }
        return false ;
    }
    /** Executes the replace variable operation. */
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (refField == field)
        {
            refField = var ;
            field.UnRegisterWritingAction(this) ;
            var.RegisterWritingAction(this) ;
            return true ;
        }
        return false ;
    }

    /** Executes the get special assignement operation. */
    public CBaseActionEntity GetSpecialAssignement(String val, CBaseEntityFactory factory)
    {
        if (refField != null)
        {
            CTerminal term = new CStringTerminal(val) ;
            CBaseActionEntity act = CEntityFieldAttribute.intGetSpecialAssignment(refField, term, factory, getLine()) ;
            return act ;
        }
        return null;
    }
}
