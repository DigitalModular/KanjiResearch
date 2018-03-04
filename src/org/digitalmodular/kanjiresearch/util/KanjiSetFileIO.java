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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Reader/writer for our kanji-set (<tt>*-set.utf8</tt>) file format.
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-15
public final class KanjiSetFileIO {
	private KanjiSetFileIO() { throw new AssertionError(); }

	public static void write(KanjiList kanjiList, String filename) throws IOException {
		int[]  codePoints = kanjiList.toArray();
		String line       = new String(codePoints, 0, codePoints.length);

		Files.write(Paths.get(filename), Collections.singletonList(line), StandardCharsets.UTF_8);
	}

	public static KanjiList read(String filename) throws IOException {
		List<Integer> codePoints = Files.lines(Paths.get(filename), StandardCharsets.UTF_8)
		                                .flatMapToInt(String::codePoints)
		                                .distinct()
		                                .boxed()
		                                .collect(Collectors.toList());

		KanjiList kanjiList = new KanjiList();
		kanjiList.addAll(codePoints);
		return kanjiList;
	}
}
