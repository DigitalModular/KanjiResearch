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

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.imageio.ImageIO;

import org.digitalmodular.graphapi.Graph.ConnectionIterator;

/**
 * @author Mark Jeronimus
 */
// Created 2018-02-12
public final class GraphIO {
	private GraphIO() { throw new AssertionError(); }

	public static Graph read(String filename) throws IOException {
		int i = filename.lastIndexOf('.');
		if (i <= 0)
			throw new IllegalArgumentException("Extension required to determine format: " + filename);

		String ext = filename.substring(i + 1).toUpperCase();
		switch (ext) {
			case "TXT":
				return readTXT(filename);
			case "CONN":
				return readCONN(filename);
			default:
				return readIMG(filename);
		}
	}

	public static void write(Graph graph, String filename) throws IOException {
		int i = filename.lastIndexOf('.');
		if (i <= 0)
			throw new IllegalArgumentException("Extension required to determine format: " + filename);

		String ext = filename.substring(i + 1).toUpperCase();
		switch (ext) {
			case "TXT":
				writeTXT(graph, filename);
				break;
			case "CONN":
				writeCONN(graph, filename);
				break;
			case "PNG":
				writePNG(graph, filename);
				break;
			default:
				throw new IllegalArgumentException("Extension not supported: " + filename);
		}
	}

	private static Graph readTXT(String filename) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(filename));

		int size = lines.size();
		if (!(size > 0))
			throw new IllegalArgumentException("Empty file: " + filename);

		Graph graph = new MatrixGraph(size);
		for (int y = 0; y < lines.size(); y++) {
			String line = lines.get(y);
			if (line.length() != size)
				throw new IOException("Line length mismatch: " + filename +
				                      " @ line " + (y + 1) + " (size = " + size + ')');

			for (int x = 0; x < size; x++) {
				char c = line.charAt(x);
				if (c != '0' && c != '1')
					throw new IOException("Invalid char found: " + filename +
					                      " @ line " + (y + 1) + " column " + (x + 1) + ": " + c);
				if (x == y && c != '0')
					throw new IOException("Self-connections not allowed: " + filename +
					                      " @ line/column " + (y + 1));

				if (x > y) {
					if (c == '1')
						graph.setConnection(x, y);
				} else {
					if (graph.isConnected(x, y) != (c == '1'))
						throw new IOException("Graph not undirected: " + filename +
						                      " at line " + (y + 1) + " column " + (x + 1));
				}
			}
		}
		return graph;
	}

	private static void writeTXT(Graph graph, String filename) throws IOException {
		int size = graph.size();

		try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(filename))) {
			for (int y = 0; y < size; y++) {
				for (int x = 0; x < size; x++)
					out.write(graph.isConnected(x, y) ? '1' : '0');

				out.write('\n');
			}
		}
	}

	private static Graph readCONN(String filename) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(filename));
		if (lines.isEmpty())
			throw new IllegalArgumentException("Empty file: " + filename);

		if (!lines.get(0).startsWith("Size:"))
			throw new IOException("First line of file doesn't start with \"Size:\": " + filename);

		int size;
		try {
			size = Integer.parseInt(lines.get(0).substring(5).trim());
		} catch (NumberFormatException ignored) {
			throw new IOException("Size is not an integer: \"" + lines.get(0) + '"');
		}

		Graph graph = new NeighborGraph(size);
		for (int i = 1; i < lines.size(); i++) {
			String line = lines.get(i);

			int x;
			int y;
			try {
				int separator = line.indexOf('\t', 1);
				if (separator < 0)
					continue;

				x = Integer.parseInt(lines.get(i).substring(0, separator).trim());
				y = Integer.parseInt(lines.get(i).substring(separator + 1).trim());
			} catch (NumberFormatException ignored) {
				throw new IOException("Cannot parse connection: " + filename +
				                      " @ line " + (i + 1));
			}

			if (x == y)
				throw new IOException("Self-connections not allowed: " + filename +
				                      " @ line " + (i + 1));
			if (x > y)
				throw new IOException("Connection with x > y found: " + filename +
				                      " @ line " + (i + 1));
			if (graph.isConnected(x, y))
				throw new IOException("The same connection appeared a second time: " + filename +
				                      " @ line " + (i + 1));

			graph.setConnection(x, y);
		}

		return graph;
	}

	private static void writeCONN(Graph graph, String filename) throws IOException {
		ConnectionIterator iterator = graph.iterator();

		try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),
		                                                                    StandardCharsets.US_ASCII))) {
			out.write("Size: ");
			out.write(String.valueOf(graph.size()));
			out.newLine();

			out.write("Number of isolated nodes: ");
			out.write(String.valueOf(GraphUtilities.countIsolatedNodes(graph)));
			out.newLine();

			out.write("Number of leaf nodes: ");
			out.write(String.valueOf(GraphUtilities.countLeafNodes(graph)));
			out.newLine();

			int[] connection = new int[2];
			while (iterator.hasNext()) {
				iterator.next(connection);
				int x = connection[0];
				int y = connection[1];
				assert x < y;
				out.write(String.valueOf(x));
				out.write('\t');
				out.write(String.valueOf(y));
				out.newLine();
			}
		}
	}

	private static Graph readIMG(String filename) throws IOException {
		BufferedImage img  = ImageIO.read(new File(filename));
		int           size = img.getWidth();
		if (size != img.getHeight())
			throw new IllegalArgumentException("Image is not square: " + filename +
			                                   " (" + size + ", " + img.getHeight() + ')');

		if (size <= 0)
			throw new IllegalArgumentException("Empty file: " + filename);

		Graph graph = new MatrixGraph(size);
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {
				int i = img.getRGB(x, y);
				if (i != -1 && i != 0xFF000000)
					throw new IOException("Image contains values other than black and white: " + filename +
					                      " @ pixel (" + x + ", " + y + ')');
				if (x == y && i == 0xFF000000)
					throw new IOException("Self-connections not allowed: " + filename +
					                      " @ pixel (" + x + ", " + y + ')');

				if (x > y) {
					if (i == 0xFF000000)
						graph.setConnection(x, y);
				} else {
					if (graph.isConnected(x, y) != (i == 0xFF000000))
						throw new IOException("Graph not undirected: " + filename +
						                      " @ pixel (" + x + ", " + y + ')');
				}
			}
		}
		return graph;

	}

	private static void writePNG(Graph graph, String filename) throws IOException {
		int           size = graph.size();
		BufferedImage img  = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
		for (int y = 0; y < size; y++)
			for (int x = 0; x < size; x++)
				img.setRGB(x, y, graph.isConnected(x, y) ? 0 : -1);

		ImageIO.write(img, "PNG", new File(filename));
	}
}
