/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils;
/**
 * 
 */

/**
 *
 * @author Pierre-Jean Ditscheid, Consultas SA
 * @version $Id: TranscoderAction.java,v 1.1 2007/06/28 06:19:46 u930bm Exp $
 */
public class TranscoderAction
{
	public static TranscoderAction SyntaxCheck = new TranscoderAction(true, false);
	public static TranscoderAction All = new TranscoderAction(true, true);
	
	private boolean issyntaxCheck = false;
	private boolean isgeneration = false;
	
	private TranscoderAction(boolean bSyntax, boolean bGeneration)
	{
		this.issyntaxCheck = bSyntax;
		this.isgeneration = bGeneration;
	}
	
	public boolean isSyntaxCheck()
	{
		return issyntaxCheck;
	}
	
	public boolean isGeneration()
	{
		return isgeneration;
	}
	
	public String getAsString()
	{
		String cs = "";
		if(issyntaxCheck)
			cs = "SyntaxCheck";
		if(isgeneration)
			cs = " Semantic Generation";
		return cs;			
	}
}
