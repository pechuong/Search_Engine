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

	public ThreadSafeQueryMap(InvertedIndex index) {
		super(index);
	}

	public static void stemQuery(QueryMap queryMap, Path queryFile, boolean exact, int threads) throws IOException {
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
						searchResults = ((ThreadSafeInvertedIndex)queryMap.getInvertedIndex()).exactSearch(uniqueWords, threads);
					} else {
						searchResults = ((ThreadSafeInvertedIndex)queryMap.getInvertedIndex()).partialSearch(uniqueWords, threads);
					}
					queryMap.addQuery(queryLine, searchResults);
				}
			}
		}
	}

	@Override
	public synchronized void addQuery(String search, List<Result> results) {
		super.addQuery(search, results);
	}

	@Override
	public synchronized boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public synchronized InvertedIndex getInvertedIndex() {
		return super.getInvertedIndex();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
