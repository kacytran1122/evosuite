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

**Screenshot: capture from `* EvoSuite 1.0.6` down to `* Computation finished`**

### Steps

```powershell
cd Tutorial_Stack

# 1. Compile the project
mvn compile

# 2. Download JUnit/Hamcrest dependencies
mvn dependency:copy-dependencies

# 3. Generate tests with EvoSuite (branch coverage, 20s budget)
java -jar ../evosuite-1.0.6.jar -class tutorial.Stack -projectCP target\classes -criterion branch -Dsearch_budget=20

# 4. Set classpath (Windows - use semicolons)
$CLASSPATH = "target\classes;..\evosuite-standalone-runtime-1.0.6.jar;evosuite-tests;target\dependency\junit-4.12.jar;target\dependency\hamcrest-core-1.3.jar"

# 5. Compile the generated tests
javac -cp $CLASSPATH evosuite-tests\tutorial\*.java

# 6. Run the generated tests
java -cp $CLASSPATH org.junit.runner.JUnitCore tutorial.Stack_ESTest
```

### Actual output from this run

```
* EvoSuite 1.0.6
* Going to generate test cases for class: tutorial.Stack
* Test criterion:
  - Branch Coverage
* Search finished after 3s and 10 generations, 4509 statements
* Coverage of criterion BRANCH: 100%
* Total number of goals: 7
* Number of covered goals: 7
* Generated 5 tests with total length 24
* Resulting test suite's coverage: 100%
* Resulting test suite's mutation score: 61%
* Writing JUnit test case 'Stack_ESTest' to evosuite-tests
* Done!
* Computation finished
```

---

## Tutorial 2 - Maven Integration

**Screenshot: capture the T E S T S block down to `BUILD SUCCESS`**

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

# 6. Run ALL tests (manual + generated)
mvn evosuite:prepare test
```

### Actual output from this run

```
T E S T S
Running tutorial.LinkedListIterator_ESTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.71 sec
Running tutorial.LinkedList_ESTest
Tests run: 8, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.018 sec
Running tutorial.Node_ESTest
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.027 sec
Running tutorial.StackTest
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0 sec
Running tutorial.Stack_ESTest
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.028 sec

Results :
Tests run: 19, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
Total time: 10.038 s
```

---

## Tutorial 3 - Running Experiments

**Screenshot: capture the full statistics.csv table**

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

# 7. View results
Get-Content evosuite-report\statistics.csv
```

### Actual output from this run

```
TARGET_CLASS,criterion,Size,Length,MutationScore
tutorial.ATM,BRANCH,10,76,0.4166666666666667
tutorial.ATMCard,BRANCH,8,48,0.6666666666666666
tutorial.Bank,BRANCH,4,11,0.8
tutorial.BankAccount,BRANCH,2,6,0.8
tutorial.Owner,BRANCH,1,1,1.0
tutorial.CurrentAccount,BRANCH,2,7,0.7391304347826086
tutorial.SavingsAccount,BRANCH,3,9,0.8529411764705882
tutorial.Company,BRANCH,1,2,1.0
tutorial.Person,BRANCH,2,4,0.0
tutorial.ATM,LINE;BRANCH;EXCEPTION;WEAKMUTATION;OUTPUT;METHOD;METHODNOEXCEPTION;CBRANCH,15,118,0.4166666666666667
tutorial.ATMCard,LINE;BRANCH;EXCEPTION;WEAKMUTATION;OUTPUT;METHOD;METHODNOEXCEPTION;CBRANCH,14,62,0.6666666666666666
tutorial.Bank,LINE;BRANCH;EXCEPTION;WEAKMUTATION;OUTPUT;METHOD;METHODNOEXCEPTION;CBRANCH,4,15,0.8
tutorial.BankAccount,LINE;BRANCH;EXCEPTION;WEAKMUTATION;OUTPUT;METHOD;METHODNOEXCEPTION;CBRANCH,8,25,1.0
tutorial.Owner,LINE;BRANCH;EXCEPTION;WEAKMUTATION;OUTPUT;METHOD;METHODNOEXCEPTION;CBRANCH,1,1,1.0
tutorial.CurrentAccount,LINE;BRANCH;EXCEPTION;WEAKMUTATION;OUTPUT;METHOD;METHODNOEXCEPTION;CBRANCH,3,13,0.8695652173913043
tutorial.SavingsAccount,LINE;BRANCH;EXCEPTION;WEAKMUTATION;OUTPUT;METHOD;METHODNOEXCEPTION;CBRANCH,4,13,0.9411764705882353
tutorial.Company,LINE;BRANCH;EXCEPTION;WEAKMUTATION;OUTPUT;METHOD;METHODNOEXCEPTION;CBRANCH,3,6,1.0
tutorial.Person,LINE;BRANCH;EXCEPTION;WEAKMUTATION;OUTPUT;METHOD;METHODNOEXCEPTION;CBRANCH,6,12,1.0
```

