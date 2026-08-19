plugins {
    `java-library`
}

description = "Naca-specific OpenRewrite recipes"

dependencies {
    implementation("org.openrewrite:rewrite-java:8.88.4")
    runtimeOnly("org.openrewrite:rewrite-java-21:8.88.4")

    testImplementation("org.openrewrite:rewrite-test:8.88.4")
    testRuntimeOnly("org.openrewrite:rewrite-java-21:8.88.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<JavaExec>("hungarianNotationBatch") {
    group = "code quality"
    description = "Audits or applies the String cs-prefix AST rename batch"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.publicitas.naca.HungarianNotationBatch")
    args(
        rootProject.projectDir.absolutePath,
        "naca-jlib/src/main/java/jlib/sql",
        "docs/quality-governance/string-hungarian-jlib-sql.json",
        providers.gradleProperty("rewriteApply").orElse("false").get()
    )
}
