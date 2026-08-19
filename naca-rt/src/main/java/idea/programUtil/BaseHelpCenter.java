/*
 * NacaRT - Naca RunTime for Java Transcoded Cobol programs.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under LGPL (LGPL-LICENSE.txt) license.
 */
package idea.programUtil;

import org.w3c.dom.Document;

import nacaLib.base.CJMapObject;
import nacaLib.basePrgEnv.BaseEnvironment;
import nacaLib.basePrgEnv.CBaseMapFieldLoader;
import nacaLib.misc.CLocalizedTextManager;

/**
 * @author SLY
 *
 */
public abstract class BaseHelpCenter extends CJMapObject
{
    public void setLangCode(String langId)
    {
        csLangId = langId ;
    }
    protected String csLangId = "" ;

    /** Executes the do help operation. */
    public abstract void doHelp(BaseEnvironment env, CBaseMapFieldLoader fieldLoader) ;
    /** Returns the help page. */
    public abstract Document getHelpPage() ;
    protected CLocalizedTextManager localizedTextManager = CLocalizedTextManager.getInstance() ;

    protected String getLocalizedText(String id)
    {
        return localizedTextManager.getLocalizedString(id, csLangId) ;
    }
}
