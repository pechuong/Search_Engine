import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class TraversePath {

	public static void traverse(InvertedIndex index, LocationMap lMap, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				for (Path file : listing) {
					traverse(index, lMap, file);
				}
			}
		} else if (path.toString().matches("(?i).*\\.te?xt$")) {
			List<String> wordList = TextFileStemmer.stemFile(path);
			index.buildiIndex(wordList, path);
			lMap.buildLocation(path, wordList.size());;
		}
	}

}
