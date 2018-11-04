import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {

	//public static final Logger log = LogManager.getLogger(InvertedIndexBuilder.class);

	public static class DirectoryWork implements Runnable {

		private InvertedIndex index;
		private Path path;
		private WorkQueue queue;


		public DirectoryWork(InvertedIndex index, WorkQueue queue, Path path) {
			this.index = index;
			this.queue = queue;
			this.path = path;
		}

		@Override
		public void run() {
			try {
				if (Files.isDirectory(path)) {
					try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
						for (Path file : listing) {
							queue.execute(new DirectoryWork(index, queue, file));;
						}
					}
				} else if (path.toString().matches("(?i).*\\.te?xt$")) {
					ThreadSafeInvertedIndexBuilder.stemFile(index, path);
				}
			} catch (IOException e) {
				//log.debug("Something went wrong with path: " + this.path);
			}
		}

	}

	public static void traverse(InvertedIndex index, Path path, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		queue.execute(new DirectoryWork(index, queue, path));
		queue.finish();
		queue.shutdown();
	}

	public synchronized static void stemFile(InvertedIndex index, Path inputFile) throws IOException {
		InvertedIndexBuilder.stemFile(index, inputFile);
	}

}
