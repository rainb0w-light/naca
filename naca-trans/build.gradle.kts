/*
 * NacaTrans - COBOL to Java Transpiler
 * Translates COBOL source code to Java
 * JDK 21 Compatible
 */

import java.util.zip.ZipFile

plugins {
    `java-library`
    application
}

description = "Naca Transpiler - COBOL to Java source code translator"

dependencies {
    // Internal dependency
    api(project(":naca-jlib"))
    
    // Apache Ant for build integration
    implementation("org.apache.ant:ant:1.10.15")
    
    // Logging - Log4j 2.x
    implementation("org.apache.logging.log4j:log4j-api:2.24.3")
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")
    implementation("org.apache.logging.log4j:log4j-1.2-api:2.24.3")

    // SnakeYAML for YAML configuration parsing
    implementation("org.yaml:snakeyaml:2.3")

    // StringTemplate 4 - Code generation template engine
    implementation("org.antlr:ST4:4.3.4")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testImplementation(project(":naca-rt"))
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

tasks.processTestResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

val javaTemplateResources = listOf(
    "templates/java/common/legacy.stg",
    "templates/java/common/common.stg",
    "templates/java/common/semantic-expressions.stg",
    "templates/java/cobol/control-flow.stg",
    "templates/java/cobol/data-operations.stg",
    "templates/java/cobol/declarations.stg",
    "templates/java/cobol/file-operations.stg",
    "templates/java/cobol/procedures.stg",
    "templates/java/cobol/roots.stg",
    "templates/java/cobol/verbs.stg",
    "templates/java/bms/actions.stg",
    "templates/java/bms/declarations.stg",
    "templates/java/bms/references.stg",
    "templates/java/bms/roots.stg",
    "templates/java/cics/cics.stg",
    "templates/java/fpac/fpac.stg",
    "templates/java/sql/sql.stg",
)

val jarTask = tasks.named<Jar>("jar")
tasks.register("templateJarCheck") {
    group = "verification"
    description = "Verifies every configured ST4 module is packaged in the transpiler JAR"
    dependsOn(jarTask)
    inputs.file(jarTask.flatMap { it.archiveFile })
    doLast {
        val archive = jarTask.get().archiveFile.get().asFile
        ZipFile(archive).use { jar ->
            javaTemplateResources.forEach { resource ->
                check(jar.getEntry(resource) != null) {
                    "Missing ST4 module in ${archive.name}: $resource"
                }
            }
            check(jar.getEntry("templates/java/java.stg") == null) {
                "Retired monolithic java.stg must not be packaged"
            }
            check(jar.getEntry("templates/base.stg") == null) {
                "Retired base.stg must not be packaged"
            }
        }
    }
}

tasks.register<JavaExec>("templateJarSmoke") {
    group = "verification"
    description = "Loads the complete ST4 catalog with naca-trans classes/resources from its JAR"
    dependsOn(jarTask, tasks.testClasses)
    mainClass.set("generate.templates.TemplateJarSmokeMain")
    classpath = files(
        sourceSets.test.get().output,
        jarTask.flatMap { it.archiveFile },
        configurations.testRuntimeClasspath)
}

tasks.named("check") {
    dependsOn("templateJarCheck", "templateJarSmoke")
}

// Architecture debt is now zero across COBOL/SQL/CICS, BMS and FPac. Keep the
// zero-tolerance contract in the daily regression gate as well as the explicit
// finalArchitectureCheck task so newly introduced debt fails immediately.
tasks.test {
    useJUnitPlatform {
        excludeTags("data-section-audit")
    }
}

tasks.register<Test>("finalArchitectureCheck") {
    group = "verification"
    description = "Runs the zero-tolerance ST4 final architecture contract"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform {
        includeTags("final-architecture")
    }
    jvmArgs("--add-opens=java.base/java.lang=ALL-UNNAMED")
    shouldRunAfter(tasks.test)
}

tasks.register<Test>("dataSectionAudit") {
    group = "verification"
    description = "Inventories real DATA SECTION entity types and their ST4 declaration-binding coverage"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform {
        includeTags("data-section-audit")
    }
    shouldRunAfter(tasks.test)
}

tasks.register<Test>("fpacAcceptance") {
    group = "verification"
    description = "Runs the canonical FPac parse, ST4 render, compile and runtime-boundary contract"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform()
    filter {
        includeTestsMatching("*FPacPipelineAcceptanceTest")
    }
    shouldRunAfter(tasks.test)
}



// Application plugin configuration
application {
    mainClass.set("NacaTrans")
}

// Custom task to run transpiler
tasks.register<JavaExec>("transpile") {
    group = "naca"
    description = "Run the COBOL to Java transpiler"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("NacaTrans")
    
    // Default arguments - can be overridden via command line
    if (project.hasProperty("configFile")) {
        args("-ConfigFile=${project.property("configFile")}")
    }
    if (project.hasProperty("inputDir")) {
        args("-InputDir=${project.property("inputDir")}")
    }
    if (project.hasProperty("outputDir")) {
        args("-OutputDir=${project.property("outputDir")}")
    }
}
