import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class WebCrawler {

	private final InvertedIndex index;
	private final ReadWriteLock lock;
	private final HashMap<String, URL> links;
	private final int limit;

	public WebCrawler(InvertedIndex index, String Url, int limit, int threads) {
		this.index = index;
		this.links = new HashMap<>();
		this.limit = limit;
		this.lock = new ReadWriteLock();
		System.out.println(Url);
		crawl(Url, threads);
	}

	public static class LinkWork implements Runnable {

		private final WebCrawler webCrawl;
		private final WorkQueue queue;
		private final URL url;

		public LinkWork(WebCrawler webCrawl, WorkQueue queue, URL url) {
			this.webCrawl = webCrawl;
			this.queue = queue;
			this.url = url;
		}

		@Override
		public void run() {
			if (webCrawl.hasSpace()) {
				try {
					String html = HTMLFetcher.fetchHTML(url, 3);
					if (html != null) {
						webCrawl.addLink(url);
						for (URL link : LinkParser.listLinks(url, html)) {
							queue.execute(new LinkWork(webCrawl, queue, link));
						}
						webCrawl.stemHTML(HTMLCleaner.stripHTML(html), url);
					}
				} catch (IOException e) {
					System.out.println("Something went wrong with: " + url + System.lineSeparator() + e);
				}
			}
		}

	}

	public void crawl(URL url) throws MalformedURLException, IOException {

	}

	public void crawl(String url, int threads) {
		WorkQueue queue = new WorkQueue(threads);
		try {
			queue.execute(new LinkWork(this, queue, LinkParser.clean(new URL(url))));
		} catch (IOException e) {
			System.out.println("Something went wrong with url: " + url);
		}
		queue.finish();
		queue.shutdown();
	}

	private void addLink(URL url) {
		lock.lockReadWrite();
		links.put(url.toString(), url);
		lock.unlockReadWrite();
	}

	public boolean hasLink(URL url) {
		lock.lockReadOnly();
		try {
			return links.containsKey(url.toString());
		} finally {
			lock.unlockReadOnly();
		}
	}

	public boolean hasSpace() {
		lock.lockReadOnly();
		try {
			return links.size() < limit;
		} finally {
			lock.unlockReadOnly();
		}
	}

	public void stemHTML(String html, URL url) {
		InvertedIndex local = new ThreadSafeInvertedIndex();
		String filePath = url.toString();
		Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		int count = 0;

		for (String word : TextParser.parse(html)) {
			count++;
			local.build(stemmer.stem(word).toString(), filePath, count);
		}

		index.addAll(local);
	}

}
