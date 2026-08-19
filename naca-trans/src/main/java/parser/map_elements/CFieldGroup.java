/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.map_elements;

import lexer.CReservedKeyword;
import lexer.CTokenList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.CEntityStructure;
import semantic.expression.CEntityString;
import semantic.forms.CEntityResourceField;
import semantic.forms.CResourceStrings;
import utils.CEntityHierarchy;

/**
 * @author U930CV
 *
 */
public class CFieldGroup extends CFieldElement
{

    /**
     * @param name
     * @param line
     */
    public CFieldGroup(String name)
    {
        super(name, 0);
    }

    /* (non-Javadoc)
     * @see parser.CBMSElement#GetType()
     */
    /** Executes the get type operation. */
    public EBMSElementType GetType()
    {
        return EBMSElementType.GROUP ;
    }

    /* (non-Javadoc)
     * @see parser.CBMSElement#DoExportCustom(org.w3c.dom.Document)
     */
    protected Element DoExportCustom(Document root)
    {
        Element eGrp = root.createElement("Group") ;
        eGrp.setAttribute("Name", getName()) ;
        eGrp.setAttribute("PosLine", String.valueOf(posLine));
        eGrp.setAttribute("PosCol", String.valueOf(posCol));
        return eGrp ;
    }

    /* (non-Javadoc)
     * @see parser.CBMSElement#InterpretKeyword(lexer.CReservedKeyword, lexer.CTokenList)
     */
    protected boolean InterpretKeyword(CReservedKeyword kw, CTokenList lstTokens)
    {
        // nothing
        return false;
    }

    /* (non-Javadoc)
     * @see parser.CBMSElement#GetResourceStrings()
     */
    /** Executes the get resource strings operation. */
    public CResourceStrings GetResourceStrings()
    {
        // nothing
        return null;
    }

    /* (non-Javadoc)
     * @see parser.CBMSElement#SetResourceStrings(semantic.forms.CResourceStrings)
     */
    /** Sets the resource strings. */
    public void SetResourceStrings(CResourceStrings res)
    {
        // nothing
    }


    /** Executes the do semantic analysis operation. */
    public CBaseLanguageEntity DoSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        CEntityResourceField ef ;
        CEntityHierarchy hier = null ;
        ef = factory.NewEntityEntryField(getLine(), getName()) ;

        CFieldElement[] fields = new CFieldElement[children.size()] ;
        children.toArray(fields) ;
        ef.nLength = 0;
        for (int i = 0; i< fields.length; i++)
        {
            CFieldElement el = fields[i] ;
            ef.nLength += el.length ;
            if (i==0)
            {
                ef.nPosCol = el.posCol ;
                ef.nPosLine = el.posLine ;
                ef.csInitialValue = "" ;
                // el.value ;
                if (el.highLight != null)
                {
                    ef.SetHighLight(el.highLight.name) ;
                }
                if (el.color != null)
                {
                    ef.SetColor(el.color.name) ;
                }
                for (int j = 0; j<el.aTTRB.size(); j++)
                {
                    String cs = el.aTTRB.get(j) ;
                    if (cs.equals("ASKIP")){
                        ef.SetProtection("AUTOSKIP") ;  }
                    else if (cs.equals("UNPROT")){
                        ef.SetProtection("UNPROTECTED");}
                    else if (cs.equals("NUM")){
                        ef.SetProtection("NUMERIC"); }
                    else if (cs.equals("NORM")){
                        ef.SetBrightness("NORMAL");}
                    else if (cs.equals("DRK")){
                        ef.SetBrightness("DARK"); }
                    else if (cs.equals("BRT")){
                        ef.SetBrightness("BRIGHT");}
                    else if (cs.equals("FSET")){
                        ef.SetModified();}
                    else if (cs.equals("IC")){
                        ef.SetCursor();}
                    else{
                        int n=0 ;
                    }
                }
                for (int j=0; j<el.arrJustify.size(); j++)
                {
                    String cs = el.arrJustify.get(j) ;
                    if (cs.equals("LEFT")){
                        ef.SetRightJustified(false) ;}
                    else if (cs.equals("RIGHT")){
                        ef.SetRightJustified(true);}
                    else if (cs.equals("BLANK")){
                        ef.SetFillValue("BLANK");}
                    else if (cs.equals("ZERO") || cs.equals("ZEROS") || cs.equals("ZEROES")){
                        ef.SetFillValue("ZERO");}
                    else{
                        int n=0 ;
                    }
                }
            }
            CEntityStructure es = factory.NewEntityStructure(0, el.getName(), "10") ;
            CEntityString val = factory.NewEntityString(el.value) ;
            es.SetInitialValue(val) ;
            es.SetTypeString(el.length) ;
            ef.AddChild(es) ;
        }
        return ef;
    }

    /** Sets the position. */
    public void setPosition(CFieldElement eField)
    {
        posCol = eField.posCol ;
        posLine = eField.posLine ;
    }

    /** Adds the child field. */
    public void AddChildField(CFieldElement e)
    {
        AddChild(e) ;
    }

}
