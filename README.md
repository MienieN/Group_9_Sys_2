# Group_9_Sys_2
Systemutveckling 2, term 4 group project. Improving an existing project and documentation.

To run the program:
- Open your IDE of choice
- Choose "File->Import...->General->Existing projects into workspace"
- "Next >".
- Click "Browse..." and choose the newly unzipped folder named "Group_9_Sys_2"
- Click "Open"
- Click "Finish"

The following needs to be set up as run configurations in your IDE (the following is for Intellij IDEA):
- On the left of the run button you will find a drop down menu, select it.
- Click on "Edit Configurations..."
- Click on the "+" in the top left corner and select "Application"
- Name the configuration "TestUI"
- Set the main class to "zenit.ui.TestUI"
- Add VM options
  - Set the VM options to the following:
    - See below for the correct setup for your OS

FOR WINDOWS:

--module-path
lib/windowsFX/lib/
--add-modules=javafx.controls,javafx.fxml,javafx.web
--add-opens
javafx.graphics/javafx.scene.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.text=ALL-UNNAMED
--add-opens
javafx.graphics/com.sun.javafx.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED
-Dprism.allowhidpi=true

FOR MAC:

--module-path
lib/javafx-sdk-23.0.2/lib/
--add-modules=javafx.controls,javafx.fxml,javafx.web
--add-opens
javafx.graphics/javafx.scene.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.text=ALL-UNNAMED
--add-opens
javafx.graphics/com.sun.javafx.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene.text=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.scene=ALL-UNNAMED
--add-exports
javafx.graphics/com.sun.javafx.geom=ALL-UNNAMED
-Dprism.allowhidpi=true


• Add the project as a module, to do this follow the steps below:
- Go to Project structure
- Go to modules
- Click on the "+"
- Import module
- Choose "Group_9_Sys_2" as module to import (you find it where you stored it when downloading the project)
- Import as Maven project
- Set language level to 21
- When imported make sure the "src" folder is the only one marked as source folder

If any folder except for "src" is marked as a source folder right-click on the folder, scroll down to "Mark directory as"
-> "Unmark as Sources root"

(If TestUI class doesn't show up in Java Application list, try running 'TestUI' first.
You'll get an error but the class will show up in the Java Application list.)

• Uncheck "Use the -XstartOnFirstThread argument when launching with SWT"

• Run src/main/java/zenit/ui/TestUI.java

Github: https://github.com/MienieN/Group_9_Sys_2
