/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 30 juil. 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.BMS;


import java.util.Hashtable;

import org.apache.log4j.Logger;

import parser.CGlobalCommentContainer;
import parser.CParser;
import parser.map_elements.*;
import utils.Transcoder;
import lexer.*;
import lexer.BMS.CBMSKeywordList;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CBMSParser extends CParser<CMapSetElement>
{	
	//protected Logger m_logger = Transcoder.ms_logger ;
	
	
	protected boolean DoParsing(CTokenList lstTokens)
	{
		commentContainer = new CGlobalCommentContainer();
//		commentContainer.lstTokens = lstTokens ;
		
		CBaseToken tokID = lstTokens.GetCurrentToken() ;
		String name = "" ;
		if (tokID.GetType() == CTokenType.END_OF_BLOCK)
		{
			tokID = lstTokens.GetNext();
		}
		if (tokID.GetType() == CTokenType.IDENTIFIER)
		{
			name = tokID.GetValue() ;
			lstTokens.GetNext() ; 
		}
		
		CBaseToken tokMS = lstTokens.GetCurrentToken() ;
		if (tokMS.GetKeyword() != CBMSKeywordList.DFHMSD)
		{
			Transcoder.logError(tokMS.getLine(), "Missing DFHMSD for " + name) ;
			return false ;
		}
		lstTokens.GetNext();
		eRoot = new CMapSetElement(name, tokID.getLine()) ;
		if (!eRoot.Parse(lstTokens, commentContainer))
		{
			Transcoder.logError("Error while parsing MAPSET") ;
			return false ;
		}
		
		String csAlias = "" ;
		Hashtable<String, CFieldGroup> tabGroups = new Hashtable<String, CFieldGroup>();
		boolean bDone = false ;
		while (!bDone)
		{
			tokID = lstTokens.GetCurrentToken() ; 
			String elName = "" ;
			if (tokID.GetType() == CTokenType.IDENTIFIER)
			{
				elName = tokID.GetValue() ;
				lstTokens.GetNext() ;
			} 
			
			CBaseToken tokMap = lstTokens.GetCurrentToken();
			if (tokMap.GetKeyword() == CBMSKeywordList.DFHMDI)
			{
				lstTokens.GetNext() ;
				curMap = new CMapElement(elName, tokMap.getLine()) ;
				eRoot.AddElement(curMap) ;
				if (!curMap.Parse(lstTokens, commentContainer))
				{
					Transcoder.logError("Error while parsing MAP") ;
					return false ; 
				}
			}
			else if (tokMap.GetKeyword() == CBMSKeywordList.DFHMDF)
			{
				lstTokens.GetNext();
				CFieldElement eField = new CFieldElement(elName, tokMap.getLine()) ;
				if (!eField.Parse(lstTokens, commentContainer))
				{
					Transcoder.logError("Error while parsing FIELD") ;
					return false ; 
				}
				String grp = eField.GetGroupName() ;
				if (grp.equals(""))
				{
					if (!csAlias.equals(""))
					{
//						csAlias = csAlias.replace('(', '_') ;
//						csAlias = csAlias.replace(')', '_') ;
						eField.SetName(csAlias) ;
						if (csAlias.indexOf('(')>0 && csAlias.indexOf(')')>0)
						{
							curMap.setFindArrays();
						} 
						csAlias = "" ;
					}
					curMap.AddElement(eField) ;
				}
				else
				{
					String grpName = grp ;
					if (!csAlias.equals(""))
					{
						grpName = csAlias ;
						csAlias = "" ;
					}
					CFieldGroup grpField = tabGroups.get(grp) ;
					if (grpField == null)
					{
						grpField = new CFieldGroup(grpName);
						tabGroups.put(grp, grpField);
						curMap.AddElement(grpField) ;
						grpField.setPosition(eField);
					}
					grpField.AddChildField(eField) ;
				}
			}
			else if (tokMap.GetType() == CTokenType.COMMENTS)
			{
				String comm = tokMap.GetValue().trim() ;
				if (comm.startsWith("'") && comm.endsWith("'"))
				{
					csAlias = comm.substring(1, comm.length()-1) ; 
					lstTokens.GetNext() ;
				}
				else
				{
					commentContainer.ParseComment(lstTokens) ;
				}
			}
			else if (tokMap.GetType() == CTokenType.END_OF_BLOCK)
			{
				lstTokens.GetNext();
			}
			else
			{
				bDone = true ;
			}
			
		}
		return true ;
	}  

	protected CMapElement curMap = null ; 

//	public class CBMSCommentContainer extends CCommentContainer
//	{
//		public CBMSCommentContainer(int line)
//		{
//			super(line);
//		}
//		protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
//		{
//			return null;
//		}
//		protected Element ExportCustom(Document root)
//		{
//			return null;
//		}
//	}
//	public CGlobalCommentContainer commentContainer = null ;
}
