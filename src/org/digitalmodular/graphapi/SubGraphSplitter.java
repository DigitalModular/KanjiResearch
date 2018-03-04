/*
 * This file is part of GraphAnalyzer.
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
 * along with AllUtilities. If not, see <http://www.gnu.org/licenses/>.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import static java.util.Objects.requireNonNull;

import org.digitalmodular.graphapi.Graph.ConnectionIterator;

/**
 * Finds all connected subgraphs within a given graph.
 *
 * @author Mark Jeronimus
 */
// Created 2018-03-03
public final class SubGraphSplitter {
	private SubGraphSplitter() { throw new AssertionError(); }

	public static List<NeighborGraph> splitGraph(NeighborGraph graph) {
		requireNonNull(graph);

		int size = graph.size();
		if (size == 0)
			return Collections.emptyList();
		if (size == 1)
			return Collections.singletonList(graph);

		int[] startingNodes = findSubGraphStartingNodes(graph);

		int[][] subGraphData = findSubGraphData(graph, startingNodes);

		List<NeighborGraph> subGraphs = constructSubGraphs(graph, subGraphData);

		subGraphs.sort(Comparator.comparingInt(Graph::size).reversed());
		return Collections.unmodifiableList(subGraphs);
	}

	/**
	 * Example of result: [0, 1, 1, 3, 1, 1, 0, 1, 1, 1]
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

	/**
	 * Example of result: [[2, 7, 1],
	 * [0, 1, 1, 2, 1, 1, 0, 1, 1, 1],
	 * [0, 0, 1, 0, 2, 3, 1, 4, 5, 6]]
	 */
	@SuppressWarnings("TooBroadScope")
	private static int[][] findSubGraphData(Graph graph, int[] startingNodes) {
		int size = graph.size();

		int[] subGraphSizes = new int[size];
		int   numSubGraphs  = 0;
		int[] nodeMap       = new int[size];
		int[] graphMap      = new int[size];
		Arrays.fill(graphMap, -1);

		for (int startingNode = 0; startingNode < size; startingNode++) {
			if (graphMap[startingNode] >= 0)
				continue;

			int numSubNodes = 0;
			int n           = 0;
			for (int node = 0; node < size; node++) {
				if (startingNodes[node] == startingNode) {
					numSubNodes++;
					nodeMap[node] = n++;
					graphMap[node] = numSubGraphs;
				}
			}

			subGraphSizes[numSubGraphs] = numSubNodes;
			numSubGraphs++;
		}

		subGraphSizes = Arrays.copyOf(subGraphSizes, numSubGraphs);

		return new int[][]{subGraphSizes, nodeMap, graphMap};
	}

	@SuppressWarnings("TypeMayBeWeakened") // Suppress IntelliJ bug: Weakening `graph` creates compiler errors!
	private static List<NeighborGraph> constructSubGraphs(Graph graph, int[][] subGraphData) {
		int[] subGraphSizes = subGraphData[0];
		int[] nodeMap       = subGraphData[1];
		int[] graphMap      = subGraphData[2];

		List<NeighborGraph> subGraphs = initSubGraphsList(subGraphSizes);

		ConnectionIterator iterator   = graph.iterator();
		int[]              connection = new int[2];
		while (iterator.hasNext()) {
			iterator.next(connection);
			int x = connection[0];
			int y = connection[1];
			assert x < y;

			Graph subGraph = subGraphs.get(graphMap[x]);

			//noinspection ObjectEquality // Comparing identity, not equality.
			assert subGraph == subGraphs.get(graphMap[y]);

			subGraph.setConnection(nodeMap[x], nodeMap[y]);
		}

		return subGraphs;
	}

	private static List<NeighborGraph> initSubGraphsList(int[] subGraphSizes) {
		List<NeighborGraph> subGraphs = new ArrayList<>(subGraphSizes.length);
		for (int subGraphSize : subGraphSizes)
			subGraphs.add(new NeighborGraph(subGraphSize));

		return subGraphs;
	}
}
