/*
 * Naca - COBOL to Java Transpiler
 * Gradle Multi-Project Configuration
 * JDK 21 + Modern Dependencies
 */

rootProject.name = "naca"

// Module build order based on dependencies
// JLib has no dependencies -> build first
// NacaRT, NacaTrans depend on JLib
// NacaRTTests depends on JLib and NacaRT

include("naca-jlib")
include("naca-rt")
include("naca-trans")
include("naca-rt-tests")
include("naca-cloud-native")

// Project directory mapping
project(":naca-jlib").projectDir = file("naca-jlib")
project(":naca-rt").projectDir = file("naca-rt")
project(":naca-trans").projectDir = file("naca-trans")
project(":naca-rt-tests").projectDir = file("naca-rt-tests")
project(":naca-cloud-native").projectDir = file("naca-cloud-native")