/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package semantic;

import utils.CObjectCatalog;

/**
 * @author sly
 *
 */
public class CEntityComment extends CBaseLanguageEntity
{

    /**
     * @param name
     * @param cat
     */
    public CEntityComment(int l, CObjectCatalog cat, String comment)
    {
        super(l, "", cat);
        csComment = comment;
    }

    /* (non-Javadoc)
     * @see semantic.CBaseSemanticEntity#RegisterMySelfToCatalog()
     */
    protected void RegisterMySelfToCatalog()
    {
        // NOTHING
    }

    protected String csComment = "" ;
    /** Executes the ignore operation. */
    public boolean ignore()
    {
        return false;
    }

    public String getOriginalComment()
    {
        return csComment.replaceAll("\n", "0x000A").replaceAll("\r", "0x000D") ;
    }

    /**
     * The raw comment text, target-neutral. The Java backend formats it (comment
     * marker, newline escaping, right trim) in its template/renderer; see
     * the Java comment renderer for the exact historical behavior.
     */
    public String getComment()
    {
        return csComment;
    }

}
