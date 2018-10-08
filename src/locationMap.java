import java.util.TreeMap;

public class locationMap {

	private final TreeMap<String, Integer> location;

	public locationMap() {
		this.location = new TreeMap<>();
	}

	public boolean hasFile(String path) {
		return this.location.containsKey(path);
	}

	private void add(String path, int amount) {
		if (!hasFile(path)) {
			addFile(path);
		}
		addWordCount(path, amount);

	}

	private void addFile(String path) {
		this.location.put(path, 0);
	}

	private void addWordCount(String path, int amount) {
		this.location.put(path, this.location.get(path) + amount);
	}
}
