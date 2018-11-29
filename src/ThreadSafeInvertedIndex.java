import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class ThreadSafeInvertedIndex extends InvertedIndex {

	private final ReadWriteLock lock;

	/**
	 * Initializes Thread Safe version of Inverted Index with a lock
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}
	
	// TODO Always use try/finally

	@Override
	public void writeIndex(Path path) throws IOException {
		lock.lockReadOnly();
		super.writeIndex(path);
		lock.unlockReadOnly();
	}

	@Override
	public void writeLocation(Path output) throws IOException {
		lock.lockReadOnly();
		super.writeLocation(output);
		lock.unlockReadOnly();
	}

	// TODO Remove this method in both places
	@Override
	public void build(String word, String file, int count) {
		lock.lockReadWrite();
		super.build(word, file, count);
		lock.unlockReadWrite();
	}

	@Override
	public List<Result> exactSearch(Set<String> queryLine) {
		lock.lockReadOnly();
		try {
			return super.exactSearch(queryLine);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public List<Result> partialSearch(Set<String> queryLine) {
		lock.lockReadOnly();
		try {
			return super.partialSearch(queryLine);
		} finally {
			lock.unlockReadOnly();
		}
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
	public void add(String word, String location, int position) {
		lock.lockReadWrite();
		super.add(word, location, position);
		lock.unlockReadWrite();
	}

	@Override
	public void addAll(InvertedIndex other) {
		// TODO lock for read/write
		lock.lockReadOnly();
		super.addAll(other);
		lock.unlockReadOnly();
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
	public int getLocationCount(String location) {
		lock.lockReadOnly();
		try {
			return super.getLocationCount(location);
		} finally {
			lock.unlockReadOnly();
		}
	}

	@Override
	public String toString() {
		lock.lockReadOnly();
		try {
			return super.toString();
		} finally {
			lock.unlockReadOnly();
		}
	}
}
