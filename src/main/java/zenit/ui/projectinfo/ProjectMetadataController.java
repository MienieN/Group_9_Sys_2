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
	private MainController mc;
	private ProjectFile projectFile;
	private Metadata metadata;
	private RunnableClass[] runnableClasses;
	private RunnableClass selectedRunnableClass;
	private FileChooser.ExtensionFilter libraryFilter = new FileChooser.ExtensionFilter("Libraries", "*.jar", "*.zip");
	private boolean darkmode, taUpdated = false;
	private double xOffset = 0, yOffset = 0;
	
	@FXML private AnchorPane header;
	@FXML private ImageView logo;
	@FXML private Text title;
	@FXML private ListView<String> directoryPathList, sourcepathList,runnableClassesList, internalLibrariesList, externalLibrariesList;
	@FXML private TextArea taProgramArguments, taVMArguments;
	@FXML private ComboBox<String> JREVersions;
	@FXML private Button addInternalLibrary, removeInternalLibrary, addExternalLibrary, removeExternalLibrary, save, run;

	public ProjectMetadataController(FileController fc, ProjectFile projectFile, boolean darkmode, MainController mc) {
		this.projectFile = projectFile;
		fileController = fc;
		this.darkmode = darkmode;
		this.mc = mc;
	}

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

	private boolean verifyMetadataFile(File mdf) {
		int verification = MetadataVerifier.verify(metadata);
		File metadataFile = mdf;
		int returnCode;

		switch (verification) {
			case MetadataVerifier.VERIFIED:
				initialize();
				ifDarkModeChanged(darkmode);
				propertyStage.show();
				break;
			case MetadataVerifier.METADATA_FILE_MISSING: // no metadata file is found
				returnCode = ProjectInfoErrorHandling.metadataMissing();
				if (returnCode == 1) {
					metadataFile = projectFile.addMetadata();
					metadata = new Metadata(metadataFile);
					initialize();
					ifDarkModeChanged(darkmode);
					propertyStage.show();
				}
				break;
			case MetadataVerifier.METADATA_OUTDATED: // metadata file is outdated
				returnCode = ProjectInfoErrorHandling.metadataOutdated();
				if (returnCode == 1) {
					metadata = fileController.updateMetadata(metadataFile);
					initialize();
					ifDarkModeChanged(darkmode);
					propertyStage.show();
				}
				break;
            default:
				return false;
		}
		return true;
	}

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

	private void initialize() {
		
		title.setText(projectFile.getName() + " settings");
		
		logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png").toExternalForm()));
		logo.setFitWidth(55);
		
		String directory = metadata.getDirectory();
		if (directory != null) {
			updateText(directoryPathList, directory);
		}
		String sourcepath = metadata.getSourcePath();
		if (sourcepath != null) {
			updateText(sourcepathList, sourcepath);
		}

		updateLists();

		File JREVersion = new File(metadata.getJREVersion());
		
		List<String> JDKs = JDKDirectories.extractJDKDirectoryNameAsString();
		if (JREVersion != null) {
			JREVersions.getItems().addAll(JDKs);
			JREVersions.getSelectionModel().select(JREVersion.getName());
		}
		
		taProgramArguments.setText("<select a runnable class>");
		taProgramArguments.setEditable(false);
		taVMArguments.setText("<select a runnable class>");
		taVMArguments.setEditable(false);
		
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

	private void updateLists() {
		internalLibrariesList.getItems().clear();
		String[] internalLibraries = metadata.getInternalLibraries();
		if (internalLibraries != null) {
			internalLibrariesList.getItems().clear();
			internalLibrariesList.getItems().addAll(internalLibraries);
			internalLibrariesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
		
		externalLibrariesList.getItems().clear();
		String[] externalLibraries = metadata.getExternalLibraries();
		if (externalLibraries != null) {
			externalLibrariesList.getItems().addAll(externalLibraries);
			externalLibrariesList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		}
		
		metadata = new Metadata(metadata.getFile());
		runnableClasses = metadata.getRunnableClasses();
		if (runnableClasses != null) {
			runnableClassesList.getItems().clear();
			for (RunnableClass runnableClass : runnableClasses) {
				runnableClassesList.getItems().add(runnableClass.getPath());
			}
		}	
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
			mc.compileAndRun(runFile);
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
		new ProjectRunnableClassesController(projectFile, darkmode, fileController).start();
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