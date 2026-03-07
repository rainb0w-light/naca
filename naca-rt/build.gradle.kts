/*
 * NacaRT - Naca Runtime for Java Transcoded COBOL Programs
 * Runtime library for executing transpiled COBOL programs
 */
plugins {
    `java-library`
}

description = "Naca Runtime - Runtime library for transpiled COBOL programs"

dependencies {
    // Internal dependency
    api(project(":naca-jlib"))
    
    // Berkeley DB Java Edition
    implementation("com.sleepycat:je:18.3.12")
    
    // Jakarta Servlet
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")
    compileOnly("jakarta.servlet.jsp:jakarta.servlet.jsp-api:4.0.0")
    
    // Jakarta Mail
    implementation("com.sun.mail:jakarta.mail:2.0.1")
    
    // XML Processing
    implementation("xerces:xercesImpl:2.12.2")
    implementation("xml-apis:xml-apis:1.0.b2")
    
    // Apache Commons - Modern versions
    implementation("commons-beanutils:commons-beanutils:1.9.4")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("commons-digester:commons-digester:2.1")
    implementation("org.apache.commons:commons-fileupload2-jakarta:2.0.0-M1")
    implementation("commons-httpclient:commons-httpclient:3.1")
    implementation("commons-logging:commons-logging:1.3.4")
    implementation("org.apache.commons:commons-pool2:2.12.1")
    implementation("org.apache.commons:commons-dbcp2:2.13.0")
    
    // Apache Struts
    implementation("org.apache.struts:struts-core:1.3.10")
    
    // Jakarta Activation
    implementation("jakarta.activation:jakarta.activation-api:2.1.3")
    
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
            include("**/*.properties")
            include("**/*.xml")
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