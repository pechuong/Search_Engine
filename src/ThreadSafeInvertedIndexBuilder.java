import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {

	/**
	 * Handles stemming each file to the index
	 */
	public static class FileWork implements Runnable {

		private final ThreadSafeInvertedIndex index;
		private final Path path;

		/**
		 * Initializes new directory work (traverse and stem)
		 *
		 * @param index The inverted index to build to
		 * @param queue The queue to add work to
		 * @param path The path to traverse or stem
		 */
		public FileWork(ThreadSafeInvertedIndex index, Path path) {
			this.index = index;
			this.path = path;
		}

		@Override
		public void run() {
			try {
				stemFile(index, path);
			} catch (IOException e) {
				System.out.println("Error occured with path: " + path.toString());
			}
		}

	}

	/**
	 * Start traversing a path and creates new File work when a file is found
	 *
	 * @param index The index to build to
	 * @param queue The workqueue to add file work to
	 * @param path The path to traverse
	 * @throws IOException
	 */
	private static void traverse(ThreadSafeInvertedIndex index, WorkQueue queue, Path path) throws IOException {
		try {
			if (Files.isDirectory(path)) {
				try (DirectoryStream<Path> listing = Files.newDirectoryStream(path)) {
					for (Path file : listing) {
						traverse(index, queue, file);
					}
				}
			} else if (path.toString().matches("(?i).*\\.te?xt$")) {
				queue.execute(new FileWork(index, path));;
			}
		} catch (IOException e) {
			System.out.println("Something went wrong with reading");
		}
	}

	/**
	 * Starts the traversing and makes a workqueue
	 *
	 * @param index The big index to build to
	 * @param path The path to start traversing from
	 * @param threads The number of threads to use in our workqueue
	 */
	public static void traverse(ThreadSafeInvertedIndex index, Path path, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		try {
			traverse(index, queue, path);
		} finally {
			queue.finish();
			queue.shutdown();
		}
	}

	/**
	 * Builds a file to a local index which is added to the overall index
	 *
	 * @param index The overall big index to add to
	 * @param inputFile The file to stem and build the index from
	 * @throws IOException
	 */
	public static void stemFile(ThreadSafeInvertedIndex index, Path inputFile) throws IOException {
		InvertedIndex local = new InvertedIndex();
		InvertedIndexBuilder.stemFile(local, inputFile);
		index.addAll(local);
	}

}
