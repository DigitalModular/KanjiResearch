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

import java.util.ArrayList;
import java.util.List;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

/**
 * Undirected unweighted graph without self-loops
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-10
public class NeighborGraph implements Graph {
	private static final long serialVersionUID = -5755588310875985669L;

	private final List<List<Integer>> neighbors;

	public NeighborGraph(int numNodes) {
		List<List<Integer>> neighbors = new ArrayList<>(numNodes);
		for (int y = 0; y < numNodes; y++)
			neighbors.add(new ArrayList<>(numNodes - 1));

		this.neighbors = unmodifiableList(neighbors);
	}

	public NeighborGraph(Graph other) {
		this(other.numNodes());

		//noinspection OverridableMethodCallDuringObjectConstruction,OverriddenMethodCallDuringObjectConstruction
		setGraph(other);
	}

	@Override
	public int numNodes() {
		return neighbors.size();
	}

	@Override
	public void setConnection(int x, int y) {
		if (isConnected(x, y) || x == y)
			return;

		addNeighbor(neighbors.get(x), y);
		addNeighbor(neighbors.get(y), x);
	}

	@Override
	public void removeConnection(int x, int y) {
		if (!isConnected(x, y) || x == y)
			return;

		removeNeighbor(x, y);
		removeNeighbor(y, x);
	}

	@Override
	public boolean isConnected(int x, int y) {
		List<Integer> neighborsOfNode = neighbors.get(y);
		return binarySearch(neighborsOfNode, x) >= 0;
	}

	@Override
	public void setGraph(Graph other) {
		requireNonNull(other);
		int numNodes = numNodes();
		if (numNodes != other.numNodes())
			throw new IllegalArgumentException("Network sizes differ: " + numNodes + " vs " + other.numNodes());

		for (int y = 0; y < numNodes; y++) {
			neighbors.get(y).clear();
			for (int x = 0; x < numNodes; x++)
				if (x != y && other.isConnected(x, y))
					neighbors.get(y).add(x);
		}
	}

	public int numNeighbors(int node)           { return neighbors.get(node).size(); }

	public int getNeighbor(int node, int index) { return neighbors.get(node).get(index); }

	private static void addNeighbor(List<Integer> neighborsOfNode, int newNeighbor) {
		int insertionPoint = binarySearch(neighborsOfNode, newNeighbor);
		assert insertionPoint < 0;
		neighborsOfNode.add(-insertionPoint - 1, newNeighbor);
	}

	private void removeNeighbor(int node1, int node2) {
		Integer removed = neighbors.get(node1).remove(node2);
		assert removed != null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(numNodes() * GraphUtilities.countConnections(this) * 4);

		for (int y = 0; y < neighbors.size(); y++) {
			if (y > 0)
				sb.append('\n');

			sb.append(y).append(' ');
			List<Integer> neighbors = this.neighbors.get(y);
			for (int x : neighbors)
				sb.append(' ').append(x);
		}
		return sb.toString();
	}

	// Modified from Collections.binarySearch()
	private static int binarySearch(List<Integer> list, int key) {
		int low  = 0;
		int high = list.size() - 1;

		while (low <= high) {
			int mid    = (low + high) >>> 1;
			int midVal = list.get(mid);

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid;
		}
		return -(low + 1);
	}
}
