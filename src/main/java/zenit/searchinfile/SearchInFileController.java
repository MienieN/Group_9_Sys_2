package main.java.zenit.searchinfile;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.zenit.ui.MainController;

/**
 * Controller for the search in file functionality.
 * Provides a UI for searching and replacing text within a file.
 */
public class SearchInFileController extends AnchorPane {

	@FXML
	private TextField fldInputField;

	@FXML
	private TextField fldReplaceWord;

	@FXML
	private Button btnUp;

	@FXML
	private Button btnDown;

	@FXML
	private Button btnEsc;

	@FXML
	private Button btnReplaceOne;

	@FXML
	private Button btnReplaceAll;

	@FXML
	private Label lblOccurrences;

	private Search search;
	private int occurrences = 0;
	private Stage window;
	private Scene scene;

	/**
	 * Constructs a new SearchInFileController.
	 *
	 * @param search        The search logic handler.
	 * @param mainController The main application controller.
	 */
	public SearchInFileController(Search search, MainController mainController) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/zenit/searchinfile/SearchInFileWindow.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.window = new Stage();
		this.scene = new Scene(this);
		this.window.initStyle(StageStyle.UNDECORATED);
		this.window.setScene(scene);
		this.window.setX(1050);
		this.window.setY(160);
		this.fldInputField.requestFocus();

		initialize();
		scene.getStylesheets().add(getClass().getResource("/zenit/searchinfile/searchInFileDarkMode.css").toExternalForm());
		this.window.show();
		this.search = search;
	}

	/**
	 * Performs a new search based on the input search word.
	 *
	 * @param searchWord The word to search for in the file.
	 */
	private void makeNewSearch(String searchWord) {
		occurrences = search.searchInFile(searchWord);

		if (occurrences < 1) {
			lblOccurrences.setText(fldInputField.getText().length() > 0 ? "0/" + occurrences : "");
		} else {
			lblOccurrences.setText("1/" + occurrences);
		}
	}

	/**
	 * Initializes the event listeners and button actions.
	 */
	private void initialize() {
		fldInputField.textProperty().addListener((observable, oldValue, newValue) -> {
			search.clearZen();
			makeNewSearch(fldInputField.getText());
		});

		btnReplaceAll.setPickOnBounds(true);
		btnReplaceAll.setOnAction(event -> search.replaceAll(fldReplaceWord.getText()));

		btnReplaceOne.setPickOnBounds(true);
		btnReplaceOne.setOnAction(event -> search.replaceOne(fldReplaceWord.getText()));

		btnUp.setPickOnBounds(true);
		btnUp.setOnAction(event -> {
			int i = search.jumpUp();
			lblOccurrences.setText(++i + "/" + occurrences);
		});

		btnDown.setPickOnBounds(true);
		btnDown.setOnAction(event -> {
			int i = search.jumpDown();
			lblOccurrences.setText(++i + "/" + occurrences);
		});

		btnEsc.setPickOnBounds(true);
		btnEsc.setOnAction(event -> {
			window.close();
			search.cleanZen();
		});

		scene.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ESCAPE) {
					window.close();
					search.cleanZen();
				}
			}
		});
	}
}
