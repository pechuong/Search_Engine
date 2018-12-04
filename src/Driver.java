import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Driver {

	public static final Logger log = LogManager.getLogger(Driver.class);

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		ArgumentMap argMap = new ArgumentMap(args);
		InvertedIndex index;
		ThreadSafeInvertedIndex threadSafe = null;
		Query queryMap;

		boolean multiThread = argMap.hasFlag("-threads") || argMap.hasFlag("-url");
		int numThreads = argMap.hasValue("-threads") ? Integer.parseInt(argMap.getString("-threads")) : 5;
		int limit;

		if (argMap.hasFlag("-limit")) {
			try {
				limit = Integer.parseInt(argMap.getString("-limit"));
			} catch (NumberFormatException e) {
				log.debug("Invalid value for limit.. defaulting to 50");
				limit = 50;
			} catch (Exception e) {
				log.debug(e);
				limit = 50;
			}
		} else {
			limit = 50;
		}

		if (multiThread) {
			threadSafe = new ThreadSafeInvertedIndex();
			index = threadSafe;
			queryMap = new ThreadSafeQueryMap(threadSafe, numThreads);
		} else {
			index = new InvertedIndex();
			queryMap = new QueryMap(index);
		}

		WebCrawler crawler = new WebCrawler(threadSafe, argMap.getString("-url"), limit, numThreads);

		/**
		 *  Traverses and makes inverted index
		 */
		if (argMap.hasValue("-path")) {
			Path output = argMap.getPath("-path");
			try {
				if (threadSafe != null) {
					ThreadSafeInvertedIndexBuilder.traverse(threadSafe, output, numThreads);
				} else {
					InvertedIndexBuilder.traverse(index, output);
				}
			} catch (IOException e) {
				log.debug("Unable to build from: " + output);
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
				log.debug("Error writing to: " + output);
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
					queryMap.stemQuery(searchFile, exact);
				} catch (IOException e){
					log.debug("Something went wrong with searching: " + searchFile);
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
				log.debug("Something went wrong writing search results to: " + output);
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
				log.debug("Something went wrong writing location to: " + output);
			}
		}
	}
}

