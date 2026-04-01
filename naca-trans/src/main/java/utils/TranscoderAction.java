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
	
	private boolean bSyntaxCheck = false;
	private boolean bGeneration = false;
	
	private TranscoderAction(boolean bSyntax, boolean bGeneration)
	{
		this.bSyntaxCheck = bSyntax;
		this.bGeneration = bGeneration;
	}
	
	public boolean isSyntaxCheck()
	{
		return bSyntaxCheck;
	}
	
	public boolean isGeneration()
	{
		return bGeneration;
	}
	
	public String getAsString()
	{
		String cs = "";
		if(bSyntaxCheck)
			cs = "SyntaxCheck";
		if(bGeneration)
			cs = " Semantic Generation";
		return cs;			
	}
}
