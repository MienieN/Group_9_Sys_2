package main.java.zenit.console;

import java.net.URL;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.kordamp.ikonli.javafx.FontIcon;

import com.kodedu.terminalfx.Terminal;
import com.kodedu.terminalfx.config.TerminalConfig;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import main.java.zenit.ConsoleRedirect;
import main.java.zenit.ui.MainController;

//TODO: create a separate class for the terminal configurations
/**
 * The ConsoleController class manages the interaction between console and terminal
 * components in the application. It handles initialization, creation, and switching
 * between consoles and terminals, as well as configuring their properties and
 * displaying corresponding UI elements.
 * <p>
 * The controller is responsible for managing actions, styles, user events for tabs,
 * and ensuring smooth navigation and usability between terminals and consoles. It
 * also allows updating and manipulating the state or content of consoles and terminals.
 * Implemented using JavaFX, it responds to JavaFX annotations and UI controls.
 * </p>
 */
public class ConsoleController implements Initializable {
	private MainController mainController;
	private final ArrayList<ConsoleArea> consoleList = new ArrayList<ConsoleArea>();
	private final ArrayList<Terminal> terminalList = new ArrayList<Terminal>();
	private AnchorPane terminalAnchorPane, consoleAnchorPane, noConsolePane;
	private ConsoleArea activeConsole;
	private Terminal activeTerminal;
	
	@FXML private TabPane consoleTabPane; //TODO: remove?
	@FXML private Button btnTerminal, btnConsole, btnNewTerminal,btnNewConsole, btnClearConsole;
	@FXML private ChoiceBox<ConsoleArea> consoleChoiceBox;
	@FXML private ChoiceBox<Terminal> terminalChoiceBox;
	@FXML private AnchorPane rootAnchor, rootNode;
	@FXML private FontIcon iconCloseConsoleInstance, iconTerminateProcess, iconCloseTerminalInstance;
	
