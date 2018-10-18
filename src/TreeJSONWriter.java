import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeJSONWriter {

	/**
	 * Writes several tab <code>\t</code> symbols using the provided
	 * {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 */
	public static String asArray(TreeSet<Integer> elements) {
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers
	 * using the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer,
			int level) throws IOException {

		if (elements.isEmpty()) {
			writer.write("[" + System.lineSeparator());
			indent(level, writer);
			writer.write("");
			writer.write("]");
			return;
		}

		writer.write("[" + System.lineSeparator());
		for (int num : elements.headSet(elements.last(), false)) {
			indent(level + 1, writer);
			writer.write(Integer.toString(num));
			writer.write("," + System.lineSeparator());
		}
		indent(level + 1, writer);
		writer.write(Integer.toString(elements.last()));
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("]");
	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 */
	public static String asObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 */
	public static void asObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 */
	public static void asObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {
		indent(level, writer);
		writer.write("{" + System.lineSeparator());
		if (!elements.isEmpty()) {
			for (String word: elements.headMap(elements.lastKey(), false).keySet()) {
				indent(level + 1, writer);
				quote(word, writer);
				writer.write(": ");
				asNestedObject(elements.get(word), writer, level + 1);
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			quote(elements.lastKey(), writer);
			writer.write(": ");
			asNestedObject(elements.get(elements.lastKey()), writer, level + 1);
		}
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("}");
	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 */
	public static String asNestedObject(TreeMap<String, TreeSet<Integer>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 */
	public static void asNestedObject(TreeMap<String, TreeSet<Integer>> elements,
			Writer writer, int level) throws IOException {

		writer.write("{" + System.lineSeparator());
		if (!elements.isEmpty()) {
			for (String key : elements.headMap(elements.lastKey(), false).keySet()) {
				indent(level + 1, writer);
				quote(key, writer);
				writer.write(": ");
				asArray(elements.get(key), writer, level + 1);
				writer.write(",");
				writer.write(System.lineSeparator());
			}
			indent(level + 1, writer);
			quote(elements.lastKey(), writer);
			writer.write(": ");
			asArray(elements.get(elements.lastKey()), writer, level + 1);
		}
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("}");
	}

}
