# Project Agent Instructions

## Scope

These instructions apply to the repository rooted at this file. Keep this file lightweight; load detailed project documents only when the task needs them.

## Project Map

- `backend/`: Spring Boot 3.5, Java 17, Gradle, MyBatis backend.
- `frontend/`: static HTML/CSS/JS prototype pages and local browser assets.
- `artifact/docs/`: project documentation, requirements, API contracts, DB design, backend notes, and submission material.
- `artifact/raw/`: original assignment PDFs and raw reference files.
- `.codex/logs/`: project work logs.

## Lazy Loading Guide

Open only the relevant document set for the current task:

- Project orientation: `artifact/docs/README.md`, then `artifact/docs/00_project/project-overview.md`.
- Requirements or scope checks: `artifact/docs/01_requirements/functional-requirements.md`, `artifact/docs/01_requirements/non-functional-requirements.md`.
- API behavior: `artifact/docs/05_api/api-spec.md` and, for frontend consumers, `artifact/docs/11_frontend-roadmap/frontend-api-contract.md`.
- Backend architecture: `artifact/docs/06_backend/backend-architecture.md`, `artifact/docs/06_backend/package-structure.md`, `artifact/docs/06_backend/error-handling.md`.
- Database work: `artifact/docs/04_database/schema.sql`, `artifact/docs/04_database/table-spec.md`, `artifact/docs/04_database/erd.md`.
- Frontend screen work: `artifact/docs/03_ui/screen-list.md`, `artifact/docs/03_ui/screen-flow.md`, `artifact/docs/03_ui/wireframe.md`.
- Algorithm, batch, or submission work: load only the matching folder under `artifact/docs/07_algorithm/`, `artifact/docs/10_batch/`, or `artifact/docs/09_submission/`.

## Commands

- Backend tests: run from `backend/` with `./gradlew test`.
- Backend app: run from `backend/` with `./gradlew bootRun`.
- Static frontend pages can be inspected directly from `frontend/*.html`; start a local server only when browser behavior requires HTTP.

## Working Rules

- Follow the global Codex rules from `~/.codex/AGENTS.md`; do not duplicate them here.
- Ask one short question before acting only when the choice changes scope or outcome.
- Preserve existing user changes. Do not reset or remove unrelated files.
- Keep edits scoped to the requested feature, fix, or documentation update.
- For frontend UI changes, apply `~/.codex/rules/frontend-design-codex.md` before implementation and verify in a browser.
- For Korean prose, apply `~/.codex/rules/ai-tell-taxonomy.md` and `~/.codex/rules/rewriting-playbook.md`.
- For OpenAI product/API questions, use the registered `openaiDeveloperDocs` MCP server first; if unavailable, search only official OpenAI domains.

Append entries as:

```markdown
## YYYY-MM-DD — [short title]

[content]
```
