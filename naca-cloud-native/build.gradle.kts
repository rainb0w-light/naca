plugins {
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    java
}

group = "com.publicitas.naca"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Internal Naca modules
    implementation(project(":naca-trans"))
    implementation(project(":naca-rt"))
    implementation(project(":naca-jlib"))
    implementation(project(":naca-analyzer"))

    // SMOJOL - COBOL interpreter from cobol-rekt
    implementation("org.smojol:smojol-core:1.0-SNAPSHOT")
    implementation("org.smojol:smojol-toolkit:1.0-SNAPSHOT")

    // LSP4COBOL Parser
    implementation("org.eclipse.lsp.cobol:parser:1.0-SNAPSHOT")
    implementation("org.eclipse.lsp.cobol:engine:1.0.0-SNAPSHOT")
    implementation("org.eclipse.lsp.cobol:common:1.0.8")

    // Graph visualization for CFG
    implementation("org.jgrapht:jgrapht-core:${rootProject.ext.get("jgraphtVersion")}")
    implementation("org.jgrapht:jgrapht-io:${rootProject.ext.get("jgraphtVersion")}")
    implementation("guru.nidi:graphviz-java:${rootProject.ext.get("graphvizVersion")}")

    // Functional programming
    implementation("io.vavr:vavr:${rootProject.ext.get("vavrVersion")}")

    // Spring Boot - exclude default logging (logback/log4j-to-slf4j)
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-actuator") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

    // Test - exclude default logging and use log4j-slf4j2-impl
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// The default test task must never regenerate the committed BATCH1 golden
// baseline; the one-off capture runs only via the dedicated task below. The
// online-corpus fail-closed baseline is a debt baseline (like
// finalArchitectureCheck): it is RED while EXEC statements are still silently
// dropped, so it runs only via its dedicated task, not the default gate.
tasks.test {
    useJUnitPlatform {
        excludeTags("baseline-capture", "online-corpus-baseline")
    }
}

tasks.register<Test>("batch1BaselineCapture") {
    group = "verification"
    description = "Regenerates the BATCH1 transpile golden baseline (one-off)"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform {
        includeTags("baseline-capture")
    }
}

tasks.register<Test>("onlineCorpusBaseline") {
    group = "verification"
    description = "ONLINE1 fail-closed inventory: fails on silently-dropped EXEC statements / missing includes"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform {
        includeTags("online-corpus-baseline")
    }
}
