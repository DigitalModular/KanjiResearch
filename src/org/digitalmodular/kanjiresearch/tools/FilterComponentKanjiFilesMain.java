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
package org.digitalmodular.kanjiresearch.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.digitalmodular.kanjiresearch.util.ComponentFileIO;
import org.digitalmodular.kanjiresearch.util.KanjiList;
import org.digitalmodular.kanjiresearch.util.KanjiSetFileIO;
import org.digitalmodular.kanjiresearch.util.RadKFileIO;
import org.digitalmodular.kanjiresearch.util.TaggedKanjiList;

/**
 * Filters the component list to only the kanji of each set.
 * <p>
 * Requires: <tt>radkfilex</tt>, <tt>*-set.utf8</tt>
 * <p>
 * Produces: <tt>*-components.utf8</tt>, <tt>*-component-frequencies.tsv</tt>
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-17
public final class FilterComponentKanjiFilesMain {
	public static void main(String... args) throws IOException {
		System.setProperty("line.separator", "\n");

		String[] filenames = Files.list(Paths.get("kanjisets"))
		                          .map(Path::toString)
		                          .filter(filename -> filename.endsWith("-set.utf8"))
		                          .toArray(String[]::new);

		for (String filename : filenames)
			process(filename);
	}

	private static void process(String filenameIn) throws IOException {
		KanjiList                   kanjiSet       = KanjiSetFileIO.read(filenameIn);
		Collection<TaggedKanjiList> componentLists = RadKFileIO.read("componentsets/radkfilex", "EUC-JP");

		componentLists.forEach(componentList -> componentList.retainAll(kanjiSet));

		makeComponentFrequencyFile(filenameIn, componentLists);
		// This one last because it modifies 'componentLists'.
		makeComponentsFile(filenameIn, componentLists);
	}

	private static void makeComponentsFile(String filenameIn, Collection<TaggedKanjiList> components)
			throws IOException {
		String filenameOut = makeFilename(filenameIn, "components-filtered-per-set", "-components.utf8");
		ComponentFileIO.write(components, filenameOut);

		System.out.println(filenameIn + " -> " + filenameOut);
	}

	private static void makeComponentFrequencyFile(String filenameIn, Collection<TaggedKanjiList> componentLists)
			throws IOException {
		AtomicInteger index     = new AtomicInteger();
		String        firstLine = "ComponentID\tComponent\tCount";
		List<String> lines = Stream.concat(Stream.of(firstLine),
		                                   componentLists.stream()
		                                                 .map(entry -> toFrequencyString(entry, index)))
		                           .collect(Collectors.toList());

		String filenameOut = makeFilename(filenameIn, "component-frequencies-per-set", "-component-frequencies.tsv");
		Files.write(Paths.get(filenameOut), lines);

		System.out.println(filenameIn + " -> " + filenameOut);
	}

	private static String toFrequencyString(TaggedKanjiList entry, AtomicInteger index) {
		return index.incrementAndGet() + "\t" +
		       entry.getComponentAsString() + '\t' +
		       Integer.toString(entry.size());
	}

	private static String makeFilename(String filenameIn, CharSequence directory, CharSequence suffix) {
		//noinspection DynamicRegexReplaceableByCompiledPattern // Suppress IntelliJ Bug (this is not a regex)
		return filenameIn.replace("kanjisets", directory)
		                 .replace("-set.utf8", suffix);
	}
}
