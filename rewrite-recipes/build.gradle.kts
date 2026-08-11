plugins {
    `java-library`
}

description = "Naca-specific OpenRewrite recipes"

dependencies {
    implementation("org.openrewrite:rewrite-java:8.88.4")

    testImplementation("org.openrewrite:rewrite-test:8.88.4")
    testRuntimeOnly("org.openrewrite:rewrite-java-21:8.88.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
