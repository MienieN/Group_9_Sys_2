package main.java.zenit.ui.tree;

import javafx.event.EventHandler;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.java.zenit.ui.MainController;
import main.java.zenit.zencodearea.ZenCodeArea;
import org.fxmisc.richtext.CodeArea;

public class TreeClickListener implements EventHandler<MouseEvent> {
	private final MainController controller;
	private final TreeView<String> treeView;
	
	public TreeClickListener(MainController controller, TreeView<String> treeView) {
		this.controller = controller;
		this.treeView = treeView;
	}

	/**
	 * Handles mouse click events on the tree view.
	 * If a file (not a directory) is double-clicked with the primary mouse button, it opens the file.
	 * Updates the status bar with the path of the selected file.
	 *
	 * @param mouseEvent The mouse event that triggered the handler
	 */
	@Override
	public void handle(MouseEvent mouseEvent) {
		FileTreeItem<String> selectedItem = (FileTreeItem<String>) 
				treeView.getSelectionModel().getSelectedItem();

		if (selectedItem != null) {
			if (!selectedItem.getFile().isDirectory() && mouseEvent.getButton().equals(MouseButton.PRIMARY) && mouseEvent.getClickCount() == 2) {
				controller.openFile(selectedItem.getFile());
			}
			controller.updateStatusLeft(selectedItem.getFile().getPath());
		}
	}
}