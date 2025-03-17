package main.java.zenit.filesystem;

import java.io.*;

/**
 * Handles workspace file creation, reading, and setup for a new workspace.
 */
public class WorkspaceHandler {

    /**
     * Creates the workspace file if it does not exist.
     */
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

    /**
     * Creates a workspace by serializing the given file into the workspace file.
     *
     * @param file The file representing the workspace.
     * @return True if the workspace file was successfully created, false otherwise.
     */
    public static boolean createWorkspace(File file) {
        boolean success = false;

        createWorkspaceFile();

        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("res/workspace/workspace.dat")))) {
            oos.writeObject(file);
            oos.flush();
            success = true;
        } catch (IOException ex) {
            System.err.println("WorkspaceHandler.createWorkspace: IOException: " + ex.getMessage());
        }
        return success;
    }

    /**
     * Reads the workspace file and deserializes it to return the workspace file.
     *
     * @return The deserialized workspace file, or null if an error occurred.
     * @throws IOException If there is an error reading the workspace file.
     */
    public static File readWorkspace() throws IOException {
        File workspace = null;
        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream("res/workspace/workspace.dat")))) {
            workspace = (File) ois.readObject();
        } catch (ClassNotFoundException ex) {
            System.err.println("WorkspaceHandler.readWorkspace: ClassNotFoundException " + ex.getMessage());
        }
        return workspace;
    }

    // TODO Creates a new workspace when user starts the program for the first time instead of asking them to choose a workspace.
    //  Uses the users desktop to save a new folder named zenit to store the new project in.
    //  Maybe make this a select folder dialog instead?

    /**
     * Sets up a new workspace by creating a "Zenit" directory on the user's desktop.
     * If the directory already exists, it simply returns the directory.
     *
     * @return The Zenit directory representing the new workspace.
     */
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
