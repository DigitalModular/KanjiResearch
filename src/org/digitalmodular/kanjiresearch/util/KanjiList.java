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
package org.digitalmodular.kanjiresearch.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mark Jeronimus
 */
// Created 2018-02-17
public class KanjiList {
	private final List<Integer> kanji = new ArrayList<>(256);

	public KanjiList() { }

	@SuppressWarnings("OverridableMethodCallDuringObjectConstruction")
	public KanjiList(Iterable<Integer> kanji) {
		addAll(kanji);
	}

	public void add(Integer codePoint) {
		int i = Collections.binarySearch(kanji, codePoint);

		// Workaround for bug in kradfile: some kanji have a radical listed more than once, hence in radkfile some
		// kanji are listed multiple times the same radical. (e.g. 場)
		if (i >= 0)
			return;

		kanji.add(-i - 1, codePoint);
	}

	public void addAll(Iterable<Integer> codePoints) {
		for (Integer e : codePoints)
			add(e);
	}

	public int size()                      { return kanji.size(); }

	public boolean isEmpty()               { return kanji.isEmpty(); }

	public Integer get(int index)          { return kanji.get(index); }

	public void retainAll(KanjiList other) { kanji.retainAll(other.kanji); }

	public int indexOf(Integer codePoint)  { return Collections.binarySearch(kanji, codePoint); }

	public int[] toArray() {
		return kanji.stream()
		            .mapToInt(Integer::intValue)
		            .toArray();
	}

	public String getKanjiString() {
		int[] array = toArray();
		return new String(array, 0, array.length);
	}

	/**
	 * @return An array with in the first element all kanji in this list but not in {@code other}, in the second index
	 * all kanji found in the {@code other} list but not in this, and in the third list all kanji in both lists.
	 */
	public KanjiList[] differences(KanjiList other) {
		List<Integer> thisNotOther = new ArrayList<>(kanji);
		List<Integer> otherNotThis = new ArrayList<>(other.kanji);
		List<Integer> inBoth       = new ArrayList<>(kanji);
		thisNotOther.removeAll(other.kanji);
		otherNotThis.removeAll(kanji);
		inBoth.retainAll(other.kanji);

		return new KanjiList[]{new KanjiList(thisNotOther), new KanjiList(otherNotThis), new KanjiList(inBoth)};
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		KanjiList kanjiList = (KanjiList)o;
		return kanji.equals(kanjiList.kanji);
	}

	@Override
	public int hashCode() {
		return kanji.hashCode();
	}

	@Override
	public String toString() {
		int[] array = toArray();
		if (array.length <= 10)
			return new String(array, 0, array.length);
		else
			return new String(array, 0, 10) + "...";
	}
}
