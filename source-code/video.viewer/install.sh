#!/bin/bash
# Video Viewer — Linux install script
echo "============================================"
echo " Video Viewer - Linux Install"
echo "============================================"
echo

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR/../.."
LIB_DIR="$PROJECT_ROOT/lib"

echo "[1/3] Checking Java..."
if ! command -v java &>/dev/null; then
    echo "ERROR: Java not found. Install Java 11+."
    exit 1
fi
java -version
echo

echo "[2/3] Checking JavaFX SDK..."
if [ ! -d "$LIB_DIR/javafx-sdk" ]; then
    echo "WARNING: JavaFX SDK not found at $LIB_DIR/javafx-sdk"
    echo "Download from https://openjfx.io and extract to $LIB_DIR/javafx-sdk"
else
    echo "JavaFX SDK found."
fi
echo

echo "[3/3] Setting up database..."
bash "$SCRIPT_DIR/database.sh"
echo

echo "============================================"
echo " Video Viewer install complete."
echo "============================================"
