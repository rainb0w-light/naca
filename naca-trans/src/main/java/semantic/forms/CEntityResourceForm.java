/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic.forms;


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
import java.util.function.Function;

/**
 * BMS map-resource DSL: a screen-map {@code MAP} (a CICS screen form), lowered from a BMS
 * {@code .bms} map definition ({@code parser/map_elements/CMapElement} -&gt;
 * {@code factory.NewEntityForm(line, name, save)}). A form declares a
 * {@code nacaLib.mapSupport.Form} and opens a {@code { ... }} block over its fields.
 *
 * <p>De-abstracted when the direct backend {@code generate.java.forms.CJavaForm} was retired
 * onto the recursive ST4 assembly contract. The backend had two live output protocols, both
 * preserved target-neutrally here:
 * <ul>
 *   <li><b>data reference</b> — {@code ExportReference(nLine) == [renderReference(of) + "."] +
 *       formatIdentifier(GetName())}. A reference to the form now renders through the BMS
 *       forms-island binding {@code semantic.forms.CEntityResourceForm -> recursiveFormEntity},
 *       whose template reads only {@code entity.formReference} (the precomputed reference: the
 *       target-formatted container qualifier — when the parser set {@code of} — followed by the
 *       target-formatted form name). {@code LegacyDataRenderer.renderReference} ignores a
 *       semantic-declared {@code ExportReference} and falls through to that binding, reproducing
 *       the backend's reference exactly.</li>
 *   <li><b>declaration block</b> — {@code DoExport} emitted
 *       {@code Form <name> = declare.form("<resourceName>", <sizeLine>, <sizeCol>) ;} followed by
 *       a {@code { ... }} block over the form's fields. The declaration LINE now renders
 *       declaratively through the {@code recursiveFormDeclarationEntity} template (invoked by the
 *       generate-layer factory bridge {@code BmsJavaEntities.renderFormDeclaration}); the block and
 *       the fields keep rendering through the still-direct BMS field backends, which that bridge
 *       drives over {@link #getFields()} (the form's fields live in {@code arrFields}, populated
 *       via {@link #AddField}, not in the generic child list).</li>
 * </ul>
 *
 * <p>This tree names no {@code generate.*} class, so the dependency arrow stays
 * generate -&gt; semantic. The two generate-layer protocols the retired backend performed inline
 * are supplied by the generate-layer factory ({@code generate.java.forms.BmsJavaEntities.form}):
 * a neutral identifier {@link Function} (standing in for
 * {@code LegacyLanguageRenderer.formatIdentifier}) and a declaration {@link Consumer} that renders
 * the declaration line through the recursive assembler and drives the
 * {@code startBlock/<fields>/endBlock} block over the still-direct field backends. A hand-built
 * entity (no factory) defaults to the neutral identifier normalization and a no-op declaration
 * renderer, so it stays well-formed and never fails.
 *
 * <p>The retired backend's data-entity protocols are preserved exactly: a form bears
 * {@code FORM} data type, needs no {@code val} ({@code isValNeeded() == false}), bears no
 * accessors ({@code HasAccessors() == false}), has no reachable write-accessor protocol
 * ({@code ExportWriteAccessorTo -> ""}, unused), and contributes no type declaration
 * ({@code GetTypeDecl -> ""}, unused).
 *
 * @author sly
 */
