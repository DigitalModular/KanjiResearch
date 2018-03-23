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
import java.util.Collection;

import org.digitalmodular.kanjiresearch.util.CorpusFrequencyIO;
import org.digitalmodular.kanjiresearch.util.FrequencyCorpus;
import org.digitalmodular.kanjiresearch.util.FrequencyCorpusBuilder;
import org.digitalmodular.kanjiresearch.util.KanjiUtilities;
import org.digitalmodular.kanjiresearch.util.RadKFileIO;
import org.digitalmodular.kanjiresearch.util.TaggedKanjiList;

/**
 * @author Mark Jeronimus
 */
// Created 2018-03-15
public final class MakeFrequencyCorporaMain {
	private MakeFrequencyCorporaMain() {throw new AssertionError(); }

	public static void main(String... args) throws IOException {
		FrequencyCorpus rawFrequency = CorpusFrequencyIO.read("corpora/internet-jp-forms.tsv");

		FrequencyCorpus wordFrequency = makeWordFrequency(rawFrequency);
		CorpusFrequencyIO.write(wordFrequency, "corpora/wordFrequency.tsv");

		FrequencyCorpus kanjiFrequency = makeKanjiFrequency(wordFrequency);
		CorpusFrequencyIO.write(kanjiFrequency, "corpora/kanjiFrequency.tsv");
	}

	private static FrequencyCorpus makeWordFrequency(FrequencyCorpus rawFrequency) {
		FrequencyCorpusBuilder wordFrequency = new FrequencyCorpusBuilder(rawFrequency.size());

		for (int i = 0; i < rawFrequency.size(); i++) {
			String phrase = rawFrequency.getPhrase(i);

			if (phrase.codePoints().noneMatch(MakeFrequencyCorporaMain::isCharacterToKeep))
				continue;

			wordFrequency.add(phrase, rawFrequency.getFrequency(i));
		}

		return wordFrequency.build();
	}

	@SuppressWarnings("ObjectEquality") // Comparing identity, not equality.
	private static boolean isCharacterToKeep(int codePoint) {
		return KanjiUtilities.isKanji(codePoint) ||
		       KanjiUtilities.isKana(codePoint);
	}

	private static FrequencyCorpus makeKanjiFrequency(FrequencyCorpus wordFrequency) {
		FrequencyCorpusBuilder kanjiFrequency = new FrequencyCorpusBuilder(wordFrequency.size());

		for (int i = 0; i < wordFrequency.size(); i++) {
			int index = i;
			wordFrequency.getPhrase(i)
			             .codePoints()
			             .filter(KanjiUtilities::isKanji)
			             .forEach(codePoint -> kanjiFrequency.add(new String(new int[]{codePoint}, 0, 1),
			                                                      wordFrequency.getFrequency(index)));
		}

		return kanjiFrequency.build();
	}
}
