/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import org.w3c.dom.Element;

import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * BMS map-resource DSL: a screen-map {@code MAPREDEFINE} (a re-view over an existing screen
 * form), lowered from a BMS {@code MAP}/{@code MAPREDEFINE} working-storage structure
 * ({@code parser/Cobol/elements/CWorkingEntry.DoSemanticAnalysisForMapRedefine} -&gt;
 * {@code factory.NewEntityFormRedefine(line, name, ref, issaveMap)}). A form redefine declares a
 * {@code nacaLib.varEx.MapRedefine} via {@code declare.level(1).redefinesMap(<origin form ref>)}
 * and opens a {@code { ... }} block over its child fields.
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaFormRedefine} was
 * retired onto the recursive ST4 assembly contract. The backend had two live output protocols,
 * both preserved target-neutrally here:
 * <ul>
 *   <li><b>data reference</b> — {@code ExportReference(nLine) == formatIdentifier(GetName())}.
 *       A reference to the form redefine now renders through the BMS forms-island binding
 *       {@code semantic.forms.CEntityFormRedefine -> recursiveFormRedefineEntity}, whose template
 *       reads only {@code entity.formattedName} (the precomputed, target-formatted identifier).
 *       {@code LegacyDataRenderer.renderReference} ignores a semantic-declared
 *       {@code ExportReference} and falls through to that binding, reproducing the backend's
 *       formatted-name reference exactly.</li>
 *   <li><b>declaration block</b> — {@code DoExport} emitted
 *       {@code MapRedefine <name> = declare.level(1).redefinesMap(<origin form ref>) ;} followed
 *       by a {@code { ... }} block over the form redefine's children. The declaration LINE now
 *       renders declaratively through the {@code recursiveFormRedefineDeclarationEntity} template
 *       (invoked by the generate-layer factory bridge
 *       {@code BmsJavaEntities.renderFormRedefineDeclaration}); the block and the children keep
 *       rendering through the still-direct BMS field backends, which that bridge drives over
 *       {@code exportChildren}.</li>
 * </ul>
 *
 * <p>This tree names no {@code generate.*} class, so the dependency arrow stays
 * generate -&gt; semantic. The two generate-layer protocols the retired backend performed inline
 * are supplied by the generate-layer factory ({@code generate.java.forms.BmsJavaEntities
 * .formRedefine}): a neutral identifier {@link Function} (standing in for
 * {@code LegacyLanguageRenderer.formatIdentifier}) and a declaration {@link Consumer} that
 * pre-renders the origin form reference through the exact legacy
 * {@code LegacyDataRenderer.renderReference} protocol, renders the declaration line through the
 * recursive assembler, and drives the {@code startBlock/exportChildren/endBlock} block over the
 * still-direct child backends. A hand-built entity (no factory) defaults to the neutral
 * identifier normalization and a no-op declaration renderer, so it stays well-formed and never
 * fails.
 *
 * <p>Latent defect fixed in this slice: the legacy constructor assigned the constructor
 * parameter to itself ({@code eForm = eForm;} — the parameter shadows the field), so the
 * {@code eForm} field stayed {@code null} and every retired backend emitted
 * {@code redefinesMap([UNDEFINED])} regardless of the origin form the parser resolved. The
 * assignment is now {@code this.eForm = eForm}, so the real origin form reference flows to the
 * {@code redefinesMap(Form)} call ({@code nacaLib.varEx.VarLevel.redefinesMap(Form) ->
 * nacaLib.varEx.MapRedefine}), matching the parser's intent.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: a form redefine bears
 * {@code FORM} data type, needs no {@code val} ({@code isValNeeded() == false}), bears no
 * accessors ({@code HasAccessors() == false}), has no reachable write-accessor protocol
 * ({@code ExportWriteAccessorTo -> ""}, unused), contributes no type declaration
 * ({@code GetTypeDecl -> ""}, unused), and contributes no XML/.res node of its own
 * ({@code DoXMLExport -> null}).
 *
 * @author U930CV
 */
public class CEntityFormRedefine extends CEntityResourceForm //CEntityAttribute
{
    /**
     * @param l
     * @param name
     * @param cat
     */
    protected CDataEntity eForm = null ;
    //protected CEntityResourceForm eForm = null ;
    /** Executes the clear operation. */
    public void Clear()
    {
        super.Clear();
        eForm = null ;
    }

    /** Creates a new centity form redefine instance. */
    public CEntityFormRedefine(int l, String name, CObjectCatalog cat, CDataEntity eForm, boolean bSaveMap)
    {
        super(l, name, cat, bSaveMap);
        if (name.equals(""))
        {
            name = GetDefaultName() ;
            if (!name.equals(""))
            {
                SetName(name) ;
            }
        }
        // Fixed latent self-assignment: the legacy constructor assigned the parameter to itself
        // (the parameter shadows the field), leaving the field null. Bind the resolved origin form.
        this.eForm = eForm ;
    }
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.FORM ;
    }
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false ;
    }
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
    {
        return null;
    }

    // ---------------------------------------------------------------------------------------------
    // Retired direct backend generate.java.forms.CJavaFormRedefine: the output protocols below were
    // moved out of the generate layer onto this pure semantic entity when the backend was retired
    // onto the recursive ST4 assembly contract. They read only precomputed state; the two
    // generate-layer operations the backend performed inline (identifier formatting and the
    // declaration/block rendering) are injected by the generate-layer factory
    // (BmsJavaEntities.formRedefine), so this tree names no generate.* class.
    // ---------------------------------------------------------------------------------------------

    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        // Preserved from the retired backend: a form redefine bears no accessors.
        return false;
    }

    public boolean isValNeeded()
    {
        // Preserved from the retired backend: a form redefine is never declared as a val.
        return false;
    }

    /** Executes the get type decl operation. */
    public String GetTypeDecl()
    {
        // Preserved from the retired backend: a form redefine contributes no type declaration
        // (unused).
        return "";
    }

    /** Executes the do xmlexport operation. */
    public Element DoXMLExport()
    {
        // Preserved from the retired backend: a form redefine contributes no XML/.res node of its
        // own (unused).
        return null;
    }

    /**
     * Read-only getter exposing the origin form this redefine re-views. Consumed by the
     * generate-layer declaration bridge ({@code BmsJavaEntities.renderFormRedefineDeclaration})
     * to pre-render the {@code redefinesMap(<ref>)} argument through the exact legacy
     * {@code LegacyDataRenderer.renderReference} protocol. A plain field read.
     */
    public CDataEntity getForm()
    {
        return eForm ;
    }

    // The identifier formatter (setIdentifierFormatter / getFormattedName) is inherited from
    // CEntityResourceForm. The generate-layer factory (BmsJavaEntities.formRedefine) injects
    // the bound output's FormatIdentifier through the inherited setter; a hand-built entity
    // inherits the neutral legacy fallback ('-'->'_', '#'->'$').

    /**
     * The pre-rendered origin form reference consumed by the
     * {@code recursiveFormRedefineDeclarationEntity} template: the
     * {@code redefinesMap(<ref>)} argument. Pre-rendered by the generate-layer bridge through
     * the exact legacy {@code LegacyDataRenderer.renderReference} protocol, preserving byte
     * parity. A plain field read.
     */
    private String redefinesReference = "" ;

    /** Sets the redefines reference. */
    public void setRedefinesReference(String reference)
    {
        if (reference != null)
        {
            redefinesReference = reference ;
        }
    }

    public String getRedefinesReference()
    {
        return redefinesReference ;
    }
}
