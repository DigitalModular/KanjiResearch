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

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.Character.UnicodeBlock;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Mark Jeronimus
 */
// Created 2018-03-13
public final class CorpusFrequencyIO {
	private CorpusFrequencyIO() { throw new AssertionError(); }

	private static final Pattern CSV_SPLITTER = Pattern.compile("\\s+|(\\s*,\\s*)");

	public static void write(CorpusFrequency corpusFrequency, String filename) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename))) {
			int n = corpusFrequency.size();
			for (int i = 0; i < n; i++) {
				writer.write(corpusFrequency.getPhrase(i));
				writer.write('\t');
				writer.write(Double.toString(corpusFrequency.getFrequency(i)));
				writer.write('\n');
			}
		}
	}

	public static CorpusFrequency read(String filename) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(filename));

		CorpusFrequency corpus = new CorpusFrequency(lines.size());

		for (String line : lines)
			decodeLine(line, corpus);

		return corpus;
	}

	private static void decodeLine(CharSequence line, CorpusFrequency corpus) throws IOException {
		String[] parts = CSV_SPLITTER.split(line);
		if (parts.length != 2)
			throw new IOException("Expected two columns: \"" + line + '"');

		try {
			String phrase = parts[0];

			if (phrase.codePoints().noneMatch(CorpusFrequencyIO::characterToKeep))
				return;

			corpus.add(phrase, Double.parseDouble(parts[1]));
		} catch (NumberFormatException ignored) {
			throw new IOException("Not a frequency: \"" + line + '"');
		}
	}

	@SuppressWarnings("ObjectEquality") // Comparing identity, not equality.
	private static boolean characterToKeep(int codePoint) {
		UnicodeBlock block = UnicodeBlock.of(codePoint);

		if (block != UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS &&
		    block != UnicodeBlock.KATAKANA &&
		    block != UnicodeBlock.HIRAGANA &&
		    block != UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION &&
		    block != UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A &&
		    block != UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B &&
		    block != UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C &&
		    block != UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D &&
		    block != UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS &&
		    block != UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT) {
			return false;
		}

		int type = Character.getType(codePoint);

		return type == Character.OTHER_LETTER ||
		       type == Character.MODIFIER_LETTER;
	}
}
