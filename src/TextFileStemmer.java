import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

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
		// TODO Try to use streams and lambda functions (optional)!
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
	public static void stemFile(Path inputFile) throws IOException {
		// TODO Use try-with-resources, buffered readers and writers, and UTF-8 encoding.
		try (
				var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
			) {
			String line = null;
			int wordCount = 1;
			while ((line = reader.readLine()) != null) {
				List<String> stemmed = stemLine(line);
				for (String word : stemmed) {
					// Does the index have the word?
					if (!Driver.invertedIndex.containsKey(word)) {
							Driver.invertedIndex.put(word, new HashMap<String, TreeSet<Integer>>());
							Driver.invertedIndex.get(word).put(inputFile.toString(), new TreeSet<Integer>());
					} // Does index have file?
					else if (!Driver.invertedIndex.get(word).containsKey(inputFile.toString())) {
						// Add word w/ new file placement
						Driver.invertedIndex.put(word, new HashMap<String, TreeSet<Integer>>());
						// Add set of integer positions of word in this file
						Driver.invertedIndex.get(word).put(inputFile.toString(), new TreeSet<Integer>());
					} 
					Driver.invertedIndex.get(word).get(inputFile.toString()).add(wordCount);
					wordCount++;
				}
			}
		}	
	}	

	/**
	 * Uses {@link #stemFile(Path, Path)} to stem a single hard-coded file. Useful
	 * for development.
	 *
	 * @param args unused
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		Path inputPath = Paths.get("test", "words.tExT");

		Files.createDirectories(Paths.get("out"));
		
		System.out.println(inputPath);
		stemFile(inputPath);
					
	}
}
