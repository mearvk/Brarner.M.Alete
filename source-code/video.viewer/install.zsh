#!/bin/zsh
# Video Viewer — macOS install script
echo "============================================"
echo " Video Viewer - macOS Install"
echo "============================================"
echo

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR/../.."
LIB_DIR="$PROJECT_ROOT/lib"

if [[ "$OSTYPE" != "darwin"* ]]; then
    echo "WARNING: This script is intended for macOS."
    exit 1
fi

echo "[1/3] Checking Java..."
if ! command -v java &>/dev/null; then
    echo "Java not found. Installing via Homebrew..."
    brew install openjdk@11
fi
java -version
echo

echo "[2/3] Checking JavaFX SDK..."
if [ ! -d "$LIB_DIR/javafx-sdk" ]; then
    echo "WARNING: JavaFX SDK not found at $LIB_DIR/javafx-sdk"
    echo "Install via: brew install --cask openjfx"
    echo "Or download from https://openjfx.io and extract to $LIB_DIR/javafx-sdk"
else
    echo "JavaFX SDK found."
fi
echo

echo "[3/3] Setting up database..."
zsh "$SCRIPT_DIR/database.sh"
echo

echo "============================================"
echo " Video Viewer macOS install complete."
echo "============================================"
