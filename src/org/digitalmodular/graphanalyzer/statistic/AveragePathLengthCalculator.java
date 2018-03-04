/*
 * This file is part of KanjiResearch.
 *
 * Copyleft 2018 Mark Jeronimus. All Rights Reversed.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KanjiResearch. If not, see <http://www.gnu.org/licenses/>.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.digitalmodular.graphanalyzer.statistic;

import java.util.Arrays;
import com.sun.istack.internal.Nullable;

import org.digitalmodular.graphapi.Graph;
import org.digitalmodular.graphapi.MatrixGraph;
import org.digitalmodular.graphapi.NeighborGraph;

/**
 * @author Mark Jeronimus
 */
// Created 2018-02-06
public final class AveragePathLengthCalculator implements LocalGraphStatisticCalculator<NeighborGraph> {
	public static final AveragePathLengthCalculator INSTANCE = new AveragePathLengthCalculator();

	private AveragePathLengthCalculator() {
		if (INSTANCE != null)
			throw new AssertionError();
	}

	@Override
	public String getName() { return "Average Path Length"; }

	@Override
	public String getAbbreviation() { return "APL"; }

	@Override
	public double[] calculateAll(NeighborGraph graph) {
		int size = graph.size();

		Graph visibilityMap = new MatrixGraph(graph);

		int[] pathLengthSums    = getNeighborCounts(graph);
		int[] numUnvisitedNodes = initNumUnvisitedNodes(graph);

		boolean visible[] = new boolean[size];

		Graph visibilityMapOfLastIteration = new MatrixGraph(visibilityMap);
		for (int pathLength = 2; pathLength < size; pathLength++) {
			visibilityMapOfLastIteration.setGraph(visibilityMap);

			boolean changed = false;
			for (int node = 0; node < size - 1; node++) {
				if (numUnvisitedNodes[node] == 0)
					continue;

				bitwiseOrOfRows(graph, visibilityMapOfLastIteration, node, visible);

				for (int node2 = node + 1; node2 < size; node2++) {
					if (visible[node2] && !visibilityMap.isConnected(node, node2)) {
						visibilityMap.setConnection(node, node2);

						numUnvisitedNodes[node]--;
						numUnvisitedNodes[node2]--;

						pathLengthSums[node] += pathLength;
						pathLengthSums[node2] += pathLength;

						changed = true;
					}
				}
			}

			if (nonzeroCount(numUnvisitedNodes) <= 0)
				return calculateAveragePathLengths(size, pathLengthSums);

			if (!changed)
				return calculateAveragePathLengths(size, null);
		}

		throw new AssertionError("Iteration overflow");
	}

	private static int[] getNeighborCounts(NeighborGraph graph) {
		int   size         = graph.size();
		int[] numNeighbors = new int[size];
		for (int node = 0; node < size; node++)
			numNeighbors[node] = graph.numNeighbors(node);

		return numNeighbors;
	}

	private static int[] initNumUnvisitedNodes(NeighborGraph graph) {
		int   size      = graph.size();
		int[] remaining = new int[size];
		for (int node = 0; node < size; node++)
			remaining[node] = size - 1 - graph.numNeighbors(node);

		return remaining;
	}

	private static int nonzeroCount(int[] array) {
		int sum = 0;
		for (int remainingNode : array)
			if (remainingNode != 0)
				sum++;

		return sum;
	}

	private static void bitwiseOrOfRows(Graph graph, Graph visibilityMapOfLastIteration, int node, boolean[] visible) {
		int size = graph.size();
		Arrays.fill(visible, false);
		for (int x = 0; x < size; x++)
			if (x != node && graph.isConnected(x, node))
				or(node, size, visible, visibilityMapOfLastIteration, x);
	}

	private static void or(int start, int end, boolean[] lhs, Graph graph, int x) {
		for (int i = start; i < end; i++)
			lhs[i] |= graph.isConnected(x, i);
	}

	private static double[] calculateAveragePathLengths(int size, @Nullable int[] pathLenthSums) {
		double[] averagePathLengths = new double[size];

		if (pathLenthSums == null)
			Arrays.fill(averagePathLengths, Double.NaN);
		else
			for (int i = 0; i < size; i++)
				averagePathLengths[i] = pathLenthSums[i] / (double)(size - 1);

		return averagePathLengths;
	}
}
