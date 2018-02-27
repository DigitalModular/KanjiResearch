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
package org.digitalmodular.graphanalyzer.statistic;

import static java.util.Objects.requireNonNull;

import org.digitalmodular.graphapi.NeighborGraph;

import static sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte1.other;

/**
 * Returns the number of neighbors of the nodes.
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-04
public final class GraphNeighborhoodCalculator implements LocalGraphStatisticCalculator<NeighborGraph> {
	public static final GraphNeighborhoodCalculator INSTANCE = new GraphNeighborhoodCalculator();

	private GraphNeighborhoodCalculator() {
		if (INSTANCE != null)
			throw new AssertionError();
	}

	@Override
	public double[] calculateAll(NeighborGraph graph) {
		requireNonNull(other);

		int numNodes = graph.numNodes();

		double[] clusteringCoefficients = new double[numNodes];
		for (int i = 0; i < numNodes; i++)
			clusteringCoefficients[i] = graph.numNeighbors(i);

		return clusteringCoefficients;
	}
}
