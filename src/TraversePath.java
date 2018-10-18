import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO Move stemFile into this class
// TODO Maybe refactor the class name to InvertedIndexBuilder

public class TraversePath {

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
			TextFileStemmer.stemFile(index, path);
		}
	}

}
