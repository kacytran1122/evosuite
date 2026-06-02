# EvoSuite Tutorials - CS 395 Software Testing

**Student:** Kacy Tran  
**Course:** CS 395 - Software Testing  
**Assignment:** EvoSuite Tutorials 1-4 (200% credit)

---

## Repository Structure

```
evosuite/
├── Tutorial_Stack/          Tutorial 1 - Command Line
├── Tutorial_Maven/          Tutorial 2 - Maven Integration
├── Tutorial_Experiments/    Tutorial 3 - Running Experiments
├── Tutorial_4/              Tutorial 4 - Extending EvoSuite
│   ├── ga/operators/crossover/MiddleCrossOver.java       (NEW)
│   ├── coverage/methodpair/MethodPairTestFitness.java    (NEW)
│   ├── coverage/methodpair/MethodPairCoverageFactory.java(NEW)
│   ├── coverage/methodpair/MethodPairSuiteFitness.java   (NEW)
│   └── modified/            (8 modified EvoSuite core files)
├── evosuite-1.0.6.jar
├── evosuite-standalone-runtime-1.0.6.jar
└── evosuite-master-extended.jar   (built jar with Tutorial 4 extensions)
```

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| Java JDK | 8 (1.8) | Required - EvoSuite 1.0.6 does not support Java 9+ |
| Apache Maven | 3.1+ | For Tutorials 2-4 |
| Git | any | To clone this repo |

### Installing Java 8 via Conda (Windows)

If you have Miniconda/Anaconda installed, the easiest way to get Java 8 is:

```powershell
conda create -n evosuite python=3.9 -y
conda install -n evosuite -c conda-forge openjdk=8 maven -y
```

Then at the start of every terminal session, set your PATH:

```powershell
# PowerShell
$env:JAVA_HOME = "$env:USERPROFILE\.conda\envs\evosuite\Library"
$env:PATH = "$env:USERPROFILE\.conda\envs\evosuite\Library\bin;" + $env:PATH
```

```bash
# Bash / Linux / macOS
export JAVA_HOME=$(conda run -n evosuite java -XshowSettings:all 2>&1 | grep "java.home" | awk '{print $3}')
export PATH="$JAVA_HOME/bin:$PATH"
```

Verify:
```
java -version   # should show openjdk version "1.8.0_..."
javac -version  # should show javac 1.8.0_...
mvn -version    # should show Apache Maven 3.x.x
```

---

## Tutorial 1 - EvoSuite on the Command Line

**Screenshot location: after running the JUnit step at the bottom of this section**

### Steps

```powershell
cd Tutorial_Stack

# 1. Compile the project
mvn compile

# 2. Download JUnit/Hamcrest dependencies
mvn dependency:copy-dependencies

# 3. Generate tests with EvoSuite (branch coverage, 30s budget)
java -jar ../evosuite-1.0.6.jar -class tutorial.Stack -projectCP target\classes -criterion branch -Dsearch_budget=30

# 4. Set classpath (Windows - use semicolons)
$CLASSPATH = "target\classes;..\evosuite-standalone-runtime-1.0.6.jar;evosuite-tests;target\dependency\junit-4.12.jar;target\dependency\hamcrest-core-1.3.jar"

# 5. Compile the generated tests
javac -cp $CLASSPATH evosuite-tests\tutorial\*.java

# 6. Run the generated tests  <- SCREENSHOT HERE
java -cp $CLASSPATH org.junit.runner.JUnitCore tutorial.Stack_ESTest
```

**Expected output:**
```
JUnit version 4.12
.....
Time: X.XXX
OK (5 tests)
```

### What to screenshot
Take a screenshot of the terminal showing:
- The EvoSuite output: `Coverage of criterion BRANCH: 100%` and `Generated X tests`
- The JUnit output: `OK (5 tests)`

---

## Tutorial 2 - Maven Integration

**Screenshot location: after `mvn evosuite:prepare test`**

### Steps

```powershell
cd Tutorial_Maven

# 1. Compile
mvn compile

# 2. Run existing manual tests
mvn test

# 3. Generate EvoSuite tests via Maven plugin
mvn evosuite:generate -Dcriterion=branch -Dsearch_budget=30

# 4. Check generation results
mvn evosuite:info

# 5. Export tests to src/test/evosuite/
mvn evosuite:export

# 6. Run ALL tests (manual + generated)  <- SCREENSHOT HERE
mvn evosuite:prepare test
```

**Expected output:**
```
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### What to screenshot
Take a screenshot of the terminal showing the test run results:
- All test classes listed (LinkedList_ESTest, Node_ESTest, Stack_ESTest, StackTest, etc.)
- `Tests run: 19, Failures: 0, Errors: 0`
- `BUILD SUCCESS`

---

## Tutorial 3 - Running Experiments

**Screenshot location: after displaying statistics.csv**

### Steps

```powershell
cd Tutorial_Experiments

# 1. Compile
mvn compile

# 2. Download runtime dependencies
mvn dependency:copy-dependencies -DincludeScope=compile

# 3. Setup EvoSuite properties
java -jar ../evosuite-1.0.6.jar -setup target\classes target\dependency\commons-collections-3.2.2.jar

# 4. Remove old statistics (if any)
Remove-Item evosuite-report\statistics.csv -ErrorAction SilentlyContinue

# 5. Run experiment - branch coverage only
java -jar ../evosuite-1.0.6.jar -criterion branch -prefix tutorial -Dsearch_budget=30 -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore

