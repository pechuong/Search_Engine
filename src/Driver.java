import java.util.Arrays;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * TODO Fill in your own comments!
 */
public class Driver {
	
	private final HashMap<String, HashMap<String, Integer>> invertedIndex;
	private HashMap<String, String> ArgMap;
	private static boolean outputting = false;

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		// TODO Fill in
		System.out.println(Arrays.toString(args));
		//parseArgs(args);
	}
	
	/**
	 * Initializes the inverted index
	 */
	public Driver(){
		this.invertedIndex = new HashMap<>();
		this.ArgMap = new HashMap<>();
	}
	
	
	
	
	
	
	
	
	
	
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
	public static boolean isText(String arg) {
		return arg.endsWith(".text") || arg.endsWith(".txt");
	}
	
	/**
	 * Determines if given argument is a directory
	 * 
	 * @param arg the command-line argument given
	 * @return true if the given argument is a directory
	 */
	public static boolean isDir(String arg) {
		if (arg == null) {
			return false;
		}
		return !isText(arg);
	}

}
