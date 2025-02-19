package main.java.zenit.ui.tree;

import java.io.File;
import java.util.ArrayList;
import javafx.scene.control.TreeItem;
import main.java.zenit.filesystem.ProjectFile;

public class FileTree {
	public static void createNodes(FileTreeItem<String> parent, File file) {
		int type = 0;
		
		if (file.listFiles() == null) { return; }
		
		var items = new ArrayList<FileTreeItem<String>>();
		
		File[] files = file.listFiles();
		String itemName;
		for (int index = 0; index < files.length; index++) {
			itemName = files[index].getName();
			
			if (!itemName.startsWith(".") && !itemName.equals("bin") && !itemName.endsWith(".class")) { //Doesn't include hidden files
				type = calculateType(parent, files[index]);
				FileTreeItem<String> item = new FileTreeItem<String> (files[index], itemName, type);
				items.add(item);
				
				if (files[index].isDirectory()) { createNodes(item, files[index]); }
			}
		}
		items.sort((a, b) -> a.getFile().getName().compareToIgnoreCase(b.getFile().getName()));
		
		for (var item : items) { parent.getChildren().add(item); }
	}
	
	public static void createParentNode(FileTreeItem<String> parent, File file) {
		if (parent == null || file == null) { return; }
		
		if (fileExistsInTree(file, parent)) { return; }
		
		int type = calculateType(parent, file);
		
		FileTreeItem<String> item = new FileTreeItem<String> (file, file.getName(), type);
		parent.getChildren().add(item);
		parent.getChildren().sort((a, b) -> {
			try {
				var fa = (FileTreeItem<String>) a;
				var fb = (FileTreeItem<String>) b;
				
				return fa.getFile().getName().compareToIgnoreCase(fb.getFile().getName());
			}
			catch (ClassCastException ex) {
				return 0;
			}
		});
		
		if (file.isDirectory()) { createNodes(item, file); }
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
	
	public static FileTreeItem<String> getTreeItemFromFile(FileTreeItem<String> root, File file) {
		if (root == null || file == null) { return null; }
		
		if (root.getFile().getAbsolutePath().equals(file.getAbsolutePath())) { return root; }
		
		for (var foo : root.getChildren()) {
			var bar = getTreeItemFromFile((FileTreeItem<String>) foo, file);
			
			if (bar != null && bar.getFile().getAbsolutePath().equals(file.getAbsolutePath())) { return bar; }
		}
		return null;
	}
	
	public static boolean removeFromFile(FileTreeItem<String> root, File file) {
		if (file == null) {
			return false;
		}
		
		var item = getTreeItemFromFile(root, file);
		
		if (item != null) {
			boolean removed = root.getChildren().remove(item);
			System.out.println("removed: " + removed);
			return removed;
		}
		
		System.out.println("not removed");
		return false;
	}
	
	private static int calculateType(FileTreeItem<String> parent, File file) {
		int type = 0;
		
		String itemName = file.getName();
		ProjectFile projectFile = new ProjectFile(file);
		
		//Project
		if (projectFile.getMetadata() != null) {
			type = FileTreeItem.PROJECT;
		}
		//Package
		else if (parent.getValue().equals("src") && file.isDirectory()) {
			type = FileTreeItem.PACKAGE;
		}
		else if (itemName.equals("src")) {
			type = FileTreeItem.SRC;
		}
		//Folder
		else if (projectFile.getMetadata() == null && file.isDirectory()) {
			type = FileTreeItem.FOLDER;
		}

		//Java-file
		else if (itemName.endsWith(".java")) {
			type = FileTreeItem.CLASS;
		} 
		//Text-file
		else if (itemName.endsWith(".txt")) {
			type = FileTreeItem.FILE;
		}
		else if (file.isFile() && itemName.indexOf('.') == -1) {
			type = FileTreeItem.FILE;
		} else {
			type = FileTreeItem.INCOMPATIBLE;
		}
		
		return type;
	}
	
	private static boolean fileExistsInTree(File file, FileTreeItem<String> root) {
		if (root == null || file == null) {
			return false;
		}

		File rootFile = root.getFile();
		
		if (rootFile.getAbsolutePath().contentEquals(file.getAbsolutePath())) {
			return true;
		}
		
		for (var child : root.getChildren()) {
			var fileTreeItem = (FileTreeItem<String>) child;

			if (fileExistsInTree(file, fileTreeItem)) {
				return true;
			}
		}
		return false;
	}
}