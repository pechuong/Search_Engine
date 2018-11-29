import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class ThreadSafeQueryMap implements Query {

	private final TreeMap<String, List<Result>> queryMap;
	private final ThreadSafeInvertedIndex index;
	private final int threads;

	/**
	 * Initializes a ThreadSafe Query Map
	 *
	 * @param index The inverted index that was already created
	 */
	public ThreadSafeQueryMap(ThreadSafeInvertedIndex index, int threads) {
		this.queryMap = new TreeMap<>();
		this.index = index;
		this.threads = threads;
	}

	/**
	 * Handles all of the searching the will be performed on the
	 * inverted index
	 */
	public class SearchWork implements Runnable {

		private final String line;
		private final boolean exact;

		/**
		 * Initializes new search work
		 *
		 * @param queryMap The mapping of all the results of searching and an index
		 * @param uniqueWords The query words to search
		 * @param queryLine The line of combined query words
		 * @param exact Boolean deciding whether or not to use exact search
		 */
		public SearchWork(String line, boolean exact) {
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			TreeSet<String> uniqueWords = new TreeSet<>();

			for (String word : TextParser.parse(line)) {
				uniqueWords.add(stemmer.stem(word).toString());
			}

			String queryLine = String.join(" ", uniqueWords);
			List<Result> searchResults;

			synchronized (queryMap) {
				if (hasQuery(queryLine) || uniqueWords.isEmpty()) {
					return;
				}
			}

			if (exact) {
				searchResults = index.exactSearch(uniqueWords);
			} else {
				searchResults = index.partialSearch(uniqueWords);
			}

			synchronized (queryMap) {
				addQuery(queryLine, searchResults);
			}
		}
	}

	@Override
	public void writeJSON(Path path) throws IOException {
		synchronized (queryMap) {
			ResultsJSON.asArray(queryMap, path);
		}
	}

	@Override
	public void stemQuery(Path queryFile, boolean exact) throws IOException {
		try (
				var reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8);
				) {

			String line;
			WorkQueue queue = new WorkQueue(threads);

			while ((line = reader.readLine()) != null) {
				queue.execute(new SearchWork(line, exact));
			}
			queue.finish();
			queue.shutdown();
		}
	}

	@Override
	public void addQuery(String search, List<Result> results) {
		synchronized(queryMap) {
			this.queryMap.put(search, results);
		}
	}

	@Override
	public boolean hasQuery(String query) {
		synchronized(queryMap) {
			return this.queryMap.containsKey(query);
		}
	}

	@Override
	public boolean isEmpty() {
		synchronized(queryMap) {
			return this.queryMap.isEmpty();
		}
	}

	@Override
	public String toString() {
		synchronized(queryMap) {
			return this.queryMap.toString();
		}
	}
}
