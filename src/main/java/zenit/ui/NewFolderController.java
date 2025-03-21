package main.java.zenit.ui;

import java.io.File;
import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NewFolderController extends AnchorPane {
	public Stage stage;
	private File workspace;
	private boolean darkMode;
	private double xOffset = 0, yOffset = 0;
	@FXML private ImageView logo;
	@FXML private AnchorPane header;
	@FXML
    public ListView<String> filePath;
	@FXML private TextField textFieldName;
	
	public NewFolderController(File workspace, boolean darkMode) {
		this.workspace = workspace;
		this.darkMode = darkMode;
	}
	
	public void start() {
		try {
			//setup scene
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/zenit/ui/NewFolder.fxml"));
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
			System.out.println("Error NewFolderController start() = " + e);
		}
	}

	private void initialize() {
		logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png").toExternalForm()));
		logo.setFitWidth(45);
		
		filePath.getItems().clear();
		filePath.getItems().add(workspace.getPath());
		filePath.getSelectionModel().selectFirst();
		
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
			   stage.setX(event.getScreenX() - xOffset);
			   stage.setY(event.getScreenY() - yOffset);
		   }
		});
	}
	
	@FXML
	private void create() {
		String folderName = textFieldName.getText();
		
		if (!folderName.equals("")) {
			
			String filePath = this.filePath.getSelectionModel().getSelectedItem() + File.separator
					+ folderName;
			File newFolder = new File(filePath);

			if (!newFolder.mkdir()) {
				DialogBoxes.errorDialog("Folder name already exist", "", "A folder with the name "
						+ folderName + " already exist. Please input a different name.");
			}
			
			stage.close();
		} else {
			DialogBoxes.errorDialog("No name selected", "", "No name has been given to the new "
					+ "folder. Please input a new name to create folder.");
		}
	}
	
	@FXML
	private void cancel() { stage.close(); }
	
	@FXML
	private void browse() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(workspace);
		directoryChooser.setTitle("Select directory to create new folder in");
		File chosen = directoryChooser.showDialog(stage);
		
		if (chosen != null) {
			filePath.getItems().clear();
			filePath.getItems().add(chosen.getPath());
			filePath.getSelectionModel().selectFirst();
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
}