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
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // Spring Boot Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    
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
            srcDir("../NacaSamples/src/batch")
            srcDir("../NacaSamples/src/commons")
            srcDir("../NacaSamples/src/commons/include")
            srcDir("../NacaSamples/src/online")
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
        }
    }
}

tasks.withType<ProcessResources> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Configure test task
tasks.named<Test>("test") {
    useJUnitPlatform()
    
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