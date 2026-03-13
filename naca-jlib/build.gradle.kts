/*
 * JLib - Publicitas Java Library
 * Base library for NacaRT and NacaTrans
 */
plugins {
    `java-library`
}

description = "Publicitas Java Library - Base library for Naca runtime and transpiler"

dependencies {
    // XML Processing - Xerces (modern version)
    implementation("xerces:xercesImpl:2.12.2")

    // Apache Commons - Modern versions
    implementation("commons-codec:commons-codec:1.16.1")
    implementation("commons-io:commons-io:2.16.1")
    implementation("commons-net:commons-net:3.11.1")

    // Jakarta Mail (replacement for javax.mail)
    implementation("com.sun.mail:jakarta.mail:2.0.1")

    // Servlet API (provided scope - for compilation only)
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")
    testCompileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")

    // SLF4J Logging - Modern logging facade
    implementation("org.slf4j:slf4j-api:2.0.16")

    // Logging - Log4j 2.x (modern replacement)
    implementation("org.apache.logging.log4j:log4j-api:2.24.3")
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")
    // Log4j 1.x bridge for backward compatibility
    implementation("org.apache.logging.log4j:log4j-1.2-api:2.24.3")
    // Log4j 2.x SLF4J binding
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.24.3")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
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