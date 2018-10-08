import java.nio.file.Path;
import java.util.TreeMap;

public class LocationMap {

	private final TreeMap<String, Integer> location;

	public LocationMap() {
		this.location = new TreeMap<>();
	}

	public boolean hasFile(String path) {
		return this.location.containsKey(path);
	}

	public void buildLocation(Path inputFile, int wordCount) {
		add(inputFile.toString(), wordCount);
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
