package main.java.zenit.ui.projectinfo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.zenit.filesystem.FileController;
import main.java.zenit.filesystem.ProjectFile;
import main.java.zenit.filesystem.RunnableClass;
import main.java.zenit.filesystem.jreversions.JDKDirectories;
import main.java.zenit.filesystem.metadata.Metadata;
import main.java.zenit.filesystem.metadata.MetadataVerifier;
import main.java.zenit.ui.DialogBoxes;
import main.java.zenit.ui.MainController;

public class ProjectMetadataController extends AnchorPane {
	private Stage propertyStage;
	private FileController fileController;
	private MainController mainController;
	private ProjectFile projectFile;
	private Metadata metadata;
	private RunnableClass[] runnableClasses;
	private RunnableClass selectedRunnableClass;
	private FileChooser.ExtensionFilter libraryFilter = new FileChooser.ExtensionFilter("Libraries", "*.jar", "*.zip");
	public enum DirectoryOrSourcepathType { INTERNAL, EXTERNAL}
	private boolean darkMode, taUpdated = false;
	private double xOffset = 0, yOffset = 0;
	private static final String DARK_MODE_STYLESHEET_PATH = "/zenit/ui/projectinfo/mainStyle.css";
	private static final String LIGHT_MODE_STYLESHEET_PATH = "/zenit/ui/projectinfo/mainStyle-lm.css";

	@FXML private AnchorPane header;
	@FXML private ImageView logo;
	@FXML private Text title;
	@FXML private ListView<String> directoryPathList, sourcepathList,runnableClassesList, internalLibrariesList, externalLibrariesList;
	@FXML private TextArea taProgramArguments, taVMArguments;
	@FXML private ComboBox<String> JREVersions;
	@FXML private Button addInternalLibrary, removeInternalLibrary, addExternalLibrary, removeExternalLibrary, save, run;

	/**
	 * Constructor for ProjectMetadataController.
	 *
	 * @param fc FileController to manage file operations
	 * @param projectFile ProjectFile representing the project
	 * @param darkMode boolean indicating if dark mode is enabled
	 * @param mainController MainController for the project
	 */
	public ProjectMetadataController(FileController fc, ProjectFile projectFile, boolean darkMode, MainController mainController) {
		this.projectFile = projectFile;
		fileController = fc;
		this.darkMode = darkMode;
		this.mainController = mainController;
	}

