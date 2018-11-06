import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class ThreadSafeInvertedIndexBuilder extends InvertedIndexBuilder {

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
					ThreadSafeInvertedIndexBuilder.stemFile(index, queue, path);
				}
			} catch (IOException e) {
				System.out.println("Something went wrong with reading");
			}
		}

	}

	public static void traverse(InvertedIndex index, Path path, int threads) throws IOException {
		WorkQueue queue = new WorkQueue(threads);
		queue.execute(new DirectoryWork(index, queue, path));
		queue.finish();
		queue.shutdown();
	}

	public static void stemFile(InvertedIndex index, WorkQueue queue, Path inputFile) throws IOException {
		try (
				var reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8);
				) {
			String line;
			int count = 0;
			String filePath = inputFile.toString();
			Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);

			while ((line = reader.readLine()) != null) {
				for (String word : TextParser.parse(line)) {
					count++;
					index.build(stemmer.stem(word).toString(), filePath, count);
				}
			}
		}
	}

}
