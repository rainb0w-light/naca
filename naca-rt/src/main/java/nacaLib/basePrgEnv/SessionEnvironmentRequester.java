/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package nacaLib.basePrgEnv;

/** Defines the contract for session environment requester. */
public interface SessionEnvironmentRequester
{
    /** Returns the user language id. */
    String getUserLanguageId();
    /** Returns the profit center. */
    String getProfitCenter();
    /** Returns the cmp session. */
    String getCmpSession();
    /** Returns the user id. */
    String getUserId();
    /** Returns the user ldap id. */
    String getUserLdapId();
}
