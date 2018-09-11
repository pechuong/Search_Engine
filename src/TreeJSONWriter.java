import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class TreeJSONWriter {

	/**
	 * Writes several tab <code>\t</code> symbols using the provided
	 * {@link Writer}.
	 *
	 * @param times  the number of times to write the tab symbol
	 * @param writer the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void indent(int times, Writer writer) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Writes the element surrounded by quotes using the provided {@link Writer}.
	 *
	 * @param element the element to quote
	 * @param writer  the writer to use
	 * @throws IOException if the writer encounters any issues
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Returns the set of elements formatted as a pretty JSON array of numbers.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static String asArray(TreeSet<Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 */
	public static void asArray(TreeSet<Integer> elements, Path path)
			throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the set of elements formatted as a pretty JSON array of numbers
	 * using the provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 */
	public static void asArray(TreeSet<Integer> elements, Writer writer,
			int level) throws IOException {
		// TODO We fill this in during class!
		// TODO Make sure to use Integer.toString() to avoid weird bugs!
		
		//System.out.println(elements.isEmpty());
		if (elements.isEmpty()) {
			writer.write("[" + System.lineSeparator());
			indent(level, writer);
			writer.write("");
			writer.write("]");
			return;
		}
		writer.write("[" + System.lineSeparator());
		/*//System.out.println(elements);
		for (int num : elements.headSet(elements.last())) {
			indent(level + 1, writer);
			writer.write(Integer.toString(num));
			writer.write("," + System.lineSeparator());
		}
		//writes last element
		indent(level + 1, writer);
		writer.write(Integer.toString(elements.last()));
		//writes the last bracket
		writer.write(System.lineSeparator());
		indent(level, writer);
		writer.write("]");
		*/
		for (int num : elements) {
			indent(level + 1, writer);
			writer.write(Integer.toString(num));
			if (elements.last() != num) {
				writer.write(",");
			}
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write("]");
	}

	/**
	 * Returns the map of elements formatted as a pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static String asObject(TreeMap<String, Integer> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the map of elements formatted as a pretty JSON object to
	 * the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asObject(TreeMap, Writer, int)
	 */
	public static void asObject(TreeMap<String, Integer> elements, Path path)
			throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the map of elements as a pretty JSON object using the provided
	 * {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 */
	public static void asObject(TreeMap<String, HashMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {
		// TODO Fill in!
		indent(level, writer);
		writer.write("{" + System.lineSeparator());
		for (String word: elements.keySet()) {
			indent(level + 1, writer);
			quote(word, writer);
			writer.write(": ");
			asNestedObject(elements.get(word), writer, level);
			if (word != elements.lastKey()) {
				writer.write("," + System.lineSeparator());
			}
		}
		indent(level, writer);
		writer.write("}");
	}

	/**
	 * Returns the nested map of elements formatted as a nested pretty JSON object.
	 *
	 * @param elements the elements to convert to JSON
	 * @return {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static String asNestedObject(HashMap<String, TreeSet<Integer>> elements) {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Writes the nested map of elements formatted as a nested pretty JSON object
	 * to the specified file.
	 *
	 * @param elements the elements to convert to JSON
	 * @param path     the path to the file write to output
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see #asNestedObject(TreeMap, Writer, int)
	 */
	public static void asNestedObject(HashMap<String, TreeSet<Integer>> elements,
			Path path) throws IOException {
		// THIS METHOD IS PROVIDED FOR YOU. DO NOT MODIFY.
		try (BufferedWriter writer = Files.newBufferedWriter(path,
				StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the nested map of elements as a nested pretty JSON object using the
	 * provided {@link Writer} and indentation level.
	 *
	 * @param elements the elements to convert to JSON
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException if the writer encounters any issues
	 *
	 * @see Writer#write(String)
	 * @see Writer#append(CharSequence)
	 *
	 * @see System#lineSeparator()
	 *
	 * @see #indent(int, Writer)
	 * @see #quote(String, Writer)
	 *
	 * @see #asArray(TreeSet, Writer, int)
	 */
	public static void asNestedObject(HashMap<String, TreeSet<Integer>> elements,
			Writer writer, int level) throws IOException {
		// TODO Fill this in!
		// TODO Reuse the asArray(...) method here!
		indent(level, writer);
		writer.write("{" + System.lineSeparator());
		for (String key : elements.keySet()) {
			indent(level + 1, writer);
			quote(key, writer);
			writer.write(": ");
			asArray(elements.get(key), writer, level + 1);
			if (elements.lastKey() != key) {
				writer.write(",");
			}
			writer.write(System.lineSeparator());
		}
		indent(level, writer);
		writer.write("}");
	}

	public static void main(String[] args) {
		// You can test your code here while developing!
		
		/*
		TreeSet<Integer> test = new TreeSet<>();
		test.add(3);
		test.add(11);
		test.add(-2);
		System.out.println(asArray(test));
 		*/
		
		/*
		TreeMap<String, Integer> test1 = new TreeMap<>();
		test1.put("hello", -1);
		test1.put("world", 2);
		test1.put("!!!", 10);
		System.out.println(asObject(test1));
		*/
		
		/*
		TreeMap<String, TreeSet<Integer>> test2 = new TreeMap<>();
		test2.put("hello", new TreeSet<>());
		Collections.addAll(test2.get("hello"), 10, 2, 4, -1);
		//System.out.println(test2);
		test2.put("world", new TreeSet<>());
		Collections.addAll(test2.get("world"), -5, 3, 6, 7);
		test2.put("!!!", new TreeSet<>());
		Collections.addAll(test2.get("!!!"), -8, 9);
		System.out.println(asNestedObject(test2));
		*/
	}
}
