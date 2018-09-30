import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Driver {

	/**
	 * Parses the command-line arguments to build and use an in-memory search
	 * engine from files or the web.
	 *
	 * @param args the command-line arguments to parse
	 */
	public static void main(String[] args) {
		// parses args
		ArgumentMap ArgMap = new ArgumentMap(args);
		InvertedIndex iIndex = new InvertedIndex();

		// Traverses and makes inverted index
		try {
			if (ArgMap.hasValue("-path"))
				TraversePath.traverse(iIndex, ArgMap.getPath("-path"));
			else
				System.out.println("No path to traverse!");
		} catch (IOException e) {
			System.out.println("File: " + ArgMap.getString("-path") + " does not exist!");
		}

		// Outputs inverted index to Json
		if (ArgMap.hasFlag("-index")) {
			try {
				Path output = ArgMap.getPath("-index", Paths.get("index.json"));
				if (!Files.exists(output)) {
					Files.createFile(output);
				}
				TreeJSONWriter.asObject(iIndex, output);

			} catch (IOException e) {
				System.out.println("Error writing to: " + ArgMap.getString("-index"));
			}
		}
	}
}

