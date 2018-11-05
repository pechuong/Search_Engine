import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
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

	public void stemQuery(Path queryFile, boolean exact, int threads) throws IOException {
		try (
				var reader = Files.newBufferedReader(queryFile, StandardCharsets.UTF_8);
				) {

			String line;
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			HashSet<String> queries = new HashSet<>();
			TreeSet<String> uniqueWords = new TreeSet<>();

			while ((line = reader.readLine()) != null) {

				uniqueWords.clear();
				for (String word : TextFileStemmer.stemLine(line, stemmer)) {
					uniqueWords.add(word.toLowerCase());
				}

				String queryLine = String.join(" ", uniqueWords);
				List<Result> searchResults;
				if (!queries.contains(queryLine) && uniqueWords.size() > 0) {
					queries.add(queryLine);
					if (exact) {
						searchResults = getInvertedIndex().exactSearch(uniqueWords);
					} else {
						searchResults = getInvertedIndex().partialSearch(uniqueWords);
					}
					addQuery(queryLine, searchResults);
				}
			}
		}
	}

	@Override
	public void addQuery(String search, List<Result> results) {
		lock.lockReadWrite();
		super.addQuery(search, results);
		lock.unlockReadWrite();
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
