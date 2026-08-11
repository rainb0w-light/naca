/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;


import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jlib.xml.Tag;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseResourceEntity;
import semantic.CDataEntity;
import utils.CObjectCatalog;
import utils.CRulesManager;
import utils.Transcoder;

/**
 * BMS map-resource DSL: a screen-map <em>mapset</em> root (a CICS mapset container), lowered from a
 * BMS {@code .bms} {@code MAPSET} definition ({@code parser/map_elements/CMapSetElement} -&gt;
 * {@code factory.NewEntityFormContainer(line, name, save)}). A mapset is the top-level artifact: it
 * emits a whole {@code class <name> extends Map} skeleton (imports, two {@code Copy} factory methods,
 * a constructor) and then unfolds its maps ({@link CEntityResourceForm}) inside the class body.
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaFormContainer} was retired
 * onto the recursive ST4 assembly contract. The backend's live output protocols are preserved
 * target-neutrally here:
 * <ul>
 *   <li><b>data reference</b> — {@code ExportReference(nLine) == formatIdentifier(GetName())}. A
 *       reference to the mapset now renders through the BMS forms-island binding
 *       {@code semantic.forms.CEntityResourceFormContainer -> recursiveFormContainerEntity}, whose
 *       template reads only {@code entity.containerReference} (the target-formatted mapset name).
 *       {@code LegacyDataRenderer.renderReference} ignores a semantic-declared {@code ExportReference}
 *       and falls through to that binding, reproducing the backend's reference exactly.</li>
 *   <li><b>class skeleton</b> — {@code DoExport} emitted the mapset class (imports, {@code class
 *       <name> extends Map {}, the two {@code Copy} methods, the constructor) and then traversed
 *       {@code arrForm} via {@code invokeExport}. The class-header declaration LINE now renders
 *       declaratively through the {@code recursiveFormContainerDeclarationEntity} template (reading
 *       only {@code entity.mapClassName}); the surrounding imports / {@code Copy} methods /
 *       constructor / {@code { ... }} map block are driven by the generate-layer factory bridge
 *       {@code BmsJavaEntities.renderFormContainerDeclaration}, which iterates {@link #getForms()}
 *       (the production map collection populated via {@link #AddForm}, not the generic child list)
 *       through {@code invokeExport} — exactly the {@code arrForm} traversal the retired backend
 *       performed.</li>
 * </ul>
 *
 * <p><b>The BMS XML/.res artifact stays target-neutral.</b> {@link #MakeXMLOutput} reads the
 * <em>neutral</em> {@code CBaseLanguageEntity.getFormattedName()} (displayName / {@code '-'->'_'} /
 * {@code '#'->'$'}), exactly as it did before this retirement — the retired backend never overrode
 * {@code getFormattedName()}, so this entity deliberately does NOT override it either. The
 * target-specific identifier formatting the ST4 reference/declaration getters need is supplied by the
 * generate-layer factory through an injected {@link Function} (standing in for
 * {@code LegacyLanguageRenderer.formatIdentifier}) and is consumed only by {@link #getContainerReference}
 * and {@link #getMapClassName}; it never touches the XML/.res name.
 *
 * <p>This tree names no {@code generate.*} class, so the dependency arrow stays generate -&gt;
 * semantic. The generate-layer rendering the retired backend performed inline is supplied by the
 * generate-layer factory ({@code generate.java.forms.BmsJavaEntities.formContainer}): a neutral
 * identifier {@link Function} and a {@link Consumer} that renders the class skeleton through the
 * recursive assembler and drives the map block. A hand-built entity (no factory) defaults to the
 * neutral identifier normalization and a no-op skeleton renderer, so it stays well-formed and never
 * fails.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: a mapset bears {@code FORM}
 * data type, needs no {@code val} ({@code isValNeeded() == false}), bears no accessors
 * ({@code HasAccessors() == false}), has no reachable write-accessor protocol
 * ({@code ExportWriteAccessorTo -> ""}, unused), is needed as an in-class declaration
 * ({@code IsNeedDeclarationInClass() == true}), and contributes the mapset type declaration
 * ({@code GetTypeDecl -> [owner.GetTypeDecl() + "."] + GetName().replace('-','_')}).
 *
 * @author sly
 */
