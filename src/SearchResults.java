import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class SearchResults /*implements Comparable<>*/ {

	private final ArrayList<TreeSet<String>> query;
	private final HashMap<String, Integer> results;

	public SearchResults() {
		query = new ArrayList<>();
		results = new HashMap<>();
	}



}
