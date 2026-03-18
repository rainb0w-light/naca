/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 5 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package semantic.forms;

import generate.CBaseLanguageExporter;

import java.util.SortedSet;
import java.util.Vector;

import jlib.xml.Tag;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.expression.CTerminal;
import semantic.CBaseActionEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseResourceEntity;
import semantic.CDataEntity;
import semantic.CEntityNoAction;
import semantic.Verbs.CEntityInitialize;
import semantic.Verbs.CEntitySetConstant;
import semantic.forms.CEntityResourceFormContainer.FieldExportType;
import utils.CEntityHierarchy;
import utils.CObjectCatalog;
import utils.CRulesManager;
import utils.Transcoder;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CEntityResourceForm extends CBaseResourceEntity
{
	public class CFormByteConsumingState
	{
		public int nCurrentField = 0 ;
		public int nCurrentByteInField = 0 ;
	}
	
	protected boolean bSaveMap = false ;

	/**
	 * @param name
	 * @param cat
	 * @param exp
	 */
	public CEntityResourceForm(int l, String name, CObjectCatalog cat, CBaseLanguageExporter lexp, boolean bSaveCopy)
	{
		super(l, name, cat, lexp);
		bSaveMap = bSaveCopy ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseEntity#RegisterMySelfToCatalog()
	 */
//	protected void RegisterMySelfToCatalog()
//	{
//		super.RegisterMySelfToCatalog() ;
//		if (!bSaveMap)
//		{
//			programCatalog.RegisterMasterMap(this) ;
//		}
//	}
	
	public void InitDependences(CBaseEntityFactory factory) 
	{
		for (int i=0; i<arrFields.size(); i++)
		{
			CEntityResourceField f = (CEntityResourceField)arrFields.get(i);
			f.SetParent(this) ;
			f.InitDependences(factory) ; 
		}
		for (int i=0; i<arrFormReferences.size();i++)
		{
			String cs = arrFormReferences.get(i);
//			CEntityFormAccessor fa1 = factory.NewEntityFormAccessor(getLine(), cs+"I", this) ; 
//			CEntityFormAccessor fa2 = factory.NewEntityFormAccessor(getLine(), cs+"O", this) ; 
//			CEntityFormAccessor fa3 = factory.NewEntityFormAccessor(getLine(), cs, this) ; 
			factory.programCatalog.RegisterDataEntity(cs+"I", this) ;
			factory.programCatalog.RegisterDataEntity(cs+"O", this) ;
			factory.programCatalog.RegisterDataEntity(cs, this) ;
		}
		String cs = GetName() ;
//		CEntityFormAccessor fa1 = factory.NewEntityFormAccessor(getLine(), GetName()+"I", this) ; 
//		CEntityFormAccessor fa2 = factory.NewEntityFormAccessor(getLine(), GetName()+"O", this) ; 
//		CEntityFormAccessor fa3 = factory.NewEntityFormAccessor(getLine(), GetName(), this) ; 
		factory.programCatalog.RegisterDataEntity(cs+"I", this) ;
		factory.programCatalog.RegisterDataEntity(cs+"O", this) ;
		factory.programCatalog.RegisterDataEntity(cs, this) ;
	}

	public void AddField(CBaseResourceEntity e)
	{
		arrFields.add(e) ;
	}

	protected Vector<CBaseResourceEntity> arrFields = new Vector<CBaseResourceEntity>() ;
	protected ArrayList<String> arrFormReferences = new ArrayList<String>() ;
	public void SetReferences(ArrayList<String> v)
	{
		arrFormReferences = v ;
	}	
	public void SetSize(int col, int line)
	{
		nSizeCol = col ;
		nSizeLine = line ;
	}
	protected int nSizeCol = 0 ;
	protected int nSizeLine = 0 ;

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(parser.expression.CTerminal)
	 */
	public CBaseActionEntity GetSpecialAssignment(CTerminal term, CBaseEntityFactory factory, int l)
	{
		String value = term.GetValue() ;
		CEntitySetConstant eAssign = factory.NewEntitySetConstant(l) ;
		if (value.equals("ZERO") || value.equals("ZEROS") || value.equals("ZEROES"))
		{
			eAssign.SetToZero(this) ;
		}
		else if (value.equals("SPACE") || value.equals("SPACES"))
		{
			eAssign.SetToSpace(this) ;
		}
		else if (value.equals("LOW-VALUE") || value.equals("LOW-VALUES"))
		{
			CEntityInitialize init = factory.NewEntityInitialize(l, this) ;
			RegisterWritingAction(init) ;
			return init ;
			//eAssign.SetToLowValue(this) ;
		}
		else
		{
			return null ;
		}
		RegisterWritingAction(eAssign) ;
		return eAssign ;
	}

	/* (non-Javadoc)
	 * @see semantic.CBaseDataEntity#GetSpecialAssignment(semantic.CBaseDataEntity)
	 */
	public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
	{
		Tag t = CRulesManager.getInstance().getRule("ReduceMaps") ;
		if (t != null)
		{
			boolean bReduce = t.getValAsBoolean("active") ;
			if (bReduce)
			{
				if (term.GetDataType() == CDataEntityType.FORM && !IsSaveCopy())
				{
					CEntityNoAction act = factory.NewEntityNoAction(l) ; 
					factory.programCatalog.RegisterMapCopy(act) ;
					return act ;
				}
			}
		}
		return null;
	}

	public void MakeSavCopy(CEntityResourceForm form, CBaseEntityFactory factory, boolean bFromRes)
	{
		form.SetSize(nSizeCol, nSizeLine) ;
		for (int i=0; i<arrFields.size(); i++)
		{
			CEntityResourceField f = (CEntityResourceField)arrFields.get(i);
			CEntityResourceField fs ;
			if (f.IsEntryField())
			{
				if(!bFromRes)
				{
					fs = factory.NewEntityEntryField(f.getLine(), "S"+f.GetName()) ;
					fs.SetDisplayName(f.GetName());
				}
				else
				{
					fs = factory.NewEntityEntryField(f.getLine(), "S"+f.GetName()) ;
					fs.SetDisplayName(f.GetDisplayName());
				}
				factory.programCatalog.RegisterSaveField(fs, f) ;
			}
			else
			{
				fs = factory.NewEntityLabelField(f.getLine()) ;
			}
			fs.of = form.of ;
			fs.csInitialValue = f.csInitialValue ;
			fs.nLength = f.nLength ;
			fs.nPosCol = f.nPosCol ;
			fs.nPosLine = f.nPosLine ;
			fs.programCatalog = f.programCatalog ;
			fs.csColor = f.csColor ;
			fs.csHighLight = f.csHighLight ;
			fs.csBrightness = f.csBrightness ;
			fs.csFillValue = f.csFillValue ;
			fs.csProtection = f.csProtection ;
			fs.bRightJustified = f.bRightJustified ;
			fs.resourceStrings = f.resourceStrings ;
			if(bFromRes)
				fs.setDevelopable(f.csDevelopableFlagMark);
			
			form.arrFields.add(fs) ;
		}
	}
	
	protected int nCurrentField = -1;
	protected int nCurrentByteInField = 0 ;
	public void StartFieldAnalyse()
	{
		nCurrentField = -1 ;
		nCurrentByteInField = 0 ;
	}
	public int getCurrentPositionInField()
	{
		return nCurrentByteInField ;
	}
	public CFormByteConsumingState getCurrentConsumingState()
	{
		CFormByteConsumingState state = new CFormByteConsumingState() ;
		state.nCurrentByteInField = nCurrentByteInField ;
		state.nCurrentField = nCurrentField ;
		return state ;
	}
	public void setCurrentConsumingState(CFormByteConsumingState state)
	{
		if (state != null)
		{
			nCurrentByteInField = state.nCurrentByteInField ;
			nCurrentField = state.nCurrentField ;
		}
	}
	public int ConsumeFieldsAsBytes(int bytes)
	{
		int nbBytesLeft = bytes ;
		if (nCurrentField == -1 && nCurrentByteInField == 0)
		{
			nbBytesLeft -= 12 ; // there are 12 bytes at begining of MAP
			nCurrentField = 0 ;
			if (nbBytesLeft <0)
			{
				Transcoder.logError(getLine(), "Unexpecting situation");
				return 0 ;
			}
			else if (nbBytesLeft == 0)
			{
				return -1 ;
			}
		}	
		int nbFields = 0;
		while (nbBytesLeft > 0 && nCurrentField < arrFields.size())
		{
			CEntityResourceField f = getCurrentField();
			if (f != null)
			{
				int byteLeftInField = f.GetByteLength() - nCurrentByteInField ;
				if (nbBytesLeft < byteLeftInField)
				{
					nCurrentByteInField += nbBytesLeft ;
					nbBytesLeft = 0 ;
				}
				else
				{
					nbBytesLeft -= byteLeftInField ;
					nbFields ++ ;
					nCurrentByteInField = 0;
					nCurrentField ++ ;
				}
			}
		} 
		if (nbBytesLeft == 0)
		{
			return nbFields ;
		}
		else
		{ // error 
			//m_logger.error("WARNING : Redefine is larger than original size. Check if this is not important");
			return nbFields ;
		}
	}
	
	/**
	 * @return
	 */
	private CEntityResourceField getCurrentField()
	{
		if (nCurrentField < arrFields.size())
		{
			CEntityResourceField f = (CEntityResourceField)arrFields.get(nCurrentField) ;
			while (!f.IsEntryField())
			{
				nCurrentField ++ ;
				if (nCurrentField == arrFields.size())
				{
					return null ;
				}
				f = (CEntityResourceField)arrFields.get(nCurrentField) ;
			}
			return f ;
		}
		return null ;
	}

	public void ConsumeFields(int n)
	{
		int nToDO = n ;
		while (nCurrentField < arrFields.size() && nToDO>0)
		{
			CEntityResourceField f = (CEntityResourceField)arrFields.get(nCurrentField);
			if (f.IsEntryField())
			{
				nToDO -- ;
			}
			nCurrentField ++ ;
		}
	}
	public CEntityHierarchy GetHierarchy()
	{
		CEntityHierarchy hier = super.GetHierarchy() ;
		hier.AddLevel(GetName()+"I");
		hier.AddLevel(GetName()+"O");
		return hier;
	}

	public boolean IsSaveCopy()
	{
		return bSaveMap;
	}

	public class CFieldRedefineDescription
	{
		public String SKIP = "SKIP" ;
		public String FIELD = "FIELD" ;
		public String OCCURS = "OCCURS" ;
		public CEntityResourceField field = null ;
		public String name = "" ;
		public String type = "" ;
		public int size = 0 ;
		
		public CFieldRedefineDescription Next()
		{
			if (next == null)
			{
				next = new CFieldRedefineDescription() ;
			}
			return next ;
		}
		protected CFieldRedefineDescription next = null ;
	}
	public class CFieldRedefineStructure
	{
		public CFieldRedefineDescription Current()
		{
			return current ;
		}
		public CFieldRedefineDescription Next()
		{
			current = current.Next() ;
			return current;
		}
		protected CFieldRedefineDescription current = null ;
		protected CFieldRedefineDescription start = null ;
	}
	public CFieldRedefineStructure GetRedefineStructure()
	{
		if (redefineStructure.start == null)
		{
			redefineStructure.start = new CFieldRedefineDescription() ;
		}
		redefineStructure.current = redefineStructure.start ;
		return redefineStructure;
	}
	protected CFieldRedefineStructure redefineStructure = new CFieldRedefineStructure() ;

	public void ExportXMLFields(SortedSet<CEntityResourceFormContainer.FieldExportDescription> setFields, Document doc, CResourceStrings res)
	{
		for (int i=0; i<arrFields.size(); i++)
		{
			CEntityResourceField field = (CEntityResourceField)arrFields.get(i);
			Element e = field.DoXMLExport(doc, res) ;
			if (e != null)
			{
				CEntityResourceFormContainer.FieldExportDescription exp = new CEntityResourceFormContainer.FieldExportDescription() ;
				exp.col = field.nPosCol ;
				exp.setLine(field.nPosLine);
				exp.length = field.nLength ;
				exp.bRightJustified = field.bRightJustified;
				exp.csFillValue = field.csFillValue;
				
				exp.tag = e ;
				if (e.getNodeName().equalsIgnoreCase("edit"))
				{
					exp.type = CEntityResourceFormContainer.FieldExportType.TYPE_EDIT ;
				}
				else if (e.getNodeName().equalsIgnoreCase("label"))
				{
					exp.type = CEntityResourceFormContainer.FieldExportType.TYPE_LABEL ;
				}
				else if (e.getNodeName().equalsIgnoreCase("title"))
				{
					exp.type = CEntityResourceFormContainer.FieldExportType.TYPE_LABEL ;
				}
				setFields.add(exp) ;
			}
		}
		
		if (arrAddedItems != null)
		{
			for (int i=0; i<arrAddedItems.size(); i++)
			{
				CEntityResourceFormContainer.FieldExportDescription exp = arrAddedItems.get(i) ;
				exp.tag = (Element)doc.importNode(exp.tag, true) ;
				setFields.add(exp) ;
			}
		}
		if (arrLines != null)
		{
			for (int i=0; i<arrLines.size(); i++)
			{
				CEntityResourceFormContainer.FieldExportDescription exp = arrLines.get(i) ;
				exp.tag = doc.createElement("line") ;
				exp.tag.setAttribute("line", String.valueOf(exp.getLine())) ;
				exp.tag.setAttribute("start", String.valueOf(exp.col)) ;
				exp.tag.setAttribute("length", String.valueOf(exp.length)) ;
				setFields.add(exp) ;
			}
		}
	}
	
	protected HashMap<String, String> tabActivePFKeys = new HashMap<String, String>() ;
	public String getPFActive(String key)
	{
		return tabActivePFKeys.get(key);
	}
	public void setPFActive(String key, String status)
	{
		tabActivePFKeys.put(key, status);
	}	
	
	protected HashMap<String, String> tabActionPFKeys = new HashMap<String, String>() ;
	public String getPFAction(String key)
	{
		return tabActionPFKeys.get(key);
	}
	public void setPFAction(String key, String action)
	{
		tabActionPFKeys.put(key, action);
	}
	
	public Element MakePFKeysDescriptionDefine(Document doc)
	{
		Element ePFKEys = doc.createElement("pfkeydefine") ;
		for (int i=0; i<tabActivePFKeys.size(); i+=2)
		{
			String pf = tabActivePFKeys.get(i) ;
			String status = tabActivePFKeys.get(i+1);
			if (status.equals("true"))
			{
				ePFKEys.setAttribute(pf, status) ;
			}
		}
		return ePFKEys;
	}
	public Element MakePFKeysDescriptionAction(Document doc)
	{
		Element ePFKEys = doc.createElement("pfkeyaction") ;
		for (int i=0; i<tabActionPFKeys.size(); i+=2)
		{
			String pf = tabActionPFKeys.get(i) ;
			String action = tabActionPFKeys.get(i+1);
			ePFKEys.setAttribute(pf, action) ;
		}
		return ePFKEys;
	}

	public void setSavCopy(CEntityResourceForm fs)
	{
		saveCopy = fs ;
	}
	CEntityResourceForm saveCopy = null ;
	public CEntityResourceForm getSaveCopy()
	{
		return saveCopy ;
	}

//	public void setDisplayName(String string)
//	{
//		csDisplayName = string ;
//	}
//	protected String csDisplayName = "" ;

	public void Clear()
	{
		super.Clear();
		for (int i=0; i<arrFields.size(); i++)
		{
			CEntityResourceField field = (CEntityResourceField)arrFields.get(i);
			field.Clear() ;
		}
		if (saveCopy!=null)
		{
			saveCopy.Clear();
		}
		saveCopy = null ;
		arrFields.clear() ;
	}

	/**
	 * @param from
	 * @param to
	 */
	public void RenameField(String from, String to)
	{
		CEntityResourceField field = getField(from) ;
		if (field != null)
		{
			field.SetDisplayName(to) ;
		}
	}

	public void setDevelopable(String name, String flagMark)
	{
		CEntityResourceField field = getField(name) ;
		if (field != null)
		{
			field.setDevelopable(flagMark) ;
		}
	}
	
	public void setFormat(String name, String format)
	{
		CEntityResourceField field = getField(name) ;
		if (field != null)
		{
			field.setFormat(format);
		}
	}

	/**
	 * @param name
	 * @param valueOn
	 * @param valueOff
	 */
	public void setCheckBox(String name, String valueOn, String valueOff)
	{
		CEntityResourceField field = getField(name) ;
		if (field != null)
		{
			field.setCheckBox(valueOn, valueOff) ;
		}
	}
	
	protected CEntityResourceField getField(String name)
	{
		for (int i=0; i<arrFields.size(); i++)
		{
			CEntityResourceField field = (CEntityResourceField)arrFields.get(i) ;
			if (field != null)
			{
				if (field.GetDisplayName().equalsIgnoreCase(name))
				{
					return field ;
				}
			}
		}
		return null ;
	}
	protected CEntityResourceField getField(int col, int line)
	{
		for (int i=0; i<arrFields.size(); i++)
		{
			CEntityResourceField field = (CEntityResourceField)arrFields.get(i) ;
			if (field != null)
			{
				if (field.nPosCol == col && field.nPosLine == line)
				{
					return field ;
				}
			}
		}
		return null ;
	}

	/**
	 * @param col
	 * @param line
	 * @param name
	 */
	public void setNameLabel(int col, int line, String name)
	{
		CEntityResourceField field = getField(col, line); 
		if (field != null)
		{
			field.SetDisplayName(name) ;
		}
	}

	/**
	 * @param name
	 */
	public void setTitle(String name)
	{
		CEntityResourceField field = getField(name); 
		if (field != null)
		{
			CEntityResourceFormContainer cont = (CEntityResourceFormContainer)of ;
			field.SetTitle(cont.resStrings) ;
		}
	}

	/**
	 * @param label
	 * @param value
	 * @param target
	 * @param submit
	 */
	public void setActiveChoice(String label, String value, String target, boolean submit)
	{
		CEntityResourceField field = getField(label) ;
		if (field != null) 
		{
			if (getField(target) != null)
			{
				field.setActiveChoice(value, target, submit);
			}
		}
	}
	public void setLinkedActiveChoice(String label, String edit, String target, boolean submit)
	{
		CEntityResourceField field = getField(label) ;
		CEntityResourceField link = getField(edit) ;
		if (field != null) 
		{
			if (getField(target) != null && getField(edit)!=null)
			{
				field.setLinkedActiveChoice(edit, target, submit);
			}
		}
	}

	/**
	 * @param field
	 */
	public void setEditReplayMutable(String name)
	{
		CEntityResourceField field = getField(name) ;
		if (field != null) 
		{
			field.setReplayMutable();
		}
	}

	/**
	 * @return
	 */
	public int GetRemainingBytesInCurrentField()
	{
		if (nCurrentField>=0)
		{
			CEntityResourceField field = getCurrentField() ;
			if (field != null)
			{
				return field.GetByteLength() - nCurrentByteInField ;
			}
		}
		else
		{
			return  12 - nCurrentByteInField ;
		}
		return 0 ;
	}

	/**
	 * @return
	 */
	public CEntityResourceField GetCurrentRedefiningField()
	{
		return getCurrentField() ;
	}

	/**
	 * @return
	 */
	public boolean isFormAlias(String id)
	{
		id = id.trim() ;
		String cs = GetDisplayName() ;
		if (cs.equals(id))
		{
			return true ;
		}
		for (int i=0; i<arrFormReferences.size(); i++)
		{
			if (arrFormReferences.get(i).equals(id))
			{
				return true ;
			}
		}
		return false ;
	}
	
	/**
	 * @param method
	 */
	public void SetCustomOnload(String method)
	{
		csCustomOnloadMethod = method ;
	}
	protected String csCustomOnloadMethod = "" ;

	/**
	 * @param method
	 */
	public void SetCustomSubmit(String method)
	{
		csCustomSubmitMethod = method ;
	}
	protected String csCustomSubmitMethod = "" ;
	
	/**
	 * @param field
	 */
	public void SetDefaultCursor(String field)
	{
		csDefaultCursor = field ;
	}
	protected String csDefaultCursor = "";

	/**
	 * @param root
	 */
	public void ExportCustomProperties(Document doc)
	{
		Element eForm = doc.getDocumentElement() ;
		if (!csCustomOnloadMethod.equals(""))
		{
			eForm.setAttribute("customOnload", csCustomOnloadMethod) ;
		}
		if (!csCustomSubmitMethod.equals(""))
		{
			eForm.setAttribute("customSubmit", csCustomSubmitMethod) ;
		}
		if (!csDefaultCursor.equals(""))
		{
			eForm.setAttribute("defaultCursor", csDefaultCursor) ;
		}
	}

	public void AddSwitchCase(String name, String value, String protection, Element tag)
	{
		CEntityResourceField field = getField(name) ;
		if (field != null) 
		{
			field.AddSwitchCase(value, protection, tag);
		}
	}

	public void HideField(String name)
	{
		CEntityResourceField field = getField(name) ;
		if (field != null) 
		{
			field.Hide();
		}
	}

	public void HideField(int col, int line)
	{
		CEntityResourceField field = getField(col, line); 
		if (field != null)
		{
			field.Hide() ;
		}
	}

	public void AddItem(int c, int l, int s, Element tag)
	{
		if (arrAddedItems == null)
		{
			arrAddedItems = new Vector<CEntityResourceFormContainer.FieldExportDescription>() ;
		}
		CEntityResourceFormContainer.FieldExportDescription exp = new CEntityResourceFormContainer.FieldExportDescription() ;
		exp.col = c ;
		exp.setLine(l) ;
		exp.length = s ;
		exp.tag = tag ;
		exp.type = FieldExportType.TYPE_CUSTOM ;
		arrAddedItems.add(exp) ;
	} 
	protected Vector<CEntityResourceFormContainer.FieldExportDescription> arrAddedItems = null ;
	protected Vector<CEntityResourceFormContainer.FieldExportDescription> arrLines = null ;

	public void AddLine(int c, int l, int s)
	{
		if (arrLines == null)
		{
			arrLines = new Vector<CEntityResourceFormContainer.FieldExportDescription>() ;
		}
		CEntityResourceFormContainer.FieldExportDescription exp = new CEntityResourceFormContainer.FieldExportDescription() ;
		exp.col = c ;
		exp.setLine(l) ;
		exp.length = s ;
		exp.tag = null ;
		exp.type = FieldExportType.TYPE_LINE;
		arrLines.add(exp) ;
	}

	public void MoveField(String name, int nc, int nl)
	{
		CEntityResourceField field = getField(name) ;
		if (field != null) 
		{
			field.move(nc, nl);
		}
	}

	public void MoveField(int c, int l, int nc, int nl)
	{
		CEntityResourceField field = getField(c, l) ;
		if (field != null) 
		{
			field.move(nc, nl);
		}
	}

	public void setResourceName(String name)
	{
		csResourceName = name ;
	}
	protected String csResourceName = "" ;

}
