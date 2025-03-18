package main.java.zenit.settingspanel;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.controlsfx.control.ToggleSwitch;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.java.zenit.console.ConsoleController;
import main.java.zenit.ui.MainController;
import main.java.zenit.zencodearea.ZenCodeArea;

public class SettingsPanelController extends AnchorPane implements ThemeCustomizable{
	private String oldFont;
	private File customThemeCSS;
	private LinkedList<String> addedCSSLines;
	private List<ThemeCustomizable> stages;
	private Stage window;
	private MainController mainController;
	private ConsoleController consoleController;
	private CustomCSSThemeHandler themeHandler;
	private OS operatingSystem;
	private String settingsPanelDarkMode = getClass().getResource(
			"/zenit/settingspanel/settingspanelDarkMode.css").toExternalForm();
	private String settingsPanelLightMode = getClass().getResource(
			"/zenit/settingspanel/settingspanelLightMode.css").toExternalForm();
	private int oldSize;
	private boolean isCustomTheme = false, isDarkMode = true;
	private enum OS { MACOS, WINDOWS, LINUX }

	@FXML private TextField fieldNewSize;
	@FXML private Slider sliderNewSize;
	@FXML private Label lblOldTextSize, lblOldFont, lblCurrentJavaHome,
			newJavaHome, lblTextAppearanceSize;
	@FXML private ChoiceBox<String> choiceBoxNewFont;
	@FXML private Button btnTextAppearance, btnJavaHome, btnSupport,
			btnTheme, btnCustomCSS, btnCustomTheme;
	@FXML private Hyperlink linkOpenInGitHub, linkSubmitIssue, linkDownloadSource;
	@FXML private ToggleSwitch toggleDarkMode, toggleSwitchCustomTheme;
	@FXML private ListView listViewAddedCSS;
	@FXML private ColorPicker colorPickerPrimaryColor, colorPickerPrimaryTint,
			colorPickerSecondaryColor, colorPickerSecondaryTint;
	@FXML private AnchorPane panelTextAppearance, panelJavaHome, panelSupport,
			panelTheme, panelCustomCSS, panelCustomTheme;
	@FXML
	private AnchorPane pnlTextAppearance;

	@FXML
	private AnchorPane pnlJavaHome;

	@FXML
	private AnchorPane pnlSupport;

	@FXML
	private AnchorPane pnlTheme;

	@FXML
	private AnchorPane pnlCustomCSS;

	@FXML
	private AnchorPane pnlCustomTheme;


	/**
	 * Creates a new instance of SettingsPanelController with the provided parameters.
	 *
	 * @param mainController the main controller instance
	 * @param oldFontSize the old font size value
	 * @param oldFontFamily the old font family value
	 * @param consoleController the console controller instance
	 */
	public SettingsPanelController(MainController mainController, int oldFontSize, String oldFontFamily, ConsoleController consoleController) {
		this.mainController = mainController;
		this.consoleController = consoleController;
		fieldNewSize = new TextField();
		sliderNewSize = new Slider();
		choiceBoxNewFont = new ChoiceBox<>();

		oldSize = oldFontSize;
		oldFont = oldFontFamily;
		addedCSSLines = new LinkedList<String>();
		setLoader();
		setWindow();
		setStage();
	}

