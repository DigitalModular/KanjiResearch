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

import java.util.Arrays;
import static java.util.Objects.requireNonNull;

import org.digitalmodular.graphapi.NeighborGraph;

/**
 * @author Mark Jeronimus
 */
// Created 2018-02-04
public class SpreadingSpeedCalculator implements LocalGraphStatisticCalculator<NeighborGraph> {
	/** The amount that the seed node gets before iterating. */
	private final double seedValue;
	/** The weight factor for the amount that gets transferred during one iteration. */
	private final double transferProbability;
	/** The amount a node should have before it's considered 'complete' . */
	private final double targetValue;
	/** The fraction of the number of nodes that must be 'complete' before iteration finishes. */
	private final double finishFactor;

	public SpreadingSpeedCalculator(double seedValue,
	                                double transferProbability,
	                                double targetValue,
	                                double finishFactor) {
		if (seedValue <= 0 || seedValue > 1)
			throw new IllegalArgumentException(
					"'seedValue' should be in the range (0, 1]: " + seedValue);
		if (transferProbability <= 0 || transferProbability > 1)
			throw new IllegalArgumentException(
					"'transferProbability' should be in the range (0, 1]: " + transferProbability);
		if (targetValue <= 0 || targetValue > 1)
			throw new IllegalArgumentException(
					"'targetValue' should be in the range (0, 1]: " + targetValue);
		if (finishFactor <= 0 || finishFactor > 1)
			throw new IllegalArgumentException(
					"'finishFactor' should be in the range (0, 1]: " + finishFactor);

		this.seedValue = seedValue;
		this.transferProbability = transferProbability;
		this.targetValue = targetValue;
		this.finishFactor = finishFactor;
	}

	@Override
	public String getName() { return "Spreading Speed"; }

	@Override
	public String getAbbreviation() { return "SS"; }

	@Override
	public double[] calculateAll(NeighborGraph graph) {
		requireNonNull(graph);

		int numNodes = graph.numNodes();

		double[] spreadingTime = new double[numNodes];
		for (int node = 0; node < numNodes; node++)
			spreadingTime[node] = calculate(graph, node);

		return spreadingTime;
	}

	public double calculate(NeighborGraph graph, int node) {
		int numNodes = graph.numNodes();

		double[]  valuesOfLastIteration  = new double[numNodes];
		double[]  values                 = new double[numNodes];
		boolean[] sendersOfLastIteration = new boolean[numNodes];
		boolean[] senders                = new boolean[numNodes];
		boolean[] receivers              = new boolean[numNodes];
		boolean[] hasMessage             = new boolean[numNodes];

		Arrays.fill(receivers, true);

		values[node] = seedValue;
		senders[node] = true;
		receivers[node] = seedValue < 1;
		hasMessage[node] = seedValue >= targetValue;

		int remaining = (int)StrictMath.ceil(finishFactor * numNodes);
		if (hasMessage[node])
			remaining--;

		if (remaining == 0)
			return 0;

		int limit = numNodes * (numNodes - 1) / 2;
		for (int step = 1; step < limit; step++) {
			System.arraycopy(values, 0, valuesOfLastIteration, 0, numNodes);
			System.arraycopy(senders, 0, sendersOfLastIteration, 0, numNodes);

			boolean changed = false;

			// Find nodes that send.
			for (int sendingNode = 0; sendingNode < numNodes; sendingNode++) {
				if (!sendersOfLastIteration[sendingNode])
					continue;

				int numNeighbors = graph.numNeighbors(sendingNode);
				for (int neighbor = 0; neighbor < numNeighbors; neighbor++) {
					int receivingNode = graph.getNeighbor(sendingNode, neighbor);
					if (!receivers[receivingNode])
						continue;

					values[receivingNode] += transferProbability * valuesOfLastIteration[sendingNode];
					senders[receivingNode] = true;

					if (values[receivingNode] >= targetValue && !hasMessage[receivingNode]) {
						remaining--;
						hasMessage[receivingNode] = true;
					}

					if (values[receivingNode] >= 1) {
						values[receivingNode] = 1;
						receivers[receivingNode] = false;
					}

					changed = true;
				}
			}

			if (remaining <= 0)
				return step;

			if (!changed)
				return Double.NaN;
		}

		throw new AssertionError("Iteration overflow");
	}
}
