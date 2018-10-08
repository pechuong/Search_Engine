import java.io.IOException;
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

	public void writeJSON(Path output) throws IOException {
		LocationJSON.asObject(location, output);
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

	/**
	 * Adds the path and wordCount to the map and does
	 * the checking to see if the file already exists or no
	 *
	 * @param path The file to put in the map
	 * @param wordCount The word to associate it with
	 */
	private void add(String path, int wordCount) {
		if (!hasFile(path)) {
			addFile(path);
			addWordCount(path, wordCount);
		}

	}

	/**
	 * Adds the file to the location and initializes the count
	 *
	 * @param path The file to put into the map
	 */
	private void addFile(String path) {
		this.location.put(path, 0);
	}

	/**
	 * Adds the wordCount to the specific file
	 *
	 * @param path The path to add the wordCount to
	 * @param amount The number of word to associate with the file
	 */
	private void addWordCount(String path, int amount) {
		this.location.put(path, this.location.get(path) + amount);
	}

	@Override
	public String toString() {
		return this.location.toString();
	}
}
