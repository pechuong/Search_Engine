import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
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

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then writes that line to a new file.
	 *
	 * @param inputFile the input file to parse
	 * @param outputFile the output file to write the cleaned and stemmed words
	 * @throws IOException if unable to read or write to file
	 *
	 * @see #stemLine(String)
	 * @see TextParser#parse(String)
	 */
	public static List<String> stemFile(Path inputFile) throws IOException {
		try (
				var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				) {
			String line;
			ArrayList<String> wordList = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				List<String> stemmed = stemLine(line);
				wordList.addAll(stemmed);
			}
			return wordList;

		}
	}

	public static List<TreeSet<String>> stemQuery(Path inputFile) throws IOException {
		try (
				var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				) {
			String line;
			List<TreeSet<String>> queries = new ArrayList<TreeSet<String>>();
			while ((line = reader.readLine()) != null) {
				TreeSet<String> uniqueWords = new TreeSet<>();
				for (String word : stemLine(line)) {
					uniqueWords.add(word.toLowerCase());
				}
				queries.add(uniqueWords);
			}
			queries = queries.stream()
					.filter((list) -> {
						return list.size() > 0;
					})
					.collect(Collectors.toList());
			return queries;
		}
	}

}
