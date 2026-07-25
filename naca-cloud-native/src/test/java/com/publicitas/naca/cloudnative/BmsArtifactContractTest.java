package com.publicitas.naca.cloudnative;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.OnlineCorpusSupport;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import semantic.CEntityClass;
import semantic.forms.CEntityResourceForm;
import semantic.forms.CEntityResourceFormContainer;
import utils.Transcoder;

/**
 * BMS artifact contract for the ONLINE canonical corpus.
 *
 * <p>{@code ONLINM1.bms} is a CICS screen-map DSL source, NOT a COBOL dialect. Its
 * artifacts are produced by the {@code BMSTranscoder} driving the {@code Resources}
 * (Type=Map) group that {@link OnlineCorpusSupport} wires in, and are consumed by the
 * COBOL program {@code ONLINE1} through {@code COPY ONLINM1} and
 * {@code EXEC SQL INCLUDE ONLINM1S}. This test pins that contract end to end:
 *
 * <ul>
 *   <li><b>input</b> — the real {@code ONLINM1.bms} source;</li>
 *   <li><b>physical / base artifact</b> — the {@code ONLINM1} form container, with the
 *       real map ({@code ONLINEF}) and its real field layout ({@code NMMASQ}, ...);</li>
 *   <li><b>symbolic artifact</b> — {@code ONLINM1S}, the save-map copy derived from the
 *       base via {@code MakeSavCopy} ({@code isSavCopy()==true});</li>
 *   <li><b>consumers</b> — {@code COPY ONLINM1} / {@code EXEC SQL INCLUDE ONLINM1S}
 *       resolve on demand from the real source (no empty/handwritten copybook, no
 *       "Missing include file"), so {@code ONLINE1} analyzes with the map inlined.</li>
 * </ul>
 *
 * <p>Unlike {@code OnlineCorpusInventoryTest} (a fail-closed debt baseline that stays
 * RED while SQL/CICS statements are still silently dropped), this contract is GREEN and
 * runs in the default gate: it asserts the BMS artifacts genuinely exist and are built
 * from source, which is a precondition for the SQL/CICS migration that follows.
 */
class BmsArtifactContractTest
{
    private static Path cobolDir;
    private static Path includeDir;
    private static Path csdFile;
    private static Path ruleFile;

    private Transcoder transcoder;

    @BeforeEach
    void build()
    {
        for (Path base : new Path[] { Path.of("NacaSamples"), Path.of("../NacaSamples") })
        {
            Path candidate = base.resolve("cobol");
            if (Files.isDirectory(candidate))
            {
                cobolDir = candidate.toAbsolutePath().normalize();
                includeDir = candidate.resolve("include").toAbsolutePath().normalize();
                csdFile = candidate.resolve("CICSCSD.txt").toAbsolutePath().normalize();
                ruleFile = base.resolve("trans/NacaTransRules.xml").toAbsolutePath().normalize();
                break;
            }
        }
        assertNotNull(cobolDir, "NacaSamples/cobol must exist");
        String outputDir = System.getProperty("java.io.tmpdir") + "/naca-bms-contract-"
            + System.nanoTime();
        // Fresh transcoder per test: the BMS engine caches form containers and save maps
        // in its catalog, so isolation keeps each assertion independent.
        transcoder = OnlineCorpusSupport.build(
            cobolDir.toString(), includeDir.toString(),
            Files.exists(csdFile) ? csdFile.toString() : null,
            Files.exists(ruleFile) ? ruleFile.toString() : null, outputDir);
    }

    @Test
    @DisplayName("ONLINM1 base mapset is generated from the real .bms source with a real field layout")
    void baseMapsetFromRealSource()
    {
        CEntityResourceFormContainer onlinm1 = OnlineCorpusSupport.analyzeMapset(transcoder, "ONLINM1");
        assertNotNull(onlinm1, "ONLINM1 must resolve from ONLINM1.bms via the BMS Resources group");
        assertEquals("ONLINM1", onlinm1.GetName(), "base artifact must be named ONLINM1");
        assertEquals(1, onlinm1.GetNbForms(), "ONLINM1 has exactly one map (ONLINEF)");

        CEntityResourceForm form = onlinm1.getForm();
        assertNotNull(form, "the ONLINEF map must exist");
        assertTrue(form.getNbFields() >= 5,
            "the map must carry a real field layout, got " + form.getNbFields());

        // Source fidelity: the field names come from ONLINM1.bms, proving this is the
        // parsed source structure and not an empty/handwritten placeholder.
        List<String> names = upper(form.getFieldNames());
        assertTrue(names.contains("NMMASQ"), "expected field NMMASQ in " + names);
        assertTrue(names.contains("LIERR"), "expected field LIERR in " + names);
    }

    @Test
    @DisplayName("ONLINM1S symbolic save-map is derived from the real source (isSavCopy)")
    void symbolicSaveMapFromRealSource()
    {
        CEntityResourceFormContainer onlinm1s = OnlineCorpusSupport.analyzeMapset(transcoder, "ONLINM1S");
        assertNotNull(onlinm1s, "ONLINM1S must resolve (S-suffix -> MakeSavCopy of ONLINM1)");
        assertEquals("ONLINM1S", onlinm1s.GetName(), "symbolic artifact must be named ONLINM1S");
        assertTrue(onlinm1s.isSavCopy(), "ONLINM1S must be flagged as a save-map copy");
        assertTrue(onlinm1s.GetNbForms() >= 1, "symbolic map must contain the mirrored form");

        CEntityResourceForm form = onlinm1s.getForm();
        assertNotNull(form, "the mirrored ONLINEFS form must exist");
        assertTrue(form.getNbFields() >= 1, "symbolic map must carry mirrored fields");
    }

    @Test
    @DisplayName("ONLINE1 resolves COPY ONLINM1 / EXEC SQL INCLUDE ONLINM1S on demand")
    void consumersResolveOnDemand()
    {
        CEntityClass root = OnlineCorpusSupport.analyze(transcoder, "ONLINE1");
        assertNotNull(root, "ONLINE1 must analyze with the BMS Resources group wired");

        // Both artifacts resolve on demand from the real source through the same
        // transcoder — the resolver/lifecycle the COPY ONLINM1 and EXEC SQL INCLUDE
        // ONLINM1S consumers rely on. (COPY wraps the resolved form container in a
        // CEntityInline; the resulting inlined map fields are what let the SEND MAP /
        // RECEIVE MAP statements survive, which OnlineCorpusInventoryTest verifies.)
        assertNotNull(OnlineCorpusSupport.analyzeMapset(transcoder, "ONLINM1"),
            "COPY ONLINM1 must resolve from ONLINM1.bms");
        assertNotNull(OnlineCorpusSupport.analyzeMapset(transcoder, "ONLINM1S"),
            "EXEC SQL INCLUDE ONLINM1S must resolve from ONLINM1.bms");
    }

    private static List<String> upper(List<String> in)
    {
        List<String> out = new ArrayList<>();
        for (String s : in)
        {
            out.add(s == null ? "" : s.toUpperCase());
        }
        return out;
    }
}
