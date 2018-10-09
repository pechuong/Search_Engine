
public class Result implements Comparable<Result> {

	private final String where;
	private int matches;
	private final int wordCount;
	private int score;

	public Result(String file, int count, int wordCount) {
		this.where = file;
		this.matches = count;
		this.wordCount = wordCount;
		this.score = this.matches / this.wordCount; // TODO convert to 6 decimal floating point
	}

	public void addMatches(int count) {
		this.matches += count;
		calculateScore();
	}

	public String getFileName() {
		return this.where;
	}

	public void calculateScore() {
		this.score = this.matches / this.wordCount;
	}

	@Override
	public int compareTo(Result o) {
		if (this.score < o.score) {
			return 1;
		} else if (this.score == o.score) {
			if (this.matches < o.matches) {
				return 1;
			} else if (this.matches == o.matches) {
				if (this.wordCount < o.wordCount) {
					return 1;
				} else if (this.wordCount == o.wordCount) {
					return 0;
				}
				return -1;
			}
			return -1;
		}
		return -1;

	}

}
