import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

public class LocationJSON {

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
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
	 * Writes the set of elements formatted as a pretty JSON array of numbers to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path output)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(output,
				StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
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
	public static void asObject(TreeMap<String, Integer> elements, Writer writer, int level) throws IOException {

		writer.write("{" + System.lineSeparator());
		for (String file : elements.headMap(elements.lastKey(), false).keySet()) {
			TreeJSONWriter.indent(level + 1, writer);
			TreeJSONWriter.quote(file, writer);
			writer.write(": " + elements.get(file) + "," + System.lineSeparator());
		}
		TreeJSONWriter.indent(level + 1, writer);
		TreeJSONWriter.quote(elements.lastKey(), writer);
		writer.write(": " + elements.get(elements.lastKey()) + System.lineSeparator());
		writer.write("}");
	}

}
