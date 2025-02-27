package main.java.zenit.ui.projectinfo;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.zenit.filesystem.FileController;
import main.java.zenit.filesystem.ProjectFile;
import main.java.zenit.filesystem.RunnableClass;
import main.java.zenit.filesystem.metadata.Metadata;
import main.java.zenit.ui.DialogBoxes;

/**
 * A class that controls the interactions and functionality of the Project Runnable Classes application component.
 * Extends AnchorPane to provide a base layout for adding JavaFX components.
 */
public class ProjectRunnableClassesController extends AnchorPane {
	private Stage stage;
	private ProjectFile projectFile;
	private FileController fileController;
	private double xOffset = 0, yOffset = 0;
	private boolean darkMode;

	@FXML private AnchorPane header;
	@FXML private ImageView logo;
	@FXML private TreeView<String> treeView;

	/**
	 * Constructor for ProjectRunnableClassesController.
	 *
	 * @param projectFile The project file associated with the controller.
	 * @param darkMode A boolean indicating whether dark mode is enabled.
	 * @param fileController The file controller used in the project.
	 */
	public ProjectRunnableClassesController(ProjectFile projectFile, boolean darkMode, FileController fileController) {
		this.projectFile = projectFile;
		this.darkMode = darkMode;
		this.fileController = fileController;
	}

	/**
	 * Sets up the scene for the application by loading the FXML file specified in the method and setting the controller.
	 *
	 * @return A new JavaFX Scene object with the loaded root AnchorPane.
	 * @throws IOException if an error occurs during loading the FXML file.
	 */
	private Scene setupScene() throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/zenit/ui/projectInfo/ProjectRunnableClasses.fxml"));
		loader.setController(this);
		AnchorPane root = (AnchorPane) loader.load();
		return new Scene(root);
	}

	/**
	 * Sets up a new stage for the application with the provided scene.
	 *
	 * @param scene The scene to be set in the stage.
	 * @return True if the stage setup was successful, false otherwise.
	 */
	private boolean setupStage(Scene scene) {
		stage = new Stage();
		stage.setResizable(false);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.setScene(scene);
		return true;
	}

	/**
	 * Starts the application by setting up the stage, initializing components, checking if dark mode is enabled, and showing the stage.
	 */
	public void start() {
		try {
			setupStage(setupScene());
			initialize();
			ifDarkModeChanged(darkMode);
			stage.showAndWait();
		} catch (IOException ignored) { }
	}

	/**
	 * Sets up the TreeView with sorting functionality based on the values of the TreeItems.
	 * It creates a root TreeItem from the project source file, sets the TreeView root to it,
	 * hides the root from being displayed, populates the tree with files from the source directory,
	 * and then sorts the children of the root based on the values of the TreeItems.
	 */
	private boolean setupTreeViewWithSorting() {
		RunnableClassTreeItem<String> root = new RunnableClassTreeItem<String>(
				projectFile.getSrc().getName(), projectFile.getSrc(), false);
		treeView.setRoot(root);
		treeView.setShowRoot(false);
		populateTree(projectFile.getSrc());

		root.getChildren().sort((o1,o2)->{
			RunnableClassTreeItem<String> t1 = (RunnableClassTreeItem<String>) o1;
			RunnableClassTreeItem<String> t2 = (RunnableClassTreeItem<String>) o2;
			return (t1.getValue().compareTo(t2.getValue()));
		});
		return true;
	}

	/**
	 * Sets event handlers for mouse press and drag actions on the header.
	 * The handler stores the initial mouse coordinates on press and moves the stage accordingly on drag.
	 *
	 * @return true if the mouse event handling was successful, false otherwise.
	 */
	private boolean handleMouseEvent() {
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
		return true;
	}

	/**
	 * Initializes the components by setting the logo image, setting up the TreeView with sorting functionality,
	 * and handling mouse press and drag events on the header.
	 */
	private void initialize() {
		logo.setImage(new Image(getClass().getResource("/zenit/setup/zenit.png").toExternalForm()));
		logo.setFitWidth(55);

		setupTreeViewWithSorting();
		handleMouseEvent();
	}

	/**
	 * Populates the tree with files from the specified root directory.
	 *
	 * @param root The root directory from which to start populating the tree.
	 */
	private void populateTree(File root) {
		File[] children = root.listFiles();
		for (File file : children) {
			addNode(file, treeView.getRoot());
		}
	}

	/**
	 * Checks if the given file is a Java class file that contains a main method.
	 *
	 * @param file The file to be checked.
	 * @return true if the file ends with '.java' and contains a main method, false otherwise.
	 */
	private boolean isRunnableClass(File file) {
		return file.getName().endsWith(".java")
				&& fileController.checkIfClassFileContainsMainMethod(file);
	}

	/**
	 * Adds a new node to the tree with the given parent node.
	 *
	 * @param file The file to create a node for.
	 * @param parent The parent node where the new node will be added.
	 */
	private void addNode(File file, TreeItem<String> parent) {
		String name = file.getName();
		boolean runnable = isRunnableClass(file);

		String fileNamePostfix = runnable ? " [runnable]" : " [not runnable]";
		name += file.getName().endsWith(".java") ? fileNamePostfix : "";

		RunnableClassTreeItem<String> node = new RunnableClassTreeItem<String>(name, file, runnable);
		parent.getChildren().add(node);
		
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File f : children) {
				addNode(f, node);
			}
		}
	}

	/**
	 * Updates the stylesheets of the stage's scene based on the dark mode setting.
	 *
	 * @param isDarkMode A boolean indicating whether dark mode is enabled.
	 */
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

	/**
	 * Retrieves the selected item from the tree view as a RunnableClassTreeItem.
	 * This method assumes that the tree view allows only RunnableClassTreeItem items to be selected.
	 *
	 * @return The selected RunnableClassTreeItem from the tree view.
	 */
	private RunnableClassTreeItem<String> getSelectedItem() {
		return (RunnableClassTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
	}

	/**
	 * Adds a new runnable class to the project metadata.
	 * If the selected item is a runnable class, it extracts necessary information
	 * such as the file path, creates a new RunnableClass object, adds it to the metadata,
	 * encodes the metadata, and then closes the window.
	 * If the selected item is not a runnable class, it displays an error dialog.
	 */
	@FXML
	private void add() {
		RunnableClassTreeItem<String> item = getSelectedItem();
		
		if (item.isRunnable()) {
			Metadata metadata = new Metadata(projectFile.getMetadata());
			String src = projectFile.getSrc().getPath();
			String filePath = item.getFile().getPath();
			filePath = filePath.replaceFirst(Matcher.quoteReplacement(src + File.separator), "");
			RunnableClass rc = new RunnableClass(filePath);
			metadata.addRunnableClass(rc);
			metadata.encode();
			close();
		} else {
			DialogBoxes.errorDialog("Class not runnable", "", "Select a class that is runnable "
					+ "(contains a main-method");
		}
	}

	/**
	 * Closes the stage associated with the method's controller.
	 * This method closes the JavaFX stage that is currently being displayed.
	 */
	@FXML
	private void close() {
		stage.close();
	}
}