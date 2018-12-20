import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;

public class SearchEngineServer {

	private static final int PORT = 8080;

	public static void main(String[] args) throws Exception {

		Server server = new Server();

		ServerConnector connector = new ServerConnector(server);
		connector.setHost("localhost");
		connector.setPort(PORT);

		ServletHandler handler = new ServletHandler();
		handler.addServletWithMapping(HomeServlet.class, "/");

		server.addConnector(connector);
		server.setHandler(handler);


		server.start();
		server.join();
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

			out.printf("<p>It is Wednesday my dudes.</p>%n");

			out.printf("</body>%n");
			out.printf("</html>%n");

			response.setStatus(HttpServletResponse.SC_OK);
		}

	}

}
