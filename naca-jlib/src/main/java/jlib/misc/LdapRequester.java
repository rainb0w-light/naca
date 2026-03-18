/*
 * JLib - Publicitas Java library.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package jlib.misc;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: LdapRequester.java,v 1.2 2007/09/07 10:13:53 u930di Exp $
 */

/*
 * Sample call:
 * class ResourceManager
 * {
 * 		...
 * 		// Members variables to initialize by some meanings
 * 		private String csLDAPServer = "" ;		// Main LDAP server address
 * 		private String csLDAPServer2 = "" ;		// 2nd LDAP server address
 * 		private String csLDAPServer3 = "" ;		// 3rd LDAP server address
 *      private String csLDAPDomain = "" ;		// Domain
 * 		private String csLDAPRootOU = "" ;		// Root OU; e.g.: OU=FUTUR_PUBLIGROUPE,DC=Publigroupe,DC=net		
 * 		private String csLDAPGenericUser = "" ;	// LDap connection user
 * 		private String csLDAPGenericPassword = "";// LDap connection password
 * 		...
 * 		public CLDAPRequester getLDAPRequester()
 *		{
 *			return new CLDAPRequester(csLDAPServer, csLDAPServer2, csLDAPServer3, csLDAPDomain, csLDAPRootOU, csLDAPGenericUser, csLDAPGenericPassword) ;
 *		}
 *		...
 *	}
 *
 * Application code:
 * ...
 * LDapRequester ldapReq = resourceManager.getLDAPRequester() ;	
 * String csUserDN = ldapReq.getUserLogin(csUserLdapId, csPassword, bLoginAuto) ;
 * boolean bLogged = !StringUtil.IsEmpty(csUserDN);
 * ...
 * // To get LDap attributs
 * csApplicationCredentials = ldapReq.getAttribute(csUserDN, "extensionAttribute12") ;
 * if (csApplicationCredentials == null)
 * {
 * 		csApplicationCredentials = "" ;
 * }
 * 
 * // To get user complete name 
 * String csSn = ldapReq.getAttribute(csUserDN, "sn") ;
 * if (csSn == null)
 * {
 * 		csUserLdapName = "";
 * }
 * else
 * {
 * 		csUserLdapName = csSn;
 * 		String csGivenName = ldapReq.getAttribute(csUserDN, "givenName") ;
 * 		if (csGivenName != null) 
 * 		{
 * 			csUserLdapName += " " + csGivenName;
 * 		}
 * }
 */

public class LdapRequester
{
	private LdapUtil ldap = null;		// Helper class
	private String csLDAPServer1 = null;	// Main LDAP server address
	private String csLDAPServer2 = null;	// 2nd LDAP server address
	private String csLDAPServer3 = null;	// 3rd LDAP server address
	private String csLDAPDomain = "" ;
	private String csLDAPRootOU = "" ;	// Root OU; e.g.: OU=FUTUR_PUBLIGROUPE,DC=Publigroupe,DC=net
	private String csLDAPGenericUser = "" ;	// LDap generic connection user
	private String csLDAPGenericPassword = "" ;	// LDap generic connection password
	private ThreadSafeCounter cptLdapRequestId = new ThreadSafeCounter(0);

	public LdapRequester(String csServer1, String csServer2, String csServer3, String csDomain, String ou, String csGenericUser, String csGenericPassword)
	{
		csLDAPServer1 = csServer1;
		csLDAPServer2 = csServer2;
		csLDAPServer3 = csServer3;
		csLDAPDomain = csDomain;
		csLDAPRootOU = ou ;
		csLDAPGenericUser = csGenericUser;
		csLDAPGenericPassword = csGenericPassword;
	}
	
	/**
	 * @param csUser: User name
	 * @param csPassword: User password
	 * @return true if ldap user/password exists, false otherwise
	 */
	public boolean validateLogin(String csUser, String csPassword)
	{		
		int nLdapRequestId = cptLdapRequestId.inc();
		int nNbLdapThread = 1;
		if(!StringUtil.isEmpty(csLDAPServer2))
			nNbLdapThread++;
		if(!StringUtil.isEmpty(csLDAPServer3))
			nNbLdapThread++;
		
		ldap = new LdapUtil(nNbLdapThread);
		ldap.addServer(nLdapRequestId, csUser+"@"+csLDAPDomain, csPassword, csLDAPServer1);
		if(!StringUtil.isEmpty(csLDAPServer2))
			ldap.addServer(nLdapRequestId, csUser+"@"+csLDAPDomain, csPassword, csLDAPServer2);
		if(!StringUtil.isEmpty(csLDAPServer3))
			ldap.addServer(nLdapRequestId, csUser+"@"+csLDAPDomain, csPassword, csLDAPServer3);
		ldap.connectOnAnyServers();
		return ldap.isValid() ;
	}

	/**
	 * @param csUser: specific user we want to login
	 * @param csPassword: specific password
	 * @param bUseGenericUser: true if connecting using generic user, not the specific user (csUser / csPassword); in that case csUser/csPassword is ignored   
	 * @return String UserDN; set to null or empty if user did not login correctly.
	 */
	public String getUserLogin(String csUser, String csPassword, boolean bUseGenericUser)
	{
		String csUserLogin = csUser;
		String csPasswordLogin = csPassword;
		if (bUseGenericUser)
		{
			csUserLogin = csLDAPGenericUser;
			csPasswordLogin = csLDAPGenericPassword;
		}
		
		if (!validateLogin(csUserLogin, csPasswordLogin))
		{
			return null ;
		}
		if (ldap == null)
		{
			return null ;
		}
		
		NamingEnumeration enumer = ldap.searchSubtree(csLDAPRootOU, "sAMAccountName="+csUser) ;
		if (enumer.hasMoreElements())
		{
			SearchResult res = (SearchResult)enumer.nextElement() ;
			String name = res.getNameInNamespace() ;
			return name ;
		}
		return null;
	}

	/**
	 * @param csUserDN: User DN whose attribute is serached
	 * @param csAttributName: Attribut name 
	 * @return String, giving the read attribut value. 
	 */	
	public String getAttribute(String csUserDN, String csAttributName)
	{
		if (ldap == null)
		{
			return null ;
		}
		String csAttributsValue = ldap.getOneAttribute(csUserDN, csAttributName) ;
		return csAttributsValue;
	}
}
