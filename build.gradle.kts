/*
 * Naca - COBOL to Java Transpiler
 * Root Project Build Configuration
 * JDK 21 + Spring Boot 3.x
 */

plugins {
    java
    id("org.springframework.boot") version "3.4.3" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.diffplug.spotless") version "6.25.0" apply false
}

allprojects {
    group = "com.publicitas.naca"
    version = "2.0.0-SNAPSHOT"
    
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(23))
        }
        sourceCompatibility = JavaVersion.VERSION_23
        targetCompatibility = JavaVersion.VERSION_23
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
    }
}

tasks.register("allTests") {
    dependsOn(subprojects.map { it.tasks.named("test") })
    group = "verification"
    description = "Runs all tests across all subprojects"
}

apply(from = "cobol-tasks.gradle.kts")