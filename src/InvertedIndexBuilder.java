import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

// TODO Maybe refactor the class name to InvertedIndexBuilder

public class InvertedIndexBuilder {

	/**
	 * TODO
	 * @param index
	 * @param path
	 * @throws IOException
	 */
	public static void traverse(InvertedIndex index, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				for (Path file : listing) {
					traverse(index, file);
				}
			}
		} else if (path.toString().matches("(?i).*\\.te?xt$")) {
			stemFile(index, path);
		}
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
	public static void stemFile(InvertedIndex index, Path inputFile) throws IOException {
		try (
				var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				) {
			String line;
			int count = 1;
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
			 */

			ArrayList<String> wordList = new ArrayList<>();
			while ((line = reader.readLine()) != null) {
				List<String> stemmed = TextFileStemmer.stemLine(line, stemmer);
				wordList.addAll(stemmed);
			}
			index.buildiIndex(wordList, inputFile);
		}
	}

}
