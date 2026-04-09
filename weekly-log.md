# Weekly Project Log

## Week of 2026-02-23 to 2026-03-01

Completed

- Held project kickoff and reviewed overall requirements and deliverables.
- Identified core implementation tracks: parser/data loading, rendering, spatial structures, pathfinding, and testing/benchmarking.
- Agreed that each member would research selected topics before starting deeper implementation.

Decisions

- Start with Bornholm first, then expand later.
- Use weekly scrum + transcripts to maintain the project diary.
- Work in separate branches on GitHub.

Blockers

- Scope details and benchmark/test strategy were still unclear this week.

Next

- Share research findings and split work into concrete subsystem ownership.
- Prepare TA questions about testing, benchmarking, and data/tool setup.

## Week of 2026-03-02 to 2026-03-08

Completed

- Reorganized the project into three systems: **Rendering**, **Data Layer**, and **Algorithms**.
- Assigned ownership per system so implementation could progress in parallel.
- Established branch workflow per workstream (e.g., parser/rendering/pathfinding).
- Continued TA alignment on interfaces and technical priorities.

Decisions

- Start with a simple parser to unblock rendering quickly.
- Start pathfinding with Dijkstra first, then optimize later (e.g., A*).
- Define shared interfaces early so teams do not block each other.
- Keep Tuesday scrum every week.

Blockers

- Team still needed clearer guidance on benchmarking and pathfinding test design.

Next

- Begin implementation: parser + first rendering output, initial Dijkstra scaffold, and R-tree research/prototyping.

## Week of 2026-03-09 to 2026-03-15

Completed

- Rendering team started parser work and got Bornholm rendering up with basic tag-based colors.
- Rendering path moved from outline-only drawing toward polygon filling and area-based draw ordering.
- Algorithm team began implementing Dijkstra structure and discussed edge/weight handling.
- Data-layer track continued R3/R-tree research and design alignment with rendering needs.

Decisions

- Keep a simple parser for early rendering, then evolve parser/data structures iteratively.
- Prioritize visible map output early, then optimize/clean up.
- Delay full dynamic rendering until base rendering + spatial structure are stable.

Blockers

- Complex polygons and missing tags caused rendering artifacts (unfilled or incorrectly filled areas).

Next

- Improve relation/polygon handling in rendering.
- Continue Dijkstra implementation and move toward concrete tests.

## Week of 2026-03-16 to 2026-03-22

Completed

- Algorithm team got a basic Dijkstra version running and created an initial basic test.
- TA session reviewed Dijkstra/priority-queue tradeoffs and testing considerations.
- Parser branch and rendering integration advanced (including way-renderer flow).
- Data-layer work progressed on R3/R-tree structure and split strategy.

Decisions

- Keep Dijkstra as the baseline and treat A* as a later optimization.
- Continue with own priority-queue-based approach for current pathfinding implementation.
- Keep subsystem ownership and integrate through shared interfaces.

Blockers

- Dijkstra still lacked full adjacency/weighted-edge coverage and robust tests.

Next

- Extend Dijkstra with adjacency/edge-weight handling and stronger tests.
- Continue R3 implementation and prepare for integration with rendering/parser branches.

## Week of 2026-03-23 to 2026-03-29

Completed

- Continued Dijkstra work with priority queue integration and Lazy Dijkstra experiments.
- Drafted/refined Dijkstra test-class structure and clarified relax/shortest-path logic.
- Rendering team reported Bornholm rendering running and continued fill/tag improvements.
- R3 implementation continued, with focus on edge integration and test setup strategy.

Decisions

- Use Euclidean distance as initial edge-weight strategy.
- Pair on Dijkstra cleanup and test approach.

Blockers

- Weighted-edge handling and test harness quality were still unstable mid-week.

Next

- Finalize Dijkstra edge handling and produce reliable correctness tests.
- Keep improving rendering for complex polygons/multipolygon fill cases.

## Week of 2026-03-30 to 2026-04-05

Completed

- Filip and Sebastian reported Dijkstra functionally complete and shifted focus to testing.
- Rendering team implemented relation renderer for multipolygons and avoided double drawing of ways in relations.
- Coastline renderer was implemented and evaluated for consolidation into relation renderer logic.
- Renderer area ordering moved to shoelace-formula-based calculations for more accurate polygon handling.
- Height-curve implementation was started and classes from earlier topographic work were integrated.
- R3 work reached final phase and was expected completed the same day.

Decisions

- Prioritize correctness tests before benchmarking pathfinding performance.
- Split testing responsibilities (shortest-path correctness vs adjacency/data-structure correctness).

Blockers

- Test coverage for both pathfinding and rendering was still limited.
- Some map elements/tags still produced incorrect fill behavior.

Next

- Implement Dijkstra/pathfinding tests and begin benchmarking once tests are stable.
- Finish R3 and continue height-curve integration.

## Week of 2026-04-06 to 2026-04-12

Completed

- Height-curve renderer was implemented as a drawable and verified on the map.
- Pathfinding testing branch was created, with grid-based and weighted test ideas drafted.
- R3 implementation was completed.
- Began integrating R3 with parser/rendering branches and refactored coordinate arrays toward a shared `BoundingBox` model.

Decisions

- Add UI-level toggle between base map and height-curve-focused visualization.
- Reuse R-tree/R3 structures (e.g., bounding box abstractions) across rendering/parser code to reduce duplication.

Blockers

- Full pathfinding test suite is still in progress.
- Migrating map data fully into the R-tree structure is taking longer due integration/spec alignment.
- Team capacity changed after Sebastian communicated he is leaving the program/project.

Next

- Complete map-data migration into R-tree-backed structures and align rendering queries.
- Finalize pathfinding tests, then start benchmarking.
- Inform TA about team-size change and any scope impact.
