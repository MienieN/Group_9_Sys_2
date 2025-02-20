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
	

	public static String newSnippet(int typeCode, String classname, String packageName) throws TypeCodeException {
		String snippet;
		
		switch (typeCode) {
			case (EMPTY):
				snippet = ""; break;
			case (CLASS):
				snippet = newClass(classname, packageName); break;
			case (INTERFACE):
				snippet = newInterface(classname, packageName); break;
			default:
				throw new TypeCodeException();
		}
		return snippet;
	}

	private static String newClass(String classname, String packageName) {
		int index = classname.indexOf(".java");
		classname = classname.substring(0, index);
		
		String codeSnippet =
				"package " + packageName + ";\n" +
				"\n" +
				"public class " + classname + " {\n" +
				"\n" +
				"}";
		return codeSnippet;
	}
	
	private static String newInterface(String classname, String packagename) {
		int index = classname.indexOf(".java");
		classname = classname.substring(0, index);
		
		String codesnippet =
				"package " + packagename + "\n" + 
				"\n" +
				"public interface " + classname + " {\n" +
				"\n" + 
				"}";
		return codesnippet;
	}
}
