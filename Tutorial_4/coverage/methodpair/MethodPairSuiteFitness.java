/*
 * Tutorial Part 4 extension: suite-level fitness for MethodPair coverage.
 */
package org.evosuite.coverage.methodpair;

import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Suite-level fitness: counts the number of uncovered MethodPair goals.
 * Lower is better; 0 means all pairs are covered.
 */
public class MethodPairSuiteFitness extends TestSuiteFitnessFunction {

    private static final long serialVersionUID = 5934872637485001234L;

    private final List<MethodPairTestFitness> goals;
    private final int totalGoals;

    public MethodPairSuiteFitness() {
        goals = new MethodPairCoverageFactory().getCoverageGoals();
        totalGoals = goals.size();
    }

    @Override
    public double getFitness(TestSuiteChromosome suite) {
        List<ExecutionResult> results = runTestSuite(suite);
        Set<MethodPairTestFitness> covered = new HashSet<>();

        for (ExecutionResult result : results) {
            TestChromosome tc = new TestChromosome();
            tc.setTestCase(result.test);
            for (MethodPairTestFitness goal : goals) {
                if (!covered.contains(goal) && goal.getFitness(tc, result) == 0.0) {
                    covered.add(goal);
                }
            }
        }

        int uncovered = totalGoals - covered.size();
        double coverage = totalGoals == 0 ? 1.0 : (double) covered.size() / totalGoals;

        suite.setNumOfCoveredGoals(this, covered.size());
        suite.setNumOfNotCoveredGoals(this, uncovered);
        suite.setCoverage(this, coverage);

        updateIndividual(suite, uncovered);
        return uncovered;
    }
}
