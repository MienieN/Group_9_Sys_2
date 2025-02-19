package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

public class PackageHandler extends FolderHandler {

	protected static void createPackage(File file) throws IOException {
		if (file.getName().equals("package")) {
			throw new IOException("Can't name package: " + file.getName());
		}
		createNewFolder(file);
	}
	
}
