#!/usr/bin/env bash
set -euo pipefail

echo "=== YallaPark Vercel Build ==="

# Check for existing Java 17+ or download portable JDK 21 in Vercel environment
if command -v java >/dev/null 2>&1 && java -version 2>&1 | grep -E -q 'version "(17|21|22)'; then
    echo "Found compatible Java: $(java -version 2>&1 | head -n 1)"
elif [ -d "/opt/homebrew/opt/openjdk@21" ]; then
    export JAVA_HOME="/opt/homebrew/opt/openjdk@21"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "Using Homebrew OpenJDK 21: $($JAVA_HOME/bin/java -version 2>&1 | head -n 1)"
else
    echo "Bootstrapping OpenJDK 21 for Vercel container..."
    mkdir -p /tmp/jdk21
    curl -sSL "https://api.adoptium.net/v3/binary/latest/21/ga/linux/x64/jdk/hotspot/normal/eclipse" -o /tmp/jdk21.tar.gz
    tar -xzf /tmp/jdk21.tar.gz -C /tmp/jdk21 --strip-components=1
    export JAVA_HOME="/tmp/jdk21"
    export PATH="$JAVA_HOME/bin:$PATH"
    echo "Installed JDK 21: $($JAVA_HOME/bin/java -version 2>&1 | head -n 1)"
fi

chmod +x gradlew
./gradlew :composeApp:wasmJsBrowserDistribution --no-daemon

echo "=== Build Complete ==="
