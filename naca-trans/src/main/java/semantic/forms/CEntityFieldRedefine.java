/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;


import java.util.Vector;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.expression.CExpression;

import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.expression.CBaseEntityExpression;
import utils.CObjectCatalog;

/**
 * BMS map-resource DSL: an edit field redefining (a sub-view over) an existing
 * screen-map field, lowered from a BMS {@code MAP}/{@code MAPREDEFINE} working-storage
 * structure ({@code parser/Cobol/elements/CWorkingEntry.DoSemanticAnalysisForMapRedefine}
 * -&gt; {@code factory.NewEntityFieldRedefine}).
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaFieldRedefine}
 * was retired onto the recursive ST4 assembly contract. The backend had two live output
 * protocols, both preserved target-neutrally here:
 * <ul>
 *   <li><b>data reference</b> — {@code ExportReference(nLine) == formatIdentifier(GetName())}.
 *       A reference to the redefining field now renders through the BMS forms-island binding
 *       {@code semantic.forms.CEntityFieldRedefine -> recursiveFieldRedefineEntity}, whose
 *       template reads only {@code entity.formattedName} (the precomputed, target-formatted
 *       identifier). {@code LegacyDataRenderer.renderReference} ignores a semantic-declared
 *       {@code ExportReference} and falls through to that binding, reproducing the backend's
 *       formatted-name reference exactly.</li>
 *   <li><b>declaration block</b> — {@code DoExport} emitted
 *       {@code Edit <name> = declare.level(<int level>)[.pic(<pic>)][.justifyRight()][.blankWhenZero()].edit() ;}
 *       followed by a {@code { ... }} block over the field's child attributes. The declaration
 *       LINE now renders declaratively through the {@code recursiveFieldRedefineDeclarationEntity}
 *       template (invoked by the generate-layer factory bridge below); the block and the child
 *       fields keep rendering through the still-direct BMS field backends, which the surrounding
 *       {@code CJavaForm} legacy traversal reflectively invokes.</li>
 * </ul>
 *
 * <p>This tree names no {@code generate.*} class, so the dependency arrow stays
 * generate -&gt; semantic. The two generate-layer protocols the retired backend performed
 * inline are supplied by the generate-layer factory ({@code generate.java.forms.BmsJavaEntities
 * .fieldRedefine}): a neutral identifier {@link Function} (mirrors {@code CEntityFieldOccurs}'s
 * injected formatter, standing in for {@code LegacyLanguageRenderer.formatIdentifier}) and a
 * declaration {@link Consumer} that renders the declaration line through the recursive assembler
 * and drives the {@code startBlock/exportChildren/endBlock} block over the still-direct children.
 * A hand-built entity (no factory) defaults to the neutral identifier normalization and a no-op
 * declaration renderer, so it stays well-formed and never fails.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: a redefining edit
 * field is always an entry field ({@code IsEntryField() == true}), carries {@code FIELD} data
 * type, needs no {@code val} ({@code isValNeeded() == false}), has no reachable write-accessor
 * protocol ({@code ExportWriteAccessorTo -> ""}, unused), declares no type decl
 * ({@code GetTypeDecl() -> ""}, unused) and contributes no XML/.res node of its own
 * ({@code DoXMLExport -> null}).
 *
 * @author U930CV
 */
public class CEntityFieldRedefine extends CEntityResourceField
{
	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public String csLevel = "" ;
	public CEntityFieldRedefine(int l, String name, CObjectCatalog cat, String level)
	{
		super(l, name, cat);
		csLevel = level;
	}
	public CDataEntity GetArrayReference(Vector v, CBaseEntityFactory factory)
	{
		CEntityFieldArrayReference e = factory.NewEntityFieldArrayReference(getLine()) ;
		e.SetReference(this) ;
		for (int i=0; i<v.size(); i++)
		{
			CExpression expr = (CExpression)v.get(i);
			CBaseEntityExpression exp = expr.AnalyseExpression(factory);
			e.AddIndex(exp);
		}
		return e ;
	}

	public boolean IsEntryField()
	{
		// Preserved from the retired backend: a redefining edit field is always an entry field.
		return true ;
	}

	public Element DoXMLExport(Document doc, CResourceStrings res)
	{
		// Preserved from the retired backend: a redefining field contributes no XML/.res node
		// of its own. Target-neutral semantic state.
		return null ;
	}

	public CDataEntityType GetDataType()
	{
		// Preserved from the retired backend.
		return CDataEntityType.FIELD ;
	}

	public boolean isValNeeded()
	{
		// Preserved from the retired backend: a redefining edit field is never declared as a val.
		return false ;
	}

	public String GetTypeDecl()
	{
		// Preserved from the retired backend: unused.
		return "" ;
	}

	protected void RegisterMySelfToCatalog()
	{
		// a Field Redefined must not register itself, because it depends on aliases found in
		// original program : only those aliases are registered
	}

	/**
	 * Target-neutral identifier formatter standing in for the retired backend's
	 * {@code LegacyLanguageRenderer.formatIdentifier(GetName())}. Installed by the
	 * generate-layer factory ({@code BmsJavaEntities.fieldRedefine} injects the bound output's
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
	 * Pure read-only getter consumed by both the {@code recursiveFieldRedefineEntity} reference
	 * binding and the {@code recursiveFieldRedefineDeclarationEntity} declaration template.
	 * Exposes the field name formatted through the injected target-specific formatter. A pure
	 * formatting step over precomputed state — no FormatIdentifier resolution, no data-reference
	 * resolution, no lowering.
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
	 * Pure read-only getter consumed by the declaration template: the optional
	 * {@code .pic("<picture>")} clause, mirroring the retired backend's DoExport branch exactly.
	 * An empty {@code type} emits no clause; {@code pic9} builds the numeric picture from the
	 * precomputed {@code nLength}/{@code nDecimals}; any other non-empty type emits the
	 * precomputed {@code format} when set. A pure computation over precomputed state.
	 */
	public String getPicClause()
	{
		if (type.equals(""))
		{
			return "" ;
		}
		StringBuilder clause = new StringBuilder(".pic(") ;
		if (type.equals("pic9"))
		{
			clause.append('"') ;
			for (int i=0; i < nLength; i++)
			{
				clause.append('9') ;
			}
			if (nDecimals>0)
			{
				clause.append('.') ;
				for (int i=0; i < nDecimals; i++)
				{
					clause.append('9') ;
				}
			}
			clause.append('"') ;
		}
		else if (!format.equals(""))
		{
			clause.append('"').append(format).append('"') ;
		}
		clause.append(')') ;
		return clause.toString() ;
	}

	/**
	 * Pure read-only getter consumed by the declaration template: the optional
	 * {@code .justifyRight()} clause (nacaLib.varEx.VarLevel/Edit fluent call), mirroring the
	 * retired backend's {@code isrightJustified} branch. A pure computation over precomputed state.
	 */
	public String getJustifyRightClause()
	{
		return isrightJustified ? ".justifyRight()" : "" ;
	}

	/**
	 * Pure read-only getter consumed by the declaration template: the optional
	 * {@code .blankWhenZero()} clause (nacaLib.varEx.VarLevel/Edit fluent call), mirroring the
	 * retired backend's {@code isblankWhenZero} branch. A pure computation over precomputed state.
	 */
	public String getBlankWhenZeroClause()
	{
		return isblankWhenZero ? ".blankWhenZero()" : "" ;
	}

}
