package com.publicitas.naca.tests.acceptance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.publicitas.naca.cloudnative.service.IncludeGroupSupport;
import com.publicitas.naca.cloudnative.service.TranspilerService;
import com.publicitas.naca.cloudnative.service.TranspilerService.TranspileResult;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Offline strict acceptance and deterministic blocker coverage for CardDemo. */
@Tag("sample-acceptance")
class CardDemoEndToEndAcceptanceTest
{
    private static final String SOURCE_URL =
        "https://github.com/aws-samples/aws-mainframe-modernization-carddemo.git";
    private static final String COMMIT =
        "59cc6c2fd7ebd7ef7925cad552a01a4b8b6e4d5e";
    private static final String LICENSE_PATH = "LICENSE";
    private static final String JAVA_CLASS_PATH = "java.class.path";
    private static final String LICENSE_HASH =
        "09e8a9bcec8067104652c168685ab0931e7868f9c8284b66f5ae6edae5f1130b";

    private static final String BLOCKED_PROGRAM_PATH = "app/cbl/CBACT01C.cbl";
    private static final String BLOCKED_PROGRAM_HASH =
        "f8eb6e3a561ff96a1889a8f777a8998850a11dddae221da2c5018067d7b95551";
    private static final String ACCOUNT_COPYBOOK_PATH = "app/cpy/CVACT01Y.cpy";
    private static final String ACCOUNT_COPYBOOK_HASH =
        "81a08bad15af5664326a6f0af3650f570821c4857ffdec3a6a39f91f07dca728";
    private static final String DATE_COPYBOOK_PATH = "app/cpy/CODATECN.cpy";
    private static final String DATE_COPYBOOK_HASH =
        "8efc6b6d79bd46d65fcdc4e6a8e3778511fca76054f3fbae8e3075b0a2aa8fb1";
    private static final String ACCOUNT_DATA_PATH = "app/data/ASCII/acctdata.txt";
    private static final String ACCOUNT_DATA_HASH =
        "c2a97b6a32dc4a87a7aafdf7f72e6712e560412d30b00c5526cca80fc9dfd260";

    private static final Scenario CBACT = new Scenario(
        "CBACT02C", "app/cbl/CBACT02C.cbl",
        "d290cbbbec1e25859847d9dfe6b28040cd6b09c3b71747e39de7b4713d838e76",
        "CVACT02Y", "app/cpy/CVACT02Y.cpy",
        "9f1c62ef31b9d541712f1ed28f79e011ea7338611f5e0f30f2e1cbf624181223",
        "app/data/ASCII/carddata.txt",
        "da217240d2567c85f84b571aeb465171c683cfd21dad1754046f6bbb10e76c1d",
        "Cbact02c.java", "Cvact02y.java", "Cbact02c", "CARDFILE",
        "ascii,fb,150", 150, 150, 1,
        "START OF EXECUTION OF PROGRAM CBACT02C",
        "END OF EXECUTION OF PROGRAM CBACT02C");

    private static final Scenario CBACT_XREF = new Scenario(
        "CBACT03C", "app/cbl/CBACT03C.cbl",
        "ee1019bc3ef7bc4e0f807b6e9b7167c9386ee7c2f647c63dfddc6fc464487855",
        "CVACT03Y", "app/cpy/CVACT03Y.cpy",
        "ffc6079e09b28739e154bf6c1e1c36d408209faa91f6cf7008078dc596a1c370",
        "app/data/ASCII/cardxref.txt",
        "efec3825ec0d5b791cf54f815bf688abfcc9db832c1600371ed2209df4e97764",
        "Cbact03c.java", "Cvact03y.java", "Cbact03c", "XREFFILE",
        "ascii,fb,50", 36, 50, 2,
        "START OF EXECUTION OF PROGRAM CBACT03C",
        "END OF EXECUTION OF PROGRAM CBACT03C");

