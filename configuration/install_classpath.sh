#!/bin/bash
echo "--- Setting Classpath ---"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
JARS_DIR="$SCRIPT_DIR/../jars"

CP="."
for jar in "$JARS_DIR"/*.jar; do
    [ -f "$jar" ] && CP="$CP:$jar"
done

echo "CLASSPATH=$CP"
echo

# Write reusable helper
cat > "$SCRIPT_DIR/set_classpath.sh" <<EOF
#!/bin/bash
export CLASSPATH="$CP"
EOF
chmod +x "$SCRIPT_DIR/set_classpath.sh"

echo "Generated set_classpath.sh"
echo "Usage: source configuration/set_classpath.sh"
