import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTMLFetcher {

	private static final Pattern HTML_TEST = Pattern.compile("(?is).*?/??html;.*?");
	private static final Pattern STATUS_CODE = Pattern.compile("(?is)^HTTPS??/\\d.\\d (.*?) \\w+?$");

	/**
	 * Given a map of headers (as returned either by {@link URLConnection#getHeaderFields()}
	 * or by {@link HttpsFetcher#fetchURL(URL)}, determines if the content type of the
	 * response is HTML.
	 *
	 * @param headers map of HTTP headers
	 * @return true if the content type is html
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static boolean isHTML(Map<String, List<String>> headers) {
		if (headers.get("Content-Type") == null) {
			return false;
		}
		Matcher matcher = HTML_TEST.matcher(headers.get("Content-Type").get(0));
		return matcher.find();
		//return headers.get("Content-Type") != null ? headers.get("Content-Type").get(0).split(" ")[0].matches("(?i).*?html;$") : false;
	}

	/**
	 * Given a map of headers (as returned either by {@link URLConnection#getHeaderFields()}
	 * or by {@link HttpsFetcher#fetchURL(URL)}, returns the status code as an int value.
	 * Returns -1 if any issues encountered.
	 *
	 * @param headers map of HTTP headers
	 * @return status code or -1 if unable to determine
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static int getStatusCode(Map<String, List<String>> headers) {
		if (headers.get(null) == null) {
			return -1;
		}
		Matcher matcher = STATUS_CODE.matcher(headers.get(null).get(0));
		return matcher.find() ? Integer.parseInt(matcher.group(1)) : -1;
		//return headers.get(null) != null ? Integer.parseInt(headers.get(null).get(0).split(" ")[1]) : -1;
	}

	/**
	 * Given a map of headers (as returned either by {@link URLConnection#getHeaderFields()}
	 * or by {@link HttpsFetcher#fetchURL(URL)}, returns whether the status code
	 * represents a redirect response *and* the location header is properly included.
	 *
	 * @param headers map of HTTP headers
	 * @return true if the HTTP status code is a redirect and the location header is non-empty
	 *
	 * @see URLConnection#getHeaderFields()
	 * @see HttpsFetcher#fetchURL(URL)
	 */
	public static boolean isRedirect(Map<String, List<String>> headers) {
		int statusCode = getStatusCode(headers);
		return (statusCode >= 300 && statusCode < 400) ? true : false;
	}

	/**
	 * Uses {@link HttpsFetcher#fetchURL(URL)} to fetch the headers and content of the
	 * specified url. If the response was HTML, returns the HTML as a single {@link String}.
	 * If the response was a redirect and the value of redirects is greater than 0, will
	 * return the result of the redirect (decrementing the number of allowed redirects).
	 * Otherwise, will return {@code null}.
	 *
	 * @param url the url to fetch and return as html
	 * @param redirects the number of times to follow a redirect response
	 * @return the html as a single String if the response code was ok, otherwise null
	 * @throws IOException
	 *
	 * @see #isHTML(Map)
	 * @see #getStatusCode(Map)
	 * @see #isRedirect(Map)
	 */
	public static String fetchHTML(URL url, int redirects) throws IOException {
		Map<String, List<String>> headers = HttpsFetcher.fetchURL(url);
		int statusCode = getStatusCode(headers);
		if (isRedirect(headers)) {
			return redirects > 0 ? fetchHTML(new URL(headers.get("Location").get(0)), redirects - 1) : null;
		}
		if (statusCode >= 200 && statusCode < 300) {
			if (isHTML(headers)) {
				List<String> content = headers.get("Content");
				StringBuilder allHTML = new StringBuilder();
				String last = content.remove(content.size() - 1);
				for (String line : content) {
					allHTML.append(line + System.lineSeparator());
				}
				allHTML.append(last);
				return allHTML.toString();
			}
		}
		return null;
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url) throws IOException {
		return fetchHTML(new URL(url), 0);
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(String url, int redirects) throws IOException {
		return fetchHTML(new URL(url), redirects);
	}

	/**
	 * @see #fetchHTML(URL, int)
	 */
	public static String fetchHTML(URL url) throws IOException {
		return fetchHTML(url, 0);
	}

}
