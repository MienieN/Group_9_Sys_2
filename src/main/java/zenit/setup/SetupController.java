package main.java.zenit.setup;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import main.java.zenit.Zenit;
import main.java.zenit.filesystem.WorkspaceHandler;
import main.java.zenit.filesystem.jreversions.JDKDirectories;
import main.java.zenit.ui.DialogBoxes;

public class SetupController extends AnchorPane {
	private Stage stage;
	private File workspaceDat;
	private File JDKDat;
	private File defaultJDKDat;
	private File workspaceFile;
	private RadioButtonListener rbListener;
	private final ToggleGroup tgGroup;
	
	@FXML ListView<String> jdkList;
	@FXML TextField workspacePath;
	@FXML ImageView logo;
	@FXML RadioButton rb1, rb2;

	/**
	 * Constructor for the SetupController class. Initializes the necessary variables and files required for setup.
	 * 1. Initializes a ToggleGroup to handle radio button selection.
	 * 2. Initializes workspaceDat file to store workspace information.
	 * 3. Initializes JDKDat file to store JDK information.
	 * 4. Initializes defaultJDKDat file to store default JDK information.
	 */
	public SetupController() {
		//Init final variable
		tgGroup = new ToggleGroup();
		
		//Init dat files
		workspaceDat = new File("res/workspace/workspace.dat");
		JDKDat = new File("res/JDK/JDK.dat");
		defaultJDKDat = new File ("res/JDK/DefaultJDK.dat");
	}

