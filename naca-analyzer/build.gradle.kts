/*
 * Naca Analyzer - COBOL Code Analysis Module
 * Integrates cobol-rekt's smojol toolkit for COBOL code analysis and interpretation
 * JDK 21 Compatible
 */

plugins {
    `java-library`
}

description = "Naca Analyzer - COBOL code analysis and interpretation using cobol-rekt/smojol"

dependencies {
    // Internal dependency
    api(project(":naca-jlib"))

    // SMOJOL Core - COBOL AST and interpreter from cobol-rekt
    implementation("org.smojol:smojol-core:1.0-SNAPSHOT")

    // SMOJOL Toolkit - Analysis toolkit from cobol-rekt
    implementation("org.smojol:smojol-toolkit:1.0-SNAPSHOT")

    // LSP4COBOL Parser - COBOL language parser from cobol-rekt
    implementation("org.eclipse.lsp.cobol:parser:1.0-SNAPSHOT")
    implementation("org.eclipse.lsp.cobol:common:1.0.8")
    implementation("org.eclipse.lsp.cobol:engine:1.0.0-SNAPSHOT")
    implementation("org.eclipse.lsp.cobol:dialect-idms:1.0-SNAPSHOT")

    // ANTLR4 Runtime - Required by LSP4COBOL parser
    implementation("org.antlr:antlr4-runtime:${rootProject.ext.get("antlrVersion")}")

    // Graph visualization - for code flow analysis
    implementation("org.jgrapht:jgrapht-core:${rootProject.ext.get("jgraphtVersion")}")
    implementation("org.jgrapht:jgrapht-io:${rootProject.ext.get("jgraphtVersion")}")

    // Functional programming utilities
    implementation("io.vavr:vavr:${rootProject.ext.get("vavrVersion")}")

    // Logging
    implementation("org.slf4j:slf4j-api:${rootProject.ext.get("slf4jVersion")}")
    implementation("ch.qos.logback:logback-classic:${rootProject.ext.get("logbackVersion")}")

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
