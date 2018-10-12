import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class InvertedIndex {
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/*
	 * TODO Really be nothing modifying the string or doing reading of files, etc.
	 * in this class. (No toLowerCase, etc.)
	 */

	/**
	 * Initializes the inverted index
	 */
	public InvertedIndex() {
		this.index = new TreeMap<>();
	}

	/**
	 * Writes the index to a JSON file
	 *
	 * @param path The file to write the index to
	 */
	public void writeJSON(Path path) throws IOException, Exception {
		TreeJSONWriter.asObject(index, path);
	}

	/**
	 * Builds the inverted index
	 * - checks for the word
	 * - checks if word has file
	 * - adds position
	 *
	 * @param wordList The list of words stemmed from 1 file
	 * @param file The file the words were stemmed from
	 */
	public void buildiIndex(List<String> wordList, Path file) {
		int wordCount = 0;
		for (String word : wordList) {
			add(word, file.toString(), ++wordCount);
		}
	}


	public List<Result> exactSearch(LocationMap lMap, TreeSet<String> queryLine) {
		/*//METHOD 1
		HashMap<String, Result> resultMap = new HashMap<>();

		// go thru my search words
		for (String word : queryLine) {
			// is it inside the index
			if (hasWord(word)) {
				// go thru all files with the word and add them to index
				for (String fileName : this.index.get(word).keySet()) {
					if (resultMap.containsKey(fileName)) {
						resultMap.get(fileName).addMatches(this.index.get(word).get(fileName).size());
						continue;
					}
					resultMap.put(fileName, new Result(fileName, this.index.get(word).get(fileName).size(), lMap.getFile(fileName)));
				}
			}
		}

		List<Result> sortedResults = resultMap.values().stream()
				.sorted((result1, result2) -> result1.compareTo(result2))
				.collect(Collectors.toList());
		return sortedResults;
		 */
		HashMap<String, Result> resultMap = new HashMap<>();

		for (String word : this.index.keySet().stream()
				.filter((word) -> queryLine.contains(word))
				.collect(Collectors.toSet())) {
			this.index.get(word).keySet().stream()
			.forEach((fileName)-> {
				if (resultMap.containsKey(fileName)) {
					resultMap.get(fileName).addMatches(this.index.get(word).get(fileName).size());
				} else {
					resultMap.put(fileName, new Result(fileName, this.index.get(word).get(fileName).size(), lMap.getFile(fileName)));
				}
			});

		}

		return resultMap.values().stream()
				.sorted((result1, result2) -> result1.compareTo(result2))
				.collect(Collectors.toList());
	}

	public List<Result> partialSearch(LocationMap lMap, TreeSet<String> queryLine) {
		HashMap<String, Result> resultMap = new HashMap<>();

		for (String word : queryLine) {
			if (hasWord(word) || startsWith(word)) {
				for (String key : this.index.keySet()) {
					if (key.startsWith(word)) {
						for (String fileName : this.index.get(key).keySet()) {
							if (resultMap.containsKey(fileName)) {
								resultMap.get(fileName).addMatches(this.index.get(key).get(fileName).size());
							} else {
								resultMap.put(fileName, new Result(fileName, this.index.get(key).get(fileName).size(), lMap.getFile(fileName)));
							}
						}
					}
				}
			}
		}
		List<Result> sortedResults = resultMap.values().stream()
				.sorted((result1, result2) -> result1.compareTo(result2))
				.collect(Collectors.toList());
		return sortedResults;
		/*
		HashMap<String, Result> resultMap = new HashMap<>();

		for (String word : this.index.keySet().stream()
				.filter((word) -> queryLine.contains(word))
				.collect(Collectors.toSet())) {


		}

		List<Result> sortedResults = resultMap.values().stream()
				.sorted((result1, result2) -> result1.compareTo(result2))
				.collect(Collectors.toList());
		return sortedResults;
		 */
	}


	/**
	 * Checks the inverted index for the word given
	 * after turning it to lowercase
	 *
	 * @param word The word to search for
	 * @return true if the index contains the word
	 */
	public boolean hasWord(String word) {
		return this.index.containsKey(word);
	}

	public boolean startsWith(String word) {
		for (String key : this.index.keySet()) {
			if (key.startsWith(word)) {
				return true;
			}
		}
		return false;
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
	 * Adds the word to the index given the word, location, and position
	 *
	 * @param word The word to add to the index
	 * @param location The file from where the word came from
	 * @param position The position that word is right now
	 */
	private void add(String word, String location, int position) {
		//do all the checking in here instead of in the builder
		if (!hasWord(word)) {
			addWord(word);
		}
		if (!hasFile(word, location)) {
			addFile(word, location);
		}
		addPosition(word, location, position);
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
	 * Adds a file to the index under the given word
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

	@Override
	public String toString() {
		return this.index.toString();
	}
}
