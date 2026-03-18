/*
 * Naca - COBOL to Java Transpiler
 * Gradle Build Script
 * JDK 21 + Modern Dependencies
 */

// Note: This script is applied via apply(from = ...) from build.gradle.kts
// The root project already has the java plugin applied in the main build file

// This file provides additional tasks for COBOL workflow

// ============================================
// COBOL Compilation Tasks
// ============================================

/**
 * Task to check if GnuCOBOL is installed
 */
tasks.register("checkCobolCompiler") {
    group = "cobol"
    description = "Check if COBOL compiler (GnuCOBOL) is installed"
    
    doLast {
        val compiler = project.findProperty("cobol.compiler")?.toString() ?: "gnucobol"
        println("Checking for COBOL compiler: $compiler")
        
        when (compiler) {
            "gnucobol" -> {
                val result = exec {
                    commandLine("which", "cobc")
                    isIgnoreExitValue = true
                }
                if (result.exitValue == 0) {
                    println("✓ GnuCOBOL (cobc) is installed")
                    exec {
                        commandLine("cobc", "--version")
                    }
                } else {
                    println("✗ GnuCOBOL (cobc) not found in PATH")
                    println("  Install with: brew install gnucobol (macOS)")
                    println("  Or: apt-get install gnucobol (Ubuntu/Debian)")
                }
            }
            "opensourcecobol4j" -> {
                println("OpenSourceCOBOL4J requires manual setup")
                println("  Download from: https://github.com/opensourcecobol/opensourcecobol4j")
            }
        }
    }
}

/**
 * Task to compile COBOL source files
 */
tasks.register("compileCobol") {
    group = "cobol"
    description = "Compile COBOL source files to executables"
    
    val inputDir = file("NacaSamples/cobol")   
    val outputDir = file("build/cobol")
    
    inputs.dir(inputDir)
    outputs.dir(outputDir)
    
    doLast {
        outputDir.mkdirs()
        
        val cobolFiles = fileTree(inputDir) {
            include("**/*.cbl")
            include("**/*.cob")
        }
        
        cobolFiles.forEach { cobolFile ->
            val baseName = cobolFile.nameWithoutExtension
            val outputFile = File(outputDir, baseName)
            
            println("Compiling: ${cobolFile.name}")
            
            exec {
                commandLine(
                    "cobc",
                    "-x",  // Create executable
                    "-o", outputFile.absolutePath,
                    cobolFile.absolutePath
                )
                isIgnoreExitValue = true
            }
        }
    }
}

/**
 * Task to run a COBOL program
 */
tasks.register("runCobol") {
    group = "cobol"
    description = "Run a compiled COBOL program (use -Pprogram=PROGRAM_NAME)"
    
    doLast {
        val programName = project.findProperty("program")?.toString()
            ?: throw GradleException("Please specify program name: -Pprogram=BATCH1")
        
        val executable = file("build/cobol/$programName")
        if (!executable.exists()) {
            throw GradleException("Executable not found: ${executable.absolutePath}. Run 'compileCobol' first.")
        }
        
        println("Running COBOL program: $programName")
        exec {
            commandLine(executable.absolutePath)
        }
    }
}

// ============================================
// COBOL to Java Transpilation Tasks
// ============================================

/**
 * Task to transpile COBOL to Java using NacaTrans
 */
tasks.register("transpileCobol") {
    group = "naca"
    description = "Transpile COBOL source files to Java using NacaTrans"
    
    val inputDir = file("NacaSamples/cobol")
    val outputDir = file("build/transpiled")
    
    inputs.dir(inputDir)
    outputs.dir(outputDir)
    
    dependsOn(":naca-trans:classes")
    
    doLast {
        outputDir.mkdirs()
        
        val cobolFiles = fileTree(inputDir) {
            include("**/*.cbl")
            include("**/*.cob")
        }
        
        cobolFiles.forEach { cobolFile ->
            val baseName = cobolFile.nameWithoutExtension
            println("Transpiling: ${cobolFile.name} -> ${baseName}.java")
            
            // NacaTrans uses a configuration file
            // For now, just show what would happen
            println("  Input: ${cobolFile.absolutePath}")
            println("  Output: ${File(outputDir, "$baseName.java").absolutePath}")
        }
    }
}

/**
 * Task to run transpiled Java program
 */
tasks.register("runTranspiled") {
    group = "naca"
    description = "Run a transpiled Java program (use -Pprogram=PROGRAM_NAME)"
    
    dependsOn(":naca-rt-tests:classes")
    
    doLast {
        val programName = project.findProperty("program")?.toString()
            ?: throw GradleException("Please specify program name: -Pprogram=BATCH1")
        
        println("Running transpiled Java program: $programName")
        javaexec {
            classpath = files(
                "NacaRTTests/bin",
                "NacaRT/bin",
                "JLib/bin"
            )
            mainClass.set("idea.entryPoint.OnlineMain")
            args("-Program=$programName")
            args("-DisableInitialDbConnection")
        }
    }
}

// ============================================
// Comparison and Testing Tasks
// ============================================

/**
 * Task to compare COBOL and Java execution results
 */
tasks.register("compareResults") {
    group = "naca"
    description = "Compare execution results between COBOL and Java versions"
    
    doLast {
        val programName = project.findProperty("program")?.toString()
            ?: throw GradleException("Please specify program name: -Pprogram=BATCH1")
        
        println("Comparing results for: $programName")
        
        val cobolOutput = file("build/results/cobol/${programName}.out")
        val javaOutput = file("build/results/java/${programName}.out")
        
        if (!cobolOutput.exists()) {
            println("  ✗ COBOL output not found: ${cobolOutput.absolutePath}")
            println("    Run 'runCobol' first")
        }
        
        if (!javaOutput.exists()) {
            println("  ✗ Java output not found: ${javaOutput.absolutePath}")
            println("    Run 'runTranspiled' first")
        }
        
        if (cobolOutput.exists() && javaOutput.exists()) {
            val cobolContent = cobolOutput.readText()
            val javaContent = javaOutput.readText()
            
            if (cobolContent == javaContent) {
                println("  ✓ Results match!")
            } else {
                println("  ✗ Results differ!")
                println("  COBOL output lines: ${cobolContent.lines().size}")
                println("  Java output lines: ${javaContent.lines().size}")
            }
        }
    }
}

/**
 * Task to run full test workflow
 */
tasks.register("testWorkflow") {
    group = "naca"
    description = "Run the complete COBOL-to-Java test workflow"
    
    dependsOn("checkCobolCompiler")
    dependsOn("compileCobol")
    dependsOn("transpileCobol")
    
    doLast {
        println("\n=====================================")
        println("COBOL Workflow Test Complete")
        println("=====================================")
        println("\nNext steps:")
        println("1. Run a COBOL program: ./gradlew runCobol -Pprogram=BATCH1")
        println("2. Run transpiled Java: ./gradlew runTranspiled -Pprogram=BATCH1")
        println("3. Compare results: ./gradlew compareResults -Pprogram=BATCH1")
    }
}

// ============================================
// Docker-based COBOL Environment
// ============================================

/**
 * Task to run COBOL in Docker (for systems without native COBOL)
 */
tasks.register("dockerCobol") {
    group = "cobol"
    description = "Run COBOL compilation in Docker container"
    
    doLast {
        println("Checking Docker availability...")
        exec {
            commandLine("docker", "--version")
        }
        
        println("\nTo use Docker for COBOL compilation:")
        println("  docker run --rm -v \"\$(pwd)/NacaSamples/cobol:/src\" gnucobol/gnucobol cobc -x /src/BATCH1.cbl")
    }
}