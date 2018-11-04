import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ThreadSafeQueryMap extends QueryMap {

	public ThreadSafeQueryMap(InvertedIndex index) {
		super(index);
	}

	@Override
	public synchronized void stemQuery(Path queryFile, boolean exact) throws IOException {
		super.stemQuery(queryFile, exact);
	}

	@Override
	public synchronized void addQuery(String search, List<Result> results) {
		super.addQuery(search, results);
	}

	@Override
	public synchronized boolean isEmpty() {
		return super.isEmpty();
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
