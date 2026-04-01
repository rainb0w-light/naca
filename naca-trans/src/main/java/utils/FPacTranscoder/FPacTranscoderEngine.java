/*
 * NacaRTTests - Naca Tests for NacaRT support.
 *
 * Copyright (c) 2005, 2006, 2007, 2008 Publicitas SA.
 * Licensed under GPL (GPL-LICENSE.txt) license.
 */
package utils.FPacTranscoder;


import jlib.engine.NotificationEngine;
import jlib.misc.FileSystem;
import jlib.xml.Tag;
import generate.CJavaFPacEntityFactory;
import generate.fpacjava.CFPacJavaClass;
import generate.java.CJavaExporter;
import parser.CParser;
import parser.FPac.CFPacParser;
import parser.FPac.elements.CFPacScript;
import semantic.CBaseEntityFactory;
import utils.Transcoder;
import utils.TranscoderEngine;
import utils.CGlobalEntityCounter;
import utils.CObjectCatalog;
import utils.CTransApplicationGroup;
import lexer.CBaseLexer;
import lexer.CTokenList;
import lexer.FPac.CFPacLexer;

public class FPacTranscoderEngine extends TranscoderEngine<CFPacScript, CFPacJavaClass>
{
	protected @Override CBaseLexer getLexer()
	{
		return new CFPacLexer();
	}

	@Override
	protected CParser<CFPacScript> doParsing(CTokenList lst)
	{
		CParser<CFPacScript> parser = new CFPacParser() ;
		if (parser.StartParsing(lst))
		{
			CGlobalEntityCounter.GetInstance().CountCobolFile();
			return parser ;
		}
		else
		{
			Transcoder.logError("FILEPAC parsing failed") ;
			return null ;
		}
	}

	@Override
	protected CFPacJavaClass doSemanticAnalysis(CParser<CFPacScript> parser, String fileName, CObjectCatalog cat, CTransApplicationGroup grp, boolean bResources)
	{
		cat.RegisterNotifHandler(new DefaultFileManager()) ;
		
		CJavaExporter out = new CJavaExporter(cat.listing, fileName, parser.commentContainer, bResources) ;
		cat.setExporter(out) ;
		CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(cat, out) ;
		InitCustomCICSEntriesFromRules(factory) ;

		CFPacScript prg = parser.GetRootElement() ;
		
		prg.setName(FileSystem.getNameWithoutExtension(fileName)) ;
		
		CFPacJavaClass eSem = prg.DoSemanticAnalysis(factory) ;
		parser.commentContainer.DoSemanticAnalysis(factory) ;
		//DoAlgorythmicAnalysis(cat, factory);
		
		return eSem ;
	}
	protected void InitCustomCICSEntriesFromRules(CBaseEntityFactory factory)
	{
//		int nb = rulesManager.getNbRules("ignoreEntity") ;
//		for (int i=0; i<nb; i++)
//		{
//			Tag e = rulesManager.getRule("ignoreEntity", i) ;
//			String name = e.getVal("name") ;
//			factory.NewIgnoreEntity(name) ;
//		}
		int nb = rulesManager.getNbRules("environmentVariableFPac") ;
		for (int i=0; i<nb; i++)
		{
			Tag e = rulesManager.getRule("environmentVariableFPac", i) ;
			String name = e.getVal("name") ;
			String read = e.getVal("methodeRead") ;
			String write = e.getVal("methodeWrite") ;
			boolean isnumeric = e.getValAsBoolean("Numeric") ;
			factory.NewEntityEnvironmentVariable(name, read, write, isnumeric) ;
		}
//		nb = rulesManager.getNbRules("keyPressed") ;
//		for (int i=0; i<nb; i++)
//		{
//			Tag e = rulesManager.getRule("keyPressed", i) ;
//			String key = e.getVal("keyName") ;
//			String alias = e.getVal("CICSAlias") ;
//			factory.NewEntityKeyPressed(alias, key) ;
//		}
		nb = rulesManager.getNbRules("routineEmulation") ;
		for (int i=0; i<nb; i++)
		{
			Tag e = rulesManager.getRule("routineEmulation", i) ;
			String name = e.getVal("routine") ;
			String method = e.getVal("method") ;
			factory.programCatalog.RegisterRoutineEmulation(name, method) ;
		}
//		
//		nb = rulesManager.getNbRules("NoExportResource") ;
//		for (int i=0; i<nb; i++)
//		{
//			Tag e = rulesManager.getRule("NoExportResource", i) ;
//			String name = e.getVal("program") ;
//			cat.RegisterNotExportingResource(name);
//		}
	}


	@Override
	protected void doLogs(String csInput, String csOutput)
	{
		Transcoder.logDebug("Start transcoding file to "+ csOutput);
	}

	@Override
	protected void doPopulateSpecialActionHandlers(NotificationEngine engine)
	{
		// TODO Auto-generated method stub
		
	}


	/**
	 * @see utils.BaseEngine#CustomInit(jlib.xml.Tag)
	 */
	@Override
	public boolean CustomInit(Tag tagTrans)
	{
		return true ;
	}


	/**
	 * @see utils.TranscoderEngine#generateOutputFileName(java.lang.String)
	 */
	@Override
	protected String generateOutputFileName(String filename)
	{
		return ReplaceExtensionFileName(filename, "java") ;
	}

	/**
	 * @see utils.TranscoderEngine#generateInputFileName(java.lang.String)
	 */
	@Override
	protected String generateInputFileName(String filename)
	{
		return filename ;
	}
}
