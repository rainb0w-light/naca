/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.varEx;

import nacaLib.mapSupport.LocalizedString;
import nacaLib.mapSupport.MapFieldAttrColor;
import nacaLib.mapSupport.MapFieldAttrFill;
import nacaLib.mapSupport.MapFieldAttrHighlighting;
import nacaLib.mapSupport.MapFieldAttrIntensity;
import nacaLib.mapSupport.MapFieldAttrJustify;
import nacaLib.mapSupport.MapFieldAttrModified;
import nacaLib.mapSupport.MapFieldAttrProtection;
import nacaLib.mapSupport.MapFieldAttribute;

/**
 * @author U930DI
 *
 */
public class DeclareTypeEditInMap extends DeclareTypeBase
{
    private Form curVarForm = null;
    private VarDefForm curDefForm = null;

    MapFieldAttribute mapFieldAttribute = null;
    int nSize = 0;
    String csName = null;
    LocalizedString localizedString = null;
    String csFormat = null;
    boolean ishasCursor = false;
    String csDevelopableMark = null;
    //String csSemanticContextValue = null;

    /** Creates a new declare type edit in map instance. */
    public DeclareTypeEditInMap()
    {
    }

    /** Executes the set operation. */
    public void set(VarLevel varLevel, Form curVarForm, VarDefForm curDefForm, String csName, int nSize)
    {
        super.set(varLevel);
        this.curVarForm = curVarForm;
        this.curDefForm = curDefForm;
        this.nSize = nSize;
        if (mapFieldAttribute == null) {
            mapFieldAttribute = new MapFieldAttribute();
        } else {
            mapFieldAttribute.resetDefaultValues();
        }
        this.csName = csName;

        this.localizedString = null;
        this.csFormat = null;
        this.ishasCursor = false;
        this.csDevelopableMark = null;
        //csSemanticContextValue = null;
    }

    /** Creates the var def. */
    public VarDefBuffer createVarDef(VarDefBuffer varDefParent)
    {
        VarDefBuffer varDef = new VarDefEditInMap(varDefParent, this);
        return varDef;
    }

    public CInitialValue getInitialValue()
    {
        return null;
    }

    /** Executes the edit operation. */
    public Edit edit()
    {
        EditInMap varEdit = new EditInMap(this);
        curDefForm.addField(varEdit.varDef);
        return varEdit;
    }

    /** Executes the initial value operation. */
    public DeclareTypeEditInMap initialValue(LocalizedString localizedString)
    {
        this.localizedString = localizedString;
        return this;
    }

    /** Executes the format operation. */
    public DeclareTypeEditInMap format(String csFormat)
    {
        this.csFormat = csFormat;
        return this;
    }


    /** Executes the color operation. */
    public DeclareTypeEditInMap color(MapFieldAttrColor mapFieldAttrColor)
    {
        mapFieldAttribute.setColor(mapFieldAttrColor);
        return this;
    }

    /** Executes the high lighting operation. */
    public DeclareTypeEditInMap highLighting(MapFieldAttrHighlighting mapFieldAttrHighlighting)
    {
        mapFieldAttribute.setHighlighting(mapFieldAttrHighlighting);
        return this;
    }

    /** Executes the protection operation. */
    public DeclareTypeEditInMap protection(MapFieldAttrProtection mapFieldAttrProtection)
    {
        mapFieldAttribute.setProtection(mapFieldAttrProtection);
        return this;
    }

    /** Executes the intensity operation. */
    public DeclareTypeEditInMap intensity(MapFieldAttrIntensity mapFieldAttrIntensity)
    {
        mapFieldAttribute.setIntensity(mapFieldAttrIntensity);
        return this;
    }

    /** Executes the justify operation. */
    public DeclareTypeEditInMap justify(MapFieldAttrJustify mapFieldAttrJustify)
    {
        mapFieldAttribute.setJustify(mapFieldAttrJustify);
        return this;
    }

    /** Executes the justify right operation. */
    public DeclareTypeEditInMap justifyRight()
    {
        mapFieldAttribute.setJustify(MapFieldAttrJustify.RIGHT);
        return this;
    }

    /** Executes the justify fill operation. */
    public DeclareTypeEditInMap justifyFill(MapFieldAttrFill mapFieldAttrFill)
    {
        mapFieldAttribute.setFill(mapFieldAttrFill);
        return this;
    }

    /** Sets the cursor. */
    public DeclareTypeEditInMap setCursor(boolean b)
    {
        ishasCursor = b;
        return this;
    }

    /** Sets the modified. */
    public DeclareTypeEditInMap setModified(MapFieldAttrModified modified)
    {
        mapFieldAttribute.setAttrModified(modified);
        return this;
    }

    /** Sets the modified. */
    public DeclareTypeEditInMap setModified()
    {
        setModified(MapFieldAttrModified.MODIFIED);
        return this ;
    }

    /** Sets the unmodified. */
    public DeclareTypeEditInMap setUnmodified()
    {
        setModified(MapFieldAttrModified.UNMODIFIED);
        return this;
    }

    /** Sets the developable mark. */
    public DeclareTypeEditInMap setDevelopableMark(String string)
    {
        csDevelopableMark = string ;
        return this ;
    }

    /** Executes the semantic context operation. */
    public DeclareTypeEditInMap semanticContext(String csSemanticContextValue)
    {
        //csSemanticContextValue = csSemanticContextValue;
        return this;
    }



    /** Executes the register edit in form operation. */
    public void registerEditInForm(EditInMap edit)
    {
        curVarForm.addEdit(edit);
    }
}
