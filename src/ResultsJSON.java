import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

public class ResultsJSON {

	public static String asArray(TreeMap<String, List<Result>> qMap) {
		try {
			StringWriter writer = new StringWriter();
			asArray(qMap, writer, 0);
			return writer.toString();
		}
		catch (Exception e) {
			return null;
		}
	}

	public static void asArray(TreeMap<String, List<Result>> qMap, Path output) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(output,
				StandardCharsets.UTF_8)) {
			asArray(qMap, writer, 0);
		}
	}

	public static void asArray(TreeMap<String, List<Result>> qMap, Writer writer, int level) throws IOException{
		writer.write("[" + System.lineSeparator());
		asSearchResult(qMap, writer, level + 1);
		writer.write("]");
	}

	public static String asSearchResult(TreeMap<String, List<Result>> qMap) {
		try {
			StringWriter writer = new StringWriter();
			asSearchResult(qMap, writer, 0);
			return writer.toString();
		}
		catch (Exception e) {
			return null;
		}
	}

	public static void asSearchResult(TreeMap<String, List<Result>> qMap, Path output) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(output,
				StandardCharsets.UTF_8)) {
			asSearchResult(qMap, writer, 0);
		}
	}

	public static void asSearchResult(TreeMap<String, List<Result>> qMap, Writer writer, int level) throws IOException {
		for (String searchName : qMap.headMap(qMap.lastKey(), false).keySet()) {
			TreeJSONWriter.indent(level, writer);
			writer.write("{" + System.lineSeparator());
			TreeJSONWriter.indent(level + 1, writer);
			TreeJSONWriter.quote("queries", writer);
			writer.write(": ");
			TreeJSONWriter.quote(searchName, writer);
			writer.write("," + System.lineSeparator());
			TreeJSONWriter.indent(level + 1, writer);
			TreeJSONWriter.quote("results", writer);
			writer.write(": [" + System.lineSeparator());
			asResultArray(qMap.get(searchName), writer, level + 2);
			TreeJSONWriter.indent(level + 1, writer);
			writer.write("]" + System.lineSeparator());
			TreeJSONWriter.indent(level, writer);
			writer.write("}," + System.lineSeparator());
		}
		// writes "queries":
		TreeJSONWriter.indent(level, writer);
		writer.write("{" + System.lineSeparator());
		TreeJSONWriter.indent(level + 1, writer);
		TreeJSONWriter.quote("queries", writer);
		writer.write(": ");
		TreeJSONWriter.quote(qMap.lastKey(), writer);
		writer.write("," + System.lineSeparator());

		// writers "results":
		TreeJSONWriter.indent(level + 1, writer);
		TreeJSONWriter.quote("results", writer);
		writer.write(": [" + System.lineSeparator());
		asResultArray(qMap.get(qMap.lastKey()), writer, level + 2);
		TreeJSONWriter.indent(level + 1, writer);
		writer.write("]" + System.lineSeparator());

		// writes "}"
		TreeJSONWriter.indent(level, writer);
		writer.write("}" + System.lineSeparator());
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 */
	public static String asResultArray(List<Result> results) {
		try {
			StringWriter writer = new StringWriter();
			asResultArray(results, writer, 0);
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
	public static void asResultArray(List<Result> results, Path output)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(output,
				StandardCharsets.UTF_8)) {
			asResultArray(results, writer, 0);
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
	public static void asResultArray(List<Result> results, Writer writer, int level) throws IOException {
		int numResults = results.size();
		if (numResults < 1) {
			return;
		}

		for (Result result : results.subList(0, numResults)) {
			TreeJSONWriter.indent(level, writer);
			writer.write("{" + System.lineSeparator());
			TreeJSONWriter.indent(level + 1, writer);
			TreeJSONWriter.quote("where", writer);
			writer.write(": ");
			TreeJSONWriter.quote(result.getFileName(), writer);
			writer.write("," + System.lineSeparator());
			TreeJSONWriter.indent(level + 1, writer);
			TreeJSONWriter.quote("count", writer);
			writer.write(": " + result.getMatches() + "," + System.lineSeparator());
			TreeJSONWriter.indent(level + 1, writer);
			TreeJSONWriter.quote("score", writer);
			writer.write(": " + result.getScore() + System.lineSeparator());
			TreeJSONWriter.indent(level, writer);
			writer.write("}," + System.lineSeparator());
		}
		TreeJSONWriter.indent(level, writer);
		writer.write("{" + System.lineSeparator());
		TreeJSONWriter.indent(level + 1, writer);
		TreeJSONWriter.quote("where", writer);
		writer.write(": ");
		TreeJSONWriter.quote(results.get(numResults-1).getFileName(), writer);
		writer.write("," + System.lineSeparator());
		TreeJSONWriter.indent(level + 1, writer);
		TreeJSONWriter.quote("count", writer);
		writer.write(": " + results.get(numResults-1).getMatches() + "," + System.lineSeparator());
		TreeJSONWriter.indent(level + 1, writer);
		TreeJSONWriter.quote("score", writer);
		writer.write(": " + results.get(numResults-1).getScore() + System.lineSeparator());
		TreeJSONWriter.indent(level, writer);
		writer.write("}" + System.lineSeparator());
	}
}