    private static final Scenario CBCUS = new Scenario(
        "CBCUS01C", "app/cbl/CBCUS01C.cbl",
        "233dbc3bc33a3b9ac555922085a92be56e88d16316bff624b5723c4c36c99714",
        "CVCUS01Y", "app/cpy/CVCUS01Y.cpy",
        "944fd9a8eb10a683d0db97b9a87f2151982f649970287eb74358826056af25f8",
        "app/data/ASCII/custdata.txt",
        "d8cfa5b77fa61614329e73ebde9052367ea31cc1b08f9056fa869f949fef9991",
        "Cbcus01c.java", "Cvcus01y.java", "Cbcus01c", "CUSTFILE",
        "ascii,fb,500", 500, 500, 2,
        "START OF EXECUTION OF PROGRAM CBCUS01C",
        "END OF EXECUTION OF PROGRAM CBCUS01C");

    @TempDir
    Path workspace;

    @Test
    void pinnedCardProgramRunsWithExactRecords()
    {
        assertDoesNotThrow(() -> runAcceptance(CBACT),
            "CBACT02C offline acceptance failed");
    }

    @Test
    void pinnedCustomerProgramRunsWithEachRecordDisplayedTwice()
    {
        assertDoesNotThrow(() -> runAcceptance(CBCUS),
            "CBCUS01C offline acceptance failed");
    }

    @Test
    void pinnedCrossReferenceProgramRunsWithPaddedRecordsDisplayedTwice()
    {
        assertDoesNotThrow(() -> runAcceptance(CBACT_XREF),
            "CBACT03C offline acceptance failed");
    }

    /** Reproduces the pinned CBACT01C runtime blocker after successful compilation. */
    @Test
    void pinnedAccountExportReproducesOutputFileStatusBlocker()
    {
        assertDoesNotThrow(this::runBlockedAccountExport,
            "CBACT01C blocker regression changed unexpectedly");
    }

    /** Runs CBACT01C through its last passing stage and asserts its first blocker. */
    private void runBlockedAccountExport() throws Exception
    {
        Path corpus = locateCorpus();
        verifyProvenance(corpus);
        Path programFile = corpus.resolve(BLOCKED_PROGRAM_PATH);
        Path accountCopybook = corpus.resolve(ACCOUNT_COPYBOOK_PATH);
        Path dateCopybook = corpus.resolve(DATE_COPYBOOK_PATH);
        Path accountData = corpus.resolve(ACCOUNT_DATA_PATH);
        assertEquals(BLOCKED_PROGRAM_HASH, sha256(programFile),
            "CBACT01C program hash mismatch");
        assertEquals(ACCOUNT_COPYBOOK_HASH, sha256(accountCopybook),
            "CVACT01Y copybook hash mismatch");
        assertEquals(DATE_COPYBOOK_HASH, sha256(dateCopybook),
            "CODATECN copybook hash mismatch");
        assertEquals(ACCOUNT_DATA_HASH, sha256(accountData),
            "account data hash mismatch");
        verifyFixedRecords(accountData, 300, "CBACT01C");

        Path scenarioWorkspace = workspace.resolve("CBACT01C");
        Path copyDir = scenarioWorkspace.resolve("copy");
        Files.createDirectories(copyDir);
        Files.copy(accountCopybook, copyDir.resolve("CVACT01Y"));
        Files.copy(dateCopybook, copyDir.resolve("CODATECN"));
        IncludeGroupSupport.configure(
            copyDir.toString(), scenarioWorkspace.resolve("includes").toString());
        TranspilerService service = new TranspilerService();
        TranspileResult result = service.transpile(
            Files.readString(programFile, StandardCharsets.ISO_8859_1),
            "CBACT01C");
        assertTrue(result.isSuccess(),
            "CBACT01C transpilation failed: " + result.getErrors());
        assertNotNull(result.getJavaSource(),
            "CBACT01C returned no Java source");
        String accountCopySource = IncludeGroupSupport.generateCopybookClass(
            "CVACT01Y");
        assertNotNull(accountCopySource, "CVACT01Y generation failed");
        String dateCopySource = IncludeGroupSupport.generateCopybookClass(
            "CODATECN");
        assertNotNull(dateCopySource, "CODATECN generation failed");

        Path sourceDir = scenarioWorkspace.resolve("sources");
        Files.createDirectories(sourceDir);
        Path programSource = sourceDir.resolve("Cbact01c.java");
        Path accountSource = sourceDir.resolve("Cvact01y.java");
        Path dateSource = sourceDir.resolve("Codatecn.java");
        Files.writeString(programSource, result.getJavaSource(),
            StandardCharsets.ISO_8859_1);
        Files.writeString(accountSource, accountCopySource,
            StandardCharsets.ISO_8859_1);
        Files.writeString(dateSource, dateCopySource,
            StandardCharsets.ISO_8859_1);
        Path classes = scenarioWorkspace.resolve("classes");
        Files.createDirectories(classes);
        ProcessResult compile = runProcess(scenarioWorkspace,
            "javac", "-proc:none", "-classpath", System.getProperty(JAVA_CLASS_PATH),
            "-d", classes.toString(), programSource.toString(),
            accountSource.toString(), dateSource.toString());
        assertEquals(0, compile.exitCode(),
            "CBACT01C generated Java compilation failed: " + compile.output());

        ProcessResult run = runProcess(scenarioWorkspace,
            "java", "-cp", System.getProperty(JAVA_CLASS_PATH)
                + File.pathSeparator + classes,
            "com.publicitas.naca.tests.acceptance.AcceptanceProgramRunner",
            "Cbact01c", classes.toString(),
            "ACCTFILE", accountData.toString(), "ascii,fb,300",
            "OUTFILE", scenarioWorkspace.resolve("account.out").toString(),
            "ascii,fb,107",
            "ARRYFILE", scenarioWorkspace.resolve("array.out").toString(),
            "ascii,fb,110",
            "VBRCFILE", scenarioWorkspace.resolve("variable.out").toString(),
            "ascii,vb,84");
        assertTrue(run.output().contains("START OF EXECUTION OF PROGRAM CBACT01C"),
            "CBACT01C did not reach runtime: " + run.output());
        assertTrue(run.output().contains("ERROR OPENING OUTFILE"),
            "CBACT01C no longer reproduces its FILE STATUS blocker: " + run.output());
        assertTrue(run.output().contains("ABENDING PROGRAM"),
            "CBACT01C blocker no longer reaches its abend path: " + run.output());
    }

