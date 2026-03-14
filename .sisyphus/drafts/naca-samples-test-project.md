# Draft: NacaSamples Test Project Conversion

## Requirements (confirmed)
- Convert NacaSamples directory into a standalone Java project
- This project should serve as a test for the naca-trans translation engine
- The project contains COBOL files (.cbl) and their corresponding Java translations
- Should be structured as a proper Gradle Java project

## Current Structure Analysis
- **COBOL files**: `/NacaSamples/cobol/` contains BATCH1.cbl, CALLMSG.cbl, ONLINE1.cbl, ONLINM1.bms
- **Java files**: `/NacaSamples/src/` contains batch/ and online/ directories with translated Java files
- **Translation config**: `/NacaSamples/trans/` contains NacaTransRules.xml, NacaTransSamples.cfg, etc.
- **Current build**: No build configuration in NacaSamples (not included in main Gradle project)

## Technical Decisions Needed
- Should this be a separate Gradle project or integrated into the main naca project?
- What dependencies are needed? (naca-jlib, naca-rt, naca-trans?)
- Should it include both source COBOL files and generated Java files?
- What type of tests should be included? (unit tests, integration tests, comparison tests?)

## Open Questions
- Should this project be completely standalone or depend on the main naca modules?
- What is the primary purpose: testing the translation engine or testing the runtime?
- Should we include automated tests that compare COBOL output vs Java output?
- How should the project be named and structured?

## Scope Boundaries
- INCLUDE: Converting NacaSamples to proper Gradle Java project structure
- EXCLUDE: Modifying the main naca project structure
- EXCLUDE: Changing the existing COBOL or Java source files