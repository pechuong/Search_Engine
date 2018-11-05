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
	public boolean isEmpty() {
		lock.lockReadOnly();
		try {
			return super.isEmpty();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public boolean hasWord(String word) {
		lock.lockReadOnly();
		try {
			return super.hasWord(word);
		} finally {
			lock.unlockReadOnly();
		}

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
		lock.lockReadOnly();
		try {
			return super.hasPosition(word, path, position);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public void add(String word, String location, int position) {
		super.add(word, location, position);
	}

	@Override
	public void updateLocation(String path) {
		lock.lockReadWrite();
		try {
			super.updateLocation(path);
		} finally {
			lock.unlockReadWrite();
		}
	}

	@Override
	public InvertedIndex getInvertedIndex() {
		lock.lockReadOnly();
		try {
			return super.getInvertedIndex();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public synchronized TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		return super.getIndex();
	}

	@Override
	public TreeMap<String, TreeSet<Integer>> getFiles(String word) {
		lock.lockReadOnly();
		try {
			return super.getFiles(word);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public int getWordCount(String word, String filePath) {
		lock.lockReadOnly();
		try {
			return super.getWordCount(word, filePath);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public TreeMap<String, Integer> getLocation() {
		lock.lockReadOnly();
		try {
			return super.getLocation();
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public int getLocationCount(String location) {
		lock.lockReadOnly();
		try {
			return super.getLocationCount(location);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public synchronized String toString() {
		return super.toString();
	}
}
