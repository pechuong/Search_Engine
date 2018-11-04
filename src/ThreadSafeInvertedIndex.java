
public class ThreadSafeInvertedIndex extends InvertedIndex {

	public ThreadSafeInvertedIndex() {
		super();
	}

	@Override
	public void build(String word, String file, int count) {
		add(word, file, count);
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
	public synchronized String toString() {
		return super.toString();
	}
}
