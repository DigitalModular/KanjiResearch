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
package org.digitalmodular.graphapi;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Mark Jeronimus
 */
// Created 2018-02-04
public interface Graph extends Serializable, Iterable<int[]> {
	int size();

	void setConnection(int x, int y);

	void removeConnection(int x, int y);

	boolean isConnected(int x, int y);

	void setGraph(Graph other);

	/**
	 * Constructs an iterator that iterates over all connections. The only guarantee is that the first index is never
	 * higher than the second index.
	 */
	@Override
	default ConnectionIterator iterator() {
		//noinspection AnonymousInnerClassWithTooManyMethods,OverlyComplexAnonymousInnerClass
		return new ConnectionIterator() {
			private int y = 0;
			private int x = 0;

			{
				findNext();
			}

			@Override
			public boolean hasNext() {
				return y < size();
			}

			@Override
			public int[] next() {
				int[] connection = new int[2];
				next(connection);
				return connection;
			}

			@Override
			public void next(int[] connection) {
				if (!hasNext())
					throw new NoSuchElementException("");

				connection[0] = x;
				connection[1] = y;
				findNext();
			}

			private void findNext() {
				while (true) {
					x++;
					if (x >= y) {
						y++;
						if (y >= size())
							break;

						x = 0;
					}

					if (isConnected(x, y))
						break;
				}
			}
		};
	}

	interface ConnectionIterator extends Iterator<int[]> {
		/**
		 * @param connection recyclable array of length 2.
		 */
		void next(int[] connection);
	}
}
