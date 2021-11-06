import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/**
 * Outputs several simple data structures in "pretty" JSON format where newlines
 * are used to separate elements and nested elements are indented using tabs.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 272 Software Development (University of San Francisco)
 * @version Fall 2021
 */
public class JsonWriter {
	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see writeEntry(String, Writer, int)
	 */
	public static void asArray(Collection<Object> elements, Writer writer,
			int level) throws IOException {
		writer.write("[");
		level++;

		var iterator = elements.iterator();
		if(iterator.hasNext()) {
			var elem = iterator.next();
			writeEntry(elem.toString(), writer, level);

			while(iterator.hasNext()) {
				elem = iterator.next();
				writer.write(",");
				writeEntry(elem.toString(), writer, level);
			}
		}
		nextLine(writer, --level);
		writer.write("]");
	}

	/**
	 * Outputs array element to writer with JSON level formatting
	 * @param elem the element to be written in
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * 
	 * @throws IOException if an IO error occurs
	 */
	public static void writeEntry(String elem, Writer writer, int level) throws IOException{
		nextLine(writer, level);
		writer.write(elem);
	}
	
	public static void nextLine(Writer writer, int level) throws IOException {
		writer.write("\n");
		writer.write("\t".repeat(level));
	}

	/**
	 * Writes the elements as a pretty JSON object.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see writerKeyValueEntry(Map.Entry, Writer, int)
	 */
	public static void asObject(Map<String, Object> elements, Writer writer,
			int level) throws IOException {
		writer.write("{");
		level++;

		var iterator = elements.entrySet().iterator();
		if(iterator.hasNext()) {
			var elem = iterator.next();
			writeKeyValueEntry(elem, writer, level);

			while(iterator.hasNext()) {
				elem = iterator.next();
				writer.write(",");
				writeKeyValueEntry(elem, writer, level);
			}
		}
		nextLine(writer, --level);
		writer.write("}");
	}
	
	/**
	 * Outputs object element to writer with JSON level formatting
	 * 
	 * @param elem the element to be written in
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see keyFormat(String, Writer)
	 */
	public static void writeKeyValueEntry(Map.Entry<String, Object> elem,
			Writer writer, int level) throws IOException{
		nextLine(writer, level);
		keyFormat(elem.getKey(), writer);
		writer.write(elem.getValue().toString());
	}

	/**
	 * Constructs JSON object key formatting by placing quotes ("") around key and colon (:) after
	 *
	 * @param writer the writer to use
	 * @param key the Map key for JSON key formatting
	 * @throws IOException if an IO error occurs
	 */
	private static void keyFormat(String key, Writer writer) throws IOException{
		quoteEnclose(key, writer);
		writer.write(": ");
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays. The generic
	 * notation used allows this method to be used for any type of map with any type
	 * of nested collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see writeKVArrayEntry(Map.Entry, Wrtier, int)
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Object>> elements,
			Writer writer, int level) throws IOException {
		writer.write("{");
		level++;

		var iterator = elements.entrySet().iterator();
		if(iterator.hasNext()) {
			var elem = iterator.next();
			writeKVArrayEntry(elem, writer, level);

			while(iterator.hasNext()) {
				elem = iterator.next();
				writer.write(",");
				writeKVArrayEntry(elem, writer, level);
			}
		}
		nextLine(writer, --level);
		writer.write("}");
	}
	
	
	/**
	 * Outputs nested array element to writer with JSON level formatting
	 * 
	 * @param elem the element to be written in
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see keyFormat(String, Writer)
	 * @see asArray(Collection, Writer, int)
	 */
	public static void writeKVArrayEntry(Map.Entry<String, ? extends Collection<Object>> elem,
			Writer writer, int level) throws IOException{
		nextLine(writer, level);
		keyFormat(elem.getKey(), writer);
		asArray(elem.getValue(), writer, level);
	}

	/**
	 * Writes the elements as a pretty JSON object with nested objects with nested arrays.
	 * The generic notation used allows this method to be used for any type of map with any
	 * nested map with any collection of integer objects.
	 *
	 * @param elements the elements to write
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see writerKVObjectEntry(Map.Entry, Writer, int)
	 */
	public static void asNestedObject(Map<String, ? extends Map<String, ? extends Collection<Object>>> elements,
			Writer writer, int level) throws IOException {
		writer.write("{");
		level++;

		var iterator = elements.entrySet().iterator();
		if(iterator.hasNext()) {
			var elem = iterator.next();
			writeKVObjectEntry(elem, writer, level);

			while(iterator.hasNext()) {
				elem = iterator.next();
				writer.write(",");
				writeKVObjectEntry(elem, writer, level);
			}
		}
		nextLine(writer, --level);
		writer.write("}");
	}
	
