/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 3 août 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package generate;

//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import parser.CGlobalCommentContainer;
//
//import javax.xml.parsers.*;
//import javax.xml.transform.*;
//import javax.xml.transform.dom.*;
//import javax.xml.transform.stream.*;
//
//import org.w3c.dom.*;

import semantic.CEntityComment;
import utils.COriginalLisiting;

/**
 * @author sly
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class CBaseLanguageExporter
{
	protected CGlobalCommentContainer commentContainer = null ;
	protected String indentItem = "\t" ;
	protected int indentWidth = 4 ;
	protected int widthBeforeOriginalCode = 80 ;

	private int lastFillerIndex = 0 ;
	public void ResetFillerIndex()
	{
		lastFillerIndex = 0 ;
	}
	public int GetLastFillerIndex()
	{
		lastFillerIndex++ ;
		return lastFillerIndex ;
	}

	public CBaseLanguageExporter(COriginalLisiting cat, CGlobalCommentContainer commCont)
	{
		catalog = cat ;
		commentContainer = commCont ;
	}
	public CBaseLanguageExporter(CBaseLanguageExporter exporter)
	{
		catalog = exporter.catalog ;
		commentContainer = exporter.commentContainer ;
	}
	protected COriginalLisiting catalog = null ;
	
	public void closeOutput()
	{
		String csCurrentLine = "" ;
		nLastOriginalLineWritten ++ ;
		while (csCurrentLine != null)
		{
			if (commentContainer.GetCurrentCommentLine() == nLastOriginalLineWritten)
			{
				CEntityComment com = commentContainer.GetCurrentComment() ;
				com.DoExportComment() ;
			}
			else
			{
				csCurrentLine = catalog.GetOriginalLine(nLastOriginalLineWritten);
				if (csCurrentLine != null)
				{
					int blanksize = widthBeforeOriginalCode - indent.length()*indentWidth;
					char[] c = new char[blanksize] ; // COBOL comments starts on line 60
					Arrays.fill(c, ' ') ;
					String blankline = new String(c) ;
					blankline += "// (" + nLastOriginalLineWritten + ") " + csCurrentLine;
					DoWriteLine(blankline);
				}
			}	
			nLastOriginalLineWritten ++ ;
		}
		doCloseOutput() ;
	}
	protected abstract void doCloseOutput() ;
	public abstract void CloseBracket() ;
	public abstract void OpenBracket() ;
	protected int nLastOriginalLineWritten = 0 ;
	protected abstract void DoWriteLine(String line) ;
	protected void DoWriteComment(String line, int n)
	{
		String fullLine = line ;
		if (nLastOriginalLineWritten < n)  
		{ 
			DisplaySkippedLines(n) ;
			int blanksize = widthBeforeOriginalCode - line.length() - indent.length()*indentWidth;
			if (blanksize > 0)
			{
				char[] c = new char[blanksize] ; // COBOL comments starts on line 80
				Arrays.fill(c, ' ') ;
				String blankline = new String(c) ;
				fullLine = line + blankline + "// (" + n + ")" ;
			}
			else
			{
				fullLine = line + indentItem + "// (" + n + ")";
			}
			nLastOriginalLineWritten = n;
		}
		DoWriteLine(fullLine) ;
	}
	protected void DoWriteLine(String line, int n)
	{
		if (n <= nLastOriginalLineWritten || n==0)
		{
			DoWriteLine(line) ;
		}
		else
		{
			DisplaySkippedLines(n);
			String csOrigLine = "" ;
			if (commentContainer.GetCurrentCommentLine() == n)
			{
				CEntityComment com = commentContainer.GetCurrentComment() ;
				if (!line.equals(""))
					line += indentItem ;
				line += com.ExportReference(n) ;
				//com.DoExportComment() ;
				if (nLastOriginalLineWritten < n)
				{
					csOrigLine = "   (" + n + ") " ;
					String orig = catalog.GetOriginalLine(n) ;
					String comm = com.getOriginalComment() ;
					orig = orig.replace(comm, "").trim() ;
					if (!orig.equals(""))
						csOrigLine += orig ;
					nLastOriginalLineWritten = n;
				}
			}
			else if (nLastOriginalLineWritten < n)  
			{ //nLastOriginalLineWritten == n-1
				csOrigLine = "// (" + n + ") " ;
				if (catalog.GetOriginalLine(n) != null)
				{
					csOrigLine += catalog.GetOriginalLine(n);
				}
				nLastOriginalLineWritten = n;
			}
			if (!line.equals("") || !csOrigLine.equals(""))
			{
				int blanksize = widthBeforeOriginalCode - line.length() - indent.length()*indentWidth;
				String fullline ;  
				if (blanksize > 0)
				{
					char[] c = new char[blanksize] ; // COBOL comments starts on line 80
					Arrays.fill(c, ' ') ;
					String blankline = new String(c) ;
					fullline = line + blankline + csOrigLine ;
				}
				else
				{
					fullline = line + indentItem + csOrigLine ;
				}
				DoWriteLine(fullline) ;
			}
		}
	}
	/**
	 * 
	 */
	private void DisplaySkippedLines(int n)
	{
		for (int i=nLastOriginalLineWritten+1; i<n; i++)
		{
			if (commentContainer.GetCurrentCommentLine() == i)
			{
				CEntityComment com = commentContainer.GetCurrentComment() ;
				com.DoExportComment() ;
			}
			else
			{
				String cs = catalog.GetOriginalLine(i);
				if (cs != null)
				{
					int blanksize = widthBeforeOriginalCode - indent.length()*indentWidth;
					blanksize = Math.max(0, blanksize);
					char[] c = new char[blanksize] ; // COBOL comments starts on line 60
					Arrays.fill(c, ' ') ;
					String blankline = new String(c) ;
					blankline += "// (" + i + ") " + catalog.GetOriginalLine(i);
					DoWriteLine(blankline);
					nLastOriginalLineWritten = i ;
				}
			}				
		}
	}
	public void WriteLine(String line)
	{
		WriteLine(line, nLastOriginalLineWritten) ;
	}
	public void WriteLine(String line, int n)
	{
		if (!currentLine.equals(""))
		{
			DoWriteLine(currentLine, nLastOriginalLineWritten) ;
			currentLine = "" ;
		}
		WriteWord(line, n); //nLastOriginalLineWritten) ;
		WriteEOL(n) ;
	}
	public void WriteComment(String line, int n)
	{
		DoWriteComment(line, n) ;
	}
	public void WriteEOL() 
	{
		WriteEOL(nLastOriginalLineWritten) ;
	}
	public void WriteEOL(int n)
	{
		if (!currentLine.equals(""))
		{
			String line = currentLine ;
			currentLine = "" ;
			DoWriteLine(line, n) ;
		}
	}
	public void WriteWord(String word) 
	{
		WriteWord(word, nLastOriginalLineWritten) ;
	}
	public void WriteWord(String word, int n) 
	{
		if (n > nLastOriginalLineWritten+1)
		{ // more than one original line to be written
			DoWriteLine("", n-1) ;
		}
		
		int pos = word.indexOf("\n") ;
		if (pos != -1)
		{
			String cs1 = word.substring(0, pos) ;
			String cs2 = word.substring(pos+1);
			WriteWord(cs1, n) ;
			WriteWord(cs2, n) ;
			return ;
		}
		
		if (currentLine.length() + word.length() > widthBeforeOriginalCode-indentWidth*indent.length() && word.length()>2 && currentLine.length()>2)
		{
			String l = currentLine ;
			currentLine = "" ;
			DoWriteLine(l, n);
			currentLine = indentItem ;
		}
		currentLine += word ;
	}
	public void WriteLongString(String string, int n) 
	{
		if (n > nLastOriginalLineWritten+1)
		{ // more than one original line to be written
			DoWriteLine("", n-1) ;
		}
				
		String remainString = string ;
		int nSizeRemaining = widthBeforeOriginalCode-indentWidth*indent.length()-currentLine.length() ;
		while (nSizeRemaining > 0 && remainString.length() - nSizeRemaining > 5)
		{
			int nPos = remainString.indexOf(' ', nSizeRemaining);
			if (nPos == -1)
			{
				if (!currentLine.equals(indentItem))
				{
					DoWriteLine(currentLine, n);
				}
				currentLine = indentItem + "\"" + remainString + "\"" ;
				remainString = "" ;
			}
			else
			{
				String item = remainString.substring(0, nPos+1);
				remainString = remainString.substring(nPos+1) ;
				currentLine += "\"" + item + "\"+" ;
				DoWriteLine(currentLine, n);
				currentLine = indentItem ;
			}
			nSizeRemaining = widthBeforeOriginalCode-indentWidth*indent.length()-currentLine.length() ;
		}
		if (!remainString.equals(""))
		{
			currentLine += "\"" + remainString + "\"" ;
		}
	}
	
	public void StartBloc()
	{
		indent += indentItem ;
	}
	public void EndBloc()
	{
		int index = indent.lastIndexOf(indentItem) ;
		indent = indent.substring(0, index) ;
	}
	protected String indent = "" ;
	protected String currentLine = "" ;

	public String FormatIdentifier(String cs)
	{
		cs = cs.replace('-', '_') ;
		cs = cs.replace('#', '$') ;
		return cs ;
	}

	/**
	 * @return
	 */
	protected String GenereTempFileName(String filename)
	{
		File f = new File(filename) ;
		File par = f.getParentFile();
		
		Date date = new Date() ;
		DateFormat format = new SimpleDateFormat("yyMMddHHmmssSSS") ;
		String cs = format.format(date) ;
		return par.getAbsolutePath() + "/~" + cs + "~.tmp" ;
	}
	
	public abstract String getOutputDir() ;

	public abstract boolean isResources() ;
	
	
	// XML exporter