public class CEntityResourceFormContainer extends CBaseResourceEntity
{
	protected boolean bSaveCopy = false ;
	/**
	 * @param name
	 * @param cat
	 * @param exp
	 */
	public CEntityResourceFormContainer(int l, String name, CObjectCatalog cat, boolean bSaveCopy)
	{
		super(l, name, cat);
		this.bSaveCopy = bSaveCopy ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseEntity#RegisterMySelfToCatalog()
	 */
	protected void RegisterMySelfToCatalog()
	{
//		programCatalog.RegisterFormContainer(GetName(), this) ;
	}
	public void AddForm(CEntityResourceForm form)
	{
		arrForm.add(form) ;
	}

	public void InitDependences(CBaseEntityFactory factory)
	{
		for (int i=0; i<arrForm.size(); i++)
		{
			CEntityResourceForm form = arrForm.get(i);
			form.InitDependences(factory) ;
		}

//		String sav = GetName()+"S" ;
//		CIgnoreExternalEntity ext = factory.NewIgnoreExternalEntity(sav) ;
//		programCatalog.AddIgnoredExternalEntity(sav, ext);
	}

	protected Vector<CEntityResourceForm> arrForm = new Vector<CEntityResourceForm>() ;
	protected CEntityResourceFormContainer owner = null ;

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
	 */
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(semantic.CBaseDataEntity)
	 */
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		return null;
	}

	public CResourceStrings resStrings = null ;


	public void clearSavCopy(CBaseEntityFactory factory)
	{
		factory.programCatalog.ClearSavCopy();
	}

	public CEntityResourceFormContainer MakeSavCopy(CBaseEntityFactory factory, boolean bFromRes)
	{
		CEntityResourceFormContainer newContainer = factory.NewEntityFormContainer(getLine(), GetName()+"S", true) ;
		//newContainer.SetDisplayName(GetName());
		newContainer.of = this ;
		newContainer.owner = null ;
		savCopy = newContainer ;

		CObjectCatalog o = Transcoder.getCurrentObjectCatalog();
		// The current object catalog is only set when the sav copy is built on demand
		// from a consuming COBOL program (CObjectCatalog.GetExternalDataReference). When
		// the mapset is generated standalone (direct BMS group drive / artifact contract
		// test) there is no surrounding program catalog, so there is nothing to clear.
		if (o != null)
		{
			o.clearSaveMaps();
		}

		for (int i=0; i<arrForm.size(); i++)
		{
			CEntityResourceForm form = arrForm.get(i);
			CEntityResourceForm fs = factory.NewEntityForm(form.getLine(), form.GetName()+"S", true) ;
			fs.setResourceName(GetName()) ;
			fs.SetDisplayName(form.GetName());
			form.setSavCopy(fs) ;
			fs.of = newContainer ;
			form.MakeSavCopy(fs, factory, bFromRes) ;
			factory.programCatalog.RegisterSaveMap(fs, form) ;
			newContainer.AddForm(fs) ;
		}
		return newContainer ;
	}
	protected CEntityResourceFormContainer savCopy = null ;
	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#ignore()
	 */
	public boolean ignore()
	{
		Tag t = CRulesManager.getInstance().getRule("ReduceMaps") ;
		if (t != null)
		{
			boolean isreduce = t.getValAsBoolean("active") ;
			if (isreduce)
			{
				return bSaveCopy ;
			}
		}
		return false ;
	}

	public Document MakeXMLOutput(boolean bResources)
	{
		if (bResources)
		{
			return null;
		}

		if (csExportFilePath.equals("") || arrForm.size()==0)
		{
			return null ;
		}
		Document doc = createNewDocument() ;
		FieldComparator comp = new FieldComparator() ;
		SortedSet<FieldExportDescription> fields = new TreeSet<FieldExportDescription>(comp) ;
//		for (int i=0; i<arrForm.size(); i++)
//		{
		CEntityResourceForm form = arrForm.get(0);
		form.exportXMLFields(fields, doc, resStrings) ;
//		}

		String name = getFormattedName() ;
		Element ePFKeysDefine = form.MakePFKeysDescriptionDefine(doc) ;
		Element ePFKeysSpecial = form.MakePFKeysDescriptionAction(doc) ;
		Element eRoot = createNewFormBody(doc, name, name, ePFKeysDefine, ePFKeysSpecial) ;
		form.exportCustomProperties(doc) ;
		Element eBody = createVBox(doc, eRoot);
		int nb = fields.size() ;
		FieldExportDescription[] arr = new FieldExportDescription[nb] ;
		fields.toArray(arr);
		int curline = 0 ;
		int curCol = 0 ;
		Element curLineElem = null ;
		FieldExportType lastType = null ;

		FieldExportDescription eLineToAdd = null ;

		for (int i=0; i<nb; i++)
		{
			FieldExportDescription f = arr[i] ;
			if (f.type == FieldExportType.TYPE_LINE)
			{
				eLineToAdd = f ;
				continue ;
			}
			int nl = f.getLine();
			if (curline != nl)
			{
				if (eLineToAdd != null && curline == eLineToAdd.getLine())
				{
					eBody.appendChild(eLineToAdd.tag) ;
					eLineToAdd = null ;
				}
				for (int j=0; j<nl-curline-1; j++)
				{
					curLineElem = createHBox(doc, eBody);
					if (eLineToAdd != null && curline+j+1 == eLineToAdd.getLine())
					{
						eBody.appendChild(eLineToAdd.tag) ;
						eLineToAdd = null ;
					}
				}
				curLineElem = createHBox(doc, eBody);
				curline = nl ;
				curCol = 1 ;
				lastType = null ;
			}
			int nc = f.col ;
			int nlen = f.length;
			if (curCol == 1 && nc == 2)
			{
				createBlank(doc, curLineElem, 0) ;
			}
			else if (nc > curCol + 1)
			{
				if (lastType == null)
				{
					createBlank(doc, curLineElem, nc - curCol-1) ;
				}
				else
				{	// the 1-lenght blank are ignored, because managed in the XSL
					createBlank(doc, curLineElem, nc - curCol-2) ;
				}
			}
			curCol = nc + nlen ;
			curLineElem.appendChild(f.tag);
			lastType = f.type ;
		}

		Tag t = new Tag();
		t.setDoc(doc);
		t.exportToFile(csExportFilePath);

		return doc;
	}

