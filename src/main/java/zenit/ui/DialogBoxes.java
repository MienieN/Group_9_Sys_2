package main.java.zenit.ui;

import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class DialogBoxes {
	
	public static String inputDialog(Stage stage, String title, String header, String content, String textInput) {
		return inputDialog(stage, title, header, content, textInput, 0, 0);
	}

	public static String inputDialog(Stage stage, String title, String header, String content,
			String textInput, int startSelection, int stopSelection) {
		TextInputDialog dialog = new TextInputDialog(textInput);
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		dialog.setContentText(content);
		TextField textField = dialog.getEditor();

		Platform.runLater(() -> {
			if (stopSelection <= 0) {
				textField.selectRange(content.length() - 1, startSelection);
			} else {
				textField.selectRange(stopSelection, startSelection);
			}
		});
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
		   return result.get();
		}
		return null;
	}
	
	public static void errorDialog(String title, String header, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	public static void informationDialog(String title, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	public static int twoChoiceDialog(String title, String header, String content, String option1, String option2) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);

		ButtonType buttonTypeOne = new ButtonType(option1);
		ButtonType buttonTypeTwo = new ButtonType(option2);
		
		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne){
		    return 1;
		} else if (result.get() == buttonTypeTwo) {
		    return 2;
		} else {
			return 0;
		}
	}
}