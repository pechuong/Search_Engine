import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryMap {

	private final TreeMap<String, List<Result>> queryMap;
	private final InvertedIndex index;

	/**
	 * Initializes the Query Map
	 */
	public QueryMap(InvertedIndex index) {
		this.queryMap = new TreeMap<>();
		this.index = index;
	}

	/**
	 * Writes the Query Map to a JSON file given a JSON output path.
	 *
	 * @param path The path to write the map to
	 * @throws IOException
	 */
	public void writeJSON(Path path) throws IOException {
		ResultsJSON.asArray(queryMap, path);
	}

	/**
	 * Stems the query file, performs a search in the index, and then
	 * stores the results of the search
	 *
	 * @param queryFile The file with all the queries to perform a search on
	 * @param exact Determines if an exact search should be performed or not
	 * @throws IOException If something goes wrong when trying to read query file
	 */
	public void stemQuery(Path queryFile, boolean exact) throws IOException {
		try (
				var reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8);
				) {

			String line;
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

			while ((line = reader.readLine()) != null) {

				TreeSet<String> uniqueWords = new TreeSet<>();
				for (String word : TextParser.parse(line)) {
					uniqueWords.add(stemmer.stem(word).toString());
				}

				String queryLine = String.join(" ", uniqueWords);
				List<Result> searchResults;
				if (!queryMap.containsKey(queryLine) && uniqueWords.size() > 0) {
					if (exact) {
						searchResults = index.exactSearch(uniqueWords);
					} else {
						searchResults = index.partialSearch(uniqueWords);
					}
					queryMap.put(queryLine, searchResults);
				}
			}
		}
	}

	// TODO Use or remove!
	/**
	 * Adds a Query (one search) into the map w/ it's result(s).
	 *
	 * @param search The search query made to store in the map
	 * @param results The list of results to store into the map
	 */
	public void addQuery(String search, List<Result> results) {
		this.queryMap.put(search, results);
	}

	/**
	 * Checks to see if the Query Map is empty or not.
	 * This means that no search has been made or stored.
	 *
	 * @return true if the Query Map has at least 1 entry.
	 */
	public boolean isEmpty() {
		return (this.queryMap.size() < 1) ? true : false;
	}

	@Override
	public String toString() {
		return this.queryMap.toString();
	}
}
