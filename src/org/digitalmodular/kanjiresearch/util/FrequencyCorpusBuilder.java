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

import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Mark Jeronimus
 */
// Created 2018-03-17
public class FrequencyCorpusBuilder {
	private static final BiFunction<Double, Double, Double> SUM_BOXED_DOUBLES     = (a, b) -> a + b;
	private static final Comparator<Entry<String, Double>>  REVERSE_SORT_BY_VALUE =
			Comparator.comparing((Function<Entry<String, Double>, Double>)Entry::getValue)
			          .reversed()
			          .thenComparing((Function<Entry<String, Double>, String>)Entry::getKey);

	private final Map<String, Double> frequencyCorpus;

	public FrequencyCorpusBuilder(int initialCapacity) {
		frequencyCorpus = new HashMap<>(initialCapacity);
	}

	public void add(String phrase, double frequency) {
		if (frequency <= 0)
			throw new IllegalArgumentException("Frequency not positive: {" + phrase + ", " + frequency + '}');

		frequencyCorpus.merge(phrase, frequency, SUM_BOXED_DOUBLES);
	}

	public FrequencyCorpus build() {
		List<Entry<String, Double>> sorted = frequencyCorpus.entrySet()
		                                                    .stream()
		                                                    .sorted(REVERSE_SORT_BY_VALUE)
		                                                    .collect(Collectors.toList());

		String[] phrases = sorted.stream()
		                         .map(Entry::getKey)
		                         .toArray(String[]::new);

		double[] frequencies = sorted.stream()
		                             .mapToDouble(Entry::getValue)
		                             .toArray();

		return new FrequencyCorpus(phrases, frequencies);
	}

	@Override
	public String toString() {
		try (Formatter formatter = new Formatter(new StringBuilder(frequencyCorpus.size() * 30))) {
			for (Entry<String, Double> entry : frequencyCorpus.entrySet())
				formatter.format("%s\t%.8f\n", entry.getKey(), entry.getValue());

			return formatter.toString();
		}
	}
}
