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
package org.digitalmodular.graphanalyzer;

import java.util.concurrent.ThreadLocalRandom;

import org.digitalmodular.graphapi.NeighborGraph;

/**
 * @author Mark Jeronimus
 */
// Created 2018-02-04
public final class GraphFactory {
	private GraphFactory() { throw new AssertionError(); }

	public static NeighborGraph newRandomGraph(int numNodes, int numConnections) {
		if (numNodes < 1)
			throw new IllegalArgumentException("numNodes should be at least 1: " + numNodes);
		if (numConnections < 0 || numConnections > numNodes * (numNodes - 1) / 2)
			throw new IllegalArgumentException("'numConnections' should be in the range [0, " +
			                                   (numNodes * (numNodes - 1) / 2) + "]: " + numNodes);

		NeighborGraph graph = new NeighborGraph(numNodes);
		for (int i = 0; i < numConnections; i++) {
			int x;
			int y;
			do {
				x = ThreadLocalRandom.current().nextInt(numNodes);
				y = ThreadLocalRandom.current().nextInt(numNodes);
			} while (x == y || graph.isConnected(x, y));

			graph.setConnection(x, y);
		}
		return graph;
	}
}
