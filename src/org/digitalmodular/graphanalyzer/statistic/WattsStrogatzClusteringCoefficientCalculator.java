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

import static java.util.Objects.requireNonNull;

import org.digitalmodular.graphapi.NeighborGraph;

/**
 * Watts, D. J. & Strogatz, S. H. (1998), ‘Collective dynamics of ‘small-world’ networks’, Nature 393(1), 440–442
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-04
public final class WattsStrogatzClusteringCoefficientCalculator
		implements LocalGraphStatisticCalculator<NeighborGraph> {
	public static final WattsStrogatzClusteringCoefficientCalculator INSTANCE =
			new WattsStrogatzClusteringCoefficientCalculator();

	private WattsStrogatzClusteringCoefficientCalculator() {
		if (INSTANCE != null)
			throw new AssertionError();
	}

	@Override
	public String getName() { return "Clustering Coefficient"; }

	@Override
	public String getAbbreviation() { return "CC"; }

	@Override
	public double[] calculateAll(NeighborGraph graph) {
		requireNonNull(graph);

		int size = graph.size();

		double[] clusteringCoefficients = new double[size];
		for (int i = 0; i < size; i++)
			clusteringCoefficients[i] = calculate(graph, i);

		return clusteringCoefficients;
	}

	public static double calculate(NeighborGraph graph, int node) {
		int numNeighbors = graph.numNeighbors(node);
		if (numNeighbors < 2)
			return 0;

		int numConnectionsBetweenNeighbors = 0;
		for (int y = 0; y < numNeighbors; y++) {
			int neighbor1 = graph.getNeighbor(node, y);
			for (int x = 0; x < y; x++) {
				int neighbor2 = graph.getNeighbor(node, x);
				if (graph.isConnected(neighbor1, neighbor2))
					numConnectionsBetweenNeighbors++;
			}
		}

		int    maximumConnections    = numNeighbors * (numNeighbors - 1) / 2;
		double clusteringCoefficient = numConnectionsBetweenNeighbors / (double)maximumConnections;
		return clusteringCoefficient;
	}
}
