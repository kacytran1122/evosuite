/*
 * Tutorial Part 4 extension: factory for MethodPair coverage goals.
 * Generates all ordered pairs (m1, m2) of accessible constructors/methods.
 * Deliberately avoids the ASM dependency so this class works in both
 * the plain client jar and the shaded master jar.
 */
package org.evosuite.coverage.methodpair;

import org.evosuite.Properties;
import org.evosuite.setup.TestUsageChecker;
import org.evosuite.testsuite.AbstractFitnessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates MethodPairTestFitness goals: all ordered pairs (m1, m2) where both
 * m1 and m2 are accessible methods/constructors of the class under test.
 */
public class MethodPairCoverageFactory extends AbstractFitnessFactory<MethodPairTestFitness> {

    private static final Logger logger = LoggerFactory.getLogger(MethodPairCoverageFactory.class);

    @Override
    public List<MethodPairTestFitness> getCoverageGoals() {
        List<MethodPairTestFitness> goals = new ArrayList<>();
        long start = System.currentTimeMillis();

        String className = Properties.TARGET_CLASS;
        Class<?> clazz = Properties.getTargetClassAndDontInitialise();
        if (clazz != null) {
            List<String> methods = getUsableMethods(clazz);
            for (String m1 : methods) {
                for (String m2 : methods) {
                    if (!m1.equals(m2)) {
                        goals.add(new MethodPairTestFitness(className, m1, m2));
                    }
                }
            }
        }

        goalComputationTime = System.currentTimeMillis() - start;
        logger.info("Generated {} method pair goals for {}", goals.size(), className);
        return goals;
    }

    private List<String> getUsableMethods(Class<?> clazz) {
        List<String> names = new ArrayList<>();
        for (Constructor<?> c : clazz.getDeclaredConstructors()) {
            if (TestUsageChecker.canUse(c)) {
                names.add("<init>" + constructorDescriptor(c));
            }
        }
        for (Method m : clazz.getDeclaredMethods()) {
            if (TestUsageChecker.canUse(m)) {
                names.add(m.getName() + methodDescriptor(m));
            }
        }
        return names;
    }

    /** Build a JVM constructor descriptor without using ASM. */
    private static String constructorDescriptor(Constructor<?> c) {
        StringBuilder sb = new StringBuilder("(");
        for (Class<?> p : c.getParameterTypes()) {
            sb.append(typeDescriptor(p));
        }
        sb.append(")V");
        return sb.toString();
    }

    /** Build a JVM method descriptor without using ASM. */
    private static String methodDescriptor(Method m) {
        StringBuilder sb = new StringBuilder("(");
        for (Class<?> p : m.getParameterTypes()) {
            sb.append(typeDescriptor(p));
        }
        sb.append(")");
        sb.append(typeDescriptor(m.getReturnType()));
        return sb.toString();
    }

    /** Convert a Class to its JVM type descriptor character/string. */
    private static String typeDescriptor(Class<?> type) {
        if (type == void.class)    return "V";
        if (type == boolean.class) return "Z";
        if (type == byte.class)    return "B";
        if (type == char.class)    return "C";
        if (type == short.class)   return "S";
        if (type == int.class)     return "I";
        if (type == long.class)    return "J";
        if (type == float.class)   return "F";
        if (type == double.class)  return "D";
        if (type.isArray())        return type.getName().replace('.', '/');
        return "L" + type.getName().replace('.', '/') + ";";
    }
}
