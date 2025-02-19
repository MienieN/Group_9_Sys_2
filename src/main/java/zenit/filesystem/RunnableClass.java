package main.java.zenit.filesystem;

import java.io.File;

public class RunnableClass {
	
	private String path;
	private String paArguments;
	private String vmArguments;
	
	public RunnableClass(String path, String paArguments, String vmArguments) {
		this.path = path;
		this.paArguments = paArguments;
		this.vmArguments = vmArguments;
	}
	
	public RunnableClass(String path) {
		this(path, "", "");
	}
	
	public RunnableClass(File file) {
		this(file.getPath());
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPaArguments() {
		return paArguments;
	}

	public void setPaArguments(String paArguments) {
		this.paArguments = paArguments;
	}
	
	public String getVmArguments() {
		return vmArguments;
	}

	public void setVmArguments(String vmArguments) {
		this.vmArguments = vmArguments;
	}
	
	public String toString() {
		String nl = "\n";
		String string = path + nl + paArguments + nl + vmArguments;
		return string;
	}
}
