/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser;

import java.util.ListIterator;
import java.util.NoSuchElementException;


import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;

/** Provides clanguage element behavior. */
public abstract class CLanguageElement extends CBaseElement
{
    /** Creates a new clanguage element instance. */
    public CLanguageElement(int line)
    {
        super(line);
    }

    protected abstract CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory) ;
    protected boolean bAnalysisDoneForChildren = false ;
    /** Executes the do semantic analysis operation. */
    public CBaseLanguageEntity DoSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        CBaseLanguageEntity eCurrent = DoCustomSemanticAnalysis(parent, factory) ;
        if (eCurrent == null)
        {
            return null ;
        }
        if (!bAnalysisDoneForChildren)
        {
            DoSemanticAnalysisForChildren(eCurrent, factory) ;
        }
        return eCurrent ;
    }
    protected void DoSemanticAnalysisForChildren(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        ListIterator<CBaseElement> i = children.listIterator() ;
        CBaseElement le = null ;
        try
        {
            le = i.next() ;
        }
        catch (NoSuchElementException e)
        {
        }
        while (le != null)
        {
            CBaseLanguageEntity e = le.DoSemanticAnalysis(parent, factory) ;
            try
            {
                le = i.next() ;
            }
            catch (NoSuchElementException ee)
            {
                le = null ;
            }
        }
        bAnalysisDoneForChildren = true ;
    }
}
