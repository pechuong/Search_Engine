import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;

public class QueryMap {

	private final TreeMap<String, List<Result>> queryMap;

	public QueryMap() {
		this.queryMap = new TreeMap<>();
	}

	public void addQuery(String path, List<Result> results) {
		this.queryMap.put(path, results);
	}

	public void writeJSON(Path path) throws IOException {
		ResultsJSON.asArray(this.queryMap, path);
	}

	@Override
	public String toString() {
		return this.queryMap.toString();
	}
}
