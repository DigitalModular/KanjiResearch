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

/**
 * @author Mark Jeronimus
 */
// Created 2018-03-13
public class CorpusFrequency {
	private final String[] phrases;
	private final double[] frequencies;

	private int    size           = 0;
	private double totalFrequency = 0;

	public CorpusFrequency(int capacity) {
		phrases = new String[capacity];
		frequencies = new double[capacity];
	}

	public void add(String phrase, double frequency) {
		if (size == phrases.length)
			throw new IllegalStateException("Capacity reached: " + size);

		if (frequency <= 0)
			throw new IllegalArgumentException("Frequency not positive: {" + phrase + ", " + frequency + '}');

		phrases[size] = phrase;
		frequencies[size] = frequency;

		size++;
		totalFrequency += frequency;
	}

	public int size()                               { return size; }

	public String getPhrase(int index)              { return phrases[index]; }

	public double getFrequency(int index)           { return frequencies[index]; }

	public double getNormalizedFrequency(int index) { return frequencies[index] / totalFrequency; }

	public double getLogFrequency(int index)        { return Math.log(frequencies[index] / totalFrequency); }

	@Override
	public String toString() {
		try (Formatter formatter = new Formatter(new StringBuilder(size * 30))) {
			for (int i = 0; i < size; i++)
				formatter.format("%s\t%.8f\n", phrases[i], frequencies[i] / totalFrequency);

			return formatter.toString();
		}
	}
}