	/**
	 * Outputs nested object element to writer with JSON level formatting
	 * 
	 * @param elem the element to be written in
	 * @param writer the writer to use
	 * @param level the initial indent level
	 * @throws IOException if an IO error occurs
	 * 
	 * @see keyFormat(String, Writer)
	 * @see asNestedArray(Map, Writer, int)
	 */
	public static void writeKVObjectEntry(Map.Entry<String, ? extends Map<String, ? extends Collection<Object>>> elem,
			Writer writer, int level) throws IOException{
		nextLine(writer, level);
		keyFormat(elem.getKey(), writer);
		asNestedArray(elem.getValue(), writer, level);
	}
	
	public static void asResult(Map<String, ? extends Collection<QueryResult>> elements, Writer writer, int level) throws IOException {
		writer.write("{");
		level++;

		var iterator = elements.entrySet().iterator();
		if(iterator.hasNext()) {
			var elem = iterator.next();
			writeResultEntry(elem, writer, level);

			while(iterator.hasNext()) {
				elem = iterator.next();
				writer.write(",");
				writeResultEntry(elem, writer, level);
			}
		}
		nextLine(writer, --level);
		writer.write("}");
	}
	
	public static void writeResultEntry(Map.Entry<String, ? extends Collection<QueryResult>> elem, Writer writer, int level) throws IOException {
		nextLine(writer, level);
		keyFormat(elem.getKey(), writer);
		asQueryArray(elem.getValue(), writer, level);
	}
	
	public static void asQueryArray(Collection<QueryResult> elements, Writer writer, int level) throws IOException {
		writer.write("[");
		level++;

		var iterator = elements.iterator();
		if(iterator.hasNext()) {
			var elem = iterator.next();
			writeQueryEntry(elem, writer, level);

			while(iterator.hasNext()) {
				elem = iterator.next();
				writer.write(",");
				writeQueryEntry(elem, writer, level);
			}
		}
		nextLine(writer, --level);
		writer.write("]");
	}
	
	public static void writeQueryEntry(QueryResult elem, Writer writer, int level) throws IOException {
		nextLine(writer, level);
		writer.write("{");
		nextLine(writer, ++level);
		keyFormat("count", writer);
		writer.write(elem.getMatchCount().toString());
		writer.write(",");
		nextLine(writer, level);
		keyFormat("score", writer);
		writer.write(String.format("%.8f", elem.getScore()));
		writer.write(",");
		nextLine(writer, level);
		keyFormat("where", writer);
		quoteEnclose(elem.getLocation().toString(), writer);
		nextLine(writer, --level);
		writer.write("}");
		
	}
	
	private static void quoteEnclose(String elem, Writer writer) throws IOException {
		writer.write("\"");
		writer.write(elem);
		writer.write("\"");
	}


	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static void asArray(Collection<Object> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static void asObject(Map<String, Object> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested arrays to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedArray(Map<String, ? extends Collection<Object>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedArray(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a pretty JSON object with nested objects to file.
	 *
	 * @param elements the elements to write
	 * @param path the file path to use
	 * @throws IOException if an IO error occurs
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static void asNestedObject(Map<String, ? extends Map<String, ? extends Collection<Object>>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}
	
	public static void asResult(Map<String, ? extends Collection<QueryResult>> elements,
			Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asResult(elements, writer, 0);
		}
	}

	/**
	 * Returns the elements as a pretty JSON array.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asArray(Collection, Writer, int)
	 */
	public static String asArray(Collection<Object> elements) {
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
	 * Returns the elements as a pretty JSON object.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asObject(Map, Writer, int)
	 */
	public static String asObject(Map<String, Object> elements) {
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
	 * Returns the elements as a pretty JSON object with nested arrays.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedArray(Map, Writer, int)
	 */
	public static String asNestedArray(Map<String, ? extends Collection<Object>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedArray(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}

	/**
	 * Returns the elements as a pretty JSON object with nested objects.
	 *
	 * @param elements the elements to use
	 * @return a {@link String} containing the elements in pretty JSON format
	 *
	 * @see #asNestedObject(Map, Writer, int)
	 */
	public static String asNestedObect(Map<String, ? extends Map<String, ? extends Collection<Object>>> elements) {
		try {
			StringWriter writer = new StringWriter();
			asNestedObject(elements, writer, 0);
			return writer.toString();
		}
		catch (IOException e) {
			return null;
		}
	}
}