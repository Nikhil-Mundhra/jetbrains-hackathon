---
name: document-manipulation
description: Toolkit for converting and manipulating documents including docx to mdx/html and markdown to PDF.
---

# Document Manipulation Skill

This skill provides utilities for automated document conversions. When converting `.docx`, `.html`, or `.md` files, invoke the corresponding conversion workflow.

## Available Workflows

### 1. Markdown to PDF

Converts a markdown (`.md`) file to a formatted `.pdf` using headless Chromium or Google Chrome.

- **LaTeX / Mathematical Typesetting**: Support for inline math (`$0.9025$`, `$P_{95}$`) and display equations (`$$ ... $$`) via KaTeX auto-rendering.
- **Deterministic Headless Rendering**: Utilizes Chrome virtual time budget flags (`--virtual-time-budget=4000`, `--run-all-compositor-stages-before-draw`) to ensure typography and equations settle before snapshotting.

**Usage:**

```bash
python -m scripts.md_to_pdf --input "path/to/input.md" --output "path/to/output.pdf"
```

### 2. HTML to PDF

Converts a rendered `.html` file directly to a `.pdf` using headless Chrome with background color rendering (`--print-background`).

**Usage:**

```bash
python -m scripts.html_to_pdf --input "path/to/report.html" --output "path/to/report.pdf"
```

### 3. DOCX to HTML / MDX

Extracts HTML or MDX content from a `.docx` file using `mammoth` and `turndown`.

**Usage:**

```bash
npx mammoth path/to/input.docx --output-format=html > output.html
```