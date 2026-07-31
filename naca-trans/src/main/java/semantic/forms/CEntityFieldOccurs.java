/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 22 oct. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;



import java.util.function.Consumer;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CDataEntity;
import utils.CObjectCatalog;

/**
 * BMS map-resource DSL: a screen-map OCCURS group field, lowered from a COBOL group
 * item carrying {@code OCCURS} inside a BMS {@code MAP}/{@code MAPREDEFINE} working-storage
 * structure ({@code parser/Cobol/elements/CWorkingEntry.DoSemanticAnalysisForMapRedefine},
 * the {@code le.occurs != null} branch -> {@code factory.NewEntityFieldOccurs}).
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaFieldOccurs}
 * was retired onto the recursive ST4 assembly contract. The backend had two live output
 * protocols, both preserved target-neutrally here:
 * <ul>
 *   <li><b>data reference</b> — {@code ExportReference(nLine) == formatIdentifier(GetName())}.
 *       A reference to the occurs group now renders through the BMS forms-island binding
 *       {@code semantic.forms.CEntityFieldOccurs -> recursiveFieldOccursEntity}, whose
 *       template reads only {@code entity.formattedName} (the precomputed, target-formatted
 *       identifier). {@code LegacyDataRenderer.renderReference} ignores a semantic-declared
 *       {@code ExportReference} and falls through to that binding, reproducing the backend's
 *       formatted-name reference exactly.</li>
 *   <li><b>declaration block</b> — {@code DoExport} emitted
 *       {@code Edit <name> = declare.level(<int level>).editOccurs(<occurs ref>, "<name>") ;}
 *       followed by a {@code { ... }} block over the group's child fields. The declaration
 *       LINE now renders declaratively through the {@code recursiveFieldOccursDeclarationEntity}
 *       template (invoked by the generate-layer factory bridge below); the block and the child
 *       fields keep rendering through the still-direct BMS field backends, which the surrounding
 *       {@code CJavaForm}/{@code CJavaFieldRedefine} legacy traversal reflectively invokes.</li>
 * </ul>
 *
 * <p>This tree names no {@code generate.*} class, so the dependency arrow stays
 * generate -&gt; semantic. The two generate-layer protocols the retired backend performed
 * inline are supplied by the generate-layer factory ({@code generate.java.forms.BmsJavaEntities
 * .fieldOccurs}): a neutral identifier {@link Function} (mirrors {@code CEntityLabelField}'s
 * injected formatter, standing in for {@code LegacyLanguageRenderer.formatIdentifier}) and a
 * declaration {@link Consumer} that pre-renders the occurs reference through the exact legacy
 * {@code LegacyDataRenderer.renderReference} protocol, renders the declaration line through the
 * recursive assembler, and drives the {@code startBlock/exportChildren/endBlock} block over the
 * still-direct children. A hand-built entity (no factory) defaults to the neutral identifier
 * normalization and a no-op declaration renderer, so it stays well-formed and never fails.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: an occurs group is
 * never an entry field ({@code IsEntryField() == false}), carries {@code FIELD} data type,
 * needs no {@code val} ({@code isValNeeded() == false}), has no reachable write-accessor
 * protocol ({@code ExportWriteAccessorTo -> null}), and contributes no XML/.res node of its
 * own ({@code DoXMLExport -> null}; the group's child fields export their own XML through the
 * {@code CEntityResourceForm.ExportXMLFields} traversal).
 *
 * @author U930CV
 */
public class CEntityFieldOccurs extends CEntityResourceField
{
	protected CDataEntity occurs = null ;
	protected String csLevel = "" ;
	public void Clear()
	{
		super.Clear() ;
		occurs = null ;
	}

