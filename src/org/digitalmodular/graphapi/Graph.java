/*
 * This file is part of GraphAPI.
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
package org.digitalmodular.graphapi;

import java.io.Serializable;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

/**
 * @author Mark Jeronimus
 */
// Created 2018-02-04
public interface Graph extends Serializable, Iterable<Entry<Integer, Integer>> {
	int numNodes();

	void setConnection(int x, int y);

	void removeConnection(int x, int y);

	boolean isConnected(int x, int y);

	void setGraph(Graph other);

	/**
	 * Constructs an iterator that iterates over all connections. The only guarantee is that the index of the first
	 * node is never higher than the index of the second node.
	 */
	@Override
	default Iterator<Entry<Integer, Integer>> iterator() {
		//noinspection AnonymousInnerClassWithTooManyMethods,OverlyComplexAnonymousInnerClass // Suppress intelliJ bug
		return new Iterator<Entry<Integer, Integer>>() {
			private int y = 0;
			private int x = 0;

			{
				findNext();
			}

			@Override
			public boolean hasNext() {
				return y < numNodes();
			}

			@Override
			public Entry<Integer, Integer> next() {
				if (!hasNext())
					throw new NoSuchElementException("");

				Entry<Integer, Integer> connection = new SimpleImmutableEntry<>(y, x);
				findNext();
				return connection;
			}

			private void findNext() {
				while (true) {
					x++;
					if (x >= y) {
						y++;
						if (y >= numNodes())
							break;

						x = 0;
					}

					if (isConnected(x, y))
						break;
				}
			}
		};
	}
}
