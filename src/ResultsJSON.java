import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.List;
import java.util.TreeMap;

public class ResultsJSON {

	/**
	 * Starts the output for the results to JSON
	 *
	 * @param qMap The search results to turn to JSON
	 * @return String writer.toString()
	 */
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

	/**
	 * Takes in the search result map and the JSON file path to output it to.
	 * Sends a writer and level to the other asArray method.
	 *
	 * @param qMap The search results
	 * @param output The JSON filename path to write the search results to.
	 * @throws IOException
	 */
	public static void asArray(TreeMap<String, List<Result>> qMap, Path output) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(output,
				StandardCharsets.UTF_8)) {
			asArray(qMap, writer, 0);
		}
	}

	/**
	 * Starts writing JSON output for search results.
	 * Starts writing the outer [ ] and then calls the other methods to write
	 * the search results into JSON file.
	 *
	 * @param qMap The search result map
	 * @param writer The writer
	 * @param level The indentation level
	 * @throws IOException
	 */
	public static void asArray(TreeMap<String, List<Result>> qMap, Writer writer, int level) throws IOException{
		writer.write("[" + System.lineSeparator());
		if (!qMap.isEmpty()) {
			asSearchResult(qMap, writer, level + 1);
		}
		writer.write("]");
	}

	/**
	 * Writes out the search results by queries.
	 * Calls the other asSearchResult method
	 *
	 * @param qMap The map of search results
	 * @return String writer.toString()
	 */
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

	/**
	 * Given a search result map and the output file to write to,
	 * will write the map out as JSON format. Calls the other
	 * asSearchResult method and gives it a writer and level.
	 *
	 * @param qMap The search result map
	 * @param output The output JSON filepath to write to
	 * @throws IOException
	 */
	public static void asSearchResult(TreeMap<String, List<Result>> qMap, Path output) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(output,
				StandardCharsets.UTF_8)) {
			asSearchResult(qMap, writer, 0);
		}
	}

	/**
	 * Writes out all the searches found in the queries out one by one.
	 * Writes the "queries": " " and then all the results that appeared
	 * from that particular search.
	 *
	 * @param qMap The search result map
	 * @param writer The writer
	 * @param level The indentation level
	 * @throws IOException
	 */
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
	 * Returns writer.toString()
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
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
	 * Writes the set of elements formatted as a pretty JSON as an Array
	 * of Result Objects.
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
	 * Writes the set of elements formatted as a pretty JSON as an Array
	 * of Result Objects (where, matches, score) given the writer and
	 * indentation level.
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
		DecimalFormat FORMATTER = new DecimalFormat("0.000000");

		for (Result result : results.subList(0, numResults - 1)) {
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
			writer.write(": " + FORMATTER.format(result.getScore()) + System.lineSeparator());
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
		writer.write(": " + FORMATTER.format(results.get(numResults-1).getScore()) + System.lineSeparator());
		TreeJSONWriter.indent(level, writer);
		writer.write("}" + System.lineSeparator());
	}
}
