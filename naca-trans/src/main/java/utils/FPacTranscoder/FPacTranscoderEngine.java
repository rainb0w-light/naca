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
import generate.java.CJavaExporter;
import parser.CParser;
import parser.FPac.CFPacParser;
import parser.FPac.elements.CFPacScript;
import semantic.CBaseEntityFactory;
import semantic.CEntityClass;
import utils.Transcoder;
import utils.TranscoderEngine;
import utils.CGlobalEntityCounter;
import utils.CObjectCatalog;
import utils.CTransApplicationGroup;
import utils.NacaTransAssertException;
import lexer.CBaseLexer;
import lexer.CTokenList;
import lexer.FPac.CFPacLexer;

public class FPacTranscoderEngine extends TranscoderEngine<CFPacScript, CEntityClass>
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
    protected CEntityClass doSemanticAnalysis(
        CParser<CFPacScript> parser,
        String fileName,
        CObjectCatalog cat,
        CTransApplicationGroup grp,
        boolean bResources)
    {
        cat.RegisterNotifHandler(new DefaultFileManager()) ;

        CJavaExporter out = new CJavaExporter(cat.listing, fileName, parser.commentContainer, bResources) ;
        cat.setExporter(out) ;
        CJavaFPacEntityFactory factory = new CJavaFPacEntityFactory(cat, out) ;
        InitCustomCICSEntriesFromRules(factory) ;

        CFPacScript prg = parser.GetRootElement() ;

        prg.setName(FileSystem.getNameWithoutExtension(fileName)) ;

        CEntityClass eSem = prg.DoSemanticAnalysis(factory) ;
        parser.commentContainer.DoSemanticAnalysis(factory) ;
        //DoAlgorythmicAnalysis(cat, factory);

        return eSem ;
    }

    /**
     * FPac root export. The {@code CFPacJavaClass} direct backend is retired: the factory
     * now hands back the pure, target-neutral {@link CEntityClass} (bound to the legacy
     * output controller), and the FPac {@code FPacProgram} class wrapper is owned by the
     * recursive-ST4 contract ({@code recursiveFPacClassEntity}, role {@code FPAC_ROOT}).
     *
     * <p>The body procedure container ({@code CFPacJavaProcedure}) is STILL a direct backend
     * on its own retirement slice ({@code CFPacJavaComment} and {@code CFPacJavaDataSection}
     * are retired: the factory hands back the pure {@link semantic.CEntityComment} /
     * {@link semantic.CEntityDataSection}). A comment renders through the shared
     * {@code semantic.CEntityComment=javaComment} binding under both {@code REFERENCE} and
     * {@code FPAC_REFERENCE}; a whole-tree {@code renderRoot(eSem, FPAC_ROOT)} would still
     * wrongly resolve the remaining shared semantic subclasses through the frozen COBOL
     * reference bindings. Until they are retired, this transitional bridge reproduces the
     * retired {@code CFPacJavaClass.DoExport} wrapper byte-for-byte through the bound legacy
     * output and drives the body with {@link #exportFpacRootChildren} exactly as the deleted
     * backend did: the still-legacy procedure renders by reflection, the retired transparent
     * {@code CEntityDataSection} is flattened in place (its still-legacy FPac file-descriptor
     * children render by reflection), and verbs already retired inside a procedure lower via
     * {@code FPAC_REFERENCE} (the procedure's own bridge). This converges to
     * {@code renderRoot(eSem, JavaTemplateRole.FPAC_ROOT)} once the container backends retire.
     */
    @Override
    protected void exportRoot(CEntityClass eSem, String filename, String csApplication, CTransApplicationGroup grp, boolean bResources)
    {
        String rendered = generate.templates.TemplateLoader.getRecursiveAssembler(
            generate.templates.JavaTemplatePipeline.FPAC)
            .renderRoot(eSem, generate.templates.recursive.JavaTemplateRole.FPAC_ROOT) ;
        String outPath = grp.csOutputPath
            + (csApplication.equals("") ? "" : csApplication + "/")
            + generateOutputFileName(filename) ;
        try
        {
            java.nio.file.Files.writeString(java.nio.file.Path.of(outPath), rendered,
                java.nio.charset.StandardCharsets.ISO_8859_1) ;
        }
        catch (java.io.IOException e)
        {
            throw new NacaTransAssertException(
                "Cannot write FPac ST4 artifact " + outPath + ": " + e.getMessage()) ;
        }
    }

    protected void InitCustomCICSEntriesFromRules(CBaseEntityFactory factory)
    {
//      int nb = rulesManager.getNbRules("ignoreEntity") ;
//      for (int i=0; i<nb; i++)
//      {
//          Tag e = rulesManager.getRule("ignoreEntity", i) ;
//          String name = e.getVal("name") ;
//          factory.NewIgnoreEntity(name) ;
//      }
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
//      nb = rulesManager.getNbRules("keyPressed") ;
//      for (int i=0; i<nb; i++)
//      {
//          Tag e = rulesManager.getRule("keyPressed", i) ;
//          String key = e.getVal("keyName") ;
//          String alias = e.getVal("CICSAlias") ;
//          factory.NewEntityKeyPressed(alias, key) ;
//      }
        nb = rulesManager.getNbRules("routineEmulation") ;
        for (int i=0; i<nb; i++)
        {
            Tag e = rulesManager.getRule("routineEmulation", i) ;
            String name = e.getVal("routine") ;
            String method = e.getVal("method") ;
            factory.programCatalog.RegisterRoutineEmulation(name, method) ;
        }
//
//      nb = rulesManager.getNbRules("NoExportResource") ;
//      for (int i=0; i<nb; i++)
//      {
//          Tag e = rulesManager.getRule("NoExportResource", i) ;
//          String name = e.getVal("program") ;
//          cat.RegisterNotExportingResource(name);
//      }
    }


    @Override
    protected void doLogs(String csInput, String csOutput)
    {
        Transcoder.logDebug("Start transcoding file to "+ csOutput);
    }

    @Override
    protected void doPopulateSpecialActionHandlers(NotificationEngine engine)
    {

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
