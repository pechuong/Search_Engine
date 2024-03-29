import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class QueryMap implements Query {

	private final TreeMap<String, List<Result>> queryMap;
	private final InvertedIndex index;

	/**
	 * Initializes the Query Map
	 */
	public QueryMap(InvertedIndex index) {
		this.queryMap = new TreeMap<>();
		this.index = index;
	}

	@Override
	public void writeJSON(Path path) throws IOException {
		ResultsJSON.asArray(queryMap, path);
	}

	@Override
	public void stemQuery(Path queryFile, boolean exact) throws IOException{
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
				if (!hasQuery(queryLine) && uniqueWords.size() > 0) {
					if (exact) {
						searchResults = index.exactSearch(uniqueWords);
					} else {
						searchResults = index.partialSearch(uniqueWords);
					}
					addQuery(queryLine, searchResults);
				}
			}
		}
	}

	@Override
	public void addQuery(String search, List<Result> results) {
		this.queryMap.put(search, results);
	}

	@Override
	public boolean hasQuery(String query) {
		return this.queryMap.containsKey(query);
	}

	@Override
	public boolean isEmpty() {
		return this.queryMap.isEmpty();
	}

	@Override
	public String toString() {
		return this.queryMap.toString();
	}
}
