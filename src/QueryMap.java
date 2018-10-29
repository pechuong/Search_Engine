import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

public class QueryMap {

	/**
	 * Key - the queries that we use to search
	 * Value - the list of results we get back from the search
	 */
	private final TreeMap<String, List<Result>> queryMap;

	/**
	 * Initializes the Query Map
	 */
	public QueryMap() {
		this.queryMap = new TreeMap<>();
	}

	/**
	 * Writes the Query Map to a JSON file given a JSON output path.
	 *
	 * @param path The path to write the map to
	 * @throws IOException
	 */
	public void writeJSON(Path path) throws IOException {
		ResultsJSON.asArray(this.queryMap, path);
	}

	/**
	 * Adds a Query (one search) into the map w/ it's result(s).
	 *
	 * @param search The search query made to store in the map
	 * @param results The list of results to store into the map
	 */
	public void addQuery(String search, List<Result> results) {
		this.queryMap.put(search, results);
	}

	/**
	 * Checks to see if the Query Map is empty or not.
	 * This means that no search has been made or stored.
	 *
	 * @return true if the Query Map has at least 1 entry.
	 */
	public boolean isEmpty() {
		if (this.queryMap.size() < 1) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return this.queryMap.toString();
	}
}
