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
		ArgumentMap argMap = new ArgumentMap(args);
		boolean multiThread = argMap.hasFlag("-threads") || argMap.hasFlag("-url");
		int numThreads = argMap.hasValue("-threads") ? Integer.parseInt(argMap.getString("-threads")) : 5;
		InvertedIndex index = multiThread ? new ThreadSafeInvertedIndex() : new InvertedIndex();
		var queryMap = multiThread ? new ThreadSafeQueryMap(index) : new QueryMap(index);
		int limit;

		if (argMap.hasFlag("-limit")) {
			try {
				if (argMap.hasValue("-limit")) {
					limit = Integer.parseInt(argMap.getString("limit"));
				} else {
					limit = 50;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid value for limit.. defaulting to 50");
				limit = 50;
			}
		} else {
			limit = 50;
		}

		//!argMap.hasValue("-flags") ? Integer.parseInt(argMap.getString("-limit")) : 50;
		WebCrawler crawler = new WebCrawler(index, argMap.getString("-url"), limit, numThreads);


		/**
		 *  Traverses and makes inverted index
		 */
		if (argMap.hasValue("-path")) {
			Path output = argMap.getPath("-path");
			try {
				if (multiThread) {
					ThreadSafeInvertedIndexBuilder.traverse(index, output, numThreads);
				} else {
					InvertedIndexBuilder.traverse(index, output);
				}
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
				index.writeIndex(output);
			} catch (IOException e) {
				System.out.println("Error writing to: " + output);
			}
		}

		/**
		 * Performs either an exact or partial search on the inverted index
		 */
		if (argMap.hasFlag("-search")) {
			if (!index.isEmpty()) {
				Path searchFile = argMap.getPath("-search");
				try {
					boolean exact = argMap.hasFlag("-exact");
					if (multiThread) {
						((ThreadSafeQueryMap)queryMap).stemQuery(searchFile, exact, numThreads);
					} else {
						((QueryMap)queryMap).stemQuery(searchFile, exact);
					}
				} catch (IOException e){
					System.out.println("Something went wrong with searching: " + searchFile);
				}
			}
		}

		/**
		 * Outputs the search results to JSON
		 */
		if (argMap.hasFlag("-results")) {
			Path output = argMap.getPath("-results", Paths.get("results.json"));
			try {
				if (multiThread) {
					queryMap.writeJSON(output);
				} else {
					queryMap.writeJSON(output);
				}
			} catch (IOException e) {
				System.out.println("Something went wrong writing search results to: " + output);
			}
		}

		/**
		 * Outputs the location map to JSON
		 */
		if (argMap.hasFlag("-locations")) {
			Path output = argMap.getPath("-locations", Paths.get("locations.json"));
			try {
				index.writeLocation(output);
			} catch (IOException e) {
				System.out.println("Something went wrong writing location to: " + output);
			}
		}
	}
}

