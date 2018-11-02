import java.util.ArrayList;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;

public class TextFileStemmer {

	/**
	 * Returns a list of cleaned and stemmed words parsed from the provided line.
	 *
	 * @param line the line of words to clean, split, and stem
	 * @param stemmer the stemmer to use
	 * @return list of cleaned and stemmed words
	 *
	 * @see Stemmer#stem(CharSequence)
	 * @see TextParser#parse(String)
	 */
	public static List<String> stemLine(String line, Stemmer stemmer) {
		ArrayList<String> wordList = new ArrayList<>();
		for (String word : TextParser.parse(line)) {
			wordList.add(stemmer.stem(word).toString());
		}
		return wordList;
	}

}
