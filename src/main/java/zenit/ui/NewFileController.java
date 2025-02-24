package main.java.zenit.ui;

import java.io.File;
import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.zenit.ui.tree.FileTreeItem;

public class NewFileController extends AnchorPane {
	private Stage stage;
	private File workspace, newFile;
	private boolean darkMode;
	private double xOffset = 0, yOffset = 0;
	@FXML private ImageView logo;
	@FXML private AnchorPane header;
	@FXML private ListView<String> filePath;
	@FXML private ComboBox<String> fileEnding;
	@FXML private TextField textFieldName;
	@FXML private TreeView<String> treeView;
	
	public NewFileController(File workspace, boolean darkMode, TreeView<String> treeView) {
		this.workspace = workspace;
		this.darkMode = darkMode;
		this.treeView = treeView;
	}
	
	public void start() {
		try {
			//setup scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/NewFile.fxml"));
			loader.setController(this);
			AnchorPane root = (AnchorPane) loader.load();
			Scene scene = new Scene(root);

			//set up stage
			stage = new Stage();
			stage.setResizable(false);
			stage.initStyle(StageStyle.UNDECORATED);
			stage.setScene(scene);
			
			initialize();
			ifDarkModeChanged(darkMode);
			stage.showAndWait();
				
		} catch (IOException e) {
			System.out.println("Error in NewFileController start() = " + e);
		}
	}

	// YRJA: Broke this down into smaller methods for readability
	private void initialize() {
		initializeLogoFilePathAndEnding();
		initializeHeader();
	}

	private void initializeLogoFilePathAndEnding() {
		logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png").toExternalForm()));
		logo.setFitWidth(45);

		filePath.getItems().clear();
		filePath.getItems().add(workspace.getPath());
		filePath.getSelectionModel().selectFirst();

		fileEnding.getItems().add(".txt");
		fileEnding.getItems().add(".java");
		fileEnding.getSelectionModel().select(".java");
	}

	private void initializeHeader() {
		header.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});

		header.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				stage.setX(event.getScreenX() - xOffset);
				stage.setY(event.getScreenY() - yOffset);
			}
		});
	}

	public File createNewFile() {
		String filename = textFieldName.getText();

		if (!filename.equals("")) {
			filename += fileEnding.getSelectionModel().getSelectedItem();
			String filePath = this.filePath.getSelectionModel().getSelectedItem() + File.separator + filename;
			newFile = new File(filePath);
			try {
				if (!newFile.createNewFile()) {
					showErrorDialog("File name already exist", "A file with the name " + filename
							+ " already exist. Please input a different name.");
					newFile = null;
				}
			} catch (IOException e) {
				showErrorDialog("Couldn't create new file", "Couldn't create new file");
				newFile = null;
			}
			return newFile;
		} else {
			showErrorDialog("No name selected", "No name has been given to the new file. " +
					"Please input a new name to create file.");
			return null;
		}
	}

	private void showErrorDialog(String title, String content) {
		DialogBoxes.errorDialog(title, "", content);
	}

	@FXML
	private void create() {
		createNewFile();
		stage.close();
	}
	
	@FXML
	private void cancel() { stage.close(); }
	
	@FXML
	private void browse() {
		DirectoryChooser dc = new DirectoryChooser();
		dc.setInitialDirectory(workspace);
		dc.setTitle("Select directory to create new file in");
		File chosen = dc.showDialog(stage);
		
		if (chosen != null) {
			filePath.getItems().clear();
			filePath.getItems().add(chosen.getPath());
			filePath.getSelectionModel().selectFirst();
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
	
	public File getNewFile() { return newFile; }

	public FileTreeItem<String> getSelectedFileTreeItem() {
		return (FileTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
	}
}