	public CEntityFieldOccurs(int l, String name, CObjectCatalog cat)
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
	}
	public void SetFieldOccurs(String level, CDataEntity occurs)
	{
		this.occurs = occurs ;
		csLevel = level ;
	}

	public boolean IsEntryField()
	{
		return false;
	}

	public String GetTypeDecl()
	{
		return null;
	}

	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD;
	}
	protected void RegisterMySelfToCatalog()
	{
		String name = GetName() ;
		programCatalog.RegisterDataEntity(name, this) ;
//		programCatalog.RegisterDataEntity(name+"I", this) ;
//		programCatalog.RegisterDataEntity(name+"O", this) ;
	}

	public boolean isValNeeded()
	{
		// Preserved from the retired backend: an occurs group is never declared as a val.
		return false;
	}

	public String ExportReference(int nLine)
	{
		// Mirrors the retired backend's formatIdentifier(GetName()). LegacyDataRenderer
		// ignores a semantic-declared ExportReference and renders the reference through the
		// recursiveFieldOccursEntity binding instead; this override stays for direct callers
		// and reads only the precomputed, target-formatted identifier.
		return getFormattedName() ;
	}

	public String ExportWriteAccessorTo(String value)
	{
		// Preserved from the retired backend: no reachable write-accessor protocol.
		return null ;
	}

	public Element DoXMLExport(Document doc, CResourceStrings res)
	{
		// Preserved from the retired backend: an occurs group contributes no XML/.res node
		// of its own (its child fields export their own nodes). Target-neutral semantic state.
		return null ;
	}

	protected void DoExport()
	{
		// Legacy traversal bridge: the surrounding CJavaForm/CJavaFieldRedefine DoExport
		// reflectively invokes this. The declaration line + block are rendered through the
		// recursive ST4 assembly contract by the generate-layer renderer the factory injects;
		// a hand-built entity (no factory) is a no-op and never fails.
		declarationRenderer.accept(this) ;
	}

	/**
	 * Target-neutral identifier formatter standing in for the retired backend's
	 * {@code LegacyLanguageRenderer.formatIdentifier(GetName())}. Installed by the
	 * generate-layer factory ({@code BmsJavaEntities.fieldOccurs} injects the bound output's
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
	 * Pure read-only getter consumed by both the {@code recursiveFieldOccursEntity} reference
	 * binding and the {@code recursiveFieldOccursDeclarationEntity} declaration template.
	 * Exposes the group name formatted through the injected target-specific formatter (the
	 * retired backend used the same formatted string for the Java variable name and the quoted
	 * {@code editOccurs} argument). A pure formatting step over precomputed state — no
	 * FormatIdentifier resolution, no data-reference resolution, no lowering.
	 */
	public String getFormattedName()
	{
		return identifierFormatter.apply(GetName()) ;
	}

	/**
	 * Pure read-only getter consumed by the declaration template: the {@code declare.level(<n>)}
	 * argument. Mirrors the retired backend's {@code Integer.parseInt(csLevel)} (the COBOL
	 * formal level parsed to an int). A pure computation over precomputed state.
	 */
	public int getLevel()
	{
		return Integer.parseInt(csLevel) ;
	}

	/**
	 * Pure read-only view of the {@code OCCURS} reference slot (the {@code OCCURS DEPENDING ON}
	 * counter the parser resolved via {@code le.occurs.GetDataEntity}). Exposed for the
	 * generate-layer bridge (and tests); the declaration template reads the pre-rendered
	 * {@link #getOccursReference()} string, not this entity.
	 */
	public CDataEntity getOccurs()
	{
		return occurs ;
	}

	/**
	 * Pre-rendered {@code editOccurs(<ref>, ...)} argument. Computed by the generate-layer
	 * declaration renderer through the exact legacy {@code LegacyDataRenderer.renderReference}
	 * protocol (preserving its ExportReference-first-then-assembler byte parity) immediately
	 * before the declaration template renders; the template reads it as a plain precomputed
	 * string. A pure field read — no reference resolution happens when ST4 accesses it.
	 */
	private String occursReference = "" ;

	public void setOccursReference(String occursReference)
	{
		this.occursReference = occursReference == null ? "" : occursReference ;
	}

	public String getOccursReference()
	{
		return occursReference ;
	}

	/**
	 * Generate-layer declaration renderer (the retired backend's {@code DoExport} body, moved
	 * out of the semantic tree). Invoked from {@link #DoExport()} when the legacy traversal
	 * reaches this group. Defaults to a no-op so a hand-built entity stays well-formed.
	 */
	private Consumer<CEntityFieldOccurs> declarationRenderer = entity -> {};

	public void setDeclarationRenderer(Consumer<CEntityFieldOccurs> renderer)
	{
		if (renderer != null)
		{
			declarationRenderer = renderer ;
		}
	}
}
