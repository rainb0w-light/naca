/*
 * NacaRTTests - Test Suite for NacaRT
 * Test programs for COBOL runtime verification
 * Spring Boot 3.x Compatible
 */

plugins {
    java
    `java-library`
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

description = "NacaRT Tests - Test suite for COBOL runtime verification"

dependencies {
    // Internal dependencies
    implementation(project(":naca-jlib"))
    implementation(project(":naca-rt"))
    
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-validation") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    
    // Spring Boot Test
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    testImplementation(project(":naca-cloud-native"))
    
    // Testing - JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    
    // Legacy JUnit 4 for compatibility (can be removed after full migration)
    implementation("junit:junit:4.13.2")
    
    
    
    // AssertJ for fluent assertions
    testImplementation("org.assertj:assertj-core:3.27.3")
    
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
}
sourceSets {
    main {
        java {
            srcDir("src/main/java")
            srcDir("src/main/translated-java")
        }
        resources {
            srcDir("Main")
            srcDir("src/main/resources")
            include("**/*.cfg")
            include("**/*.xml")
            include("**/*.properties")
            include("**/*.yaml")
            include("**/*.yml")
            include("**/*.out")
        }
    }
    test {
        java {
            srcDir("src/test/java")
        }
        resources {
            srcDir("src/test/resources")
            include("**/*.out")
            include("**/*.yaml")
            include("**/*.yml")
            include("**/*.properties")
            include("naca-samples/**")
        }
    }
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Configure test task
tasks.named<Test>("test") {
    useJUnitPlatform {
        excludeTags("legacy-runtime", "sample-acceptance")
    }
    
    // Test output configuration
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        showCauses = true
        showStackTraces = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
    
    // Fail on test failures but continue running all tests
    failFast = false
    
    // JaCoCo configuration for coverage
    finalizedBy(tasks.jacocoTestReport)
}

tasks.register<Test>("sampleAcceptance") {
    group = "verification"
    description = "Runs the canonical GnuCOBOL vs Naca/Javac/NacaRT acceptance pipeline"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    inputs.file(layout.projectDirectory.file(
        "src/test/resources/naca-samples/source/cobol/TEST-A-STANDALONE.cbl"))
    inputs.files(
        layout.projectDirectory.file("src/test/resources/naca-samples/source/cobol/BATCH1.cbl"),
        layout.projectDirectory.file("src/test/resources/naca-samples/source/cobol/CALLMSG.cbl"),
        layout.projectDirectory.file("src/test/resources/naca-samples/source/copybooks/MSGZONE"),
        layout.projectDirectory.file("src/test/resources/testdata/FILEIN.dat"))
    outputs.upToDateWhen { false }
    useJUnitPlatform {
        includeTags("sample-acceptance")
    }
    shouldRunAfter(tasks.test)
}

// The hand-written 2005 compatibility programs exercise runtime behavior that
// predates the ST4 migration and currently carry an explicit failure baseline.
// Keep them runnable as a strict, opt-in audit without making unrelated runtime
// debt indistinguishable from a transpiler/build regression.
tasks.register<Test>("legacyRuntimeTest") {
    group = "verification"
    description = "Run the strict legacy NacaRT compatibility suite (known runtime debt)."
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform {
        includeTags("legacy-runtime")
    }
    failFast = false
    shouldRunAfter(tasks.test)
}

// Configure JaCoCo to include coverage from dependencies
tasks.jacocoTestReport {
    dependsOn(tasks.test)
    
    // Include source sets from dependencies
    val nacaRt = project(":naca-rt")
    val nacaJlib = project(":naca-jlib")
    
    additionalClassDirs(files(nacaRt.layout.buildDirectory.dir("classes/java/main")))
    additionalClassDirs(files(nacaJlib.layout.buildDirectory.dir("classes/java/main")))
    
    additionalSourceDirs(files(nacaRt.layout.projectDirectory.dir("src/main/java")))
    additionalSourceDirs(files(nacaJlib.layout.projectDirectory.dir("src/main/java")))
    
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
        
        html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/test/html"))
        xml.outputLocation.set(layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml"))
    }
}

// Custom task to run test programs
tasks.register<JavaExec>("runTest") {
    group = "naca"
    description = "Run a test program (use -Pprogram=ProgramName)"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.publicitas.naca.NacaRtApplication")
    
    if (project.hasProperty("program")) {
        args("--program=${project.property("program")}")
    }
}

// Spring Boot main class configuration
springBoot {
    mainClass.set("com.publicitas.naca.NacaRtApplication")
}
