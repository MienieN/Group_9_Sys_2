package main.java.zenit.ui;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
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
 * Controller class responsible for the "New File" creation window.
 * This class allows users to create a new file with a specified name and file type
 * in a specified workspace. It provides functionality to select a directory,
 * handle dark mode, and manage the creation of new files.
 */
public class NewFileController extends AnchorPane {

    private Stage stage;
    private boolean darkmode;

    @FXML
    private ImageView logo;
    @FXML
    private AnchorPane header;
    @FXML
    private ListView<String> filepath;
    @FXML
    private ComboBox<String> fileEnding;
    @FXML
    private TextField tfName;

    private double xOffset = 0;
    private double yOffset = 0;

    private File workspace;
    private File newFile;

    /**
     * Constructor for NewFileController, initializing workspace and dark mode settings.
     *
     * @param workspace The workspace directory where the new file will be created.
     * @param darkmode A boolean indicating whether dark mode is enabled or not.
     */
    public NewFileController(File workspace, boolean darkmode) {
        this.workspace = workspace;
        this.darkmode = darkmode;
    }

    /**
     * Opens the "New File" window and displays the UI to create a new file.
     * The window includes fields for entering a filename, selecting a file type,
     * and specifying the file's location.
     */
    public void start() {
        try {
            // Setup scene and load the FXML layout for the NewFile window
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/zenit/ui/NewFile.fxml"));
            loader.setController(this);
            AnchorPane root = (AnchorPane) loader.load();
            Scene scene = new Scene(root);

            // Set up the stage (window) for displaying the scene
            stage = new Stage();
            stage.setResizable(false);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);

            initialize();
            ifDarkModeChanged(darkmode);
            stage.showAndWait();

        } catch (IOException e) {

        }

    }

    /**
     * Initializes the UI components with default values and behavior.
     * Sets the workspace path, file types, and drag-and-drop behavior for the window header.
     */
    private void initialize() {
        logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png").toExternalForm()));
        logo.setFitWidth(45);

        filepath.getItems().clear();
        filepath.getItems().add(workspace.getPath());
        filepath.getSelectionModel().selectFirst();

        fileEnding.getItems().add(".txt");
        fileEnding.getItems().add(".java");
        fileEnding.getSelectionModel().select(".java");

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
     * Creates a new file with the name and file extension specified by the user.
     * It checks if the file already exists, and if not, creates the file in the chosen directory.
     * If any errors occur, an error dialog is displayed.
     */
    @FXML
    private void create() {
        String fileName = tfName.getText();

        if (!fileName.equals("")) {
            fileName += fileEnding.getSelectionModel().getSelectedItem();

            String filePath = this.filepath.getSelectionModel().getSelectedItem() + File.separator + fileName;
            newFile = new File(filePath);
            try {
                if (!newFile.createNewFile()) {
                    DialogBoxes.errorDialog("File name already exist", "", "A file with the name " + fileName + " already exist. Please input a different name.");
                    newFile = null;
                }
            } catch (IOException e) {
                DialogBoxes.errorDialog("Couldn't create new file", "", "Couldn't create new file");
                newFile = null;
            }

            stage.close();
        } else {
            DialogBoxes.errorDialog("No name selected", "", "No name has been given to the new file" + ". Please input a new name to create file.");
        }

    }

    /**
     * Closes the "New File" window without creating a file.
     */
    @FXML
    private void cancel() {
        stage.close();
    }

    /**
     * Opens a directory chooser dialog to select a directory in which to create the new file.
     * Updates the filepath field with the selected directory.
     */
    @FXML
    private void browse() {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(workspace);
        dc.setTitle("Select directory to create new file in");
        File chosen = dc.showDialog(stage);

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

    /**
     * Retrieves the newly created file.
     *
     * @return The newly created file, or null if no file was created.
     */
    public File getNewFile() {
        return newFile;
    }
}