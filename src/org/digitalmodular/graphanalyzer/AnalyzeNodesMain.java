package org.digitalmodular.graphanalyzer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;

import org.digitalmodular.graphanalyzer.statistic.AveragePathLengthCalculator;
import org.digitalmodular.graphanalyzer.statistic.BansalClusteringCoefficientCalculator;
import org.digitalmodular.graphanalyzer.statistic.NodeDegreeCalculator;
import org.digitalmodular.graphanalyzer.statistic.SpreadingSpeedCalculator;
import org.digitalmodular.graphapi.GraphIO;
import org.digitalmodular.graphapi.GraphUtilities;
import org.digitalmodular.graphapi.IsolatedSubGraphFinder;
import org.digitalmodular.graphapi.NeighborGraph;

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

		int[][] permutations = IsolatedSubGraphFinder.findIsolatedSubGraphs(graph);
		Arrays.sort(permutations, Comparator.comparingInt((int[] i) -> i.length).reversed());

		graph = graph.permute(permutations[0]);

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
