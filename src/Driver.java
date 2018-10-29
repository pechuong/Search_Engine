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
		LocationMap lMap = new LocationMap();
		QueryMap queryMap = new QueryMap();

		// Traverses and makes inverted index
		if (argMap.hasValue("-path")) {
			Path output = argMap.getPath("-path");
			try {
				InvertedIndexBuilder.traverse(index, output);
				InvertedIndexBuilder.traverse(index, lMap, argMap.getPath("-path"));
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

		/**
		 * Searches the Query file and performs either an exact or partial search.
		 * The result is stored in query map where the key is the query / the search
		 * word or phrase to perform on and the value is all the result objects I get
		 * as a result of performing the search.
		 */
		if (argMap.hasFlag("-search")) {
			Path searchFile = argMap.getPath("-search");
			try {
				List<TreeSet<String>> queries = TextFileStemmer.stemQuery(searchFile);
				if (argMap.hasFlag("-exact")) {
					for (TreeSet<String> oneSearch : queries) {
						String searchName = String.join(" ", oneSearch);
						queryMap.addQuery(searchName, index.exactSearch(lMap, oneSearch));
					}
				} else {
					for (TreeSet<String> oneSearch : queries) {
						String searchName = String.join(" ", oneSearch);
						queryMap.addQuery(searchName, index.partialSearch(lMap, oneSearch));
					}
				}
			} catch (IOException e){
				System.out.println("Something went wrong searching: " + searchFile);
			}
		}

		/**
		 * Prints out the results from the searching.
		 * Goes through the query map and outputs it to a json format.
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
		 * Outputs a Json file showing the total word count per file.
		 * Outputs from a Location Map that stores file / path as it's key and
		 * the total count of stemmed words in it's value.
		 */
		if (argMap.hasFlag("-locations")) {
			Path output = argMap.getPath("-locations", Paths.get("locations.json"));
			try {
				lMap.writeJSON(output);
			} catch (IOException e) {
				System.out.println("Error writing location to: " + output);
			}
		}
	}
}

