package main.java.zenit.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import main.java.zenit.filesystem.jreversions.JREVersions;

public class JREVersionsController extends AnchorPane {
	private Stage stage;
	private List<File> JVMs;
	private boolean darkMode;
	@FXML private ListView<String> JDKList;
	
	public JREVersionsController(boolean darkMode) {
		this.darkMode = darkMode;
	}
	
	public void start() {
		try {
			//setup scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/JREVersions.fxml"));
			loader.setController(this);
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);

			//set up stage
			stage = new Stage();
			stage.setResizable(false);
			stage.setScene(scene);
			
			initialize();
			
		} catch (IOException e) {
			System.out.println("Error JREVersionsController start() = " + e);
		}
	}
	
	private void initialize() {	
		ifDarkModeChanged(darkMode);
		updateList();
		stage.show();
	}
	
	// TODO Simplify if possible
	private void updateList() {
		JVMs = JREVersions.read();
		ArrayList<String> JVMsString = new ArrayList<String>();
		
		for (File JVM : JVMs) {
			JVMsString.add(JVM.getName());
		}
		
		JDKList.getItems().clear();
		JDKList.getItems().addAll(JVMsString);
		
		File defaultJDK = JREVersions.getDefaultJDKFile();
		
		if (defaultJDK != null) {
			String defaultName = defaultJDK.getName() + " [default]";
			JDKList.getItems().remove(defaultJDK.getName());
			JDKList.getItems().add(defaultName);
		}
		
		JDKList.getItems().sort((o1,o2)->{
			return o1.compareTo(o2);
		});
	}
	
	@FXML
	private void addJRE() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(JREVersions.getJVMDirectory());
		directoryChooser.setTitle("Select JDK to add");
		
		File selected = directoryChooser.showDialog(stage);
		
		if (selected != null) {
			boolean success = JREVersions.append(selected);
			if (success) {
				updateList();
			} else {
				DialogBoxes.errorDialog("JDK doesn't contain java or javac", "", "The selected JDK doesn't"
						+ "contain the needed java or javac executables");
			}
		}
	}
	
	// TODO Separate concerns and simplify
	@FXML
	private void removeJRE() {
		String selected = JDKList.getSelectionModel().getSelectedItem();
		File selectedFile = null;
		
		if (selected != null && selected.endsWith(" [default]")) {
			DialogBoxes.errorDialog("Can't remove default JDK", "", "Can't remove the default"
					+ "JDK, choose another default JDK to remove this one");
			return;
		}
		
		if (selected != null) {
			for (File JVM : JVMs) {
				if (JVM.getPath().endsWith(selected)) {
					selectedFile = JVM;
					break;
				}
			}
			if (selectedFile != null) { 
				boolean success = JREVersions.remove(selectedFile);
				if (success) {
					DialogBoxes.informationDialog("JDK removed from Zenit", "The JDK " + selected
							+ " has been removed from Zenit");
					updateList();
				} else {
					DialogBoxes.errorDialog("Couldn't remove JDK", "", "The JDK " + selected +
							" couldn't be removed from Zenit");
				}
			}
		} else {
			DialogBoxes.errorDialog("No JDK selected", "", "Select a JDK to remove from Zenit");
		}
	}
	
	// TODO simplify if possible
	@FXML
	private void selectDefaultJRE() {
		String selected = JDKList.getSelectionModel().getSelectedItem();
		File selectedFile = null;
		
		if (selected != null && selected.endsWith(" [default]")) {
			return;
		}
		
		if (selected != null) {
			for (File JVM : JVMs) {
				if (JVM.getPath().endsWith(selected)) {
					selectedFile = JVM;
					break;
				}
			}
		}
		if (selectedFile != null) {
			JREVersions.setDefaultJDKFile(selectedFile);
			updateList();
		}
	}
	
	public void ifDarkModeChanged(boolean isDarkMode) {
		var stylesheets = stage.getScene().getStylesheets();
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
}