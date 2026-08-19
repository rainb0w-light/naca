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
    implementation("org.springframework.boot:spring-boot-starter-jdbc") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-validation") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")

    // Test - exclude default logging. The pinned COBOL parser engine currently shades Logback.
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testcontainers:junit-jupiter:1.20.6")
    testImplementation("org.testcontainers:postgresql:1.20.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.processResources {
    from(rootProject.file("naca-rt-tests/src/test/resources/carddemo/CAPABILITY_INVENTORY.json")) {
        into("carddemo/capabilities")
    }
}

// Environment-dependent acceptance suites run only through their dedicated tasks.
tasks.test {
    useJUnitPlatform {
        excludeTags("online-corpus-baseline", "carddemo-online-baseline", "carddemo-postgres")
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

tasks.register<Test>("cardDemoOnlineBaseline") {
    group = "verification"
    description = "CardDemo online fail-closed inventory for translated EXEC statements"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform {
        includeTags("carddemo-online-baseline")
    }
}

tasks.register<Test>("cardDemoPostgresAcceptance") {
    group = "verification"
    description = "Runs the CardDemo PostgreSQL/Flyway/health acceptance with Testcontainers"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    useJUnitPlatform {
        includeTags("carddemo-postgres")
    }
}
