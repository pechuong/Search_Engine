import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	private final TreeMap<String, Integer> location;

	/**
	 * Initializes the inverted index
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
		this.location = new TreeMap<>();
	}

	/**
	 * Writes the index to a JSON file
	 *
	 * @param path The file to write the index to
	 */
	public void writeIndex(Path path) throws IOException {
		TreeJSONWriter.asDoubleNestedObject(index, path);
	}

	/**
	 * Writes the location to a JSON file given the output path
	 *
	 * @param output
	 * @throws IOException
	 */
	public void writeLocation(Path output) throws IOException {
		TreeJSONWriter.asLocation(location, output);
	}

	/**
	 * Builds / adds to the inverted index
	 *
	 * @param word The word to add to the index
	 * @param file The file the word was stemmed from
	 * @param count The position the word is found in from the file
	 */
	public void build(String word, String file, int count) {
		add(word, file, count);
	}

	/**
	 * Performs an exact search on index given a set of query words
	 *
	 * @param queryLine set of query words to perform one search on
	 * @return List<Result> list of all the results from the search
	 */
	public List<Result> exactSearch(Set<String> queryLine) {
		HashMap<String, Result> lookUp = new HashMap<>();
		ArrayList<Result> results = new ArrayList<>();

		for (String query : queryLine) {
			if (index.containsKey(query)) {
				handleResults(query, lookUp, results);
			}
		}

		Collections.sort(results);
		return results;
	}

	/**
	 * Performs a partial search on index given a set of query words
	 *
	 * @param queryLine The set of query words to search for
	 * @return List<Result> list of all results from the search
	 */
	public List<Result> partialSearch(Set<String> queryLine) {
		HashMap<String, Result> lookUp = new HashMap<>();
		ArrayList<Result> results = new ArrayList<>();

		for (String query : queryLine) {
			for (String word : index.tailMap(query, true).keySet()) {
				if (word.startsWith(query) || word.equalsIgnoreCase(query)) {
					handleResults(word, lookUp, results);
				} else {
					break;
				}
			}
		}

		Collections.sort(results);
		return results;
	}
	/**
	 * Handles what happens when a match or result is found
	 *
	 * @param word The word matched to the index
	 * @param lookUp The map of existing results
	 * @param results The list of result objects
	 */
	private void handleResults(String word, HashMap<String, Result> lookUp, ArrayList<Result> results) {
		for (String path : index.get(word).keySet()) {
			if (lookUp.containsKey(path)) {
				lookUp.get(path).addMatches(getWordCount(word, path));
			} else {
				Result result = new Result(path, getWordCount(word, path), getLocationCount(path));
				lookUp.put(path, result);
				results.add(result);
			}
		}
	}

	/**
	 * Check if the inverted index is empty or not
	 *
	 * @return true if the index is empty
	 */
	public boolean isEmpty() {
		return this.index.keySet().isEmpty();
	}

	/**
	 * Checks the inverted index for the word given
	 *
	 * @param word The word to search for
	 * @return true if the index contains the word
	 */
	public boolean hasWord(String word) {
		return this.index.containsKey(word);
	}

	/**
	 * Checks if the indexed word has the given path
	 *
	 * @param word The word index to find the path
	 * @param path The path to be found
	 * @return true if the path exists in the specific word's index
	 */
	public boolean hasFile(String word, String path) {
		if (hasWord(word)) {
			return this.index.get(word).containsKey(path);
		}
		return false;
	}

	/**
	 * Checks if the index has a position based on given word and path
	 *
	 * @param word The word to find in the index
	 * @param path The file to find for the word
	 * @param position The position to find
	 * @return true if position is found
	 */
	public boolean hasPosition(String word, String path, int position) {
		if (hasFile(word, path)) {
			return this.index.get(word).get(path).contains(position);
		}
		return false;
	}

	/**
	 * Adds the word to the index given the word, location, and position
	 *
	 * @param word The word to add to the index
	 * @param location The file from where the word came from
	 * @param position The position that word is right now
	 */
	public void add(String word, String location, int position) {
		if (!hasWord(word)) {
			addWord(word);
		}
		if (!hasFile(word, location)) {
			addFile(word, location);
		}
		addPosition(word, location, position);
		updateLocation(location);
	}

	/**
	 * Adds a word to the new index and creates the inner
	 * data structures (TreeMap<String, TreeSet<Integer>>)
	 *
	 * @param word The word to be put into the index
	 * @param filePath The file to put into the index for this word
	 * @param position The position of the word to add
	 */
	private void addWord(String word) {
		this.index.put(word, new TreeMap<>());
	}

	/**
	 * Adds a file to the index under the given word and
	 * creates the last inner data structure (TreeSet<Integer>)
	 *
	 * @param word The word to put the filePath under
	 * @param filePath The path to be put in the index
	 */
	private void addFile(String word, String filePath) {
		this.index.get(word).put(filePath, new TreeSet<>());
	}

	/**
	 * Adds the position of the word to the index
	 *
	 * @param word The word found from stemming
	 * @param filePath The file to put the position in
	 * @param position The position number to put into the set
	 */
	private void addPosition(String word, String filePath, int position) {
		this.index.get(word).get(filePath).add(position);
	}

	/**
	 * Updates the word count at the specified location or file
	 *
	 * @param path The location to update the int value for
	 */
	private void updateLocation(String path) {
		this.location.put(path, this.location.getOrDefault(path, 0) + 1);
	}

	public InvertedIndex getInvertedIndex() {
		return this;
	}

	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		return this.index;
	}

	public TreeMap<String, TreeSet<Integer>> getFiles(String word) {
		return this.index.get(word);
	}

	public int getWordCount(String word, String filePath) {
		return this.index.get(word).get(filePath).size();
	}

	public TreeMap<String, Integer> getLocation() {
		return this.location;
	}

	/**
	 * Gets the number of words in the file path or location
	 *
	 * @param location The location to get the num of words for
	 * @return word count in the location
	 */
	public int getLocationCount(String location) {
		return this.location.get(location);
	}

	@Override
	public String toString() {
		return this.index.toString() + this.location.toString();
	}
}
