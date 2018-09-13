import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * TODO Fill in your own comments!
 */
public class Driver {
	
	private static TreeMap<String, HashMap<String, TreeSet<Integer>>> invertedIndex = new TreeMap<>();
	private ArgumentMap ArgMap;

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		System.out.println(Arrays.toString(args));
		
		// TODO Parses args
		Driver test1 = new Driver(args);
		
		//TODO traverse Directory
		try {
			traverse(test1.ArgMap.getPath("-path"));
		} catch (Exception e){
			System.out.println("Error");
		}
		
		//TODO parse the file and stem words into index (stem hw)
		
		
		//TODO format the inverted index into JSON file if output is true
		if (test1.ArgMap.output = true) {
			try {
				TreeJSONWriter.asObject(invertedIndex, Paths.get(test1.ArgMap.getString("index")));
			} catch (IOException e) {
				System.out.println("Error in opening Path given to 'asObject' method");
			}
		} else {
			TreeJSONWriter.asObject(invertedIndex);
		}		
		
		//test1.ArgMap.put("-path", "test/path");
		System.out.println(test1.ArgMap.toString());
	}
	
	/**
	 * Initializes the inverted index
	 */
	public Driver(String[] args){
		this.ArgMap = new ArgumentMap(args);
	}
	
	//TODO Make a method for traversing a directory and listing all text files
	// The below methods are given from lecture
	
	/**
	 * Outputs the name of the file or subdirectory, with proper indentation to
	 * help indicate the hierarchy. If a subdirectory is encountered, will
	 * recursively list all the files in that subdirectory.
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
		/*
		 * The try-with-resources block makes sure we close the directory stream
		 * when done, to make sure there aren't any issues later when accessing this
		 * directory.
		 *
		 * Note, however, we are still not catching any exceptions. This type of try
		 * block does not have to be accompanied with a catch block. (You should,
		 * however, do something about the exception.)
		 */
		try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
			// Efficiently iterate through the files and subdirectories.
			for (Path file : listing) {
				// Print the name with the proper padding/prefix.
				System.out.print(prefix + file.getFileName());

				// Check if this is a subdirectory
				if (Files.isDirectory(file)) {
					// Add a slash so we can tell it is a directory
					System.out.println("/");

					// Recursively traverse the subdirectory.
					// Add a little bit of padding so files in subdirectory
					// are indented under that directory.
					traverse("  " + prefix, file);
				} else {
					// Add the file size next to the name
					System.out.printf(" (%d bytes)%n", Files.size(file));
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
	public static void traverse(Path directory) throws IOException {
		if (Files.isDirectory(directory)) {
			traverse("- ", directory);
		} else {
			System.out.println(directory.getFileName());
		}
	}
}
