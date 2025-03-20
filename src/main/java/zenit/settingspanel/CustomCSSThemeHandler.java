package main.java.zenit.settingspanel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Handles the custom CSS theme settings for the application.
 * Allows the user to customize colors, store them in properties, and apply the custom theme across different stages.
 */
public class CustomCSSThemeHandler {

    // Color settings
    private String Color_Primary;
    private String Color_PrimaryTint;
    private String Color_Secondary;
    private String Color_SecondaryTint;

    // Flag to indicate if custom theme is toggled
    private boolean isCustomThemeToggled;

    // List of stages that can be customized
    private List<ThemeCustomizable> stages;

    // Property keys for storing theme colors
    private final String key_Primary = "primary";
    private final String key_PrimaryTint = "primtint";
    private final String key_Secondary = "secondary";
    private final String key_SecondaryTint = "secondtint";

    private InputStream input = null;
    private OutputStream output = null;

    /**
     * Constructor initializes the theme handler with the list of stages.
     * Loads all properties from the configuration file.
     *
     * @param stages List of stages that support custom themes
     */
    public CustomCSSThemeHandler(List<ThemeCustomizable> stages) {
        this.stages = stages;
        loadAllProperties();
    }

    /**
     * Opens input and output streams for reading and writing properties.
     */
    public void openStreams() {
        try {
            input = new FileInputStream("config.properties");
            output = new FileOutputStream("config.properties", true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the open input and output streams.
     */
    public void closeStreams() {
        closeStream(input);
        closeStream(output);
    }

    /**
     * Helper method to close a given stream.
     *
     * @param stream the stream to be closed
     */
    private void closeStream(AutoCloseable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Validates if a given color is not already one of the stored theme colors.
     *
     * @param color the color to be validated
     * @return true if the color is valid (not equal to any of the stored theme colors), false otherwise
     */
    private boolean isColorValid(String color) {
        return !color.equals(Color_Primary) &&
                !color.equals(Color_PrimaryTint) &&
                !color.equals(Color_Secondary) &&
                !color.equals(Color_SecondaryTint);
    }

    /**
     * Changes the color of the theme for a given color theme (primary, secondary, etc.).
     * Updates the stylesheets of all stages and stores the new color.
     *
     * @param color the new color to be applied
     * @param colorTheme the type of color (e.g., primary, secondary)
     */
    public void changeColor(Color color, CustomColor colorTheme) {
        String newColor;

        if (isColorValid(newColor = colorToHex(color))) {
            openStreams();

            // Update the stylesheet for each stage
            for (ThemeCustomizable stage : stages) {
                changeStyleSheet(stage.getStage(), stage.getCustomThemeCSS(),
                        getStringFromEnum(colorTheme), newColor);
            }

            storeColor(newColor, colorTheme);
            closeStreams();
        }
    }

    /**
     * Retrieves the corresponding string for the given enum value (CustomColor).
     *
     * @param enumColor the color enum to be converted to a string
     * @return the corresponding string for the enum value
     */
    public String getStringFromEnum(CustomColor enumColor) {
        String toReturn = "";

        switch (enumColor) {
            case primaryColor:
                toReturn = Color_Primary;
                break;

            case primaryTint:
                toReturn = Color_PrimaryTint;
                break;

            case secondaryColor:
                toReturn = Color_Secondary;
                break;

            case secondaryTint:
                toReturn = Color_SecondaryTint;
                break;
        }

        return toReturn;
    }

    /**
     * Stores the given color in the properties file under the corresponding theme color key.
     *
     * @param color the color to be stored
     * @param colorTheme the type of color (e.g., primary, secondary)
     */
    public void storeColor(String color, CustomColor colorTheme) {
        switch (colorTheme) {
            case primaryColor:
                Color_Primary = color;
                storeProperty(key_Primary, Color_Primary);
                break;

            case primaryTint:
                Color_PrimaryTint = color;
                storeProperty(key_PrimaryTint, Color_PrimaryTint);
                break;

            case secondaryColor:
                Color_Secondary = color;
                storeProperty(key_Secondary, Color_Secondary);
                break;

            case secondaryTint:
                Color_SecondaryTint = color;
                storeProperty(key_SecondaryTint, Color_SecondaryTint);
                break;
        }
    }

    /**
     * Loads all the theme properties from the configuration file.
     */
    public void loadAllProperties() {
        openStreams();
        Color_Primary = getProperty(key_Primary);
        closeStreams();

        openStreams();
        Color_PrimaryTint = getProperty(key_PrimaryTint);
        closeStreams();

        openStreams();
        Color_Secondary = getProperty(key_Secondary);
        closeStreams();

        openStreams();
        Color_SecondaryTint = getProperty(key_SecondaryTint);
        closeStreams();
    }

    /**
     * Retrieves the value of a property from the configuration file.
     *
     * @param key the property key
     * @return the property value
     */
    public String getProperty(String key) {
        Properties prop = new Properties();
        String property = "";

        try {
            prop.load(input);
            property = prop.getProperty(key);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return property;
    }

    /**
     * Stores a property in the configuration file.
     *
     * @param key the property key
     * @param value the value to store for the key
     */
    public void storeProperty(String key, String value) {
        Properties prop = new Properties();

        try {
            openStreams();
            prop.setProperty(key, value);
            prop.store(output, null);
            closeStreams();
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    /**
     * Updates the stylesheet of the specified stage by replacing a color value in the CSS.
     *
     * @param stage the stage whose stylesheet needs to be updated
     * @param customThemeCSSfile the CSS file for the custom theme
     * @param regex the regex pattern to match the color value
     * @param replacement the new color to replace the matched value
     */
    public void changeStyleSheet(Stage stage, File customThemeCSSfile, String regex, String replacement) {
        String fullPath = System.getProperty("user.dir") + customThemeCSSfile;
        var stylesheets = stage.getScene().getStylesheets();

        if (stylesheets.contains("file:" + fullPath)) {
            stylesheets.remove("file:" + fullPath);
        }

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(fullPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            String replacedLine = line.replaceAll(regex, replacement);
            newLines.add(replacedLine);
        }

        try {
            String stringNewStylesheet = String.join("\n", newLines);
            BufferedWriter writer = new BufferedWriter(new FileWriter(fullPath));
            writer.write(stringNewStylesheet);
            writer.close();

            if (isCustomThemeToggled) {
                stylesheets.add("file:" + fullPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggles the custom theme for all stages.
     *
     * @param isToggled true to enable the custom theme, false to disable it
     */
    public void toggleCustomTheme(boolean isToggled) {
        if (isToggled) {
            updateDefaultStylesheets(isToggled);
            for (ThemeCustomizable stage : stages) {
                String fullPath = "file:" + System.getProperty("user.dir") +
                        stage.getCustomThemeCSS();
                var stylesheets = stage.getStage().getScene().getStylesheets();
                stylesheets.add(fullPath);
            }
        } else {
            for (ThemeCustomizable stage : stages) {
                String fullPath = "file:" + System.getProperty("user.dir") +
                        stage.getCustomThemeCSS();
                var stylesheets = stage.getStage().getScene().getStylesheets();
                stylesheets.remove(fullPath);
            }
            updateDefaultStylesheets(isToggled);
        }
        isCustomThemeToggled = isToggled;
    }

    /**
     * Updates the default stylesheets for all stages based on the custom theme toggle state.
     *
     * @param isCustomTheme true if the custom theme is enabled, false otherwise
     */
    public void updateDefaultStylesheets(boolean isCustomTheme) {
        for (ThemeCustomizable stage : stages) {
            var stylesheets = stage.getStage().getScene().getStylesheets();
            var activeSheet = stage.getActiveStylesheet();

            if (isCustomTheme) {
                stylesheets.remove(activeSheet);
            } else {
                stylesheets.add(activeSheet);
            }
        }
    }

    /**
     * Converts a JavaFX Color object to its corresponding hexadecimal string representation.
     *
     * @param color the color to be converted
     * @return the hex string of the color
     */
    private String colorToHex(Color color) {
        String hex1 = Integer.toHexString(color.hashCode()).toUpperCase();
        String hex2;

        switch (hex1.length()) {
            case 2:
                hex2 = "000000";
                break;
            case 3:
                hex2 = String.format("00000%s", hex1.substring(0, 1));
                break;
            case 4:
                hex2 = String.format("0000%s", hex1.substring(0, 2));
                break;
            case 5:
                hex2 = String.format("000%s", hex1.substring(0, 3));
                break;
            case 6:
                hex2 = String.format("00%s", hex1.substring(0, 4));
                break;
            case 7:
                hex2 = String.format("0%s", hex1.substring(0, 5));
                break;
            default:
                hex2 = hex1.substring(0, 6);
        }

        return "#" + hex2;
    }
}
