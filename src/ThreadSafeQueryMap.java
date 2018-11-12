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

	public ThreadSafeQueryMap(InvertedIndex index) {
		super(index);
		lock = new ReadWriteLock();
	}

	public static class SearchWork implements Runnable {

		private final QueryMap queryMap;
		private TreeSet<String> uniqueWords;
		private final String queryLine;
		private final boolean exact;

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
						searchResults = queryMap.getInvertedIndex().exactSearch(uniqueWords);
					} else {
						searchResults = queryMap.getInvertedIndex().partialSearch(uniqueWords);
					}
					queryMap.addQuery(queryLine, searchResults);
				}
			}
		}

	}

	/**
	 * Stems the files of queries and performs searches on each line of query
	 *
	 * @param queryFile The files of queries
	 * @param exact Whether or not to use exact search or partial search
	 * @param threads The number of threads to give to use for workqueue
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
					uniqueWords.add(stemmer.stem(word).toString().toLowerCase());
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
	public InvertedIndex getInvertedIndex() {
		lock.lockReadOnly();
		try {
			return super.getInvertedIndex();
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
