
public class Result implements Comparable<Result> {

	private final String where;
	private int matches;
	private final int wordCount;
	private double score;

	/**
	 * Constructor for Result
	 *
	 * @param file The filename in a relative path form
	 * @param count The number of matches found so far in this file
	 * @param wordCount The number of words in this file
	 */
	public Result(String file, int count, int wordCount) {
		this.where = file;
		this.matches = count;
		this.wordCount = wordCount;
		this.score = (double)this.matches / this.wordCount;
	}

	/**
	 * Adds more matches to the total found matches
	 *
	 * @param count The amount of matches found
	 */
	public synchronized void addMatches(int count) {
		if (count > 0) {
			this.matches += count;
			calculateScore();
		}
	}

	/**
	 * Recalculates the score because matches changed
	 */
	public synchronized void calculateScore() {
		this.score = (double)this.matches / this.wordCount;
	}

	/**
	 * Gets the name of the filename / the relative path
	 *
	 * @return String - the filename / path
	 */
	public String getFileName() {
		return this.where;
	}

	/**
	 * Gets the number of Matches found for the file
	 *
	 * @return int - Num of matches
	 */
	public int getMatches() {
		return this.matches;
	}

	/**
	 * Gets the number of Words found in this file
	 *
	 * @return int - the total num of words in this file
	 */
	public int getWordCount() {
		return this.wordCount;
	}

	/**
	 * Gets the score of this file from the search
	 *
	 * @return double - the score from this file
	 */
	public double getScore() {
		return this.score;
	}

	@Override
	public int compareTo(Result o) {
		int resultScore = Double.compare(o.getScore(), getScore());
		if (resultScore == 0) {
			resultScore = Integer.compare(o.getMatches(), getMatches());
			if (resultScore == 0) {
				return getFileName().compareTo(o.getFileName());
			}
		}
		return resultScore;
	}

	@Override
	public String toString() {
		StringBuffer myString = new StringBuffer();
		return myString.append("Where: " + this.where + System.lineSeparator() +
				"Matches: " + this.matches + System.lineSeparator() +
				"Score: " + Double.toString(this.score)).toString();
	}

}
