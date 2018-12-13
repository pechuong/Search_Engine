import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

public class WebCrawler {

	private final ThreadSafeInvertedIndex index;
	private final HashMap<String, URL> links;
	private final int limit; // TODO Not final...

	public WebCrawler(ThreadSafeInvertedIndex index, String Url, int limit, int threads) {
		this.index = index;
		this.links = new HashMap<>();
		this.limit = limit;
		crawl(Url, threads);
	}

	public static class LinkWork implements Runnable { // TODO Make non-static

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
			if (!webCrawl.hasSpace() || webCrawl.hasLink(url)) {
				return;
			}
			webCrawl.addLink(url);

			try {
				String html = HTMLFetcher.fetchHTML(url, 3);
				if (html != null) {
					// TODO lock around the entire loop
					for (URL link : LinkParser.listLinks(url, html)) {
						// TODO Checking here if the link is unique and there is room (but then have to add to the set immediately)
						// TODO add the link to the set, and then create a worker
						queue.execute(new LinkWork(webCrawl, queue, link));
					}
					webCrawl.stemHTML(HTMLCleaner.stripHTML(html), url);
				}
			} catch (IOException e) {
				System.out.println("Something went wrong with: " + url + System.lineSeparator() + e);
			}

		}

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

	/**
	 * Adds a link to the list of visited links
	 *
	 * @param url The url to add to visited links list
	 */
	private void addLink(URL url) {
		synchronized (links) {
			links.put(url.toString(), url);
		}
	}

	/**
	 * Checks if the link already exists or was already visited
	 *
	 * @param url The url to check
	 * @return true if link was already visited otherwise false
	 */
	public boolean hasLink(URL url) {
		synchronized (links) {
			return links.containsKey(url.toString());
		}
	}

	/**
	 * Checks if the limit for the amount of links to visit has been reached.
	 *
	 * @return true if number of links visited has reached the limit otherwise false
	 */
	public boolean hasSpace() {
		synchronized (links) {
			return links.size() < limit;
		}
	}

	/**
	 * Builds the index from the cleaned up html
	 *
	 * @param html The cleaned up html
	 * @param url The link / location to associate with the html
	 */
	private void stemHTML(String html, URL url) {
		ThreadSafeInvertedIndex local = new ThreadSafeInvertedIndex();
		String filePath = url.toString();
		Stemmer stemmer = new SnowballStemmer(SnowballStemmer.ALGORITHM.ENGLISH);
		int count = 0;

		for (String word : TextParser.parse(html)) {
			count++;
			local.add(stemmer.stem(word).toString(), filePath, count);
		}

		index.addAll(local);
	}

}
