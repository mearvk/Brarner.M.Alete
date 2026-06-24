#!/bin/bash
# Video Viewer — Compile script (Linux)
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR/../.."
LIB_DIR="$PROJECT_ROOT/lib"
OUT_DIR="$PROJECT_ROOT/build/classes"

mkdir -p "$OUT_DIR"

echo "Compiling Video Viewer..."
javac -d "$OUT_DIR" -cp "$LIB_DIR/*:$LIB_DIR/javafx-sdk/lib/*" "$SCRIPT_DIR/Launcher.java" "$SCRIPT_DIR/VideoViewer.java"
echo "Video Viewer compiled successfully."
