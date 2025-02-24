package main.java.zenit.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.java.zenit.filesystem.jreversions.JDKDirectories;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JREVersionsControllerStart extends AnchorPane {
	private Stage stage;
	private List<File> JVMs;
	private boolean projectCreated = false, darkMode;
	@FXML private ListView<String> JDKList;
	@FXML private Button createProjectButton;
	
	public boolean isProjectCreated() {
		return projectCreated;
	}

	public JREVersionsControllerStart(boolean darkmode) {
		this.darkMode = darkmode;
	}

	public void start() {
		if (stage != null && stage.isShowing()) { return; }

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/JREVersionsStartUp.fxml"));
			loader.setController(this);
			AnchorPane root = loader.load();
			Scene scene = new Scene(root);

			stage = new Stage();
			stage.setResizable(false);
			stage.setScene(scene);

			initialize();
			stage.showAndWait();

		} catch (IOException e) {
			System.out.println("Error JREVersionsControllerStart start() = " + e);
		}
	}
	
	private void initialize() {	
		ifDarkModeChanged(darkMode);
		updateList();
	}

	private void updateList() {
		JVMs = JDKDirectories.readJDKInstallationDirectoriesFromFile();
		List<String> JVMsString = getJVMNames(JVMs);

		JDKList.getItems().clear();
		JDKList.getItems().addAll(JVMsString);

		File defaultJDK = JDKDirectories.getDefaultJDKFile();
		if (defaultJDK != null) {
			updateDefaultJDK(defaultJDK);
		}

		JDKList.getItems().sort(String::compareTo);
	}

	private List<String> getJVMNames(List<File> JVMs) {
		return JVMs.stream().map(File::getName).collect(Collectors.toList());
	}

	private void updateDefaultJDK(File defaultJDK) {
		String defaultJDKName = defaultJDK.getName() + " [default]";
		JDKList.getItems().remove(defaultJDKName);
		JDKList.getItems().add(defaultJDKName);
	}

	@FXML
	private void addJRE() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(JDKDirectories.getJVMDirectory());
		directoryChooser.setTitle("Select JDK to add");

		File selected = directoryChooser.showDialog(stage);

		if (selected != null) {
			boolean success = JDKDirectories.appendToTrackedDirectoriesList(selected);
			if (success) {
				updateList();
			} else {
				showErrorDialog("JDK doesn't contain java or javac", "", "The selected JDK doesn't contain the needed java or javac executables");
			}
		}
	}

	@FXML
	private void createProject() {
		projectCreated = true;
		if (stage != null) { stage.close(); }
	}

	@FXML
	private void removeJRE() {
		String selected = JDKList.getSelectionModel().getSelectedItem();

		if (selected == null) {
			showErrorDialog("No JDK selected", "", "Select a JDK to remove from Zenit");
			return;
		}

		if (selected.endsWith(" [default]")) {
			showErrorDialog("Can't remove default JDK", "", "Can't remove the default JDK, choose another default JDK to remove this one");
			return;
		}

		File selectedFile = findSelectedFile(selected);
		if (selectedFile != null) {
			removeJDK(selected, selectedFile);
		}
	}

	private File findSelectedFile(String selected) {
	    for (File JVM : JVMs) {
	        if (JVM.getPath().endsWith(selected)) {
	            return JVM;
	        }
	    }
	    return null;
	}

	private void removeJDK(String selected, File selectedFile) {
		boolean success = JDKDirectories.removeFromTrackedDirectoriesList(selectedFile);
		if (success) {
			DialogBoxes.informationDialog("JDK removed from Zenit", "The JDK " + selected + " has been removed from Zenit");
			updateList();
		} else {
			showErrorDialog("Couldn't remove JDK", "", "The JDK " + selected + " couldn't be removed from Zenit");
		}
	}

	/**
	 * This method selects a default JRE from the list,
	 * checks if it is already the default, and if not,
	 * sets it as the default JRE and updates the list.
	 */
	@FXML
	private void selectDefaultJRE() {
		String selected = JDKList.getSelectionModel().getSelectedItem();
		
		if (selected != null && selected.endsWith(" [default]")) { return; }

		File selectedFile = JVMs.stream()
				.filter(JVM -> JVM.getPath().endsWith(selected))
				.findFirst()
				.orElse(null);

		if (selectedFile != null) {
			JDKDirectories.setDefaultJDKFile(selectedFile);
			updateList();
		}
	}
	
	public void ifDarkModeChanged(boolean isDarkMode) {
		var styleSheets = stage.getScene().getStylesheets();
		var darkMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle.css").toExternalForm();
		var lightMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle-lm.css").toExternalForm();

		if (isDarkMode) {
			if (styleSheets.contains(lightMode)) {
				styleSheets.remove(lightMode);
			}
			
			styleSheets.add(darkMode);
		} else {
			if (styleSheets.contains(darkMode)) {
				styleSheets.remove(darkMode);
			}
			styleSheets.add(lightMode);
		}	
	}

	private void showErrorDialog(String title, String header, String content) {
		DialogBoxes.errorDialog(title, header, content);
	}
}