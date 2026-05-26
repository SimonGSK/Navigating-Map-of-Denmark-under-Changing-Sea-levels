# Weekly Project Log

## Week of 2026-02-23 to 2026-03-01

Completed

- Hannibal ran the first group kickoff meeting and walked through the full project requirements: OSM parsing, basic rendering, height curves, pathfinding (Dijkstra → A*), level of detail, dynamic rendering, and binary serialization.
- The group discussed technology choices (Java/JavaFX), set up a group Discord for communication, and created a shared GitHub repository.
- Each member picked a research area for the coming week: Hannibal on spatial data structures (R-tree), Filip and Sebastian on pathfinding algorithms, Simon and Daniel on rendering.

Decisions

- Start with Bornholm as the primary test map, expand to other maps later.
- Run weekly Tuesday Scrum meetings and use Discord recording with transcripts to build the project diary.
- Work in separate branches on GitHub and integrate through shared interfaces.

Blockers

- Scope was broad; benchmark strategy and testing approach were still undefined.

Next

- Share research findings and split work into concrete subsystem ownership.
- Prepare TA questions about testing, benchmarking, and data/tool setup.

## Week of 2026-03-02 to 2026-03-08

Completed

- First TA session with Marcus. Code of conduct reviewed and agreed as a group.
- Marcus endorsed the three-system split: Rendering (Simon + Daniel), Data Layer (Hannibal), Algorithms/Pathfinding (Filip + Sebastian).
- Marcus advised the team to prioritise getting something visual on screen quickly before worrying about architecture.
- Marcus shared a JavaFX template with panning and zooming already working, which the team could use as a starting point for the renderer.
- Marcus confirmed R-tree is a valid choice for the spatial index, introduced the Mark7 benchmarking framework for performance testing, and confirmed that using AI-assisted transcript summaries as the project diary was acceptable.

Decisions

- Start with a simple parser to unblock rendering quickly.
- Start pathfinding with Dijkstra first, then optimize to A* once Dijkstra is solid.
- Define shared data model interfaces early so the three tracks can work in parallel.
- Keep Tuesday Scrum every week.

Blockers

- No code written yet; all tracks still in research and planning phase.

Next

- Begin implementation: parser + first rendering output, initial Dijkstra scaffold, and R-tree research/prototyping.

## Week of 2026-03-09 to 2026-03-15

Completed

- Formal ownership assignments confirmed at the March 10 group meeting: Simon and Daniel own Rendering, Filip and Sebastian own Algorithms/Pathfinding, Hannibal owns the Data Layer.
- Hannibal set up the Maven project structure, `.gitignore`, and entry point, then created all core OSM domain model classes: `Coordinate`, `Element`, `Node`, `Way`, `Relation`, and `Member`, plus an initial `MBR` record for bounding boxes.
- Daniel started building the OSM parser; Simon set up the JavaFX rendering layer using code from earlier exercises for the scene setup with panning and zooming.
- The Bornholm OSM file was downloaded and loaded through the parser; the canvas with panning/zooming was up, but Bornholm was not yet being drawn — ways and relations still needed to be rendered.
- Filip and Sebastian started researching Dijkstra in parallel and began thinking about how to structure the algorithm.
- At the March 13 TA session, Marcus demonstrated Mark7 benchmarking code and the team sorted out GitHub SSH setup.

Decisions

- Keep a simple parser for early rendering, then evolve parser/data structures iteratively.
- Prioritise getting something visual on screen before optimising anything.

Blockers

- OSM data was loading but Bornholm was not yet rendering — the map canvas just showed a blank panning/zooming view.

Next

- Simon and Daniel to connect the parser output to the renderer so Bornholm draws on screen.
- Filip and Sebastian to start a first Dijkstra implementation.
- Hannibal to begin the R-tree skeleton and BoundingBox data model.

## Week of 2026-03-16 to 2026-03-22

Completed

