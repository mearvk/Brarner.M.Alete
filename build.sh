#!/bin/bash
# Build script for Brarner.M.Alete — Linux/macOS
# Produces: Brarner.M.Alete.jar (the signal-processing engine + servers)
#
# Scope: this builds the core platform only — the shared engine (bma/),
# Main, the master BaseServer, and the per-module BaseServers. It deliberately
# excludes the optional ai/ (DJL) and video/ (JavaFX) trees, which pull in large,
# platform-specific native dependencies and are not part of the engine. Only the
# jars actually needed by the engine (jars/signal-processing/*) are placed on the
# classpath, so the build is deterministic and fast.

set -euo pipefail

JAR_NAME="Brarner.M.Alete.jar"
SRC_DIR="source-code"
OUT_DIR="build/classes"
LIB_DIR="jars/signal-processing"
MAIN_CLASS="Main"

mkdir -p "$OUT_DIR"

# Collect engine sources (exclude the optional ai/ and video/ subtrees).
find "$SRC_DIR" -name "*.java" \
    -not -path "$SRC_DIR/ai/*" \
    -not -path "$SRC_DIR/video/*" \
    > build/sources.txt

echo "Compiling $(wc -l < build/sources.txt) source file(s)..."

# Classpath: only the signal-processing dependencies the engine needs.
CLASSPATH=$(find "$LIB_DIR" -name "*.jar" | tr '\n' ':')
javac -d "$OUT_DIR" -cp "$CLASSPATH" @build/sources.txt

# Package with a runnable manifest (Main delegates to BaseServer).
jar cfe "$JAR_NAME" "$MAIN_CLASS" -C "$OUT_DIR" .

echo "Built $JAR_NAME (main class: $MAIN_CLASS)"
echo "Run with: java -cp \"$JAR_NAME:$CLASSPATH\" $MAIN_CLASS"
