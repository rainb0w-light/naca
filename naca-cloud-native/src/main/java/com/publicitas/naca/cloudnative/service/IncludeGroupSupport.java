package com.publicitas.naca.cloudnative.service;

import jlib.xml.Tag;
import semantic.CEntityExternalDataStructure;
import utils.BaseEngine;
import utils.CTransApplicationGroup;
import utils.Transcoder;

/**
 * Builds and caches the COPY/copybook "include group" infrastructure used by the
 * simplified cloud-native transpile pipeline.
 *
 * <p>The semantic {@code COPY <name>} resolution path
 * ({@code CObjectCatalog.GetExternalDataReference -> CGlobalCatalog.GetExternalDataStructure})
 * looks the copybook up through the global catalog's include group: it needs a
 * live {@link Transcoder} exposing a group named {@code Includes} whose engine
 * (the {@code CobolIncludeTranscoderEngine}) parses the copybook from the group's
 * input path. The simplified pipeline used to pass an empty global catalog
 * ({@code new CGlobalCatalog(null, "", "", "")}), so every {@code COPY} resolved
 * to nothing. This class supplies that include group, mirroring the engine/group
 * layout of the full NacaTrans configuration (see test-output/st4/BATCH1/config.xml).
 */
public final class IncludeGroupSupport {

    /** Name of the include group, referenced as the global catalog's include group. */
    public static final String INCLUDE_GROUP_NAME = "Includes";

    private static volatile String copybookDir;
    private static volatile String copybookOutputDir;
    private static volatile Transcoder cachedTranscoder;

    private IncludeGroupSupport() {
    }

    /**
     * Configure the directory holding copybook source files (e.g.
     * {@code NacaSamples/cobol/include}) and the directory where generated
     * copybook classes are written.
     */
    public static synchronized void configure(String copybookDirectory, String outputDirectory) {
        if (copybookDirectory == null || copybookDirectory.isEmpty()) {
            return;
        }
        copybookDir = copybookDirectory.endsWith("/") ? copybookDirectory : copybookDirectory + "/";
        copybookOutputDir = (outputDirectory == null || outputDirectory.isEmpty())
            ? System.getProperty("java.io.tmpdir") + "/naca-includes"
            : outputDirectory;
        cachedTranscoder = null; // force rebuild on next use
    }

    public static boolean isConfigured() {
        return copybookDir != null;
    }

    /**
     * @return the shared include-group {@link Transcoder}, or {@code null} when no
     * copybook directory has been configured (COPY then resolves to nothing, as before).
     */
    public static Transcoder getIncludeTranscoder() {
        if (copybookDir == null) {
            return null;
        }
        Transcoder local = cachedTranscoder;
        if (local == null) {
            synchronized (IncludeGroupSupport.class) {
                local = cachedTranscoder;
                if (local == null) {
                    local = build(copybookDir, copybookOutputDir);
                    cachedTranscoder = local;
                }
            }
        }
        return local;
    }

    /**
     * Transpile a copybook to its Java {@code Copy} class source, by running the
     * include engine's analysis and re-exporting the resulting external data
     * structure into an in-memory string exporter. Returns {@code null} when the
     * copybook cannot be resolved.
     */
    @SuppressWarnings("rawtypes")
    private static CEntityExternalDataStructure resolveCopybook(String copybookName) {
        Transcoder transcoder = getIncludeTranscoder();
        if (transcoder == null) {
            return null;
        }
        CTransApplicationGroup group = transcoder.getGroup(INCLUDE_GROUP_NAME);
        if (group == null) {
            return null;
        }
        BaseEngine engine = group.getEngine();
        Object resolved = engine.doAllAnalysis(copybookName, "", group, false);
        if (!(resolved instanceof CEntityExternalDataStructure)) {
            return null;
        }
        return (CEntityExternalDataStructure) resolved;
    }

    /**
     * Generates the copybook as its own Java {@code Copy} class through the
     * recursive assembler with the ROOT role ({@code javaCopyClass}) — the same
     * single-flattening path the program root uses. The field identifiers come
     * from the copybook entities' own exporters (lower-case camelCase), so they
     * match the identifiers the ST4-transpiled programs reference. Returns
     * {@code null} when the copybook cannot be resolved.
     */
    public static String generateCopybookClass(String copybookName) {
        CEntityExternalDataStructure structure = resolveCopybook(copybookName);
        if (structure == null) {
            return null;
        }
        return generate.templates.TemplateLoader.getRecursiveAssembler()
            .renderRoot(structure, generate.templates.recursive.JavaTemplateRole.ROOT);
    }

    private static Transcoder build(String inputDir, String outputDir) {
        String interDir = outputDir + "/stat";
        String configXml =
            "<NacaTrans Log4jConf=\"\">\n"
            + "  <Engines>\n"
            + "    <Transcoder Name=\"IncludeTranscoder\""
            + " Class=\"utils.CobolTranscoder.CobolIncludeTranscoderEngine\"\n"
            + "        ReferenceGroupName=\"\" ResourceGroupName=\"\" IncludeGroupName=\"\"/>\n"
            + "  </Engines>\n"
            + "  <Groups>\n"
            + "    <Group Name=\"" + INCLUDE_GROUP_NAME + "\""
            + " InputPath=\"" + inputDir + "\""
            + " OutputPath=\"" + outputDir + "/\""
            + " InterPath=\"" + interDir + "/\""
            + " Type=\"Included\" Engine=\"IncludeTranscoder\"/>\n"
            + "  </Groups>\n"
            + "  <GlobalPaths RuleFilePath=\"\"/>\n"
            + "</NacaTrans>\n";
        Transcoder transcoder = new Transcoder();
        if (!transcoder.Init(Tag.createFromString(configXml))) {
            throw new IllegalStateException(
                "Failed to initialize COPY include group from copybook dir: " + inputDir);
        }
        return transcoder;
    }
}
