package main.java.zenit.filesystem.helpers;

import main.java.zenit.exceptions.TypeCodeException;

public class CodeSnippets {
	
	public static final int EMPTY = 99;
	public static final int CLASS = 100;
	public static final int INTERFACE = 101;
	
	public static String newSnippet(int typeCode, String classname, String packagename) throws TypeCodeException {
		String snippet;
		switch (typeCode) {
		case (EMPTY): snippet = ""; break;
		case (CLASS): snippet = newClass(classname, packagename); break;
		case (INTERFACE): snippet = newInterface(classname, packagename); break;
		default: throw new TypeCodeException();
		}
		return snippet;
	}
	
	private static String newClass (String classname, String packagename) {
		
		int index = classname.indexOf(".java");
		classname = classname.substring(0, index);
		
		String codesnippet =
				"package " + packagename + ";\n" + 
				"\n" +
				"public class " + classname + " {\n" +
				"\n" + 
				"}";
				
		return codesnippet;
	}
	
	private static String newInterface (String classname, String packagename) {
		
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
