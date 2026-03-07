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
    
    testImplementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
}
sourceSets {
    main {
        java {
            srcDir("src/main/java")
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

// Configure test task to run CobolLikeIntegrationTests by default
tasks.named<Test>("test") {
    useJUnitPlatform()
    
    // Default to running CobolLikeIntegrationTests
    if (!project.hasProperty("testClass")) {
        include("com/publicitas/naca/tests/integration/CobolLikeIntegrationTests.class")
    }
    
    // Allow overriding with -PtestClass=ClassName
    if (project.hasProperty("testClass")) {
        val testClass = project.property("testClass") as String
        include("**/${testClass}.class")
    }
    
    // Allow running specific test method with -PtestMethod=methodName
    if (project.hasProperty("testMethod")) {
        val testMethod = project.property("testMethod") as String
        filter {
            includeTestsMatching("*$testMethod")
        }
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