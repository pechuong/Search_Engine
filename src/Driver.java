import java.util.Arrays;
import java.util.HashMap;

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
	public static int main(String[] args) {
		// TODO Fill in
		System.out.println(Arrays.toString(args));
		return 0;
		
		// Start of lab
	}
	
	/**
	 * Initializes the inverted index
	 */
	public Driver(){
		this.invertedIndex = new HashMap<>();
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
		return !isText(arg);
	}

}
