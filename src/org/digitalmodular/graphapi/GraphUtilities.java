/*
 * This file is part of GraphAPI.
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

/**
 * @author Mark Jeronimus
 */
// Created 2018-02-04
public final class GraphUtilities {
	private GraphUtilities() { throw new AssertionError(); }

	public static MatrixGraph toMatrixGraph(Graph graph) {
		if (graph instanceof MatrixGraph)
			return (MatrixGraph)graph;

		return new MatrixGraph(graph);
	}

	public static NeighborGraph toNeighborGraph(Graph graph) {
		if (graph instanceof NeighborGraph)
			return (NeighborGraph)graph;

		return new NeighborGraph(graph);
	}

	public static int countConnections(Graph graph) {
		int size           = graph.size();
		int numConnections = 0;
		for (int y = 0; y < size; y++)
			for (int x = y + 1; x < size; x++)
				if (graph.isConnected(x, y))
					numConnections++;

		return numConnections;
	}

	public static int countIsolatedNodes(Graph graph) {
		int size             = graph.size();
		int numIsolatedNodes = 0;
		for (int y = 0; y < size; y++) {
			int numNeighbors = 0;
			for (int x = 0; x < size; x++)
				if (x != y && graph.isConnected(x, y))
					numNeighbors++;

			if (numNeighbors == 0)
				numIsolatedNodes++;
		}

		return numIsolatedNodes;
	}

	public static int countLeafNodes(Graph graph) {
		int size         = graph.size();
		int numLeafNodes = 0;
		for (int y = 0; y < size; y++) {
			int numNeighbors = 0;
			for (int x = 0; x < size; x++)
				if (x != y && graph.isConnected(x, y))
					numNeighbors++;

			if (numNeighbors == 1)
				numLeafNodes++;
		}

		return numLeafNodes;
	}

	public static double networkAverage(double[] values, boolean skipInvalid) {
		double sum   = 0;
		int    count = 0;

		for (double value : values) {
			if (!(skipInvalid && Double.isNaN(value))) {
				sum += value;
				count++;
			}
		}

		if (count == 0)
			return Double.NaN;

		return sum / count;
	}
}
