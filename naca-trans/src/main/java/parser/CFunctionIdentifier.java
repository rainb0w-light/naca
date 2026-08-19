/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.Cobol.CCobolElement;
import parser.expression.CExpression;

import semantic.CDataEntity;
import semantic.CBaseEntityFactory;
import semantic.CSubStringAttributReference;
import semantic.expression.CBaseEntityExpression;
import semantic.expression.CBaseEntityFunction;
import utils.Transcoder;
import lexer.CBaseToken;
import lexer.CReservedKeyword;
import lexer.CTokenList;
import lexer.CTokenType;
import lexer.Cobol.CCobolKeywordList;

/**
 * @author sly
 *
 */
public class CFunctionIdentifier extends CIdentifier
{

    /**
     * @param s
     */
    public CFunctionIdentifier(CTokenList lstTokens, CCobolElement owner)
    {
        super("");
        Parse(lstTokens, owner) ;
    }

    protected void Parse(CTokenList lstTokens, CCobolElement owner)
    {
        CBaseToken tok = lstTokens.GetCurrentToken();
        if (tok.GetKeyword() == CCobolKeywordList.FUNCTION)
        {
            tok = lstTokens.GetNext();
        }

        if (tok.GetKeyword() == CCobolKeywordList.CURRENT_DATE)
        {
            function = tok.GetKeyword() ;
            lstTokens.GetNext() ;
        }
        else if (tok.GetKeyword() == CCobolKeywordList.LENGTH)
        {
            CBaseToken tokOf = lstTokens.GetNext() ;
            if (tokOf.GetKeyword() == CCobolKeywordList.OF)
            {
                function = tok.GetKeyword() ;
                lstTokens.GetNext() ;
                parameter = owner.ReadIdentifier();
            }
            else
            {
                Transcoder.logError(tok.getLine(), "Unexpecting situation");
            }
        }
        else if (tok.GetKeyword() == CCobolKeywordList.ADDRESS)
        {
            CBaseToken tokOf = lstTokens.GetNext() ;
            if (tokOf.GetKeyword() == CCobolKeywordList.OF)
            {
                function = tok.GetKeyword() ;
                lstTokens.GetNext() ;
                parameter = owner.ReadIdentifier();
            }
            else
            {
                Transcoder.logError(tok.getLine(), "Unexpecting situation");
            }
        }
        else if (tok.GetType() == CTokenType.IDENTIFIER)
        {
            intrinsicFunctionName = tok.GetValue().toUpperCase();
            tok = lstTokens.GetNext();
            if (tok.GetType() != CTokenType.LEFT_BRACKET)
            {
                Transcoder.logError(tok.getLine(), "Expecting '(' after intrinsic function " + intrinsicFunctionName);
                return;
            }

            tok = lstTokens.GetNext();
            if (tok.GetType() == CTokenType.RIGHT_BRACKET)
            {
                lstTokens.GetNext();
                return;
            }

            boolean done = false;
            while (!done)
            {
                CExpression argument = owner.ReadCalculExpression();
                if (argument == null)
                {
                    Transcoder.logError(tok.getLine(), "Cannot read argument of intrinsic function " + intrinsicFunctionName);
                    return;
                }
                intrinsicArguments.add(argument);

                tok = lstTokens.GetCurrentToken();
                if (tok.GetType() == CTokenType.COMMA)
                {
                    tok = lstTokens.GetNext();
                }
                else if (tok.GetType() == CTokenType.RIGHT_BRACKET)
                {
                    lstTokens.GetNext();
                    done = true;
                }
                else
                {
                    Transcoder.logError(tok.getLine(), "Expecting ',' or ')' in intrinsic function " + intrinsicFunctionName);
                    return;
                }
            }
        }
        else
        {
            Transcoder.logError(tok.getLine(), "Unexpecting token : "+tok.GetValue());
        }
    }

    public CDataEntity GetDataReference(int nLine, CBaseEntityFactory fact)
    {
        CBaseEntityFunction f = null ;
        if (function == CCobolKeywordList.LENGTH)
        {
            CDataEntity e = parameter.GetDataReference(nLine, fact);
            f = fact.NewEntityLengthOf(e);
        }
        else if (function == CCobolKeywordList.ADDRESS)
        {
            CDataEntity e = parameter.GetDataReference(nLine, fact);
            f = fact.NewEntityAddressOf(e);
        }
        else if (function == CCobolKeywordList.CURRENT_DATE)
        {
            f = fact.NewEntityCurrentDate();
            if (exprStringLengthReference != null & exprStringStartReference != null)
            {
                CSubStringAttributReference ref = fact.NewEntitySubString(nLine);
                CBaseEntityExpression start = exprStringStartReference.AnalyseExpression(fact) ;
                CBaseEntityExpression len = exprStringLengthReference.AnalyseExpression(fact) ;
                ref.SetReference(f, start, len) ;
                return ref ;
            }
        }
        else if (intrinsicFunctionName != null)
        {
            List<CBaseEntityExpression> arguments = new ArrayList<>();
            for (CExpression argument : intrinsicArguments)
            {
                CBaseEntityExpression semanticArgument = argument.AnalyseExpression(fact);
                if (semanticArgument == null)
                {
                    Transcoder.logError(nLine, "Missing semantic argument for intrinsic function " + intrinsicFunctionName);
                    return null;
                }
                arguments.add(semanticArgument);
            }
            f = fact.NewEntityIntrinsicFunction(intrinsicFunctionName, arguments);
        }
        else
        {
            Transcoder.logError(nLine, "Missing semantic analysis for FUNCTIONS");
            f = null ;
        }
        return f ;
    }

    public void ExportTo(Element e, Document root)
    {
        if (function == CCobolKeywordList.LENGTH)
        {
            Element eLen = root.createElement("LengthOf");
            e.appendChild(eLen);
            parameter.ExportTo(eLen, root);
        }
        else if (function == CCobolKeywordList.ADDRESS)
        {
            Element eLen = root.createElement("AddressOf");
            e.appendChild(eLen);
            parameter.ExportTo(eLen, root);
        }
        else if (function == CCobolKeywordList.CURRENT_DATE)
        {
            e.setAttribute("Function", "Current-Date") ;
        }
        else if (intrinsicFunctionName != null)
        {
            Element intrinsic = root.createElement("IntrinsicFunction");
            intrinsic.setAttribute("Name", intrinsicFunctionName);
            e.appendChild(intrinsic);
            for (CExpression argument : intrinsicArguments)
            {
                intrinsic.appendChild(argument.Export(root));
            }
        }
        else
        {
            Element eLen = root.createElement("Undefined");
            e.appendChild(eLen);
        }
    }

    protected CReservedKeyword function = null ;
    protected CIdentifier parameter = null ;
    private String intrinsicFunctionName = null;
    private final List<CExpression> intrinsicArguments = new ArrayList<>();
}
