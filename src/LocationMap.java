import java.nio.file.Path;
import java.util.TreeMap;

public class LocationMap {

	private final TreeMap<String, Integer> location;

	/**
	 * Initializes the location
	 */
	public LocationMap() {
		this.location = new TreeMap<>();
	}

	/**
	 * Checks if the location Map has this file
	 *
	 * @param path The file to search for in the map
	 * @return true if the file was found
	 */
	public boolean hasFile(String path) {
		return this.location.containsKey(path);
	}

	/**
	 * Builds the locationMap further and adds to it
	 *
	 * @param inputFile The file to add to the map
	 * @param wordCount The word count to associate w/ file
	 */
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

	@Override
	public String toString() {
		return this.location.toString();
	}
}
