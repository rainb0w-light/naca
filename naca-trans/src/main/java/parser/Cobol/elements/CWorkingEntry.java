/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on Jul 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package parser.Cobol.elements;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Vector;

import lexer.*;
import lexer.Cobol.CCobolConstantList;
import lexer.Cobol.CCobolKeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import parser.CIdentifier;
import parser.Cobol.CCobolElement;
import parser.expression.CTerminal;
import semantic.CDataEntity;
import semantic.CBaseLanguageEntity;
import semantic.CBaseEntityFactory;
import semantic.CEntityAttribute;
import semantic.CEntityIndex;
import semantic.CEntityStructure;
import semantic.CEntityValueReference;
import semantic.CIgnoredEntity;
import semantic.ITypableEntity;
import semantic.forms.CEntityFieldAttribute;
import semantic.forms.CEntityFieldColor;
import semantic.forms.CEntityFieldHighlight;
import semantic.forms.CEntityFieldLength;
import semantic.forms.CEntityFieldOccurs;
import semantic.forms.CEntityFieldFlag;
import semantic.forms.CEntityFieldRedefine;
import semantic.forms.CEntityFieldValidated;
import semantic.forms.CEntityFormRedefine;
import semantic.forms.CEntityResourceField;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntitySkipFields;
import semantic.forms.CEntityResourceForm.CFieldRedefineDescription;
import utils.Transcoder;
import utils.CGlobalEntityCounter;
import utils.NacaTransAssertException;

