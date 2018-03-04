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
import static java.util.Arrays.binarySearch;
import static java.util.Objects.requireNonNull;

/**
 * Undirected unweighted graph without self-loops
 * <p>
 * Not Thread-safe.
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-10
public class NeighborGraph extends MatrixGraph {
	private static final long serialVersionUID = -5755588310875985669L;

	private final int[]   numNeighbors;
	private final int[][] neighbors;

	public NeighborGraph(int size) {
		super(size);

		numNeighbors = new int[size];
		neighbors = new int[size][size - 1];
	}

	public NeighborGraph(Graph other) {
		this(other.size());

		//noinspection OverridableMethodCallDuringObjectConstruction,OverriddenMethodCallDuringObjectConstruction
		setGraph(other);
	}

	@Override
	public void setConnection(int x, int y) {
		if (isConnected(x, y) || x == y)
			return;

		super.setConnection(x, y);

		addNeighbor(x, y);
		addNeighbor(y, x);
	}

	@Override
	public void removeConnection(int x, int y) {
		if (!isConnected(x, y) || x == y)
			return;

		super.removeConnection(x, y);

		removeNeighbor(x, y);
		removeNeighbor(y, x);
	}

	@Override
	public void setGraph(Graph other) {
		requireNonNull(other);
		int size = neighbors.length;
		if (size != other.size())
			throw new IllegalArgumentException("Network sizes differ: " + size + " vs " + other.size());

		if (other instanceof NeighborGraph) {
			NeighborGraph neighborGraph = (NeighborGraph)other;
			System.arraycopy(neighborGraph.numNeighbors, 0, numNeighbors, 0, size);
			for (int i = 0; i < size; i++)
				System.arraycopy(neighborGraph.neighbors[i], 0, neighbors[i], 0, numNeighbors[i]);
		} else {
			Arrays.fill(numNeighbors, 0);

			ConnectionIterator iterator   = other.iterator();
			int[]              connection = new int[2];
			while (iterator.hasNext()) {
				iterator.next(connection);
				setConnection(connection[0], connection[1]);
			}
		}
	}

	public int numNeighbors(int node)           { return numNeighbors[node]; }

	public int getNeighbor(int node, int index) { return neighbors[node][index]; }

	private void addNeighbor(int node, int newNeighbor) {
		int[] neighborsOfNode = neighbors[node];
		int   insertionPoint  = binarySearch(neighborsOfNode, 0, numNeighbors[node], newNeighbor);
		assert insertionPoint < 0;
		int newPosition = -insertionPoint - 1;

		System.arraycopy(neighborsOfNode, newPosition,
		                 neighborsOfNode, newPosition + 1,
		                 numNeighbors[node] - newPosition);

		neighborsOfNode[newPosition] = newNeighbor;
		numNeighbors[node]++;
	}

	private void removeNeighbor(int node, int oldNeighbor) {
		int[] neighborsOfNode = neighbors[node];
		int   position        = binarySearch(neighborsOfNode, 0, numNeighbors[node], oldNeighbor);
		assert position >= 0;

		System.arraycopy(neighborsOfNode, position + 1,
		                 neighborsOfNode, position,
		                 numNeighbors[node] - position - 1);
		numNeighbors[node]--;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(neighbors.length * GraphUtilities.countConnections(this) * 4);

		for (int node = 0; node < neighbors.length; node++) {
			if (node > 0)
				sb.append('\n');

			sb.append(node).append(' ');
			for (int i = 0; i < numNeighbors[node]; i++)
				sb.append(' ').append(neighbors[node][i]);
		}
		return sb.toString();
	}
}
