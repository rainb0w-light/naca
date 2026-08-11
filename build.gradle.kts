import groovy.json.JsonSlurper
import java.math.BigDecimal
import java.math.RoundingMode
import javax.xml.parsers.DocumentBuilderFactory

/*
 * Naca - COBOL to Java Transpiler
 * Root Project Build Configuration
 * JDK 21 + Spring Boot 3.x
 */

plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.3" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
    id("com.github.spotbugs") version "6.5.10" apply false
}

val pmdCpd by configurations.creating

dependencies {
    pmdCpd("net.sourceforge.pmd:pmd-cli:7.26.0")
    pmdCpd("net.sourceforge.pmd:pmd-java:7.26.0")
}

// Dependency version management - centralized for all modules
ext {
    set("lombokVersion", "1.18.30")
    set("guavaVersion", "33.4.8-jre")
    set("commonsLangVersion", "3.12.0")
    set("commonsTextVersion", "1.10.0")
    set("commonsIoVersion", "2.16.1")
    set("antlrVersion", "4.13.2")
    set("jgraphtVersion", "1.5.2")
    set("slf4jVersion", "2.0.9")
    set("logbackVersion", "1.5.7")
    set("junitVersion", "5.11.4")
    set("mockitoVersion", "5.10.0")
    set("guiceVersion", "4.2.2")
    set("jooqVersion", "3.19.14")
    set("sqliteVersion", "3.47.0.0")
    set("neo4jDriverVersion", "5.23.0")
    set("graphvizVersion", "0.18.1")
    set("graalJsVersion", "24.2.2")
    set("vavrVersion", "0.10.4")
    set("picocliVersion", "4.7.6")
    set("jlineVersion", "3.26.1")
    set("lsp4jVersion", "0.14.0")
}

allprojects {
    group = "com.publicitas.naca"
    version = "2.0.0-SNAPSHOT"

    repositories {
        mavenLocal()  // For cobol-rekt modules
        maven { url = uri("https://maven.aliyun.com/repository/central") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "checkstyle")
    apply(plugin = "pmd")
    apply(plugin = "com.github.spotbugs")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        encoding("ISO-8859-1")
        ratchetFrom("origin/master")
        java {
            target("src/main/java/**/*.java", "src/test/java/**/*.java")
            targetExclude("src/main/translated-java/**", "**/generated/**", "**/build/**")
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    configure<CheckstyleExtension> {
        toolVersion = "13.10.0"
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")
        configProperties["checkstyle.suppressions.file"] =
            rootProject.file("config/checkstyle/suppressions.xml").absolutePath
        isIgnoreFailures = true
        isShowViolations = true
    }

    tasks.withType<Checkstyle>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    tasks.named<Checkstyle>("checkstyleMain") {
        source = fileTree("src/main/java") { include("**/*.java") }
    }

    tasks.named<Checkstyle>("checkstyleTest") {
        source = fileTree("src/test/java") { include("**/*.java") }
    }

    configure<PmdExtension> {
        toolVersion = "7.26.0"
        ruleSetFiles = files(rootProject.file("config/pmd/ruleset.xml"))
        ruleSets = emptyList()
        isIgnoreFailures = true
        isConsoleOutput = false
        threads.set(1)
    }

    tasks.withType<Pmd>().configureEach {
        reports {
            xml.required.set(true)
            html.required.set(true)
        }
    }

    tasks.named<Pmd>("pmdMain") {
        setSource(fileTree("src/main/java") { include("**/*.java") })
    }

    tasks.named<Pmd>("pmdTest") {
        setSource(fileTree("src/test/java") { include("**/*.java") })
    }

    configure<com.github.spotbugs.snom.SpotBugsExtension> {
        toolVersion.set("4.9.6")
        ignoreFailures.set(true)
        showProgress.set(false)
        effort.set(com.github.spotbugs.snom.Effort.DEFAULT)
        reportLevel.set(com.github.spotbugs.snom.Confidence.MEDIUM)
    }

    tasks.withType<com.github.spotbugs.snom.SpotBugsTask>().configureEach {
        val analysisTaskName = name
        reports.create("xml") {
            required.set(true)
            outputLocation.set(layout.buildDirectory.file("reports/spotbugs/$analysisTaskName.xml"))
        }
        reports.create("html") {
            required.set(true)
            outputLocation.set(layout.buildDirectory.file("reports/spotbugs/$analysisTaskName.html"))
            setStylesheet("fancy-hist.xsl")
        }
    }
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "ISO-8859-1"
        options.isFork = true
        options.forkOptions.memoryMaximumSize = "1g"
        options.compilerArgs.addAll(listOf(
            "-Xlint:deprecation",
            "-Xlint:unchecked"
        ))
        doFirst {
            // Set JVM args right before compilation
            options.forkOptions.jvmArgs = listOf(
                "--add-exports=java.xml/com.sun.org.apache.xml.internal.utils=ALL-UNNAMED",
                "--add-exports=java.xml/com.sun.org.apache.xpath.internal.operations=ALL-UNNAMED",
                "--add-exports=java.xml/com.sun.org.apache.xalan.internal.xsltc.runtime=ALL-UNNAMED",
                "--add-exports=java.xml/com.sun.org.apache.xpath.internal=ALL-UNNAMED",
                "--add-opens=java.xml/com.sun.org.apache.xml.internal.utils=ALL-UNNAMED",
                "--add-opens=java.xml/com.sun.org.apache.xpath.internal.operations=ALL-UNNAMED",
                "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.runtime=ALL-UNNAMED"
            )
        }
    }
    
    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
    }
    
    tasks.test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
        jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
        finalizedBy(tasks.jacocoTestReport)
    }
    
    tasks.jacocoTestReport {
        dependsOn(tasks.test)
        reports {
            xml.required.set(true)
            html.required.set(true)
            csv.required.set(false)
        }
    }
    
    tasks.jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    minimum = "0.0".toBigDecimal()
                }
            }
        }
    }
}

