plugins {
    `java-library`
}

description = "Naca-specific OpenRewrite recipes"

dependencies {
    implementation("org.openrewrite:rewrite-java:8.88.4")
    implementation("org.openrewrite.recipe:rewrite-static-analysis:2.40.0")
    runtimeOnly("org.openrewrite:rewrite-java-21:8.88.4")
    runtimeOnly("org.openrewrite:rewrite-kotlin:8.88.4")

    testImplementation("org.openrewrite:rewrite-test:8.88.4")
    testRuntimeOnly("org.openrewrite:rewrite-java-21:8.88.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.register<JavaExec>("needBracesBatch") {
    group = "code quality"
    description = "Audits or applies the AST control-flow braces batch"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.publicitas.naca.NeedBracesBatch")
    args(
        rootProject.projectDir.absolutePath,
        "docs/quality-governance/checkstyle-need-braces-batch.json",
        providers.gradleProperty("rewriteApply").orElse("false").get(),
        "need-braces",
        "naca-analyzer/src/main/java",
        "naca-analyzer/src/test/java",
        "naca-cloud-native/src/main/java",
        "naca-cloud-native/src/test/java",
        "naca-jlib/src/main/java",
        "naca-jlib/src/test/java",
        "naca-rt/src/main/java",
        "naca-rt/src/test/java",
        "naca-rt-tests/src/main/java",
        "naca-rt-tests/src/test/java",
        "naca-trans/src/main/java",
        "naca-trans/src/test/java",
        "rewrite-recipes/src/main/java",
        "rewrite-recipes/src/test/java"
    )
}

tasks.register<JavaExec>("camelCaseBatch") {
    group = "code quality"
    description = "Audits or applies AST local-variable and private-field camel-case recipes"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.publicitas.naca.NeedBracesBatch")
    args(
        rootProject.projectDir.absolutePath,
        "docs/quality-governance/checkstyle-camel-case-batch.json",
        providers.gradleProperty("rewriteApply").orElse("false").get(),
        "all-variable-names",
        "naca-analyzer/src/main/java",
        "naca-analyzer/src/test/java",
        "naca-cloud-native/src/main/java",
        "naca-cloud-native/src/test/java",
        "naca-jlib/src/main/java",
        "naca-jlib/src/test/java",
        "naca-rt/src/main/java",
        "naca-rt/src/test/java",
        "naca-rt-tests/src/main/java",
        "naca-rt-tests/src/test/java",
        "naca-trans/src/main/java",
        "naca-trans/src/test/java",
        "rewrite-recipes/src/main/java",
        "rewrite-recipes/src/test/java"
    )
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
