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
package org.digitalmodular.graphanalyzer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import org.digitalmodular.graphanalyzer.statistic.AveragePathLengthCalculator;
import org.digitalmodular.graphanalyzer.statistic.BansalClusteringCoefficientCalculator;
import org.digitalmodular.graphanalyzer.statistic.NodeDegreeCalculator;
import org.digitalmodular.graphanalyzer.statistic.SpreadingSpeedCalculator;
import org.digitalmodular.graphapi.GraphIO;
import org.digitalmodular.graphapi.GraphUtilities;
import org.digitalmodular.graphapi.NeighborGraph;
import org.digitalmodular.graphapi.SubGraphSplitter;

/**
 * @author Mark Jeronimus
 */
// Created 2018-02-28
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class AnalyzeNodesMain {
	private static final NodeDegreeCalculator                  ND  = NodeDegreeCalculator.INSTANCE;
	private static final BansalClusteringCoefficientCalculator CC  = BansalClusteringCoefficientCalculator.INSTANCE;
	private static final AveragePathLengthCalculator           APL = AveragePathLengthCalculator.INSTANCE;
	private static final SpreadingSpeedCalculator              SS  = new SpreadingSpeedCalculator(1, 1, 1, 1);

	public static void main(String... args) throws IOException, InterruptedException {
		String[] filenames = Files.list(Paths.get("kanjigraphs"))
//		                          .filter(path -> {
//			                          try {
//				                          return Files.size(path) > 10_000_000;
//			                          } catch (IOException ignored) {
//				                          return false;
//			                          }
//		                          })
		                          .sorted(Comparator.comparingLong(path -> {
			                          try {
				                          return Files.size(path);
			                          } catch (IOException ignored) {
				                          return Long.MAX_VALUE;
			                          }
		                          }))
		                          .map(Path::toString)
		                          .filter(filename -> filename.endsWith("-graph.conn"))
		                          .toArray(String[]::new);

		for (String filename : filenames)
			analyze(filename);
	}

	private static void analyze(String filenameIn) throws IOException {
		Benchmark.start();
		NeighborGraph graph = GraphUtilities.toNeighborGraph(GraphIO.read(filenameIn));
		Benchmark.record("load");

		graph = SubGraphSplitter.splitGraph(graph).get(0);
		int size = graph.size();
		Benchmark.record("subGraph");

		double[] nd = ND.calculateAll(graph);
		Benchmark.record("ND");
		double[] cc = CC.calculateAll(graph);
		Benchmark.record("CC");
		double[] apl = APL.calculateAll(graph);
		Benchmark.record("APL");
		double[] ss = SS.calculateAll(graph);
		Benchmark.record("SS");
		Benchmark.printResults(size);

		String filenameOut = makeFilename(filenameIn, "graphstatistics", "-statistics.tsv");
		try (BufferedWriter out = Files.newBufferedWriter(Paths.get(filenameOut))) {
			out.write("i\tDegree\tInterconnections\tCC\tAPL\tSS\n");

			for (int i = 0; i < size; i++) {
				int ic = (int)Math.rint(nd[i] * (nd[i] - 1) / 2 * cc[i]);
				out.write(String.format("%d\t%d\t%d\t%7.5f\t%7.5f\t%7.5f\n", i,
				                        (int)nd[i], ic, cc[i], apl[i], ss[i]));
			}

			System.out.println(filenameIn + " -> " + filenameOut);
		}
	}

	private static String makeFilename(String filenameIn, CharSequence directory, CharSequence suffix) {
		//noinspection DynamicRegexReplaceableByCompiledPattern // Suppress IntelliJ Bug (this is not a regex)
		return filenameIn.replace("kanjigraphs", directory)
		                 .replace("-graph.conn", suffix);
	}
}
