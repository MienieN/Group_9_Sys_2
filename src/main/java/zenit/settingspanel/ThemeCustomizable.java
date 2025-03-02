package main.java.zenit.settingspanel;

import java.io.File;
import javafx.stage.Stage;

/**
 * Represents an interface for customizing the theme of an application.
 */
public interface ThemeCustomizable {
	
	public File getCustomThemeCSS();
	
	public Stage getStage();
	
	public String getActiveStylesheet();
}