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


	public void writeJSON(Path path) throws IOException {
		ResultsJSON.asArray(this.queryMap, path);
	}

	public void addQuery(String path, List<Result> results) {
		this.queryMap.put(path, results);
	}

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