- Simon and Daniel got Bornholm rendering for the first time, with tag-based colours for ways; coastline, land, and water areas were now distinguishable on screen. Drawing was outline-only at this stage.
- Sebastian had a basic Dijkstra running using a priority queue, though it still used raw integer distances rather than a proper adjacency list or weighted edges. Filip was researching Dijkstra and A* design to build on top of it.
- Hannibal presented the R-tree design to Marcus at the TA session on March 20. Marcus gave guidance on leaf-node capacity parameters, the quadratic splitting heuristic, and confirmed that ways belonging to a relation should not be stored separately in the tree.
- Hannibal continued designing the R-tree, working through the insert, search, `chooseLeaf`, and `adjustTree` algorithms.

Decisions

- Keep Dijkstra as the baseline; A* is a later optimisation once Dijkstra works correctly.
- Use own priority-queue-based approach rather than importing an external graph library.
- Subsystem ownership and branch-per-track workflow stays in place; integration through shared interfaces later.

Blockers

- Dijkstra had no adjacency list, no real edge weights, and no meaningful tests.
- R-tree was still in design phase; implementation not yet started.
- Rendering was outline-only; polygon fill was not yet working.

Next

- Sebastian and Filip to add adjacency list and weighted edges to Dijkstra, then write small manual tests.
- Simon and Daniel to work on polygon fill and multipolygon rendering.
- Hannibal to start coding the R-tree from the design worked out this week.

## Week of 2026-03-23 to 2026-03-29

Completed

- Filip worked on integrating the priority queue with Dijkstra and experimented with a Lazy Dijkstra variant to see if it would simplify the implementation.
- Sebastian walked the team through the current Dijkstra code at the Scrum, clarified what "relax" means in the algorithm (it is just the step where a shorter path distance replaces the current recorded distance), and discussed how to add a weighted graph on top.
- Simon continued improving rendering: Bornholm was drawing with tag-based colours and the team was working on filling polygons correctly and drawing larger elements first.
- Daniel worked with Simon on multipolygon fill cases where inner rings were leaving white unfilled patches.
- Hannibal continued R-tree implementation; a video Marcus shared turned out to be especially helpful for working through the split logic.

Decisions

- Use Euclidean distance as the initial edge-weight strategy — agreed between Filip, Sebastian, and Hannibal.
- Filip and Sebastian to each write a separate test so there is some peer review of the test logic.

Blockers

- Weighted edges were still not implemented; the test class only had a skeleton / boilerplate structure.
- Polygon fill still had rendering artifacts — some areas left unfilled.

Next

- Get a proper adjacency list and weighted edges working in Dijkstra.
- Simon and Daniel to fix multipolygon fill issues and refine tag-based colour handling.
- Hannibal to push toward completing the R-tree implementation.

## Week of 2026-03-30 to 2026-04-05

Completed

- Filip and Sebastian confirmed Dijkstra was functionally complete and shifted focus to writing tests. They planned to split the work: one testing shortest-path correctness, the other testing the adjacency list structure.
- Simon implemented a `RelationRenderer` for multipolygons and a `MapData` class that deduplicates elements so ways belonging to a relation are not drawn twice. Also added a `CoastlineRenderer`, though the team noted it could likely be consolidated into `RelationRenderer` later.
- Simon switched polygon area calculations from a simple min/max bounding-box approach to the shoelace formula, giving more accurate areas for sorting draw order (larger polygons drawn first).
- Daniel worked on rendering fixes — patching specific multipolygon fill cases where stitching inner rings was still leaving white patches — and integrated height curve classes from the earlier topographic exercise to lay the groundwork for height curve rendering.
- Hannibal was in the final stretch of the R-tree implementation and expected to finish it the same day.

Decisions

- Write correctness tests for Dijkstra before benchmarking — benchmarks are only meaningful once the algorithm is confirmed correct.
- Split Dijkstra testing responsibilities between shortest-path correctness and adjacency/data-structure correctness.

Blockers

- No Dijkstra tests had actually been committed yet; planning was complete but implementation was still pending.
- Some OSM tags were still causing incorrect polygon fill behaviour.

Next

- Filip and Sebastian to write and commit Dijkstra tests.
- Hannibal to finish and merge the R-tree; prepare integration branch with parser and rendering.
- Daniel to continue integrating height curve parsing and get a first rendering of elevation lines.

## Week of 2026-04-06 to 2026-04-12

Completed

