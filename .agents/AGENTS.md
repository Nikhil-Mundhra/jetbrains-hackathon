# Global Agent Directives

**START HERE FOR EVERY REQUEST:** Before taking ANY action or answering ANY question, you MUST first explicitly evaluate if Graphify or Docmancer are relevant to the request:

1.  **Internal Map (Graphify):** Does this require understanding the codebase architecture, file locations, or what the next implementation steps are? If yes, check the `graphify-out/` graph or use Graphify (`/graphify query`, `/graphify path`) to orient yourself.
2.  **External Guide (Docmancer):** Does this involve external libraries, APIs, or specific framework rules? If yes, use Docmancer (`docmancer query`) to retrieve the correct syntax and rules. Only after you have established your internal and external bearings should you proceed with execution.

---

## Subagent Delegation (Parallel vs Sequential)

When assigned multiple features or bugs in a single request, you MUST decide between parallel delegation and sequential execution based on codebase overlap:

1.  **Parallel Delegation (`self` subagents):** Highly beneficial for naturally isolated tasks (e.g., one agent works on frontend UI, one works on the backend database, one writes documentation). Spawn multiple `self` subagents (using `invoke_subagent`) to tackle these simultaneously.
2.  **Sequential Execution (Single agent):** Detrimental for heavily intertwined tasks modifying the same files. For overlapping code changes, it is faster and safer to just knock them out sequentially yourself, ensuring you maintain the full context of the ongoing changes and avoid merge conflicts.

## 3. Execution Workflow

1.  **Analyze:** Understand the current state of the code using Graphify.
2.  **Plan:** Formulate a minimal-impact execution plan.
3.  **Execute:** Implement the changes. Do not leave placeholder comments (e.g., "TODO" or "insert logic here"). Write the complete implementation.
4.  **Verify:** Run the project's type-checker, linter, or build command to ensure your changes did not break the repository.
5. **Commit & Push:** After user confirmation, commit & push code to remote github repo. 

## 4. Code Standards

-   Match the existing architectural patterns, naming conventions, and paradigm of the codebase you are in.
-   Write robust error handling; do not swallow exceptions silently.
-   Remove any debugging statements (e.g., console.log, print) before finalizing the task.

## 5. Language Use

- Kotlin is the go-to language for frontend development across every platform.
- Python is the go-to language for a unified backend development

## 6. No Emoji Policy

- **Zero Unicode Emojis:** Do NOT use Unicode emojis in production UI, code strings, buttons, chips, logs, comments, or documentation headers.
- **SVG / Vector Drawables Only:** Use clean, professional vector icons (inline SVG in Web/HTML, Compose `ImageVector` or vector drawables in Kotlin) ONLY when functionally necessary for navigational or state affordance.
- **Eliminate Useless Emojis:** Remove all decorative, playful, or redundant emojis (e.g. rocket ships, sparkles, robot heads, food emojis) in favor of crisp typography, semantic badges, and enterprise-grade design standards.