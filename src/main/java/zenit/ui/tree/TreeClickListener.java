package main.java.zenit.ui.tree;

import javafx.event.EventHandler;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.java.zenit.ui.MainController;

public class TreeClickListener implements EventHandler<MouseEvent> {
	private MainController controller;
	private TreeView<String> treeView;
	
	public TreeClickListener(MainController controller, TreeView<String> treeView) {
		this.controller = controller;
		this.treeView = treeView;
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		FileTreeItem<String> selectedItem = (FileTreeItem<String>) 
				treeView.getSelectionModel().getSelectedItem();

		if (selectedItem != null && !selectedItem.getFile().isDirectory() && 
				mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
			controller.openFile(selectedItem.getFile());
		}
		if (selectedItem != null) {
			controller.updateStatusLeft(selectedItem.getFile().getPath());
		}
	}
}