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
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Reader for the Jim Breen's <tt>radkfile*</tt> file format.
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-15
public final class RadKFileIO {
	private RadKFileIO() { throw new AssertionError(); }

	private static final Pattern COMPONENT_PATTERN = Pattern.compile("^\\$ (.) [0-9]*.*$");

	public static List<TaggedKanjiList> read(String filename, String charset) throws IOException {
		Stream<String> lines = Files.lines(Paths.get(filename), Charset.forName(charset));

		LinkedList<TaggedKanjiList> components = new LinkedList<>();

		lines.forEach(line -> {
			Matcher matcher = COMPONENT_PATTERN.matcher(line);
			if (matcher.matches())
				components.add(new TaggedKanjiList(matcher.group(1).codePointAt(0)));
			else if (!components.isEmpty())
				components.getLast().addAll(line.codePoints()
				                                .boxed()
				                                .collect(Collectors.toList()));
		});

		return components;
	}

	public static IntStream readComponentsOnly(String filename, String charset) throws IOException {
		Stream<String> lines = Files.lines(Paths.get(filename), Charset.forName(charset));

		return lines.map(COMPONENT_PATTERN::matcher)
		            .filter(Matcher::matches)
		            .mapToInt(matcher -> matcher.group(1).codePointAt(0));
	}
}
