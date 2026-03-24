---
name: weekly-log-from-transcripts
description: Create short, concise weekly project log entries using the user's weekly rundown as the primary source and transcripts as supporting context.
---

# Weekly Log From Transcripts

Use this skill to create short weekly log entries for a university project.

Treat the user's own weekly rundown as the primary source of truth.
Use meeting transcripts only as supporting context for clarification and detail.

The output must be concise, concrete, and easy to scan.

## When to Use

- The user shares transcript files from team meetings.
- The user asks for a weekly log, status update, or summary.
- The project requires regular progress reporting.

## Inputs

Ask for the following if not already provided:

- A quick rundown from the user of what was done this week (required).
- Which week or date range the log should cover.
- Which transcript files to include.
- Preferred output format (bullet points or short paragraphs).

If the user's quick rundown is missing, ask for it before writing the log.

## Output Requirements

- Keep each weekly entry short (typically 4-8 bullets total).
- No fluff, no motivational language, no filler.
- Use plain, specific wording.
- Prioritize facts from the user's rundown.
- Use transcripts to support, disambiguate, or add context only.
- If transcript content conflicts with the user's rundown, follow the user's rundown and note uncertainty briefly if needed.
- Focus on:
  - Work completed
  - Decisions made
  - Blockers/risks
  - Next actions
- Do not infer completion from discussion alone in transcripts.
- Do not invent details that are not explicitly stated by the user or transcript evidence.
- If information is missing, write `Not discussed`.

## Source Priority

Use this order when generating entries:

1. User's weekly rundown (authoritative)
2. Meeting transcripts (context only)
3. Explicitly mark unknowns as `Not discussed`

Transcripts should never override the user's explicit weekly rundown.

## Recommended Entry Structure

Use this exact section order:

1. `Week of: <date range>`
2. `Completed`
3. `Decisions`
4. `Blockers`
5. `Next`

Keep each bullet to one sentence when possible.

## Style Rules

- Prefer strong verbs: "implemented", "tested", "decided", "blocked".
- Avoid vague phrases like "worked on stuff" or "made progress".
- Keep statements factual and verifiable from the provided sources.
- Do not include assumptions, interpretation-heavy claims, or speculation.

## Process

1. Collect the user's quick rundown for the week (required).
2. Identify transcript files relevant to the same week.
3. Extract concrete actions, decisions, blockers, and next steps from both sources.
4. Resolve conflicts by prioritizing the user's rundown.
5. Remove repetition and merge duplicate points.
6. Draft a compact weekly entry using the required structure.
7. Tighten wording until each bullet is direct and specific.

## Template

```md
Week of: <YYYY-MM-DD to YYYY-MM-DD>

Completed
- <specific completed item from user rundown, optionally supported by transcript context>
- <specific completed item>

Decisions
- <decision made, with brief context>

Blockers
- <blocker and impact>
  - or `Not discussed`

Next
- <next action with owner if known>
- <next action>
```

## Example

```md
Week of: 2026-03-10 to 2026-03-16

Completed
- Implemented parser handling for nodes and ways (from team weekly rundown).
- Split map rendering tasks across team members (confirmed by meeting transcript context).

Decisions
- Decided to prioritize MVP map interaction before UI polish.

Blockers
- JavaFX setup mismatch on one machine delayed local testing.

Next
- Fix JavaFX environment setup and rerun tests.
- Prepare TA meeting agenda with open architecture questions.
```