	/**
	 * Sets the main controller for this ConsoleController. The main controller acts as a
	 * central manager to coordinate actions between different components.
	 *
	 * @param mainController the instance of MainController to be set as the main controller
	 */
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
		
	}
	
	/**
	 * Retrieves the list of stylesheets currently associated with the root node.
	 *
	 * @return a List of Strings representing the file paths or URLs of the stylesheets.
	 */
	public List<String> getStylesheets() {
		return rootNode.getStylesheets();
	}
	
	/**
	 * Switches the display to show console tabs and hides terminal tabs.
	 * <p>
	 * This method updates the styles of the console and terminal buttons to indicate the active tab.
	 * It hides the terminal choice box and shows the console choice box. The visibility and
	 * disable state of the relevant buttons and icons (such as new console, clear console, and
	 * terminate process) are updated accordingly to match the active console tab context.
	 * </p>
	 * <p>
	 * If the `consoleAnchorPane` is not null, it is brought to the front of the display hierarchy.
	 * Additionally, if there are no existing console entries in the `consoleList`, an empty console
	 * pane is created and displayed.
	 * </p>
	 */
	public void showConsoleTab() {
		btnTerminal.setStyle("");
		
		btnConsole.setStyle("-fx-text-fill:white; -fx-border-color:#666; -fx-border-width: 0 0 2 0;");
		
		terminalChoiceBox.setVisible(false);
		terminalChoiceBox.setDisable(true);
		
		consoleChoiceBox.setVisible(true);
		consoleChoiceBox.setDisable(false);
		
		btnNewTerminal.setVisible(false);
		
		btnNewConsole.setVisible(true);
		
		btnClearConsole.setDisable(false);
		btnClearConsole.setVisible(true);
		
		iconTerminateProcess.setVisible(true);
		iconTerminateProcess.setDisable(false);
		
		iconCloseConsoleInstance.setVisible(true);
		iconCloseConsoleInstance.setDisable(false);
		
		iconCloseTerminalInstance.setVisible(false);
		iconCloseTerminalInstance.setDisable(true);
		
		if (consoleAnchorPane != null) {
				consoleAnchorPane.toFront();
		}
		
		if(consoleList.size() == 0) {
			createEmptyConsolePane();
		}
	}
	
	/**
	 * Creates a placeholder console pane to display when there is no active console.
	 * <p>
	 * This method initializes an AnchorPane named `noConsolePane`, sets its dimensions and properties
	 * to fill its parent anchor pane, and populates it with a centered label displaying the message
	 * "No Console To Display". The label is styled with a font size of 14 and black text color.
	 * </p>
	 * <p>
	 * The `noConsolePane` is assigned a unique identifier "empty" for styling or identification purposes
	 * and is added to `rootAnchor`, bringing it to the front of the display hierarchy.
	 * </p>
	 */
	private void createEmptyConsolePane() {
		noConsolePane = new AnchorPane();
		fillAnchor(noConsolePane);
		
		Label label = new Label("No Console To Display");
		noConsolePane.getChildren().add(label);
		
		label.setFont(new Font(14));
		label.setTextFill(Color.BLACK);
		label.setMaxWidth(Double.MAX_VALUE);
		
		AnchorPane.setLeftAnchor(label, 0.0);
		AnchorPane.setRightAnchor(label, 0.0);
		
		label.setAlignment(Pos.CENTER);
		
		noConsolePane.setId("empty");
		rootAnchor.getChildren().add(noConsolePane);
		
		noConsolePane.toFront();
	}
	
	/**
	 * Switches the display to show terminal tabs and hides console tabs.
	 * <p>
	 * This method sets the styles of the terminal and console buttons to indicate the active tab,
	 * hides the console choice box, and shows the terminal choice box. It also toggles the visibility
	 * of the new console and terminal buttons, as well as the clear console and terminate process icons.
	 * The method ensures that the terminal anchor pane is brought to the front of the display hierarchy.
	 * </p>
	 */
	public void showTerminalTabs() {
		btnConsole.setStyle("");
		btnTerminal.setStyle("-fx-text-fill:white; -fx-border-color:#666; -fx-border-width: 0 0 2 0;");
		
		if(terminalList.size() == 0) {
			newTerminal();
		}
		else {
			terminalAnchorPane.toFront();
		}
		
		consoleChoiceBox.setVisible(false);
		consoleChoiceBox.setDisable(true);
		
		terminalChoiceBox.setVisible(true);
		terminalChoiceBox.setDisable(false);
		
		btnNewTerminal.setVisible(true);
		
		btnNewConsole.setVisible(false);
		
		btnClearConsole.setDisable(true);
		btnClearConsole.setVisible(false);
		
		iconTerminateProcess.setVisible(false);
		iconTerminateProcess.setDisable(true);
		
		iconCloseConsoleInstance.setVisible(false);
		iconCloseConsoleInstance.setDisable(true);
		
		iconCloseTerminalInstance.setVisible(true);
		iconCloseTerminalInstance.setDisable(false);
	}
	
	/**
	 * Creates and configures a new console area component within the application.
	 * <br>
	 * This method initializes a new `AnchorPane` to host the provided `ConsoleArea`, sets specific
	 * IDs to the components for style or reference purposes, and adjusts their layout properties
	 * using the `fillAnchor` method. The newly created console area is added to the `consoleList`
	 * and displayed in the `consoleChoiceBox`. The `ConsoleRedirect` object is instantiated to handle
	 * output redirection for the newly added console area. It also ensures the console tabs are visible.
	 *
	 * @param consoleArea the `ConsoleArea` instance to be initialized and added to the application.
	 *                    This object represents the interactive area for console output.
	 */
	public void newConsole(ConsoleArea consoleArea) {
		consoleAnchorPane = new AnchorPane();
		consoleArea.setId("consoleArea");
		consoleAnchorPane.setId("consoleAnchor");
		
		fillAnchor(consoleArea);
		fillAnchor(consoleAnchorPane);
		
		consoleAnchorPane.getChildren().add(consoleArea);
		rootAnchor.getChildren().add(consoleAnchorPane);
		
		consoleList.add(consoleArea);
		
		consoleChoiceBox.getItems().add(consoleArea);
		consoleChoiceBox.getSelectionModel().select(consoleArea);
		
		new ConsoleRedirect(consoleArea);	
		showConsoleTab();
	}
	
	/**
	 * Creates and adds a new terminal instance to the application's interface.
	 * <p>
	 * This method initializes a Terminal object with a configuration created by
	 * the `createTerminalConfig` method and sets its associated properties such as
	 * unique identifier and styling. Additionally, a new AnchorPane is created
	 * to host the terminal, which is styled and arranged to fill its parent pane
	 * using the `fillAnchor` method.
	 * </p>
	 * <p>
	 * The new terminal is added to the respective data structures (e.g., `terminalList`)
	 * and UI elements (e.g., `terminalChoiceBox`) for management and interaction.
	 * It also updates the UI to display terminal-related components.
	 * </p>
	 */
	public void newTerminal() {
		Terminal terminal = new Terminal(createTerminalConfig(), FileSystems.getDefault().getPath(".").toAbsolutePath());
		terminal.setId("Terminal ("+terminalList.size()+")");
		
		terminalAnchorPane = new AnchorPane();
		terminalAnchorPane.setStyle("-fx-background-color:black");
		
		terminal.setMinHeight(5);
		fillAnchor(terminal);
		fillAnchor(terminalAnchorPane);
		
		terminalAnchorPane.getChildren().add(terminal);
		rootAnchor.getChildren().add(terminalAnchorPane);
		terminalList.add(terminal);
		terminalChoiceBox.getItems().add(terminal);
		terminalChoiceBox.getSelectionModel().select(terminal);
		
		showTerminalTabs();
	}
	
	/**
	 * Creates and configures a TerminalConfig instance with predefined settings such as background color,
	 * foreground color, cursor settings, font family, font size, and scrollbar visibility. If the current OS
	 * is determined to be Windows-based, the configured instance is returned; otherwise, a new default
	 * TerminalConfig instance is returned.
	 *
	 * @return a TerminalConfig object with custom configurations for Windows or a default TerminalConfig instance for other operating systems.
	 */
	private TerminalConfig createTerminalConfig() {
		TerminalConfig windowsConfig = new TerminalConfig();
		windowsConfig.setBackgroundColor(Color.BLACK);
		windowsConfig.setForegroundColor(Color.WHITE);
		windowsConfig.setCursorBlink(true);
		windowsConfig.setCursorColor(Color.WHITE);
		windowsConfig.setFontFamily("consolas");
		windowsConfig.setFontSize(12);
		windowsConfig.setScrollbarVisible(false);

		return (System.getProperty("os.name").startsWith("W") ? windowsConfig : new TerminalConfig());
	}
	
	/**
	 * Sets all anchor constraints of the specified Node to fill its parent AnchorPane completely.
	 *
	 * @param node the Node to which top, right, bottom, and left anchor constraints will be set to 0.0
	 */
	public void fillAnchor(Node node) {
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
	}
	
	//TODO: try using the original method rather than having to repeat this here
	/**
	 * Clears the content of the currently active console.
	 * <p>
	 * This method uses the `clear()` method of the `activeConsole` field
	 * to completely remove all text or output displayed in the active console instance.
	 * It is typically invoked when the user wants to reset the active console view.
	 * </p>
	 * <p>
	 * Assumes that `activeConsole` is not null. If `activeConsole` is null,
	 * invoking this method will result in a `NullPointerException`.
	 * </p>
	 */
	public void clearConsole() {
		activeConsole.clear();
	}
	
	//TODO: try using the original method rather than having to repeat this here
	/**
	 * Closes the currently active console component.
	 * <p>
	 * This method removes the active console from the display hierarchy by removing its parent
	 * anchor pane from the root anchor. It also removes the active console from the `consoleList`
	 * and the console choice box. If there are no remaining consoles in the `consoleList`, an empty
	 * console pane is created and displayed. The method ensures that the console choice box selects
	 * the last remaining console entry after closing the active console.
	 * </p>
	 */
	public void closeComponent() {
		mainController.closeConsoleComponent();
	}
	
	/**
	 * Changes the background color of all console areas managed within the ConsoleController.
	 * This method iterates through a list of console areas and sets their background
	 * color to the specified value.
	 *
	 * @param color the new background color to be applied to all console areas.
	 *              This should be a valid color string (e.g., CSS color codes).
	 */
	public void changeAllConsoleAreaBackgroundColors(String color) {
		for(ConsoleArea c : consoleList) {
			c.setBackgroundColor(color);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		consoleChoiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
						
			if(newValue != null) {
				for(ConsoleArea console : consoleList) {
					if(newValue.equals(console)) {
						console.getParent().toFront();
						activeConsole = console;
					}
				}
			}
			
		});
		
		terminalChoiceBox.getSelectionModel().selectedItemProperty().addListener( (v, oldValue, newValue) -> {
			if(newValue != null) {
				for(Terminal t : terminalList) {
					if(newValue.equals(t)) {
						t.getParent().toFront();
						activeTerminal = t;
						t.onTerminalFxReady(()-> {
							t.focusCursor();
						});
					}
				}
			}
		});
		
		showConsoleTab();
		
		//Console
		iconCloseConsoleInstance.setOnMouseClicked(e -> {
				rootAnchor.getChildren().remove(activeConsole.getParent());
				consoleList.remove(activeConsole);
				consoleChoiceBox.getItems().remove(activeConsole);
				consoleChoiceBox.getSelectionModel().selectLast();

				if(consoleList.size() == 0) {
					createEmptyConsolePane();
				}
		});
		
		//Terminal
		iconCloseTerminalInstance.setOnMouseClicked(e ->{
			
			if(terminalList.size() > 1) {
				rootAnchor.getChildren().remove(activeTerminal.getParent());
				terminalList.remove(activeTerminal);
				terminalChoiceBox.getItems().remove(activeTerminal);
				terminalChoiceBox.getSelectionModel().selectLast();
			}
		});
		
		
		btnNewConsole.setOnMouseClicked(e -> {
			if(mainController.isDarkmode()) {
				newConsole(new ConsoleArea("Console(" + consoleList.size() + ")", null, "-fx-background-color:#444"));
			}else {
				newConsole(new ConsoleArea("Console(" + consoleList.size() + ")", null, "-fx-background-color:#989898"));
			}
		});
		
		iconTerminateProcess.setOnMouseClicked(e -> {
			for(var item : consoleList) {
				if(item.equals(activeConsole)) {
					if(item != null) {
						item.getProcess().destroy();
					}	
				}
			}
							
		});
	}
}