import java.util.HashMap;
import java.util.TreeSet;

public class SearchResults /*implements Comparable<>*/ {

	private final TreeSet<String> query;
	private final HashMap<String, Object> results;

	public SearchResults() {
		query = new TreeSet<>();
		results = new HashMap<>();
	}

}
