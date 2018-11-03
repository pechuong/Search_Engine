import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryMap {

	/**
	 * Key - the queries that we use to search
	 * Value - the list of results we get back from the search
	 */
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
		ResultsJSON.asArray(this.queryMap, path);
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
			HashSet<String> queries = new HashSet<>();
			while ((line = reader.readLine()) != null) {

				TreeSet<String> uniqueWords = new TreeSet<>();
				for (String word : TextFileStemmer.stemLine(line, stemmer)) {
					uniqueWords.add(word.toLowerCase());
				}

				String queryLine = String.join(" ", uniqueWords);
				if (!queries.contains(queryLine)) {
					queries.add(queryLine);
					if (exact) {
						List<Result> searchResults = index.exactSearch(uniqueWords);
						queryMap.put(queryLine, searchResults);
					} else {
						// TODO partial search
						// TODO add the search result and queryLine to queryMap
					}
				}
			}
		}
	}

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
		if (this.queryMap.size() < 1) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return this.queryMap.toString();
	}
}
