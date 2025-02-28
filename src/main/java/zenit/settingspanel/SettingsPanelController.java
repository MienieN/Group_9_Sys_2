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
	private boolean setLoader() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/settingspanel/SettingsPanel.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (IOException e) {
			System.out.println("Error loading SettingsPanel.fxml: " + e.getMessage());
		}
		return true;
	}

	/**
	 * Sets up the window for displaying the preferences panel.
	 *
	 * @return true if the window setup was successful, false otherwise
	 */
	private boolean setWindow() {
		window = new Stage();
		Scene scene = new Scene(this);
		window.setScene(scene);
		window.setTitle("Preferences");
		initialize();
		//scene.getStylesheets().add(getClass().getResource("/zenit/settingspanel/settingspanelDarkMode.css").toString());

		darkModeChanged(mainController.isDarkmode());
		window.show();

		return true;
	}

	/**
	 * Sets up the theme customization for the settings panel.
	 *
	 * @return true if the stage setup was successful, false otherwise
	 */
	private boolean setStage() {
		this.customThemeCSS = new File("/customtheme/settingspanelCustomTheme.css");
		stages = new  ArrayList<ThemeCustomizable>();
		stages.add(mainController);
		stages.add(this);
		themeHandler = new CustomCSSThemeHandler(stages);
		return true;
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

	/**
	 * A map that associates button actions with corresponding Runnable implementations.
	 * The keys are instances of Button enum and the values are Runnable instances.
	 */
	Map<Button, Runnable> buttonActions = new HashMap<>() {{
		put(btnTextAppearance, () -> panelTextAppearance.toFront());
		put(btnJavaHome, () -> panelJavaHome.toFront());
		put(btnSupport, () -> panelSupport.toFront());
		put(btnTheme, () -> panelTheme.toFront());
		put(btnCustomCSS, () -> panelCustomCSS.toFront());
		put(btnCustomTheme, () -> panelCustomTheme.toFront());
	}};

	/**
	 * Brings the panel associated with the specified event to the front to make it visible.
	 *
	 * @param event the event triggering the action to bring the panel to the front
	 */
	public void panelToFront(Event event) {
		Runnable action = buttonActions.get(event.getSource());
		if (action != null) {
			action.run();
		}
	}
	
	@FXML
	private void setNewJavaHome() {
		/*
		 * TODO REMOVE
		 */
		DirectoryChooser directoryChooser = new DirectoryChooser();
		File selectedDirectory = directoryChooser.showDialog(window);

		if(selectedDirectory == null){
		     //No Directory selected
		}
		else{
			 ProcessBuilder pb = new ProcessBuilder();
			    Map<String, String> env = pb.environment();
			    env.put("JAVA_HOME", selectedDirectory.getAbsolutePath());
			    try {
					Process p = pb.start();
					Thread.sleep(100);
					newJavaHome.setText(System.getenv("JAVA_HOME"));
					newJavaHome.setStyle("-fx-text-fill: #0B6623;");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	@FXML
	private void addCSSLine() {
		
//		String CSSLine = fldCSSLineInput.getText();
//		try {
//			Scene mockScene = new Scene(new Region());
//			mockScene.getRoot().setStyle(CSSLine);
//			
//			String allCusomLinesOfCSS = "";
//			addedCSSLines.addFirst(CSSLine);
//			
//			for(int i = 0; i < addedCSSLines.size(); i++) {
//				allCusomLinesOfCSS += addedCSSLines.get(i);
//			}
//			this.window.getScene().getRoot().setStyle(allCusomLinesOfCSS);
//			
//			updateCustomCSSListView();
//		}
//		
//		catch(Exception e) {
//			e.printStackTrace();
//		}
	}

	private void updateCustomCSSListView() {
		listViewAddedCSS.getItems().clear();
		
		for(int i = 0; i < addedCSSLines.size(); i++) {
			listViewAddedCSS.getItems().add(new CustomCSSListItem(addedCSSLines.get(i)));
		}
	}
	
	@FXML
	private void openLinkInBrowserEvent(Event e) {	
		
		if(e.getSource() == linkOpenInGitHub) {
			openInBrowser("https://github.com/strazan/zenit");
		}
		if(e.getSource() == linkSubmitIssue) {
			openInBrowser("https://github.com/strazan/zenit/issues/new");
		}
		if(e.getSource() == linkDownloadSource) {
			openInBrowser("https://github.com/strazan/zenit/archive/develop.zip");
		}	
	}

	private void openInBrowser(String url) {
		Runtime rt = Runtime.getRuntime();
		
		switch(operatingSystem) {
			case LINUX:
			try {
				rt.exec("xdg-open " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				break;
				
			case MACOS:
			try {
				rt.exec("open " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				break;
				
			case WINDOWS:
			try {
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				break;
			default:
				System.err.println("OS not found. Open link manually in browser:\n" + url );
				break;
		}
	}

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

	private void initialize() {
		lblOldTextSize.setText(String.valueOf(oldSize));
		fieldNewSize.setText(String.valueOf(oldSize));
		sliderNewSize.setValue(oldSize);
		
		sliderNewSize.valueProperty().addListener(
			(ChangeListener<? super Number>) (arg0, arg1, arg2) -> {
				setNewFontSize(Math.round(sliderNewSize.getValue()));
		});
		
		fieldNewSize.textProperty().addListener((arg0, arg1, arg2) -> {
			try {  
				setNewFontSize(Long.parseLong(fieldNewSize.getText()));
			  } catch(NumberFormatException e){  
			e.printStackTrace();	 
			  }  
		});
		
		List<String> fonts = Font.getFamilies();
		
		for(int i = 0; i < fonts.size(); i++) {
			choiceBoxNewFont.getItems().add(fonts.get(i));
		}
		choiceBoxNewFont.setValue(oldFont);
		lblOldFont.setText(oldFont);
		choiceBoxNewFont.getSelectionModel().selectedItemProperty().addListener((arg0, arg1, arg2) -> {
			setNewFont(arg2);
		});
		
		lblCurrentJavaHome.setText(System.getenv("JAVA_HOME"));
		
		fieldNewSize.setAlignment(Pos.CENTER_RIGHT);
		
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("win") >= 0) {
			operatingSystem = OS.WINDOWS;
		}	
		else if(os.indexOf("mac") >= 0) {
			operatingSystem = OS.MACOS;
		}
		else if(os.indexOf("nix") >=0 || os.indexOf("nux") >=0) {
			operatingSystem = OS.LINUX;
		}
		
		toggleDarkMode.setSelected(mainController.isDarkmode());
		toggleDarkMode.selectedProperty().addListener(new ChangeListener <Boolean> () {
            @Override
			public void changed(
				ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
				darkModeChanged(newValue);
			}
        });
		
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
		
		listViewAddedCSS.getItems().add(new AnchorPane());

		colorPickerPrimaryColor.setOnAction((event) -> {
		     Platform.runLater(() -> {
		    	 themeHandler.changeColor(colorPickerPrimaryColor.getValue(),
		    		CustomColor.primaryColor);
			     });
		});	
		
		colorPickerPrimaryTint.setOnAction((event) -> {
	    	 Platform.runLater(() -> {
		    	 themeHandler.changeColor(colorPickerPrimaryTint.getValue(),
		    		CustomColor.primaryTint);
		     });
		});	
		
		colorPickerSecondaryColor.setOnAction((event) -> {
	    	 Platform.runLater(() -> {
		    	 themeHandler.changeColor(colorPickerSecondaryColor.getValue(),
				    CustomColor.secondaryColor);
		     });
		});	
		
		colorPickerSecondaryTint.setOnAction((event) -> {
	    	 Platform.runLater(() -> {
		    	 themeHandler.changeColor(colorPickerSecondaryTint.getValue(),
				    CustomColor.secondaryTint);
		     });
		});	
	}

	public Stage getStage() {
		return this.window;
	}

	public File getCustomThemeCSS() {
		return this.customThemeCSS;
	}

	public String getActiveStylesheet() {
	
		if(isDarkMode) {
			return settingsPanelDarkMode;
		}
		else {
			return settingsPanelLightMode;
		}
	}
}
