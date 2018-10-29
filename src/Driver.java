import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeSet;

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
		LocationMap locMap = new LocationMap();
		QueryMap queryMap = new QueryMap();

		/**
		 *  Traverses and makes inverted index
		 */
		if (argMap.hasValue("-path")) {
			Path output = argMap.getPath("-path");
			try {
				InvertedIndexBuilder.traverse(index, locMap, output);
			} catch (IOException e) {
				System.out.println("Unable to build from: " + output);
			}
		}

		/**
		 * Outputs inverted index to Json
		 */
		if (argMap.hasFlag("-index")) {
			Path output = argMap.getPath("-index", Paths.get("index.json"));
			try {
				index.writeJSON(output);
			} catch (IOException e) {
				System.out.println("Error writing to: " + output);
			}
		}

		/**
		 * Performs either an exact or partial search on the inverted index
		 */
		if (argMap.hasFlag("-search")) {
			Path searchFile = argMap.getPath("-search");
			try {
				List<TreeSet<String>> queries = TextFileStemmer.stemQuery(searchFile);
				if (argMap.hasFlag("-exact")) {
					for (TreeSet<String> oneSearch : queries) {
						String searchName = String.join(" ", oneSearch);
						queryMap.addQuery(searchName, index.exactSearch(locMap, oneSearch));
					}
				} else {
					for (TreeSet<String> oneSearch : queries) {
						String searchName = String.join(" ", oneSearch);
						queryMap.addQuery(searchName, index.partialSearch(locMap, oneSearch));
					}
				}
			} catch (IOException e){
				System.out.println("Something went wrong searching: " + searchFile);
			}
		}

		/**
		 * Outputs the search results to JSON
		 */
		if (argMap.hasFlag("-results")) {
			Path output = argMap.getPath("-results", Paths.get("results.json"));
			try {
				queryMap.writeJSON(output);
			} catch (IOException e) {
				System.out.println("Error writing search results to: " + output);
			}
		}

		/**
		 * Outputs the location map to JSON
		 */
		if (argMap.hasFlag("-locations")) {
			Path output = argMap.getPath("-locations", Paths.get("locations.json"));
			try {
				locMap.writeJSON(output);
			} catch (IOException e) {
				System.out.println("Error writing location to: " + output);
			}
		}
	}
}