	private Document createNewDocument()
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.newDocument();
			return doc;
		}
		catch(ParserConfigurationException e)
		{
			return null ;
		}
	}

	private Element createNewFormBody(Document doc, String csFormName, String csTitle, Element ePFKeysDefine, Element ePFKeysSpecial)
	{
		Element eForm = doc.createElement("form");
		doc.appendChild(eForm);
		eForm.setAttribute("name", csFormName);
		eForm.setAttribute("title", csTitle);
		eForm.appendChild(ePFKeysDefine);
		eForm.appendChild(ePFKeysSpecial);

		// list all languages
		String lang = resStrings.exportAllLangId() ;
		eForm.setAttribute("allLanguages", lang) ;

		Element eBody = doc.createElement("formbody");
		eForm.appendChild(eBody);

		return eBody;
	}

	private Element createVBox(Document doc, Element eParent)
	{
		Element eVBox = doc.createElement("vbox");
		eParent.appendChild(eVBox);
		return eVBox;
	}

	private Element createHBox(Document doc, Element eParent)
	{
		Element eHBox = doc.createElement("hbox");
		eParent.appendChild(eHBox);
		return eHBox;
	}

	private Element createBlank(Document doc, Element eParent, int size)
	{
		Element eBlank = doc.createElement("blank");
		eBlank.setAttribute("length", ""+size);
//		String cs = "" ;
//		for (int i=0; i<size; i++)
//		{
//			cs += " " ;
//		}
//		eBlank.setAttribute("text", cs);
		eParent.appendChild(eBlank);
		return eBlank;
	}
	public static class FieldExportDescription
	{
		private int line = 0 ;
		int col = 0 ;
		int length = 0 ;

		boolean isrightJustified = false;	// Valid only for Edits
		String csFillValue = "";			// Valid only for Edits

		Element tag = null ;
		FieldExportType type = null ;

		void setLine(int n)
		{
			line = n;
			Transcoder.setLine(line);
		}

		int getLine()
		{
			return line;
		}
	}
	public enum FieldExportType
	{
		TYPE_EDIT, TYPE_LABEL, TYPE_CUSTOM, TYPE_LINE ;
	}
	private class FieldComparator implements Comparator<FieldExportDescription>
	{
		public int compare(FieldExportDescription e1, FieldExportDescription e2)
		{
			int line1 = e1.getLine();
			int line2 = e2.getLine();
			if (line1 < line2)
			{
				return -1 ;
			}
			else if (line1 > line2)
			{
				return 1 ;
			}
			else
			{
				int col1 = e1.col ;
				int col2 = e2.col ;
				if (col1 < col2)
				{
					return -1 ;
				}
				else if (col1 > col2)
				{
					return 1 ;
				}
				else
				{
					return 0;
				}
			}
		}
	}
	protected String csExportFilePath = "" ;
	public void setExportFilePath(String string)
	{
		csExportFilePath = string ;
	}

	public boolean isSavCopy()
	{
		return bSaveCopy ;
	}
