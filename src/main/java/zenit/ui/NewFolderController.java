package main.java.zenit.ui;

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

import java.io.File;
import java.io.IOException;

/**
 * Controller class responsible for handling the "New Folder" creation window.
 * This class allows users to create a new folder within a specified workspace.
 * Users can specify the folder name, browse for a directory, and handle dark mode settings.
 */
public class NewFolderController extends AnchorPane {
    private Stage stage;
    private File workspace;
    private boolean darkMode;
    private double xOffset = 0, yOffset = 0;
    @FXML
    private ImageView logo;
    @FXML
    private AnchorPane header;
    @FXML
    private ListView<String> filepath;
    @FXML
    private TextField tfName;

    /**
     * Constructor for the NewFolderController.
     *
     * @param workspace The workspace directory where the new folder will be created.
     * @param darkMode  A boolean indicating whether dark mode is enabled.
     */
    public NewFolderController(File workspace, boolean darkMode) {
        this.workspace = workspace;
        this.darkMode = darkMode;
    }

    /**
     * Initializes and displays the "New Folder" window.
     * Loads the FXML layout, applies dark mode if needed, and waits for user input.
     */
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

    /**
     * Initializes UI components, sets default values, and enables window dragging functionality.
     */
    private void initialize() {
        logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png").toExternalForm()));
        logo.setFitWidth(45);

        filepath.getItems().clear();
        filepath.getItems().add(workspace.getPath());
        filepath.getSelectionModel().selectFirst();

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

    /**
     * Creates a new folder with the user-specified name in the selected directory.
     * If a folder with the same name already exists, an error dialog is displayed.
     */
    @FXML
    private void create() {
        String folderName = tfName.getText();

        if (!folderName.equals("")) {
            String filePath = this.filepath.getSelectionModel().getSelectedItem() + File.separator
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

    /**
     * Closes the "New Folder" window without creating a folder.
     */
    @FXML
    private void cancel() {
        stage.close();
    }

    /**
     * Opens a directory chooser dialog to allow users to select a directory for the new folder.
     * Updates the filepath field with the selected directory.
     */
    @FXML
    private void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(workspace);
        directoryChooser.setTitle("Select directory to create new folder in");
        File chosen = directoryChooser.showDialog(stage);

        if (chosen != null) {
            filepath.getItems().clear();
            filepath.getItems().add(chosen.getPath());
            filepath.getSelectionModel().selectFirst();
        }
    }

    /**
     * Updates the style of the window based on whether dark mode is enabled.
     *
     * @param isDarkMode A boolean indicating whether dark mode is enabled or not.
     */
    public void ifDarkModeChanged(boolean isDarkMode) {
        var stylesheets = stage.getScene().getStylesheets();
        var darkMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle.css").toExternalForm();
        var lightMode = getClass().getResource("/zenit/ui/projectinfo/mainStyle-lm.css").toExternalForm();

        stylesheets.removeIf(s -> s.equals(darkMode) || s.equals(lightMode)); // Remove both first
        stylesheets.add(isDarkMode ? darkMode : lightMode); // Add the correct one
    }
}