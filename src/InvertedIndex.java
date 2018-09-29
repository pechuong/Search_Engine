import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

import javafx.scene.shape.Path;

public class InvertedIndex {
	public static TreeMap<String, TreeMap<String, TreeSet<Integer>>> iIndex;

	/**
	 * Initializes the inverted index
	 */
	public InvertedIndex() {
		this.iIndex = new TreeMap<>();
	}

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
		return iIndex.containsKey(word.toLowerCase());
	}

	/**
	 * Checks if the indexed word has the given path
	 *
	 * @param word The word index to find the path
	 * @param path The path to be found
	 * @return true if the path exists in the specific word's index
	 */
	public boolean hasFile(String word, Path path) {
		return iIndex.get(word).containsKey(path.toString());
	}

	/**
	 * Checks the whole index for the path and if it
	 * find one, then it returns true
	 *
	 * @param path The path to be found
	 * @return true if the path exists in the inverted index
	 */
	public boolean hasFile(Path path) {
		for (String word : iIndex.keySet()) {
			if (hasFile(word, path)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a word to the new index and creates the inner
	 * data structures (TreeMap<String, TreeSet<Integer>>)
	 *
	 * @param word The word to be put into the index
	 * @param filePath The file to put into the index for this word
	 * @param position The position of the word to add
	 */
	public void addWord(String word) {
		iIndex.put(word, new TreeMap<>());
	}

	/**
	 * Adds a file to the index under the given word
	 *
	 * @param word The word to put the filePath under
	 * @param filePath The path to be put in the index
	 */
	public void addFile(String word, Path filePath) {
		iIndex.get(word).put(filePath.toString(), new TreeSet<>());
	}

	/**
	 * Adds the position of the word to the index
	 *
	 * @param word The word found from stemming
	 * @param filePath The file to put the position in
	 * @param position The position number to put into the set
	 */
	public void addPosition(String word, Path filePath, int position) {
		iIndex.get(word).get(filePath.toString()).add(position);
	}
}
