package main.java.zenit.ui.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.scene.control.TreeItem;
import main.java.zenit.filesystem.ProjectFile;

public class FileTree {
	/**
	 * Iterates through files, filtering valid files, creating FileTreeItem objects,
	 * and recursively calling itself for directories.
	 * @param parent The parent FileTreeItem
	 * @param file The file to iterate through
	 */
	public static void createNodes(FileTreeItem<String> parent, File file) {
		if (file.listFiles() == null) { return; }

		var items = new ArrayList<FileTreeItem<String>>();
		for (File child : file.listFiles()) {
			if (isValidFile(child)) {
				FileTreeItem<String> item = createFileTreeItem(parent, child);
				items.add(item);
				if (child.isDirectory()) { createNodes(item, child); }
			}
		}
		items.sort(Comparator.comparing(a -> a.getFile().getName().toLowerCase()));
		parent.getChildren().addAll(items);
	}

	/**
	 * A helper method to check if a file should be included.
	 * @param file The file to check
	 * @return True if the file is valid, false otherwise
	 */
	private static boolean isValidFile(File file) {
		String name = file.getName();
		return !name.startsWith(".") && !name.equals("bin") && !name.equals(".class");
	}

	/**
	 * A helper method to create a FileTreeItem with the appropriate type.
	 * @param parent The parent FileTreeItem
	 * @param file The file to create the FileTreeItem from
	 * @return The created FileTreeItem
	 */
	private static FileTreeItem<String> createFileTreeItem(FileTreeItem<String> parent, File file) {
		int type = calculateType(parent, file);
		return new FileTreeItem<>(file, file.getName(), type);
	}

	/**
	 * This method is responsible for adding a new FileTreeItem to the parent node's children,
	 * ensuring that the new item is correctly placed and sorted.
	 * @param parent The parent node
	 * @param file The file to create a new node from
	 */
	public static void createParentNode(FileTreeItem<String> parent, File file) {
		if (parent == null || file == null || fileExistsInTree(file, parent)) {
			return;
		}
		
		int type = calculateType(parent, file);
		FileTreeItem<String> item = new FileTreeItem<String> (file, file.getName(), type);
		parent.getChildren().add(item);
		sortChildren(parent);

		if (file.isDirectory()) {
			createNodes(item, file);
		}
	}

	/**
	 * This method sorts the parent's children alphabetically by their file names in a case-insensitive manner.
	 * EXPLANATION OF LAMBDA EXPRESSION:
	 * parent.getChildren() retrieves the list of children nodes of the parent.
	 * .sort(...) sorts this list in place.
	 * Comparator.comparing(...) creates a comparator that compares the items based on a specific key.
	 * a -> ((FileTreeItem<String>) a).getFile().getName().toLowerCase() is a lambda expression that:
	 * 		Casts each child node a to FileTreeItem<String>.
	 * 		Retrieves the File object associated with the FileTreeItem.
	 * 		Gets the name of the file and converts it to lowercase for case-insensitive comparison.
	 */
	private static void sortChildren(FileTreeItem<String> parent) {
		parent.getChildren().sort(Comparator.comparing(a -> ((FileTreeItem<String>) a).getFile().getName().toLowerCase()));
	}
	
	public static void changeFileForNodes(FileTreeItem<String> parent, File file) {
		if (file.listFiles() == null) { return; }
		
		File[] files = file.listFiles();
		
		for (TreeItem<String> item : parent.getChildren()) {
			FileTreeItem<String> ftItem = (FileTreeItem<String>) item;
			for (File entry : files) {
				if (entry.getName().equals(ftItem.getValue())) {
					ftItem.setFile(entry);
				}
			}
			if (ftItem.getFile().isDirectory()) {
				changeFileForNodes(ftItem, ftItem.getFile());
			}
		}
	}

	/**
	 * Recursive function that searches for a FileTreeItem in a tree structure based on a given File.
	 * It starts from the root node and traverses through its children to find a node that matches
	 * the files' absolute path.
	 * @param root The root node of the tree
	 * @param file The file to search for
	 * @return The FileTreeItem that matches the file, or null if not found
	 */
	public static FileTreeItem<String> getTreeItemFromFile(FileTreeItem<String> root, File file) {
		if (root == null || file == null) {
			return null; // If the root or file is null.
		}
		
		if (root.getFile().getAbsolutePath().equals(file.getAbsolutePath())) {
			return root; // If the root's file matches the given file's absolute path.
		}

		// Iterate through the children of the root and recursively call the function on each child.
		for (TreeItem<String> child : root.getChildren()) {
			FileTreeItem<String> result = getTreeItemFromFile((FileTreeItem<String>) child, file);
			if (result != null) {
				return result;
			}
		}
		return null; // No match found.
	}

	/**
	 * Removes the specified file from the tree rooted at the given root node.
	 * @param root The root node of the tree
	 * @param file The file to remove
	 * @return true if the file was removed, false otherwise
	 */
	public static boolean removeFromFile(FileTreeItem<String> root, File file) {
		if (file == null) {
			System.out.println("not removed");
			return false;
		}
		
		var item = getTreeItemFromFile(root, file);
		boolean removed = item != null && root.getChildren().remove(item);
		System.out.println("removed: " + removed);
		return removed;
	}

	/**
	 * Calculates the type of file based on its parent and its own properties.
	 * @param parent The parent FileTreeItem
	 * @param file The file to calculate the type for
	 * @return The type of the file
	 */
	private static int calculateType(FileTreeItem<String> parent, File file) {
		String itemName = file.getName();
		ProjectFile projectFile = new ProjectFile(file);
		int type;

		if (projectFile.getMetadata() != null) {
			type = FileTreeItem.PROJECT;
		} else if (itemName.equals("src")) {
			type = FileTreeItem.SRC;
		} else if (parent.getValue().equals("src") && file.isDirectory()) {
			type = FileTreeItem.PACKAGE;
		} else if (file.isDirectory()) {
			type = FileTreeItem.FOLDER;
		} else if (itemName.endsWith(".java")) {
			type = FileTreeItem.CLASS;
		} else if (itemName.endsWith(".txt") || itemName.indexOf('.') == -1) {
			type = FileTreeItem.FILE;
		} else {
			type = FileTreeItem.INCOMPATIBLE;
		}

		return type;
	}

	/**
	 * Checks if a file exists in the tree rooted at the given root node.
	 * @param file The file to search for
	 * @param root The root node of the tree
	 * @return true if the file exists in the tree, false otherwise
	 */
	private static boolean fileExistsInTree(File file, FileTreeItem<String> root) {
		if (root == null || file == null) {
			return false;
		}

		if (root.getFile().getAbsolutePath().equals(file.getAbsolutePath())) {
			return true;
		}

		return root.getChildren().stream()
				.anyMatch(child -> fileExistsInTree(file, (FileTreeItem<String>) child));
	}
}