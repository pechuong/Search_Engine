import java.util.TreeMap;
import java.util.TreeSet;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Driver {
	
	public static TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex = new TreeMap<>();
	public ArgumentMap ArgMap;

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		// parses args
		Driver test1 = new Driver(args);
		
		// traverses and makes inverted index
		try {
			if (test1.ArgMap.getPath("-path") != null) {
				traverse(test1.ArgMap.getPath("-path"));
			} else 
				System.out.println("Didn't traverse path");
		} catch (NullPointerException e){
			System.out.println("No -path found :(");
		} catch (IOException e) {
			System.out.println("File doesn't exist");
		}
		
		// outputs inverted index to json
		if (test1.ArgMap.output == true) {
			try {
				if (test1.ArgMap.getString("-index") != null) {
					if (!Files.exists(test1.ArgMap.getPath("-index"))) {
						Files.createFile(test1.ArgMap.getPath("-index"));
					}
					TreeJSONWriter.asObject(invertedIndex, test1.ArgMap.getPath("-index"));
				}
				else 
					System.out.println("Didn't format inverted index... no output found");
			} catch (IOException e) {
				System.out.println("Error in opening Path given to 'asObject' method");
			}
		}
		invertedIndex.clear();
	}
	
	/**
	 * Initializes the inverted index
	 */
	public Driver(String[] args){
		this.ArgMap = new ArgumentMap(args);
	}
	
	/**
	 * Outputs the name of the file or subdirectory, with proper indentation to
	 * help indicate the hierarchy. If a subdirectory is encountered, will
	 * recursively list all the files in that subdirectory. For each file found,
	 * the function will stem that file into an inverted index
	 *
	 * The recursive version of this method is private. Users of this class will
	 * have to use the public version (see below).
	 *
	 * @param prefix the padding or prefix to put infront of the file or
	 *               subdirectory name
	 * @param path   to retrieve the listing, assumes a directory and not a file
	 *               is passed
	 * @throws IOException
	 */
	private static void traverse(String prefix, Path path) throws IOException {
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			// looks thru directory
			for (Path file : listing) {
				//System.out.print(prefix + file.getFileName());
				if (Files.isDirectory(file)) {
					// Add a slash so we can tell it is a directory
					System.out.println("/");
					traverse("  " + prefix, file);
				} else {
					if (file.toString().matches(".*[tT][eE][xX][tT]$") || file.toString().matches(".*[tT][xX][tT]$")) {
						TextFileStemmer.stemFile(file);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Files doesn't exist!");
		}
	}

	/**
	 * Safely starts the recursive traversal with the proper padding. Users of
	 * this class can access this method, so some validation is required.
	 *
	 * @param directory to traverse
	 * @throws IOException
	 */
	public static void traverse(Path directory) throws IOException {
		if (Files.isDirectory(directory)) {
			traverse("- ", directory);
		} else {
			System.out.println(directory.getFileName());
			TextFileStemmer.stemFile(directory);
		}
	}
}
