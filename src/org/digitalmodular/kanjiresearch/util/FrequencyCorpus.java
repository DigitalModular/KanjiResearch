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

import java.util.Formatter;
import static java.util.Objects.requireNonNull;

/**
 * @author Mark Jeronimus
 */
// Created 2018-03-13
public class FrequencyCorpus {
	private final String[] phrases;
	private final double[] frequencies;

	private double totalFrequency = 0;

	FrequencyCorpus(String[] phrases, double[] frequencies) {
		requireNonNull(phrases, "phrases");
		requireNonNull(frequencies, "frequencies");
		if (phrases.length != frequencies.length)
			throw new IllegalArgumentException("Length mismatch: " + phrases.length + " vs " + frequencies.length);
		if (phrases.length < 1)
			throw new IllegalArgumentException("Length should be at least 1: " + phrases.length);

		this.phrases = phrases;
		this.frequencies = frequencies;

		for (double frequency : frequencies)
			totalFrequency += frequency;
	}

	public int size() { return phrases.length; }

	public String getPhrase(int index) {
		if (index < 0 || index >= phrases.length)
			throw new IndexOutOfBoundsException("'index' must be in the range [0, " + (phrases.length - 1) + ']');

		return phrases[index];
	}

	public double getFrequency(int index) {
		if (index < 0 || index >= phrases.length)
			throw new IndexOutOfBoundsException("'index' must be in the range [0, " + (phrases.length - 1) + ']');

		return frequencies[index];
	}

	public double getNormalizedFrequency(int index) {
		if (index < 0 || index >= phrases.length)
			throw new IndexOutOfBoundsException("'index' must be in the range [0, " + (phrases.length - 1) + ']');

		return frequencies[index] / frequencies[0];
	}

	public double getRelativeFrequency(int index) {
		if (index < 0 || index >= phrases.length)
			throw new IndexOutOfBoundsException("'index' must be in the range [0, " + (phrases.length - 1) + ']');

		return frequencies[index] / totalFrequency;
	}

	@Override
	public String toString() {
		try (Formatter formatter = new Formatter(new StringBuilder(phrases.length * 30))) {
			for (int i = 0; i < phrases.length; i++)
				formatter.format("%s\t%.8f\n", phrases[i], getRelativeFrequency(i));

			return formatter.toString();
		}
	}
}
