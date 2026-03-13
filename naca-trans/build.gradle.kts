/*
 * NacaTrans - COBOL to Java Transpiler
 * Translates COBOL source code to Java
 * JDK 21 Compatible
 */

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

    // StringTemplate 4 - Code generation template engine
    implementation("org.antlr:ST4:4.3.4")

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

// Configure resource copying to handle duplicate files
tasks.processResources {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
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