package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

public class PackageHandler extends FolderHandler {

    /**
     * Creates a new package folder if the name is valid.
     *
     * @param file The file representing the package folder to be created.
     * @throws IOException If the package name is invalid or the folder cannot be created.
     */
    protected static void createPackage(File file) throws IOException {
        if (file.getName().equals("package")) {
            throw new IOException("Can't name package: " + file.getName());
        }
        createNewFolder(file);
    }

}