	/**
	 * Sets up the loader for loading the SettingsPanel.fxml file and binding it to the controller.
	 *
	 * @return true if the loader setup was successful, false otherwise
	 */
	private void setLoader() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/settingspanel/SettingsPanel.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			System.out.println("Error loading SettingsPanel.fxml: " + e.getMessage());
		}
	}

	/**
	 * Sets up the window for displaying the preferences panel.
	 *
	 * @return true if the window setup was successful, false otherwise
	 */
	private void setWindow() {
		window = new Stage();
		Scene scene = new Scene(this);
		window.setScene(scene);
		window.setTitle("Preferences");
		initialize();
		//scene.getStylesheets().add(getClass().getResource("/zenit/settingspanel/settingspanelDarkMode.css").toString());

		darkModeChanged(mainController.isDarkmode());
		window.show();

	}

	/**
	 * Sets up the theme customization for the settings panel.
	 *
	 * @return true if the stage setup was successful, false otherwise
	 */
	private void setStage() {
		this.customThemeCSS = new File("/customtheme/settingspanelCustomTheme.css");
		stages = new  ArrayList<ThemeCustomizable>();
		stages.add(mainController);
		stages.add(this);
		themeHandler = new CustomCSSThemeHandler(stages);
	}

	/**
	 * Sets a new font for the choice box and the main controller.
	 *
	 * @param newFont the new font to be set
	 */
	public void setNewFont(String newFont) {
		choiceBoxNewFont.setValue(newFont);
		mainController.setFontFamily(newFont);
	}

	/**
	 * Sets a new font size for the text in the application.
	 *
	 * @param newFontSize the new font size to be set
	 */
	public void setNewFontSize(long newFontSize) {
		long size = newFontSize;
		fieldNewSize.textProperty().setValue(String.valueOf(size));
		if(size > 100) {
			size = 100;
		}
		else if(size < 6) {
			size = 6;
		}
		sliderNewSize.setValue(size);
		mainController.setFontSize((int)size);//this.codeArea.setFontSize((int)size);
	}


	public void panelToFront(Event e) {
		if(e.getSource() == btnTextAppearance) {
			pnlTextAppearance.toFront();
		}
		else if(e.getSource() == btnJavaHome) {
			pnlJavaHome.toFront();
		}
		else if(e.getSource() == btnSupport) {
			pnlSupport.toFront();
		}
		else if(e.getSource() == btnTheme) {
			pnlTheme.toFront();
		}
		else if(e.getSource() == btnCustomCSS) {
			pnlCustomCSS.toFront();
		}
		else if(e.getSource() == btnCustomTheme) {
			pnlCustomTheme.toFront();
		}
	}



	@FXML
	private void setNewJavaHome() {
		/*
		 * TODO REMOVE (Who made this comment, what are we removing and why?)
		 */
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(window);

		if(selectedDirectory == null){
			//No Directory selected
		}
		else{
			ProcessBuilder processBuilder = new ProcessBuilder();
			Map<String, String> environment = processBuilder.environment();
			environment.put("JAVA_HOME", selectedDirectory.getAbsolutePath());
			try {
				Process process = processBuilder.start();
				Thread.sleep(100);
				newJavaHome.setText(System.getenv("JAVA_HOME"));
				newJavaHome.setStyle("-fx-text-fill: #0B6623;");
			} catch (Exception e) {
				System.out.println("Error opening Java Home: " + e.getMessage());
			}
		}
	}

	@FXML
	private void addCSSLine() {
		// TODO Why is the logic commented out, but not the method itself, and why is it still here then? - Yrja
       /*String CSSLine = fldCSSLineInput.getText();
       try {
          Scene mockScene = new Scene(new Region());
          mockScene.getRoot().setStyle(CSSLine);

          String allCustomLinesOfCSS = "";
          addedCSSLines.addFirst(CSSLine);

          for(int i = 0; i < addedCSSLines.size(); i++) {
             allCustomLinesOfCSS += addedCSSLines.get(i);
          }
          this.window.getScene().getRoot().setStyle(allCustomLinesOfCSS);
          updateCustomCSSListView();
       }
       catch(Exception e) {
          e.printStackTrace();
       }*/
	}

	/**
	 * Updates the custom CSS list view with the current list of added CSS lines.
	 * Clears the existing items in the list view and populates it with new CustomCSSListItem objects
	 * based on the addedCSSLines list.
	 */
	private void updateCustomCSSListView() {
		listViewAddedCSS.getItems().clear();
		for (String addedCSSLine : addedCSSLines) {
			listViewAddedCSS.getItems().add(new CustomCSSListItem(addedCSSLine));
		}
	}




	/**
	 * Opens the specified URL in the default web browser.
	 *
	 * @param e The Event triggering the method.
	 */
	@FXML
	private void openLinkInBrowserEvent(Event e) {
		/**
		 * Represents a map that links Hyperlink objects to their corresponding URLs.
		 * The linkMap is initialized with predefined Hyperlink objects and their associated URLs.
		 * Key: Hyperlink object
		 * Value: String representing the URL
		 */
		Map<Hyperlink, String> linkMap = Map.of(
				linkOpenInGitHub, "https://github.com/strazan/zenit",
				linkSubmitIssue, "https://github.com/strazan/zenit/issues/new",
				linkDownloadSource, "https://github.com/strazan/zenit/archive/develop.zip"
		);
		String url = linkMap.get(e.getSource());

		if (url != null) {
			openInBrowser(url);
		}
	}

	/**
	 * Opens the specified URL in the default web browser based on the operating system.
	 *
	 * @param url the URL to be opened in the browser
	 */
	private void openInBrowser(String url) {
		String command;
		switch(operatingSystem) {
			case LINUX:
				command = "xdg-open ";
				break;
			case MACOS:
				command = "open ";
				break;
			case WINDOWS:
				command = "rundll32 url.dll,FileProtocolHandler ";
				break;
			default:
				System.err.println("Unsupported operating system. Open link manually in browser:\n" + url );
				return;
		}

		try {
			Runtime.getRuntime().exec(command + url);
		} catch (IOException e) {
			System.out.println("Error opening link: " + e.getMessage());
		}
	}

	/**
	 * Handles the change in dark mode for the application, updating stylesheets and settings accordingly.
	 *
	 * @param isDarkMode true if the dark mode is enabled, false otherwise
	 */
	private void darkModeChanged(boolean isDarkMode) {
		if(!isCustomTheme) {
			var stylesheets = this.mainController.getStage().getScene().getStylesheets();
			var settingsPanelStylesheets = window.getScene().getStylesheets();
			var lightMode = getClass().getResource("/zenit/ui/mainStyle-lm.css").toExternalForm();
			var darkMode = getClass().getResource("/zenit/ui/mainStyle.css").toExternalForm();
			var darkModeKeywords = ZenCodeArea.class.getResource("/zenit/ui/keywords.css").toExternalForm();
			var lightModeKeywords = ZenCodeArea.class.getResource("/zenit/ui/keywords-lm.css").toExternalForm();
			var darkModeConsole = getClass().getResource("/zenit/console/consoleStyle.css").toExternalForm();
			var lightModeConsole = getClass().getResource("/zenit/console/consoleStyleLight.css").toExternalForm();

			if (isDarkMode) {
				settingsPanelStylesheets.clear();
				settingsPanelStylesheets.add(settingsPanelDarkMode);
				consoleController.getStylesheets().remove(lightModeConsole);
				consoleController.getStylesheets().add(darkModeConsole);
				consoleController.changeAllConsoleAreaBackgroundColors("-fx-background-color:#444");

				stylesheets.clear();
				stylesheets.add(darkModeKeywords);
				stylesheets.add(darkMode);
			} else {
				settingsPanelStylesheets.clear();
				settingsPanelStylesheets.add(settingsPanelLightMode);
				consoleController.getStylesheets().remove(darkModeConsole);
				consoleController.getStylesheets().add(lightModeConsole);
				consoleController.changeAllConsoleAreaBackgroundColors("-fx-background-color:#989898");

				stylesheets.clear();
				stylesheets.add(lightModeKeywords);
				stylesheets.add(lightMode);
			}
		}
		this.isDarkMode = isDarkMode;
		mainController.setDarkmode(this.isDarkMode);
	}

	/**
	 * Initializes the Settings Panel view by setting up various components and listeners.
	 * It initializes the text fields, sliders, labels, choice boxes, Java Home path, operating system, dark mode toggle,
	 * custom theme toggle, CSS list view, primary and secondary color pickers, and event handlers for theme customization.
	 */
	private void initialize() {
		lblOldTextSize.setText(String.valueOf(oldSize));
		fieldNewSize.setText(String.valueOf(oldSize));
		sliderNewSize.setValue(oldSize);
		createSliderListener();
		createFieldListener();
		createChoiceBoxListener();
		lblCurrentJavaHome.setText(System.getenv("JAVA_HOME"));
		fieldNewSize.setAlignment(Pos.CENTER_RIGHT);
		setOperatingSystem();
		toggleDarkMode.setSelected(mainController.isDarkmode());

		setToggleDarkModeListener();
		setToggleSwitchCustomThemeListener();

		listViewAddedCSS.getItems().add(new AnchorPane());

		setPrimaryColorEventHandler();
		setPrimaryTintEventHandler();
		setSecondaryColorEventHandler();
		setSecondaryTintEventHandler();
	}

	/**
	 * Sets the event handler for changing the secondary tint color in the color picker.
	 * This method listens for changes in the color picker's secondary tint value and updates the theme
	 * by changing the secondary tint color.
	 *
	 * @return true if the event handler was set successfully
	 */
	private boolean setSecondaryTintEventHandler() {
		colorPickerSecondaryTint.setOnAction((event) -> {
			Platform.runLater(() -> {
				themeHandler.changeColor(colorPickerSecondaryTint.getValue(),
						CustomColor.secondaryTint);
			});
		});
		return true;
	}

	/**
	 * Sets the event handler for changing the secondary color in the color picker.
	 *
	 * @return true if the event handler was set successfully
	 */
	private boolean setSecondaryColorEventHandler() {
		colorPickerSecondaryColor.setOnAction((event) -> {
			Platform.runLater(() -> {
				themeHandler.changeColor(colorPickerSecondaryColor.getValue(),
						CustomColor.secondaryColor);
			});
		});
		return true;
	}

	/**
	 * Sets the event handler for changing the primary tint color in the color picker.
	 *
	 * @return true if the event handler was set successfully
	 */
	private boolean setPrimaryTintEventHandler() {
		colorPickerPrimaryTint.setOnAction((event) -> {
			Platform.runLater(() -> {
				themeHandler.changeColor(colorPickerPrimaryTint.getValue(),
						CustomColor.primaryTint);
			});
		});
		return true;
	}

	/**
	 * Sets the event handler for changing the primary color in the color picker.
	 *
	 * @return true if the event handler was set successfully
	 */
	private boolean setPrimaryColorEventHandler() {
		colorPickerPrimaryColor.setOnAction((event) -> {
			Platform.runLater(() -> {
				themeHandler.changeColor(colorPickerPrimaryColor.getValue(),
						CustomColor.primaryColor);
			});
		});
		return true;
	}

	/**
	 * Sets a listener for the toggle switch representing a custom theme.
	 * When the toggle switch value changes, it invokes necessary methods to handle theme changes.
	 *
	 * @return true if the toggle switch listener was successfully set
	 */
	private boolean setToggleSwitchCustomThemeListener() {
		toggleSwitchCustomTheme.selectedProperty().addListener(new ChangeListener <Boolean> () {
			@Override
			public void changed(
					ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				themeHandler.toggleCustomTheme(toggleSwitchCustomTheme.isSelected());
				isCustomTheme = toggleSwitchCustomTheme.isSelected();
				darkModeChanged(toggleDarkMode.isSelected());
			}
		});
		return true;
	}

	/**
	 * Sets a listener for the dark mode toggle switch.
	 * When the toggle switch value changes, it invokes the darkModeChanged method with the updated value.
	 *
	 * @return true if the toggle switch listener was successfully set
	 */
	private boolean setToggleDarkModeListener() {
		toggleDarkMode.selectedProperty().addListener(new ChangeListener <Boolean> () {
			@Override
			public void changed(
					ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
			{
				darkModeChanged(newValue);
			}
		});
		return true;
	}

	/**
	 * Sets the operating system based on the current system properties.
	 *
	 * @return true if the operating system was successfully set, false otherwise
	 */
	private boolean setOperatingSystem() {
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("win")) {
			operatingSystem = OS.WINDOWS;
			return true;
		}
		else if(os.contains("mac")) {
			operatingSystem = OS.MACOS;
			return true;
		}
		else if(os.contains("nix") || os.contains("nux")) {
			operatingSystem = OS.LINUX;
			return true;
		}
		return false;
	}

	/**
	 * Populates the choice box with a list of fonts, sets the initial value to the old font,
	 * updates the label with the old font, and sets up a listener to change the font when a new one is selected.
	 *
	 * @return true if the choice box listener was successfully created
	 */
	private boolean createChoiceBoxListener() {
		List<String> fonts = Font.getFamilies();
		for (String font : fonts) {
			choiceBoxNewFont.getItems().add(font);
		}
		choiceBoxNewFont.setValue(oldFont);
		lblOldFont.setText(oldFont);
		choiceBoxNewFont.getSelectionModel().selectedItemProperty().addListener((arg0, arg1, arg2) -> {
			setNewFont(arg2);
		});
		return true;
	}

	/**
	 * Adds a listener to the text property of fieldNewSize to update the font size when it changes.
	 *
	 * @return true if the field listener was successfully created
	 */
	private boolean createFieldListener() {
		fieldNewSize.textProperty().addListener((arg0, arg1, arg2) -> {
			try {
				setNewFontSize(Long.parseLong(fieldNewSize.getText()));
			} catch(NumberFormatException e) {
				System.out.println("Error parsing new font size: " + e.getMessage());
			}
		});
		return true;
	}

	/**
	 * Adds a change listener to the sliderNewSize property to update the new font size
	 * when the value changes.
	 *
	 * @return true if the slider listener was successfully created, false otherwise
	 */
	private boolean createSliderListener() {
		sliderNewSize.valueProperty().addListener(
				(ChangeListener<? super Number>) (arg0, arg1, arg2) -> {
					setNewFontSize(Math.round(sliderNewSize.getValue()));
				});
		return true;
	}

	/**
	 * Retrieves the Stage instance associated with this object.
	 *
	 * @return the Stage instance representing the window
	 */
	public Stage getStage() {
		return this.window;
	}

	/**
	 * Retrieves the custom theme CSS file associated with this object.
	 *
	 * @return the custom theme CSS file
	 */
	public File getCustomThemeCSS() {
		return this.customThemeCSS;
	}

	/**
	 * Retrieves the active stylesheet based on the current dark mode setting.
	 *
	 * @return the active stylesheet path, either for dark mode or light mode
	 */
	public String getActiveStylesheet() {
		if(isDarkMode) {
			return settingsPanelDarkMode;
		}
		else {
			return settingsPanelLightMode;
		}
	}
}

