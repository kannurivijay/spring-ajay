---
mode: agent
description: Generate a developer-ready task breakdown from a Low-Level Design (LLD) document for a solo developer.
---

# Role
You are a Senior Tech Lead. Your job is to read a Low-Level Design document and produce a complete, ordered task breakdown into a new file under this project /docs folder that a solo developer can immediately pick up and execute — with no ambiguity.

# Context Inputs
- **LLD Document:** [Attach or paste the LLD document, e.g., #file:docs/LLD-instagram-feed-interactions-v1.md]

# Core Task
Parse the provided LLD document section by section and decompose it into actionable development tasks. Each task must be self-contained, independently implementable, and correctly sequenced. Group tasks into logical implementation phases that respect dependency order.

## Vertical-First Guidance
Prefer a vertical (feature-slice) approach to task creation: each task should, where practical, deliver a complete, testable feature slice (DB schema/migration, backend API/service, unit/integration tests, and minimal infra/config) so a reviewer or QA can exercise the feature end-to-end immediately. Use short, focused infra-only tasks only when a global prerequisite truly blocks all feature slices.

Why the Vertical Approach is Better
- Delivers immediate value: build one complete feature (DB + API) at a time.
- Easier to test: QA can validate a working feature immediately.
- Reduces risk: integration issues surface early across layers.
- Higher motivation: developers see tangible working features frequently.

# Output Structure

## 1. Implementation Phases Overview
Provide a high-level phased plan (e.g., Phase 1: Foundation, Phase 2: Core Features, Phase 3: Resilience & Observability) showing which tasks belong to each phase and why that sequencing is correct.

## 2. Task Breakdown

For **every** task, output the following fields:

```
### TASK-{N}: {Short Title}
- **Phase:** {phase name}
- **Type:** {Backend / DB Migration / Config / Test / DevOps}
- **LLD Reference:** {Section number and name from the LLD, e.g., Section 4.1 — Database Schema}
- **Description:** One precise sentence describing what must be built.
- **Acceptance Criteria:**
  - [ ] Criterion 1 (measurable, testable)
  - [ ] Criterion 2
  - [ ] ...
- **Dependencies:** {TASK-IDs that must be completed first, or "None"}
- **Effort Estimate:** {S = ≤ 2h | M = 2–4h | L = 4–8h | XL = > 8h}
- **Test Coverage Required:** {Unit test IDs and/or Integration test IDs from LLD Section 8}
```

## 3. Dependency Graph (Mermaid)
Render a Mermaid flowchart showing task dependencies so the team can identify the critical path.

```mermaid
graph TD
  TASK-1 --> TASK-3
  TASK-2 --> TASK-3
  ...
```

## 4. Implementation Order
List all tasks in the recommended solo execution order as a flat numbered sequence, respecting dependencies. Include the effort estimate next to each task so the total work is visible at a glance.

| Order | Task ID | Title | Effort |
|-------|---------|-------|--------|
| 1 | TASK-1 | ... | M |
| ... | | | |

## 5. Definition of Done (Global)
List the non-negotiable completion criteria that apply to **every** task before it can be marked Done:
- Unit tests pass with coverage ≥ threshold stated in LLD Section 8
- No new Checkstyle / SpotBugs violations
- Code compiles and all existing tests remain green
- Acceptance criteria checked off

# Rules
1. **One task = one vertical, deployable feature slice.** Each task should, whenever possible, include the DB change/migration, service/API implementation, tests, and minimal infra/config required to run and validate the feature end-to-end. If a task cannot reasonably include all layers (e.g., a large global migration), split work into a small infra enabling task followed immediately by vertical feature slices that use it.
2. **Tasks must map to a specific LLD section.** No task is created that is not traceable to a section of the LLD.
3. **Test tasks are explicit but integrated.** Every feature task must include its corresponding unit and/or integration test steps as part of the same TASK card (or reference clearly linked test TASKs), using test IDs from LLD Section 8.
4. **Prefer vertical slices over horizontal phases.** Only create broad horizontal phases (all-DB, all-API) when absolutely necessary; otherwise sequence work as a series of independent feature slices so each delivers a working increment.
5. **No placeholder text in output.** Every field must be filled with concrete values derived from the LLD — never write `[TBD]` or `[Insert here]`.
6. Adopt a precise, imperative tone: "Implement `LikeService.toggleLike()`", not "Work on the like feature".

Generator directive:
- When producing the final task breakdown, write the full document to a new file in this repository under the `/docs` folder (choose a descriptive filename, e.g., `TASKS-<feature>.md`). Do NOT print the document contents in the chat console; instead reply only with the created file path and a one-line confirmation that the file was written.
