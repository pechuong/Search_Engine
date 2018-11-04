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
		//InvertedIndex index = new InvertedIndex();
		//QueryMap queryMap = new QueryMap(index);
		InvertedIndex index;
		QueryMap queryMap;
		int numThreads = 1;
		boolean multiThread = argMap.hasFlag("-threads");

		if (multiThread) {
			numThreads = argMap.hasValue("-threads") ? Integer.parseInt(argMap.getString("-threads")) : 5;
			index = new ThreadSafeInvertedIndex();
		} else {
			index = new InvertedIndex();
		}
		queryMap = new QueryMap(index);

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
						queryMap.stemQuery(searchFile, exact);
					} else {
						queryMap.stemQuery(searchFile, exact);
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
				queryMap.writeJSON(output);
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

