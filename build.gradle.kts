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

apply(from = "cobol-tasks.gradle.kts")