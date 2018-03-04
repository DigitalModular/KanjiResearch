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
package org.digitalmodular.kanjiresearch.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reader/writer for our component&rarr;kanji mapping (<tt>*-component-kanji.utf8</tt>) file format.
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-15
public final class ComponentFileIO {
	private ComponentFileIO() { throw new AssertionError(); }

	public static void write(Collection<TaggedKanjiList> components, String filename) throws IOException {
		List<String> lines = components.stream()
		                               .map(ComponentFileIO::toComponentString)
		                               .collect(Collectors.toList());

		Files.write(Paths.get(filename), lines, StandardCharsets.UTF_8);
	}

	public static List<TaggedKanjiList> read(String filename) throws IOException {
		return Files.lines(Paths.get(filename), StandardCharsets.UTF_8)
		            .map(ComponentFileIO::fromComponentString)
		            .collect(Collectors.toList());
	}

	private static String toComponentString(TaggedKanjiList entry) {
		return entry.getComponentAsString() + '\t' + entry.getKanjiString();
	}

	private static TaggedKanjiList fromComponentString(String line) {
		TaggedKanjiList entry = new TaggedKanjiList(line.codePointAt(0));

		line.codePoints()
		    .skip(2)
		    .forEach(entry::add);

		return entry;
	}
}
