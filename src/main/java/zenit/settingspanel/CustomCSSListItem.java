package main.java.zenit.settingspanel;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Represents a list item in the custom CSS settings panel.
 * Each item displays a CSS style line.
 */
public class CustomCSSListItem extends AnchorPane {

	/** Label to display the CSS style line. */
	@FXML
	private Label lblCSSText;

	/** The CSS style line represented by this list item. */
	private String cssStyleLine;

	/**
	 * Constructs a new {@code CustomCSSListItem} with the given CSS style.
	 *
	 * @param style The CSS style line to be displayed.
	 */
	public CustomCSSListItem(String style) {
		this.cssStyleLine = style;
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/settingspanel/CustomCSSListItem.fxml"));
		loader.setRoot(this);
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		initialize();
	}

	/** Initializes the UI component by setting the label text. */
	private void initialize() {
		lblCSSText.setText(cssStyleLine);
	}
}
