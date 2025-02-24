package main.java.zenit.ui.tree;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A custom TreeItem that holds a File object and an icon for the tree item.
 * The type of the tree item is stored as an integer and is used to determine the icon.
 * @param <T> The type of the value of the tree item
 */
public class FileTreeItem<T> extends TreeItem<T> {
	private File file;
	private ImageView icon;
	private int type;
	public static final int WORKSPACE = 100; // Arbitrary numbers to represent the type of the tree item
	public static final int PROJECT = 101;
	public static final int PACKAGE = 102;
	public static final int CLASS = 103;
	public static final int SRC = 104;
	public static final int FOLDER = 105;
	public static final int FILE = 106;
	public static final int INCOMPATIBLE = 107;

	// Icons for the tree items stored in a map
	private static final Map<Integer, String> ICON_URLS = new HashMap<>();
	static {
		ICON_URLS.put(PROJECT, "/zenit/ui/tree/project.png");
		ICON_URLS.put(PACKAGE, "/zenit/ui/tree/package.png");
		ICON_URLS.put(CLASS, "/zenit/ui/tree/class.png");
		ICON_URLS.put(SRC, "/zenit/ui/tree/src.png");
		ICON_URLS.put(FOLDER, "/zenit/ui/tree/folder.png");
		ICON_URLS.put(FILE, "/zenit/ui/tree/file.png");
		ICON_URLS.put(INCOMPATIBLE, "/zenit/ui/tree/incompatible.png");
	}

	// Strings for the types stored in a map
	private static final Map<Integer, String> TYPE_STRINGS = new HashMap<>();
	static {
		TYPE_STRINGS.put(PROJECT, "project");
		TYPE_STRINGS.put(PACKAGE, "package");
		TYPE_STRINGS.put(CLASS, "class");
		TYPE_STRINGS.put(SRC, "src-folder");
		TYPE_STRINGS.put(FOLDER, "folder");
		TYPE_STRINGS.put(FILE, "file");
		TYPE_STRINGS.put(INCOMPATIBLE, "incompatible");
	}
	
	public FileTreeItem(File file, T name, int type) {
		super(name);
		this.file = file;
		this.type = type;
		
		setIcon();
	}

	/**
	 * Sets the icon of the tree item based on the type of the item.
	 * The icon is set to null if the type is not found in the map.
	 */
	private void setIcon() {
		String url = ICON_URLS.get(type);
		if (url != null) {
			icon = new ImageView(new Image(getClass().getResource(url).toExternalForm()));
			icon.setFitHeight(16);
			icon.setFitWidth(16);
			icon.setSmooth(true);
			this.setGraphic(icon);
		}
	}
	
	public void setFile(File file) { this.file = file; }
	
	public File getFile() { return file; }
	
	public int getType() { return type; }

	public String getStringType() {
		return TYPE_STRINGS.get(type);
	}
}