    private void runAcceptance(Scenario scenario) throws Exception
    {
        Path corpus = locateCorpus();
        verifyProvenance(corpus);
        Path programFile = corpus.resolve(scenario.programPath());
        Path copybookFile = corpus.resolve(scenario.copybookPath());
        Path dataFile = corpus.resolve(scenario.dataPath());
        assertEquals(scenario.programHash(), sha256(programFile),
            "program hash mismatch for " + scenario.programPath());
        assertEquals(scenario.copybookHash(), sha256(copybookFile),
            "copybook hash mismatch for " + scenario.copybookPath());
        assertEquals(scenario.dataHash(), sha256(dataFile),
            "data hash mismatch for " + scenario.dataPath());
        List<String> records = verifyRecords(dataFile, scenario);

        Path scenarioWorkspace = workspace.resolve(scenario.programName());
        Path runtimeDataFile = prepareRuntimeData(
            dataFile, records, scenario, scenarioWorkspace);
        Path copyDir = scenarioWorkspace.resolve("copy");
        Files.createDirectories(copyDir);
        Files.copy(copybookFile, copyDir.resolve(scenario.copybookName()));
        IncludeGroupSupport.configure(
            copyDir.toString(), scenarioWorkspace.resolve("includes").toString());
        TranspilerService service = new TranspilerService();
        TranspileResult result = service.transpile(
            Files.readString(programFile, StandardCharsets.ISO_8859_1),
            scenario.programName());
        assertTrue(result.isSuccess(), scenario.programName()
            + " transpilation failed: " + result.getErrors());
        assertNotNull(result.getJavaSource(),
            scenario.programName() + " returned no Java source");
        String copySource = IncludeGroupSupport.generateCopybookClass(
            scenario.copybookName());
        assertNotNull(copySource,
            scenario.copybookName() + " generation failed");

        Path sourceDir = scenarioWorkspace.resolve("sources");
        Files.createDirectories(sourceDir);
        Path programSource = sourceDir.resolve(scenario.programJava());
        Path copybookSource = sourceDir.resolve(scenario.copybookJava());
        Files.writeString(programSource, result.getJavaSource(),
            StandardCharsets.ISO_8859_1);
        Files.writeString(copybookSource, copySource, StandardCharsets.ISO_8859_1);
        Path classes = scenarioWorkspace.resolve("classes");
        Files.createDirectories(classes);
        ProcessResult compile = runProcess(scenarioWorkspace,
            "javac", "-proc:none", "-classpath", System.getProperty(JAVA_CLASS_PATH),
            "-d", classes.toString(), programSource.toString(), copybookSource.toString());
        assertEquals(0, compile.exitCode(), scenario.programName()
            + " generated Java compilation failed: " + compile.output());

        ProcessResult run = runProcess(scenarioWorkspace,
            "java", "-cp", System.getProperty(JAVA_CLASS_PATH)
                + File.pathSeparator + classes,
            "com.publicitas.naca.tests.acceptance.AcceptanceProgramRunner",
            scenario.runtimeClass(), classes.toString(), scenario.logicalName(),
            runtimeDataFile.toString(), scenario.descriptor());
        assertEquals(0, run.exitCode(),
            scenario.programName() + " runtime failed: " + run.output());
        assertExactBusinessOutput(run.output(), records, scenario);
    }

