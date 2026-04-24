---
name: weekly-log-from-transcripts
description: Revise/create TA-facing weekly project diary entries with concrete programming progress, using the user's rundown as primary truth and transcripts as supporting evidence.
---

# Weekly Log From Transcripts

Use this skill to build or revise weekly logs so a TA can immediately see technical project progress.

The log must describe engineering progress, not just meeting/process activity.

## Primary Goal

Make each week answer these questions clearly:
1. What was implemented/integrated/refactored/tested?
2. What technical decisions were made?
3. What blockers or risks remain?
4. What coding/integration steps are next?

## When to Use

- The user shares transcript files from team meetings.
- The user asks for a weekly log, project diary update, or status summary.
- The project requires regular progress reporting.
- The existing weekly log is too process-focused and needs technical detail.

## Inputs

Ask for the following if not already provided:

- A quick user rundown of coding progress (required).
- Week(s) or date range to cover.
- Transcript files to include.
- Whether to append new weeks only or also rewrite older vague weeks (default: rewrite affected weeks).

If the user's quick rundown is missing, ask for it before writing the log.

## Source Priority (Strict)

Use this order when producing content:
1. User's weekly rundown (**authoritative**)
2. Meeting transcripts (**supporting evidence only**)
3. Unknown details explicitly marked as `Not discussed`

Transcripts must never override explicit user statements.

## Mandatory Output Requirements

- Keep entries concise and scan-friendly (typically 2-6 bullets per section).
- No fluff, no motivational language, no filler.
- Use concrete, verifiable technical wording.
- Focus on implementation outcomes, integration progress, testing/benchmark status, and technical blockers.
- Process-only bullets (e.g., "we had scrum") are not enough unless tied to a technical outcome.
- Do not infer completion from discussion alone in transcripts.
- Do not invent details that are not explicitly stated by the user or transcript evidence.
- If information is missing, write `Not discussed`.
- If existing weekly entries are vague/process-heavy, rewrite them to reflect real engineering progress.
- When transcript filenames are duplicated or mislabeled (e.g., wrong year), deduplicate and use content/date context.

## Required Section Structure (Per Week)

Use this exact order:

1. `Week of <YYYY-MM-DD to YYYY-MM-DD>`
2. `Completed`
3. `Decisions`
4. `Blockers`
5. `Next`

## Programming-Progress Focus (Mandatory)

In `Completed`, prioritize statements like:
- Implemented feature/data structure/algorithm
- Integrated branches/components
- Refactored shared models/interfaces
- Added/ran tests or set up test branches
- Started/finished benchmark setup
- Fixed concrete rendering/parsing/pathfinding issues

Prefer subsystem framing when useful:
- Rendering
- Data Layer
- Algorithms/Pathfinding
- Testing/Benchmarking

## Style Rules

- Prefer strong verbs: `implemented`, `integrated`, `refactored`, `tested`, `decided`, `blocked`.
- Avoid vague phrases like "worked on stuff" or "made progress".
- Keep each bullet factual and verifiable from provided sources.
- Keep each bullet to one sentence when possible.
- Do not include assumptions, interpretation-heavy claims, or speculation.

## Process Workflow

1. Collect/confirm the user's coding-progress rundown (required).
2. Identify transcript files for the target period.
3. Map transcripts to calendar weeks; remove duplicates/mirror files.
4. Extract verifiable engineering milestones by week.
5. Rewrite affected weeks (not only append) if existing entries underreport programming progress.
6. Draft each week with `Completed / Decisions / Blockers / Next`.
7. Ensure blockers and next steps are technical and specific.
8. Resolve conflicts by prioritizing the user's rundown.
9. Mark unknowns as `Not discussed`.
10. Final pass: ensure a TA can quickly judge implementation depth and remaining risk.

## Template

```md
## Week of <YYYY-MM-DD to YYYY-MM-DD>

Completed
- <implemented/integrated item with concrete scope>
- <tested/refactored/merged item>

Decisions
- <technical decision and short rationale>

Blockers
- <technical blocker and impact>
- <or `Not discussed`>

Next
- <next coding/integration action>
- <next testing/benchmarking action>
```

## Example

```md
## Week of 2026-03-30 to 2026-04-05

Completed
- Implemented relation renderer updates to handle multipolygons and avoid double-drawing ways in relations.
- Moved area ordering to shoelace-based calculations for more reliable polygon rendering.
- Reported Dijkstra functionally complete and shifted focus to correctness tests.
- Advanced R-tree implementation toward integration with rendering/parser branches.

Decisions
- Prioritized correctness tests before benchmarking pathfinding performance.

Blockers
- Pathfinding test coverage remained incomplete, delaying meaningful benchmark comparisons.

Next
- Finalize Dijkstra test cases (shortest path and adjacency behavior).
- Complete R-tree integration and align rendering queries with the shared bounding-box model.
```
