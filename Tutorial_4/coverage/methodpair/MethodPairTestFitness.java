/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite contributors
 *
 * Tutorial Part 4 extension: MethodPair coverage criterion.
 * Requires that two methods are called in sequence within a single test.
 */
package org.evosuite.coverage.methodpair;

import org.evosuite.Properties;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.statements.ConstructorStatement;
import org.evosuite.testcase.statements.EntityWithParametersStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Fitness function for a pair of methods that must be called in sequence.
 * Fitness is 0 when both methods are called (in order) within a test,
 * 0.5 when only the first method is called, and 1.0 otherwise.
 */
public class MethodPairTestFitness extends TestFitnessFunction {

    private static final long serialVersionUID = 7392847362938450001L;

    private final String className;
    private final String methodName1;
    private final String methodName2;

    public MethodPairTestFitness(String className, String methodName1, String methodName2) {
        this.className = className;
        this.methodName1 = methodName1;
        this.methodName2 = methodName2;
    }

    @Override
    public double getFitness(TestChromosome individual, ExecutionResult result) {
        double fitness = 1.0;
        boolean firstCalled = false;

        List<Integer> exceptionPositions = asSortedList(result.getPositionsWhereExceptionsWereThrown());

        for (Statement stmt : result.test) {
            if (!isValidPosition(exceptionPositions, stmt.getPosition())) {
                break;
            }
            if (stmt instanceof MethodStatement || stmt instanceof ConstructorStatement) {
                EntityWithParametersStatement ps = (EntityWithParametersStatement) stmt;
                String stmtClass = ps.getDeclaringClassName();
                String stmtMethod = ps.getMethodName() + ps.getDescriptor();

                if (className.equals(stmtClass)) {
                    if (!firstCalled && methodName1.equals(stmtMethod)) {
                        firstCalled = true;
                        fitness = 0.5;
                    } else if (firstCalled && methodName2.equals(stmtMethod)) {
                        fitness = 0.0;
                        break;
                    }
                }
            }
        }

        updateIndividual(individual, fitness);

        if (fitness == 0.0) {
            individual.getTestCase().addCoveredGoal(this);
        }

        // Skip archive update: MethodPairTestFitness doesn't participate in the
        // DynaMOSA branch-based archive to avoid criterion-mismatch errors.

        return fitness;
    }

    private boolean isValidPosition(List<Integer> exceptionPositions, Integer position) {
        if (Properties.BREAK_ON_EXCEPTION) {
            return exceptionPositions.isEmpty() || position <= exceptionPositions.get(0);
        }
        return true;
    }

    private <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<>(c);
        java.util.Collections.sort(list);
        return list;
    }

    public String getClassName() { return className; }
    public String getMethodName1() { return methodName1; }
    public String getMethodName2() { return methodName2; }

    @Override
    public String toString() {
        return "[METHODPAIR] " + className + ": " + methodName1 + " -> " + methodName2;
    }

    @Override
    public int hashCode() {
        return 31 * (31 * className.hashCode() + methodName1.hashCode()) + methodName2.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MethodPairTestFitness)) return false;
        MethodPairTestFitness other = (MethodPairTestFitness) obj;
        return className.equals(other.className)
                && methodName1.equals(other.methodName1)
                && methodName2.equals(other.methodName2);
    }

    @Override
    public int compareTo(TestFitnessFunction other) {
        if (other instanceof MethodPairTestFitness) {
            MethodPairTestFitness o = (MethodPairTestFitness) other;
            int c = className.compareTo(o.className);
            if (c != 0) return c;
            int c2 = methodName1.compareTo(o.methodName1);
            if (c2 != 0) return c2;
            return methodName2.compareTo(o.methodName2);
        }
        return compareClassName(other);
    }

    @Override
    public String getTargetClass() { return className; }

    @Override
    public String getTargetMethod() { return methodName1 + "->" + methodName2; }
}
