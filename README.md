# ☕ Java Katas ☕

## What is it?

- Java is easy to write, but "Modern Java" is a different beast entirely. The difference between "legacy code running on a modern JVM" and "idiomatic Java 21" lies in using **immutability (Records)**, **composition (Streams)**, and **concurrency (Virtual Threads)** effectively.

- This repository is a collection of **Daily Katas**: small, standalone coding challenges designed to drill specific modern Java patterns into your muscle memory.

- The focus is on challenging oneself to solve common software engineering problems **without** falling back on imperative habits (e.g., `for` loops with mutable accumulators, nested `if (x != null)` checks, or `synchronized` blocks).

- Many developers have years of experience with Java 8 or 11. They often face two challenges when moving to Java 21+:
  - How do I stop writing "Lombok-heavy" mutable beans and switch to Records?
  - How do I refactor complex imperative logic into readable Stream chains?
  - How do I leverage Virtual Threads without breaking existing thread-local logic?

## Out of scope

- This is not intended to teach syntax (loops, classes) to beginners. It is not a "Learn Java in 24 Hours" course.

## How to Use This Repo

1. **Pick a Kata:** Navigate to any `XX-kata-yy` folder.
2. **Read the Challenge:** Open the `README.md` inside that folder. It defines the goal, the constraints (e.g., "No `for` loops allowed"), and the "idiomatic patterns" you must use.
3. **Solve It:** Open the project (Maven/Gradle) and write your solution.
4. **Reflect:** Compare your solution with the provided "Reference Implementation" (if available) to see how modern APIs can simplify the code.

## Folder Structure

```text
java-kata/
├── 01-functional-shift/                # Functional-shift kata group
│   └── 01-declarative-aggregator/      # Gradle subproject (:01-functional-shift:01-declarative-aggregator)
│       ├── README.md                   # Kata instructions
│       ├── build.gradle.kts            # Module build config (java-quality, test deps)
│       └── src/
│           ├── main/java/              # Production code
│           └── test/java/              # Unit/integration tests
├── build-logic/                        # Shared convention plugins for all modules
│   ├── build.gradle.kts                # Build for convention plugins
│   ├── settings.gradle.kts             # build-logic build settings
│   └── src/main/kotlin/
│           └── java-quality.gradle.kts # Precompiled convention plugin (id: java-quality)
├── config/                             # Quality and tooling configuration
│   └── checkstyle/
│       └── checkstyle.xml              # Checkstyle ruleset
├── gradle/                             # Gradle shared metadata
│   ├── libs.versions.toml              # Version catalog (deps/plugins)
│   └── wrapper/
│       ├── gradle-wrapper.jar          # Wrapper runtime
│       └── gradle-wrapper.properties   # Wrapper version/distribution URL
├── .vscode/                            # Editor settings
│   └── settings.json                   # Workspace VS Code config
├── AGENTS.md                           # Repo-specific agent instructions
├── FORMAT.md                           # Formatting guidelines/reference
├── README.md                           # Project overview and kata index
├── Taskfile.yml                        # Cross-platform task runner commands
├── build.gradle.kts                    # Root build orchestration
├── settings.gradle.kts                 # Root project/modules declaration
├── gradle.properties                   # Gradle properties
├── gradlew                             # Unix Gradle wrapper launcher
└── gradlew.bat                         # Windows Gradle wrapper launcher
```

## Contribution Guidelines

> TBU

## Katas Index (Grouped)

### 01) The Functional Shift (Streams & Lambdas)

Moving from imperative instruction to declarative pipelines. Focus on Collectors, reducing state, and readability.

- [x] [01 - The Declarative Aggregator (Grouping & Partitioning)](./01-functional-shift/01-declarative-aggregator)
- [ ] [03 - Custom Collectors (Beyond toList)](./01-functional-shift/03-custom-collectors)
- [ ] [05 - FlatMap & Optional Chaining](./01-functional-shift/05-flatmap-optional-chain)
- [ ] [09 - The Infinite Stream Generator](./01-functional-shift/09-infinite-stream-generator)
- [ ] [14 - Parallel Streams: When and How](./01-functional-shift/14-parallel-streams-benchmark)

