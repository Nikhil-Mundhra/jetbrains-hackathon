FROM eclipse-temurin:21-jdk-alpine AS builder

# Install Gradle
ENV GRADLE_VERSION=8.4
RUN apk add --no-cache bash grep

# Set working directory
WORKDIR /home/app

# Copy gradle wrapper
COPY gradle/wrapper /home/app/gradle/wrapper

# Copy build files
COPY settings.gradle.kts /home/app/settings.gradle.kts
COPY composeApp/build.gradle.kts /home/app/composeApp/build.gradle.kts
COPY shared/build.gradle.kts /home/app/shared/build.gradle.kts

# Copy source code
COPY composeApp/src /home/app/composeApp/src
COPY shared/src /home/app/shared/src

# Build WASM using Compose Multiplatform
# Using gradle with compose plugin
RUN --mount=type=cache,target=/root/.gradle \
    gradle --no-daemon composeJsWasmMetadata

# Copy built WASM artifacts
FROM alpine:3.19 AS runtime

WORKDIR /app

# Install nginx for serving static files
RUN apk add --no-cache nginx

# Copy WASM output from builder stage
COPY --from=builder /home/app/composeApp/build/wasmJs/bin/main /app/

# Copy entrypoint script
COPY composeApp/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh

EXPOSE 8080

ENTRYPOINT ["/app/entrypoint.sh"]