    private static void verifyProvenance(Path corpus) throws Exception
    {
        String provenance = Files.readString(
            corpus.resolve("PROVENANCE.json"), StandardCharsets.UTF_8);
        assertTrue(provenance.contains("\"url\": \"" + SOURCE_URL + "\""),
            "provenance source URL mismatch");
        assertTrue(provenance.contains("\"commit\": \"" + COMMIT + "\""),
            "provenance commit mismatch");
        assertTrue(provenance.contains("\"SPDX\": \"Apache-2.0\""),
            "provenance license SPDX mismatch");
        assertTrue(provenance.contains("\"path\": \"" + LICENSE_PATH + "\""),
            "provenance license path mismatch");

        Map<String, String> expectedFiles = Map.ofEntries(
            Map.entry(BLOCKED_PROGRAM_PATH, BLOCKED_PROGRAM_HASH),
            Map.entry(ACCOUNT_COPYBOOK_PATH, ACCOUNT_COPYBOOK_HASH),
            Map.entry(DATE_COPYBOOK_PATH, DATE_COPYBOOK_HASH),
            Map.entry(ACCOUNT_DATA_PATH, ACCOUNT_DATA_HASH),
            Map.entry(CBACT.programPath(), CBACT.programHash()),
            Map.entry(CBACT.copybookPath(), CBACT.copybookHash()),
            Map.entry(CBACT.dataPath(), CBACT.dataHash()),
            Map.entry(CBCUS.programPath(), CBCUS.programHash()),
            Map.entry(CBCUS.copybookPath(), CBCUS.copybookHash()),
            Map.entry(CBCUS.dataPath(), CBCUS.dataHash()),
            Map.entry(CBACT_XREF.programPath(), CBACT_XREF.programHash()),
            Map.entry(CBACT_XREF.copybookPath(), CBACT_XREF.copybookHash()),
            Map.entry(CBACT_XREF.dataPath(), CBACT_XREF.dataHash()),
            Map.entry(LICENSE_PATH, LICENSE_HASH));
        long fileEntries = provenance.lines()
            .map(String::trim)
            .filter(line -> line.matches("\"[^\"]+\": \"[0-9a-f]{64}\"[,]?"))
            .count();
        assertEquals(expectedFiles.size(), fileEntries,
            "provenance must enumerate every vendored CardDemo asset");
        for (Map.Entry<String, String> entry : expectedFiles.entrySet())
        {
            String expectedEntry = "\"" + entry.getKey() + "\": \""
                + entry.getValue() + "\"";
            assertTrue(provenance.contains(expectedEntry),
                "provenance hash mismatch for " + entry.getKey());
            assertEquals(entry.getValue(), sha256(corpus.resolve(entry.getKey())),
                "vendored file hash mismatch for " + entry.getKey());
        }
    }

