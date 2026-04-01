/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import java.util.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;

/**
 * <p>Envoi de mail</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Consultas</p>
 * @author <a href=mailto:dbarman@consultas.ch>Barman Dominique</a>
 * @version 1.0
 */

public class Mail 
{
	private MimeMessage mimeMessage = null;

	private String csFrom = "";
	private Vector<String> toList = new Vector<String>(0);
	private Vector<String> cCList = new Vector<String>(0);
	private Vector<String> bCCList = new Vector<String>(0);
	
	/**
	 * Contructeur du message � envoyer
	 */
	public Mail(MailService mailService) 
	{
	    Properties props = new Properties();
	    props.put("mail.smtp.host", mailService.getSMTPServer());
	    Session session = Session.getInstance(props);
	
	    mimeMessage = new MimeMessage(session);
	}

	/**
	 * Retourne le message
	 */
	public MimeMessage getMessage() 
	{
		return mimeMessage;
	}

	/**
	 * Initialise le sujet du mail
	 */
	public void setSubject(String subject)
	{
		try
		{
			mimeMessage.setSubject(subject);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
  	}

	/**
	 * Initialise l'exp�diteur du mail
	 */
	public void setFrom(String from) 
	{	
		csFrom = from;
  	}

	/**
	 * Initialise le texte du mail
	 */
	public void setText(String text) 
	{
		try
		{
			mimeMessage.setText(text);
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Ajoute un destinataire
	 */
	public void addTo(String to) 
	{
	    add(toList, to);
	}

  /**
   * Ajoute un destinataire en copie
   */
	public void addCc(String cc) 
	{
		add(cCList, cc);
	}

  /**
   * Ajoute un destinataire en copie cach�e
   */
	public void addBcc(String bcc) 
	{
    	add(bCCList, bcc);
	}

	private void add(Vector<String> from, String mail) 
	{
		String  mailList[] = null;
	
	    mailList = mail.split(";");
	    for(int i=0; i < mailList.length; i++) 
	    {
	    	from.add(mailList[i]);
	    }
	}

	/**
	 * Vide les destinataires
	 */
	public void clearTo() 
	{
    	toList.clear();
	}

	/**
	 * Vide les destinataires en copie
	 */
	public void clearCc() 
	{
    	cCList.clear();
  	}

  	/**
  	 * Vide les destinataires en copie cach�e
  	 */
  	public void clearBcc() 
  	{
    	bCCList.clear();
  	}

  	/**
   	 * Envoie le mail
  	 */
  	public void send() 
  	{
	    try 
		{
	    	mimeMessage.setFrom(new InternetAddress(csFrom));
	
			Enumeration entriesMail = toList.elements();
		    while (entriesMail.hasMoreElements()) 
		    {
		        mimeMessage.addRecipient(Message.RecipientType.TO,
		                                 new InternetAddress((String)entriesMail.nextElement()));
		    }
	
		    entriesMail = cCList.elements();
		    while (entriesMail.hasMoreElements()) 
		    {
		        mimeMessage.addRecipient(Message.RecipientType.CC,
		                                 new InternetAddress((String)entriesMail.nextElement()));
		    }
	
			entriesMail = bCCList.elements();
			while (entriesMail.hasMoreElements()) 
			{
				mimeMessage.addRecipient(Message.RecipientType.BCC,
	                                 new InternetAddress((String)entriesMail.nextElement()));
			}
	
			Transport.send(mimeMessage);
	    }
	    catch (Exception ex)
		{
	    	throw new RuntimeException(ex);
	    }
  	}
}