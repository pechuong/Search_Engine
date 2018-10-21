import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		// parses args
		ArgumentMap argMap = new ArgumentMap(args);
		InvertedIndex index = new InvertedIndex();

		// Traverses and makes inverted index
		if (argMap.hasValue("-path")) {
			Path output = argMap.getPath("-path");
			try {
				InvertedIndexBuilder.traverse(index, output);
			} catch (IOException e) {
				System.out.println("Unable to build from: " + output);
			}
		}
		// Outputs inverted index to Json
		if (argMap.hasFlag("-index")) {
			Path output = argMap.getPath("-index", Paths.get("index.json"));
			try {
				index.writeJSON(output);
			} catch (IOException e) {
				System.out.println("Error writing to: " + output);
			}
		}
	}
}

