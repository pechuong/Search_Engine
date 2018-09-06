import java.util.Arrays;
import java.util.HashMap;
import java.nio.file.Files;


/**
 * TODO Fill in your own comments!
 */
public class Driver {
	
	private HashMap<String, HashMap<String, Integer>> invertedIndex;

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 * @return 0 if everything went well
	 */
	public static void main(String[] args) {
		// TODO Fill in
		System.out.println(Arrays.toString(args));
		return 0;
	}
	
	/**
	 * Initializes the inverted index
	 */
	public Driver(){
		this.invertedIndex = new HashMap<>();
	}
	
	/**
	 * 
	 * @param args
	 */
	public void parseArgs(String[] args) {
		
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
	
	/**
	 * Figures out if given argument is a flag or not
	 * 
	 * @param arg the command-line argument given
	 * @return true if it is a flag
	 */
	public static boolean isFlag(String arg) {
		if (arg == null) {
			return false;
		}
		return arg.matches("^-\\S+");
	}
	

}
