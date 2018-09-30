import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.TreeSet;

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

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		// parses args
		ArgumentMap ArgMap = new ArgumentMap(args);
		InvertedIndex iIndex = new InvertedIndex();

		// Traverses and makes inverted index
		try {
			if (ArgMap.hasValue("-path"))
				TraversePath.traverse(iIndex, ArgMap.getPath("-path"));
			else
				System.out.println("No path to traverse!");
		} catch (IOException e) {
			System.out.println("File: " + ArgMap.getString("-path") + " does not exist!");
		}

		// Outputs inverted index to Json
		if (ArgMap.hasFlag("-index")) {
			try {
				Path output = ArgMap.getPath("-index", Paths.get("index.json"));
				if (!Files.exists(output)) {
					Files.createFile(output);
				}
				TreeJSONWriter.asObject(invertedIndex, output);

			} catch (IOException e) {
				System.out.println("Error writing to: " + ArgMap.getString("-index"));
			}
		}
		// Avoids the index stacking up when the second args is run
		invertedIndex.clear();
	}


	@Override
	public String toString() {
		return this.toString();
	}

}

