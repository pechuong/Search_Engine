
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
		this.score = 0;
		calculateScore();
	}

	/**
	 * Adds more matches to the total found matches
	 *
	 * @param count The amount of matches found
	 */
	public void addMatches(int count) {
		if (count > 0) {
			this.matches += count;
			calculateScore();
		}
	}

	/**
	 * Recalculates the score because matches changed
	 */
	public void calculateScore() {
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

	/**
	 * compareTo function that sorts the Result objects by
	 * 1.) Score
	 * 2.) Raw Matches
	 * 3.) Location (alphabetically)
	 */
	@Override
	public int compareTo(Result o) {
		/* TODO
		int result = Double.compare(this.score, o.score);
		
		if (result == 0) {
			result = Integer.compare(x, y);
			
			check once more and set to location compare it needed
					
		}
		
		return result;
		*/
		
		if (this.score < o.score) {
			return 1;
		} else if (this.score == o.score) {
			if (this.matches < o.matches) {
				return 1;
			} else if (this.matches == o.matches) {
				return this.where.compareTo(o.where);
			}
			return -1;
		}
		return -1;
	}

	@Override
	public String toString() {
		StringBuffer myString = new StringBuffer();
		return myString.append("Where: " + this.where + System.lineSeparator() +
				"Matches: " + this.matches + System.lineSeparator() +
				"Score: " + Double.toString(this.score)).toString();
	}

}
