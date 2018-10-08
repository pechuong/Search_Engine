import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class TraversePath {

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