//	public Element CreateRoot(String name)
//	{
//		try
//		{
//			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			m_Document = builder.newDocument();
//			m_Root = m_Document.createElement(name) ;
//			m_Document.appendChild(m_Root) ;
//			return m_Root ;
//		}
//		catch (Exception e)
//		{
//		}
//		return null ;
//	}
//	public Element CreateElementTo(String name, Element eto)
//	{
//		Element e = m_Document.createElement(name) ;
//		eto.appendChild(e) ;
//		return e ;
//	}
//	public Element CreateElement(String name)
//	{
//		return m_Document.createElement(name) ;
//	}
//	public Document GetDocument()
//	{
//		return m_Document;
//	}
//	
//	public void ExportTo(String filename)
//	{
//	   if (m_Document != null)
//	   {
//			try
//			{
//				Source source = new DOMSource(m_Document);
//				FileOutputStream file = new FileOutputStream(filename);
//				StreamResult res = new StreamResult(file) ;
//				Transformer xformer = TransformerFactory.newInstance().newTransformer();
//				xformer.setOutputProperty(OutputKeys.ENCODING, "ISO8859-1");
//				xformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//				xformer.setOutputProperty(OutputKeys.INDENT, "yes");
//				xformer.transform(source, res);
//			}
//			catch (FileNotFoundException e)
//			{
//			}
//			catch (TransformerConfigurationException e)
//			{
//			}
//			catch (TransformerException e)
//			{
//			}
//		}
//	}
//	
	
//	protected Document m_Document = null ;
//	protected Element m_Root = null ; 
	
}
