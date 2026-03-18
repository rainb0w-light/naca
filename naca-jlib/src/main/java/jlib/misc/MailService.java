/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

/**
 * <p>Service de Mail</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Consultas</p>
 * @author <a href=mailto:dbarman@consultas.ch>Barman Dominique</a>
 * @version 1.0
 */

public class MailService 
{
	private String csSMTPServer = null;
	private String csAddressFrom = null;
	private StringArray arrAddressTo = new StringArray() ;
	
	public MailService(String smtp, String from) 
	{
		this.csSMTPServer = smtp;
		this.csAddressFrom = from;
	}

	public Mail createMail() 
	{
		Mail m = new Mail(this);
		m.setFrom(csAddressFrom);
		for (int i=0; i<arrAddressTo.size(); i++)
		{
			String add = arrAddressTo.get(i) ;
			m.addTo(add);
		}
		return m ;
	}

	public String getSMTPServer() 
	{
		return csSMTPServer;
	}
//
//	public String getFrom() 
//	{
//		return csAddressFrom;
//	}

	public void addAddressTo(String add) 
	{
		arrAddressTo.add(add); 
	}
}