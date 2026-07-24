package com.publicitas.naca.cloudnative.service;

import jlib.xml.Tag;
import semantic.CEntityClass;
import utils.BaseEngine;
import utils.CTransApplicationGroup;
import utils.Transcoder;

/**
 * Builds a portable, path-parameterized transpile environment for the ONLINE
 * canonical corpus (ONLINE1 + ONLINM1.bms): an Online group driven by the
 * {@code CobolTranscoder} with a CICS CSD, plus the copybook {@code Includes}
 * group (VTBMSGA / TUAZONE for {@code EXEC SQL INCLUDE}). Paths are supplied by
 * the caller (resolved relative to the working directory in tests), so there are
 * no hardcoded machine-specific paths and no requirement for a permanent
 * environment. Mirrors the engine/group layout of the full NacaTrans
 * configuration (the Windows {@code NacaTransSamples.cfg}), which the Unix
 * config currently lacks.
 *
 * <p>This is the acceptance foundation (T0): it lets tests parse the online
 * corpus and inventory the SQL/CICS/BMS nodes so no recognized source statement
 * can silently disappear.
 */
public final class OnlineCorpusSupport {

    public static final String ONLINE_GROUP_NAME = "Online";
    public static final String INCLUDE_GROUP_NAME = "Includes";

    private OnlineCorpusSupport() {
    }

    /**
     * Build a live {@link Transcoder} for the online corpus.
     *
     * @param cobolDir   directory holding the COBOL sources (e.g. NacaSamples/cobol/)
     * @param includeDir directory holding copybooks (e.g. NacaSamples/cobol/include/)
     * @param csdFile    the CICS CSD file (e.g. NacaSamples/cobol/CICSCSD.txt), or null
     * @param outputDir  directory for generated output / intermediate files
     */
    public static Transcoder build(String cobolDir, String includeDir, String csdFile,
        String outputDir) {
        // Normalize to absolute paths: TranscoderEngine.ReplaceExtensionFileName uses
        // lastIndexOf('.'), so a relative path containing ".." (e.g. ../NacaSamples)
        // would be mis-split into a bogus "..cbl" input name and lexing would fail.
        String cobol = absoluteDir(cobolDir);
        String include = absoluteDir(includeDir);
        String output = absoluteDir(outputDir);
        String interDir = output + "stat";
        String csd = (csdFile == null || csdFile.isEmpty())
            ? null
            : java.nio.file.Path.of(csdFile).toAbsolutePath().normalize().toString();

        String csdElement = (csd == null)
            ? ""
            : "<CSD File=\"" + csd + "\" Output=\"" + output + "TransIDMapping.xml\"/>";

        String configXml =
            "<NacaTrans Log4jConf=\"\">\n"
            + "  <Engines>\n"
            + "    <Transcoder Name=\"CobolTranscoder\""
            + " Class=\"utils.CobolTranscoder.CobolTranscoderEngine\"\n"
            + "        ReferenceGroupName=\"\" ResourceGroupName=\"\" IncludeGroupName=\""
            + INCLUDE_GROUP_NAME + "\">\n"
            + "      " + csdElement + "\n"
            + "    </Transcoder>\n"
            + "    <Transcoder Name=\"IncludeTranscoder\""
            + " Class=\"utils.CobolTranscoder.CobolIncludeTranscoderEngine\"\n"
            + "        ReferenceGroupName=\"\" ResourceGroupName=\"\" IncludeGroupName=\"\"/>\n"
            + "  </Engines>\n"
            + "  <Groups>\n"
            + "    <Group Name=\"" + ONLINE_GROUP_NAME + "\""
            + " InputPath=\"" + cobol + "\""
            + " OutputPath=\"" + output + "\""
            + " InterPath=\"" + interDir + "/\""
            + " Type=\"Online\" Engine=\"CobolTranscoder\"/>\n"
            + "    <Group Name=\"" + INCLUDE_GROUP_NAME + "\""
            + " InputPath=\"" + include + "\""
            + " OutputPath=\"" + output + "include/\""
            + " InterPath=\"" + interDir + "/\""
            + " Type=\"Included\" Engine=\"IncludeTranscoder\"/>\n"
            + "  </Groups>\n"
            + "  <GlobalPaths RuleFilePath=\"\"/>\n"
            + "</NacaTrans>\n";

        Transcoder transcoder = new Transcoder();
        if (!transcoder.Init(Tag.createFromString(configXml))) {
            throw new IllegalStateException(
                "Failed to initialize online corpus transcoder from " + cobol);
        }
        return transcoder;
    }

    /** Absolute, normalized directory path with a trailing separator. */
    private static String absoluteDir(String dir) {
        String normalized = java.nio.file.Path.of(dir).toAbsolutePath().normalize().toString();
        return normalized.endsWith("/") ? normalized : normalized + "/";
    }

    /**
     * Parse one online program through the corpus transcoder and return its
     * semantic root, or {@code null} if it cannot be analyzed.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CEntityClass analyze(Transcoder transcoder, String programName) {
        CTransApplicationGroup group = transcoder.getGroup(ONLINE_GROUP_NAME);
        if (group == null) {
            return null;
        }
        BaseEngine engine = group.getEngine();
        Object analyzed = engine.doAllAnalysis(programName, "", group, false);
        return analyzed instanceof CEntityClass ? (CEntityClass) analyzed : null;
    }
}
