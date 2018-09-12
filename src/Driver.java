import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.io.IOException;
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
		// also parses args
		Driver test1 = new Driver(args);
		//TODO traverse Directory
		
		
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
	

}
