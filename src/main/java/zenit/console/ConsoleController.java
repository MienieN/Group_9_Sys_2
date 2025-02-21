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
	
	// Console Methods:
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
	public void createNewConsoleArea(ConsoleArea consoleArea) {
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
		
		if(consoleList.isEmpty()) {
			createEmptyConsolePane();
		}
	}
	
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
	
	
	// Terminal Methods:
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
	
	
	// Utility Methods:
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
	
	/**
	 * <p>
	 * Initializes the ConsoleController by setting up necessary event listeners and handlers,
	 * and ensuring the UI is prepared to display console and terminal interactions.
	 * This method is invoked automatically after the controller is loaded by the FXMLLoader.
	 * </p>
	 * @param location the location used to resolve relative paths for the root object,
	 *                 or null if the location is not known.
	 * @param resources the resources used to localize the root object,
	 *                  or null if the root object was not localized.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setupConsoleChoiceBoxListener();	// Sets up listener for changes in the console choice box
		setupTerminalChoiceBoxListener();	// Sets up listener for changes in the terminal choice box
		closeConsoleButtonHandler();		// Configures the handler for closing the currently selected console instance
		closeTerminalButtonHandler();	    // Configures the handler for closing the currently selected terminal instance
		createNewConsoleAreaHandler();		// Configures the handler for creating a new console instance
		terminateProcessHandler();			// Configures the handler for terminating the process in the active console
		showConsoleTab();					// Ensures that the console tab is displayed initially
	}
	
	/**
	 * Sets up a listener for the `consoleChoiceBox` to monitor changes in the selected console.
	 *<p>
	 * When the user selects a new console from the choice box, this listener ensures that the
	 * selected console becomes the active console. The method uses the `selectedItemProperty()`
	 * of the `consoleChoiceBox`'s `SelectionModel` to add a change listener that updates the
	 * active console to the newly selected console.
	 *	</p>
	 *	<p>
	 * If the new console is not null, the `setActiveConsole` method is invoked to update
	 * the application's internal state and display to reflect the newly selected console.
	 * </p>
	 */
	private void setupConsoleChoiceBoxListener(){
		consoleChoiceBox.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldConsole, newConsole) -> {
			if (newConsole != null) {
				setAsActiveConsole(newConsole);
			}
		});
	}
	
	/**
	 * Sets the specified console as the active console in the application.
	 * <p>
	 * This method iterates through the list of available consoles (`consoleList`)
	 * and identifies the console that matches the given `newConsole` parameter.
	 * Once identified, the parent node of that console is brought to the front of the
	 * display hierarchy, and the selected console is assigned to the `activeConsole` field.
	 * This method ensures that only one console is actively displayed and interacted with at a time.
	 *
	 * @param newConsole the console to be activated. Must match an existing console
	 *                   in the `consoleList`. If the specified console is not found,
	 *                   the method does nothing.
	 */
	private void setAsActiveConsole (ConsoleArea newConsole) {
		for (ConsoleArea console : consoleList) {
			if (newConsole.equals(console)) {
				console.getParent().toFront();
				activeConsole = console;
				break;
			}
		}
	}
	
	/**
	 * Configures a listener for the terminalChoiceBox to handle changes in the selection.
	 * When the selected item changes, this listener updates the active terminal
	 * by invoking the setActiveTerminal method with the newly selected terminal.
	 */
	private void setupTerminalChoiceBoxListener() {
		terminalChoiceBox.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldTerminal, newTerminal) -> {
			if (newTerminal != null) {
				setAsActiveTerminal(newTerminal);
			}
		});
	}
	
	/**
	 * Sets the given terminal as the active terminal. This method iterates through
	 * the list of terminals and, if a match is found, brings the parent window of
	 * the terminal to the front, updates the active terminal reference, and focuses
	 * the cursor in the terminal.
	 *
	 * @param selectedTerminal the terminal to be set as the active terminal
	 */
	private void setAsActiveTerminal (Terminal selectedTerminal) {
		for (Terminal terminal : terminalList) {
			if (selectedTerminal.equals(terminal)) {
				terminal.getParent().toFront();
				activeTerminal = terminal;
				terminal.focusCursor();
				break;
			}
		}
	}
	
	/**
	 * Sets up the event handler for the close console icon. When the close icon is clicked,
	 * the currently active console is removed from the UI and the list of consoles. The
	 * console dropdown is updated accordingly. If all consoles are removed, a new empty
	 * console pane will be created automatically to maintain the UI structure.
	 */
	private void closeConsoleButtonHandler () {
		iconCloseConsoleInstance.setOnMouseClicked(event -> {
			if (activeConsole != null) {
				rootAnchor.getChildren().remove(activeConsole.getParent());
				consoleList.remove(activeConsole);
				consoleChoiceBox.getItems().remove(activeConsole);
				consoleChoiceBox.getSelectionModel().selectLast();
				if (consoleList.isEmpty()) {
					createEmptyConsolePane();
				}
			}
		});
	}
	
	/**
	 * Handles the event for closing the currently active terminal instance when the close button is clicked.
	 * If the active terminal is not null and there is more than one terminal in the terminal list,
	 * the method removes the active terminal's parent node from the root container, removes the active terminal
	 * from the terminal list, and updates the terminal selection choice box to exclude the removed terminal.
	 * The selection is then set to the last terminal in the choice box.
	 */
	private void closeTerminalButtonHandler () {
		iconCloseTerminalInstance.setOnMouseClicked(event -> {
			if (activeTerminal != null && terminalList.size() > 1) {
				rootAnchor.getChildren().remove(activeTerminal.getParent());
				terminalList.remove(activeTerminal);
				terminalChoiceBox.getItems().remove(activeTerminal);
				terminalChoiceBox.getSelectionModel().selectLast();
			}
		});
	}
	
	/**
	 * Sets up a new console handler by configuring the action to be performed
	 * when the associated button is clicked. When triggered, a new console area
	 * is created with a dynamically assigned name, background color based on
	 * the application's current theme, and added to the list of consoles.
	 * <p>
	 * This method determines the appropriate background color for the new
	 * console area depending on whether dark mode is enabled in the main controller.
	 * It assigns a mouse click event handler to the associated button to execute
	 * the creation and addition process for the new console area.
	 * </p>
	 */
	private void createNewConsoleAreaHandler () {
		btnNewConsole.setOnMouseClicked(event -> {
			String backgroundColor = mainController.isDarkmode() ? "-fx-background-color:#444" : "-fx-background-color:#989898";
			createNewConsoleArea(new ConsoleArea("Console(" + consoleList.size() + ")", null, backgroundColor));
		});
	}
	
	/**
	 * Sets up the event handler for terminating a process when the terminate icon is clicked.
	 * <p>
	 * This method assigns a mouse click event listener to the terminate icon. When the icon
	 * is clicked, it iterates through the list of console items to find the active console.
	 * Upon locating the active console, the associated process is terminated by invoking
	 * the destroy method.
	 * </p>
	 */
	private void terminateProcessHandler () {
		iconTerminateProcess.setOnMouseClicked(event -> {
			for(var item : consoleList) {
				if(item.equals(activeConsole)) {
                    item.getProcess().destroy();
                }
			}
		});
	}
	
	
	// Getters:
	/**
	 * Retrieves the list of stylesheets currently associated with the root node.
	 *
	 * @return a List of Strings representing the file paths or URLs of the stylesheets.
	 */
	public List<String> getStylesheets() {
		return rootNode.getStylesheets();
	}
	
	
	// Setters:
	/**
	 * Sets the main controller for this ConsoleController. The main controller acts as a
	 * central manager to coordinate actions between different components.
	 *
	 * @param mainController the instance of MainController to be set as the main controller
	 */
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}
}