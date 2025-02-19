package main.java.zenit.ui.projectinfo;

import main.java.zenit.ui.DialogBoxes;

public class ProjectInfoErrorHandling {
	
	public static int metadataMissing() {
		return DialogBoxes.twoChoiceDialog("Metadata missing", "Metadata missing from "
				+ "project", "It seems this is the first time you are using this project in "
				+ "Zenit. Do you want to generate a new metadata file about this project?",
				"Yes, generate", "No, close window");
	}
	
	public static int metadataOutdated() {
		return DialogBoxes.twoChoiceDialog("Metadata outdated", "Metadata must be updated",
				"It seems that you must update the metadata file. Do you want to update the "
				+ "metadata file about this project?", "Yes, update", "No, close window");
	}

	public static void addInternalLibraryFail() {
		DialogBoxes.errorDialog("Import failed", "Couldn't import internal libraries",
				"Failed to import internal libraries");
	}

	public static void removeInternalLibraryFail() {
		DialogBoxes.errorDialog("Removal failed", "Couldn't remove internal libraries",
				"Failed to remove internal libraries");
	}

	public static void addExternalLibraryFail() {
		DialogBoxes.errorDialog("Add failed", "Couldn't add external libraries",
				"Failed to add external libraries");
	}

	public static void removeExternalLibraryFail() {
		DialogBoxes.errorDialog("Removal failed", "Couldn't remove external libraries",
				"Failed to remove external libraries");
	}
}
