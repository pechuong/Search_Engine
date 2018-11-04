import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	// TODO add thread work for exact and partial search
	public static class ResultWork implements Runnable {

		private final InvertedIndex index;
		private final HashMap<String, Result> lookUp;
		private final ArrayList<Result> results;
		private final String word;
		private final String path;

		public ResultWork(InvertedIndex index, HashMap<String, Result> lookUp, ArrayList<Result> results, String word, String path) {
			this.index = index;
			this.lookUp = lookUp;
			this.results = results;
			this.word = word;
			this.path = path;
		}

		@Override
		public void run() {
			if (lookUp.containsKey(path)) {
				lookUp.get(path).addMatches(index.getWordCount(word, path));
			} else {
				Result result = new Result(path, index.getWordCount(word, path), index.getLocationCount(path));
				lookUp.put(path, result);
				results.add(result);
			}
		}

	}

	public ThreadSafeInvertedIndex() {
		super();
	}

	@Override
	public void build(String word, String file, int count) {
		add(word, file, count);
	}

	@Override
	public List<Result> exactSearch(Set<String> queryLine) {
		HashMap<String, Result> lookUp = new HashMap<>();
		ArrayList<Result> results = new ArrayList<>();
		WorkQueue queue = new WorkQueue();

		for (String query : queryLine) {
			if (hasWord(query)) {
				for (String path : getFile(query).keySet()) {
					queue.execute(new ResultWork(this, lookUp, results, query, path));
				}
			}
		}

		Collections.sort(results);
		return results;
	}

	@Override
	public synchronized boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public synchronized boolean hasWord(String word) {
		return super.hasWord(word);
	}

	@Override
	public synchronized boolean hasFile(String word, String path) {
		return super.hasFile(word, path);
	}

	@Override
	public synchronized boolean hasPosition(String word, String path, int position) {
		return super.hasPosition(word, path, position);
	}

	@Override
	public synchronized void add(String word, String location, int position) {
		super.add(word, location, position);
	}

	@Override
	public synchronized int getLocationCount(String location) {
		return super.getLocationCount(location);
	}

	@Override
	public synchronized int getWordCount(String word, String filePath) {
		return super.getWordCount(word, filePath);
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}
}
