/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;


import semantic.CBaseActionEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntitySetColor extends CBaseActionEntity
{

    /**
     * @param line
     * @param cat
     */
    public CEntitySetColor(int line, CObjectCatalog cat, CDataEntity field)
    {
        super(line, cat);
        this.field = field ;
    }

    /*
     * Pure read-only getters consumed by the recursive ST4 assembly contract
     * (semantic.forms.CEntitySetColor -> recursiveSetColorEntity), preserved from
     * the retired direct backend generate.java.forms.CJavaSetColor whose DoExport
     * selected the emitted OnlineProgram.moveColor(<color>, <field>) call from
     * exactly these slots, with this branch precedence: a set color constant emits
     * moveColor(MapFieldAttrColor.<name>, <field>), else a moved color variable
     * emits moveColor(<variable>, <field>), else the fall-through emits
     * moveColor(MapFieldAttrColor.NEUTRAL, <field>). No output protocol lives here;
     * the getters only expose the already-resolved semantic state (the constant
     * name is a static nacaLib.mapSupport.MapFieldAttrColor field reference, never
     * lowering) and the assembler renders the field and variable data references
     * recursively.
     */
    public CDataEntity getField()
    {
        return field ;
    }
    public CDataEntity getColorVariable()
    {
        return colorVariable ;
    }
    public String getColorConstant()
    {
        if (color != null)
        {
            return "MapFieldAttrColor." + color.text ;
        }
        if (colorVariable != null)
        {
            return null ;
        }
        return "MapFieldAttrColor.NEUTRAL" ;
    }

    public void SetColor(CEntityFieldColor.CFieldColor c)
    {
        color = c ;
    }
    protected CEntityFieldColor.CFieldColor color = null ;
    protected CDataEntity field = null ;
    public void Clear()
    {
        super.Clear();
        field = null ;
    }
    public boolean ignore()
    {
        if (field == null || field.ignore())
        {
            return true ;
        }
        else if (colorVariable != null && colorVariable.ignore())
        {
            return true ;
        }
        return false ;
    }
    public boolean IgnoreVariable(CDataEntity data)
    {
        if (data == field)
        {
            field = null ;
            data.UnRegisterWritingAction(this) ;
            return true ;
        }
        return false ;
    }
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var)
    {
        if (this.field == field)
        {
            this.field = var ;
            field.UnRegisterWritingAction(this) ;
            var.RegisterWritingAction(this) ;
            return true ;
        }
        return false ;
    }

    /**
     * @param term
     */
    public void SetColor(CDataEntity term)
    {
        color = null ;
        colorVariable = term ;
    }
    protected CDataEntity colorVariable = null ;
}
