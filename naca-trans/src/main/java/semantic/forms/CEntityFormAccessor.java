/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CBaseDataReference;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CEntityNoAction;
import semantic.Verbs.CEntityInitialize;
import semantic.Verbs.CEntitySetConstant;
import utils.CObjectCatalog;

/**
 * BMS map-resource DSL: a screen-map form accessor — a pseudo-variable aliasing its
 * owning CICS {@code MAP} ({@link CEntityResourceForm}). The transcoder rules that once
 * created accessors ({@code CBaseEntityFactory.NewEntityFormAccessor}) are commented out;
 * no production path constructs this entity today (the retired direct backend was dead
 * wiring), but the pure semantic node and its recursive-ST4 reference binding stay
 * well-formed for any tree that holds one.
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaFormAccessor}
 * was retired onto the recursive ST4 assembly contract. The backend's only live output
 * protocol was {@code ExportReference(nLine) == LegacyDataRenderer.renderReference(owner,
 * getLine())} — i.e. exactly the owning form's data reference (the backend merely
 * delegated). A reference to the accessor now renders through the BMS forms-island binding
 * {@code semantic.forms.CEntityFormAccessor -> recursiveFormAccessorEntity}, whose template
 * reads only {@code entity.formReference}; that pure getter delegates to the owning form's
 * own precomputed reference (the owner renders through {@code recursiveFormEntity}) and
 * falls back to the legacy {@code [UNDEFINED]} sentinel {@code renderReference(null)}
 * returned when the owner is absent. {@code LegacyDataRenderer.renderReference} ignores a
 * semantic-declared {@code ExportReference} and falls through to that binding, reproducing
 * the backend's reference exactly.
 *
 * <p>The constructor also fixes the legacy {@code owner = owner} self-assignment no-op
 * (the parameter shadowed the field, so {@link #GetForm()} and every owner-delegating
 * protocol saw {@code null}): the field is now assigned with {@code this.owner = owner}.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: the accessor bears
 * accessors iff its owner does ({@code HasAccessors() == owner.HasAccessors()} — a form
 * bears none), needs no {@code val} ({@code isValNeeded() == false}), and has no reachable
 * write-accessor protocol ({@code ExportWriteAccessorTo -> null}: the backend delegated to
 * {@code LegacyDataRenderer.renderWriteAccessor(owner, value)}, which returns {@code null}
 * for the semantic-declared owner method). {@code DoExport} was unused and stays a no-op.
 * This tree names no {@code generate.*} class, so the dependency arrow stays
 * generate -&gt; semantic.
 *
 * @author U930CV
 */
public class CEntityFormAccessor extends CBaseDataReference
{
    /**
     * @param l
     * @param name
     * @param cat
     * @param owner the owning screen-map form this accessor aliases
     */
    public CEntityFormAccessor(int l, String name, CObjectCatalog cat, CEntityResourceForm owner)
    {
        super(l, name, cat);
        // Fixes the legacy `owner = owner` self-assignment no-op (the parameter shadowed
        // the field, leaving it null); the owner-delegating protocols below rely on it.
        this.owner = owner ;
        reference = owner ;
        parent = owner ;
    }
    public CEntityResourceForm GetForm()
    {
        return owner ;
    }
    protected CEntityResourceForm owner = null ;
    public void Clear()
    {
        super.Clear();
        owner = null ;
    }
    public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
    {
        String value = term.GetValue() ;
        CEntitySetConstant eAssign = factory.NewEntitySetConstant(l) ;
        if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
        {
            eAssign.SetToZero(owner) ;
        }
        else if (value.equals("SPACE") || value.equals("SPACES"))
        {
            eAssign.SetToSpace(owner) ;
        }
        else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
        {
            CEntityInitialize init = factory.NewEntityInitialize(l, owner);
            owner.RegisterWritingAction(init);
            return init ;
            //eAssign.SetToLowValue(owner) ;
        }
        else
        {
            return null ;
        }
        owner.RegisterWritingAction(eAssign);
        return eAssign ;
    }
    public boolean ignore()
    {
        return false ;
    }

    protected boolean isvirtual = false ;
    public void setVirtual()
    {
        isvirtual = true ;
    }
    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#GetDataType()
     */
    public CDataEntityType GetDataType()
    {
        if (isvirtual)
        {
            return CDataEntityType.VIRTUAL_FORM ;
        }
        else
        {
            return CDataEntityType.FORM ;
        }
    }
    public String GetConstantValue()
    {
        return "" ;
    }
    public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
    {
        if (term.GetDataType() == CDataEntityType.FORM && !owner.IsSaveCopy())
        {
            CEntityNoAction act = factory.NewEntityNoAction(l) ;
            factory.programCatalog.RegisterMapCopy(act) ;
            return act ;
        }
        else
        {
            return null;
        }
    }
    public boolean ReplaceVariable(CDataEntity field, CDataEntity var, boolean bRead)
    {
        boolean b = super.ReplaceVariable(field, var, bRead) ;
        if (field == owner)
        {
            owner = (CEntityResourceForm)var ;
            if (bRead)
            {
                var.RegisterReadReference(this) ;
                field.UnRegisterReadReference(this) ;
            }
            else
            {
                var.RegisterWriteReference(this) ;
                field.UnRegisterWriteReference(this) ;
            }
            return true ;
        }
        return b ;
    }
    public CEntityResourceForm getSaveCopy()
    {
        return owner.getSaveCopy() ;
    }

    /**
     * Pure read-only getter consumed by the {@code recursiveFormAccessorEntity} template:
     * the owning form's data reference this accessor aliases. The owner's reference is the
     * precomputed, target-formatted reference {@link CEntityResourceForm#getFormReference()}
     * exposes (itself rendered through the {@code recursiveFormEntity} binding) — a pure
     * delegation over precomputed state, no data-reference resolution and no lowering when
     * ST4 accesses it. When the owner is absent (a cleared entity), returns the legacy
     * {@code [UNDEFINED]} sentinel {@code LegacyDataRenderer.renderReference(null, ...)}
     * returned for the retired backend.
     */
    public String getFormReference()
    {
        if (owner == null)
        {
            return "[UNDEFINED]" ;
        }
        return owner.getFormReference() ;
    }

    public boolean HasAccessors()
    {
        // Preserved from the retired backend: the accessor bears accessors iff its owner
        // does (a screen-map form bears none).
        return owner != null && owner.HasAccessors() ;
    }

    public boolean isValNeeded()
    {
        // Preserved from the retired backend: a form accessor is never declared as a val.
        return false ;
    }

//  protected void RegisterMySelfToCatalog()
//  {
//      programCatalog.RegisterDataEntity(GetName(), this) ;
//      programCatalog.RegisterDataEntity("S" + GetName(), this) ;
//  }
}
