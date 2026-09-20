---
name: docmancer
description: Query local context, fetch public documentation, and retrieve API references using the docmancer CLI.
---

# Docmancer Skill

Docmancer indexes and compresses documentation context so coding agents spend tokens on implementation rather than rereading raw docs.

## Execution

Ensure `docmancer` is on PATH (e.g. `~/.local/bin/docmancer` on macOS/Linux or standard Python Scripts on Windows).

If custom configuration is needed, specify `--config <path_to_config>`.

### Common Invocation Examples

```bash
# General query
docmancer query "how to configure Ktor HttpClient in Kotlin Multiplatform"

# Expand surrounding context
docmancer query "Ktor ContentNegotiation JSON" --expand

# Ingest local documentation folder
docmancer ingest ./docs

# Add public docs URL
docmancer add https://ktor.io/docs/
```

## Core Commands Reference

- `docmancer setup`: Initialize docmancer environment.
- `docmancer ingest <dir>`: Index a directory of local documentation files.
- `docmancer add <url>`: Add and index a remote documentation URL.
- `docmancer update`: Refresh indexed documentation sources.
- `docmancer query "<text>"`: Search indexed documentation.
- `docmancer query "<text>" --limit <n>`: Limit number of returned chunks.
- `docmancer query "<text>" --expand`: Expand adjacent sections for full context.
- `docmancer query "<text>" --format json`: Output structured JSON results.
- `docmancer list`: List all indexed documentation sources.
- `docmancer inspect`: Inspect index metadata and statistics.
- `docmancer doctor`: Validate installation and dependencies.

## Usage Guidelines

- When external library or framework documentation is relevant, query docmancer first to obtain verified syntax and rules.
- Ground implementation details directly in retrieved documentation chunks.