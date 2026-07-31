/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jan 10, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CBaseEntityFactory;
import utils.CObjectCatalog;

/**
 * BMS map-resource DSL: a screen-map field ARRAY, lowered from a run of same-named
 * indexed BMS fields ({@code FLD(1)}, {@code FLD(2)}, ...) that {@code CMapElement}
 * collapses into a {@code parser.map_elements.CFieldArray} motif
 * ({@code CMapElement.ManageArray} -> {@code CFieldArray.ReadField}), whose
 * {@code DoSemanticAnalysis} builds this entity via {@code factory.NewEntityFieldArray()}.
 * BMS is a CICS screen-map DSL, never a COBOL dialect.
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaFieldArray}
 * was retired onto the recursive-ST4 assembly contract. Like the retired label-field
 * backend, a field array emits <b>no Java of its own</b>: the backend's {@code DoExport}
 * only traversed its child motif fields ({@code LegacyLanguageRenderer.exportChildren(this,
 * false)}) with no declaration line and no surrounding block, and its
 * {@code ExportReference}/{@code ExportWriteAccessorTo}/{@code GetTypeDecl} were empty
 * stubs — so there is no ST4 template/binding for this entity (its ledger item carries
 * {@code manifestBinding/template == null}; the {@code semantic/forms} subtree is excluded
 * from the COBOL declarative-manifest gate exactly as for {@code CEntityLabelField}). The
 * child motif fields keep rendering through the still-direct BMS field backends the
 * surrounding {@code CJavaForm} legacy traversal reflectively invokes.
 *
 * <p>The only own output protocol is the BMS XML/.res artifact {@code DoXMLExport}, which
 * stays target-neutral here (the {@code BMS-XML-OUTPUT-IN-SEMANTIC} discovered debt records
 * the whole BMS XML/.res protocol as semantic-resident and directs retirement slices to keep
 * it "target-neutral semantic state"). The retired backend's DOM building — an
 * {@code <array nbCol nbItems vert line col>} element wrapping a single {@code <item>} that
 * aggregates each child field's XML — is preserved exactly; it reads only precomputed
 * semantic state ({@code nbColumns}, {@code nbItems}, {@code isverticalFilling},
 * {@code nPosLine}, {@code nPosCol}) and the children's own {@code DoXMLExport}.
 *
 * <p>This tree names no {@code generate.*} class, so the dependency arrow stays
 * generate -&gt; semantic. The single generate-layer operation the retired backend performed
 * inline (the {@code DoExport} child traversal) is supplied by the generate-layer factory
 * ({@code generate.java.forms.BmsJavaEntities.fieldArray} binds the active output and injects
 * a declaration {@link Consumer} that drives {@code LegacyLanguageRenderer.exportChildren});
 * a hand-built entity (no factory) defaults to a no-op renderer, so it stays well-formed and
 * never fails.
 *
 * <p>Latent defect fixed in this slice: the legacy {@link #SetArray(int, int, boolean)}
 * assigned two parameters to themselves ({@code nbItems = nbItems;} and
 * {@code bVerticalFilling = bVerticalFilling;} — each parameter shadows/no-ops, so the
 * {@code nbItems} field stayed {@code 0} and the {@code isverticalFilling} field stayed
 * {@code false} regardless of what the parser resolved via {@code CFieldArray.ReadField}).
 * Only {@code nbColumns = NbCol} reached its field. The XML artifact therefore always emitted
 * {@code nbItems="0"} and {@code vert="false"}. The assignments now bind the real resolved
 * values ({@code this.nbItems = nbItems}, {@code this.isverticalFilling = bVerticalFilling}),
 * matching the parser's intent instead of preserving the buggy zero/false byte-for-byte — the
 * same class of self-assignment the {@code CEntitySkipFields} retirement fixed.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: a field array is never
 * an entry field ({@code IsEntryField() == false}), carries {@code FIELD} data type, needs no
 * {@code val} ({@code isValNeeded() == false}), and bears no type decl / reference /
 * write-accessor output ({@code GetTypeDecl}/{@code ExportReference}/{@code ExportWriteAccessorTo}
 * all empty).
 *
 * @author U930CV
 */
public class CEntityResourceFieldArray extends CEntityResourceField
{

	/**
	 * @param l
	 * @param name
	 * @param cat
	 */
	public CEntityResourceFieldArray(int l, String name, CObjectCatalog cat)
	{
		super(l, name, cat);
	}

	public CDataEntityType GetDataType()
	{
		return CDataEntityType.FIELD ;
	}
	public boolean IsEntryField()
	{
		return false;
	}

	public void SetArray(int nbItems, int NbCol, boolean bVerticalFilling)
	{
		// Fixed latent self-assignment: the legacy body assigned the parameters to themselves
		// (nbItems = nbItems; bVerticalFilling = bVerticalFilling;), leaving the nbItems field 0
		// and the isverticalFilling field false regardless of the parser-resolved values. Bind
		// the real counts so the XML artifact reports the true motif layout.
		this.nbItems = nbItems ;
		nbColumns = NbCol ;
		this.isverticalFilling = bVerticalFilling ;
	}

