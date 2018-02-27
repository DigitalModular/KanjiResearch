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

import java.util.Map.Entry;
import static java.util.Objects.requireNonNull;

/**
 * Undirected unweighted graph without self-loops
 *
 * @author Mark Jeronimus
 */
// Created 2018-02-04
public class MatrixGraph implements Graph {
	private static final long serialVersionUID = -2686507047061452160L;

	private final boolean[][] matrix;

	public MatrixGraph(int numNodes) {
		if (numNodes < 1)
			throw new IllegalArgumentException("numNodes should be at least 1: " + numNodes);

		matrix = new boolean[numNodes][numNodes];
	}

	public MatrixGraph(Graph other) {
		this(other.numNodes());

		//noinspection OverridableMethodCallDuringObjectConstruction,OverriddenMethodCallDuringObjectConstruction
		setGraph(other);
	}

	@Override
	public int numNodes() {
		return matrix.length;
	}

	@Override
	public void setConnection(int x, int y) {
		matrix[y][x] = true;
		matrix[x][y] = true;
	}

	@Override
	public void removeConnection(int x, int y) {
		matrix[y][x] = false;
		matrix[x][y] = false;
	}

	@Override
	public boolean isConnected(int x, int y) {
		return matrix[y][x];
	}

	@Override
	public void setGraph(Graph other) {
		requireNonNull(other);
		if (numNodes() != other.numNodes())
			throw new IllegalArgumentException("Network sizes differ: " + numNodes() + " vs " + other.numNodes());

		if (other instanceof MatrixGraph)
			setMatrixGraph((MatrixGraph)other);
		else
			for (Entry<Integer, Integer> connections1 : other)
				setConnection(connections1.getKey(), connections1.getValue());
	}

	private void setMatrixGraph(MatrixGraph other) {
		int numNodes = numNodes();
		for (int i = 0; i < numNodes; i++)
			System.arraycopy(other.matrix[i], 0, matrix[i], 0, numNodes);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder((matrix.length + 1) * matrix.length - 1);
		for (int y = 0; y < matrix.length; y++) {
			if (y > 0)
				sb.append('\n');

			boolean[] row = matrix[y];
			for (int x = 0; x < row.length; x++) {
				if (x == y)
					sb.append('＼');
				else
					sb.append(row[x] ? '●' : '○');
			}
		}
		return sb.toString();
	}
}
