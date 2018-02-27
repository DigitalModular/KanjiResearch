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
package org.digitalmodular.kanjiresearch.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.digitalmodular.graphapi.Graph;
import org.digitalmodular.graphapi.GraphIO;
import org.digitalmodular.graphapi.MatrixGraph;
import org.digitalmodular.kanjiresearch.util.ComponentFileIO;
import org.digitalmodular.kanjiresearch.util.KanjiList;
import org.digitalmodular.kanjiresearch.util.TaggedKanjiList;

/**
 * Creates graphs where kanji are nodes and shared components are connections.
 * <p>
 * Requires: <tt>*-components.utf8</tt>
 * <p>
 * Produces: <tt>*-graph.conn</tt>, <tt>*-graph.txt</tt>
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-13
public final class MakeKanjiGraphsMain {
	public static void main(String... args) throws IOException {
		System.setProperty("line.separator", "\n");

		String[] filenames = Files.list(Paths.get("components-filtered-per-set"))
		                          .map(Path::toString)
		                          .filter(filename -> filename.endsWith("-components.utf8"))
		                          .toArray(String[]::new);

		for (String filename : filenames)
			process(filename);
	}

	private static void process(String filenameIn) throws IOException {
		List<TaggedKanjiList> componentLists = ComponentFileIO.read(filenameIn);

		KanjiList kanjiSet = collectKanji(componentLists);

		Graph graph = new MatrixGraph(kanjiSet.size());

		for (TaggedKanjiList componentList : componentLists)
			addCluster(componentList, kanjiSet, graph);

		write(graph, filenameIn, "-graph.txt");
		write(graph, filenameIn, "-graph.conn");
		if (graph.numNodes() < 3000)
			write(graph, filenameIn, "-graph.png");
	}

	private static KanjiList collectKanji(Iterable<? extends KanjiList> componentLists) {
		KanjiList kanjiSet = new KanjiList();
		componentLists.forEach(kanjiSet::addAll);
		return kanjiSet;
	}

	private static void addCluster(KanjiList componentList, KanjiList kanjiSet, Graph graph) {
		int n = componentList.size();
		for (int i = 1; i < n; i++) {
			int node1 = kanjiSet.indexOf(componentList.get(i));
			for (int j = 0; j < i; j++) {
				int node2 = kanjiSet.indexOf(componentList.get(j));

				graph.setConnection(node1, node2);
			}
		}
	}

	private static void write(Graph graph, String filenameIn, CharSequence suffix) throws IOException {
		String filenameOut = makeFilename(filenameIn, "kanjigraphs", suffix);
		GraphIO.write(graph, filenameOut);

		System.out.println(filenameIn + " -> " + filenameOut);
	}

	private static String makeFilename(String filenameIn, CharSequence directory, CharSequence suffix) {
		//noinspection DynamicRegexReplaceableByCompiledPattern // Suppress IntelliJ Bug (this is not a regex)
		return filenameIn.replace("components-filtered-per-set", directory)
		                 .replace("-components.utf8", suffix);
	}
}
