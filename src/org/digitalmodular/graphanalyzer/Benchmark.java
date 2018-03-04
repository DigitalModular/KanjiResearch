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
package org.digitalmodular.graphanalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mark Jeronimus
 */
// Created 2015-09-08
@SuppressWarnings("UseOfSystemOutOrSystemErr")
public final class Benchmark {
	private Benchmark() { throw new AssertionError(); }

	private static final List<Long>   times              = new ArrayList<>(16);
	private static final List<String> descriptions       = new ArrayList<>(16);
	private static       int          longestDescription = 0;

	public static void start() {
		times.clear();
		descriptions.clear();
		longestDescription = 0;

		times.add(System.nanoTime());
	}

	public static void record(String description) {
		descriptions.add(description);
		longestDescription = Math.max(longestDescription, description.length());

		times.add(System.nanoTime());
	}

	public static void printResults(int workSize) {
		String formatString = "%-" + longestDescription + "s(%d) %,9.3f (%f·N)\n";
		for (int i = 0; i < descriptions.size(); i++) {
			double duration            = (times.get(i + 1) - times.get(i)) / 1.0e9;
			double durationPerWorkUnit = duration / workSize;
			String description         = descriptions.get(i);
			System.out.printf(formatString, description, workSize, duration, durationPerWorkUnit);
		}
	}
}