	/**
	 * Sets up the scene and stage for the ProjectMetadataController.
	 * Loads the ProjectMetadata.fxml file using a FXMLLoader, sets the controller to this instance,
	 * and initializes the scene with an AnchorPane. Creates a new Stage, sets properties, and displays the scene.
	 * If any exceptions occur during the setup, an error message is printed.
	 *
	 * @return true if the scene and stage were set up successfully, false otherwise
	 */
	private boolean setupSceneAndStage() {
		try {
			//setup scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/projectinfo/ProjectMetadata.fxml"));
			loader.setController(this);
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);

			//set up stage
			propertyStage = new Stage();
			propertyStage.setResizable(false);
			propertyStage.setScene(scene);
			propertyStage.initStyle(StageStyle.UNDECORATED);

			return true;
		} catch (IOException e) {
			System.out.println("Error setupSceneAndStage() = " + e);
		}
        return false;
    }

	/**
	 * Process the metadata by initializing, checking if dark mode changed, and showing the property stage.
	 *
	 * @return true if the metadata processing was successful, false otherwise
	 */
	private boolean processMetadata() {
		initialize();
		ifDarkModeChanged(darkMode);
		propertyStage.show();
		return true;
	}

	/**
	 * Handles missing metadata by adding metadata if necessary and processing it.
	 *
	 * @param metadataFile the file representing the metadata
	 * @return true if metadata is added and processed successfully, false otherwise
	 */
	private boolean handleMissingMetadata(File metadataFile) {
		if (ProjectInfoErrorHandling.metadataMissing() == 1) {
			metadataFile = projectFile.addMetadata();
			processMetadata();
			return true;
		}
		return false;
	}

	/**
	 * Handles outdated metadata by updating the metadata file and processing it.
	 *
	 * @param metadataFile the File object representing the metadata file to be updated
	 * @return true if the metadata is updated and processed successfully, false otherwise
	 */
	private boolean handleOutdatedMetadata(File metadataFile) {
		if (ProjectInfoErrorHandling.metadataOutdated() == 1) {
			metadata = fileController.updateMetadata(metadataFile);
			processMetadata();
			return true;
		}
		return false;
	}

	/**
	 * Verifies the metadata file by checking its validity and consistency.
	 *
	 * @param metadataFile the File object representing the metadata file to be verified
	 * @return true if the metadata file is successfully verified, false otherwise
	 */
	private boolean verifyMetadataFile(File metadataFile) {
		int verification = MetadataVerifier.verify(metadata);

		switch (verification) {
			case MetadataVerifier.VERIFIED:
				return processMetadata();
			case MetadataVerifier.METADATA_FILE_MISSING:
				// no metadata file is found
				return handleMissingMetadata(metadataFile);
			case MetadataVerifier.METADATA_OUTDATED:
				// metadata file is outdated
				return handleOutdatedMetadata(metadataFile);
			default:
				return false;
		}
	}

	/**
	 * Starts the project metadata controller by setting up the scene and stage,
	 * initializing metadata if available, and verifying the metadata file.
	 * If any exceptions occur during the process, an error message is printed.
	 */
	public void start() {
		try {
			boolean stageCreated = setupSceneAndStage();
			if (stageCreated) {
				File metadataFile = projectFile.getMetadata(); //gets metadata file
				if (metadataFile != null) {
					metadata = new Metadata(metadataFile);
				} else {
					metadata = null;
				}

				if (!verifyMetadataFile(metadataFile)) {
					System.out.println("Something went wrong while verifying metadata file");
				}
			}
		} catch (Exception e) {
			System.out.println("Error in ProjectMetadataController.start() = " + e);
		}
	}

	/**
	 * Sets the title and logo for the project settings.
	 *
	 * @return true if the title and logo were set successfully, false otherwise
	 */
	private boolean setTitleAndLogo() {
		title.setText(projectFile.getName() + " settings");
		logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png").toExternalForm()));
		logo.setFitWidth(55);

		return true;
	}

	/**
	 * Updates the text fields with the directory and source path from the metadata.
	 *
	 * @return true if the text fields were updated successfully, false otherwise
	 */
	private boolean updateTextFields() {
		if (metadata.getDirectory() != null) {
			updateText(directoryPathList, metadata.getDirectory());
		}
		if (metadata.getSourcePath() != null) {
			updateText(sourcepathList, metadata.getSourcePath());
		}
		return true;
	}

	/**
	 * Initializes and sets the JRE versions based on the retrieved JRE version from the metadata.
	 *
	 * @return true if the JRE versions are successfully initialized and set, false otherwise
	 */
	private boolean initializeAndSetJREVersions() {
		File JREVersion = new File(metadata.getJREVersion());
		List<String> JDKs = JDKDirectories.extractJDKDirectoryNameAsString();
		if (JREVersion != null) {
			JREVersions.getItems().addAll(JDKs);
			JREVersions.getSelectionModel().select(JREVersion.getName());
		}
		return true;
	}

	/**
	 * Sets the default arguments for program and VM text areas.
	 *
	 * @return true after setting the arguments successfully
	 */
	private boolean setArguments() {
		taProgramArguments.setText("<select a runnable class>");
		taProgramArguments.setEditable(false);
		taVMArguments.setText("<select a runnable class>");
		taVMArguments.setEditable(false);

		return true;
	}

	/**
	 * Initializes the project settings interface by setting up title and logo, updating text fields,
	 * updating lists of internal libraries, external libraries, and runnable classes, initializing and setting JRE versions,
	 * and setting up mouse click and drag events for the header.
	 */
	private void initialize() {
		setTitleAndLogo();
		updateTextFields();
		updateLists();
		initializeAndSetJREVersions();
		setArguments();
		
	    header.setOnMousePressed(new EventHandler<MouseEvent>() {
	    	   @Override
	    	   public void handle(MouseEvent event) {
	    	       xOffset = event.getSceneX();
	    	       yOffset = event.getSceneY();
	    	   }
	    	});

	    	//move around here
	    header.setOnMouseDragged(new EventHandler<MouseEvent>() {
	    	   @Override
	    	   public void handle(MouseEvent event) {
	    	       propertyStage.setX(event.getScreenX() - xOffset);
	    	       propertyStage.setY(event.getScreenY() - yOffset);
	    	   }
	    	});
	}

	/**
	 * Updates the internal libraries list by clearing it and adding internal libraries retrieved from metadata.
	 *
	 * @return true if the internal libraries list was updated successfully, false otherwise
	 */
	private boolean updateInternalLibrariesList() {
		internalLibrariesList.getItems().clear();
		String[] internalLibraries = metadata.getInternalLibraries();
		if (internalLibraries != null) {
			internalLibrariesList.getItems().clear();
			internalLibrariesList.getItems().addAll(internalLibraries);
			internalLibrariesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
		return true;
	}

	/**
	 * Updates the external libraries list by clearing it and adding external libraries retrieved from the metadata.
	 *
	 * @return true if the external libraries list was updated successfully, false otherwise
	 */
	private boolean updateExternalLibrariesList() {
		externalLibrariesList.getItems().clear();
		String[] externalLibraries = metadata.getExternalLibraries();
		if (externalLibraries != null) {
			externalLibrariesList.getItems().addAll(externalLibraries);
			externalLibrariesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
		return true;
	}

	/**
	 * Updates the list of runnable classes based on the metadata file.
	 * This method retrieves the list of runnable classes from the metadata and updates the runnableClassesList accordingly.
	 *
	 * @return true if the update operation was successful, false otherwise
	 */
	private boolean updateRunnableClassesList() {
		metadata = new Metadata(metadata.getFile());
		runnableClasses = metadata.getRunnableClasses();
		if (runnableClasses != null) {
			runnableClassesList.getItems().clear();
			for (RunnableClass runnableClass : runnableClasses) {
				runnableClassesList.getItems().add(runnableClass.getPath());
			}
		}
		return true;
	}

	/**
	 * Updates the internal libraries list, external libraries list, and runnable classes list.
	 * It's helper methods all return booleans for testing purposes.
	 */
	private void updateLists() {
		updateInternalLibrariesList();
		updateExternalLibrariesList();
		updateRunnableClassesList();
	}

	/**
	 * Determines the type of directory based on user choice.
	 *
	 * @return 1 if the user chooses 'Internal', 2 if the user chooses 'External', 0 otherwise
	 */
	private int determineDirectoryType() {
		return DialogBoxes.twoChoiceDialog("Internal directory", "Internal directory",
				"Do you want the new directory to be internal or external?", "Internal", "External");
	}

	/**
	 * Initializes a DirectoryChooser with the initial directory set to the project's bin directory.
	 *
	 * @param directoryOrSourcepath a String indicating the type of directory or sourcepath to choose
	 * @return a DirectoryChooser object initialized with the appropriate title and initial directory
	 */
	private DirectoryChooser initializeDirectoryOrSourcepathChooser(String directoryOrSourcepath) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(projectFile.getBin());
		directoryChooser.setTitle("Choose new " + directoryOrSourcepath);
		return directoryChooser;
	}

	/**
	 * Converts an integer representation of directory type to DirectoryOrSourcepathType enum.
	 *
	 * @param directoryType the integer representation of the directory type
	 * @return DirectoryOrSourcepathType enum corresponding to the directory type
	 * @throws IllegalArgumentException if the directory type is not 0 or 1
	 */
	private DirectoryOrSourcepathType intToDirectoryOrSourcepathType(int directoryType) throws IllegalArgumentException {
		switch (directoryType) {
            case 0:
                return DirectoryOrSourcepathType.INTERNAL;
            case 1:
                return DirectoryOrSourcepathType.EXTERNAL;
            default:
                throw new IllegalArgumentException("Invalid type: " + directoryType);
        }
	}

	/**
	 * Method to handle changing the directory of the project.
	 * It determines the type of directory (internal or external) based on user choice,
	 * initializes a DirectoryChooser with the project's bin directory as the initial directory,
	 * prompts the user to select a new directory, and updates the project file with the new directory.
	 * If the directory selection is successful, it updates the displayed directory path in the UI.
	 */
	@FXML
	private void changeDirectory() {
		try {
			DirectoryOrSourcepathType directoryOrSourcepathType = intToDirectoryOrSourcepathType(determineDirectoryType());
			DirectoryChooser directoryChooser = initializeDirectoryOrSourcepathChooser("directory");
			File directory = directoryChooser.showDialog(propertyStage);

			if (directory != null) {
				String directoryPath = fileController
					.changeDirectory(
						directory,
						projectFile,
						directoryOrSourcepathType == DirectoryOrSourcepathType.INTERNAL
					);
				updateText(directoryPathList, directoryPath);
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Error changing directory: " + e);
		}
	}

	/**
	 * Determines the type of sourcepath based on user choice.
	 *
	 * @return 1 if the user chooses 'Internal', 2 if the user chooses 'External', 0 otherwise
	 */
	private int determineSourcepathType() {
		return DialogBoxes.twoChoiceDialog("Internal sourcepath", "Internal sourcepath",
				"Do you want the new sourcepath to be internal or external?",
				"Internal", "External");
	}

	/**
	 * Method to handle changing the sourcepath of the project.
	 * It determines the type of sourcepath (internal or external) based on user choice,
	 * initializes a DirectoryChooser to select a new directory,
	 * updates the project file with the new sourcepath if a directory is chosen,
	 * and updates the displayed sourcepath in the UI.
	 * If an IllegalArgumentException occurs during the process, an error message is printed.
	 */
	@FXML
	private void changeSourcepath() {
		try {
			DirectoryOrSourcepathType sourcepathType = intToDirectoryOrSourcepathType(determineSourcepathType());
			DirectoryChooser directoryChooser = initializeDirectoryOrSourcepathChooser("sourcepath");

			File directory = directoryChooser.showDialog(propertyStage);

			if (directory != null) {
				String sourcepath = fileController
						.changeSourcePath(
								directory,
								projectFile,
								sourcepathType == DirectoryOrSourcepathType.INTERNAL);
				updateText(sourcepathList, sourcepath);
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Error changing sourcepath: " + e);
		}
	}

	/**
	 * Method to change the JRE version of the project.
	 * Retrieves the selected JRE version from the JREVersions ListView,
	 * gets the full path of the selected JDK from JDKDirectories,
	 * sets the JRE version in the metadata,
	 * and encodes the metadata.
	 */
	@FXML
	private void changeJREVersion() {
		String JDKName = JREVersions.getSelectionModel().getSelectedItem();
		String JDK = JDKDirectories.getFullPathFromName(JDKName);
		metadata.setJREVersion(JDK);
		metadata.encode();
	}

	/**
	 * Displays a file chooser dialog for the user to select multiple files.
	 *
	 * @return A List of File objects representing the files selected by the user
	 */
	private List<File> chooseFiles() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(libraryFilter);
		return fileChooser.showOpenMultipleDialog(propertyStage);
	}

	/**
	 * Allows the user to add internal libraries to the project.
	 * Prompts the user to choose multiple files using a file chooser dialog.
	 * If files are selected, attempts to add them as internal libraries to the project.
	 * Updates the project metadata and lists of internal libraries if the addition is successful.
	 * If the addition fails, displays an error message.
	 */
	@FXML
	private void addInternalLibrary() {
		List<File> selectedFiles = chooseFiles();
		if (selectedFiles != null) {
			boolean success = fileController.addInternalLibraries(selectedFiles, projectFile);
			if (success) {
				metadata = new Metadata(metadata.getFile());
				updateLists();
			} else {
				ProjectInfoErrorHandling.addInternalLibraryFail();
			}
		}
	}

	/**
	 * Removes the selected internal libraries from the project.
	 * Retrieves the list of selected libraries from the internalLibrariesList ListView.
	 * Removes the selected libraries using the FileController's removeInternalLibraries method,
	 * passing the list of selected libraries and the projectFile.
	 * If removal is successful, updates the metadata with the new information and updates the UI lists.
	 * If removal fails, displays an error message using ProjectInfoErrorHandling.removeInternalLibraryFail method.
	 */
	@FXML
	private void removeInternalLibrary() {
		List<String> selectedLibraries = internalLibrariesList.getSelectionModel().getSelectedItems();
		
		if (selectedLibraries != null) {
			boolean success = fileController
					.removeInternalLibraries(selectedLibraries, projectFile);
			if (success) {
				metadata = new Metadata(metadata.getFile());
				updateLists();
			} else {
				ProjectInfoErrorHandling.removeInternalLibraryFail();
			}
		}
	}

	/**
	 * Handles the selected files by adding them as external libraries to the project.
	 *
	 * @param files A List of File objects representing the selected files to be added as external libraries
	 * @return true if the files are successfully added as external libraries and metadata is updated, false otherwise
	 */
	private boolean handleSelectedFiles(List<File> files) {
		boolean success = fileController.addExternalLibraries(files, projectFile);
		if (success) {
			metadata = new Metadata(metadata.getFile());
			updateLists();
		} else {
			ProjectInfoErrorHandling.addExternalLibraryFail();
		}
		return true;
	}

	/**
	 * Opens a file chooser dialog for selecting external libraries to add to the project.
	 * Only files matching the library filter will be displayed.
	 * Once files are selected, the method handleSelectedFiles() is called to process them.
	 */
	@FXML
	private void addExternalLibrary() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(libraryFilter);

		List<File> selectedFiles = fileChooser.showOpenMultipleDialog(propertyStage);
		if (selectedFiles != null) {
			handleSelectedFiles(selectedFiles);
		}
	}

	/**
	 * Removes selected external libraries from the project.
	 * Retrieves the list of selected libraries from the externalLibrariesList view component,
	 * then attempts to remove these libraries using the fileController.
	 * If removal is successful, updates the metadata and refreshes the lists displayed in the application.
	 * If removal fails, displays an error message using ProjectInfoErrorHandling.
	 */
	@FXML
	private void removeExternalLibrary() {
		List<String> selectedLibraries = externalLibrariesList.getSelectionModel().getSelectedItems();
		
		if (selectedLibraries != null) {
			boolean success = fileController.removeExternalLibraries(selectedLibraries, projectFile);
			if (success) {
				metadata = new Metadata(metadata.getFile());
				updateLists();
			} else {
				ProjectInfoErrorHandling.removeExternalLibraryFail();
			}
		}
	}

	/**
	 * Sets the program arguments and VM arguments for the selected runnable class.
	 *
	 * @param programArgs the program arguments to be set
	 * @param vmArgs the VM arguments to be set
	 */
	private void setArguments(String programArgs, String vmArgs) {
		selectedRunnableClass.setPaArguments(programArgs);
		selectedRunnableClass.setVmArguments(vmArgs);
	}

	/**
	 * Encodes the metadata using the set runnable classes.
	 *
	 * @return true if the metadata is successfully encoded, false otherwise
	 */
	private boolean encodeMetadata() {
		metadata.setRunnableClasses(runnableClasses);
		return metadata.encode();
	}

	/**
	 * Updates the dialogs based on the provided flag.
	 *
	 * @param isEncoded Flag indicating if the dialogs should be updated as encoded.
	 * @return Boolean value representing the success of updating the dialogs.
	 */
	private boolean updateDialogs(boolean isEncoded) {
		if (isEncoded) {
			taUpdated = false;
			DialogBoxes.informationDialog("Arguments saved", "Arguments have been saved");
		} else {
			DialogBoxes.errorDialog(null, "Arguments not saved", "Arguments couldn't be saved");
		}
		return true;
	}

	/**
	 * Saves the program and VM arguments entered by the user and updates the dialogs with encoded metadata.
	 */
	@FXML
	private void save() {
		String programArgumentsText = taProgramArguments.getText();
		String vmArgumentsText = taVMArguments.getText();
		setArguments(programArgumentsText, vmArgumentsText);
		updateDialogs(encodeMetadata());
	}

	/**
	 * Executes the selected runnable class by compiling and running it.
	 * Retrieves the path of the selected runnable class and the source path of the project file.
	 * If both paths are not null, it compiles and runs the selected class.
	 */
	@FXML
	private void run() {
		String runClass = getSelectedRunnableClass().getPath();
		String srcPath = projectFile.getSrc().getPath() + File.separator;
		
		if (runClass != null && srcPath != null) {
			File runFile = new File(srcPath + runClass);
			mainController.compileAndRun(runFile);
		}
	}

	/**
	 * Sets the program arguments and VM arguments based on the selected runnable class.
	 *
	 * @return true if the program and VM arguments are set successfully, false otherwise
	 */
	private boolean setProgramAndVMArguments() {
		selectedRunnableClass = getSelectedRunnableClass();
		if (selectedRunnableClass != null) {
			taProgramArguments.setText(selectedRunnableClass.getPaArguments());
			taProgramArguments.setEditable(true);
			taVMArguments.setText(selectedRunnableClass.getVmArguments());
			taVMArguments.setEditable(true);
		} else {
			taProgramArguments.setText("<select a runnable class>");
			taProgramArguments.setEditable(false);
			taVMArguments.setText("<select a runnable class>");
			taVMArguments.setEditable(false);
		}
		return true;
	}

	/**
	 * Method for changing the runnable class that is called when a button is clicked.
	 * If the text area is updated, a confirmation dialog is shown to ask whether to save the changes or discard them.
	 * If the choice is to save, the changes are saved using the 'save' method.
	 * If the choice is to discard, the 'taUpdated' flag is set to false.
	 * Finally, the method sets the program and VM arguments.
	 */
	@FXML
	private void runnableClassChange() {
		if (taUpdated) {
			int choice = DialogBoxes.twoChoiceDialog("Arguments updated", "Arguments have been updated",
					"Would you like to save the updated arguments?", "Yes, save", "No, discard");
			if (choice == 1) {
				save();
			} else {
				taUpdated = false;
			}
		}
		setProgramAndVMArguments();
	}

	/**
	 * Marks that the arguments have been changed, indicating that the text area has been updated.
	 */
	@FXML
	private void argumentsChanged() {
		taUpdated = true;
	}

	/**
	 * Adds a new runnable class to the project.
	 * This method creates a new ProjectRunnableClassesController and starts it with the provided projectFile, darkMode, and fileController.
	 * It then updates the lists to reflect the changes made.
	 */
	@FXML
	private void addRunnableClass() {
		new ProjectRunnableClassesController(projectFile, darkMode, fileController).start();
		updateLists();
	}

	/**
	 * Removes the selected runnable class from the list and updates the metadata and UI accordingly.
	 */
	@FXML
	private void removeRunnableClass() {
		String selected = runnableClassesList.getSelectionModel().getSelectedItem();
		if (runnableClassesList.getSelectionModel().getSelectedItem() != null) {
			metadata.removeRunnableClass(selected);
			metadata.encode();
			updateLists();
		}
	}

	/**
	 * Closes the property stage.
	 */
	@FXML
	private void close() {
		propertyStage.close();
	}

	/**
	 * Returns the RunnableClass object with the specified path.
	 *
	 * @param selected the path of the RunnableClass to retrieve
	 * @return the RunnableClass object with the specified path, or null if not found
	 */
	private RunnableClass getRunnableClass(String selected) {
		for (RunnableClass runnableClass : runnableClasses) {
			if (runnableClass.getPath().equals(selected)) {
				return runnableClass;
			}
		}
		return null;
	}

	/**
	 * Retrieves the selected RunnableClass object based on the selection in the runnableClassesList.
	 *
	 * @return The selected RunnableClass object or null if no selection is made or if runnableClasses is null.
	 */
	private RunnableClass getSelectedRunnableClass() {
		String selected = runnableClassesList.getSelectionModel().getSelectedItem();
		if (runnableClasses != null) {
            return getRunnableClass(selected);
		}
		return null;
	}

	/**
	 * Executes the appropriate action when the dark mode status is changed.
	 *
	 * @param isDarkMode a boolean indicating whether dark mode is enabled or not
	 */
    public void ifDarkModeChanged(boolean isDarkMode) {
        List<String> stylesheets = propertyStage.getScene().getStylesheets();

        if (isDarkMode) {
            switchStylesheet(stylesheets, LIGHT_MODE_STYLESHEET_PATH, DARK_MODE_STYLESHEET_PATH);
        } else {
            switchStylesheet(stylesheets, DARK_MODE_STYLESHEET_PATH, LIGHT_MODE_STYLESHEET_PATH);
        }
    }

	/**
	 * Switches the stylesheet from one resource to another in a given list of stylesheets.
	 *
	 * @param stylesheets A list of stylesheets to perform the switch on.
	 * @param from The resource path of the stylesheet to switch from.
	 * @param to The resource path of the stylesheet to switch to.
	 */
    private void switchStylesheet(List<String> stylesheets, String from, String to) {
        String fromStylesheet = getClass().getResource(from).toExternalForm();
        if (stylesheets.contains(fromStylesheet)) {
            stylesheets.remove(fromStylesheet);
        }
        String toStylesheet = getClass().getResource(to).toExternalForm();
        stylesheets.add(toStylesheet);
    }

	/**
	 * Updates the text in the given ListView with the specified string.
	 *
	 * @param list the ListView in which the text will be updated
	 * @param string the new text to be displayed in the ListView
	 */
	private void updateText(ListView<String> list, String string) {
		list.getItems().clear();
		list.getItems().add(string);
	}
}