import java.util.TreeMap;
import java.util.TreeSet;
import java.io.IOException;
import java.nio.file.Files;

/*
 * TODO
 * Put anything generally-useful in its own class outside of Driver
 * In Driver only handle project-specific stuff, just checking for specific flags and values
 * 
 * main {
 *   ArgMap map...
 *   
 *   if (have the -path flag)
 *      trigger building the index
 *     
 *   if (have the -index flag)
 *       trigger writing the index
 * 
 * }
 */

public class Driver {
	// TODO Need to make this non-static, put this in its own data-structure-like class
	// TODO Adding stuff to this data structure, toString(), other methods similar to what you found in WordIndex
	// TODO Step 1: Create your own class but keep the static keyword, Step 2: Remove the static keyword
	public static TreeMap<String, TreeMap<String, TreeSet<Integer>>> invertedIndex = new TreeMap<>();
	
	public ArgumentMap ArgMap;  // TODO Make a local variable inside your main method and then remove the constructor

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		// parses args
		Driver test1 = new Driver(args);
		
		// Traverses and makes inverted index
		try {
			// If I don't get nothing from my -path flag, traverse it
			if (test1.ArgMap.getPath("-path") != null) {
				TraversePath.traverse(test1.ArgMap.getPath("-path"));
			} else 
				System.out.println("Didn't traverse path");
		} catch (NullPointerException e){
			System.out.println("No -path found :(");
		} catch (IOException e) {
			System.out.printf("File: " + test1.ArgMap.getString("-path") + " does not exist!" + System.lineSeparator());
		}
		
		// Outputs inverted index to json if output is true
		if (test1.ArgMap.output == true) {
			try {
				// If -index flag doesn't give me a null 
				if (test1.ArgMap.getString("-index") != null) {
					// If the file doesn't exist, make that file
					if (!Files.exists(test1.ArgMap.getPath("-index"))) {
						Files.createFile(test1.ArgMap.getPath("-index"));
					}
					// Output my index to the provided json file
					TreeJSONWriter.asObject(invertedIndex, test1.ArgMap.getPath("-index"));
				}
				else 
					System.out.println("Didn't format inverted index... no output found");
			} catch (IOException e) {
				System.out.println("Error in opening Path given to 'asObject' method");
			}
		}
		// Avoids the index stacking up when the second args is run
		invertedIndex.clear();
	}
	
	/**
	 * Instantiates Driver and makes a new ArgMap
	 */
	public Driver(String[] args){
		this.ArgMap = new ArgumentMap(args);
	}
	
	
}
