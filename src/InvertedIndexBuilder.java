import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class InvertedIndexBuilder {

	/**
	 * Traverses a path and stems each file found. If a directory is found,
	 * will continue traversing until a file pops up.
	 *
	 * @param index The inverted index to build to
	 * @param path The path to traverse and go thru
	 * @throws IOException
	 */
	public static void traverse(InvertedIndex index, LocationMap locMap, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				for (Path file : listing) {
					traverse(index, locMap, file);
				}
			}
		} else if (path.toString().matches("(?i).*\\.te?xt$")) {
			stemFile(index, locMap, path);
		}
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds it to the inverted index.
	 *
	 * @param index The inverted index to add the words in the file to
	 * @param inputFile the input file to parse
	 * @throws IOException if unable to read or write to file
	 *
	 * @see TextParser#parse(String)
	 */
	public static void stemFile(InvertedIndex index, LocationMap locMap, Path inputFile) throws IOException {
		try (
				var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				) {
			String line;
			int count = 0;
			String filePath = inputFile.toString();
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					count++;
					index.build(stemmer.stem(word).toString(), filePath, count);
				}
			}
			if (count > 0) {
				locMap.buildLocation(inputFile, count);
			}
		}
	}
}