---

### 02) Modern Concurrency (Loom & CompletableFuture)

Drills focused on Virtual Threads, Structured Concurrency, and non-blocking composition.

- [ ] [02 - The Virtual Thread Throttler (Semaphore vs Pool)](./02-modern-concurrency/02-virtual-thread-throttler)
- [ ] [07 - CompletableFuture Composition Chain](./02-modern-concurrency/07-completablefuture-composition)
- [ ] [10 - Structured Concurrency (Scope & Shutdown)](./02-modern-concurrency/10-structured-concurrency-scope)
- [ ] [17 - The Scatter-Gather Router (Timeout Handling)](./02-modern-concurrency/17-scatter-gather-router)

---

### 03) Data Modeling & Immutability (Records & Sealed Types)

Using Java 14-21 features to model domain logic strictly and concisely.

- [ ] [06 - The Immutable Domain (Records Refactoring)](./03-data-modeling/06-records-refactoring)
- [ ] [11 - Pattern Matching for Switch (State Machines)](./03-data-modeling/11-pattern-matching-switch)
- [ ] [12 - Sealed Classes as Domain Events](./03-data-modeling/12-sealed-classes-domain-events)

---

### 04) Error Handling & Resilience

Moving away from try-catch spaghetti toward functional error handling and resiliency patterns.

- [ ] [08 - The "Sneaky Throws" Wrapper (Exceptions in Streams)](./04-error-handling/08-sneaky-throws-streams)
- [ ] [19 - Retry with Exponential Backoff (Functional Approach)](./04-error-handling/19-retry-exponential-backoff)
- [ ] [20 - Result<T> Pattern (Avoiding Exceptions for Control Flow)](./04-error-handling/20-result-type-pattern)

---

### 05) Modern IO & Time

Efficient file handling and precise time calculations without legacy `Date` or `File` classes.

- [ ] [13 - The Memory-Efficient Directory Walker (NIO.2)](./05-io-time/13-nio2-directory-walker)
- [ ] [18 - Time-Zone Sensitive Scheduler (java.time)](./05-io-time/18-timezone-scheduler)

---

### 06) Testing & Architecture

Idiomatic testing patterns using JUnit 5, Mockito, and ArchUnit.

- [ ] [15 - Parameterized Tests & Dynamic Containers](./06-testing/15-parameterized-tests)
- [ ] [16 - ArchUnit: Enforcing Layer Boundaries](./06-testing/16-archunit-boundaries)

---

### 07) Advanced Katas

End-to-end challenges that represents complex architectural problems. These focus on API design, parsing, generics, and extensibility.

- [ ] [21 - The Money Kata](./07-evolutionary-katas/21-money-kata) (Precision, Allocation, Multi-Currency)
- [ ] [22 - Semantic Versioning (SemVer)](./07-evolutionary-katas/22-semver-kata) (Parsing, Comparable, Constraints)
- [ ] [23 - Cron Expression Parser](./07-evolutionary-katas/23-cron-expression-kata) (Temporal Logic, Prediction)
- [ ] [24 - LRU Cache (Generics & Concurrency)](./07-evolutionary-katas/24-lru-cache-kata) (Data Structures, Eviction Policies)
- [ ] [25 - The Rate Limiter Middleware](./07-evolutionary-katas/25-rate-limiter-kata) (Algorithms, Time Windows, API)
- [ ] [26 - Feature Flag System](./07-evolutionary-katas/26-feature-flag-kata) (Boolean Logic, Strategy Pattern)
- [ ] [27 - The Spreadsheet Cell](./07-evolutionary-katas/27-spreadsheet-cell-kata) (Recursion, DAGs, Parsing)
- [ ] [28 - Bowling Game Extended](./07-evolutionary-katas/28-bowling-game-extended) (State Management, Validation)