- Daniel implemented the height curve renderer as a `Drawable`, adjusted the colour scheme for Bornholm's elevation range, and confirmed it was working. Height curves drew correctly on their own but were buried under other elements when everything was enabled together — a toggle was identified as the right solution.
- Hannibal completed the R-tree implementation and created an integration branch to merge the R-tree with the parser and rendering branches. During the merge, he also unified the coordinate representation by replacing the raw double arrays that Simon and Daniel had been using with his `BoundingBox` class.
- Filip, who was sick over Easter, created a pathfinding testing branch and drafted a test plan on paper: a grid-based approach for shortest-path correctness and weighted-node test cases.
- At the Scrum on April 7, Sebastian announced that he is leaving the software development programme and will not continue on the project. The remaining team — Hannibal, Simon, Daniel, and Filip — agreed to continue and to inform the TA.

Decisions

- Add a UI toggle to switch between the base OSM map and the height curve elevation view.
- Unify all coordinate/MBR representations through the `BoundingBox` class to reduce duplication across the codebase.

Blockers

- Pathfinding tests were still only planned, not yet implemented.
- R-tree and rendering branches were on separate tracks; the integration branch needed review and a pull request before merging into main.
- Team capacity reduced following Sebastian's departure.

Next

- Hannibal to merge the R-tree integration branch into main via pull request.
- Filip to start implementing the pathfinding tests from the plan drafted over Easter.
- Simon and Daniel to continue rendering fixes and testing.
- Inform the TA about the team-size change.

## Week of 2026-04-13 to 2026-04-19

Completed

- Hannibal merged the R-tree into the main branch via pull request and gave the team a live demo of the tree querying the viewport — ways and relations were now fetched from the R-tree on every redraw rather than from a flat list. A basic level-of-detail stub was also in place, filtering elements smaller than 16 pixels on screen.
- Hannibal identified that the current tree design had a structural problem: it held ways and relations in separate trees, which made nearest-neighbor search for pathfinding impossible without a full brute-force scan. A refactoring to a unified tree storing all element types was planned for the coming week.
- Daniel and Simon continued work on parser tests, but a build regression partway through the week prevented the app from running and slowed progress.
- Filip continued working through the benchmarking tooling. He was trying to get Mark7-style microbenchmarks working but found the setup confusing.
- The team discussed the rendering performance challenge posed by height curves at low zoom levels and agreed they should sit behind a toggle rather than always being drawn.

Decisions

- R-tree will be refactored into a unified structure holding nodes, ways, and relations so that nearest-neighbor search can query a single index.
- Height curves will be opt-in via a UI toggle rather than always rendered alongside the base map.

Blockers

- Nearest-neighbor search (needed to wire up click-to-select pathfinding) is blocked until the tree refactoring is done.
- Daniel and Simon's build issue needed resolving before testing could continue.
- Filip still hadn't found a working benchmarking approach.

Next

- Hannibal to start refactoring the tree for unified nearest-neighbor support.
- Daniel and Simon to fix build issue and resume test work.
- Filip to find a benchmarking setup that actually works on the project.

## Week of 2026-04-20 to 2026-04-26

Completed

- Hannibal continued restructuring the R-tree into a unified `SearchResults`-based design, eliminating the separate way-tree and relation-tree setup and folding the `MapData` deduplication logic into tree construction instead.
- Simon shipped several UI features: a toggle to switch between the OSM base map and the altitude/elevation view, a separate toggle to overlay height curves as lines on top of the base map, a sea-level slider that updates both views in real time, and a zoom-level indicator displayed using the log of the affine scale factor.
- Daniel worked through the test suite and got ten tests passing with two still failing; started scoping out what rendering tests would look like.
- Filip ran basic benchmark timings on a hand-constructed 5-node graph to validate that the Dijkstra and A* outputs were stable, and discussed with Hannibal when it would make sense to start benchmarking against real map data.
- At the TA meeting on April 24, the TA explained the two-phase nearest-neighbor algorithm — an initial coarse bounding-box pass to get an upper-bound distance, followed by a refined search within that radius — and demonstrated VisualVM for memory and CPU profiling.

Decisions

- Proper benchmarks should wait until nearest-neighbor is integrated and pathfinding can run on real OSM data.
- Filip to start studying the rendering and parsing code so he can understand how to visualize the computed path.

