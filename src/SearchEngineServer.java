import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class SearchEngineServer {

	private static ThreadSafeInvertedIndex index;
	private final Server server;

	public SearchEngineServer(ThreadSafeInvertedIndex index, int port) throws Exception {
		this.index = index;
		this.server = new Server();

		ServerConnector connector = new ServerConnector(this.server);
		connector.setHost("localhost");
		connector.setPort(port);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(HomeServlet.class, "/");
		handler.addServletWithMapping(SearchResultServlet.class, "/results");

		this.server.addConnector(connector);
		this.server.setHandler(handler);
		this.server.start(); // starting threads that stops when you hit stop
		this.server.join();
	}

	@SuppressWarnings("serial")
	public static class HomeServlet extends HttpServlet {

		private static final String TITLE = "Home";

		@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {

			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");
			out.printf("<p>It is %s my dudes.</p>%n", dayOfWeek());

			out.printf("<h2>Welcome to my Search Engine</h2>%n");

			// search query to enter (form)
			out.printf("<form method=\"POST\" action=\"/results\">%n");
			out.printf("<input type=\"text\" name=\"query\" placeholder=\"Enter a query to search\">%n");
			out.printf("<input type=\"submit\">%n");
			out.printf("</form>%n");

			response.setStatus(HttpServletResponse.SC_OK);
		}
	}

	public static String dayOfWeek() {
		return Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);
	}

	@SuppressWarnings("serial")
	public static class SearchResultServlet extends HttpServlet {

		private static final String TITLE = "Results";

		@Override
		protected void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();

			out.printf("<html>%n");
			out.printf("<head><title>%s</title></head>%n", TITLE);
			out.printf("<body>%n");
			out.printf("<pre>%n");
			index.writeIndex(out);
			out.printf("</pre>%n");
			out.printf("</body>%n");

			response.setStatus(HttpServletResponse.SC_OK);
		}
	}

}
