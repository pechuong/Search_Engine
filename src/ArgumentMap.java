import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ArgumentMap {

	private final Map<String, String> map;
	public boolean output;

	/**
	 * Initializes this argument map.
	 */
	public ArgumentMap() {
		this.map = new HashMap<String, String>(); // DONE Properly initialize this.
	}

	/**
	 * Initializes this argument map and then parsers the arguments into
	 * flag/value pairs where possible. Some flags may not have associated values.
	 * If a flag is repeated, its value is overwritten.
	 *
	 * @param args
	 */
	public ArgumentMap(String[] args) {
		this();
		this.output = false;
		parse(args);
	}

	/**
	 * Parses the arguments into flag/value pairs where possible. Some flags may
	 * not have associated values. If a flag is repeated, its value is
	 * overwritten.
	 *
	 * @param args the command line arguments to parse
	 */
	public void parse(String[] args) {
		for (int i = 0; i < args.length; i++) {
			if (isValidFlag(args[i])) {
				if (!isIndexFlag(args[i])) {
					try {
						if (isValue(args[i+1]) && args[i+1].matches(".*[jJ][sS][oO][nN]")) {
							this.map.put(args[i], args[i+1]);
						} else {
							this.map.put(args[i], "index.json");
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						System.out.println("End of args reached");
					}
				}
				/*
				try {
					if (isValue(args[i+1]) && isValidPath(args[i+1])) {
						this.map.put(args[i], args[i+1]);
					} else {
						this.map.put(args[i], null);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					this.map.put(args[i], null);
				}
				*/
			}
		}
		if (this.map.containsKey("-index")) {
			this.output = true;
		}
	}

	private boolean isIndexFlag(String arg) {
		return arg.matches("^-index");
	}

	/**
	 * Determines whether the argument is a flag. Flags start with a dash "-"
	 * character, followed by at least one other non-whitespace character.
	 *
	 * @param arg the argument to test if its a flag
	 * @return {@code true} if the argument is a flag
	 *
	 * @see String#startsWith(String)
	 * @see String#trim()
	 * @see String#isEmpty()
	 * @see String#length()
	 */
	public static boolean isValidFlag(String arg) {
		if (arg == null) {
			return false;
		}
		return arg.matches("^-index") | arg.matches("^-path");
	}

	/**
	 * Determines whether the argument is a value. Values do not start with a dash
	 * "-" character, and must consist of at least one non-whitespace character.
	 *
	 * @param arg the argument to test if its a value
	 * @return {@code true} if the argument is a value
	 *
	 * @see String#startsWith(String)
	 * @see String#trim()
	 * @see String#isEmpty()
	 * @see String#length()
	 */
	public static boolean isValue(String arg) {
		//return arg.matches("[^-]\\S+.*");   // test | old code
		if (arg == null) {
			return false;
		}
		return arg.matches("[^-\\s]+.*");
	}

	/**
	 * Checks if the string provided is a valid path object (either a file or a directory)
	 * 
	 * @return true if string provided is a valid path
	 */
	private boolean isValidPath(String arg) {
		Path currPath = Paths.get(arg);
		return (Files.isDirectory(currPath) | Files.exists(currPath))?true:false;
	}
	
	/**
	 * Returns the number of unique flags.
	 *
	 * @return number of unique flags
	 */
	public int numFlags() {
		return this.map.keySet().size();
	}

	/**
	 * Determines whether the specified flag exists.
	 *
	 * @param flag the flag to search for
	 * @return {@code true} if the flag exists
	 */
	public boolean hasFlag(String flag) {
		return this.map.containsKey(flag);
	}

	/**
	 * Determines whether the specified flag is mapped to a non-null value.
	 *
	 * @param flag the flag to search for
	 * @return {@code true} if the flag is mapped to a non-null value
	 */
	public boolean hasValue(String flag) {
		return this.map.get(flag) != null;
		//DONE Fill in this method and fix the return. 
	}

	/**
	 * Returns the value to which the specified flag is mapped as a
	 * {@link String}, or null if there is no mapping for the flag.
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         there is no mapping for the flag
	 */
	public String getString(String flag) {
		//System.out.printf("My flag is %s and value is %s\n", flag, this.map.get(flag));  //testing values
		return this.map.get(flag);
	}

	/**
	 * Returns the value to which the specified flag is mapped as a
	 * {@link String}, or the default value if there is no mapping for the flag.
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping for
	 *                     the flag
	 * @return the value to which the specified flag is mapped, or the default
	 *         value if there is no mapping for the flag
	 */
	public String getString(String flag, String defaultValue) {
		String myValue = getString(flag);
		return (myValue != null)?myValue:defaultValue;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path},
	 * or {@code null} if unable to retrieve this mapping for any reason
	 * (including being unable to convert the value to a {@link Path} or no value
	 * existing for this flag).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag the flag whose associated value is to be returned
	 * @return the value to which the specified flag is mapped, or {@code null} if
	 *         unable to retrieve this mapping for any reason
	 *
	 * @see Paths#get(String, String...)
	 */
	public Path getPath(String flag) throws IOException {
		var myValue = getString(flag);
		//System.out.printf("myValue is %d\n", myValue);
		return (myValue != null)?Paths.get(myValue):null;
	}

	/**
	 * Returns the value to which the specified flag is mapped as a {@link Path},
	 * or the default value if unable to retrieve this mapping for any reason
	 * (including being unable to convert the value to a {@link Path} or no value
	 * existing for this flag).
	 *
	 * This method should not throw any exceptions!
	 *
	 * @param flag         the flag whose associated value is to be returned
	 * @param defaultValue the default value to return if there is no mapping for
	 *                     the flag
	 * @return the value to which the specified flag is mapped as a {@link Path},
	 *         or the default value if there is no mapping for the flag
	 */
	public Path getPath(String flag, Path defaultValue) throws IOException {
		var myPath = getPath(flag);
		return (myPath != null)?myPath:defaultValue;
	}

	@Override
	public String toString() {
		return this.map.toString();
	}
}
