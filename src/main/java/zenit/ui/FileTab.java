package main.java.zenit.ui;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import main.java.zenit.filesystem.FileController;
import main.java.zenit.util.StringUtilities;
import main.java.zenit.zencodearea.ZenCodeArea;

public class FileTab extends Tab {
	private File initialFile, file;
	private String initialTitle;
	private MainController mainController;
	private ZenCodeArea zenCodeArea;
	private boolean hasChanged;
	
	public FileTab(ZenCodeArea zenCodeArea, MainController mc) {
		this.zenCodeArea = zenCodeArea;
		this.mainController = mc;
		initialTitle = "Untitled";
		
		zenCodeArea.setOnMouseClicked(new UpdateDetector());
		zenCodeArea.setOnKeyPressed(new UpdateDetector());
		
		initializeUI();
	}
	
	private void initializeUI() {
		AnchorPane anchorPane = new AnchorPane();	
		AnchorPane.setTopAnchor(zenCodeArea, 0.0);
		AnchorPane.setRightAnchor(zenCodeArea, 0.0);
		AnchorPane.setBottomAnchor(zenCodeArea, 0.0);
		AnchorPane.setLeftAnchor(zenCodeArea, 0.0);
		anchorPane.getChildren().add(zenCodeArea);
		
		setContent(anchorPane);
		setText(initialTitle);
		
		zenCodeArea.textProperty().addListener((observable, oldText, newText) -> {
			String initialFileContent = FileController.readFile(initialFile);
			
			hasChanged = !initialFileContent.equals(newText);
			updateUI();
		});

		setStyle("-fx-background-color: #444;");
		setStyle("-fx-stroke: #fff;");
		
		Platform.runLater(zenCodeArea::requestFocus);
	}

	public void setStyle(int row, int column, String style) {
		int columnLength = zenCodeArea.getParagraph(row - 1).getText().length();
		int endColumn = (column >= columnLength) ? column : column + 1;

		Platform.runLater(() -> zenCodeArea.setStyle(row - 1, column - 1, endColumn, Arrays.asList(style)));
	}
	
	public void addTextPropertyListener(ChangeListener<? super String> listener) {
		zenCodeArea.textProperty().addListener(listener);
	}

	public void shortcutsTrigger() {
		if (file == null) { return; }

		String text = zenCodeArea.getText();
		int caretPosition = zenCodeArea.getCaretPosition();
		Map<String, String> shortcuts = initializeShortcuts();

		shortcuts.forEach((shortcut, replacement) -> {
			if (caretPosition >= shortcut.length() && text.endsWith(shortcut)) {
				replaceShortcutText(caretPosition, shortcut, replacement);
				return;
			}
		});
	}

	private void replaceShortcutText(int caretPosition, String shortcut, String replacement) {
		zenCodeArea.replaceText(caretPosition - shortcut.length(), caretPosition, replacement);
		zenCodeArea.moveTo(caretPosition + replacement.length() - shortcut.length());
	}

	private Map<String, String> initializeShortcuts() {
		Map<String, String> shortcuts = new HashMap<>();
		shortcuts.put("sysout", "System.out.println();");
		shortcuts.put("syserr", "System.err.println();");
		shortcuts.put("main", "public static void main(String[] args) {\n\n}");
		shortcuts.put("pv", "public void ");
		return shortcuts;
	}

	public void commentsShortcutsTrigger() {
		if (file == null) { return; }

		int caretPosition = zenCodeArea.getCaretPosition();
		String text = zenCodeArea.getText();

		switch (text.substring(Math.max(0, caretPosition - 3), caretPosition)) {
			case "/*":
				handleBlockComment(caretPosition);
				break;
			case "/**":
				handleJavadocComment(caretPosition);
				break;
			default:
				zenCodeArea.replaceText(caretPosition, caretPosition, "\n");
				break;
		}
	}

	private void handleBlockComment(int caretPosition) {
		zenCodeArea.replaceText(caretPosition - 2, caretPosition, "/*\n* \n*/");
		zenCodeArea.moveTo(caretPosition + 3);
	}

	private void handleJavadocComment(int caretPosition) {
		zenCodeArea.replaceText(caretPosition - 3, caretPosition, "/**\n* \n* @author \n*/");
		zenCodeArea.moveTo(caretPosition + 3);
	}
	
	public void navigateToCorrectTabIndex() {
		int previousLine = zenCodeArea.getCurrentParagraph() - 1;
		String previousText = zenCodeArea.getParagraph(previousLine).getText();
		
		int count = StringUtilities.countLeadingSpaces(previousText);
		
		String spaces = "";
		for (int i = 0; i < count; i++) {
			spaces += " ";
		}
		
		if (previousText.endsWith("{")) {
			spaces += "    ";
			zenCodeArea.insertText(zenCodeArea.getCaretPosition(), spaces);
			addMissingCurlyBrace(previousLine + 2, 0, spaces);
		} else {
			zenCodeArea.insertText(zenCodeArea.getCaretPosition(), spaces);
		}
	}
	
	private void addMissingCurlyBrace(int row, int column, String spaces) {
		int[] counts = {
			StringUtilities.count(zenCodeArea.getText(), '{'),
			StringUtilities.count(zenCodeArea.getText(), '}'),
		};
		
		if (counts[0] == counts[1] + 1) {
			zenCodeArea.insertText(zenCodeArea.getCaretPosition(), "\n");
			zenCodeArea.insertText(
				row, column, 
				spaces.substring(0, spaces.length() - 4) + "}"
			);
			zenCodeArea.moveTo(row - 1, spaces.length());
		}
	}
	
	private void updateUI() {
		if (hasChanged) {
			setText(initialTitle + " *");
		} else {
			setText(initialTitle);
		}
	}
	
	public void update(File file) {
		setFile(file, false);
		hasChanged = false;
		updateUI();
	}
	
	public File getFile() { return file; }
	
	public String getFileText() { return zenCodeArea.getText(); }
	
	public void setFile(File file, boolean shouldSetContent) {
		this.initialFile = file;
		this.file = file;
		this.initialTitle = file == null ? "Untitled" : file.getName();

		setText(initialTitle);
		
		if (shouldSetContent && file != null) { setFileText(FileController.readFile(file)); }
	}
	
	public void setFileText(String text) { zenCodeArea.replaceText(text); }
	
	public boolean hasChanged() { return hasChanged; }
		
	public ZenCodeArea getZenCodeArea() { return zenCodeArea; }
	
	public int showConfirmDialog() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Save?");
		alert.setHeaderText("The file has been modified. Would you like to save?");
		alert.setContentText("Save?");
		
		ButtonType okButton = new ButtonType("Yes", ButtonData.OK_DONE);
		ButtonType noButton = new ButtonType("No", ButtonData.NO);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		
		alert.getButtonTypes().setAll(cancelButton, okButton, noButton);
		
		var wrapper = new Object() { int response; };

		alert.showAndWait().ifPresent(result -> {
			switch (result.getButtonData()) {
				case CANCEL_CLOSE:
					wrapper.response = 0;
					break;
				case NO:
					wrapper.response = 1;
					break;
				case OK_DONE:
					wrapper.response = 2;
					break;
				default:
					wrapper.response = 0;
					break;
			}
		});
		return wrapper.response;
	}
	
	private class UpdateDetector implements EventHandler<Event> {

		@Override
		public void handle(Event event) {
			int row = zenCodeArea.getCurrentParagraph();
			int column = zenCodeArea.getCaretColumn();
			mainController.updateStatusRight((row+1) + " : " + (column+1));
		}
	}
}