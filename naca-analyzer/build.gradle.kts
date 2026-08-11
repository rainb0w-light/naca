import java.util.Properties

/*
 * Naca Analyzer - COBOL Code Analysis Module
 * Integrates cobol-rekt's smojol toolkit for COBOL code analysis and interpretation
 * JDK 21 Compatible
 */

plugins {
    `java-library`
}

description = "Naca Analyzer - COBOL code analysis and interpretation using cobol-rekt/smojol"

configurations.configureEach {
    exclude(group = "ch.qos.logback", module = "logback-classic")
    exclude(group = "org.slf4j", module = "slf4j-simple")
}

val cobolRekt = Properties().apply {
    rootProject.file("gradle/cobol-rekt.properties").inputStream().use { load(it) }
}

dependencies {
    // Internal dependency
    api(project(":naca-jlib"))

    // SMOJOL Core - COBOL AST and interpreter from cobol-rekt
    implementation("org.smojol:smojol-core:${cobolRekt.getProperty("smojolVersion")}")

    // SMOJOL Toolkit - Analysis toolkit from cobol-rekt
    implementation("org.smojol:smojol-toolkit:${cobolRekt.getProperty("smojolVersion")}")

    // LSP4COBOL Parser - COBOL language parser from cobol-rekt
    implementation("org.eclipse.lsp.cobol:parser:${cobolRekt.getProperty("parserVersion")}")
    implementation("org.eclipse.lsp.cobol:common:${cobolRekt.getProperty("commonVersion")}")
    implementation("org.eclipse.lsp.cobol:engine:${cobolRekt.getProperty("engineVersion")}")
    implementation("org.eclipse.lsp.cobol:dialect-idms:${cobolRekt.getProperty("dialectIdmsVersion")}")

    // ANTLR4 Runtime - Required by LSP4COBOL parser
    implementation("org.antlr:antlr4-runtime:${rootProject.ext.get("antlrVersion")}")

    // Graph visualization - for code flow analysis
    implementation("org.jgrapht:jgrapht-core:${rootProject.ext.get("jgraphtVersion")}")
    implementation("org.jgrapht:jgrapht-io:${rootProject.ext.get("jgraphtVersion")}")

    // Functional programming utilities
    implementation("io.vavr:vavr:${rootProject.ext.get("vavrVersion")}")

    // Logging
    implementation("org.slf4j:slf4j-api:${rootProject.ext.get("slf4jVersion")}")
    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:${rootProject.ext.get("junitVersion")}")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
    main {
        java {
            srcDir("src/main/java")
        }
        resources {
            srcDir("src/main/resources")
        }
    }
    test {
        java {
            srcDir("src/test/java")
        }
        resources {
            srcDir("src/test/resources")
        }
    }
}

// Configure resource copying to handle duplicate files
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val verifyCobolRektLock by tasks.registering {
    group = "verification"
    description = "Verifies resolved cobol-rekt coordinates against the pinned source lock"
    inputs.file(rootProject.file("gradle/cobol-rekt.properties"))
    doLast {
        val expected: Map<String, String> = mapOf(
            "org.smojol:smojol-core" to cobolRekt.getProperty("smojolVersion"),
            "org.smojol:smojol-toolkit" to cobolRekt.getProperty("smojolVersion"),
            "org.eclipse.lsp.cobol:parser" to cobolRekt.getProperty("parserVersion"),
            "org.eclipse.lsp.cobol:engine" to cobolRekt.getProperty("engineVersion"),
            "org.eclipse.lsp.cobol:dialect-idms" to cobolRekt.getProperty("dialectIdmsVersion"),
            "org.eclipse.lsp.cobol:common" to cobolRekt.getProperty("commonVersion")
        )
        val resolved = configurations.runtimeClasspath.get()
            .resolvedConfiguration.resolvedArtifacts.associate { artifact ->
                "${artifact.moduleVersion.id.group}:${artifact.name}" to
                    artifact.moduleVersion.id.version
            }
        expected.forEach { (coordinate, version) ->
            check(resolved[coordinate] == version) {
                "Expected $coordinate:$version but resolved ${resolved[coordinate]}"
            }
        }
    }
}

tasks.named("check") {
    dependsOn(verifyCobolRektLock)
}