tasks.register("allTests") {
    dependsOn(subprojects.map { it.tasks.named("test") })
    group = "verification"
    description = "Runs all tests across all subprojects"
}

// Aggregate JaCoCo report for all subprojects
tasks.register<JacocoReport>("jacocoAggregateReport") {
    group = "verification"
    description = "Generates aggregate JaCoCo coverage report"
    
    subprojects.forEach { subproject ->
        executionData(subproject.tasks.test.get())
        sourceSets(subproject.sourceSets.main.get())
    }
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.register("sampleAcceptance") {
    group = "verification"
    description = "Runs the canonical COBOL-to-Java end-to-end acceptance pipeline"
    dependsOn(":naca-trans:finalArchitectureCheck")
    dependsOn(":naca-rt-tests:sampleAcceptance")
}

tasks.register("qualityCheck") {
    group = "verification"
    description = "Runs formatting and no-growth static-analysis quality gates"
    dependsOn(subprojects.map { "${it.path}:spotlessCheck" })
    dependsOn("qualityRatchet")
}

val staticAnalysisTasks = listOf(
        "checkstyleMain",
        "checkstyleTest",
        "pmdMain",
        "pmdTest",
        "spotbugsMain",
        "spotbugsTest"
)

tasks.register("qualityRatchet") {
    group = "verification"
    description = "Rejects growth in Checkstyle, PMD, SpotBugs, or CPD findings"
    dependsOn(subprojects.flatMap { subproject ->
        staticAnalysisTasks
            .filterNot { subproject.name == "naca-rt-tests" && it == "spotbugsMain" }
            .map { taskName -> "${subproject.path}:$taskName" }
    })
    dependsOn("cpdReport")

    doLast {
        val baseline = JsonSlurper().parse(rootProject.file("docs/project-quality-baseline.json")) as Map<*, *>
        val moduleBaselines = baseline["staticAnalysis"] as Map<*, *>
        val failures = mutableListOf<String>()
        val improvements = mutableListOf<String>()

        fun countElements(report: File, elementName: String): Int {
            check(report.isFile) { "Missing static-analysis report: ${report.absolutePath}" }
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            val document = factory.newDocumentBuilder().parse(report)
            return document.getElementsByTagNameNS("*", elementName).length
        }

        val metrics = listOf(
            Triple("checkstyleMain", "checkstyle/main.xml", "error"),
            Triple("checkstyleTest", "checkstyle/test.xml", "error"),
            Triple("pmdMain", "pmd/main.xml", "violation"),
            Triple("pmdTest", "pmd/test.xml", "violation"),
            Triple("spotbugsMain", "spotbugs/spotbugsMain.xml", "BugInstance"),
            Triple("spotbugsTest", "spotbugs/spotbugsTest.xml", "BugInstance")
        )

        subprojects.forEach { subproject ->
            val expected = moduleBaselines[subproject.name] as? Map<*, *>
                ?: error("Missing static-analysis baseline for ${subproject.name}")
            metrics.filter { expected[it.first] is Number }.forEach { (metric, relativePath, elementName) ->
                val maximum = (expected[metric] as Number).toInt()
                val report = subproject.layout.buildDirectory.file("reports/$relativePath").get().asFile
                val actual = countElements(report, elementName)
                when {
                    actual > maximum -> failures += "${subproject.name}.$metric grew from $maximum to $actual"
                    actual < maximum -> improvements += "${subproject.name}.$metric is $actual (baseline $maximum; tighten the baseline)"
                }
            }
        }

        val cpdMaximum = (baseline["cpdDuplications"] as Number).toInt()
        val cpdActual = countElements(layout.buildDirectory.file("reports/pmd/cpd.xml").get().asFile, "duplication")
        when {
            cpdActual > cpdMaximum -> failures += "CPD duplications grew from $cpdMaximum to $cpdActual"
            cpdActual < cpdMaximum -> improvements += "CPD duplications are $cpdActual (baseline $cpdMaximum; tighten the baseline)"
        }

        if (improvements.isNotEmpty()) {
            logger.lifecycle("Quality debt improved:\n${improvements.joinToString("\n") { "  - $it" }}")
        }
        check(failures.isEmpty()) {
            "Static-analysis debt may not grow:\n${failures.joinToString("\n") { "  - $it" }}"
        }
    }
}

tasks.register("sourceDebtCheck") {
    group = "verification"
    description = "Prevents generated comment boilerplate and active m_ identifiers from returning"

    val maintainedSources = subprojects.flatMap { subproject ->
        listOf("src/main/java", "src/test/java").flatMap { sourcePath ->
            val root = subproject.layout.projectDirectory.dir(sourcePath).asFile
            if (!root.isDirectory) emptyList() else fileTree(root) { include("**/*.java") }.files
        }
    }
    inputs.files(maintainedSources)

    doLast {
        val failures = mutableListOf<String>()
        val generatedTodo = "TODO " + "Auto-generated"
        val ideTemplate = "To change the template" + " for this generated"
        val hungarianIdentifier = Regex("\\bm_[A-Za-z0-9_]+\\b")

        fun stripCommentsAndLiterals(source: String): String {
            val result = StringBuilder(source.length)
            var state = 0 // 0 code, 1 line comment, 2 block comment, 3 string, 4 character
            var escaped = false
            var index = 0
            while (index < source.length) {
                val current = source[index]
                val next = source.getOrNull(index + 1)
                when (state) {
                    0 -> when {
                        current == '/' && next == '/' -> {
                            result.append("  ")
                            state = 1
                            index++
                        }
                        current == '/' && next == '*' -> {
                            result.append("  ")
                            state = 2
                            index++
                        }
                        current == '"' -> {
                            result.append(' ')
                            state = 3
                        }
                        current == '\'' -> {
                            result.append(' ')
                            state = 4
                        }
                        else -> result.append(current)
                    }
                    1 -> {
                        result.append(if (current == '\n') '\n' else ' ')
                        if (current == '\n') state = 0
                    }
                    2 -> {
                        if (current == '*' && next == '/') {
                            result.append("  ")
                            state = 0
                            index++
                        } else {
                            result.append(if (current == '\n') '\n' else ' ')
                        }
                    }
                    3, 4 -> {
                        result.append(if (current == '\n') '\n' else ' ')
                        if (escaped) {
                            escaped = false
                        } else if (current == '\\') {
                            escaped = true
                        } else if ((state == 3 && current == '"') || (state == 4 && current == '\'')) {
                            state = 0
                        }
                    }
                }
                index++
            }
            return result.toString()
        }

        maintainedSources.sortedBy(File::getPath).forEach { sourceFile ->
            val source = sourceFile.readText(Charsets.ISO_8859_1)
            source.lineSequence().forEachIndexed { index, line ->
                if (line.contains(generatedTodo) || line.contains(ideTemplate)) {
                    failures += "${sourceFile.relativeTo(rootProject.projectDir)}:${index + 1}: generated comment boilerplate"
                }
            }
            val isProductionSource = sourceFile.path.contains("${File.separator}src${File.separator}main${File.separator}java${File.separator}")
            if (isProductionSource) {
                stripCommentsAndLiterals(source).lineSequence().forEachIndexed { index, line ->
                    if (hungarianIdentifier.containsMatchIn(line)) {
                        failures += "${sourceFile.relativeTo(rootProject.projectDir)}:${index + 1}: active m_ identifier"
                    }
                }
            }
        }
        check(failures.isEmpty()) { "Source debt may not return:\n${failures.joinToString("\n") { "  - $it" }}" }
    }
}

tasks.register("aggregateCoverageCheck") {
    group = "verification"
    description = "Enforces the aggregate line-coverage ratchet"
    dependsOn(subprojects.map { "${it.path}:test" })
    dependsOn(subprojects.map { "${it.path}:jacocoTestReport" })

    doLast {
        val baseline = JsonSlurper().parse(rootProject.file("docs/project-quality-baseline.json")) as Map<*, *>
        val minimum = BigDecimal(baseline["aggregateLineCoverageMinimum"].toString())
        var covered = 0L
        var missed = 0L

        subprojects.forEach { subproject ->
            val report = subproject.layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile
            check(report.isFile) { "Missing JaCoCo XML report: ${report.absolutePath}" }
            val factory = DocumentBuilderFactory.newInstance()
            factory.isNamespaceAware = true
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
            factory.setFeature("http://xml.org/sax/features/validation", false)
            val document = factory.newDocumentBuilder().parse(report)
            val counters = document.documentElement.childNodes
            for (index in 0 until counters.length) {
                val node = counters.item(index)
                if (node.nodeName == "counter" && node.attributes?.getNamedItem("type")?.nodeValue == "LINE") {
                    covered += node.attributes.getNamedItem("covered").nodeValue.toLong()
                    missed += node.attributes.getNamedItem("missed").nodeValue.toLong()
                }
            }
        }

        val total = covered + missed
        check(total > 0) { "Aggregate JaCoCo report contains no executable lines" }
        val ratio = BigDecimal.valueOf(covered).divide(BigDecimal.valueOf(total), 8, RoundingMode.HALF_UP)
        logger.lifecycle(
            "Aggregate line coverage: $covered/$total " +
                "(${ratio.multiply(BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)}%)"
        )
        check(ratio >= minimum) {
            "Aggregate line coverage $ratio is below the ratcheted minimum $minimum"
        }
    }
}

tasks.register<JavaExec>("cpdReport") {
    group = "verification"
    description = "Generates the report-only PMD copy/paste detector baseline"
    classpath = pmdCpd
    mainClass.set("net.sourceforge.pmd.cli.PmdCli")

    val sourceDirectories = subprojects.flatMap { subproject ->
        listOf(
            subproject.layout.projectDirectory.dir("src/main/java").asFile,
            subproject.layout.projectDirectory.dir("src/test/java").asFile
        ).filter(File::isDirectory)
    }
    val reportFile = layout.buildDirectory.file("reports/pmd/cpd.xml")

    inputs.files(sourceDirectories)
    outputs.file(reportFile)
    doFirst {
        reportFile.get().asFile.parentFile.mkdirs()
        args(
            "cpd",
            "--minimum-tokens", "100",
            "--language", "java",
            "--no-fail-on-violation",
            "--encoding", "ISO-8859-1",
            "--format", "xml",
            "--report-file", reportFile.get().asFile.absolutePath,
            "--dir", sourceDirectories.joinToString(",") { it.absolutePath }
        )
    }
}

// The translated compatibility programs are generated fixtures. They remain
// compiled and tested, but are intentionally outside source-quality policy.
project(":naca-rt-tests") {
    tasks.named("spotbugsMain") {
        enabled = false
    }
}

tasks.named("check") {
    dependsOn("qualityCheck")
    dependsOn("sourceDebtCheck")
    dependsOn("aggregateCoverageCheck")
    dependsOn(":rewrite-recipes:test")
}
