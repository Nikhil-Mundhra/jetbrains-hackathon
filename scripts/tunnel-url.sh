#!/usr/bin/env sh
set -eu

# Cloudflare Quick Tunnels print the public URL to the tunnel container logs.
# This command extracts the latest URL without exposing any credentials.
docker compose logs --no-log-prefix tunnel \
  | sed -n 's|.*\(https://[-a-z0-9]*\.trycloudflare\.com\).*|\1|p' \
  | tail -n 1
