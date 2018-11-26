import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Query {

	public void writeJSON(Path path) throws IOException;
	public void addQuery(String search, List<Result> results);
	public boolean hasQuery(String query);
	public boolean isEmpty();
	public String toString();

}
