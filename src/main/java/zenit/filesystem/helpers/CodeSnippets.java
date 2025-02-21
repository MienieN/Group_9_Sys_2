package main.java.zenit.filesystem.helpers;

import main.java.zenit.exceptions.TypeCodeException;

/**
 * The CodeSnippets class provides utility methods to generate code snippets
 * for Java classes and interfaces. It includes predefined type codes for empty
 * code, class definitions, and interface definitions. This class also defines
 * handlers for constructing code snippets based on specified parameters.
 */
public class CodeSnippets {
	public static final int EMPTY = 99, CLASS = 100, INTERFACE = 101;
	
	
	// Methods:
	/**
	 * Generates a new piece of code based on the specified type code. The method
	 * creates an empty snippet, a class definition, or an interface definition depending
	 * on the provided type code. If the type code is invalid, an exception is thrown.
	 *
	 * @param typeCode      an integer representing the type of snippet to generate.
	 *                      Valid codes include:
	 *                      - `EMPTY` for an empty snippet,
	 *                      - `CLASS` for a class definition,
	 *                      - `INTERFACE` for an interface definition.
	 * @param classname     the name of the class or interface to be used in the generated
	 *                      snippet. This parameter is ignored if the type code is `EMPTY`.
	 *                      The value must end with ".java".
	 * @param packageName   the package name to include in the generated snippet. This
	 *                      parameter is ignored if the type code is `EMPTY`.
	 * @return a string containing the generated code snippet.
	 * @throws TypeCodeException if the provided type code is invalid or unsupported.
	 */
	public static String newSnippet(int typeCode, String classname, String packageName) throws TypeCodeException {
		String snippet;
		
		switch (typeCode) {
			case (EMPTY):
				snippet = ""; // creates empty code
				break;
			case (CLASS):
				snippet = newClass(classname, packageName); // creates a class
				break;
			case (INTERFACE):
				snippet = newInterface(classname, packageName); // creates an interface
				break;
			default:
				throw new TypeCodeException(); // Found in the exceptions package
		}
		return snippet;
	}

	/**
	 * Generates a basic Java class based on the provided class name
	 * and package name.
	 *
	 * @param classname the name of the class to be included in the generated snippet.
	 *                  The value must end with ".java".
	 * @param packageName the name of the package to include in the generated snippet.
	 * @return a string containing the generated Java class definition code.
	 */
	private static String newClass(String classname, String packageName) {
		int index = classname.indexOf(".java");
		classname = classname.substring(0, index);
        
        return "package " + packageName + ";\n" +
        "\n" +
        "public class " + classname + " {\n" +
        "\n" +
        "}";
	}
	
	/**
	 * Generates a new Java interface based on the specified class name and package name.
	 *
	 * @param classname the name of the interface to be generated. The value must end with ".java".
	 * @param packageName the name of the package to be included in the generated interface snippet.
	 * @return a string containing the generated Java interface code snippet.
	 */
	private static String newInterface(String classname, String packageName) {
		int index = classname.indexOf(".java");
		classname = classname.substring(0, index);
        
        return "package " + packageName + "\n" +
        "\n" +
        "public interface " + classname + " {\n" +
        "\n" +
        "}";
	}
}
