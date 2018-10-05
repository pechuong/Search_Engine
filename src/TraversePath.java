import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO Remove the String prefix logic from the code

public class TraversePath {

	/**
	 * Outputs the name of the file or subdirectory, with proper indentation to
	 * help indicate the hierarchy. If a subdirectory is encountered, will
	 * recursively list all the files in that subdirectory. For each file found,
	 * the function will stem that file into an inverted index
	 *
	 * The recursive version of this method is private. Users of this class will
	 * have to use the public version (see below).
	 *
	 * @param iIndex The inverted index passed in
	 * @param prefix the padding or prefix to put infront of the file or
	 *               subdirectory name
	 * @param path   to retrieve the listing, assumes a directory and not a file
	 *               is passed
	 * @throws IOException
	 */
	private static void traverse(InvertedIndex iIndex, String prefix, Path path) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			// looks thru directory
			for (Path file : listing) {
				//System.out.print(prefix + file.getFileName());
				if (Files.isDirectory(file)) {
					// Add a slash so we can tell it is a directory
					//System.out.println("/");
					traverse(iIndex, "  " + prefix, file);
				} else {
					// If file is a text file, stem it
					if (file.toString().matches("(?i).*\\.te?xt$")) {
						TextFileStemmer.stemFile(iIndex, file);
					}
				}
			}
		}
	}

	/**
	 * Safely starts the recursive traversal with the proper padding. Users of
	 * this class can access this method, so some validation is required.
	 *
	 * @param directory to traverse
	 * @throws IOException
	 */
	public static void traverse(InvertedIndex iIndex, Path directory) throws IOException {
		// look thru path if directory
		if (Files.isDirectory(directory)) {
			traverse(iIndex, "- ", directory);
		} else {
			// otherwise stem file from path
			//System.out.println(directory.getFileName());
			TextFileStemmer.stemFile(iIndex, directory);
		}
	}
	
	/* TODO 
	public static void traverse1(InvertedIndex index, Path path) throws IOException {
		if (Files.isDirectory(path)) {
			try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
				for (Path file : listing) {
					traverse1(index, file);
				}
			}
		} else if (path.toString().matches("(?i).*\\.te?xt$")) {
			TextFileStemmer.stemFile(index, path);
		}
	}
	*/
	
}
