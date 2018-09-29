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


}
