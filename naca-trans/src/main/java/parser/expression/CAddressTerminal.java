/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package parser.expression;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import semantic.CBaseEntityFactory;
import semantic.CDataEntity;
import semantic.expression.CEntityAddress;

public class CAddressTerminal extends CTerminal
{

	protected String csAddresse;

	public CAddressTerminal(String add)
	{
		super();
		csAddresse = add ;
	}

	@Override
	public String GetValue()
	{
		return csAddresse ;
	}

	@Override
	public boolean IsReference()
	{
		return false ;
	}

	@Override
	public void ExportTo(Element e, Document root)
	{
		e.setAttribute("Address", csAddresse) ;
	}

	@Override
	public CDataEntity GetDataEntity(int nLine, CBaseEntityFactory factory)
	{
		CEntityAddress ref = factory.NewEntityAddress(csAddresse) ;
		return ref ;
	}

	public boolean IsNumber()
	{
		return false ;
	}
}