### Key finding
Combined criteria produces larger test suites (higher Size and Length) and generally higher mutation scores than branch-only. For example, BankAccount goes from mutation score 0.8 (BRANCH) to 1.0 (combined), and Person goes from 0.0 to 1.0.

---

## Tutorial 4 - Extending EvoSuite

**Screenshot: capture from `* EvoSuite 1.2.1-SNAPSHOT` down to `* Done!` - stop before the AgentLoader lines**

This tutorial adds two extensions to EvoSuite 1.2.1-SNAPSHOT:

### Extension 1: `MiddleCrossOver`
A custom crossover operator that always cuts each chromosome at its midpoint instead of a random point. Located at [Tutorial_4/ga/operators/crossover/MiddleCrossOver.java](Tutorial_4/ga/operators/crossover/MiddleCrossOver.java).

### Extension 2: `MethodPair` Coverage Criterion
A new coverage criterion requiring that two specific methods are called in sequence within the same test. Files in [Tutorial_4/coverage/methodpair/](Tutorial_4/coverage/methodpair/).

### Running with the pre-built extended jar

```powershell
cd Tutorial_Stack

# Extension 1: MIDDLE crossover
java -jar ../evosuite-master-extended.jar `
  -class tutorial.Stack `
  -projectCP target\classes `
  -criterion branch `
  -Dcrossover_function=MIDDLE `
  -Dsearch_budget=20

# Extension 2: METHODPAIR criterion
java -jar ../evosuite-master-extended.jar `
  -class tutorial.Stack `
  -projectCP target\classes `
  "-Dcriterion=METHODPAIR:BRANCH" `
  -Dsearch_budget=20
```

### Actual output from this run

```
* EvoSuite 1.2.1-SNAPSHOT
* Going to generate test cases for class: tutorial.Stack
* Test criteria:
  - Method Pair Coverage
  - Branch Coverage
* Total number of test goals for DYNAMOSA: 19
* Search finished after 2s and 5 generations, 5364 statements
* Coverage analysis for criterion METHODPAIR
* Coverage of criterion METHODPAIR: 100%
* Total number of goals: 12
* Number of covered goals: 12
* Coverage analysis for criterion BRANCH
* Coverage of criterion BRANCH: 100%
* Total number of goals: 7
* Number of covered goals: 7
* Generated 6 tests with total length 33
* Resulting test suite's coverage: 100%
* Resulting test suite's mutation score: 61%
* Writing JUnit test case 'Stack_ESTest' to evosuite-tests
* Done!
* Computation finished
```

> Note: `AgentLoader` errors after `Done!` are a known Windows JVM attach limitation. They do not affect test generation results.

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
| 1 | Command Line | From `* EvoSuite 1.0.6` to `* Computation finished` |
| 2 | Maven Integration | From `T E S T S` to `BUILD SUCCESS` |
| 3 | Running Experiments | Full statistics.csv table (18 data rows) |
| 4 | Extending EvoSuite | From `* EvoSuite 1.2.1-SNAPSHOT` to `* Done!` (stop before AgentLoader errors) |
