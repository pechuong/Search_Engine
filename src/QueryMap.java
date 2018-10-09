import java.util.List;
import java.util.TreeMap;

public class QueryMap {

	private final TreeMap<String, List<Result>> queryMap;

	public QueryMap() {
		this.queryMap = new TreeMap<>();
	}

	public void addQuery(String path, List<Result> results) {
		System.out.println("hello I got here");
		this.queryMap.put(path, results);
		System.out.println("hello I got here");
	}

	@Override
	public String toString() {
		return this.queryMap.toString();
	}
}
