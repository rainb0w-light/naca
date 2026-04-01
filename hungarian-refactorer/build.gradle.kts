plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.2"
    id("org.jetbrains.kotlin.jvm") version "1.9.21"
}

group = "com.example"
version = "1.0.0"

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public") }
    maven { url = uri("https://www.jetbrains.com/intellij-repository/releases") }
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")

    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.9.21")
}

intellij {
    version.set("2024.1")
    type.set("IU")  // IntelliJ IDEA Ultimate
    plugins.set(listOf(
        "com.intellij.java",  // Java 支持
        "Git4Idea"            // Git 支持（可选）
    ))
}

tasks {
    buildPlugin {
        archiveFileName.set("hungarian-refactorer-$version.zip")
    }

    runIde {
        // 调试模式配置
        systemProperty("idea.debug.mode", "true")
        jvmArgs("-Xmx2g")
    }

    // 无头模式运行配置
    register<JavaExec>("runHeadless") {
        dependsOn(buildPlugin)
        classpath = sourceSets["main"].runtimeClasspath
        mainClass.set("com.intellij.idea.Main")
        args = listOf(
            "headless",
            "--batch",
            "--plugin=${buildDir}/distributions/hungarian-refactorer-$version.zip"
        )
        systemProperty("idea.headless", "true")
    }

    patchPluginXml {
        sinceBuild.set("233")  // 2023.3+
        untilBuild.set("252.*")  // Support up to 2025.2.x
    }
}
