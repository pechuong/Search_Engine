import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
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
		// TODO Fill in
		System.out.println(Arrays.toString(args));
		Driver test1 = new Driver(args);
		//TODO traverse Directory
		
		
		//TODO parse the file and stem words into index (stem hw)
		
		
		//TODO format the inverted index into JSON file if output is true
		TreeJSONWriter.asObject(invertedIndex, (test1.ArgMap.output = true)?test1.ArgMap.get("-index"));
		
		
		//test1.ArgMap.put("-path", "test/path");
		System.out.println(test1.ArgMap.toString());
		//parseArgs(args);
	}
	
	/**
	 * Initializes the inverted index
	 */
	public Driver(String[] args){
		this.ArgMap = new ArgumentMap(args);
	}
	// start of lab04 
	
	
	
	
	
	
	
	
	
	//TODO Make a method for traversing a directory and listing all text files
	public void listFiles(Path dir) {
		
	}
	
	/**
	 * Determines if given argument is a text file
	 * (if ends w/ .text or .txt)
	 * 
	 * @param arg the command-line argument given
	 * @return true if the given argument is a text file
	 */
	public static boolean isText(String path) {
		return path.endsWith(".text") || path.endsWith(".txt");
	}
	
	/**
	 * Determines if given argument is a directory
	 * 
	 * @param arg the command-line argument given
	 * @return true if the given argument is a directory
	 */
	public static boolean isDir(String path) {
		if (path == null) {
			return false;
		}
		return !isText(arg);
	}

}
