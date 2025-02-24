package main.java.zenit.ui;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.LinkedList;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.IndexRange;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.java.zenit.Zenit;
import main.java.zenit.console.ConsoleArea;
import main.java.zenit.console.ConsoleAndTerminalController;
import main.java.zenit.filesystem.FileController; // Aggregation
import main.java.zenit.filesystem.ProjectFile;
import main.java.zenit.filesystem.RunnableClass;
import main.java.zenit.filesystem.WorkspaceHandler;
import main.java.zenit.filesystem.metadata.Metadata;
import main.java.zenit.javacodecompiler.DebugError;
import main.java.zenit.javacodecompiler.DebugErrorBuffer;
import main.java.zenit.javacodecompiler.JavaSourceCodeCompiler;
import main.java.zenit.javacodecompiler.ProcessBuffer;
import main.java.zenit.settingspanel.SettingsPanelController;
import main.java.zenit.settingspanel.ThemeCustomizable; // Implements
import main.java.zenit.searchinfile.Search;
import main.java.zenit.ui.tree.FileTree;
import main.java.zenit.ui.tree.FileTreeItem;
import main.java.zenit.ui.tree.TreeClickListener;
import main.java.zenit.ui.tree.TreeContextMenu;
import main.java.zenit.util.Tuple;
import main.java.zenit.ui.projectinfo.ProjectMetadataController;
import main.java.zenit.zencodearea.ZenCodeArea;

public class MainController extends VBox implements ThemeCustomizable {
	private Stage stage;
	private FileController fileController;
	private NewFileController newFileController;
	private ProjectMetadataController projectMetadataController;
	private String zenCodeAreasFontFamily, activeStylesheet;
	private LinkedList<ZenCodeArea> activeZenCodeAreas;
	private File customThemeCSS;
	private Process process;
	private Tuple<File, String> deletedFile = new Tuple<>();
	private boolean isDarkMode = true;
	private int zenCodeAreasTextSize;
	@FXML private AnchorPane consolePane;
	@FXML private SplitPane splitPane;
	@FXML private MenuItem newTab, newFile, newFolder, newProject, openFile, saveFile,
			importProject, changeWorkspace, JREVersions, undo, redo, delete;
	@FXML private CheckMenuItem checkMenuItemDarkMode;
	@FXML private TabPane tabPane;
	@FXML private TreeView<String> treeView;
	@FXML private Button btnRun, btnStop;
	@FXML private ConsoleAndTerminalController consoleAndTerminalController;
	@FXML private Label statusBarLeftLabel, statusBarRightLabel;
	@FXML private FXMLLoader loader;