    private static List<String> verifyRecords(Path dataFile, Scenario scenario)
        throws Exception
    {
        byte[] data = Files.readAllBytes(dataFile);
        assertEquals(50 * (scenario.inputRecordLength() + 1), data.length,
            scenario.programName() + " data byte count mismatch");
        List<String> records = Files.readAllLines(
            dataFile, StandardCharsets.ISO_8859_1);
        assertEquals(50, records.size(),
            scenario.programName() + " input must contain 50 records");
        assertTrue(records.stream().allMatch(record -> record.getBytes(
            StandardCharsets.ISO_8859_1).length == scenario.inputRecordLength()),
            scenario.programName() + " records have an invalid byte length");
        return records.stream()
            .map(record -> record + " ".repeat(
                scenario.outputRecordLength() - scenario.inputRecordLength()))
            .toList();
    }

    /** Verifies the fixed-record shape of a vendored CardDemo data file. */
    private static void verifyFixedRecords(
        Path dataFile, int recordLength, String programName) throws Exception
    {
        byte[] data = Files.readAllBytes(dataFile);
        assertEquals(50 * (recordLength + 1), data.length,
            programName + " data byte count mismatch");
        List<String> records = Files.readAllLines(
            dataFile, StandardCharsets.ISO_8859_1);
        assertEquals(50, records.size(),
            programName + " input must contain 50 records");
        assertTrue(records.stream().allMatch(record -> record.getBytes(
            StandardCharsets.ISO_8859_1).length == recordLength),
            programName + " records have an invalid byte length");
    }

    private static Path prepareRuntimeData(Path dataFile, List<String> records,
        Scenario scenario, Path scenarioWorkspace) throws Exception
    {
        if (scenario.inputRecordLength() == scenario.outputRecordLength())
        {
            return dataFile;
        }
        Files.createDirectories(scenarioWorkspace);
        Path normalized = scenarioWorkspace.resolve("fixed-records.dat");
        Files.writeString(normalized, String.join("\n", records) + "\n",
            StandardCharsets.ISO_8859_1);
        return normalized;
    }

    private static void assertExactBusinessOutput(
        String output, List<String> records, Scenario scenario)
    {
        List<String> business = output.lines()
            .filter(line -> scenario.start().equals(line)
                || scenario.end().equals(line)
                || line.length() == scenario.outputRecordLength())
            .toList();
        List<String> expected = new ArrayList<>();
        expected.add(scenario.start());
        for (String record : records)
        {
            for (int repetition = 0; repetition < scenario.repetitions(); repetition++)
            {
                expected.add(record);
            }
        }
        expected.add(scenario.end());
        int expectedLineCount = 2 + 50 * scenario.repetitions();
        assertEquals(expectedLineCount, business.size(),
            scenario.programName() + " business line count mismatch: " + output);
        assertEquals(expected, business,
            scenario.programName() + " business output order mismatch: " + output);
    }

    private static String sha256(Path file) throws Exception
    {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
            .digest(Files.readAllBytes(file)));
    }

    private static Path locateCorpus()
    {
        for (Path candidate : List.of(Path.of("src/test/resources/carddemo"),
            Path.of("naca-rt-tests/src/test/resources/carddemo")))
        {
            if (Files.isRegularFile(candidate.resolve("PROVENANCE.json")))
            {
                return candidate.toAbsolutePath().normalize();
            }
        }
        throw new IllegalStateException("CardDemo corpus not found");
    }

    private static ProcessResult runProcess(Path directory, String... command)
        throws Exception
    {
        Process process = new ProcessBuilder(command).directory(directory.toFile())
            .redirectErrorStream(true).start();
        if (!process.waitFor(30, TimeUnit.SECONDS))
        {
            process.destroyForcibly();
            throw new AssertionError("process timed out: " + String.join(" ", command));
        }
        return new ProcessResult(process.exitValue(), new String(
            process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
    }

    private record Scenario(
        String programName, String programPath, String programHash,
        String copybookName, String copybookPath, String copybookHash,
        String dataPath, String dataHash, String programJava, String copybookJava,
        String runtimeClass, String logicalName, String descriptor,
        int inputRecordLength, int outputRecordLength, int repetitions,
        String start, String end)
    {
    }

    private record ProcessResult(int exitCode, String output)
    {
    }
}
