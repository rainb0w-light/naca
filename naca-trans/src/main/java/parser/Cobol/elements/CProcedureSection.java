/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 28, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;


import lexer.CBaseToken;
import lexer.CTokenType;


import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CCommentContainer;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.CEntityBloc;
import semantic.CEntityProcedureSection;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CProcedureSection extends CCommentContainer
{
	public CProcedureSection(String name, int line)
	{
		super(line);
		name = name ;
	}
	
	protected boolean DoParsing()
	{
		CBaseToken tok = GetCurrentToken() ;
		if (tok == null)
		{
			return true ; // empty section
		}
		if (tok.GetType() == CTokenType.KEYWORD)
		{
			sectionBloc = new CBaseProcedure(getLine());
			if (!Parse(sectionBloc))
			{
				return false ;
			}
		}
		return true;
	}

	protected Element ExportCustom(Document root)
	{
		Element e = root.createElement("ProcedureSection") ;
		if (!name.equals(""))
		{
			e.setAttribute("Name", name) ;
		}
		if (sectionBloc != null)
		{
			Element eBloc = sectionBloc.Export(root);
			e.appendChild(eBloc);
		}
		return e;
	}
	public void AddProcedure(CProcedure p)
	{
		AddChild(p) ;
	}
	
	protected String name = "" ;
	protected CBaseProcedure sectionBloc = null ;

	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityProcedureSection eSection ;
		if (name.equals(""))
		{
			eSection = factory.NewEntityProcedureSection(0, "") ;
		}
		else
		{
			eSection = factory.NewEntityProcedureSection(getLine(), name) ;
			factory.programCatalog.RegisterProcedureSection(eSection) ;
		}
		parent.AddChild(eSection);

		if (sectionBloc != null)
		{
			CEntityBloc el = (CEntityBloc)sectionBloc.DoSemanticAnalysis(eSection, factory);
			eSection.SetSectionBloc(el) ;
		}		
		return eSection;
	}
}
