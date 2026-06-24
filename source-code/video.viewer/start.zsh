#!/bin/zsh
# Video Viewer — macOS startup script

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR/../.."
LIB_DIR="$PROJECT_ROOT/lib"
CLASS_DIR="$PROJECT_ROOT/build/classes"

MODULE_PATH="--module-path $LIB_DIR/javafx-sdk/lib --add-modules javafx.controls,javafx.web,javafx.media"

java $MODULE_PATH -cp "$CLASS_DIR:$LIB_DIR/*" video.viewer.Launcher "$@"