/**
 * @author U930CV
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CWorkingEntry extends CCobolElement
{
	/**
	 * @param line
	 */
	public CWorkingEntry(int line) {
		super(line);
	}

	public static class CWorkingPicType
	{
		public String text = "" ;
		protected CWorkingPicType(String text)
		{
			text = text ;
		}
		public static CWorkingPicType STRING = new CWorkingPicType("STRING") ;
		public static CWorkingPicType NUMBER = new CWorkingPicType("NUMBER") ;
		public static CWorkingPicType SIGNED = new CWorkingPicType("SIGNED NUMBER") ;
		public static CWorkingPicType SIGNED_DECIMAL = new CWorkingPicType("SIGNED DECIMAL") ;
		public static CWorkingPicType DECIMAL = new CWorkingPicType("DECIMAL") ;
		public static CWorkingPicType EDITED = new CWorkingPicType("ZONED NUMBER") ;
	}
	public static class CWorkingEntryType
	{
		protected CWorkingEntryType() {}
		public static CWorkingEntryType STRUCTURE = new CWorkingEntryType() ;
		public static CWorkingEntryType VARIABLE = new CWorkingEntryType() ;
	}
	public static class CWorkingSignType
	{
		protected CWorkingSignType() {}
		public static CWorkingSignType LEADING = new CWorkingSignType() ;
		public static CWorkingSignType TRAILING = new CWorkingSignType() ;
	}
	protected int entryLevel = 0 ;
	protected String formalLevel = "" ;
	protected CWorkingPicType type = null ;
	protected String name = "" ;
	protected CIdentifier redefines = null ;
	protected int length = 0 ;
	protected int decimal = 0 ;
	protected CTerminal value = null ;
	protected String comp = "" ;
	protected boolean sync = false ;
	protected CTerminal occurs = null ;
	protected CIdentifier occursDepending = null ;
	protected CWorkingEntryType entryType = null ;
	protected String format = ""  ;
	protected final List<CIdentifier> occursIndexedBy = new ArrayList<CIdentifier>() ;
	protected CTerminal blankWhenValue = null ;
	protected boolean isfillAll = false ;
	protected boolean isisPointer = false ;
	protected boolean isisIndex = false;
	protected boolean isjustifiedRight = false ;
	protected boolean isblankWhenZero = false ;
	protected CWorkingSignType issignSeparateType;
	protected Vector<CIdentifier> tableSortKey = null ;
	protected boolean istableSortedAscending = false ;
	protected boolean isbinary = true ;
	

	/* (non-Javadoc)
	 * @see parser.CLanguageElement#Parse(lexer.CTokenList)
	 */
	protected boolean DoParsing()
	{
		CBaseToken tokEntry = GetCurrentToken();
		if (tokEntry.GetType() == CTokenType.NUMBER)
		{
			entryLevel = tokEntry.GetIntValue() ;
			if (entryLevel == 77)
			{
				entryType = CWorkingEntryType.VARIABLE ;
				CGlobalEntityCounter.GetInstance().CountCobolVerb("WORKING_VARIABLE") ;
			}
			else
			{
				entryType = CWorkingEntryType.STRUCTURE ;
				CGlobalEntityCounter.GetInstance().CountCobolVerb("WORKING_ENTRY") ;
			} 
			formalLevel = tokEntry.GetValue() ;
			CBaseToken tokName = GetNext(); // consume PIC LEVEL
			if (tokName.IsKeyword() && tokName.GetKeyword()==CCobolKeywordList.FILLER)
			{
				name = "" ;
				GetNext() ; // consume FILLER
			}
			else if (tokName.GetType() == CTokenType.IDENTIFIER)
			{
				name = tokName.GetValue() ;
				GetNext() ; // consume NAME
			}
			if (!ParsePicOptions())
			{
				return false ;
			}
			return ParseContent() ; // no PIC type, certainly a structure
		}
		else
		{
			Transcoder.logError(getLine(), "Unexpecting token : " + tokEntry.GetValue()) ;
			return false ;
		}
	}
	
	protected boolean ParsePicOptions()
	{
		boolean isdone = false ;
		while (!isdone)
		{
			CBaseToken tokPic = GetCurrentToken();
			if (tokPic == null)
			{
				return true;
			}
			if (tokPic.GetType() == CTokenType.DOT)
			{
				GetNext() ; // consume DOT
				return true ;
			}
			boolean isnext = false ;
			if (tokPic.GetKeyword()==CCobolKeywordList.REDEFINES)
			{	// in case of redefine...
				CBaseToken tokRedefine = GetNext() ; // consume REDEFINES, expecting an identifier
				if (tokRedefine.GetType() == CTokenType.IDENTIFIER)
				{
					redefines = ReadIdentifier() ;
					isnext = true ;
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting sequence : " + tokPic.toString() + tokRedefine.toString()) ;
					return false ;
				}
			}
			else if (tokPic.GetKeyword()==CCobolKeywordList.PIC || tokPic.GetKeyword()==CCobolKeywordList.PICTURE)
			{
				boolean b = ParsePicItSelf() ;
				if (b)
				{
					isnext = true ;
				}
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.COMP_4)
			{
				comp = "COMP4" ;
				isnext = true ;
				GetNext();
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.COMP_3)
			{
				comp = "COMP3" ;
				isnext = true ;
				GetNext();
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.COMP_2)
			{
				comp = "COMP2" ;
				isnext = true ;
				GetNext();
			}
			else if (tokPic.GetKeyword()==CCobolKeywordList.COMP || tokPic.GetKeyword()==CCobolKeywordList.COMP_5 || tokPic.GetKeyword()==CCobolKeywordList.COMPUTATIONAL)
			{
				comp = "COMP" ;
				isnext = true ;
				GetNext();
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.SYNC)
			{
				sync = true ;
				isnext = true ;
				GetNext();
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.SIGN)
			{
				CBaseToken tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.TRAILING)
				{
					tok = GetNext();
					if (tok.GetKeyword() == CCobolKeywordList.SEPARATE)
					{
						issignSeparateType = CWorkingSignType.TRAILING ;
						isnext = true ;
						GetNext();
					}
					else
					{
						return false ;
					}
				}
				else if (tok.GetKeyword() == CCobolKeywordList.LEADING)
				{
					tok = GetNext();
					if (tok.GetKeyword() == CCobolKeywordList.SEPARATE)
					{
						issignSeparateType = CWorkingSignType.LEADING ;
						isnext = true ;
						GetNext();
					}
					else
					{
						return false ;
					}
				}
				else
				{
					return false ;
				}
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.BLANK)
			{
				CBaseToken tok = GetNext() ;
				if (tok.GetKeyword() == CCobolKeywordList.WHEN)
				{
					tok = GetNext();
				}
				if (tok.GetConstant() == CCobolConstantList.ZERO || tok.GetConstant() == CCobolConstantList.ZEROS || tok.GetConstant() == CCobolConstantList.ZEROES)
				{
					tok = GetNext();
					isblankWhenZero = true ;
					isnext = true ;
				}
				else
				{
					return false ;
				}
			}
			else if (tokPic.GetKeyword()==CCobolKeywordList.JUST || tokPic.GetKeyword()==CCobolKeywordList.JUSTIFIED)
			{
				isjustifiedRight = true ;

				isnext = true ;
				CBaseToken tok = GetNext();
				if (tok.GetKeyword() == CCobolKeywordList.RIGHT)
				{
					GetNext();
				}
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.USAGE)
			{
				isnext = true ;
				CBaseToken tok = GetNext();
				if (tok.GetKeyword() == CCobolKeywordList.IS)
				{
					tok = GetNext();
				}
				if (tok.GetKeyword() == CCobolKeywordList.POINTER)
				{
					isisPointer = true ;
					tok = GetNext();
				}
				else if (tok.GetKeyword() == CCobolKeywordList.INDEX)
				{
					isisIndex = true ;
					tok = GetNext();
				}
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.BINARY)
			{
				GetNext() ;
				isnext = true ;
				isbinary = true ;
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.VALUE)
			{
				CBaseToken tokValue = GetNext() ;
				if (tokValue.GetKeyword() == CCobolKeywordList.IS)
				{
					tokValue = GetNext() ;
				}
				if (tokValue.GetType()==CTokenType.STRING || tokValue.GetType()==CTokenType.NUMBER || 
					tokValue.GetType()==CTokenType.CONSTANT || tokValue.GetType()==CTokenType.MINUS)
				{
					value = ReadTerminal() ; 
				}
				else if (tokValue.GetType()==CTokenType.PLUS)
				{
					CBaseToken tokNum = GetNext();
					if (tokNum.GetType() == CTokenType.NUMBER)
					{
						value = ReadTerminal() ; 
					}
					else
					{
						Transcoder.logError(getLine(), "Expecting NUM value as value for PIC, instead of : " + tokNum.toString()) ;
					}
				}
				else if (tokValue.GetKeyword() == CCobolKeywordList.ALL)
				{
					GetNext();
					value = ReadTerminal() ;
					isfillAll = true ;
				}
				isnext = true ;
			}
			else if (tokPic.GetKeyword() == CCobolKeywordList.BLANK)
			{
				isnext = true ;
				tokPic = GetNext();
				if (tokPic.GetKeyword() == CCobolKeywordList.WHEN)
				{
					tokPic = GetNext();
					blankWhenValue = ReadTerminal() ;
				}
			}
			else if (tokPic.GetKeyword() == CCobolKeywordList.IS)
			{
				isnext = false ;
				if(GetNext().GetKeyword() == CCobolKeywordList.EXTERNAL)
				{
					Transcoder.logWarn(getLine(), "External variable not supported");
					GetNext();
				}
			}
			else if (tokPic.IsKeyword() && tokPic.GetKeyword()==CCobolKeywordList.OCCURS)
			{
				isnext = true ;
				CBaseToken tokOccurs = GetNext() ;
				if (tokOccurs.GetType() == CTokenType.NUMBER)
				{
					occurs = ReadTerminal() ;
					CBaseToken tokTimes = GetCurrentToken() ;
					if (tokTimes.IsKeyword() && tokTimes.GetKeyword()==CCobolKeywordList.TIMES)
					{
						tokTimes = GetNext();
					}
					if(tokTimes.IsKeyword() && tokTimes.GetKeyword()==CCobolKeywordList.TO)
					{	// OCCURS DEPENDING ON statement
						CBaseToken tokTo = GetNext() ;
						if (tokTo.GetType() == CTokenType.NUMBER)
						{
							occurs = ReadTerminal() ;
							CBaseToken tokDep = GetCurrentToken() ;
							if (tokDep.GetKeyword() == CCobolKeywordList.TIMES)
							{
								tokDep = GetNext() ;
							}
						}
						else 
						{
							return false ;
						}
					}
					boolean isdone2 = false ;
					while (!isdone2)
					{
						CBaseToken tokOpt = GetCurrentToken() ;
						if (tokOpt.GetType() == CTokenType.COMMA)
						{
							tokOpt = GetNext() ;
						}
						if (tokOpt.GetKeyword() == CCobolKeywordList.DEPENDING)
						{
							tokOpt = GetNext() ;
							if (tokOpt.GetKeyword() == CCobolKeywordList.ON)
							{
								tokOpt = GetNext();
							}
							if (tokOpt.GetType() != CTokenType.IDENTIFIER)
							{
								return false ;
							}
							occursDepending = ReadIdentifier() ;
						}
						else if (tokOpt.GetKeyword() == CCobolKeywordList.INDEXED)
						{
							CBaseToken tokBy = GetNext();
							if (tokBy.GetKeyword() == CCobolKeywordList.BY)
							{
								tokBy = GetNext();
							}
							while (tokBy.GetType() == CTokenType.IDENTIFIER)
							{
								occursIndexedBy.add(ReadIdentifier()) ;
								tokBy = GetCurrentToken();
							}
						}
						else if (tokOpt.GetKeyword() == CCobolKeywordList.ASCENDING || tokOpt.GetKeyword() == CCobolKeywordList.DESCENDING)
						{
							if (tokOpt.GetKeyword() == CCobolKeywordList.ASCENDING)
							{
								istableSortedAscending = true ;
							}
							else
							{
								istableSortedAscending = false ;
							}
							tokOpt = GetNext() ;
							if (tokOpt.GetKeyword() == CCobolKeywordList.KEY)
							{
								tokOpt = GetNext();
							}
							if (tokOpt.GetKeyword() == CCobolKeywordList.IS)
							{
								tokOpt = GetNext();
							}
							tableSortKey = new Vector<CIdentifier>() ;
							CIdentifier tableSortKey ;
							tableSortKey = ReadIdentifier() ;
							while (tableSortKey != null)
							{
								this.tableSortKey.add(tableSortKey) ;
								tableSortKey = ReadIdentifier() ;
							}
						}
						else
						{
							isdone2 = true ;
						}
					}
				}
				else
				{
					Transcoder.logError(getLine(), "Expecting int value after occurs instead of : " + tokOccurs.toString());
					return false ;
				}
			}
			if (!isnext)
			{
				isdone = true ;
			}
		}
		return true ;
	}

	protected String ReadPicType()
	{
		String cs = "" ;
		boolean isdone = false ;
		while (!isdone)
		{
			CBaseToken tok = GetCurrentToken();
			if (tok.GetType() == CTokenType.NUMBER || tok.GetType() == CTokenType.IDENTIFIER)
			{
				cs += tok.GetValue() ;
			}
			else if (tok.GetType() == CTokenType.LEFT_BRACKET)
			{
				cs += "(" ;
			}
			else if (tok.GetType() == CTokenType.RIGHT_BRACKET)
			{
				cs += ")" ;
			}
			else if (tok.GetType() == CTokenType.COMMA)
			{
				cs += "," ;
			}
			else if (tok.GetType() == CTokenType.DOLLAR)
			{
				cs += "$" ;
			}
			else if (tok.GetType() == CTokenType.PLUS)
			{
				cs += "+" ;
			}
			else if (tok.GetType() == CTokenType.MINUS)
			{
				cs += "-" ;
			}
			else if (tok.GetType() == CTokenType.DOT)
			{ // depending on following token
				CBaseToken tokNext = GetNext() ;
				if (tokNext == null || tokNext.isisNewLine)
				{
					return cs ;
				}
				else
				{
					cs += "." ;
					continue ;
				}
			}
			else if (tok.GetType() == CTokenType.KEYWORD)
			{
				isdone = true ;
			}
			else
			{
				Transcoder.logError(getLine(), "Unexpecting token in PIC type : " + tok.GetValue());
				return "" ; 
			}
			if (!isdone)
			{
				GetNext() ;
			}		
		}
		return cs ;
	}
	protected boolean isedited = false ;
	protected boolean ParsePicItSelf()
	{
		CBaseToken tokType = GetNext() ; // consume PIC token, expecting S9 / 9 / X / XX ...
		String csPicType = ReadPicType() ;
		byte[] tab = csPicType.getBytes() ;
		int nCurrentChar = 0 ;
		char repeatPattern = 0 ;
		
		while (nCurrentChar<tab.length)
		{
			char c = (char)tab[nCurrentChar] ;
			if (type == null)
			{
				if (c == 'X')
				{
					type = CWorkingPicType.STRING ;
					length = 1 ;
				}
				else if (c == 'S')
				{
					type = CWorkingPicType.SIGNED;
				}
				else if (c == 'V')
				{
					type = CWorkingPicType.DECIMAL ; 
					length = 0 ;
				}
				else if (c == '9')
				{
					type = CWorkingPicType.NUMBER;
					length = 1 ;
					repeatPattern = '9' ;
				}
				else if (c == 'Z')
				{
					type = CWorkingPicType.NUMBER ;
					length = 1 ;
					isedited = true ;
					repeatPattern = 'Z' ;
				}
				else if (c == 'B')
				{
					type = CWorkingPicType.NUMBER ;
					length = 1 ;
					isedited = true ;
					repeatPattern = 'B' ;
				}
				else if (c == '+')
				{
					type = CWorkingPicType.NUMBER ;
					length = 0 ;
					isedited = true ;
					repeatPattern = '+' ;
				}
				else if (c == '-')
				{
					type = CWorkingPicType.NUMBER ;
					length = 1 ;
					isedited = true ;
					repeatPattern = '-' ;
				}
				else if (c == '$')
				{
					type = CWorkingPicType.NUMBER ;
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting character in Pic Type : " + c);
					return false ;
				}
				format += c ;
			}
			else
			{
				if (c == 'X' && type == CWorkingPicType.STRING)
				{
					length ++ ;
					format += c ;
				}
				else if (c == '9' && (type == CWorkingPicType.NUMBER || type == CWorkingPicType.SIGNED || type == CWorkingPicType.EDITED))
				{
					length ++ ;
					format += c ;
					repeatPattern = '9' ;
				}
				else if (c == 'B') // ????
				{
					length ++ ;
					format += c ;
					isedited = true ;
					repeatPattern = 'B' ;
				}
				else if (c == 'Z' && type == CWorkingPicType.EDITED)
				{
					length ++ ;
					format += c ;
					isedited = true ;
					repeatPattern = 'Z' ;
				}
				else if (c == 'Z' && type == CWorkingPicType.NUMBER)
				{
					length ++ ;
					format += c ;
					isedited = true ;
					repeatPattern = 'Z' ;
				}
				else if (c == '-' && (type == CWorkingPicType.NUMBER || type == CWorkingPicType.DECIMAL))
				{
					length ++ ;
					format += c ;
					isedited = true ;
				}
				else if (c == '+' && type == CWorkingPicType.NUMBER)
				{
					length ++ ;
					format += c ;
					isedited = true ;
				}
				else if (c == 'V' && type == CWorkingPicType.NUMBER)
				{
					type = CWorkingPicType.DECIMAL ; 
					format += c ;
				}
				else if (c == 'V' && type == CWorkingPicType.SIGNED)
				{
					type = CWorkingPicType.SIGNED_DECIMAL ; 
					format += c ;
				}
				else if (c == '9' && (type == CWorkingPicType.DECIMAL || type == CWorkingPicType.SIGNED_DECIMAL))
				{
					decimal ++ ;
					format += c ;
					repeatPattern = '9' ;
				}
				else if (c == '.' || c == ',' || c == '$')
				{
					isedited = true ;
					format += c ;
				}
				else if (c == '(')
				{
					nCurrentChar ++ ;
					c = (char)tab[nCurrentChar] ;
					String number = "" ;
					while (c >= '0' && c <= '9')
					{
						number += c ;
						nCurrentChar ++ ;
						c = (char)tab[nCurrentChar] ;
					}
					if (c != ')')
					{
						Transcoder.logError(getLine(), "Expecting ')' after number");
						return false ;
					}
					int n = Integer.parseInt(number) ;
					if (n == 0)
					{
						Transcoder.logError(getLine(), "Unparsed number '" + number +"'");
						return false ;
					}
					char completc = ' ' ;
					if (type ==CWorkingPicType.STRING)
					{
						length *= n ;
						completc = 'X' ;
					}
					else if (type == CWorkingPicType.NUMBER  && isedited)
					{
						length *= n ;
						completc = repeatPattern;
					}
					else if (type == CWorkingPicType.NUMBER || type == CWorkingPicType.SIGNED)
					{
						length *= n ;
						completc = '9' ;
					}
					else if(type == CWorkingPicType.DECIMAL || type == CWorkingPicType.SIGNED_DECIMAL)
					{
						decimal *= n;
						completc = '9' ;
					}
					else
					{
						Transcoder.logError(getLine(), "Unexpecting situation");
						return false ;
					}
					for (int i=1; i<n; i++)
					{
						format += completc ;
					}
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting character : " + c) ;
					return false ;					
				}
			}
			nCurrentChar ++ ;
		}
		return true ;
	}			
	
	protected boolean ParsePicBrackets()
	{
		CBaseToken tokBra = GetCurrentToken();
		if (tokBra.GetType() == CTokenType.LEFT_BRACKET)
		{
			CBaseToken tokRep = GetNext() ; // consume '(', expecting an int
			int rep = tokRep.GetIntValue();
			if (rep > 0)
			{
				CBaseToken tokBraOff = GetNext();
				if (tokBraOff.GetType() == CTokenType.RIGHT_BRACKET)
				{
					length = length * rep ;
					GetNext() ;	// consume ')'
					return true ;
				}
				else
				{
					Transcoder.logError(getLine(), "Unexpecting token after pic length : " + tokBraOff.toString()) ;
					return false ;
				}
			}
			else
			{
				Transcoder.logError(getLine(), "Invalid parameter for PIC length : " + tokRep.toString());
				return false ;
			}
			
		}
		return true ;
	}
	
	protected boolean ParseContent()
	{
		boolean isdone = false ;
		while (!isdone)
		{
			CBaseToken tokEntry = GetCurrentToken();
			if (tokEntry == null)
			{
				return true ;
			}
			if (tokEntry.GetType()==CTokenType.NUMBER)
			{
				int level = tokEntry.GetIntValue();
				if (level == 88)
				{
					CCobolElement eEntry = new CWorkingValueEntry(tokEntry.getLine());
					if (!Parse(eEntry))
					{
						return false ;
					}
					AddChild(eEntry) ;
				}
				else if (level > entryLevel && level <= 49)
				{
					CCobolElement eEntry = new CWorkingEntry(tokEntry.getLine()) ;
					if (!Parse(eEntry))
					{
						return false ;
					}
					AddChild(eEntry) ;
				}
				else
				{
					isdone = true ; // this entry is a sub-entry of one of our parents
				}
			}
			else
			{
				isdone = true ;	// this token is not parsed by this function, go back to caller
			}
		}
		return true ;
	}

	
	/* (non-Javadoc)
	 * @see parser.CLanguageElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element eItem ;
		if (entryType == CWorkingEntryType.STRUCTURE)
		{
			eItem = root.createElement("Item") ;
			eItem.setAttribute("Level", formalLevel) ;
		}
		else if (entryType == CWorkingEntryType.VARIABLE)
		{
			eItem = root.createElement("Variable") ; 
		}
		else
		{
			return null ;
		}
		if (type != null)
		{
			eItem.setAttribute("Type", type.text) ;
		}
		eItem.setAttribute("Name", name) ;
		if (redefines != null)
		{
			Element eRedef = root.createElement("Redefines") ;
			eItem.appendChild(eRedef) ;
			redefines.ExportTo(eRedef, root) ;
		}
		if (length>1)
		{
			eItem.setAttribute("Length", String.valueOf(length)) ;
		}
		if (value != null)
		{
			value.ExportTo(eItem, root) ;
		}
		if (!comp.equals(""))
		{
			eItem.setAttribute("Comp", comp);
		}
		if (occurs != null)
		{
			Element eOccurs = root.createElement("Occurs") ;
			eItem.appendChild(eOccurs) ;
			occurs.ExportTo(eOccurs, root) ;
			for (CIdentifier indexedBy : occursIndexedBy)
			{
				Element eIndexed = root.createElement("IndexedBy") ;
				eOccurs.appendChild(eIndexed) ;
				indexedBy.ExportTo(eIndexed, root) ;
			}
		}
		if (sync)
		{
			eItem.setAttribute("Sync", sync?"yes":"no");
		}
		if (!format.equals(""))
		{
			eItem.setAttribute("Format", format);
		}
		return eItem;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#DoCustomSemanticAnalysis(semantic.CBaseSemanticEntity, semantic.CBaseSemanticEntityFactory)
	 */
	protected CBaseLanguageEntity DoCustomSemanticAnalysis(CBaseLanguageEntity parent, CBaseEntityFactory factory)
	{
		CEntityAttribute eAtt = null ;
		if (entryType == CWorkingEntryType.STRUCTURE)
		{
			CEntityStructure eStruct = null ;
			if (occurs != null)
			{
				eStruct = factory.NewEntityStructure(getLine(), name, formalLevel) ;
				eAtt = eStruct ;
				CDataEntity eSize = occurs.GetDataEntity(getLine(), factory);
				if (occursDepending != null)
				{
					CDataEntity eDep = occursDepending.GetDataReference(getLine(), factory) ;
					CEntityValueReference eRef = factory.NewEntityValueReference(eDep) ;
					eStruct.SetTableSizeDepending(eSize, eRef) ;
					eDep.RegisterReadReference(eRef) ;
				}
				else
				{
					eStruct.SetTableSize(eSize) ;
				}
				
				for (CIdentifier indexedBy : occursIndexedBy)
				{
					CEntityIndex index = factory.NewEntityIndex(indexedBy.GetName()) ;
					eStruct.setOccursIndex(index) ;
					parent.AddChild(index) ;
				}
			}
			if (redefines != null)
			{
				CDataEntity e = redefines.GetDataReference(getLine(), factory, parent) ;
				if (e != null)
				{
					if (e.GetDataType() == CDataEntity.CDataEntityType.VIRTUAL_FORM)
					{
						CIgnoredEntity ign = factory.NewIgnoreEntity(getLine(), name) ;
						bAnalysisDoneForChildren = true ;// this is redefining a ignored structure => ignore children
						return ign ;
					}
					else if (e.GetDataType() == CDataEntity.CDataEntityType.FORM)
					{
						CEntityResourceForm form = (CEntityResourceForm)e ;
						//CEntityFormAccessor eAcc = (CEntityFormAccessor)e ; 
						CBaseLanguageEntity ebase = DoSemanticAnalysisForMapRedefine(form, factory);
						if (parent != null)
							parent.AddChild(ebase);
						return ebase ;
					}
					else
					{
						if (eStruct == null)
						{
							eStruct = factory.NewEntityStructure(getLine(), name, formalLevel) ;
						}
						eStruct.SetRedefine(e) ;
					}
				}
				else
				{
					Transcoder.logError(getLine(), "Identifier not found : " + redefines.GetName());
				}
			}
			if (eStruct == null)
			{
				eStruct = factory.NewEntityStructure(getLine(), name, formalLevel) ;
			}
			eAtt = eStruct ;
		}
		else if (entryType == CWorkingEntryType.VARIABLE)
		{
			eAtt = factory.NewEntityAttribute(getLine(), name) ;
			factory.programCatalog.RegisterAttribute(eAtt) ;
		}
		
		eAtt.SetSignSeparateType(issignSeparateType) ;
		eAtt.SetJustifiedRight(isjustifiedRight) ;
		eAtt.SetBlankWhenZero(isblankWhenZero) ;
		SetType(eAtt) ;
		eAtt.SetSync(sync) ;
		if (parent!=null)
		{
			CBaseLanguageEntity par = parent.FindLastEntityAvailableForLevel(eAtt.GetInternalLevel()) ;
			if (par != null && par != parent)
			{
				par.AddChild(eAtt) ;
			}
			else
			{
				parent.AddChild(eAtt) ;
			}
		}
		if (value != null)
		{
			if (value.GetValue().equals(CCobolConstantList.SPACE.name) || value.GetValue().equals(CCobolConstantList.SPACES.name))
			{
				eAtt.SetInitialValueSpaces() ;
			}
			else if (value.GetValue().equals(CCobolConstantList.ZERO.name) || value.GetValue().equals(CCobolConstantList.ZEROS.name)|| value.GetValue().equals(CCobolConstantList.ZEROES.name))
			{
				eAtt.SetInitialValueZeros() ;
			}
			else if (value.GetValue().equals(CCobolConstantList.LOW_VALUE.name) || value.GetValue().equals(CCobolConstantList.LOW_VALUES.name))
			{
				eAtt.SetInitialLowValue() ;
			}
			else if (value.GetValue().equals(CCobolConstantList.HIGH_VALUE.name) || value.GetValue().equals(CCobolConstantList.HIGH_VALUES.name))
			{
				eAtt.SetInitialHighValue() ;
			}
			else
			{
				CDataEntity eInitial = value.GetDataEntity(getLine(), factory);
				if (eInitial != null)
				{
					if (isfillAll)
					{
						eAtt.SetInitialValueAll(eInitial) ;
					}
					else
					{
						eAtt.SetInitialValue(eInitial) ;
					}
					eInitial.RegisterReadReference(eAtt) ;
					factory.programCatalog.RegisterInitializedStructure(eAtt) ;
				}
				else
				{
					Transcoder.logError(getLine(), "Missing semantic for initial value : "+value.GetValue());
				}
			}
		}
		eAtt.SetComp(comp) ;
		return eAtt;
		
	}
	
	/**
	 * @param att
	 */
	private void SetType(ITypableEntity eAtt)
	{
		if (isedited)
		{
			eAtt.SetTypeEdited(format) ;
		}
		else if (type == CWorkingPicType.STRING)
		{
			eAtt.SetTypeString(length) ;
		}
		else if (type == CWorkingPicType.NUMBER || type == CWorkingPicType.DECIMAL)
		{
			eAtt.SetTypeNum(length, decimal) ;
		}
		else if (type == CWorkingPicType.SIGNED || type == CWorkingPicType.SIGNED_DECIMAL)
		{
			eAtt.SetTypeSigned(length, decimal) ;
		}
		else if (type != null)
		{
			Transcoder.logError(getLine(), "Not managed type");
		}
	}

	protected CBaseLanguageEntity DoSemanticAnalysisForMapRedefine(CEntityResourceForm eForm, CBaseEntityFactory factory)
	{
		boolean issaveMap = eForm.IsSaveCopy() ;
		CEntityResourceForm.CFieldRedefineStructure listfields;
		CEntityResourceForm map = factory.programCatalog.GetAssociatedMap(eForm) ;
		if (issaveMap)
		{
			listfields = map.GetRedefineStructure() ;
		}
		else
		{
			listfields = eForm.GetRedefineStructure() ;
		}
		CEntityValueReference ref = factory.NewEntityValueReference(eForm) ;
		eForm.RegisterReadReference(ref) ;
		CEntityFormRedefine eRedef = factory.NewEntityFormRedefine(getLine(), name, ref, issaveMap);
		eForm.StartFieldAnalyse();
		DoSemanticAnalysisForMapRedefineForChildren(eForm, factory, eRedef, issaveMap, listfields);
		if (issaveMap)
		{
			factory.programCatalog.RegisterSaveMap(eRedef, map) ;
		}
		else
		{
			factory.programCatalog.RegisterMap(eRedef) ;
		}
		return eRedef ;
	}
	protected int DoSemanticAnalysisForMapRedefineForChildren(CEntityResourceForm eForm, CBaseEntityFactory factory, CBaseLanguageEntity eParent, boolean bSaveMap, CEntityResourceForm.CFieldRedefineStructure structure)
	{
		CEntityResourceForm.CFieldRedefineDescription curRedefineStructure = structure.Current() ;
			// this structure is used to link save-fields with their symbolic-field
		ListIterator i = children.listIterator() ;
		CWorkingEntry le = null ;
		CEntityFieldRedefine eFieldRedef = null ;  // current field
		CEntityFieldRedefine eLastFieldRedef = null ;  // current field
		le = GetNext(i);
		int nbFieldConsumed = 0 ; // total number of field consumed by children ; used for iteration
		Hashtable<CBaseLanguageEntity, CEntityResourceForm.CFormByteConsumingState> tabPassedStates = new Hashtable<CBaseLanguageEntity, CEntityResourceForm.CFormByteConsumingState>() ;
		while (le != null)
		{
			int nElementSize = le.GetByteLength() ;
			int nRemainingSizeInField = eForm.GetRemainingBytesInCurrentField();
			int nbFields = 0 ;
			if (nElementSize <= nRemainingSizeInField // current entry is part of a field
					&& le.occurs == null   // current entry is not a group
					&& (le.children.size() == 0   // current entry is not a group
							|| eForm.getCurrentPositionInField()>0)) // but groups are allowed for DATA attribute field
			{ // current entry is an attribute of a field, or a field itself
				CIdentifier idRedefine = le.redefines ;
				if (idRedefine != null)
				{ // current attribute is a redefine of an existing attribute
					if (factory.programCatalog.IsExistingDataEntity(idRedefine.GetName(), idRedefine.GetMemberOf()))
					{
						CDataEntity e = idRedefine.GetDataReference(getLine(), factory) ;
						if (le.name.equals("") && le.children.size()==1)
						{
							le = (CWorkingEntry)le.children.get(0) ;
						}
						if (e == null)
						{
							Transcoder.logError(le.getLine(), "Error during MAP REDEFINE");
						}
						else if (e.GetDataType() == CDataEntity.CDataEntityType.FIELD)
						{
//							CBaseTranscoder.ms_logger.info("INFO : Field redefined, line" + le.getLine()) ;
							le.DoCustomSemanticAnalysis(eParent, factory) ;
						}
						else if (e.GetDataType() == CDataEntity.CDataEntityType.FIELD_ATTRIBUTE)
						{
							factory.programCatalog.RegisterDataEntity(le.name, e) ;
						}
						else
						{
							Transcoder.logError(le.getLine(), "Unmanaged REDEFINE inside MAP REDEFINE");
							e.GetDataType() ;
						}
					}
					else
					{
						if (idRedefine.GetName().endsWith("F") || idRedefine.GetName().endsWith("A"))
						{
							CEntityFieldAttribute eCol = factory.NewEntityFieldAttribute(le.getLine(), le.name, eFieldRedef) ;
						}
						else
						{
							Transcoder.logError(le.getLine(), "Unmanaged REDEFINE inside MAP REDEFINE");
						}
					}
				}
				else if (le.name.equals("") && nElementSize < nRemainingSizeInField && eFieldRedef==null)
				{
					nbFields = eForm.ConsumeFieldsAsBytes(le.length);
				}
				else if (le.name.equals("") && nElementSize == nRemainingSizeInField && eFieldRedef==null)
				{
					nbFields = eForm.ConsumeFieldsAsBytes(nElementSize); // must be 1
					if (nbFields != -1)
					{ // -1 means the first 12 bytes => bypass
						if (le.children.size()>0)
						{
//							CBaseTranscoder.ms_logger.info("INFO : Data field splitted into sub-fields, line" + le.getLine()) ;
							String name = le.name ;
							if (name.equals(""))
							{
								CEntityResourceField curF = eForm.GetCurrentRedefiningField() ;
								name = curF.GetName() + "$edit" ;
							}
							CEntityFieldRedefine eSkip = factory.NewEntityFieldRedefine(le.getLine(), name, le.formalLevel); 
							eParent.AddChild(eSkip) ;
							le.DoSemanticAnalysisForChildren(eSkip, factory) ;
							if (curRedefineStructure.field != null)
							{
								if (curRedefineStructure.size != 1)
								{
									Transcoder.logError(le.getLine(), "Unexpecting situation while analysing MAP REDEFINE");
									throw new NacaTransAssertException("ERROR : unexpected situation while analysing MAP REDEFINE, line "+le.getLine()) ; // ASSERT
								} 
							}
							else
							{
								curRedefineStructure.field = eSkip ;
								curRedefineStructure.size = nbFields ;
								curRedefineStructure.type = curRedefineStructure.SKIP ;
							}
						}
						else
						{
							CEntitySkipFields eSkip = factory.NewEntityWorkingSkipField(le.getLine(), le.name, 1, le.formalLevel); 
							eParent.AddChild(eSkip) ;
							if (curRedefineStructure.field != null)
							{
								if (curRedefineStructure.size != 1)
								{
									Transcoder.logError(le.getLine(), "Unexpecting situation while analysing MAP REDEFINE");
									throw new NacaTransAssertException("ERROR : unexpected situation while analysing MAP REDEFINE, line "+le.getLine()) ; // ASSERT
								} 
							}
							else
							{
								curRedefineStructure.field = eSkip ;
								curRedefineStructure.size = nbFields ;
								curRedefineStructure.type = curRedefineStructure.SKIP ;
							}
						}
						curRedefineStructure = structure.Next() ;
						eFieldRedef = null ;
						eLastFieldRedef = null ;
						nbFieldConsumed ++ ;
					}
				}
				else
				{ // not redefine : consume bytes
					if (eForm.getCurrentPositionInField()>6 && nElementSize<nRemainingSizeInField)
					{  // the data field is cut into subfields but with no parent explicit : we must create such a parent for all subfields of the data field
//						CBaseTranscoder.ms_logger.info("INFO : Data field splitted into sub-fields, line" + le.getLine()) ;
						CEntityResourceField field = eForm.GetCurrentRedefiningField() ;
						eFieldRedef = factory.NewEntityFieldRedefine(le.getLine(), field.GetName()+"$edit", le.formalLevel) ;
						eParent.AddChild(eFieldRedef) ;
						int nTotalSize = 0 ;
						while (nTotalSize < nRemainingSizeInField && le != null)
						{
							int level = new Integer(le.formalLevel).intValue();
							le.formalLevel = new Integer(level + 1).toString(); 
							le.DoCustomSemanticAnalysis(eFieldRedef, factory) ;
							nTotalSize += le.GetByteLength() ;
							if (nTotalSize < nRemainingSizeInField)
							{
								le = GetNext(i);
							}
						}
						nbFields = eForm.ConsumeFieldsAsBytes(nTotalSize) ;
						factory.programCatalog.RegisterFieldRedefine(eFieldRedef) ;
						if (bSaveMap)
						{
							//factory.programCatalog.RegisterSaveField(eFieldRedef, curRedefineStructure.field) ;
						}
						else
						{
							//factory.programCatalog.RegisterSymbolicField(eFieldRedef) ;
							curRedefineStructure.field = eFieldRedef ;
							curRedefineStructure.type = curRedefineStructure.FIELD ;
							curRedefineStructure.size = nbFields ; //eField.GetByteLength() ;
							curRedefineStructure.name = eFieldRedef.GetName() ;
						}
					}
					else
					{
						eFieldRedef = CheckRadical(eFieldRedef, le, eParent, factory, curRedefineStructure, bSaveMap) ;
						CreateAttributeForCurrentPositionInField(le, eForm, eFieldRedef, factory) ;
						if (le.children.size()>0)
						{
							if (le.children.size()==1 && eForm.getCurrentPositionInField()<7)
							{
								CWorkingEntry child = (CWorkingEntry)le.children.get(0);
								CreateAttributeForCurrentPositionInField(child, eForm, eFieldRedef, factory) ;
							}
							else
							{
//								CBaseTranscoder.ms_logger.info("INFO : Data field splitted into sub-fields, line" + le.getLine()) ;
								le.DoSemanticAnalysisForChildren(eFieldRedef, factory) ;
							}
						}
						nbFields = eForm.ConsumeFieldsAsBytes(nElementSize) ;
					}
					if (nbFields == 1)
					{
						curRedefineStructure = structure.Next() ;
						eLastFieldRedef = eFieldRedef ;
						eFieldRedef = null ;
						nbFieldConsumed ++ ;
					}
				}
			}
			else if (le.children.size()>0)
			{ // current entry is a structure wrapping several fields
				if (le.redefines != null)
				{
					CDataEntity eRedef = le.redefines.GetDataReference(getLine(), factory) ;
					if (eRedef.GetParent() == eParent && eRedef.HasChildren())
					{
						CEntityResourceForm.CFormByteConsumingState state_sav = eForm.getCurrentConsumingState();
						CEntityResourceForm.CFormByteConsumingState state = tabPassedStates.get(eRedef) ;
						if (state == null)
						{
							CBaseLanguageEntity entity = le.DoSemanticAnalysis(eParent, factory) ;
						}
						else
						{
							eForm.setCurrentConsumingState(state) ;
							
							CBaseLanguageEntity eData = le.DoCustomSemanticAnalysis(eParent, factory) ;
							int n = le.DoSemanticAnalysisForMapRedefineForChildren(eForm, factory, eData, bSaveMap, structure) ;
							
							eForm.setCurrentConsumingState(state_sav) ;
						}
					}
					else
					{
//						CBaseTranscoder.ms_logger.info("INFO : redefine, line" + le.getLine()) ;
						CBaseLanguageEntity entity = le.DoSemanticAnalysis(eParent, factory) ;
					}
				}
				else if (le.occurs != null)
				{
					CDataEntity occ = le.occurs.GetDataEntity(getLine(), factory);
					CEntityFieldOccurs eData = factory.NewEntityFieldOccurs(le.getLine(), le.name);
					eData.SetFieldOccurs(le.formalLevel, occ);
					eParent.AddChild(eData);
					CEntityResourceForm.CFormByteConsumingState state = eForm.getCurrentConsumingState();
					tabPassedStates.put(eData, state) ;
					if (curRedefineStructure.field != null)
					{
//						if (!curRedefineStructure.type.equals(curRedefineStructure.OCCURS))
//						{
//							Transcoder.logError("ERROR : unexpected situation while analysing MAP REDEFINE, line "+le.getLine());
//							throw new NacaTransAssertException("ERROR : unexpected situation while analysing MAP REDEFINE, line "+le.getLine()) ;
//						}
					}
					else
					{
						curRedefineStructure.field = eData ;
						curRedefineStructure.name = le.name ;
						curRedefineStructure.type = curRedefineStructure.OCCURS ;
					}
					structure.Next() ;
					nbFieldConsumed += le.DoSemanticAnalysisForMapRedefineForChildren(eForm, factory, eData, bSaveMap, structure) ;
					curRedefineStructure = structure.Current() ;
					//eParent.AddChild(eData) ;
				}
				else 
				{
					if (nElementSize > nRemainingSizeInField)
					{
//						CBaseTranscoder.ms_logger.info("INFO : group of fields, line" + le.getLine()) ;
					}
					CBaseLanguageEntity eData = le.DoCustomSemanticAnalysis(eParent, factory) ;
					CEntityResourceForm.CFormByteConsumingState state = eForm.getCurrentConsumingState();
					tabPassedStates.put(eData, state) ;
					int n = le.DoSemanticAnalysisForMapRedefineForChildren(eForm, factory, eData, bSaveMap, structure) ;
					curRedefineStructure = structure.Current() ;
					nbFieldConsumed += n;
					//eParent.AddChild(eData) ;
				}
			}
			else
			{ // current entry is a skipfield, with or without a name
				nbFields = eForm.ConsumeFieldsAsBytes(le.length);
				if (nbFields>0)
				{
					CEntitySkipFields eSkip = factory.NewEntityWorkingSkipField(le.getLine(), le.name, nbFields, le.formalLevel); 
					eParent.AddChild(eSkip) ;
					nbFieldConsumed += nbFields ;
					if (curRedefineStructure.field != null && bSaveMap)
					{
						int nRemaining = nbFields ;
						while (curRedefineStructure.size>0 && curRedefineStructure.size < nRemaining)
						{
							nRemaining -= curRedefineStructure.size ;
							curRedefineStructure = structure.Next() ;
						}
						if (curRedefineStructure.size != nRemaining)
						{
							Transcoder.logError(le.getLine(), "Unexpecting situation while analysing MAP REDEFINE");
							throw new NacaTransAssertException("ERROR : unexpected situation while analysing MAP REDEFINE, line "+le.getLine()) ; // ASSERT
						} 
					}
					else
					{
						curRedefineStructure.field = eSkip ;
						curRedefineStructure.size = nbFields ;
						curRedefineStructure.type = curRedefineStructure.SKIP ;
					}
					curRedefineStructure = structure.Next() ;		
				}
				else
				{
					le.DoSemanticAnalysis(eParent, factory) ;
				}
			}
			
			le = GetNext(i);
			
		}
			
		bAnalysisDoneForChildren = true ;
		if (occurs != null)
		{
			int n = Integer.parseInt(occurs.GetValue());
			if (n < 2)
			{
				Transcoder.logError(le.getLine(), "Unexpecting situation while analysing MAP REDEFINE");
				return 0 ;
			}
			else
			{
				eForm.ConsumeFields((n-1)*nbFieldConsumed) ;
				nbFieldConsumed *= n ;
			}
		}
		else if (nbFieldConsumed == 1 && eLastFieldRedef != null)
		{
			eLastFieldRedef.csLevel = formalLevel ;
			eLastFieldRedef.SetLine(getLine()) ;
			eParent.GetParent().AddChild(eLastFieldRedef) ;
			eParent.SetParent(null);
			if (eLastFieldRedef.GetName().equals(eParent.GetName()))
			{
				eParent.Rename("") ;
			}
		}
		return nbFieldConsumed ;
	}
	 
	 /**
	 * @param fieldRedef
	 * @param le
	 * @param eParent
	 * @param factory
	 * @param saveMap
	 * @param curRedefineStructure
	 * @return
	 */
	private CEntityFieldRedefine CheckRadical(CEntityFieldRedefine fieldRedef, CWorkingEntry le, CBaseLanguageEntity eParent, CBaseEntityFactory factory, CFieldRedefineDescription curRedefineStructure, boolean bSaveMap)
	{
		String csRadical = "" ;
		if (!le.name.equals(""))
		{
			csRadical = le.name.substring(0, le.name.length()-1) ;
		}
		if (fieldRedef == null)
		{
			String name = csRadical ;
			int n=1 ;
			while (factory.programCatalog.IsExistingFieldRedefine(name))
			{
				name = csRadical +"$" + n ;
				n++ ;
			}
			if (factory.programCatalog.IsExistingDataEntity(name, ""))
			{
				name += "$edit" ;
			}
			fieldRedef = factory.NewEntityFieldRedefine(le.getLine(), name, le.formalLevel);
			eParent.AddChild(fieldRedef);
			factory.programCatalog.RegisterFieldRedefine(fieldRedef) ;
			if (bSaveMap)
			{
				//factory.programCatalog.RegisterSaveField(fieldRedef, curRedefineStructure.field) ;
			}
			else
			{
				//factory.programCatalog.RegisterSymbolicField(fieldRedef) ;
				curRedefineStructure.field = fieldRedef ;
				curRedefineStructure.type = curRedefineStructure.FIELD ;
				curRedefineStructure.size = 1 ; //eField.GetByteLength() ;
				curRedefineStructure.name = name ;
			}
		}
		else if (!fieldRedef.GetName().equals(csRadical) && !csRadical.equals(""))
		{
			String name = csRadical ;
			int n=1 ;
			while (factory.programCatalog.IsExistingFieldRedefine(name))
			{
				name = csRadical +"$" + n ;
				n++ ;
			}
			if (factory.programCatalog.IsExistingDataEntity(name, ""))
			{
				name += "$edit" ;
			}
			fieldRedef.Rename(name) ;
			factory.programCatalog.RegisterFieldRedefine(fieldRedef) ;
			if (bSaveMap)
			{
				//factory.programCatalog.RegisterSaveField(fieldRedef, curRedefineStructure.field) ;
			}
			else
			{
				//factory.programCatalog.RegisterSymbolicField(fieldRedef) ;
			}
		}
		return fieldRedef ;
	}

	/**
	 * @param length
	 * @param currentPositionInField
	 * @param name
	 * @param field
	 * @return
	 */
	private boolean CreateAttributeForCurrentPositionInField(CWorkingEntry le, CEntityResourceForm eForm, CEntityResourceField eField, CBaseEntityFactory factory)
	{
		String name = le.name ;
		int length = le.GetByteLength() ;
		int currentPositionInField = eForm.getCurrentPositionInField() ;
		if (length == 2 && currentPositionInField == 0)
		{
			CEntityFieldLength eLen = factory.NewEntityFieldLengh(le.getLine(), name, eField) ;
//			return eForm.ConsumeFieldsAsBytes(2) ;				
		}
		else if (currentPositionInField == 2 && length == 1)
		{
			CEntityFieldAttribute eCol = factory.NewEntityFieldAttribute(le.getLine(), name, eField) ;
//			return eForm.ConsumeFieldsAsBytes(1) ;				
		}
		else if (currentPositionInField == 3 && length == 1)
		{
			CEntityFieldColor eCol = factory.NewEntityFieldColor(le.getLine(), name, eField) ;
//			return eForm.ConsumeFieldsAsBytes(1) ;				
		}
		else if (currentPositionInField == 4 && length == 1)
		{
			CEntityFieldFlag eCol = factory.NewEntityFieldFlag(le.getLine(), name, eField) ;
//			return eForm.ConsumeFieldsAsBytes(1) ;				
		}
		else if (currentPositionInField == 5 && length == 1)
		{
			CEntityFieldHighlight eCol = factory.NewEntityFieldHighlight(le.getLine(), name, eField) ;
//			return eForm.ConsumeFieldsAsBytes(1) ;				
		}
		else if (currentPositionInField == 6 && length == 1)
		{
			CEntityFieldValidated eCol = factory.NewEntityFieldValidated(le.getLine(), name, eField) ;
//			return eForm.ConsumeFieldsAsBytes(1) ;				
		}
		else
		{
			if (!name.equals(""))
			{
				factory.programCatalog.RegisterDataEntity(name, eField) ;
			}
			if (le.type != CWorkingEntry.CWorkingPicType.STRING && le.type!= null)
			{
//				CBaseTranscoder.ms_logger.info("INFO : Data field typed as "+le.type.text+":"+le.format+", line" + le.getLine()) ;
			}
			le.SetType(eField) ;
			eField.SetRightJustified(le.isjustifiedRight) ;
			eField.SetBlankWhenZero(le.isblankWhenZero) ;
//			return eForm.ConsumeFieldsAsBytes(length);
		}
		return true ;
	}

	private CWorkingEntry GetNext(ListIterator i)
	 {
	 	try
	 	{
			return (CWorkingEntry)i.next() ;
	 	}
	 	catch (NoSuchElementException ee)
	 	{
	 		return null ;
	 	}
	 	catch (ClassCastException e)
	 	{
			return GetNext(i);
	 	}
	 }
	 
	protected int GetByteLength()
	{
		int n = 0 ;
		if (children.size() > 0)
		{
			ListIterator i = children.listIterator() ;
			CWorkingEntry le = null ;
			try
			{	
				le = (CWorkingEntry)i.next() ;
			}
			catch (NoSuchElementException e)
			{
			}
			while (le != null)
			{
				if (le.redefines == null)
				{
					n += le.GetByteLength() ;
				}
				try
				{	
					le = (CWorkingEntry)i.next() ;
				}
				catch (NoSuchElementException ee)
				{
					le = null ;
				}
			}
		}
		else
		{
			if (type == CWorkingPicType.STRING)
			{
				n = length ;
			}
			else if (type == CWorkingPicType.EDITED || isedited)
			{
				n = format.length() ;
			}
			else 
			{	// NUMERIC TYPE
				if (comp.equals(""))
				{
					n = length + decimal ;
				}
				else if (comp.equals("COMP") || comp.equals("COMP4"))
				{
					switch (length)
					{
						case 4:
							n = 2 ;
							break ;
						default:
							n= length / 2 ;
					}
				}
				else if (comp.equals("COMP3"))
				{
					n = length/2 + length%2 ;
				}
				else
				{
					int ndsf = 0 ;
					Transcoder.logError(getLine(), "Unhandled situation : GetByteLength on unmanaged type");
				}
			}
		}
		if (occurs!=null)
		{
			int nocc = Integer.parseInt(occurs.GetValue()) ;
			n *= nocc ;
		}
		return n ;
	}
}
