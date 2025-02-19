package main.java.zenit.filesystem;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class WorkspaceHandler {
	
	private static void createWorkspaceFile() {
		File workspaceFile = new File("res/workspace/workspace.dat");
		
		if (!workspaceFile.exists()) {
			workspaceFile.getParentFile().mkdirs();

			try {
				workspaceFile.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static boolean createWorkspace(File file) {
		boolean success = false;
				
		createWorkspaceFile();
		
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(
				new FileOutputStream("res/workspace/workspace.dat")))) {
			oos.writeObject(file);
			oos.flush();
			success = true;
		} catch (IOException ex) {
			System.err.println("WorkspaceHandler.createWorkspace: IOException: " + ex.getMessage());
		}
		return success;
	}

	public static File readWorkspace() throws IOException {
		File workspace = null;
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(
				new FileInputStream("res/workspace/workspace.dat")))) {
			workspace = (File) ois.readObject();
		} catch (ClassNotFoundException ex) {
			System.err.println("WorkspaceHandler.readWorkspace: ClassNotFoundException " + ex.getMessage());
		}
		return workspace;
	}

	// TODO Creates a new workspace when user starts the program for the first time instead of asking them to choose a workspace.
	//  Uses the users desktop to save a new folder named zenit to store the new project in.
	//  Maybe make this a select folder dialog instead?
	public static File setUpNewWorkspace() {
		String userHome = System.getProperty("user.home");
		File desktop = new File(userHome, "Desktop");
		File zenitDir = new File(desktop, "Zenit");

		if (!zenitDir.exists()) {
			zenitDir.mkdirs();
		}

		return zenitDir;
	}

}
