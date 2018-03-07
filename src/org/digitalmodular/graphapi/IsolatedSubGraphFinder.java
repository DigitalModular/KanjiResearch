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
package org.digitalmodular.graphapi;

import java.util.Arrays;
import static java.util.Objects.requireNonNull;

import org.digitalmodular.graphapi.Graph.ConnectionIterator;

/**
 * Finds all connected subgraphs within a given graph.
 *
 * @author Mark Jeronimus
 */
// Created 2018-03-03
public final class IsolatedSubGraphFinder {
	private IsolatedSubGraphFinder() { throw new AssertionError(); }

	private static final int[][] EMPTY_INTS_ARRAY = new int[0][];

	public static int[][] findIsolatedSubGraphs(NeighborGraph graph) {
		requireNonNull(graph);

		int size = graph.size();
		if (size == 0)
			return EMPTY_INTS_ARRAY;
		if (size == 1)
			return new int[][]{{0}};

		int[] startingNodes = findSubGraphStartingNodes(graph);

		return findSubGraphPermutations(graph, startingNodes);
	}

	/*
	 * Example Output: [0, 1, 1, 3, 1, 1, 0, 1, 1, 1]
	 */
	private static int[] findSubGraphStartingNodes(Graph graph) {
		int size = graph.size();

		int[] startingNodes = new int[size];
		for (int i = 0; i < size; i++)
			startingNodes[i] = i;

		int[] connection = new int[2];

		for (int step = 1; step < size; step++) {
			boolean changed = false;

			ConnectionIterator iterator = graph.iterator();
			// Suppress IntelliJ bug: Replacing by foreach changes to default `next()` which is much less efficient.
			//noinspection WhileLoopReplaceableByForEach
			while (iterator.hasNext()) {
				iterator.next(connection);
				int x = connection[0];
				int y = connection[1];
				assert x < y;

				if (startingNodes[y] != startingNodes[x]) {
					startingNodes[x] = Math.min(startingNodes[x], startingNodes[y]);
					startingNodes[y] = startingNodes[x];

					changed = true;
				}
			}

			if (!changed)
				return startingNodes;
		}

		throw new AssertionError("Iteration overflow");
	}

	/*
	 * Example Input:  [0, 1, 1, 3, 1, 1, 0, 1, 1, 1]
	 * Example Output: [[0, 6], [1, 2, 4, 5, 7, 8, 9], [3]]
	 */
	@SuppressWarnings("TooBroadScope")
	private static int[][] findSubGraphPermutations(Graph graph, int[] startingNodes) {
		int size = graph.size();

		int[] startingNodeToSubGraphIndex = new int[size]; // Example Intermediate: [0, 1, ◌, 2, ...]
		int[] subGraphSizes               = new int[size]; // Example Intermediate: [2, 7, 1, ...]
		int[] subGraphIndices             = new int[size]; // Example Intermediate: [0, 1, 1, 2, 1, 1, 0, 1, 1, 1]
		int   numSubGraphs                = 0;

		Arrays.fill(startingNodeToSubGraphIndex, -1);

		for (int i = 0; i < size; i++) {
			if (startingNodeToSubGraphIndex[startingNodes[i]] == -1) {
				startingNodeToSubGraphIndex[startingNodes[i]] = numSubGraphs;
				numSubGraphs++;
			}

			int subGraphIndex = startingNodeToSubGraphIndex[startingNodes[i]];

			subGraphSizes[subGraphIndex]++;
			subGraphIndices[i] = subGraphIndex;
		}

		return makePermutations(size, numSubGraphs, subGraphSizes, subGraphIndices);
	}

	/*
	 * Example Input:  10, 3, [2, 7, 1, ...], [0, 1, 1, 2, 1, 1, 0, 1, 1, 1]
	 * Example Output: [[0, 6], [1, 2, 4, 5, 7, 8, 9], [3]]
	 */
	private static int[][] makePermutations(int size, int numSubGraphs, int[] subGraphSizes, int[] subGraphIndices) {
		int[][] permutations = preparePermutations(numSubGraphs, subGraphSizes);

		int[] indicesInSubGraphs = new int[numSubGraphs];
		for (int i = 0; i < size; i++) {
			int subGraphIndex = subGraphIndices[i];
			permutations[subGraphIndex][indicesInSubGraphs[subGraphIndex]++] = i;
		}

		return permutations;
	}

	/*
	 * Example Input:  3, [2, 7, 1, ...]
	 * Example Output: [int[2], int[7], int[1]]
	 */
	private static int[][] preparePermutations(int numSubGraphs, int[] subGraphSizes) {
		int[][] permutations = new int[numSubGraphs][];
		for (int i = 0; i < numSubGraphs; i++)
			permutations[i] = new int[subGraphSizes[i]];

		return permutations;
	}
}
