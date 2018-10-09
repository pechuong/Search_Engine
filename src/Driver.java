import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
		LocationMap lMap = new LocationMap();
		QueryMap queryMap = new QueryMap();

		// Traverses and makes inverted index
		if (argMap.hasValue("-path")) {
			try {
				TraversePath.traverse(index, lMap, argMap.getPath("-path"));
			} catch (IOException e) {
				System.out.println("Unable to build from: " + argMap.getString("-path"));
			}
		}

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

		// Searches
		if (argMap.hasFlag("-search")) {
			try {
				Path searchFile = argMap.getPath("-search");
				ArrayList<TreeSet<String>> queries = TextFileStemmer.stemQuery(searchFile);
				if (argMap.hasFlag("-exact")) {
					// Does one search at a time
					for (TreeSet<String> oneSearch : queries) {
						String searchName = String.join(" ", oneSearch);
						queryMap.addQuery(searchName, index.exactSearch(lMap, oneSearch));
					}
				} else {
					// do partial search
					System.out.println("I'm going to do a partial search!");
				}
			} catch (IOException e){
				System.out.println("Something went wrong with: " + argMap.getString("-search"));
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
				Path output = argMap.getPath("-locations", Paths.get("locations.json"));
				lMap.writeJSON(output);
			} catch (IOException e) {
				System.out.println("Error writing to: " + argMap.getString("-locations"));
			}
		}
	}
}

