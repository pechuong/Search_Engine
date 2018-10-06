import java.io.IOException;
import java.nio.file.Files;
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

		// TODO Nest the try/catch inside of the if-statement
		// Traverses and makes inverted index
		try {
			if (argMap.hasValue("-path")) {
				TraversePath.traverse(index, argMap.getPath("-path"));
			} else {
				System.out.println("No path to traverse!");
			}
		} catch (IOException e) {
			System.out.println("File: " + argMap.getString("-path") + " does not exist!");
			// TODO sysout(Unable to build from + path);
		}

		// Outputs inverted index to Json
		if (argMap.hasFlag("-index")) {
			try {
				Path output = argMap.getPath("-index", Paths.get("index.json")); // TODO Move outside of try-catch
				if (!Files.exists(output)) { // TODO Might be able to remove?
					Files.createFile(output);
				}
				TreeJSONWriter.asObject(index, output);

			} catch (IOException e) {
				// TODO System.out.println("Error writing to: " + output);
				System.out.println("Error writing to: " + argMap.getString("-index"));
			}
		}
	}
}

