import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {
	// TODO private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> iIndex;

	/*
	 * TODO Really be nothing modifying the string or doing reading of files, etc.
	 * in this class. (No toLowerCase, etc.)
	 */
	
	/**
	 * Initializes the inverted index
	 */
	public InvertedIndex() {
		this.iIndex = new TreeMap<>();
	}

	/* TODO 
	public void writeJSON(Path path) {
		TreeJSONWriter.asObject(iIndex, path);
	}
	*/
	
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
			if (!hasWord(word)) {
				addWord(word);
			}
			if (!hasFile(word, file)) {
				addFile(word, file);
			}
			addPosition(word, file, ++wordCount);
		}
	}

	/**
	 * Checks the inverted index for the word given
	 * after turning it to lowercase
	 *
	 * @param word The word to search for
	 * @return true if the index contains the word
	 */
	public boolean hasWord(String word) {
		// TODO No lowercase
		return this.iIndex.containsKey(word.toLowerCase());
	}

	/**
	 * Checks if the indexed word has the given path
	 *
	 * @param word The word index to find the path
	 * @param path The path to be found
	 * @return true if the path exists in the specific word's index
	 */
	public boolean hasFile(String word, Path path) { // TODO Take a String instead of a Path
		// TODO Check if iIndex.get(word) returns null, if so return false
		return this.iIndex.get(word).containsKey(path.toString());
	}

	// TODO Remove for now, maybe re-add in project 2.
	/**
	 * Checks the whole index for the path and if it
	 * find one, then it returns true
	 *
	 * @param path The path to be found
	 * @return true if the path exists in the inverted index
	 */
	public boolean hasFile(Path path) {
		for (String word : this.iIndex.keySet()) {
			if (hasFile(word, path)) {
				return true;
			}
		}
		return false;
	}

	/* TODO 
	public void add(String word, String location, int position) {
		do all the checking in here instead of in the builder
	}
	*/
	
	// TODO These should maybe be private so that nothing can mess up and overwrite what is already stored
	/**
	 * Adds a word to the new index and creates the inner
	 * data structures (TreeMap<String, TreeSet<Integer>>)
	 *
	 * @param word The word to be put into the index
	 * @param filePath The file to put into the index for this word
	 * @param position The position of the word to add
	 */
	public void addWord(String word) {
		this.iIndex.put(word, new TreeMap<>());
	}

	/**
	 * Adds a file to the index under the given word
	 *
	 * @param word The word to put the filePath under
	 * @param filePath The path to be put in the index
	 */
	public void addFile(String word, Path filePath) {
		this.iIndex.get(word).put(filePath.toString(), new TreeSet<>());
	}

	/**
	 * Adds the position of the word to the index
	 *
	 * @param word The word found from stemming
	 * @param filePath The file to put the position in
	 * @param position The position number to put into the set
	 */
	public void addPosition(String word, Path filePath, int position) {
		this.iIndex.get(word).get(filePath.toString()).add(position);
	}

	@Override
	public String toString() {
		return this.iIndex.toString();
	}
}
