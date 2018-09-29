import java.util.TreeMap;
import java.util.TreeSet;

public class InvertedIndex {
	public static TreeMap<String, TreeMap<String, TreeSet<Integer>>> iIndex;

	/**
	 * Initializes the inverted index
	 */
	public InvertedIndex() {
		this.iIndex = new TreeMap<>();
	}

	/**
	 * Checks the inverted index for the word given
	 * after turning it to lowercase
	 *
	 * @param word The word to search for
	 * @return true if the index contains the word
	 */
	public boolean hasWord(String word) {
		return iIndex.containsKey(word.toLowerCase());
	}

	/**
	 * Checks if the indexed word has the given path
	 *
	 * @param word The word index to find the path
	 * @param path The path to be found
	 * @return true if the path exists in the specific word's index
	 */
	public boolean hasFile(String word, String path) {
		return iIndex.get(word).containsKey(path);
	}

	/**
	 * Checks the whole index for the path and if it
	 * find one, then it returns true
	 *
	 * @param path The path to be found
	 * @return true if the path exists in the inverted index
	 */
	public boolean hasFile(String path) {
		for (String word : iIndex.keySet()) {
			if (hasFile(word, path)) {
				return true;
			}
		}
		return false;
	}

}