	/**
	 * Initializes the workspace, sets up the file controller, and loads the main FXML layout.
	 * @param stage the primary stage for this application
	 */
	public MainController(Stage stage) {
		this.stage = stage;
		this.zenCodeAreasTextSize = 12;
		this.zenCodeAreasFontFamily = "Menlo";
		this.activeZenCodeAreas = new LinkedList<>();
		this.customThemeCSS = new File("/customtheme/mainCustomTheme.css");
		this.newFileController = new NewFileController(fileController.getWorkspace(), isDarkMode, this.treeView);

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/zenit/ui/Main.fxml"));
			File workspace = initializeWorkspace(); // Initialize workspace
			this.fileController = new FileController(workspace); // Set up the file controller
			setFileController(fileController);

			if (workspace != null) {
				fileController.changeWorkspace(workspace); // Change to the initialized workspace
			}

			setupLoader(loader);
			setupScene(loader);
			initialize();
			setupStage();
		} catch (Exception e) {
			System.out.println("Error in MainController = " + e);
		}
	}

	/**
	 * Initializes the workspace by reading the existing workspace or setting up a new one.
	 * If no workspace is found, it starts the JRE version selector and creates a new project.
	 *
	 * @return the File object representing the initialized workspace, or null if the workspace is not created
	 * @throws IOException if an I/O error occurs during workspace initialization
	 */
	private File initializeWorkspace() throws IOException {
		File workspace = WorkspaceHandler.readWorkspace();
		if (workspace == null) {
			JREVersionsControllerStart jdkSelector = new JREVersionsControllerStart(false);
			jdkSelector.start();
			if (!jdkSelector.isProjectCreated()) {
				return null; // Return null if the project is not created
			}

			workspace = WorkspaceHandler.setUpNewWorkspace();
			this.fileController = new FileController(workspace);
			fileController.createProject("New Project");
		}
		return workspace;
	}

	/**
	 * Sets up the FXMLLoader by setting the root and controller to this instance and loading the FXML file.
	 *
	 * @param loader the FXMLLoader instance to be set up
	 * @throws IOException if an I/O error occurs during loading
	 */
	private void setupLoader(FXMLLoader loader) throws IOException {
		loader.setRoot(this);
		loader.setController(this);
		loader.load();
	}

	/**
	 * Sets up the scene by applying stylesheets and setting the stage title.
	 * The scene is created using the current instance as the root.
	 *
	 * @param loader the FXMLLoader instance used to load the FXML file
	 */
	private void setupScene(FXMLLoader loader) {
		Scene scene = new Scene(this);
		scene.getStylesheets().add(getClass().getResource("/zenit/ui/mainStyle.css").toString());
		scene.getStylesheets().add(getClass().getResource("/zenit/ui/keywords.css").toExternalForm());
		this.stage.setScene(scene);
		this.stage.setTitle("Zenit - " + fileController.getWorkspace().getPath());
		this.activeStylesheet = getClass().getResource("/zenit/ui/mainStyle.css").toExternalForm();
	}

	/**
	 * Sets up the stage by showing it, setting up keyboard shortcuts, and defining the close request action.
	 */
	private void setupStage() {
		this.stage.show();
		KeyboardShortcuts.setupMain(this.stage.getScene(), this);
		this.stage.setOnCloseRequest(event -> quit());
	}

	public FXMLLoader getFXMLLoader() {
		return loader;
	}
	
	public void setFileController(FileController fileController) {
		this.fileController = fileController;
	}

	/**
	 * Returns the currently selected file tree item.
	 * @return the currently selected file tree item
	 */
	public FileTreeItem<String> getSelectedFileTreeItem() {
		return newFileController.getSelectedFileTreeItem();
	}

	/**
	 * Deletes the currently selected file from the file tree view.
	 * If a file is selected, it is deleted from the file system and removed from the tree view.
	 */
	public void deleteFileFromTreeView() {
		var selectedItem = getSelectedFileTreeItem();
		if (selectedItem != null) {
			deleteFile(selectedItem.getFile());
			selectedItem.getParent().getChildren().remove(selectedItem);
		}
	}

	/**
	 * This  method sets up the initial state of the user interface components, including clearing status labels,
	 * configuring buttons, initializing the file tree, and setting the main controller for the console and terminal controller.
	 */
	public void initialize() {
		statusBarLeftLabel.setText("");
		statusBarRightLabel.setText("");

		btnRun.setPickOnBounds(true);
		btnRun.setOnAction(event -> compileAndRun());
		btnStop.setOnAction(event -> terminate());
		initTree();
		consoleAndTerminalController.setMainController(this);
	}

	public void openSettingsPanel() {
		new SettingsPanelController(this, zenCodeAreasTextSize, zenCodeAreasFontFamily, consoleAndTerminalController);
	}

	public synchronized void setFontSize(int newFontSize) {
		zenCodeAreasTextSize = newFontSize;
		updateZenCodeAreasAppearance();
	}
	
	public synchronized void setFontFamily(String newFontFamily) {
		zenCodeAreasFontFamily = newFontFamily;
		updateZenCodeAreasAppearance();
	}

	public void updateZenCodeAreasAppearance() {
        for (ZenCodeArea activeZenCodeArea : activeZenCodeAreas) {
            activeZenCodeArea.updateAppearance(zenCodeAreasFontFamily, zenCodeAreasTextSize);
        }
	}

	public Stage getStage() { return stage; }

	public ZenCodeArea createNewZenCodeArea() {
		ZenCodeArea zenCodeArea = new ZenCodeArea(zenCodeAreasTextSize, zenCodeAreasFontFamily);
		activeZenCodeAreas.add(zenCodeArea);
		return zenCodeArea;
	}

	/**
	 * Initializes the tree view with the workspace as the root. Populates the tree with files
	 * and directories, sets up a context menu, and sorts the children alphabetically.
	 */
	private void initTree() {
		FileTreeItem<String> rootItem = new FileTreeItem<String>(fileController.getWorkspace(), "workspace",
				FileTreeItem.WORKSPACE);
		File workspace = fileController.getWorkspace();
		if (workspace != null) {
			FileTree.createNodes(rootItem, workspace);
		}
		
		treeView.setRoot(rootItem);
		treeView.setShowRoot(false);
		TreeContextMenu treeContextMenu = new TreeContextMenu(this, treeView);
		TreeClickListener treeClickListener = new TreeClickListener(this, treeView);
		treeView.setContextMenu(treeContextMenu);
		treeView.setOnMouseClicked(treeClickListener);
		
		rootItem.getChildren().sort((o1,o2)->{
			FileTreeItem<String> t1 = (FileTreeItem<String>) o1;
			FileTreeItem<String> t2 = (FileTreeItem<String>) o2;
			return (t1.getValue().compareTo(t2.getValue()));
		});
	}

	/**
	 * This method creates a new file in the specified parent directory with the given type code.
	 * It prompts the user to enter a file name and creates the file with the specified type code.
	 * If the file is created successfully, it is opened in a new tab.
	 * @param parent the parent directory in which to create the file
	 * @param typeCode the type code of the file to create
	 * @return the created file, or null if the file was not created
	 */
	public File createFile(File parent, int typeCode) {
		File file = null;
		String className = DialogBoxes.inputDialog(null, "New file", "Create new file", "Enter new file name",
				"File name");
		if (className != null) {
			String filepath = parent.getPath() + "/" + className;
			file = new File(filepath);
			file = fileController.createFile(file, typeCode);
			openFile(file);
		}
		return file;
	}

	public void shortcutsTrigger() {
		FileTab selectedTab = getSelectedTab();
		if (selectedTab != null) { selectedTab.shortcutsTrigger(); }
	}

	public void commentsShortcutsTrigger() {
		FileTab selectedTab = getSelectedTab();
		if (selectedTab != null) { selectedTab.commentsShortcutsTrigger(); }
	}

	public void navigateToCorrectTabIndex() {
		FileTab selectedTab = getSelectedTab();
		if (selectedTab != null) { selectedTab.navigateToCorrectTabIndex(); }
	}

	@FXML
	public boolean saveFile(Event event) {
		return saveFile(true);
	}

	/**
	 * Internal method to handle the common logic for saving a file.
	 *
	 * @param backgroundCompile whether to compile the file in the background
	 * @param file the file to save
	 * @param text the text to save in the file
	 * @return true if the file was successfully saved, false otherwise
	 */
	private boolean saveFileInternal(boolean backgroundCompile, File file, String text) {
		boolean didWrite = fileController.writeFile(file, text);

		if (didWrite) {
			FileTreeItem<String> root = FileTree.getTreeItemFromFile((FileTreeItem<String>) treeView.getRoot(), file.getParentFile());
			System.out.println(root);
			FileTree.createParentNode(root, file);
			treeView.refresh();
			treeView.layout();

			if (backgroundCompile) {
				backgroundCompiling(file);
			}
		} else {
			System.out.println("Did not write.");
		}
		return didWrite;
	}

	/**
	 * Saves the currently selected file.
	 *
	 * @param backgroundCompile whether to compile the file in the background
	 * @return true if the file was successfully saved, false otherwise
	 */
	private boolean saveFile(boolean backgroundCompile) {
		FileTab tab = getSelectedTab();
		if (tab == null) {
			return false;
		}

		File file = tab.getFile();
		if (file == null) {
			file = chooseFile();
		}

		return saveFileInternal(backgroundCompile, file, tab.getFileText());
	}

	/**
	 * Saves the specified file with the given text.
	 *
	 * @param backgroundCompile whether to compile the file in the background
	 * @param file the file to save
	 * @param text the text to save in the file
	 * @return true if the file was successfully saved, false otherwise
	 */
	private boolean saveFile(boolean backgroundCompile, File file, String text) {
		if (file == null) {
			return saveFile(backgroundCompile);
		}

		return saveFileInternal(backgroundCompile, file, text);
	}

	/**
	 * This method compiles the specified file in the background.
	 * If the file is not null, it creates a new JavaSourceCodeCompiler instance and starts the compilation process.
	 * @param file the file to compile
	 */
	private void backgroundCompiling(File file) {
		File metadataFile = getMetadataFile(file);

		try {
			if (file != null) {
				DebugErrorBuffer buffer = new DebugErrorBuffer();
				JavaSourceCodeCompiler compiler = new JavaSourceCodeCompiler(file, metadataFile, true, buffer, this);
				compiler.startCompile();
			}
		} catch (Exception e) {
			System.out.println("Error in MainController backgroundCompiling() = " + e);
		}
	}

	public void errorHandler(DebugErrorBuffer buffer) {
		DebugError error;
		while (!buffer.isEmpty()) {
			error = buffer.get();

			getSelectedTab().setStyle(error.getRow(), error.getColumn(), "underline");
		}
	}

	private File chooseFile() {
		FileChooser fileChooser = new FileChooser();
		File workspace = fileController.getWorkspace();
		if (workspace != null) { fileChooser.setInitialDirectory(fileController.getWorkspace()); }
		return fileChooser.showSaveDialog(stage);
	}

	@FXML
	public void newTab(Event event) { addTab(); }

	@FXML
	private void newFile() {
		newFileController.start();
		File newFile = newFileController.createNewFile();

		if (newFile != null) {
			initTree();
			openFile(newFile);
		}
	}
	
	@FXML
	public void newFolder() {
		new NewFolderController(fileController.getWorkspace(), isDarkMode).start();
		initTree();
	}
	
	@FXML
	public void quit() { System.exit(0); }
	
	@FXML
	public void openFile(Event event) {
		try {
			FileChooser fileChooser = new FileChooser();
			File workspace = fileController.getWorkspace();
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text files", "*.txt", "*.java");
			fileChooser.getExtensionFilters().add(extFilter);
			
			if (workspace != null) {
				fileChooser.setInitialDirectory(fileController.getWorkspace());
			}
			File file = fileChooser.showOpenDialog(stage);

			if (file != null) {
				openFile(file); }

		} catch (NullPointerException ex) { ex.printStackTrace(); }
	}

	/**
	 * This method opens the specified file in a new tab if it is not already open.
	 * @param file the file to open
	 */
	public void openFile(File file) {
		if (file != null && getTabFromFile(file) == null) {
			if (supportedFileFormat(file)) {
				openSupportedFile(file);
			} else {
				showUnsupportedFileDialog(file);
			}
		} else if (file != null) {
			tabPane.getSelectionModel().select(getTabFromFile(file));
		}
	}

	/**
	 * This method handles the logic of opening the specified file in a new tab.
	 * @param file the file to open
	 */
	private void openSupportedFile(File file) {
		FileTab selectedTab = addTab();
		selectedTab.setFile(file, true);
		selectedTab.setText(file.getName());
	}

	/**
	 * This method shows an error dialog for unsupported file types.
	 * @param file the file with the unsupported file type
	 */
	private void showUnsupportedFileDialog(File file) {
		String fileType = file.getName().substring(file.getName().lastIndexOf('.'));
		DialogBoxes.errorDialog("Not supported", "File type not supported by Zenit",
				"The file type " + fileType + " is not yet supported by this application.");
	}

	private boolean supportedFileFormat(File file) {
		boolean supported = false;

		String fileName = file.getName();
		int periodIndex = fileName.lastIndexOf('.');
		if (periodIndex >= 0) {
			String fileType = fileName.substring(periodIndex);

			switch (fileType) {
				case ".java":
				case ".txt": supported = true; break;
				default:
			}
		} else if (file.isFile()){ supported = true; }
		return supported;
	}

	public File renameFile(File file) {
		File newFile = null;
		int prefixPosition = file.getName().lastIndexOf('.');

		String newName = DialogBoxes.inputDialog(null, "New name", "Rename file", "Enter a new name", file.getName(), 0,
				prefixPosition);
		if (newName != null) {
			newFile = fileController.renameFile(file, newName);
			var tabs = tabPane.getTabs();
			for (Tab tab : tabs) {
				FileTab fileTab = (FileTab) tab;
				if (fileTab.getText().equals(file.getName())) {
					fileTab.setText(newName);
					fileTab.setFile(newFile, false);
					break;
				}
			}
		}
		return newFile;
	}

	public void deleteFile(File file) {
		/* TODO: Can we remove this?
		deletedTexts.put(file, FileController.readFile(file));
		fileHistory.add(0, file);
		historyIndex++;
		System.out.println(historyIndex);
		 */
		
		deletedFile.set(file, FileController.readFile(file));
		fileController.deleteFile(file);
		var tabs = tabPane.getTabs();
		
		if (tabs != null) {
			for (var tab : tabs) {
				var fileTab = (FileTab) tab;
				
				if (fileTab != null && fileTab.getFile().equals(file)) {
					Platform.runLater(() -> closeTab(null));
					return;
				}
			}
		}
	}
	
	public void undoDeleteFile() {
		if (!treeView.isFocused()) {
			System.out.println("not focused");
			return;
		}
		
		if (deletedFile.fst() != null && !deletedFile.fst().exists()) {
			try {
				deletedFile.fst().createNewFile();
				fileController.writeFile(deletedFile.fst(), deletedFile.snd());
				saveFile(false, deletedFile.fst(), deletedFile.snd());
			} catch (IOException e) {
				System.out.println("Error MainController undoDeleteFile() = " + e);
			}
		}
	}
	
	public void redoDeleteFile() {
		if (!treeView.isFocused()) {
			System.out.println("not focused");
			return;
		}
		
		if (deletedFile.fst() != null && deletedFile.fst().exists()) {
			deleteFile(deletedFile.fst());
			FileTree.removeFromFile((FileTreeItem<String>) treeView.getRoot(), deletedFile.fst());
		}
	}

	@FXML
	public void newProject(Event event) {
		String projectName = DialogBoxes.inputDialog(null, "New project", "Create new project",
				"Enter a new project name", "Project name");
		if (projectName != null) {
			File newProject = fileController.createProject(projectName);
			if (newProject != null) {
				FileTree.createParentNode((FileTreeItem<String>) treeView.getRoot(), newProject);
			}
		}
	}
	
	@FXML
	public void undo(Event event) {
		FileTab selectedTab = getSelectedTab();
		ZenCodeArea zenCodeArea = selectedTab == null ? null : selectedTab.getZenCodeArea();
		
		if (treeView.isFocused()) {
			undoDeleteFile();
		} else if (zenCodeArea != null && zenCodeArea.isFocused()) {
			if (zenCodeArea.isUndoAvailable()) {
				zenCodeArea.undo();
			}
		}
	}
	
	@FXML
	public void redo(Event event) {
		FileTab selectedTab = getSelectedTab();
		ZenCodeArea zenCodeArea = selectedTab == null ? null : selectedTab.getZenCodeArea();
		
		if (treeView.isFocused()) {
			redoDeleteFile();
		} else if (zenCodeArea != null && zenCodeArea.isFocused()) {
			if (zenCodeArea.isRedoAvailable()) {
				zenCodeArea.redo();
			}
		}
	}
	
	@FXML
	public void delete(Event event) { deleteFileFromTreeView(); }

	public File newPackage(File parent) {
		String packageName = DialogBoxes.inputDialog(null, "New package", "Create new package",
				"Enter new package name", "Package name");
		if (packageName != null) {
			String filepath = parent.getPath() + "/" + packageName;
			File packageFile = new File(filepath);

			boolean success = fileController.createPackage(packageFile);

			if (success) { return packageFile; }
		}
		return null;
	}
	
	// TODO Break it down if possible
	public void compileAndRun(File file) {
		File metadataFile = getMetadataFile(file);
		ConsoleArea consoleArea;
		
		if(isDarkMode) {
			consoleArea = new ConsoleArea(file.getName(), null, "-fx-background-color:#444");
		}
		else {
			consoleArea = new ConsoleArea(file.getName(), null, "-fx-background-color:#989898");
		}
		consoleArea.setFileName(file.getName());
		consoleAndTerminalController.createNewConsoleArea(consoleArea);
		openConsoleComponent();
		
		try {
			ProcessBuffer buffer = new ProcessBuffer();
			JavaSourceCodeCompiler compiler = new JavaSourceCodeCompiler(file, metadataFile,
					false, buffer, this);
			compiler.startCompileAndRun();
			process = buffer.get();
			
			if (process != null && metadataFile != null) {
				ProjectFile projectFile = new ProjectFile(metadataFile.getParent());
				String src = projectFile.getSrc().getPath();
				String filePath = file.getPath().replaceAll(Matcher.quoteReplacement(src + 
						File.separator), "");
				RunnableClass rc = new RunnableClass(filePath);
				Metadata metadata = new Metadata(metadataFile);
				
				if (metadata.addRunnableClass(rc)) { metadata.encode(); }
				consoleArea.setProcess(process);
				
				if(consoleArea.getProcess().isAlive()) {
					consoleArea.setID(consoleArea.getFileName() + " <Running>");
				} else {
					consoleArea.setID(consoleArea.getFileName()+ " <Terminated>");
				}
			}
		} catch (Exception e) {
			System.out.println("Error in MainController compileAndRun() = " + e);
		}
	}
	
	@FXML
	public void compileAndRun() {
		if (getSelectedTab() != null) {
			File file = getSelectedTab().getFile();
			saveFile(false);
			compileAndRun(file);
		}
	}

	public void updateStatusLeft(String text) { statusBarLeftLabel.setText(text); }

	public void updateStatusRight(String text) { statusBarRightLabel.setText(text); }

	public static File getMetadataFile(File file) {
		File[] files = file.listFiles();
		if (files != null) {
			for (File entry : files) {
				if (entry.getName().equals(".metadata") && entry.isFile()) {
					return entry;
				}
			}
		}
		
		File parent = file.getParentFile();
		if (parent == null) {
			return null;
		}
		return getMetadataFile(parent);
	}

	public FileTab addTab() {
		FileTab tab = new FileTab(createNewZenCodeArea(), this);
		tab.setOnCloseRequest(event -> closeTab(event));
		tabPane.getTabs().add(tab);
		var selectionModel = tabPane.getSelectionModel();
		selectionModel.select(tab);
		updateStatusRight("");
		return tab;
	}

	@FXML
	public void closeTab(Event event) {
		FileTab selectedTab = getSelectedTab();

		if (selectedTab.getFile() != null && selectedTab.hasChanged()) {
			int response = selectedTab.showConfirmDialog();
			switch (response) {
			case 1:
				tabPane.getTabs().remove(selectedTab);
				break;
			case 2:
				saveFile(null);
				tabPane.getTabs().remove(selectedTab);
				break;
			default:
				if (event != null) { event.consume(); }
				return;
			}
		} else if (selectedTab.hasChanged()) {
			boolean didSave = saveFile(null);

			if (didSave) { Platform.runLater(() -> tabPane.getTabs().remove(selectedTab)); }
		} else {
			tabPane.getTabs().remove(selectedTab);
		}
		updateStatusRight("");
	}
	
	@FXML
	public void changeWorkspace() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		directoryChooser.setTitle("Select new workspace folder");
		File workspace = directoryChooser.showDialog(stage);
		if (workspace != null) {
			stage.close();
			boolean success = fileController.changeWorkspace(workspace);
			if (success) {	
				try {
					//new TestUI().start(stage);
					new Zenit().start(stage);
				} catch (Exception ex) {
					System.err.println("MainController.changeWorkspace: IOException: " + ex.getMessage());
				}
			} else {
				stage.show();
				DialogBoxes.errorDialog("Can't change workspace", "", "");
			}
		}
	}

	public FileTab getSelectedTab() {
		var tabs = tabPane.getTabs();

		for (Tab tab : tabs) {
			if (tab.isSelected()) {
				return (FileTab) tab;
			}
		} return null;
	}

	private FileTab getTabFromFile(File file) {
		var tabs = tabPane.getTabs();

		for (Tab tab : tabs) {
			FileTab fileTab = (FileTab) tab;
			File tabFile = fileTab.getFile();

			if (tabFile != null && file.equals(tabFile)) {
				return fileTab;
			}
		} return null;
	}

	@FXML
	public void importProject() {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select project to import");
		File source = directoryChooser.showDialog(stage);

		if (source != null) {
			try {
				File target = fileController.importProject(source);
				FileTree.createParentNode((FileTreeItem<String>) treeView.getRoot(), target);
				DialogBoxes.informationDialog("Import complete", "Project is imported to workspace");
			} catch (IOException ex) {
				DialogBoxes.errorDialog("Import failed", "Couldn't import project", ex.getMessage());
			}
		}
	}

	public File getCustomThemeCSS() { return this.customThemeCSS; }
	
	@FXML
	public void search() {
		FileTab selectedTab = getSelectedTab();

		if (selectedTab != null) {
			ZenCodeArea zenCodeArea = selectedTab.getZenCodeArea();
			File file = selectedTab.getFile();
			new Search(zenCodeArea, file, isDarkMode, this);
		}
	}
	
	@Override
	public String getActiveStylesheet() { return activeStylesheet; }

	// YRJA: Created a class to handle this logic. It's bananas and my brain is fried.
	public void commentAndUncomment() {
		ZenCodeArea zenCodeArea = getSelectedTab().getZenCodeArea();
		int caretPosition = zenCodeArea.getCaretPosition();
		int caretColumn = zenCodeArea.getCaretColumn();
		int length = zenCodeArea.getLength();
		IndexRange zen = zenCodeArea.getSelection();
		int endOfSelection = zen.getEnd();
		int startOfSelection = zen.getStart();

		// Create an instance of the processor class
		CommentUncommentProcessor processor = new CommentUncommentProcessor(zenCodeArea, caretPosition, caretColumn, length, startOfSelection, endOfSelection);

		// Process the comment/uncomment action
		processor.process();
	}

	public void chooseAndImportLibraries(ProjectFile projectFile) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select jar file to import");
		
		FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Libraries", "*.jar", "*.zip");
		fileChooser.getExtensionFilters().add(filter);

		List<File> jarFiles = fileChooser.showOpenMultipleDialog(stage);
		
		if (jarFiles != null) {
			boolean success = fileController.addInternalLibraries(jarFiles, projectFile);
			if (success) {
				DialogBoxes.informationDialog("Import complete", "Jar file(s) have successfully been imported to workspace");
			} else {
				DialogBoxes.errorDialog("Import failed", "Couldn't import jar file(s)", "An error occured while trying to import jar file(s)");
			}
		}
	}
	
	public void showProjectProperties(ProjectFile projectFile) {
		projectMetadataController = new ProjectMetadataController(fileController, projectFile, isDarkMode, this);
		projectMetadataController.start();
	}
	
	public void openJREVersions() {
		JREVersionsController jvc = new JREVersionsController(true);
		jvc.start();
	}
	
	@FXML
	private void terminate() {
		if (process != null ) { process.destroy(); }
	}
	
	public boolean isDarkmode() { return isDarkMode; }
	
	public void setDarkmode(boolean isDarkmode) { this.isDarkMode = isDarkmode; }
	
	public void closeConsoleComponent() {
		
		splitPane.setDividerPosition(0, 1.0);
	    consolePane.setMinHeight(0.0);
		consolePane.setVisible(false);
		consolePane.setDisable(true);
		splitPane.resize(splitPane.getWidth() + 2, splitPane.getHeight() + 2);
		
		Node divider = splitPane.lookup(".split-pane-divider");
	    if(divider!=null){
	        divider.setStyle("-fx-padding: 0");
	    }
	}
	
	public void openConsoleComponent() {
		
		consolePane.setVisible(true);
		consolePane.setDisable(false);
		splitPane.setDividerPosition(0, 0.85);
		consolePane.setMinHeight(34.0);
		
		Node divider = splitPane.lookup(".split-pane-divider");

		if(divider != null) { divider.setStyle("-fx-padding: 1"); }
		
		splitPane.resize(splitPane.getWidth() + 2 , splitPane.getHeight() + 2);
	}
}