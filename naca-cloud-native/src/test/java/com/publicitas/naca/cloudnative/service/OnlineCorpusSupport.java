package com.publicitas.naca.cloudnative.service;

import jlib.xml.Tag;
import semantic.CEntityClass;
import semantic.forms.CEntityResourceFormContainer;
import utils.BaseEngine;
import utils.CTransApplicationGroup;
import utils.Transcoder;

/**
 * Builds a portable, path-parameterized transpile environment for the ONLINE
 * canonical corpus (ONLINE1 + ONLINM1.bms): an Online group driven by the
 * {@code CobolTranscoder} with a CICS CSD, the copybook {@code Includes} group
 * (VTBMSGA / TUAZONE for {@code EXEC SQL INCLUDE}), and a {@code Resources}
 * (Type=Map) group driven by the {@code BMSTranscoder} that turns the real
 * {@code ONLINM1.bms} source into the physical map/resource and the symbolic
 * maps {@code ONLINM1}/{@code ONLINM1S}. The COBOL engine's
 * {@code ResourceGroupName} points at that group, so {@code COPY ONLINM1} and
 * {@code EXEC SQL INCLUDE ONLINM1S} resolve on demand from the real BMS source
 * (no handwritten/empty copybook). Paths are supplied by the caller (resolved
 * relative to the working directory in tests), so there are no hardcoded
 * machine-specific paths and no requirement for a permanent environment.
 * Mirrors the engine/group layout of the full NacaTrans configuration (the
 * Windows {@code NacaTransSamples.cfg}), which the Unix config currently lacks.
 *
 * <p>This is the acceptance foundation (T0): it lets tests parse the online
 * corpus and inventory the SQL/CICS/BMS nodes so no recognized source statement
 * can silently disappear.
 */
public final class OnlineCorpusSupport {

    public static final String ONLINE_GROUP_NAME = "Online";
    public static final String INCLUDE_GROUP_NAME = "Includes";
    public static final String RESOURCE_GROUP_NAME = "Resources";
    public static final String BMS_ENGINE_NAME = "BMSTranscoder";

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
        String ruleFile, String outputDir) {
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
        // The rules file carries the ignoredCopy rules (SQLCA/DFHAID/DFHCWADS are
        // runtime-provided, so COPY/INCLUDE of them must be ignored, not resolved
        // as missing includes).
        String rules = (ruleFile == null || ruleFile.isEmpty())
            ? ""
            : java.nio.file.Path.of(ruleFile).toAbsolutePath().normalize().toString();

        String csdElement = (csd == null)
            ? ""
            : "<CSD File=\"" + csd + "\" Output=\"" + output + "TransIDMapping.xml\"/>";

        String configXml =
            "<NacaTrans Log4jConf=\"\">\n"
            + "  <Engines>\n"
            + "    <Transcoder Name=\"CobolTranscoder\""
            + " Class=\"utils.CobolTranscoder.CobolTranscoderEngine\"\n"
            + "        ReferenceGroupName=\"\" ResourceGroupName=\"" + RESOURCE_GROUP_NAME
            + "\" IncludeGroupName=\"" + INCLUDE_GROUP_NAME + "\">\n"
            + "      " + csdElement + "\n"
            + "    </Transcoder>\n"
            + "    <Transcoder Name=\"IncludeTranscoder\""
            + " Class=\"utils.CobolTranscoder.CobolIncludeTranscoderEngine\"\n"
            + "        ReferenceGroupName=\"\" ResourceGroupName=\"\" IncludeGroupName=\"\"/>\n"
            // BMS engine driving the Resources/Map group. No BMSSpec/form-transform is
            // configured (formEnhancer stays null) to keep the corpus portable; the
            // engine null-guards the enhancer. Symbolic maps (ONLINM1/ONLINM1S) are
            // generated from the real ONLINM1.bms source, never handwritten.
            + "    <Transcoder Name=\"" + BMS_ENGINE_NAME + "\""
            + " Class=\"utils.CobolTranscoder.BMSTranscoderEngine\"\n"
            + "        ResourceGroupName=\"" + RESOURCE_GROUP_NAME + "\"/>\n"
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
            // Resources/Map group: same InputPath as the Online group so the BMS
            // engine finds ONLINM1.bms next to ONLINE1.cbl. COPY ONLINM1 and
            // EXEC SQL INCLUDE ONLINM1S resolve on demand through this group
            // (CObjectCatalog.GetExternalDataReference -> CGlobalCatalog.GetFormContainer
            // -> BMSTranscoderEngine.doAllAnalysis), which reads the real .bms source.
            + "    <Group Name=\"" + RESOURCE_GROUP_NAME + "\""
            + " InputPath=\"" + cobol + "\""
            + " OutputPath=\"" + output + "resources/\""
            + " InterPath=\"" + interDir + "/\""
            + " Type=\"Map\" Engine=\"" + BMS_ENGINE_NAME + "\"/>\n"
            + "  </Groups>\n"
            + "  <GlobalPaths RuleFilePath=\"" + rules + "\"/>\n"
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

    /**
     * Drive the BMS {@code Resources}/{@code Map} group directly on a mapset name
     * (e.g. {@code ONLINM1} or its symbolic variant {@code ONLINM1S}) and return the
     * resulting form container, or {@code null} if it cannot be built. The container
     * is generated from the real {@code <name>.bms} source by
     * {@code BMSTranscoderEngine.doAllAnalysis} (the {@code S} variant is derived via
     * {@code MakeSavCopy} from the base mapset) — this is the BMS artifact contract
     * that {@code COPY ONLINM1} and {@code EXEC SQL INCLUDE ONLINM1S} resolve through.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static CEntityResourceFormContainer analyzeMapset(Transcoder transcoder,
        String mapsetName) {
        CTransApplicationGroup group = transcoder.getGroup(RESOURCE_GROUP_NAME);
        if (group == null) {
            return null;
        }
        BaseEngine engine = group.getEngine();
        Object analyzed = engine.doAllAnalysis(mapsetName, "", group, false);
        return analyzed instanceof CEntityResourceFormContainer
            ? (CEntityResourceFormContainer) analyzed
            : null;
    }
}
