import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/*
 * TODO It is hard to get this to work with the extends QueryMap relationship.
 * You really need access to the private data to efficiently synchronize it.
 * I recommend instead you create a QueryMapInterface that is implemented by
 * both QueryMap and ThreadSafeQueryMap, then the classes can each have their own
 * private data and separate implementations. Does that make sense?
 */

public class ThreadSafeQueryMap implements Query {

	private final TreeMap<String, List<Result>> queryMap;
	private final InvertedIndex index;
	private final ReadWriteLock lock;

	/**
	 * Initializes a ThreadSafe Query Map
	 *
	 * @param index The inverted index that was already created
	 */
	public ThreadSafeQueryMap(InvertedIndex index) {
		this.queryMap = new TreeMap<>();
		this.index = index;
		lock = new ReadWriteLock();
	}

	/**
	 * Handles all of the searching the will be performed on the
	 * inverted index
	 */
	public static class SearchWork implements Runnable {

		private final ThreadSafeQueryMap safeQueryMap;
		private final Stemmer stemmer;
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
		public SearchWork(ThreadSafeQueryMap queryMap, Stemmer stemmer, String line, boolean exact) {
			this.safeQueryMap = queryMap;
			this.stemmer = stemmer;
			this.line = line;
			this.exact = exact;
		}

		@Override
		public void run() {
			synchronized (safeQueryMap.queryMap) {
				TreeSet<String> uniqueWords = new TreeSet<>();
				for (String word : TextParser.parse(line)) {
					uniqueWords.add(stemmer.stem(word).toString());
				}

				String queryLine = String.join(" ", uniqueWords);
				List<Result> searchResults;

				if (!safeQueryMap.hasQuery(queryLine) && uniqueWords.size() > 0) {
					if (exact) {
						searchResults = safeQueryMap.index.exactSearch(uniqueWords);
					} else {
						searchResults = safeQueryMap.index.partialSearch(uniqueWords);
					}
					safeQueryMap.addQuery(queryLine, searchResults);
				}
			}
		}
	}

	@Override
	public void writeJSON(Path path) throws IOException {
		ResultsJSON.asArray(queryMap, path);
	}

	/**
	 * Stems the files of queries and performs searches on each line of query
	 * (Multi-threaded version)
	 *
	 * @param queryFile The files of queries
	 * @param exact Whether or not to use exact search or partial search
	 * @param threads The number of threads to run the search with
	 * @throws IOException
	 */
	public void stemQuery(Path queryFile, boolean exact, int threads) throws IOException {
		try (
				var reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8);
				) {

			String line;
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			WorkQueue queue = new WorkQueue(threads);

			while ((line = reader.readLine()) != null) {
				queue.execute(new SearchWork(this, stemmer, line, exact));
			}
			queue.finish();
			queue.shutdown();
		}
	}

	@Override
	public void addQuery(String search, List<Result> results) {
		lock.lockReadWrite();
		this.queryMap.put(search, results);
		lock.unlockReadWrite();
	}

	@Override
	public boolean hasQuery(String query) {
		lock.lockReadOnly();
		try {
			return this.queryMap.containsKey(query);
		} finally {
			lock.unlockReadOnly();
		}
	}

	public boolean isEmpty() {
		lock.lockReadOnly();
		try {
			return queryMap.isEmpty();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return this.queryMap.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}
}
