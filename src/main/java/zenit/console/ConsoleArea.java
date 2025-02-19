package main.java.zenit.console;

import org.fxmisc.richtext.InlineCssTextArea;
import javafx.application.Platform;

public class ConsoleArea extends InlineCssTextArea {
	
	private String ID, backgroundColor, fileName;
	private Process process;
	
	
	public ConsoleArea(){
		this("UNKNOWN", null, "");
	}

	public ConsoleArea(String identity, Process process, String backgroundColor) {
		this.ID = identity;
		this.process = process;
		
		// TODO maybe remove this one and add to main.css
		getStylesheets().add(getClass().getResource("/zenit/console/consoleStyle.css").toString());
		this.setStyle(backgroundColor);
		this.setEditable(false);
	}
	
	public String getFileName() {
		return this.fileName;
	}
	
	public void setFileName(String name) {
		this.fileName = name;
	}
	
	public String getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public void setBackgroundColor(String color ) {
		this.backgroundColor = color;
		this.setStyle(color);
	}
	
	public Process getProcess() {
		return this.process;
	}
	
	public void setProcess(Process p) {
		this.process = p;
	}
	
	
	public String getID() {
		return ID;
	}
	

	public void errPrint(String stringToPrint) {
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
	
	public void setID(String ID) {
		this.ID = ID;
	}
	
	@Override
	public String toString() {
		return this.ID;
	}
}