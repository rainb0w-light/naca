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
        reports.create("html") {
            required.set(true)
            outputLocation.set(layout.buildDirectory.file("reports/spotbugs/${name}.html"))
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
    description = "Runs formatting gates and non-blocking static-analysis reports"
    val qualityTasks = listOf(
        "spotlessCheck",
        "checkstyleMain",
        "checkstyleTest",
        "pmdMain",
        "pmdTest",
        "spotbugsMain",
        "spotbugsTest"
    )
    dependsOn(subprojects.flatMap { subproject ->
        qualityTasks.map { taskName -> "${subproject.path}:$taskName" }
    })
    dependsOn("cpdReport")
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
