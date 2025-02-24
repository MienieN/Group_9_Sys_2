package main.java.zenit.ui.tree;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import main.java.zenit.filesystem.ProjectFile;
import main.java.zenit.filesystem.helpers.CodeSnippets;
import main.java.zenit.ui.MainController;

public class TreeContextMenu extends ContextMenu implements EventHandler<ActionEvent>{
	private final MainController controller;
	private final TreeView<String> treeView;
	private final Menu createItem = new Menu("New...");
	private final MenuItem createClass = new MenuItem("New class");
	private final MenuItem createInterface = new MenuItem("New interface");
	private final MenuItem createPackage = new MenuItem("New package");
	private final MenuItem renameItem = new MenuItem("Rename");
	private final MenuItem deleteItem = new MenuItem("Delete");
	private final MenuItem importJar = new MenuItem("Import jar");
	private final MenuItem properties = new MenuItem("Properties");
	
	public TreeContextMenu(MainController controller, TreeView<String> treeView) {
		super();
		this.controller = controller;
		this.treeView = treeView;
		initContextMenu();
	}

	/**
	 * Sets the context of the context menu based on the selected node.
	 * Updates the text of the rename and delete menu items to include the name of the selected node.
	 * Adds or removes the "New package" menu item based on whether the selected node is the "src" folder.
	 * Adds or removes the "Import jar" and "Properties" menu items based on whether the selected node is a project.
	 *
	 * @param selectedNode The name of the selected node
	 */
	private void setContext(String selectedNode) {
		renameItem.setText(String.format("Rename \"%s\"", selectedNode));
		deleteItem.setText(String.format("Delete \"%s\"", selectedNode));

		if (selectedNode.equals("src") && !createItem.getItems().contains(createPackage)) {
			createItem.getItems().add(createPackage);
		} else {
			createItem.getItems().remove(createPackage);
		}

		FileTreeItem<String> selectedItem = (FileTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
		if (selectedItem.getType() == FileTreeItem.PROJECT) {
			getItems().add(importJar);
			getItems().add(properties);
		} else {
			getItems().remove(importJar);
			getItems().remove(properties);
		}
	}

	/**
	 * Shows the context menu at the specified coordinates.
	 * @param node The node to show the context menu for
	 * @param x The x-coordinate of the context menu
	 * @param y The y-coordinate of the context menu
	 */
	@Override
	public void show(Node node, double x, double y) {
		TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
		if (selectedItem != null) { setContext(selectedItem.getValue()); }
		super.show(node, x, y);
	}

	/**
	 * Initializes the context menu by adding the menu items and setting their action handlers.
	 */
	private void initContextMenu() {
		createItem.getItems().addAll(createClass, createInterface);
		getItems().addAll(createItem, renameItem, deleteItem);
		setMenuItemActionHandlers(createClass, createInterface, renameItem, deleteItem, createPackage, importJar, properties);
	}

	/**
	 * Sets the action handlers for the specified menu items.
	 * @param menuItems The menu items to set the action handlers for
	 */
	private void setMenuItemActionHandlers(MenuItem... menuItems) {
		for (MenuItem menuItem : menuItems) {
			menuItem.setOnAction(this);
		}
	}

	/**
	 * Creates a new file of the specified type and adds it to the tree by calling its helper method.
	 * @param typeCode The type code of the file to create
	 */
	private void newFile(int typeCode) {
		createAndAddFileToTree(typeCode, FileTreeItem.CLASS);
	}

	/**
	 * Creates a new file of the specified type and adds it to the tree.
	 * @param typeCode The type code of the file to create
	 * @param fileType The type of the file to add to the tree
	 */
	private void createAndAddFileToTree(int typeCode, int fileType) {
		FileTreeItem<String> parent = (FileTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
		File newFile = controller.createFile(parent.getFile(), typeCode);
		if (newFile != null) {
			FileTreeItem<String> newItem = new FileTreeItem<>(newFile, newFile.getName(), fileType);
			parent.getChildren().add(newItem);
		}
	}

	/**
	 * Handles action events triggered by the context menu.
	 * Executes the corresponding action based on the menu item text.
	 *
	 * @param actionEvent The action event that triggered the handler
	 */
	@Override
	public void handle(ActionEvent actionEvent) {
		FileTreeItem<String> selectedItem = (FileTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
		File selectedFile = selectedItem.getFile();

		Map<String, Consumer<File>> actions = createActionsMap(selectedItem);

		Object source = actionEvent.getSource();
		if (source instanceof MenuItem) {
			MenuItem menuItem = (MenuItem) source;
			actions.getOrDefault(menuItem.getText(), f -> {}).accept(selectedFile);
		}
	}

	/**
	 * Creates a map of actions associated with the menu item texts.
	 * @param selectedItem The selected item in the tree view
	 * @return The map of actions
	 */
	private Map<String, Consumer<File>> createActionsMap(FileTreeItem<String> selectedItem) {
		Map<String, Consumer<File>> actions = new HashMap<>();
		actions.put("New class", file -> newFile(CodeSnippets.CLASS));
		actions.put("New interface", file -> newFile(CodeSnippets.INTERFACE));
		actions.put("Rename", file -> renameFile(selectedItem, file));
		actions.put("Delete", file -> deleteFile(selectedItem, file));
		actions.put("New package", file -> newPackage(selectedItem, file));
		actions.put("Import jar", this::importJar);
		actions.put("Properties", file -> showProperties(selectedItem, file));
		return actions;
	}

	/**
	 * Renames the selected file and updates the tree view.
	 * @param selectedItem The selected item in the tree view
	 * @param file The file to rename
	 */
	private void renameFile(FileTreeItem<String> selectedItem, File file) {
		File newFile = controller.renameFile(file);
		if (newFile != null) {
			selectedItem.setFile(newFile);
			selectedItem.setValue(newFile.getName());
			FileTree.changeFileForNodes(selectedItem, selectedItem.getFile());
		}
	}

	/**
	 * Deletes the selected file and removes the tree item.
	 *
	 * @param selectedItem The selected item in the tree view
	 * @param file The file to delete
	 */
	private void deleteFile(FileTreeItem<String> selectedItem, File file) {
		controller.deleteFile(file);
		selectedItem.getParent().getChildren().remove(selectedItem);
	}

	/**
	 * Creates a new package and adds it to the tree.
	 *
	 * @param selectedItem The selected item in the tree view
	 * @param file The file representing the package
	 */
	private void newPackage(FileTreeItem<String> selectedItem, File file) {
		File packageFile = controller.newPackage(file);
		if (packageFile != null) {
			FileTreeItem<String> packageNode = new FileTreeItem<>(packageFile, packageFile.getName(), FileTreeItem.PACKAGE);
			selectedItem.getChildren().add(packageNode);
		}
	}

	/**
	 * Imports a JAR file into the project.
	 *
	 * @param file The JAR file to import
	 */
	private void importJar(File file) {
		ProjectFile projectFile = new ProjectFile(file.getPath());
		controller.chooseAndImportLibraries(projectFile);
	}

	/**
	 * Shows the properties of the selected project.
	 *
	 * @param selectedItem The selected item in the tree view
	 * @param file The file representing the project
	 */
	private void showProperties(FileTreeItem<String> selectedItem, File file) {
		if (selectedItem.getType() == FileTreeItem.PROJECT) {
			ProjectFile projectFile = new ProjectFile(file.getPath());
			controller.showProjectProperties(projectFile);
		}
	}
}