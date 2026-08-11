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
 * BMS map-resource DSL: a screen-map SKIPFIELD run, lowered from a BMS
 * {@code MAP}/{@code MAPREDEFINE} working-storage structure
 * ({@code parser/Cobol/elements/CWorkingEntry.DoSemanticAnalysisForMapRedefine}, the
 * skip-field branches -> {@code factory.NewEntityWorkingSkipField(line, name, nbFields, level)}).
 * A skip field consumes {@code nbFields} screen bytes that carry no addressable field of their
 * own; it emits a single {@code declare.level(<n>).editSkip(<nbFields>)} declaration.
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaSkipField} was
 * retired onto the recursive ST4 assembly contract. The backend had two live output protocols,
 * both preserved target-neutrally here:
 * <ul>
 *   <li><b>data reference</b> — {@code ExportReference(nLine) == formatIdentifier(GetName())}.
 *       A reference to the skip field now renders through the BMS forms-island binding
 *       {@code semantic.forms.CEntitySkipFields -> recursiveSkipFieldEntity}, whose template
 *       reads only {@code entity.formattedName} (the precomputed, target-formatted identifier).
 *       {@code LegacyDataRenderer.renderReference} ignores a semantic-declared
 *       {@code ExportReference} and falls through to that binding, reproducing the backend's
 *       formatted-name reference exactly.</li>
 *   <li><b>declaration block</b> — {@code DoExport} emitted
 *       {@code Edit <name> = declare.level(<int level>).editSkip(<nbFields>) ;} followed by a
 *       {@code { ... }} block over any child fields. The declaration LINE now renders
 *       declaratively through the {@code recursiveSkipFieldDeclarationEntity} template (invoked
 *       by the generate-layer factory bridge {@code BmsJavaEntities.renderSkipFieldDeclaration});
 *       the block and the child fields keep rendering through the still-direct BMS field
 *       backends, which the surrounding {@code CJavaForm}/{@code CJavaFieldRedefine} legacy
 *       traversal reflectively invokes.</li>
 * </ul>
 *
 * <p>This tree names no {@code generate.*} class, so the dependency arrow stays
 * generate -&gt; semantic. The two generate-layer protocols the retired backend performed inline
 * are supplied by the generate-layer factory ({@code generate.java.forms.BmsJavaEntities
 * .skipFields}): a neutral identifier {@link Function} (mirrors {@code CEntityLabelField}'s
 * injected formatter, standing in for {@code LegacyLanguageRenderer.formatIdentifier}) and a
 * declaration {@link Consumer} that renders the declaration line through the recursive assembler
 * and drives the {@code startBlock/exportChildren/endBlock} block over the still-direct children.
 * A hand-built entity (no factory) defaults to the neutral identifier normalization and a no-op
 * declaration renderer, so it stays well-formed and never fails.
 *
 * <p>Latent defect fixed in this slice: the legacy constructor assigned the constructor
 * parameter to itself ({@code nbFields = nbFields;} — the parameter shadows the field), so the
 * {@code nbFields} field stayed {@code 0} and every retired backend emitted
 * {@code editSkip(0)} regardless of the byte count the parser resolved via
 * {@code eForm.ConsumeFieldsAsBytes(le.length)}. The assignment is now {@code this.nbFields =
 * nbFields}, so the real consumed-field count flows to the {@code editSkip(int)} call
 * ({@code nacaLib.varEx.VarLevel.editSkip(int)}), matching the parser's intent instead of
 * preserving the buggy {@code editSkip(0)} byte-for-byte.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: a skip field is never an
 * entry field ({@code IsEntryField() == false}), carries {@code FIELD} data type, needs no
 * {@code val} ({@code isValNeeded() == false}), bears no accessors ({@code HasAccessors() ==
 * false}), has no reachable write-accessor protocol ({@code ExportWriteAccessorTo -> null}), and
 * contributes no XML/.res node of its own ({@code DoXMLExport -> null}).
 *
 * @author U930CV
 */
public class CEntitySkipFields extends CEntityResourceField
{
	protected int nbFields = 0 ;
	protected String csLevel = "" ;
	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public CEntitySkipFields(int l, String name, CObjectCatalog cat, int nbFields, String level)
	{
		super(l, name, cat);
		if (name.equals(""))
		{
			name = GetDefaultName() ;
			if (!name.equals(""))
			{
				SetName(name) ;
			}
		}
		// Fixed latent self-assignment: the legacy constructor assigned the parameter to itself
		// (the parameter shadows the field), leaving the field 0. Bind the resolved count.
		this.nbFields = nbFields ;
		csLevel = level ;
	}
	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetDataType()
	 */
	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD ;
	}
	public boolean ignore()
	{
		return false ;
	}
	public String GetConstantValue()
	{
		return "" ;
	}

	public boolean IsEntryField()
	{
		return false;
	}

	public String GetTypeDecl()
	{
		return null;
	}
	public Element DoXMLExport(Document doc, CResourceStrings res)
	{
		// Preserved from the retired backend: a skip field contributes no XML/.res node of its
		// own. Target-neutral semantic state.
		return null;
	}
	protected void RegisterMySelfToCatalog()
	{
		String name = GetName() ;
		programCatalog.RegisterDataEntity(name, this) ;
	}

	public boolean HasAccessors()
	{
		// Preserved from the retired backend: a skip field bears no accessors.
		return false;
	}

	public boolean isValNeeded()
	{
		// Preserved from the retired backend: a skip field is never declared as a val.
		return false;
	}

	/**
	 * Target-neutral identifier formatter standing in for the retired backend's
	 * {@code LegacyLanguageRenderer.formatIdentifier(GetName())}. Installed by the generate-layer
	 * factory ({@code BmsJavaEntities.skipFields} injects the bound output's
	 * {@code FormatIdentifier}); defaults to the neutral legacy fallback so a directly
	 * constructed entity stays well-formed. A pure injected value — no {@code generate.*}
	 * coupling lives in this tree.
	 */
	private Function<String, String> identifierFormatter =
		identifier -> identifier.replace('-', '_').replace('#', '$');

	public void setIdentifierFormatter(Function<String, String> formatter)
	{
		if (formatter != null)
		{
			identifierFormatter = formatter ;
		}
	}

	/**
	 * Pure read-only getter consumed by both the {@code recursiveSkipFieldEntity} reference
	 * binding and the {@code recursiveSkipFieldDeclarationEntity} declaration template. Exposes
	 * the skip-field name formatted through the injected target-specific formatter. A pure
	 * formatting step over precomputed state — no FormatIdentifier resolution, no data-reference
	 * resolution, no lowering.
	 */
	public String getFormattedName()
	{
		return identifierFormatter.apply(GetName()) ;
	}

	/**
	 * Pure read-only getter consumed by the declaration template: the {@code declare.level(<n>)}
	 * argument. Mirrors the retired backend's {@code Integer.parseInt(csLevel)} (the COBOL formal
	 * level parsed to an int). A pure computation over precomputed state.
	 */
	public int getLevel()
	{
		return Integer.parseInt(csLevel) ;
	}

	/**
	 * Pure read-only getter consumed by the declaration template: the {@code editSkip(<n>)}
	 * argument — the count of screen bytes the parser resolved for this skip run. A plain field
	 * read; the latent self-assignment that zeroed this value is fixed in the constructor.
	 */
	public int getNbFields()
	{
		return nbFields ;
	}

}
