#!/bin/bash
echo "--- Port Check ---"
echo

check_port() {
    local port=$1
    local label=$2
    if lsof -i :"$port" &>/dev/null || ss -tuln 2>/dev/null | grep -q ":$port "; then
        echo "  [OPEN] Port $port ($label) is in use"
    else
        echo "  [FREE] Port $port ($label) is available"
    fi
}

check_port 3306 "MySQL"
check_port 8080 "HTTP/App Server"
check_port 443 "HTTPS"
check_port 8443 "HTTPS Alt"
check_port 1099 "RMI/JMX"
