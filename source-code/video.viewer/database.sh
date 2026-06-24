#!/bin/bash
# Video Viewer — Database setup (Linux/macOS)
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "Setting up Video Viewer database..."
if command -v mysql &>/dev/null; then
    mysql -u root -p < "$SCRIPT_DIR/database.sql"
else
    echo "MySQL not found. Install MySQL and re-run, or run database.sql manually."
fi
