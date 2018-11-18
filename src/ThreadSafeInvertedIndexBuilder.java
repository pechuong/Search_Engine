import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/*
 * TODO This multithreading follows the lecture example, because traversing
 * directories was where the "work" was located. But, this project specifically
 * asked you to create a task PER FILE. You need to change the task!
 */

public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {

	/**
	 * Handles traversing the path and stemming each file to the index
	 */
	public static class DirectoryWork implements Runnable {

		private final InvertedIndex index;
		private final Path path;
		private final WorkQueue queue;

		/**
		 * Initializes new directory work (traverse and stem)
		 *
		 * @param index The inverted index to build to
		 * @param queue The queue to add work to
		 * @param path The path to traverse or stem
		 */
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
					// TODO This is what each task should do, not the directory traversal.
					ThreadSafeInvertedIndexBuilder.stemFile(index, path);
				}
			} catch (IOException e) {
				System.out.println("Something went wrong with reading");
			}
		}

	}

	/**
	 * Start the traversing and multithreads it
	 *
	 * @param index The index to build to
	 * @param path The path to traverse
	 * @param threads The number of threads to use
	 * @throws IOException
	 */
	public static void traverse(InvertedIndex index, Path path, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		queue.execute(new DirectoryWork(index, queue, path));
		queue.finish();
		queue.shutdown();
	}

	// TODO Do not reimplement stemFile... reuse InvertedIndexBuilder where possible!
	/**
	 * Builds a file to a local index which is added to the overall index
	 *
	 * @param index The overall big index to add to
	 * @param inputFile The file to stem and build the index from
	 * @throws IOException
	 */
	public static void stemFile(InvertedIndex index, Path inputFile) throws IOException {
		try (
				var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				) {
			String line;
			int count = 0;
			String filePath = inputFile.toString();
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
			InvertedIndex local = new InvertedIndex();

			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					count++;
					local.build(stemmer.stem(word).toString(), filePath, count);
				}
			}

			index.addAll(local);
		}
	}

}
