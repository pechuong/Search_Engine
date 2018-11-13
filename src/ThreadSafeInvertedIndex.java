import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	private final ReadWriteLock lock;

	/**
	 * Initializes Thread Safe version of Inverted Index with a lock
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

	@Override
	public void build(String word, String file, int count) {
		add(word, file, count);
	}

	@Override
	public List<Result> exactSearch(Set<String> queryLine) {
		return super.exactSearch(queryLine);
	}

	@Override
	public List<Result> partialSearch(Set<String> queryLine) {
		return super.partialSearch(queryLine);
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
	public boolean hasPosition(String word, String path, int position) {
		lock.lockReadOnly();
		try {
			return super.hasPosition(word, path, position);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public synchronized void add(String word, String location, int position) {
		super.add(word, location, position);
	}

	@Override
	public synchronized void addAll(InvertedIndex other) {
		super.addAll(other);
	}

	@Override
	public TreeMap<String, TreeMap<String, TreeSet<Integer>>> getIndex() {
		lock.lockReadOnly();
		try {
			return super.getIndex();
		} finally {
			lock.unlockReadOnly();
		}
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
