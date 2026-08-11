/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
/*
 * Created on 13 août 2004
 *
 */
package parser.Cobol.elements;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author sly
 *
 */
public class CGenericBloc extends CBlocElement
{

	/**
	 * @param line
	 */
	public CGenericBloc(String type, int line)
	{
		super(line);
		this.type = type ;
	}

	/* (non-Javadoc)
	 * @see parser.CBaseElement#ExportCustom(org.w3c.dom.Document)
	 */
	protected Element ExportCustom(Document root)
	{
		Element e = root.createElement(type) ;
		return e;
	}

	protected String type = "" ;

	/* (non-Javadoc)
	 * @see parser.elements.CBlocElement#isTopLevelBloc()
	 */
	protected boolean isTopLevelBloc()
	{
		return false;
	}

}
