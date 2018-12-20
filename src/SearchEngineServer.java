import javax.servlet.http.HttpServlet;

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

	public static class HomeServlet extends HttpServlet {

	}

}