	protected int nbItems = 0 ;
	protected int nbColumns = 0 ;
	protected boolean isverticalFilling = false ;

	public void SetPosition(int Line, int Col)
	{
		nPosCol = Col ;
		nPosLine = Line ;
	}
	public void InitDependences(CBaseEntityFactory factory)
	{
		ListIterator iter = lstChildren.listIterator() ;
		try
		{
			CEntityResourceField field = (CEntityResourceField)iter.next() ;
			while (field != null)
			{
				field.InitDependences(factory) ;
				field = (CEntityResourceField)iter.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
		}
	}

	public String GetTypeDecl()
	{
		// Preserved from the retired backend: a field array bears no type decl.
		return "";
	}

	public String ExportReference(int nLine)
	{
		// Preserved from the retired backend: a field array bears no reference output of its own
		// (an element access lowers through the separate CEntityFieldArrayReference entity).
		return "" ;
	}

	public String ExportWriteAccessorTo(String value)
	{
		// Preserved from the retired backend: no reachable write-accessor protocol (unused).
		return "" ;
	}

	public boolean isValNeeded()
	{
		// Preserved from the retired backend: a field array is never declared as a val.
		return false;
	}

	protected void DoExport()
	{
		// Legacy traversal bridge: the surrounding CJavaForm DoExport reflectively invokes this.
		// The retired backend emitted no Java of its own — it only traversed its child motif
		// fields. That generate-layer traversal is supplied by the generate-layer renderer the
		// factory injects; a hand-built entity (no factory) is a no-op and never fails.
		declarationRenderer.accept(this) ;
	}

	/* (non-Javadoc)
	 * @see semantic.forms.CEntityResourceField#DoXMLExport(org.w3c.dom.Document, semantic.forms.CResourceStrings)
	 */
	public Element DoXMLExport(Document doc, CResourceStrings res)
	{
		// Preserved byte-for-byte from the retired backend, target-neutral: an <array> element
		// wrapping a single <item> that aggregates each child motif field's XML. Reads only
		// precomputed semantic state and the children's own DoXMLExport.
		Element eArray = doc.createElement("array");
		eArray.setAttribute("nbCol", String.valueOf(nbColumns)) ;
		eArray.setAttribute("nbItems", String.valueOf(nbItems)) ;
		eArray.setAttribute("vert", String.valueOf(isverticalFilling)) ;
		eArray.setAttribute("line", String.valueOf(nPosLine)) ;
		eArray.setAttribute("col", String.valueOf(nPosCol)) ;

		Element eItem = doc.createElement("item") ;
		eArray.appendChild(eItem);
		ListIterator iter = lstChildren.listIterator() ;
		try
		{
			CEntityResourceField field = (CEntityResourceField)iter.next() ;
			while (field != null)
			{
				Element e = field.DoXMLExport(doc, res) ;
				if (e != null)
				{
					eItem.appendChild(e) ;
				}
				field = (CEntityResourceField)iter.next() ;
			}
		}
		catch (NoSuchElementException e)
		{
		}
		return eArray ;
	}

	/**
	 * Pure read-only getter consumed by the XML artifact and tests: the number of motif
	 * occurrences the parser resolved ({@code CFieldArray.nbItems}). A plain field read; the
	 * latent self-assignment that zeroed this value is fixed in {@link #SetArray}.
	 */
	public int getNbItems()
	{
		return nbItems ;
	}

	/**
	 * Pure read-only getter consumed by the XML artifact and tests: the number of array columns
	 * the parser resolved ({@code CFieldArray.nbCol}). A plain field read.
	 */
	public int getNbColumns()
	{
		return nbColumns ;
	}

	/**
	 * Pure read-only getter consumed by the XML artifact and tests: whether the array fills
	 * vertically ({@code CFieldArray.isverticalFilling}). A plain field read; the latent
	 * self-assignment that pinned this false is fixed in {@link #SetArray}.
	 */
	public boolean isVerticalFilling()
	{
		return isverticalFilling ;
	}

	/**
	 * Pure read-only getter consumed by the XML artifact and tests: the array's screen line
	 * ({@code nPosLine}, set via {@link #SetPosition}). A plain field read.
	 */
	public int getPosLine()
	{
		return nPosLine ;
	}

	/**
	 * Pure read-only getter consumed by the XML artifact and tests: the array's screen column
	 * ({@code nPosCol}, set via {@link #SetPosition}). A plain field read.
	 */
	public int getPosCol()
	{
		return nPosCol ;
	}

	/**
	 * Generate-layer declaration renderer (the retired backend's {@code DoExport} child-traversal
	 * body, moved out of the semantic tree). Invoked from {@link #DoExport()} when the legacy
	 * traversal reaches this array. Defaults to a no-op so a hand-built entity stays well-formed.
	 */
	private Consumer<CEntityResourceFieldArray> declarationRenderer = entity -> {};

	public void setDeclarationRenderer(Consumer<CEntityResourceFieldArray> renderer)
	{
		if (renderer != null)
		{
			declarationRenderer = renderer ;
		}
	}
}
