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
		LocationMap lMap = new LocationMap();

		// Traverses and makes inverted index
		if (argMap.hasValue("-path")) {
			try {
				TraversePath.traverse(index, lMap, argMap.getPath("-path"));
			} catch (IOException e) {
				System.out.println("Unable to build from: " + argMap.getString("-path"));
			}
		}

		System.out.println(lMap.toString());

		// Outputs inverted index to Json
		if (argMap.hasFlag("-index")) {
			try {
				Path output = argMap.getPath("-index", Paths.get("index.json")); // TODO Move outside of try-catch
				if (!Files.exists(output)) { // TODO Might be able to remove?
					Files.createFile(output);
				}
				index.writeJSON(output);

			} catch (IOException e) {
				// TODO System.out.println("Error writing to: " + output);
				System.out.println("Error writing to: " + argMap.getString("-index"));
			} catch (Exception e) {
				System.out.println("Error");
			}
		}

		if (argMap.hasFlag("-search")) {
			System.out.println("I'm going to stem the query file first!");
			if (argMap.hasFlag("-exact")) {
				System.out.println("I'm going to do an exact search!");
			} else {
				// do partial search
				System.out.println("I'm going to do a partial search!");
			}
		}

		if (argMap.hasFlag("-results")) {
			try {
				Path output = argMap.getPath("-results", Paths.get("results.json"));
				System.out.println("I'm going to write out search results to Json");
			} catch (IOException e) {
				System.out.println("Error writing to: " + argMap.getString("-results"));
			}
		}

		if (argMap.hasFlag("-locations")) {
			try {
				Path output = argMap.getPath("-results", Paths.get("locations.json"));
				System.out.println("I'm going to write out locations to Json");
			} catch (IOException e) {
				System.out.println("Error writing to: " + argMap.getString("-locations"));
			}
		}
	}
}

