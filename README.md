# Navigating Map of Denmark under Changing Sea Levels

**BFST2026 — First Year Project, Spring 2026**
**Group 20 — IT University of Copenhagen**

An interactive map application for exploring regions of Denmark with real-time
sea level simulation and dynamic route finding. Built as a first-year project
at ITU Copenhagen, Spring 2026.

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Build-Maven-blue)
![JUnit](https://img.shields.io/badge/Tests-JUnit5-green)

---

## Features

- **Map rendering** — Loads and renders OpenStreetMap data with distinct styling
  for road types, bodies of water, and other OSM features
- **Sea level simulation** — Raise the simulated sea level via a slider and watch
  affected areas update dynamically on the map
- **Route finding** — Compute the shortest route between two points using A*,
  with automatic rerouting when roads become submerged
- **Binary caching** — Raw `.osm` and `.hc` files are parsed once and saved as
  binary cache files, reducing subsequent startup times by ~27.8×

---

## Prerequisites

- Java 21 or later
- Maven 3.8 or later
- JavaFX 21 (bundled via Maven dependencies — no separate install needed)

---

## Getting Started

**1. Clone the repository**

    git clone https://github.itu.dk/SWU-FYP-Spring2026/BFST2026GroupNo20.git
    cd BFST2026GroupNo20

**2. Build the project**

    mvn clean install

**3. Run the application**

    mvn javafx:run

On launch a startup dialog will appear with three pre-loaded options:

- **Bornholm** — the primary dataset (588 km², 800,000+ elements)
- **Tunø** — smaller island, useful for quick testing
- **Samsø** — medium-sized island

Simply click one to load it. Alternatively, use the **"Load custom files"** option
in the dialog to load your own `.osm` and `.hc` files from disk.

Binary cache files (`.bin`) are generated automatically on first load of each
dataset and reused on subsequent launches for faster startup (~27.8× speedup).

---

## How to Use

| Action | How |
|---|---|
| Pan the map | Click and drag |
| Zoom | Scroll wheel and the `+`/`-` buttons in the toolbar  |
| Adjust sea level | Use the slider in the toolbar |
| Set route start/end | Press `S` to enter selection mode, then click once for start and once for end |
| Reset interface | Press `esc` to reset the interface and enter exploration mode |
| Load a different map | Use the file menu to open new `.osm` and `.hc` files |

---

## Project Structure

    src/
    ├── main/java/
    │   ├── models/RTree/          # R-Tree spatial index
    │   ├── models/pathfinding/    # Dijkstra and A* implementation
    │   ├── models/parser/         # OSM and height curve parsers, binary cache
    │   ├── models/geometry/       # AdaptivePath (RDP simplification), BoundingBox
    │   ├── models/heightcurve/    # Height curve tree and submergence simulation
    │   ├── models/rendering/      # Map rendering pipeline
    │   └── models/ui/             # JavaFX application and event handling
    ├── main/Resources/data/       # Bundled datasets (Bornholm, Tunø, Samsø)
    └── test/java/util/            # JUnit 5 test suite

---

## Running the Tests

    mvn test

The test suite uses JUnit 5 (Jupiter) throughout with no mocking framework.
Tests cover the R-Tree, pathfinder, height curve system, AdaptivePath (RDP),
and both parsers.

---

## Known Limitations

- **Tunø submergence** — The submergence simulation does not behave entirely
  correctly for Tunø due to a mismatch between the OSM and height curve ID
  systems. Bornholm and Samsø are unaffected.
- **Submerged road segments** — Submergence is checked at the node level only.
  A road segment whose endpoints are both above sea level will not trigger a
  reroute even if the segment itself passes through a flooded area.
- **Zip file support** — The parser reads plain `.osm` and `.hc` files only.
  Zipped files are not supported and must be unzipped before loading.

---

## Group Members

| Name | Email |
|---|---|
| Hannibal Munk | hmun@itu.dk |
| Daniel Kyhl | daky@itu.dk |
| Simon Skouboe | sgsk@itu.dk |
| Filip Jacobsen | fija@itu.dk |

---

## Built With

- [Java 21](https://openjdk.org/)
- [JavaFX 21](https://openjfx.io/)
- [Apache Maven](https://maven.apache.org/)
- [JUnit 5](https://junit.org/junit5/)
- [JMH](https://github.com/openjdk/jmh) — for benchmarking