Blockers

- Nearest-neighbor still not implemented — blocked Dijkstra integration with the map.
- Daniel's two failing tests needed root-cause investigation before the test suite could be trusted.

Next

- Hannibal to finish tree refactoring and implement nearest-neighbor on top of it.
- Filip to read through Simon and Daniel's rendering code and understand the draw pipeline.
- Daniel to fix remaining test failures and plan rendering-side test coverage.

## Week of 2026-04-27 to 2026-05-03

Completed

- Hannibal completed a large architectural refactoring: the monolithic `App` class was split into `AppController` (startup and wiring), `AppData` (shared parsed state), `EventHandler` (mouse and keyboard input), and `UserInterface` (all JavaFX controls and layout). Both parsers were promoted to extend a shared `AbstractParser`. This was the prerequisite for being able to handle map click events cleanly enough to implement click-to-select pathfinding.
- Simon tracked down and fixed a rendering bug where a closed hiking-route `Way` inside a large multipolygon was being stitched in as if it were a hole, punching gaps in large forest areas. The fix was to check for a `boundary` type tag before treating closed paths as inner rings.
- Simon also added binary serialization: after the first load the app now writes all parsed data to a binary file, so subsequent launches skip XML parsing entirely and start up noticeably faster.
- At the TA meeting on May 1, the team gave a full demo of the app. Pathfinding click-to-select was almost working. The TA pointed out that the small island Tunø was not rendering correctly and suggested path caching as a next performance optimization.

Decisions

- Architecture refactoring scope kept tight — only what is needed to unblock click-to-select event handling.
- Binary serialization to be the default startup path once it is stable.

Blockers

- Tunø rendering bug flagged by TA; root cause not yet identified.
- Pathfinding UI still not fully wired up — needed the architecture refactor to land first, which it did this week.

Next

- Hannibal to wire up the full pathfinding UI end-to-end: click handlers, nearest-neighbor lookup, algorithm call, and route drawing.
- Simon to investigate the Tunø rendering issue.
- Daniel and Filip to push test coverage and get JMH benchmarking infrastructure in place.

## Week of 2026-05-04 to 2026-05-10

Completed

- Hannibal wired up the complete pathfinding flow: clicking the map in select mode now calls nearest-neighbor to snap to the closest road node, stores start and end in a `PathfindingObject` singleton, runs the selected algorithm, and draws the result via a `GraphicsRenderer`. Keyboard shortcuts (S to enter select mode, Escape to cancel) were added. The `OsmParser` was also extended to build the road graph during parsing, respecting `oneway` tags.
- Hannibal expanded nearest-neighbor to also collect nodes from the node lists inside `Way` and `Relation` objects, not just from direct tree leaf entries, which was needed to get good coverage across the full road network.
- Filip identified and fixed a critical bug in the A* implementation: the call to add the source node to the priority queue had been accidentally commented out, causing A* to silently return wrong results. Fixing that single line made the algorithm behave correctly.
- Daniel continued expanding the test suite, focusing on areas not yet covered: OSM element tag handling, edge weights, and graph construction.
- At the TA meeting on May 8, the team discussed the Tunø rendering issue (an unclosed `Way` preventing fill), shape caching as a major rendering performance win, and the 80% test-coverage target for the report.

Decisions

- `ShapeBuilder` to pre-compute `Path2D` objects for all elements during parsing, avoiding per-frame path rebuilding.
- One-way road logic to be covered by explicit tests.

Blockers

- Tunø rendering bug still not resolved — root cause was an unclosed way, but the fix needed careful handling.
- A* now correct, but needed new tests to confirm it stays correct.

Next

- Simon to implement `ShapeBuilder` shape caching and integrate `AdaptivePath` into renderers.
- Daniel to expand test coverage toward 80% and start JavaDoc.
- Filip to set up JMH properly with the Maven plugin and run first real benchmarks.
- Hannibal to continue performance work on the R-tree and search pipeline.

## Week of 2026-05-11 to 2026-05-17

Completed

