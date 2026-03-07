/*
 * Naca - COBOL to Java Transpiler
 * COBOL Workflow Configuration
 * 
 * This file configures the COBOL compilation, execution, and comparison workflow.
 */

// COBOL Compiler Configuration
// Supported compilers: gnucobol, opensourcecobol4j

cobol {
    // Default compiler: GnuCOBOL (free, open-source)
    // Alternative: opensourcecobol4j (compiles COBOL to Java bytecode)
    compiler = "gnucobol"
    
    // GnuCOBOL settings
    gnucobol {
        // Installation path (auto-detected if in PATH)
        installPath = "/usr/local"
        
        // Compiler flags
        compileFlags = ["-free", "-x"]
        
        // Runtime library path
        libraryPath = "/usr/local/lib"
    }
    
    // OpenSourceCOBOL4J settings (COBOL to Java bytecode)
    opensourcecobol4j {
        // Path to opensourcecobol4j jar
        jarPath = ""
        
        // JVM options for running compiled COBOL
        jvmOptions = ["-Xmx512m"]
    }
    
    // Docker settings (alternative: run in container)
    docker {
        enabled = false
        image = "gnucobol/gnucobol:latest"
    }
}

// Sample COBOL programs for testing
samples {
    // Input directory containing COBOL source files
    inputDir = "NacaSamples/cobol"
    
    // Output directory for compiled executables
    outputDir = "build/cobol"
    
    // Transpiled Java output directory
    javaOutputDir = "build/transpiled"
    
    // Test results directory
    resultsDir = "build/results"
    
    // Sample programs
    programs = [
        "BATCH1",
        "ONLINE1",
        "CALLMSG"
    ]
}

// Test data configuration
testData {
    // Input files for COBOL programs
    inputFiles = "NacaSamples/testdata"
    
    // Expected output files
    expectedOutput = "NacaSamples/expected"
}