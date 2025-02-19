package main.java.zenit.console;

import org.fxmisc.richtext.InlineCssTextArea;
import javafx.application.Platform;

/**
 * The ConsoleArea class extends the InlineCssTextArea to represent a customizable console widget
 * that allows for the display of output including inline styles such as colors for error and standard output.
 * It supports associating a background color, process, file name, and identifier.
 * This class is primarily designed to display console-like interactions with support for styled text.
 */
public class ConsoleArea extends InlineCssTextArea {
	private String ID, backgroundColor, fileName;
	private Process process;
	
	/**
	 * Constructs a new ConsoleArea with default values.
	 * Initializes the instance with default values and applies default settings such as making it uneditable
	 * and applying the default stylesheet.
	 */
	public ConsoleArea(){
		this("UNKNOWN", null, "");
	}
    
    /**
	 * Constructs a new ConsoleArea with the specified identifier, process, and background color.
	 * Initializes the instance with provided values and applies default settings such as making it uneditable
	 * and applying the default stylesheet.
	 *
	 * @param identity the unique identifier for this ConsoleArea
	 * @param process the Process to associate with this ConsoleArea
	 * @param backgroundColor the background color for this ConsoleArea, specified as a String (e.g., a CSS color value)
	 */
	public ConsoleArea(String identity, Process process, String backgroundColor) {
        setID(identity);
        setProcess(process);
        setBackgroundColor(backgroundColor);
        
        applyStylesheet();
        this.setEditable(false);
    }
    
    /**
     Applies the default stylesheet to this ConsoleArea.
     */
    private void applyStylesheet() {
        getStylesheets().add(getClass().getResource("/zenit/console/consoleStyle.css").toString());
    }
	
	/**
	 * Retrieves the name of the file associated with this object.
	 *
	 * @return the file name as a String, or null if no file name has been set.
	 */
	public String getFileName() {
		return this.fileName;
	}
	
	/**
	 * Sets the name of the file associated with this object.
	 *
	 * @param name the name of the file to associate with this object
	 */
	public void setFileName(String name) {
		this.fileName = name;
	}
	
	/**
	 * Retrieves the background color currently associated with this ConsoleArea.
	 *
	 * @return the background color as a String value.
	 */
	public String getBackgroundColor() {
		return this.backgroundColor;
	}
	
	/**
	 * Sets the background color for this ConsoleArea and applies the corresponding style.
	 *
	 * @param color the background color to set, represented as a String (e.g., a CSS color value)
	 */
	public void setBackgroundColor(String color) {
		this.backgroundColor = color;
		this.setStyle(color);
	}
	
	/**
	 * Retrieves the Process associated with this ConsoleArea instance.
	 *
	 * @return the Process object currently associated with this ConsoleArea, or null if no Process is set.
	 */
	public Process getProcess() {
		return this.process;
	}
	
	/**
	 * Sets the Process associated with this ConsoleArea instance.
	 *
	 * @param process the Process to associate with this ConsoleArea
	 */
	public void setProcess(Process process) {
		this.process = process;
	}
	
	
	/**
	 * Retrieves the identifier associated with this instance.
	 *
	 * @return the identifier as a String.
	 */
	public String getID() {
		return ID;
	}
	

	public void errorPrint(String stringToPrint) {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {    	
		    	try {
					appendText(stringToPrint);
					setStyle(getText().length() - stringToPrint.length(), getText().length(), "-fx-fill: red;");
				} catch (IndexOutOfBoundsException e) {
					// Windows bug, don't do anything with the exception.
				}
		    }
		});
	}
	
	public void outPrint(String stringToPrint) {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {	 
			    	try {
						appendText(stringToPrint);
						setStyle(getText().length() - stringToPrint.length(), getText().length(),
								"-fx-fill: white");
					} catch (IndexOutOfBoundsException e) {
						// Windows bug, dont do anything with the exception.
					}	   
		    }
		});
	}
	
	/**
	 * Sets the identifier for this ConsoleArea instance.
	 *
	 * @param ID the unique identifier to associate with this ConsoleArea
	 */
	public void setID(String ID) {
		this.ID = ID;
	}
	
	/**
	 * Returns a string representation of this ConsoleArea instance.
	 * The returned string is the unique identifier (ID) of this ConsoleArea.
	 *
	 * @return the identifier (ID) of this ConsoleArea as a String.
	 */
	@Override
	public String toString() {
		return this.ID;
	}
}