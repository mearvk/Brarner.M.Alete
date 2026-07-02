#!/bin/bash
# Build script for Brarner.M.Alete — Linux
# Produces: Brarner.M.Alete.jar

set -e

JAR_NAME="Brarner.M.Alete.jar"
SRC_DIR="source-code"
OUT_DIR="build/classes"
LIB_DIR="jars"

mkdir -p "$OUT_DIR"

# Find all Java source files
find "$SRC_DIR" -name "*.java" > build/sources.txt

# Compile
CLASSPATH=$(find "$LIB_DIR" -name "*.jar" | tr '\n' ':')
javac -d "$OUT_DIR" -cp "$CLASSPATH" @build/sources.txt

# Package
jar cf "$JAR_NAME" -C "$OUT_DIR" .

echo "Built $JAR_NAME"
