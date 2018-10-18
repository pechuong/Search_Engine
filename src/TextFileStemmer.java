import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

	// TODO Move into the TraversePath class
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
	public static void stemFile(InvertedIndex index, Path inputFile) throws IOException {
		try (
				var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				) {
			String line;
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			/*
			 * TODO Some efficiency issues
			 * 1) Any time you use "temporary storage" like this list, and then
			 * move data elsewhere (like into your index), using more space and
			 * time than necessary.
			 *
			 * Efficiency is one reason to create a more specific version of
			 * generalized code. Copy/paste some of what is going on in stemLine
			 * into your while loop... and instead of adding to a list immediately
			 * add to your index
			 *
			 * 2) Creating a stemmer object for every line, causing garbage collection
			 * Instead, create 1 stemmer per file.
			 */

			ArrayList<String> wordList = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				List<String> stemmed = stemLine(line, stemmer);
				wordList.addAll(stemmed);
			}
			index.buildiIndex(wordList, inputFile);
		}
	}

}
