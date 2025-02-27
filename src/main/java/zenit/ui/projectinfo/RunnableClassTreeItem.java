package main.java.zenit.ui.projectinfo;

import java.io.File;
import javafx.scene.control.TreeItem;

/**
 * Represents a TreeItem that stores information about a file, its name, and whether it is marked as runnable.
 *
 * @param <t> the type of the value stored in this TreeItem
 */
public class RunnableClassTreeItem<t> extends TreeItem<t> {
	private File file;
	private boolean runnable;

	/**
	 * Constructs a new RunnableClassTreeItem with the given name, file, and runnable status.
	 *
	 * @param name the name of the TreeItem
	 * @param file the File associated with the TreeItem
	 * @param runnable indicates if the TreeItem is marked as runnable
	 */
	public RunnableClassTreeItem(t name, File file, boolean runnable) {
		super(name);
		this.file = file;
		this.runnable = runnable;
	}

	/**
	 * Retrieves the File associated with this TreeItem.
	 *
	 * @return The File associated with this TreeItem.
	 */
	public File getFile() { return file; }

	/**
	 * Returns whether the TreeItem is marked as runnable.
	 *
	 * @return true if the TreeItem is marked as runnable, false otherwise
	 */
	public boolean isRunnable() { return runnable; }
}