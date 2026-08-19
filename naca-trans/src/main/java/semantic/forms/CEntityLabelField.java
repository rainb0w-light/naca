/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;

import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import utils.CObjectCatalog;

/**
 * BMS map-resource DSL: a screen-map label/title field, lowered from an unnamed
 * {@code DFHMDF} (parser/map_elements/CFieldElement, no field name) or mirrored for a
 * save-map copy (CEntityResourceForm.MakeSavCopy).
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaLabelField}
 * was retired. A label field emits <b>no Java</b> of its own: the retired backend's
 * {@code DoExport()} was empty (the surrounding {@code CJavaForm} traversal reflectively
 * invokes it and gets nothing), and {@code ExportReference}/{@code ExportWriteAccessorTo}/
 * {@code GetTypeDecl} were unused empty stubs — so there is no Java template/binding for
 * this entity (its ledger item carries {@code manifestBinding/template == null}). Its only
 * live output protocol is the BMS XML/.res artifact {@code DoXMLExport}, which the
 * semantic form/container traversal ({@code CEntityResourceForm.ExportXMLFields} ->
 * {@code CEntityResourceFormContainer.MakeXMLOutput}) dispatches polymorphically through
 * the abstract {@link CEntityResourceField#DoXMLExport(Document, CResourceStrings)}.
 *
 * <p>That XML emission stays target-neutral here (the {@code BMS-XML-OUTPUT-IN-SEMANTIC}
 * discovered debt records the whole BMS XML/.res protocol as semantic-resident and directs
 * retirement slices to keep it "target-neutral semantic state"). The retired backend's DOM
 * building is preserved byte-for-byte; its <b>single</b> generate-layer call —
 * {@code generate.LegacyLanguageRenderer.formatIdentifier(this, csActiveChoiceValue)} in
 * the {@code linkedActiveChoice} branch — is replaced by a precomputed identifier formatter
 * installed from the generate-layer factory ({@code generate.java.forms.BmsJavaEntities
 * .labelField} binds the active output and injects {@code output::FormatIdentifier}). The
 * formatter is a neutral {@link java.util.function.Function}; this tree names no
 * {@code generate.*} class, so the dependency arrow stays generate -> semantic and there is
 * no generate coupling in the semantic tree (the guarantee the retired leaf siblings such
 * as {@code CEntityIsFieldColor}/{@code CEntityIsFieldCursor} enforce). The default
 * formatter mirrors {@code LegacyLanguageRenderer.formatIdentifier}'s no-output fallback so
 * a hand-built entity never fails.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: a label is never an
 * entry field ({@code IsEntryField() == false}), carries {@code FIELD} data type, needs no
 * {@code val}, and has no reachable write-accessor protocol.
 *
 * @author U930CV
 */
public class CEntityLabelField extends CEntityResourceField
{
    /**
     * @param l source line
     * @param cat the program catalog
     */
    public CEntityLabelField(int l, CObjectCatalog cat)
    {
        super(l, "", cat);
    }

    /** Executes the is entry field operation. */
    public boolean IsEntryField()
    {
        return false;
    }

    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        return CDataEntityType.FIELD ;
    }

    public boolean isValNeeded()
    {
        return false;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseExternalEntity#GetTypeDecl()
     */
    /** Executes the get type decl operation. */
    public String GetTypeDecl()
    {
        return "" ; // unused
    }

    /**
     * Target-neutral identifier formatter for the {@code linkedActiveChoice} XML branch.
     * Installed by the generate-layer factory ({@code BmsJavaEntities.labelField} injects
     * the bound output's {@code FormatIdentifier}); defaults to the neutral legacy fallback
     * so a directly-constructed entity stays well-formed. A pure injected value — no
     * {@code generate.*} coupling lives in this tree.
     */
    private Function<String, String> identifierFormatter =
        identifier -> identifier.replace('-', '_').replace('#', '$');

    /** Sets the identifier formatter. */
    public void setIdentifierFormatter(Function<String, String> formatter)
    {
        if (formatter != null)
        {
            identifierFormatter = formatter ;
        }
    }

    /**
     * Pure read-only view of the active-choice link slot, formatted through the injected
     * target-specific formatter. Exposed so the XML emission below (and tests) read only
     * precomputed state; the formatting is supplied by the generate-layer factory, never
     * resolved here.
     */
    public String getActiveChoiceLink()
    {
        return identifierFormatter.apply(csActiveChoiceValue) ;
    }

    /** Executes the do xmlexport operation. */
    public Element DoXMLExport(Document doc, CResourceStrings res)
    {
        Element ef ;
        if (mode == FieldMode.TITLE)
        {
            ef = doc.createElement("title") ;
        }
        else if (mode == FieldMode.HIDDEN)
        {
            return null ;
        }
        else if (mode == FieldMode.ACTIVE_CHOICE)
        {
            ef = doc.createElement("label") ;
            ef.setAttribute("type", "activeChoice") ;
            ef.setAttribute("activeChoiceValue", csActiveChoiceValue);
            ef.setAttribute("activeChoiceTarget", csActiveChoiceTarget);
            ef.setAttribute("activeChoiceSubmit", isactiveChoiceSubmit ?"true":"false");
        }
        else if (mode == FieldMode.LINKED_ACTIVE_CHOICE)
        {
            ef = doc.createElement("label") ;
            ef.setAttribute("type", "linkedActiveChoice") ;
            ef.setAttribute("activeChoiceLink", getActiveChoiceLink());
            ef.setAttribute("activeChoiceTarget", csActiveChoiceTarget);
            ef.setAttribute("activeChoiceSubmit", isactiveChoiceSubmit ?"true":"false");
        }
        else
        {
            ef = doc.createElement("label") ;
        }
        ef.setAttribute("length", String.valueOf(nLength)) ;
        ef.setAttribute("line", String.valueOf(nPosLine)) ;
        ef.setAttribute("col", String.valueOf(nPosCol)) ;
        if (!csInitialValue.equals(""))
        {
            ef.appendChild(res.exportResource(csInitialValue, doc)) ;
        }
        if (!csDisplayName.equals(""))
        {
            ef.setAttribute("name", csDisplayName);
        }
        if (!csColor.equals(""))
        {
            ef.setAttribute("color", csColor.toLowerCase());
        }
        if (!csHighLight.equals(""))
        {
            ef.setAttribute("highlighting", csHighLight.toLowerCase());
        }
        if (!csBrightness.equals(""))
        {
            ef.setAttribute("brightness", csBrightness.toLowerCase());
        }
        return ef ;
    }
}
