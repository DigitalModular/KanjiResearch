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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.digitalmodular.kanjiresearch.util.KanjiList;
import org.digitalmodular.kanjiresearch.util.KanjiSetFileIO;
import org.digitalmodular.kanjiresearch.util.RadKFileIO;
import org.digitalmodular.kanjiresearch.util.TaggedKanjiList;

/**
 * Extracts unique kanji from various sources.
 * <p>
 * Requires: <tt>radkfile</tt>, <tt>radkfile2</tt>, <tt>radkfilex</tt>
 * <p>
 * Produces: <tt>jisx0208-set.utf8</tt>, <tt>jisx0212-set.utf8</tt><tt>jisx0208+jisx0212-set.utf8</tt>
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-13
public final class MakeKanjiSetsMain {
	public static void main(String... args) throws IOException {
		System.setProperty("line.separator", "\n");

		fromRadKFile("componentsets/radkfile", "kanjisets/jisx0208-set.utf8");
		fromRadKFile("componentsets/radkfile2", "kanjisets/jisx0212-set.utf8");
		fromRadKFile("componentsets/radkfilex", "kanjisets/jisx0208+jisx0212-set.utf8");
	}

	private static void fromRadKFile(String filenameIn, String filenameOut) throws IOException {
		List<TaggedKanjiList> componentKanji = RadKFileIO.read(filenameIn, "EUC-JP");

		KanjiList kanjiSet = convert(componentKanji);
		KanjiSetFileIO.write(kanjiSet, filenameOut);

		System.out.println(filenameIn + " -> " + filenameOut);
	}

	private static KanjiList convert(Collection<TaggedKanjiList> componentLists) {
		return componentLists.stream()
		                     .map(KanjiList::toArray)
		                     .flatMapToInt(Arrays::stream)
		                     .distinct()
		                     .sorted()
		                     .collect(KanjiList::new,
		                              KanjiList::add,
		                              null);
	}
}
