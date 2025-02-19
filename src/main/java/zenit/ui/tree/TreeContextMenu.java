package main.java.zenit.ui.tree;

import java.io.File;
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
	private MainController controller;
	private TreeView<String> treeView;
	private Menu createItem = new Menu("New...");
	private MenuItem createClass = new MenuItem("New class");
	private MenuItem createInterface = new MenuItem("New interface");
	private MenuItem createPackage = new MenuItem("New package");
	private MenuItem renameItem = new MenuItem("Rename");
	private MenuItem deleteItem = new MenuItem("Delete");
	private MenuItem importJar = new MenuItem("Import jar");
	private MenuItem properties = new MenuItem("Properties");
	
	public TreeContextMenu(MainController controller, TreeView<String> treeView) {
		super();
		this.controller = controller;
		this.treeView = treeView;
		initContextMenu();
	}
	
	private void setContext(String selectedNode) {
		String renameItemTitle = String.format("Rename \"%s\"", selectedNode);
		String deleteItemTitle = String.format("Delete \"%s\"", selectedNode);
		renameItem.setText(renameItemTitle);
		deleteItem.setText(deleteItemTitle);
				
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
	
	@Override
	public void show(Node node, double x, double y) {
		TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
		
		if (selectedItem != null) { setContext(selectedItem.getValue()); }
		
		super.show(node, x, y);
	}
	
	private void initContextMenu() {
		createItem.getItems().add(createClass);
		createItem.getItems().add(createInterface);
		getItems().addAll(createItem, renameItem, deleteItem);
		createClass.setOnAction(this);
		createInterface.setOnAction(this);
		renameItem.setOnAction(this);
		deleteItem.setOnAction(this);
		createPackage.setOnAction(this);
		importJar.setOnAction(this);
		properties.setOnAction(this);
	}
	
	private void newFile(int typeCode) {
		FileTreeItem<String> parent = (FileTreeItem<String>) 
				treeView.getSelectionModel().getSelectedItem();
		File newFile = controller.createFile(parent.getFile(), typeCode);
		if (newFile != null) {
			FileTreeItem<String> newItem = new FileTreeItem<String>(newFile, newFile.getName(), FileTreeItem.CLASS);
			parent.getChildren().add(newItem);
		}
	}

	@Override
	public void handle(ActionEvent actionEvent) {
		FileTreeItem<String> selectedItem = (FileTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
		File selectedFile = selectedItem.getFile();
		
		if (actionEvent.getSource().equals(createClass)) {
			newFile(CodeSnippets.CLASS);
		} else if (actionEvent.getSource().equals(createInterface)) {
			newFile(CodeSnippets.INTERFACE);
		} else if (actionEvent.getSource().equals(renameItem)) {
			File newFile = controller.renameFile(selectedFile);
			if (newFile != null) {
				selectedItem.setFile(newFile);
				selectedItem.setValue(newFile.getName());
				FileTree.changeFileForNodes(selectedItem, selectedItem.getFile());
			}
		} else if (actionEvent.getSource().equals(deleteItem)) {
			controller.deleteFile(selectedFile);
			selectedItem.getParent().getChildren().remove(selectedItem);
		} else if (actionEvent.getSource().equals(createPackage)) {
			File packageFile = controller.newPackage(selectedFile);
			if (packageFile != null) {
				FileTreeItem<String> packageNode = new FileTreeItem<String>(packageFile, packageFile.getName(), FileTreeItem.PACKAGE);
				selectedItem.getChildren().add(packageNode);
			}
		} else if (actionEvent.getSource().equals(importJar)) {
			ProjectFile projectFile = new ProjectFile(selectedFile.getPath());
			controller.chooseAndImportLibraries(projectFile);
		} else if (actionEvent.getSource().equals(properties) && selectedItem.getType() == FileTreeItem.PROJECT) {
			ProjectFile projectFile = new ProjectFile(selectedFile.getPath());
			controller.showProjectProperties(projectFile);
		}
	}
}