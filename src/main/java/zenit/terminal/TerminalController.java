package main.java.zenit.terminal;

import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

/**
 * The TerminalController class is responsible for managing the terminal functionality in the application.
 */
public class TerminalController {
	@FXML
    public AnchorPane basePane;
	@FXML
    public TabPane tabPane;

	/**
	 * Initializes the TerminalController by adding a new terminal tab using a predefined dark configuration.
	 */
	public void initialize() {
		addTerminalTab();
	}

	/**
	 * Adds a new terminal tab to the tab pane using a predefined dark configuration.
	 * The terminal tab is created with the dark configuration settings for background color, foreground color,
	 * cursor blink, cursor color, and font family.
	 */
	private void addTerminalTab() {
		TerminalConfig config = getDarkTerminalConfig();
		TerminalBuilder builder = new TerminalBuilder(config);
		TerminalTab terminalTab = builder.newTerminal();
		tabPane.getTabs().add(terminalTab);
	}

	/**
	 * Retrieves a TerminalConfig object representing the configuration for a dark-themed terminal.
	 *
	 * @return a TerminalConfig object with settings for background color, foreground color, cursor blink,
	 * cursor color, and font family appropriate for a dark-themed terminal.
	 */
    public TerminalConfig getDarkTerminalConfig() {
		TerminalConfig darkConfig = new TerminalConfig();
		darkConfig.setBackgroundColor(Color.BLACK);
		darkConfig.setForegroundColor(Color.WHITE);
		darkConfig.setCursorBlink(true);
		darkConfig.setCursorColor(Color.WHITE);
		darkConfig.setFontFamily("consolas");
		return darkConfig;
	}
}