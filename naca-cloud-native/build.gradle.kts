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

configurations.configureEach {
    exclude(group = "ch.qos.logback", module = "logback-classic")
    exclude(group = "org.apache.logging.log4j", module = "log4j-slf4j2-impl")
    exclude(group = "org.slf4j", module = "slf4j-simple")
}

dependencies {
    // Internal Naca modules
    implementation(project(":naca-trans"))
    implementation(project(":naca-rt"))
    implementation(project(":naca-jlib"))
    implementation(project(":naca-analyzer"))

    // Graph visualization for CFG
    implementation("org.jgrapht:jgrapht-core:${rootProject.ext.get("jgraphtVersion")}")
    implementation("org.jgrapht:jgrapht-io:${rootProject.ext.get("jgraphtVersion")}")
    implementation("guru.nidi:graphviz-java:${rootProject.ext.get("graphvizVersion")}")
    runtimeOnly("org.graalvm.polyglot:js-community:${rootProject.ext.get("graalJsVersion")}")

    // Functional programming
    implementation("io.vavr:vavr:${rootProject.ext.get("vavrVersion")}")

    // Spring Boot - exclude default logging (logback/log4j-to-slf4j)
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-actuator") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

    // Test - exclude default logging. The pinned COBOL parser engine currently shades Logback.
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Environment-dependent acceptance suites run only through their dedicated tasks.
tasks.test {
    useJUnitPlatform {
        excludeTags("online-corpus-baseline")
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