- Hannibal focused on the search and rendering pipeline performance. Added area caching to `BoundingBox` so the same area value is not recalculated repeatedly during search and sort. Switched `SearchResults.sort()` from sequential to `Arrays.parallelSort()`, which gave roughly a 2.7× speedup on large result sets. Added zoom-level LOD pruning inside the R-tree itself: each `TreeNode` now tracks the minimum zoom level of its subtree, so entire branches are skipped at low zoom levels without visiting their leaves.
- Hannibal also refactored the OSM model: introduced `SpatialElement` as an interface and `OsmElement` as an abstract base class with lazy MBR computation, and added `HeightCurveData.search(BoundingBox)` so the renderer can query only the height curves visible in the current viewport rather than iterating all of them.
- Daniel replaced the boolean `isDijkstra` flag in the pathfinding API with a proper `Algorithm` enum (`DIJKSTRA` / `A_STAR`), making callsites readable and enabling parameterized JMH benchmarks. Daniel also removed the `Drawable` interface that had been sitting as an empty abstraction, and stripped the unused `draws(Graphics2D)` method from `Node`, `Way`, `Relation`, and `HeightCurve` — all drawing logic now lives exclusively in the dedicated renderer classes.
- Simon implemented `ShapeBuilder`, which pre-computes the `Path2D` for each element during parsing so it only has to be built once. Simon also integrated Daniel's `AdaptivePath` (a custom `Path2D` with a configurable pixel step) into the relation and height curve renderers and tuned the pixel step per element type for a good quality/performance balance.
- Filip upgraded the A* heuristic from a simple Euclidean distance approximation to haversine distance via the shared `UtilityTools` class, making the geographic distance estimates much more accurate.

Decisions

- 8192 identified as the optimal threshold for switching `SearchResults` to parallel sort — below that the parallelism overhead is not worth it, above it the speedup is consistent.
- `AppSettings` singleton introduced to gate height-curve rendering globally without threading flags through constructor chains.

Blockers

- JMH benchmarking infrastructure not yet in place — benchmarks were still manual timing runs.
- Tunø rendering bug still present.

Next

- Set up JMH with Maven plugin and start systematic benchmarks.
- JavaDoc pass across all classes.
- Fix Tunø rendering.

## Week of 2026-05-18 to 2026-05-26

Completed

- Hannibal set up the full JMH benchmarking framework: added the Maven JMH plugin, wrote `TreeBuildBenchmark` and `TreeSearchBenchmark`, and created `BenchmarkUtils` with shared setup code. Expanded coverage to seven benchmark categories including viewport-scaling queries, nearest-neighbor performance, tree vs. linear scan comparison, and filtering behaviour. Ran all benchmarks and confirmed 8192 as the optimal `SearchResults` parallel-sort threshold — at that size parallel sort is consistently 2× faster, below it the overhead eats the gain. Also fixed a bug in `TreeData.size()` that was returning the wrong count and causing the renderer to undercount elements.
- Filip set up a `PathfinderBenchmark` with proper JMH `@Benchmark` annotations, a `PathfinderBenchmarkRunner` to execute it and write CSV output, and `PathfinderBenchmarkUtils` to load real Bornholm OSM data. Contributed to the shared `AbstractTreeBenchmark` base class and co-authored `TreeBuildBenchmark` and `TreeSearchBenchmark`. Exported benchmark results to a spreadsheet comparing Dijkstra and A* across different graph sizes.
- Simon benchmarked the `AdaptivePath` pixel_step parameter across five configurations to quantify the quality/performance tradeoff and identify the best default values. Also ran benchmarks on binary file load times vs. parsing from the raw OSM file.
- Daniel completed a full JavaDoc pass across the core classes and wrote the remaining unit tests: `PathfinderTest`, `EdgeTest`, `GraphBuilderTest`, `OsmElementTest`, `RelationTest`, and `UtilityToolsTest` — bringing the total test suite to over 1200 lines.
- Simon wrote JavaDoc across the rendering and height curve classes.

Decisions

- `AdaptivePath` pixel step tuned to different default values for OSM elements and height curves based on benchmark results.
- Report structure finalized: introduction, data model, algorithms, testing and performance.

Blockers

- None major. Project is in final documentation and polish phase.

Next

- Complete the written report.
- Final review of all tests and benchmarks before submission.