	/**
	 * This method starts the setup process by loading the setup interface from the FXML file, setting up the stage with the provided scene,
	 * initializing necessary graphical components, and displaying the stage.
	 */
	public void start() {
		try {
			FXMLLoader loader = loadSetupFXML();
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);
			setUpStage(scene);

			initialize(); //Init graphical components
			stage.showAndWait(); //display stage
			
		} catch (IOException e) {
			System.out.println("Error in SetupController start method: " + e.getMessage());
		}
	}

	/**
	 * Loads the FXML file for the setup interface.
	 *
	 * @return FXMLLoader object for the setup FXML file.
	 */
	private FXMLLoader loadSetupFXML() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/zenit/setup/Setup.fxml"));
		loader.setController(this);
		return loader;
	}

	/**
	 * Sets up the stage for the application with the given scene.
	 *
	 * @param scene the Scene object to set on the stage
	 * @return true if the stage setup was successful, false otherwise
	 */
	private boolean setUpStage(Scene scene) {
		stage = new Stage();
		stage.setResizable(false);
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		return true;
	}

	/**
	 * Initializes the setup process by setting dark mode, loading logo and OS default JDKs,
	 * loading workspace information, initializing radio buttons, and updating the JDK list.
	 */
	private void initialize() {
		setDarkMode();
		loadLogoAndOsJdks();
		loadWorkspace();
		initRadioButtons();
		updateJdkList();
	}

	/**
	 * Sets the dark mode for the application by adding the dark mode stylesheet to the scene.
	 *
	 * @return true if the dark mode was set successfully, false otherwise
	 */
	private boolean setDarkMode() {
		var stylesheets = stage.getScene().getStylesheets();
		var darkMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle.css")
				.toExternalForm();
		stylesheets.add(darkMode);
		return true;
	}

	/**
	 * Loads the logo image and OS default JDKs if necessary.
	 *
	 * @return true if the logo image and default JDKs were loaded successfully, false otherwise
	 */
	private boolean loadLogoAndOsJdks() {
		//Load logo
		logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png")
				.toExternalForm()));
		logo.setFitWidth(55);

		//Load OS default JDKs if none are saved
		if (!JDKDat.exists()) {
			JDKDirectories.createNewFileWithDefaultJVMDirectories();
		}

		return true;
	}

	/**
	 * Loads the workspace information. If the workspace data file exists, it reads the workspace information
	 * from the file and sets the workspace path. If the workspace data file does not exist, it sets up a new
	 * workspace using WorkspaceHandler and returns true.
	 *
	 * @return true if the workspace was loaded successfully or a new workspace was set up, false otherwise
	 */
	private boolean loadWorkspace() {
		if (workspaceDat.exists()) {
			try {
				workspaceFile = WorkspaceHandler.readWorkspace();
				workspacePath.setText(workspaceFile.getPath());
			} catch (IOException e) {
				System.out.println("Eror while loading workspace: " + e.getMessage());
			}
		} else {
			WorkspaceHandler.setUpNewWorkspace();
		}
		return true;
	}

	/**
	 * Initializes the radio buttons based on the workspace file availability.
	 * If the workspace file is null, selects rb2; otherwise, selects rb1.
	 *
	 * @return true if the radio buttons are successfully initialized
	 */
	private boolean initRadioButtons() {
		rb1.setToggleGroup(tgGroup);
		rb2.setToggleGroup(tgGroup);
		
		if (workspaceFile == null) {
			rb2.setSelected(true);
		} else {
			rb1.setSelected(true);
		}

		tgGroup.selectedToggleProperty().addListener(new RadioButtonListener());
		return true;
	}

	/**
	 * Updates the JDK list with the latest JDK names extracted from directories.
	 *
	 * @return true if the JDK list was successfully updated, false otherwise
	 */
	private boolean updateJdkList() {
		List<String> jdkNames = JDKDirectories.extractJDKDirectoryNameAsString();
		markDefaultJdkNameAsDefault(jdkNames);
		updateJdkListInUI(jdkNames);
		return true;
	}

	/**
	 * Updates the JDK list in the user interface.
	 *
	 * @param jdkNames a List of JDK names to update the JDK list with
	 * @return true if the JDK list was successfully updated, false otherwise
	 */
	private boolean updateJdkListInUI(List<String> jdkNames) {
		ObservableList<String> observableJdkNames = FXCollections.observableArrayList(jdkNames);
		FXCollections.sort(observableJdkNames);
		jdkList.setItems(observableJdkNames);
		return true;
	}

	/**
	 * Marks the default JDK name as default in the provided list of JDK names.
	 *
	 * @param jdkNames a List of JDK names to check and mark the default JDK name as default
	 * @return true if the default JDK name is successfully marked as default, false otherwise
	 */
	private boolean markDefaultJdkNameAsDefault(List<String> jdkNames) {
		//Try to read the default JDK
		File defaultJdkFile = JDKDirectories.getDefaultJDKFile();
		if (defaultJdkFile != null) {
			String defaultJdkName = defaultJdkFile.getName();
			if (defaultJdkName != null && jdkNames.remove(defaultJdkName)) {
				jdkNames.add(defaultJdkName + " [default]");
			}
		}
		return true;
	}

	/**
	 * Opens a DirectoryChooser dialog for the user to select a workspace directory.
	 * Sets the selected directory as the workspace and updates the workspace path text field.
	 * If no directory is selected and the current workspace path field is empty, toggles radio buttons accordingly.
	 */
	@FXML
	private void browse() {
		DirectoryChooser directoryChooser = createDirectoryChooser();
		File newWorkspace = directoryChooser.showDialog(stage);
		
		if (newWorkspace != null) {
			workspaceFile = newWorkspace;
			workspacePath.setText(workspaceFile.getPath());
			toggleRadiobutton(true);
		} else if (newWorkspace == null && workspacePath.getText().equals("")){
			toggleRadiobutton(false);
		}
	}

	/**
	 * Creates a DirectoryChooser object with preset properties.
	 *
	 * @return DirectoryChooser object with title set to "Choose a workspace" and initial directory set to the user's home directory.
	 */
	private DirectoryChooser createDirectoryChooser() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose a workspace");
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		return directoryChooser;
	}

	/**
	 * Opens a DirectoryChooser dialog for the user to select a JDK directory.
	 * Sets the initial directory to the current JDK directory.
	 * Calls addNewJdkIfValid method with the selected JDK directory.
	 */
	@FXML
	private void addJDK() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Choose JDK to add");
		directoryChooser.setInitialDirectory(JDKDirectories.getJVMDirectory());
		addNewJdkIfValid(directoryChooser.showDialog(stage));
	}

	/**
	 * Adds a new JDK to the list if the provided JDK file is valid.
	 *
	 * @param newJDK the File object representing the JDK to be added
	 * @return true if the JDK was successfully added, false otherwise
	 */
	private boolean addNewJdkIfValid(File newJDK) {
		if (newJDK != null) {
			if (jdkList.getItems().contains(newJDK.getName())) {
				DialogBoxes.errorDialog("JDK already exist in list", "", "A JDK with that name"
						+ " already exist in the list.");
			} else {
				if (JDKDirectories.appendToTrackedDirectoriesList(newJDK)) {
					updateJdkList();
				} else {
					DialogBoxes.errorDialog("Not a valid JDK folder", "", "The chosen folder is not"
							+ " a valid JDK folder, must contain a java and javac executable");
				}
			}
		}
		return true;
	}

	/**
	 * Removes the selected JDK from the JDK list.
	 *
	 * Retrieves the name of the JDK to remove from the selection list.
	 * Displays an error dialog if no JDK is selected.
	 * Displays an error dialog if the default JDK is selected for removal.
	 * Prompts the user to confirm the removal if there is only one JDK remaining in the list.
	 * Removes the JDK from the tracked directories list, updates the JDK list, and UI accordingly.
	 */
	@FXML
	private void removeJDK() {
		String jdkToRemoveName = jdkList.getSelectionModel().getSelectedItem();

		if (jdkToRemoveName == null) {
			DialogBoxes.errorDialog("Choose JDK to remove", "", "Choose a JDK from the list to "
					+ "remove it");
			return;
		}
		if (jdkToRemoveName.endsWith(" [default]")) {
			DialogBoxes.errorDialog("Can't remove default JDK", "", "Can't remove the default "
					+ "JDK, choose another default JDK to remove this one");
			return;
		}
		if (!confirmLastJDKRemoval()) {
			return;
		}
		String jdkToRemovePath = JDKDirectories.getFullPathFromName(jdkToRemoveName);
		File jdkToRemoveFile = new File(jdkToRemovePath);
		JDKDirectories.removeFromTrackedDirectoriesList(jdkToRemoveFile);
		updateJdkList();
	}

	/**
	 * Checks if there is only one JDK remaining in the list. If yes, prompts the user to confirm removal as at least one JDK is needed to run Zenit.
	 *
	 * @return true if there is more than one JDK in the list or if the user confirms the removal of the last JDK, false otherwise
	 */
	private boolean confirmLastJDKRemoval() {
		if (jdkList.getItems().size() > 1) {
			return true;
		}
		int choice = DialogBoxes.twoChoiceDialog("Remove the last JDK from list", "",
				"There is only one JDK remaining in the list. Are you sure you want to "
						+ "remove it? At least one JDK is needed to run Zenit", "Yes, remove", "No, keep it");
		return !(choice == 0 || choice == 2);
	}
	
	@FXML
	private void setDefaultJDK() {
		String defaultJDKName = jdkList.getSelectionModel().getSelectedItem();
		
		if (defaultJDKName != null) {
			String defaultJDKPath = JDKDirectories.getFullPathFromName(defaultJDKName);
			File deaultJDKFile = new File(defaultJDKPath);
			JDKDirectories.setDefaultJDKFile(deaultJDKFile);
		
			updateJdkList();
		} else {
			DialogBoxes.errorDialog("Choose JDK to make default", "", "Choose a JDK from the list to "
					+ "make it the default");
		}
	}
	
	@FXML
	private void quit() {
		System.exit(0);
	}
	
	@FXML
	private void done() {
		
		//Check if workspace input text has been updated since save
		boolean notSavedWorkspace = true;
		
		while (notSavedWorkspace) {
			if (tgGroup.getSelectedToggle().equals(rb1) && workspaceFile != null && 
					!workspaceFile.getPath().equals(workspacePath.getText())) {
				int choice = DialogBoxes.twoChoiceDialog("Save changes to workspace", "",
						"The changes to workspace has not been saved, would you like: " + 
						workspacePath.getText() + " to be your workspace?", "Yes", "No");
				if (choice == 1) {
					onEnter();
				} else {
					notSavedWorkspace = false;
				}
			} else {
				notSavedWorkspace = false;
			}
		}
		
		//Check if default workspace is selected
		if (tgGroup.getSelectedToggle().equals(rb2)) {
			String userPath = System.getProperty("user.home");
			String documentsPath = getDocumentsPath();
			File defaultWorkspace = new File(userPath + File.separator + documentsPath +
					"Zenit" + File.separator + "Default Workspace");
			if (!defaultWorkspace.exists()) {
				defaultWorkspace.mkdirs();
			}
			workspaceFile = defaultWorkspace;
		}
		
		if (!workspaceFile.exists() || !JDKDat.exists() || !defaultJDKDat.exists()) {
			DialogBoxes.errorDialog("Missing files", "", "Please enter the required information to"
					+ " launch Zenit");
		} else {
			WorkspaceHandler.createWorkspace(workspaceFile);
			stage.close();
		}
	}
	
	@FXML
	private void onEnter() {
		String input = workspacePath.getText();
		File file = new File(input);
		
		if (file.exists() && file.isDirectory()) {
			workspaceFile = file;
			toggleRadiobutton(true);
		} else if (file.exists() && !file.isDirectory()) {
			DialogBoxes.errorDialog("File selected", "", "You have selected a file as workspace. A"
					+ " workspace must be a directory");
			toggleRadiobutton(false);
		} else if (!file.exists()) {
			int choice = DialogBoxes.twoChoiceDialog("Folder doesn't exist", "", "The folder " + 
					input + " doesn't exist. Would you like to create it?", "Yes, create folder",
					"No, don't create folder");
			
			if (choice == 1) {
				if (file.mkdir()) {
					DialogBoxes.informationDialog("Folder created", "The folder " + input + 
							" is now created.");
					workspaceFile = file;
					toggleRadiobutton(true);
				} else {
					DialogBoxes.errorDialog("Folder couldn't be created", "", "The folder " +
							input + " couldn't be created. You can only create a folder in an"
									+ " existing folder");
					toggleRadiobutton(false);
				}
			} else {
				toggleRadiobutton(false);
			}
		}
	}

	private String getDocumentsPath() {
		String OS = Zenit.OS;
		if (OS.equals("Mac OS X")) {
			return "documents" + File.separator;
		} else if (OS.startsWith("Windows")) {
			return "Documents";
		} else if (OS.equals("Linux")) {
			return "Documents";
		} else {
			return null;
		}
	}

	private void toggleRadiobutton(boolean toggleOwn) {
		tgGroup.selectedToggleProperty().removeListener(rbListener);
		if (toggleOwn) {
			rb2.setSelected(false);
			rb1.setSelected(true);
		} else {
			rb1.setSelected(false);
			rb2.setSelected(true);
		}
		tgGroup.selectedToggleProperty().addListener(rbListener);
	}
	
	private class RadioButtonListener implements ChangeListener<Toggle> {

		@Override
		public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            if (tgGroup.getSelectedToggle().equals(rb1)) {
            	if (workspacePath.getText().equals("")) {
            		browse();
            	} else {
            		onEnter();
            	}
            }
		}
	}
	
}