public class CEntityResourceForm extends CBaseResourceEntity
{
    /** Provides cform byte consuming state behavior. */
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
    public CEntityResourceForm(int l, String name, CObjectCatalog cat, boolean bSaveCopy)
    {
        super(l, name, cat);
        bSaveMap = bSaveCopy ;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseEntity#RegisterMySelfToCatalog()
     */
//  protected void RegisterMySelfToCatalog()
//  {
//      super.RegisterMySelfToCatalog() ;
//      if (!bSaveMap)
//      {
//          programCatalog.RegisterMasterMap(this) ;
//      }
//  }

    /** Executes the init dependences operation. */
    public void InitDependences(CBaseEntityFactory factory)
    {
        for (int i=0; i<arrFields.size(); i++)
        {
            CEntityResourceField f = (CEntityResourceField)arrFields.get(i);
            f.SetParent(this) ;
            f.InitDependences(factory) ;
        }
        for (int i = 0; i< formReferences.size(); i++)
        {
            String cs = formReferences.get(i);
//          CEntityFormAccessor fa1 = factory.NewEntityFormAccessor(getLine(), cs+"I", this) ;
//          CEntityFormAccessor fa2 = factory.NewEntityFormAccessor(getLine(), cs+"O", this) ;
//          CEntityFormAccessor fa3 = factory.NewEntityFormAccessor(getLine(), cs, this) ;
            factory.programCatalog.RegisterDataEntity(cs+"I", this) ;
            factory.programCatalog.RegisterDataEntity(cs+"O", this) ;
            factory.programCatalog.RegisterDataEntity(cs, this) ;
        }
        String cs = GetName() ;
//      CEntityFormAccessor fa1 = factory.NewEntityFormAccessor(getLine(), GetName()+"I", this) ;
//      CEntityFormAccessor fa2 = factory.NewEntityFormAccessor(getLine(), GetName()+"O", this) ;
//      CEntityFormAccessor fa3 = factory.NewEntityFormAccessor(getLine(), GetName(), this) ;
        factory.programCatalog.RegisterDataEntity(cs+"I", this) ;
        factory.programCatalog.RegisterDataEntity(cs+"O", this) ;
        factory.programCatalog.RegisterDataEntity(cs, this) ;
    }

    /** Adds the field. */
    public void AddField(CBaseResourceEntity e)
    {
        arrFields.add(e) ;
    }

    /**
     * Read-only count of this form's fields. Exposes the parsed BMS form structure
     * for inspection (e.g. artifact-contract tests that verify a mapset generated
     * from real {@code .bms} source has a non-empty field layout). Performs no
     * semantic analysis or code generation.
     */
    public int getNbFields()
    {
        return arrFields.size() ;
    }

    /**
     * Read-only names of this form's fields, in layout order. Lets artifact-contract
     * tests verify a mapset was generated from real {@code .bms} source (real field
     * names such as {@code NMMASQ}), not an empty or handwritten placeholder.
     */
    public java.util.List<String> getFieldNames()
    {
        java.util.List<String> names = new java.util.ArrayList<>() ;
        for (CBaseResourceEntity f : arrFields)
        {
            names.add(f.GetName()) ;
        }
        return names ;
    }

    /**
     * Read-only snapshot of this form's fields, in layout order. Consumed by the generate-layer
     * declaration bridge ({@code BmsJavaEntities.renderFormDeclaration}) to drive the {@code { ... }}
     * block over the still-direct BMS field backends — exactly the {@code arrFields} traversal the
     * retired {@code CJavaForm.DoExport} performed. The form's fields live in {@code arrFields}
     * (populated via {@link #AddField}), not in the generic child list, so this list — not
     * {@code exportChildren} — is the production field collection. A fresh copy is returned so the
     * semantic tree's internal state cannot be mutated through it.
     */
    public java.util.List<CBaseResourceEntity> getFields()
    {
        return new java.util.ArrayList<>(arrFields) ;
    }

    protected Vector<CBaseResourceEntity> arrFields = new Vector<CBaseResourceEntity>() ;
    protected ArrayList<String> formReferences = new ArrayList<String>() ;
    /** Sets the references. */
    public void SetReferences(ArrayList<String> v)
    {
        formReferences = v ;
    }
    /** Sets the size. */
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
    /** Executes the get special assignment operation. */
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
    /** Executes the get special assignment operation. */
    public CBaseActionEntity GetSpecialAssignment(CDataEntity term, CBaseEntityFactory factory, int l)
    {
        Tag t = CRulesManager.getInstance().getRule("ReduceMaps") ;
        if (t != null)
        {
            boolean isreduce = t.getValAsBoolean("active") ;
            if (isreduce)
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

    /** Executes the make sav copy operation. */
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
            fs.isrightJustified = f.isrightJustified;
            fs.resourceStrings = f.resourceStrings ;
            if (bFromRes) {
                fs.setDevelopable(f.csDevelopableFlagMark);
            }

            form.arrFields.add(fs) ;
        }
    }

    protected int nCurrentField = -1;
    protected int nCurrentByteInField = 0 ;
    /** Executes the start field analyse operation. */
    public void StartFieldAnalyse()
    {
        nCurrentField = -1 ;
        nCurrentByteInField = 0 ;
    }
    public int getCurrentPositionInField()
    {
        return nCurrentByteInField ;
    }
    /** Returns the current consuming state. */
    public CFormByteConsumingState getCurrentConsumingState()
    {
        CFormByteConsumingState state = new CFormByteConsumingState() ;
        state.nCurrentByteInField = nCurrentByteInField ;
        state.nCurrentField = nCurrentField ;
        return state ;
    }
    /** Sets the current consuming state. */
    public void setCurrentConsumingState(CFormByteConsumingState state)
    {
        if (state != null)
        {
            nCurrentByteInField = state.nCurrentByteInField ;
            nCurrentField = state.nCurrentField ;
        }
    }
    /** Executes the consume fields as bytes operation. */
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

    /** Executes the consume fields operation. */
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
    /** Executes the get hierarchy operation. */
    public CEntityHierarchy GetHierarchy()
    {
        CEntityHierarchy hier = super.GetHierarchy() ;
        hier.AddLevel(GetName()+"I");
        hier.AddLevel(GetName()+"O");
        return hier;
    }

    /** Executes the is save copy operation. */
    public boolean IsSaveCopy()
    {
        return bSaveMap;
    }

    /** Provides cfield redefine description behavior. */
    public class CFieldRedefineDescription
    {
        public String skip = "SKIP" ;
        public String fieldType = "FIELD" ;
        public String occurs = "OCCURS" ;
        public CEntityResourceField field = null ;
        public String name = "" ;
        public String type = "" ;
        public int size = 0 ;

        /** Executes the next operation. */
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
    /** Provides cfield redefine structure behavior. */
    public class CFieldRedefineStructure
    {
        /** Executes the current operation. */
        public CFieldRedefineDescription Current()
        {
            return current ;
        }
        /** Executes the next operation. */
        public CFieldRedefineDescription Next()
        {
            current = current.Next() ;
            return current;
        }
        protected CFieldRedefineDescription current = null ;
        protected CFieldRedefineDescription start = null ;
    }
    /** Executes the get redefine structure operation. */
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

    /** Exports the xmlfields. */
    public void exportXMLFields(
        SortedSet<CEntityResourceFormContainer.FieldExportDescription> setFields,
        Document doc,
        CResourceStrings res)
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
                exp.isrightJustified = field.isrightJustified;
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

        if (addedItems != null)
        {
            for (int i = 0; i< addedItems.size(); i++)
            {
                CEntityResourceFormContainer.FieldExportDescription exp = addedItems.get(i) ;
                exp.tag = (Element)doc.importNode(exp.tag, true) ;
                setFields.add(exp) ;
            }
        }
        if (lines != null)
        {
            for (int i = 0; i< lines.size(); i++)
            {
                CEntityResourceFormContainer.FieldExportDescription exp = lines.get(i) ;
                exp.tag = doc.createElement("line") ;
                exp.tag.setAttribute("line", String.valueOf(exp.getLine())) ;
                exp.tag.setAttribute("start", String.valueOf(exp.col)) ;
                exp.tag.setAttribute("length", String.valueOf(exp.length)) ;
                setFields.add(exp) ;
            }
        }
    }

    protected HashMap<String, String> tabActivePFKeys = new HashMap<String, String>() ;
    /** Returns the pfactive. */
    public String getPFActive(String key)
    {
        return tabActivePFKeys.get(key);
    }
    /** Sets the pfactive. */
    public void setPFActive(String key, String status)
    {
        tabActivePFKeys.put(key, status);
    }

    protected HashMap<String, String> tabActionPFKeys = new HashMap<String, String>() ;
    /** Returns the pfaction. */
    public String getPFAction(String key)
    {
        return tabActionPFKeys.get(key);
    }
    /** Sets the pfaction. */
    public void setPFAction(String key, String action)
    {
        tabActionPFKeys.put(key, action);
    }

    /** Executes the make pfkeys description define operation. */
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
    /** Executes the make pfkeys description action operation. */
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

//  public void setDisplayName(String string)
//  {
//      csDisplayName = string ;
//  }
//  protected String csDisplayName = "" ;

    /** Executes the clear operation. */
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

    /** Sets the developable. */
    public void setDevelopable(String name, String flagMark)
    {
        CEntityResourceField field = getField(name) ;
        if (field != null)
        {
            field.setDevelopable(flagMark) ;
        }
    }

    /** Sets the format. */
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
    /** Sets the linked active choice. */
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
        for (int i = 0; i< formReferences.size(); i++)
        {
            if (formReferences.get(i).equals(id))
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
    public void exportCustomProperties(Document doc)
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

    /** Adds the switch case. */
    public void AddSwitchCase(String name, String value, String protection, Element tag)
    {
        CEntityResourceField field = getField(name) ;
        if (field != null)
        {
            field.AddSwitchCase(value, protection, tag);
        }
    }

    /** Executes the hide field operation. */
    public void HideField(String name)
    {
        CEntityResourceField field = getField(name) ;
        if (field != null)
        {
            field.Hide();
        }
    }

    /** Executes the hide field operation. */
    public void HideField(int col, int line)
    {
        CEntityResourceField field = getField(col, line);
        if (field != null)
        {
            field.Hide() ;
        }
    }

    /** Adds the item. */
    public void AddItem(int c, int l, int s, Element tag)
    {
        if (addedItems == null)
        {
            addedItems = new Vector<CEntityResourceFormContainer.FieldExportDescription>() ;
        }
        CEntityResourceFormContainer.FieldExportDescription exp = new CEntityResourceFormContainer.FieldExportDescription() ;
        exp.col = c ;
        exp.setLine(l) ;
        exp.length = s ;
        exp.tag = tag ;
        exp.type = FieldExportType.TYPE_CUSTOM ;
        addedItems.add(exp) ;
    }
    protected Vector<CEntityResourceFormContainer.FieldExportDescription> addedItems = null ;
    protected Vector<CEntityResourceFormContainer.FieldExportDescription> lines = null ;

    /** Adds the line. */
    public void AddLine(int c, int l, int s)
    {
        if (lines == null)
        {
            lines = new Vector<CEntityResourceFormContainer.FieldExportDescription>() ;
        }
        CEntityResourceFormContainer.FieldExportDescription exp = new CEntityResourceFormContainer.FieldExportDescription() ;
        exp.col = c ;
        exp.setLine(l) ;
        exp.length = s ;
        exp.tag = null ;
        exp.type = FieldExportType.TYPE_LINE;
        lines.add(exp) ;
    }

    /** Executes the move field operation. */
    public void MoveField(String name, int nc, int nl)
    {
        CEntityResourceField field = getField(name) ;
        if (field != null)
        {
            field.move(nc, nl);
        }
    }

    /** Executes the move field operation. */
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

    // ---------------------------------------------------------------------------------------------
    // Retired direct backend generate.java.forms.CJavaForm: the output protocols below were moved
    // out of the generate layer onto this pure semantic entity when the backend was retired onto
    // the recursive ST4 assembly contract. They read only precomputed state; the two generate-layer
    // operations the backend performed inline (identifier formatting and the declaration/block
    // rendering) are injected by the generate-layer factory (BmsJavaEntities.form), so this tree
    // names no generate.* class.
    // ---------------------------------------------------------------------------------------------

    /* (non-Javadoc)
     * @see semantic.CBaseDataEntity#GetDataType()
     */
    /** Executes the get data type operation. */
    public CDataEntityType GetDataType()
    {
        // Preserved from the retired backend: a screen map bears the FORM data type.
        return CDataEntityType.FORM ;
    }

    /** Executes the has accessors operation. */
    public boolean HasAccessors()
    {
        // Preserved from the retired backend: a form bears no accessors.
        return false;
    }

    public boolean isValNeeded()
    {
        // Preserved from the retired backend: a form is never declared as a val.
        return false;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseExternalEntity#GetTypeDecl()
     */
    /** Executes the get type decl operation. */
    public String GetTypeDecl()
    {
        // Preserved from the retired backend: a form contributes no type declaration (unused).
        return "";
    }

    /**
     * Target-neutral identifier formatter standing in for the retired backend's
     * {@code LegacyLanguageRenderer.formatIdentifier}. Installed by the generate-layer factory
     * ({@code BmsJavaEntities.form} injects the bound output's {@code FormatIdentifier}); defaults
     * to the neutral legacy fallback so a directly constructed entity stays well-formed. A pure
     * injected value — no {@code generate.*} coupling lives in this tree.
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
     * Pure read-only getter consumed by the {@code recursiveFormDeclarationEntity} template: the
     * form's Java variable name — the target-formatted {@code GetName()}. Mirrors the retired
     * backend's {@code formatIdentifier(GetName())}. A pure formatting step over precomputed state.
     */
    public String getFormattedName()
    {
        return identifierFormatter.apply(GetName()) ;
    }

    /**
     * Pure read-only getter consumed by the {@code recursiveFormDeclarationEntity} template: the
     * quoted {@code declare.form("<resourceName>", ...)} argument — the target-formatted resource
     * name. Mirrors the retired backend's {@code formatIdentifier(csResourceName)}. A pure
     * formatting step over precomputed state.
     */
    public String getFormattedResourceName()
    {
        return identifierFormatter.apply(csResourceName) ;
    }

    /**
     * Pure read-only getter consumed by the {@code recursiveFormDeclarationEntity} template and by
     * the {@code recursiveFormEntity} reference binding: the full data reference to this form.
     * Mirrors the retired backend's {@code ExportReference}: when the parser set the {@code of}
     * qualifier (the enclosing mapset container, {@code CMapElement} assigns {@code ef.of =
     * container}), the target-formatted container name prefixes the target-formatted form name with
     * a {@code .}; otherwise the reference is just the target-formatted form name. Reads only
     * precomputed names through the injected formatter — no data-reference resolution, no lowering.
     */
    public String getFormReference()
    {
        String ref = "" ;
        if (of != null)
        {
            ref = identifierFormatter.apply(of.GetName()) + "." ;
        }
        ref += identifierFormatter.apply(GetName()) ;
        return ref ;
    }

    /**
     * Pure read-only getter consumed by the {@code recursiveFormDeclarationEntity} template: the
     * {@code declare.form(..., <sizeLine>, ...)} argument — the screen line count the parser
     * resolved ({@link #SetSize}). A plain field read.
     */
    public int getSizeLine()
    {
        return nSizeLine ;
    }

    /**
     * Pure read-only getter consumed by the {@code recursiveFormDeclarationEntity} template: the
     * {@code declare.form(..., ..., <sizeCol>)} argument — the screen column count the parser
     * resolved ({@link #SetSize}). A plain field read.
     */
    public int getSizeCol()
    {
        return nSizeCol ;
    }

}
