import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

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
			if (!webCrawl.isFull()) {
				try {
					String html = HTMLFetcher.fetchHTML(url, 3);
					if (html != null) {
						webCrawl.addLink(url);
						for (URL link : LinkParser.listLinks(url, html)) {
							queue.execute(new LinkWork(webCrawl, queue, link));
						}
						HTMLCleaner.stripHTML(html);
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
			crawl(LinkParser.clean(new URL(url)));
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

	public boolean isFull() {
		lock.lockReadOnly();
		try {
			return links.size() < limit;
		} finally {
			lock.unlockReadOnly();
		}
	}

	public void stemHTML(String html, URL url) {

	}

}