//	public String getExportFilePath()
//	{
//		return csExportFilePath ;
//	}

	public void Clear()
	{
		if (programCatalog != null)
		{
			programCatalog.GetGlobalCatalog().ClearFormContainers() ;
		}
		super.Clear();
		if (savCopy!=null)
		{
			savCopy.Clear() ;
			savCopy = null ;
		}
		for (int i=0; i<arrForm.size(); i++)
		{
			CEntityResourceForm form = arrForm.get(i);
			form.Clear() ;
		}
		arrForm.clear() ;
		owner = null ;
	}

	/**
	 * @return
	 */
	public CEntityResourceFormContainer GetSavCopy()
	{
		return savCopy ;
	}

	/**
	 * @return
	 */
	public CEntityResourceForm getForm()
	{
		if (arrForm.size()>0)
		{
			return arrForm.get(0);
		}
		return null;
	}

	/**
	 * @return
	 */
	public int GetNbForms()
	{
		return arrForm.size() ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseLanguageEntity#SetDisplayName(java.lang.String)
	 */
	@Override
	public void SetDisplayName(String name)
	{
		super.SetDisplayName(name);
	}

	/**
	 * Read-only snapshot of this mapset's maps, in definition order. Consumed by the generate-layer
	 * skeleton bridge ({@code BmsJavaEntities.renderFormContainerDeclaration}) to drive the class-body
	 * {@code { ... }} block over the still-direct/still-semantic BMS map backends — exactly the
	 * {@code arrForm} traversal the retired {@code CJavaFormContainer.DoExport} performed via
	 * {@code invokeExport(eForm)}. The mapset's maps live in {@code arrForm} (populated via
	 * {@link #AddForm}), not in the generic child list, so this list — not {@code exportChildren} — is
	 * the production map collection. A fresh copy is returned so the semantic tree's internal state
	 * cannot be mutated through it.
	 */
	public java.util.List<CEntityResourceForm> getForms()
	{
		return new java.util.ArrayList<>(arrForm) ;
	}

	// ---------------------------------------------------------------------------------------------
	// Retired direct backend generate.java.forms.CJavaFormContainer: the output protocols below were
	// moved out of the generate layer onto this pure semantic entity when the backend was retired onto
	// the recursive ST4 assembly contract. They read only precomputed state; the two generate-layer
	// operations the backend performed inline (identifier formatting and the class-skeleton rendering)
	// are injected by the generate-layer factory (BmsJavaEntities.formContainer), so this tree names no
	// generate.* class. NOTE: getFormattedName() is deliberately NOT overridden — MakeXMLOutput (the
	// BMS XML/.res artifact) keeps the neutral CBaseLanguageEntity normalization, exactly as it did
	// before this retirement (the retired backend never overrode it either).
	// ---------------------------------------------------------------------------------------------

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetDataType()
	 */
	public CDataEntityType GetDataType()
	{
		// Preserved from the retired backend: a mapset bears the FORM data type.
		return CDataEntityType.FORM ;
	}

	public boolean HasAccessors()
	{
		// Preserved from the retired backend: a mapset bears no accessors.
		return false;
	}

	public boolean isValNeeded()
	{
		// Preserved from the retired backend: a mapset is never declared as a val.
		return false;
	}

	public boolean IsNeedDeclarationInClass()
	{
		// Preserved from the retired backend: a mapset is needed as an in-class declaration.
		return true ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseExternalEntity#GetTypeDecl()
	 */
	public String GetTypeDecl()
	{
		// Preserved from the retired backend: these are class names, not identifiers, so DO NOT FORMAT
		// like other identifiers — only '-' -> '_'. Qualified by the owner's type declaration when set.
		if (owner != null)
		{
			return owner.GetTypeDecl() + "." + GetName().replace('-', '_');
		}
		else
		{
			return GetName().replace('-', '_');
		}
	}

	/**
	 * Target-neutral identifier formatter standing in for the retired backend's
	 * {@code LegacyLanguageRenderer.formatIdentifier}. Installed by the generate-layer factory
	 * ({@code BmsJavaEntities.formContainer} injects the bound output's {@code FormatIdentifier});
	 * defaults to the neutral legacy fallback so a directly constructed entity stays well-formed. A
	 * pure injected value — no {@code generate.*} coupling lives in this tree. Consumed ONLY by the
	 * Java-reference/declaration getters below; {@link #MakeXMLOutput} never uses it (it keeps the
	 * neutral {@code getFormattedName()}).
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
	 * Pure read-only getter consumed by the {@code recursiveFormContainerEntity} reference binding:
	 * the target-formatted mapset name. Mirrors the retired backend's {@code ExportReference ==
	 * formatIdentifier(GetName())}. A pure formatting step over a precomputed name through the injected
	 * formatter — no data-reference resolution, no lowering.
	 */
	public String getContainerReference()
	{
		return identifierFormatter.apply(GetName()) ;
	}

	/**
	 * Pure read-only getter consumed by the {@code recursiveFormContainerDeclarationEntity} template
	 * and by the generate-layer skeleton bridge: the mapset's Java class name. The retired backend's
	 * {@code DoExport} used the RAW {@code GetName()} for the class name (it did NOT format it — only
	 * {@code GetTypeDecl} applies {@code '-' -> '_'}), so this returns the raw name to preserve the
	 * emitted Java byte-for-byte. A plain field read.
	 */
	public String getMapClassName()
	{
		return GetName() ;
	}

}
