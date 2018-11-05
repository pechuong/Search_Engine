import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	private ReadWriteLock lock;

	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

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
			synchronized (lookUp) {
				if (lookUp.containsKey(path)) {
					try {
						lookUp.get(path).addMatches(index.getWordCount(word, path));
					} catch (Exception e) {
						System.out.println(e);
					}
				} else {
					Result result = new Result(path, index.getWordCount(word, path), index.getLocationCount(path));
					lookUp.put(path, result);
					results.add(result);
				}
			}
		}

	}

	@Override
	public void build(String word, String file, int count) {
		add(word, file, count);
	}

	public List<Result> exactSearch(Set<String> queryLine, int threads) {
		HashMap<String, Result> lookUp = new HashMap<>();
		ArrayList<Result> results = new ArrayList<>();
		WorkQueue queue = new WorkQueue(threads);

		for (String query : queryLine) {
			if (hasWord(query)) {
				for (String path : getFiles(query).keySet()) {
					queue.execute(new ResultWork(this, lookUp, results, query, path));
				}
			}
		}
		queue.finish();
		queue.shutdown();

		Collections.sort(results);
		return results;
	}

	public List<Result> partialSearch(Set<String> queryLine, int threads) {
		HashMap<String, Result> lookUp = new HashMap<>();
		ArrayList<Result> results = new ArrayList<>();
		WorkQueue queue = new WorkQueue(threads);

		for (String query : queryLine) {
			for (String word : getIndex().keySet()) {
				if (word.startsWith(query) || word.equalsIgnoreCase(query)) {
					for (String path : getFiles(word).keySet()) {
						queue.execute(new ResultWork(this, lookUp, results, word, path));
					}
				}
			}
		}
		queue.finish();
		queue.shutdown();

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
	public boolean hasFile(String word, String path) {
		lock.lockReadOnly();
		try {
			return super.hasFile(word, path);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public synchronized boolean hasPosition(String word, String path, int position) {
		return super.hasPosition(word, path, position);
	}

	@Override
	public void add(String word, String location, int position) {
		lock.lockReadWrite();
		super.add(word, location, position);
		lock.unlockReadWrite();
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
	public synchronized InvertedIndex getInvertedIndex() {
		return super.getInvertedIndex();
	}

	@Override
	public synchronized TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		return super.getIndex();
	}

	@Override
	public synchronized TreeMap<String, Integer> getLocation() {
		return super.getLocation();
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}
}
