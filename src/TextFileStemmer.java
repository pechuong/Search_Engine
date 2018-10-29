import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class TextFileStemmer {

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 * Uses the English {@link SnowballStemmer.ALGORITHM} for stemming.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @return list of cleaned and stemmed words
	 *
	 * @see SnowballStemmer
	 * @see SnowballStemmer.ALGORITHM#ENGLISH
	 * @see #stemLine(String, Stemmer)
	 */
	public static List<String> stemLine(String line) {
		return stemLine(line, new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH));
	}

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static List<String> stemLine(String line, Stemmer stemmer) {
		ArrayList<String> wordList = new ArrayList<>();
		for (String word : TextParser.parse(line)) {
			wordList.add(stemmer.stem(word).toString());
		}
		return wordList;
	}

	public static List<Set<String>> stemQuery(Path inputFile) throws IOException {
		try (
				var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				) {
			String line;
			List<Set<String>> queries = new ArrayList<Set<String>>();
			while ((line = reader.readLine()) != null) {
				/*
				TreeSet<String> uniqueWords = new TreeSet<>();
				for (String word : stemLine(line)) {
					uniqueWords.add(word.toLowerCase());
				}
				queries.add(uniqueWords);
				 */
				queries.add(stemLine(line).stream()
						.filter((word) -> word.length() > 0)
						.map((word) -> word.toLowerCase())
						.collect(Collectors.toSet()));

			}
			return queries.stream()
					.filter((list) -> list.size() > 0)
					.collect(Collectors.toList());
		}
	}

}
