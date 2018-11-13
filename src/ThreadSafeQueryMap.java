import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class ThreadSafeQueryMap extends QueryMap {

	private ReadWriteLock lock;

	/**
	 * Initializes a ThreadSafe Query Map
	 *
	 * @param index The inverted index that was already created
	 */
	public ThreadSafeQueryMap(InvertedIndex index) {
		super(index);
		lock = new ReadWriteLock();
	}

	public static class SearchWork implements Runnable {

		private final QueryMap queryMap;
		private final TreeSet<String> uniqueWords;
		private final String queryLine;
		private final boolean exact;

		/**
		 * Initializes new search work
		 *
		 * @param queryMap The mapping of all the results of searching and an index
		 * @param uniqueWords The query words to search
		 * @param queryLine The line of combined query words
		 * @param exact Boolean deciding whether or not to use exact search
		 */
		public SearchWork(QueryMap queryMap, TreeSet<String> uniqueWords, String queryLine, boolean exact) {
			this.queryMap = queryMap;
			this.uniqueWords = uniqueWords;
			this.queryLine = queryLine;
			this.exact = exact;
		}

		@Override
		public void run() {
			synchronized (queryMap) {
				List<Result> searchResults;
				if (!queryMap.hasQuery(queryLine) && uniqueWords.size() > 0) {
					if (exact) {
						searchResults = queryMap.getIndex().exactSearch(uniqueWords);
					} else {
						searchResults = queryMap.getIndex().partialSearch(uniqueWords);
					}
					queryMap.addQuery(queryLine, searchResults);
				}
			}
		}

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

				TreeSet<String> uniqueWords = new TreeSet<>();
				for (String word : TextParser.parse(line)) {
					uniqueWords.add(stemmer.stem(word).toString());
				}

				String queryLine = String.join(" ", uniqueWords);
				queue.execute(new SearchWork(this, uniqueWords, queryLine, exact));
			}
			queue.finish();
			queue.shutdown();
		}
	}

	@Override
	public void addQuery(String search, List<Result> results) {
		lock.lockReadWrite();
		super.addQuery(search, results);
		lock.unlockReadWrite();
	}

	@Override
	public boolean hasQuery(String query) {
		lock.lockReadOnly();
		try {
			return super.hasQuery(query);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public boolean isEmpty() {
		lock.lockReadOnly();
		try {
			return super.isEmpty();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public InvertedIndex getIndex() {
		lock.lockReadOnly();
		try {
			return super.getIndex();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}
}
