#!/bin/sh
# Simple HTTP server to serve the WASM application
# The composeJsWasmMetadata build produces a WASM binary and an index.html

cd /app

# Start a simple HTTP server on port 8080
# This serves static files from the current directory
python3 -m http.server 8080 --directory /app