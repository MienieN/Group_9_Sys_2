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
	private boolean darkMode, taUpdated = false;
	private double xOffset = 0, yOffset = 0;
	
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
	
	//Settings
	@FXML
	private void changeDirectory() {
		int returnValue = DialogBoxes.twoChoiceDialog("Internal directory", "Internal directory",
				"Do you want the new directory to be internal or external?", "Internal", "External");
		boolean internal;
		if (returnValue == 1) {
			internal = true;
		} else if (returnValue == 2) {
			internal = false;
		} else {
			return;
		}
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(projectFile.getBin());
		dc.setTitle("Choose new directory");
		
		File directory = dc.showDialog(propertyStage);
		
		if (directory != null) {
			String directoryPath = fileController.changeDirectory(directory, projectFile, internal);
			updateText(directoryPathList, directoryPath);
		}
	}

	@FXML
	private void changeSourcepath() {
		int returnValue = DialogBoxes.twoChoiceDialog("Internal sourcepath", "Internal sourcepath",
				"Do you want the new sourcepath to be internal or external?", 
				"Internal", "External");
		boolean internal;
		if (returnValue == 1) {
			internal = true;
		} else if (returnValue == 2) {
			internal = false;
		} else {
			return;
		}
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(projectFile.getSrc());
		dc.setTitle("Choose new sourcepath");
		
		File directory = dc.showDialog(propertyStage);
		
		if (directory != null) {
			String sourcepath = fileController.changeSourcePath(directory, projectFile, internal);
			updateText(sourcepathList, sourcepath);
		}
	}

	@FXML
	private void changeJREVersion() {
		String JDKName = JREVersions.getSelectionModel().getSelectedItem();
		String JDK = JDKDirectories.getFullPathFromName(JDKName);
		metadata.setJREVersion(JDK);
		metadata.encode();
	}
	
	@FXML
	private void addInternalLibrary() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(libraryFilter);

		List<File> selectedFiles = fc.showOpenMultipleDialog(propertyStage);
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

	@FXML
	private void removeInternalLibrary() {
		List<String> selectedLibraries = internalLibrariesList.getSelectionModel().getSelectedItems();
		
		if (selectedLibraries != null) {
			boolean success = fileController.removeInternalLibraries(selectedLibraries, projectFile);
			if (success) {
				metadata = new Metadata(metadata.getFile());
				updateLists();
			} else {
				ProjectInfoErrorHandling.removeInternalLibraryFail();
			}
		}
	}
	
	@FXML
	private void addExternalLibrary() {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(libraryFilter);

		List<File> selectedFiles = fc.showOpenMultipleDialog(propertyStage);
		if (selectedFiles != null) {
			boolean success = fileController.addExternalLibraries(selectedFiles, projectFile);
			if (success) {
				metadata = new Metadata(metadata.getFile());
				updateLists();
			} else {
				ProjectInfoErrorHandling.addExternalLibraryFail();
			}
		}
	}

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
	
	//Advanced settings
	@FXML
	private void save() {
		String pa = taProgramArguments.getText();
		String vma = taVMArguments.getText();
		
		selectedRunnableClass.setPaArguments(pa);
		selectedRunnableClass.setVmArguments(vma);
		
		metadata.setRunnableClasses(runnableClasses);
		boolean encoded = metadata.encode();
		
		if (encoded) {
			taUpdated = false;
			DialogBoxes.informationDialog("Arguments saved", "Arguments have been saved");
		} else {
			DialogBoxes.errorDialog(null, "Arguments not saved", "Arguments couldn't be saved");
		}	
	}

	@FXML
	private void run() {
		String runClass = getSelectedRunnableClass().getPath();
		String srcPath = projectFile.getSrc().getPath() + File.separator;
		
		if (runClass != null && srcPath != null) {
			File runFile = new File(srcPath + runClass);
			mainController.compileAndRun(runFile);
		}
	}
	
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
	}

	@FXML
	private void argumentsChanged() { taUpdated = true; }
	
	@FXML
	private void addRunnableClass() {
		new ProjectRunnableClassesController(projectFile, darkMode, fileController).start();
		updateLists();
	}
	
	@FXML
	private void removeRunnableClass() {
		String selected = runnableClassesList.getSelectionModel().getSelectedItem();
		
		if (selected != null) {
			metadata.removeRunnableClass(selected);
			metadata.encode();
			updateLists();
		}
	}
	
	@FXML
	private void close() { propertyStage.close(); }
	
	//Help classes
	private RunnableClass getSelectedRunnableClass() {
		String selected = runnableClassesList.getSelectionModel().getSelectedItem();

		if (runnableClasses != null) {

			for (RunnableClass runnableClass : runnableClasses) {
				if (runnableClass.getPath().equals(selected)) {
					return runnableClass;
				}
			}
		}
		return null;
	}
	
	public void ifDarkModeChanged(boolean isDarkMode) {
		var stylesheets = propertyStage.getScene().getStylesheets();
		var darkMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle.css").toExternalForm();
		var lightMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle-lm.css").toExternalForm();
		
		if (isDarkMode) {
			if (stylesheets.contains(lightMode)) {
				stylesheets.remove(lightMode);
			}
			
			stylesheets.add(darkMode);
		} else {
			if (stylesheets.contains(darkMode)) {
				stylesheets.remove(darkMode);
			}
			stylesheets.add(lightMode);
		}	
	}
	
	private void updateText(ListView<String> list, String string) {
		list.getItems().clear();
		list.getItems().add(string);
	}
}