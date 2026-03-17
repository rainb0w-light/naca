#!/bin/bash
# Direct wrapper to launch jdtls with specific JDK
JAVA_21_HOME="/opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk/Contents/Home"
JDTLS_HOME="/opt/homebrew/Cellar/jdtls/1.57.0/libexec"

# Find the equinox launcher jar
LAUNCHER_JAR=$(find "$JDTLS_HOME/plugins" -name "org.eclipse.equinox.launcher_*.jar" | head -1)

if [ -z "$LAUNCHER_JAR" ]; then
    echo "Error: Could not find equinox launcher jar"
    exit 1
fi

# Get shared config path based on platform
if [[ "$OSTYPE" == "darwin"* ]]; then
    CONFIG_DIR="config_mac"
else
    CONFIG_DIR="config_linux"
fi

SHARED_CONFIG="$JDTLS_HOME/$CONFIG_DIR"

# Build the Java arguments
exec "$JAVA_21_HOME/bin/java" \
    -Declipse.application=org.eclipse.jdt.ls.core.id1 \
    -Dosgi.bundles.defaultStartLevel=4 \
    -Declipse.product=org.eclipse.jdt.ls.core.product \
    -Dosgi.checkConfiguration=true \
    -Dosgi.sharedConfiguration.area="$SHARED_CONFIG" \
    -Dosgi.sharedConfiguration.area.readOnly=true \
    -Dosgi.configuration.cascaded=true \
    -Xms1G \
    --add-modules=ALL-SYSTEM \
    --add-opens java.base/java.util=ALL-UNNAMED \
    --add-opens java.base/java.lang=ALL-UNNAMED \
    -jar "$LAUNCHER_JAR" \
    "$@"
