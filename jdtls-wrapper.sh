#!/bin/bash
# Direct jdtls launcher with JDK 25
# Uses system default JDK

JDTLS_HOME="/opt/homebrew/Cellar/jdtls/1.57.0/libexec"
JAVA_HOME="/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home"

exec "$JAVA_HOME/bin/java" \
  -Djdk.xml.maxGeneralEntitySizeLimit=0 \
  -Djdk.xml.totalEntitySizeLimit=0 \
  -Declipse.application=org.eclipse.jdt.ls.core.id1 \
  -Dosgi.bundles.defaultStartLevel=4 \
  -Declipse.product=org.eclipse.jdt.ls.core.product \
  -Dosgi.checkConfiguration=true \
  -Dosgi.sharedConfiguration.area="$JDTLS_HOME/config_mac" \
  -Dosgi.sharedConfiguration.area.readOnly=true \
  -Dosgi.configuration.cascaded=true \
  -Xms1G \
  --add-modules=ALL-SYSTEM \
  --add-opens java.base/java.util=ALL-UNNAMED \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  -jar "$JDTLS_HOME/plugins/org.eclipse.equinox.launcher_1.7.100.v20251111-0406.jar" \
  -data "/Volumes/AppData/codebase/naca/.jdtls-workspace" \
  "$@"
