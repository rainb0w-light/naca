/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.Cobol.elements;

import java.util.Vector;

import lexer.CBaseToken;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import parser.expression.CConstantTerminal;
import parser.expression.CIdentifierTerminal;
import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CBaseLanguageEntity;
import semantic.Verbs.CEntityAddTo;
import utils.CGlobalEntityCounter;
import utils.Transcoder;

/**
 * @author U930CV
 *
 */
public class CAdd extends CCobolElement
{
    /**
     * @param line
     */
    protected CAdd(int line) {
        super(line);
    }
    /* (non-Javadoc)
     * @see parser.CLanguageElement#Parse(lexer.CTokenList)
     */
    protected boolean DoParsing()
    {
        CBaseToken tok = GetCurrentToken() ;
        if (tok.GetKeyword() != CCobolKeywordList.ADD)
        {
            Transcoder.logError(getLine(), "Expecting 'ADD' keyword") ;
            return false ;
        }
        CGlobalEntityCounter.GetInstance().CountCobolVerb(tok.GetKeyword().name) ;
        GetNext() ;

        boolean isdone = false ;
        while (!isdone)
        {
            CTerminal t = ReadTerminal() ;
            if (t == null)
            {
                isdone = true ;
            }
            else
            {
                values.add(t) ;
            }
            tok = GetCurrentToken() ;
            if (tok.GetType() == CTokenType.COMMA)
            {
                GetNext() ;
            }
            else if (tok.GetKeyword() == CCobolKeywordList.TO)
            {
                isdone = true ;
            }
        }

        CBaseToken tokTo= GetCurrentToken() ;
        if (tokTo.GetKeyword() == CCobolKeywordList.TO)
        { // 'TO' is optional
            GetNext() ;
            isdone = false ;
            while (!isdone)
            {
                CBaseToken destinationToken = GetCurrentToken() ;
                String destinationValue = destinationToken.GetValue();
                boolean isFigurativeZero = destinationValue != null
                    && ("ZERO".equalsIgnoreCase(destinationValue)
                        || "ZEROS".equalsIgnoreCase(destinationValue)
                        || "ZEROES".equalsIgnoreCase(destinationValue));
                if (isFigurativeZero)
                {
                    hasFigurativeToOperand = true ;
                }
                CTerminal t = ReadTerminal() ;
                if (t == null && (destinationToken.GetConstant() != null
                    || isFigurativeZero))
                {
                    GetNext() ;
                    t = createConstantTerminal(destinationToken) ;
                }
                if (t == null)
                {
                    isdone = true ;
                }
                else
                {
                    toOperands.add(t) ;
                }
                tok = GetCurrentToken() ;
                if (tok.GetType() == CTokenType.COMMA)
                {
                    GetNext() ;
                }
            }
        }

        tok = GetCurrentToken() ;
        boolean hasGiving = false ;
        if (tok.GetKeyword() == CCobolKeywordList.GIVING)
        {
            hasGiving = true ;
            GetNext();
            isdone = false ;
            while (!isdone)
            {
                CIdentifier identifier = ReadIdentifier() ;
                if (identifier == null)
                {
                    isdone = true ;
                }
                else
                {
                    result.add(identifier) ;
                }
            }
        }
        if (!hasGiving)
        {
            if (hasFigurativeToOperand)
            {
                Transcoder.logError(getLine(), "ADD TO target must be a writable identifier") ;
                return false ;
            }
            for (CTerminal destination : toOperands)
            {
                if (!isWritableIdentifier(destination))
                {
                    Transcoder.logError(getLine(), "ADD TO target must be a writable identifier") ;
                    return false ;
                }
            }
        }

        tok = GetCurrentToken() ;
        if (tok.GetKeyword() == CCobolKeywordList.ROUNDED)
        {
            GetNext() ;
            isrounded = true ;
        }

        return true ;
    }
    /* (non-Javadoc)
     * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
     */
    protected Element ExportCustom(Document root)
    {
        Element e = root.createElement("Add") ;
        for (int i = 0; i< values.size(); i++)
        {
            Element eVal = root.createElement("Add");
            e.appendChild(eVal);
            CTerminal value = values.get(i);
            value.ExportTo(eVal, root) ;
        }
        for (int i = 0; i< toOperands.size(); i++)
        {
            Element eTo = root.createElement("To") ;
            CTerminal terminal = toOperands.get(i) ;
            terminal.ExportTo(eTo, root) ;
            e.appendChild(eTo) ;
        }
        for (int i = 0; i< result.size(); i++)
        {
            Element eTo = root.createElement("Giving") ;
            CIdentifier id = result.get(i) ;
            id.ExportTo(eTo, root) ;
            e.appendChild(eTo) ;
        }
        return e ;
    }

    protected Vector<CTerminal> values = new Vector<CTerminal>() ;
    protected Vector<CTerminal> toOperands = new Vector<CTerminal>() ;
    protected Vector<CIdentifier> result = new Vector<CIdentifier>() ;
    protected boolean isrounded ;
    protected boolean hasFigurativeToOperand ;
    /* (non-Javadoc)
     * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
     */
    protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
    {
        if (result.size() == 0)
        {
            CEntityAddTo eAdd = factory.NewEntityAddTo(getLine()) ;
            parent.AddChild(eAdd) ;
            for (int i = 0; i< values.size(); i++)
            {
                CTerminal value = values.get(i);
                CDataEntity eRef = value.GetDataEntity(getLine(), factory) ;
                eAdd.SetAddValue(eRef) ;
            }
            for (int i = 0; i< toOperands.size(); i++)
            {
                CTerminal destination = toOperands.get(i) ;
                CDataEntity eDest = destination.GetDataEntity(getLine(), factory);
                eAdd.SetAddDest(eDest) ;
            }
            if (isrounded)
            {
                eAdd.SetRounded(true) ;
            }
        }
        else
        {
            CEntityAddTo eAdd = factory.NewEntityAddTo(getLine()) ;
            parent.AddChild(eAdd) ;
            for (int i = 0; i< values.size(); i++)
            {
                CTerminal value = values.get(i);
                CDataEntity eRef = value.GetDataEntity(getLine(), factory) ;
                eAdd.SetAddValue(eRef) ;
            }
            for (int i = 0; i< toOperands.size(); i++)
            {
                CTerminal destination = toOperands.get(i) ;
                CDataEntity eDest = destination.GetDataEntity(getLine(), factory);
                eAdd.SetAddValue(eDest) ;
            }
            if (isrounded)
            {
                eAdd.SetRounded(true) ;
            }
            for (int i = 0; i< result.size(); i++)
            {
                CIdentifier idRes = result.get(i) ;
                CDataEntity eRes = idRes.GetDataReference(getLine(), factory) ;
                eAdd.SetAddDest(eRes);
            }
        }
        return null;
    }

    private boolean isWritableIdentifier(CTerminal destination)
    {
        String value = destination.GetValue() ;
        if ("ZERO".equals(value) || "ZEROS".equals(value) || "ZEROES".equals(value))
        {
            return false ;
        }
        if (!destination.IsReference())
        {
            return false ;
        }
        if (destination instanceof CIdentifierTerminal)
        {
            String name = ((CIdentifierTerminal) destination).GetIdentifier().GetName() ;
            return !"ZERO".equals(name) && !"ZEROS".equals(name) && !"ZEROES".equals(name) ;
        }
        return true ;
    }

    private CConstantTerminal createConstantTerminal(CBaseToken token)
    {
        return new CConstantTerminal(token.GetValue()) ;
    }
}
