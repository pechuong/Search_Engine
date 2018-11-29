import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Query {

	/**
	 * Writes the Query Map to a JSON file given a JSON output path.
	 *
	 * @param path The path to write the map to
	 * @throws IOException
	 */
	public void writeJSON(Path path) throws IOException;

	/**
	 * Adds a Query (one search) into the map w/ it's result(s).
	 *
	 * @param search The search query made to store in the map
	 * @param results The list of results to store into the map
	 */
	public void addQuery(String search, List<Result> results);

	/**
	 * Checks the query map for the given query
	 *
	 * @param query The query to search for in the map
	 * @return true if query is in the map
	 */
	public boolean hasQuery(String query);

	/**
	 * Checks to see if the Query Map is empty or not.
	 * This means that no search has been made or stored.
	 *
	 * @return true if the Query Map has at least 1 entry.
	 */
	public boolean isEmpty();

	/**
	 * Stems the query file, performs a search in the index, and then
	 * stores the results of the search
	 *
	 * @param queryFile The file with all the queries to perform a search on
	 * @param exact Determines if an exact search should be performed or not
	 * @throws IOException If something goes wrong when trying to read query file
	 */
	public void stemQuery(Path queryFile, boolean exact) throws IOException;

	@Override
	public String toString();

}
