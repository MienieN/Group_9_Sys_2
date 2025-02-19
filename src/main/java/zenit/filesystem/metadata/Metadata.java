package main.java.zenit.filesystem.metadata;

import java.io.File;
import main.java.zenit.filesystem.RunnableClass;

public class Metadata {
	
	private File metadataFile;
	private String version, directory, sourcepath, JREVersion;
	private RunnableClass[] runnableClasses;
	private String[] internalLibraries, externalLibraries;

	public Metadata(File metadataFile) {
		this.metadataFile = metadataFile;
		MetadataDecoder.decode(metadataFile, this);
	}
	
	public boolean encode() {
		return MetadataEncoder.encode(metadataFile, this);
	}
	
	public File getMetadataFile() {
		return metadataFile;
	}

	public void setMetadataFile(File metadataFile) {
		this.metadataFile = metadataFile;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public String getSourcepath() {
		return sourcepath;
	}
	
	public void setSourcepath(String sourcepath) {
		this.sourcepath = sourcepath;
	}

	public String getJREVersion() {
		return JREVersion;
	}

	public void setJREVersion(String jREVersion) {
		JREVersion = jREVersion;
	}

	public RunnableClass[] getRunnableClasses() {
		return runnableClasses;
	}
	
	public void setRunnableClasses(RunnableClass[] runnableClasses) {
		this.runnableClasses = runnableClasses;
	}
	
	public boolean addRunnableClass(RunnableClass runnableClass) {
		if (runnableClasses == null) {
			runnableClasses = new RunnableClass[0];
		}
		
		RunnableClass[] temp = new RunnableClass[runnableClasses.length + 1];
		for (int i = 0; i < runnableClasses.length; i++) {
			if (runnableClasses[i].getPath().equals(runnableClass.getPath())) {
				return false;
			} else {
				temp[i] = runnableClasses[i];
			}
		}

		temp[temp.length - 1] = runnableClass;
		runnableClasses = temp;

		return true;
	}
	
	public boolean removeRunnableClass(String runnableClassPath) {
		if (runnableClasses != null) {
			RunnableClass[] temp = new RunnableClass[runnableClasses.length-1];
			int counter = 0;
			for (int i = 0; i < runnableClasses.length; i++) {
				if (!runnableClasses[i].getPath().equals(runnableClassPath)) {
					if (counter != temp.length) {
						temp[counter++] = runnableClasses[i];
					} else {
						return false;
					}
				}
			}
			
			runnableClasses = temp;
		}

		return true;
	}
	
	public RunnableClass containRunnableClass(String classPath) {
		if (runnableClasses != null) {
			for (int i = 0; i < runnableClasses.length; i++) {
				if ((runnableClasses[i].getPath()).equals(classPath + ".java")) {
					return runnableClasses[i];
				}
			}
		}
		return null;
	}

	public String[] getInternalLibraries() {
		return internalLibraries;
	}

	public void setInternalLibraries(String[] internalLibraries) {
		this.internalLibraries = internalLibraries;
	}

	public String[] getExternalLibraries() {
		return externalLibraries;
	}

	public void setExternalLibraries(String[] externalLibraries) {
		this.externalLibraries = externalLibraries;
	}

	public String toString() {
		String nl = "\n";
		String string = version + nl + directory + nl + sourcepath + nl + JREVersion + nl;
		if (runnableClasses != null) {
			for (RunnableClass rc : runnableClasses) {
				string += rc + nl;
			}
		}
		if (internalLibraries != null) {
			for (String il : internalLibraries) {
				string += il + nl;
			}
		}
		if (externalLibraries != null) {
			for (String el : externalLibraries) {
				string += el + nl;
			}
		}

		return string;
	}
}