
public class Result implements Comparable<Result> {

	private final String where;
	private double matches;
	private final int wordCount;
	private double score;

	public Result(String file, int count, int wordCount) {
		this.where = file;
		this.matches = count;
		this.wordCount = wordCount;
		this.score = this.matches / this.wordCount;
	}

	public void addMatches(int count) {
		this.matches += count;
		calculateScore();
	}

	public String getFileName() {
		return this.where;
	}

	public int getMatches() {
		return (int)this.matches;
	}

	public double getScore() {
		return this.score;
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
				return this.where.compareTo(o.where);
			}
			return -1;
		}
		return -1;

	}

}
