/*
 * Copyright (C) 2010-2018 Gordon Fraser, Andrea Arcuri and EvoSuite
 * contributors
 *
 * This file is part of EvoSuite.
 *
 * EvoSuite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3.0 of the License, or
 * (at your option) any later version.
 *
 * EvoSuite is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EvoSuite. If not, see <http://www.gnu.org/licenses/>.
 */
package org.evosuite.ga.operators.crossover;

import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;

/**
 * Crossover at the midpoint of each chromosome rather than a random point.
 * Tutorial Part 4 extension: custom crossover operator.
 */
public class MiddleCrossOver<T extends Chromosome<T>> extends CrossOverFunction<T> {

    private static final long serialVersionUID = 8432987654321098765L;

    /**
     * Split each parent at its midpoint and swap the halves.
     */
    @Override
    public void crossOver(T parent1, T parent2) throws ConstructionFailedException {
        if (parent1.size() < 2 || parent2.size() < 2) {
            return;
        }

        int point1 = (int) Math.round(parent1.size() / 2.0);
        int point2 = (int) Math.round(parent2.size() / 2.0);

        T t1 = parent1.clone();
        T t2 = parent2.clone();

        parent1.crossOver(t2, point1, point2);
        parent2.crossOver(t1, point2, point1);
    }
}