# 6. Run experiment - combined criteria (default)
java -jar ../evosuite-1.0.6.jar -prefix tutorial -Dsearch_budget=30 -Doutput_variables=TARGET_CLASS,criterion,Size,Length,MutationScore

# 7. View results  <- SCREENSHOT HERE
Get-Content evosuite-report\statistics.csv
```

**Expected output:** A CSV table comparing 9 classes under branch-only vs combined criteria, showing Size, Length, and MutationScore columns.

### What to screenshot
Take a screenshot of the terminal showing the full `statistics.csv` content - the table with all 9 classes listed twice (once per criterion).

### Key finding
Combined criteria produces **larger test suites** (higher Size and Length) and generally **higher mutation scores** than branch-only, at the cost of longer generation time.

---

## Tutorial 4 - Extending EvoSuite

**Screenshot location: after running both extension tests**

This tutorial adds two extensions to EvoSuite 1.2.1-SNAPSHOT:

### Extension 1: `MiddleCrossOver`
A custom crossover operator that always cuts each chromosome at its **midpoint** instead of a random point. Located at [Tutorial_4/ga/operators/crossover/MiddleCrossOver.java](Tutorial_4/ga/operators/crossover/MiddleCrossOver.java).

### Extension 2: `MethodPair` Coverage Criterion
A new coverage criterion requiring that two specific methods are called **in sequence** within the same test. Files in [Tutorial_4/coverage/methodpair/](Tutorial_4/coverage/methodpair/).

### Running with the pre-built extended jar

```powershell
cd Tutorial_Stack

# Test Extension 1: MIDDLE crossover
java -jar ../evosuite-master-extended.jar `
  -class tutorial.Stack `
  -projectCP target\classes `
  -criterion branch `
  -Dcrossover_function=MIDDLE `
  -Dsearch_budget=20

# Test Extension 2: METHODPAIR criterion  <- SCREENSHOT HERE
java -jar ../evosuite-master-extended.jar `
  -class tutorial.Stack `
  -projectCP target\classes `
  -Dcriterion=METHODPAIR:BRANCH `
  -Dsearch_budget=20
```

**Expected output for Extension 1:**
```
* Test criterion:
  - Branch Coverage
* Coverage of criterion BRANCH: 100%
* Generated X tests with total length XX
* Done!
```

**Expected output for Extension 2:**
```
* Test criterion:
  - Method Pair Coverage
  - Branch Coverage
* Coverage of criterion METHODPAIR: 100%
* Coverage of criterion BRANCH: 100%
* Generated X tests with total length XX
* Done!
```

### What to screenshot
Take a screenshot showing **both** extension runs in the terminal:
- Extension 1: MIDDLE crossover achieving 100% branch coverage
- Extension 2: METHODPAIR criterion achieving 100% METHODPAIR + BRANCH coverage

### Modified EvoSuite files (in `Tutorial_4/modified/`)

| File | Change |
|------|--------|
| `Properties.java` | Added `MIDDLE` to `CrossoverFunction` enum; `METHODPAIR` to `Criterion` enum |
| `FitnessFunctions.java` | Registered `MethodPairSuiteFitness` and `MethodPairCoverageFactory` |
| `TestSuiteAdapter.java` | Added `MiddleCrossOver` to crossover adaptation switch |
| `PropertiesSuiteGAFactory.java` | Added `MIDDLE` case returning `new MiddleCrossOver<>()` |
| `ArchiveUtils.java` | Added `METHODPAIR` to `MethodPairTestFitness` archive mapping |
| `MultiCriteriaManager.java` | Added `METHODPAIR` case; fixed branch-only init for custom criteria |
| `CoverageCriteriaAnalyzer.java` | Registered `METHODPAIR` for coverage reporting |
| `TestSuiteGeneratorHelper.java` | Added `METHODPAIR` label ("Method Pair Coverage") |

---

## Extension / Modification Question

**What would be your next extension/modification of EvoSuite?**

My next extension would be an LLM-Guided Seed Test Generator.

Instead of starting from a random initial population, use a Large Language Model (e.g., Claude API) to analyze the class under test - its source code, method signatures, and documentation - and generate semantically informed seed tests. The hypothesis is that an LLM can identify meaningful preconditions and interesting input combinations that random generation takes much longer to find.

This integrates naturally into EvoSuite by overriding generateInitialPopulation() in a new LLMSeedStrategy class. The LLM generates test skeletons as text, which are compiled and parsed into TestChromosome objects, then handed to the existing genetic algorithm for refinement.

The MethodPair criterion built in Tutorial 4 is the ideal first benchmark: an LLM would naturally understand that push should precede pop on a Stack, generating targeted method-pair seeds that random search takes much longer to discover. The experiment infrastructure from Tutorial 3 (statistics.csv, Size/Length/MutationScore metrics) would then be used to empirically compare LLM-seeded vs random-seeded populations across multiple classes.

---

## Screenshot Summary

| # | Tutorial | What to capture |
|---|----------|----------------|
| 1 | Command Line | EvoSuite generation output + `OK (5 tests)` from JUnit |
| 2 | Maven Integration | `Tests run: 19, Failures: 0` + `BUILD SUCCESS` |
| 3 | Running Experiments | Full `statistics.csv` table (18 rows, BRANCH vs combined) |
| 4 | Extending EvoSuite | Both extensions running: MIDDLE crossover + METHODPAIR 100% |
