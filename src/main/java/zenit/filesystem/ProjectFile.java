package main.java.zenit.filesystem;

import java.io.File;
import java.io.IOException;

public class ProjectFile extends File {
	
	private static final long serialVersionUID = -9201755155887621850L;
	private File metadata;
	private File src;
	private File bin;
	private File lib;

	public ProjectFile(String pathname) {
		super(pathname);
	}

	public ProjectFile(File file) {
		super(file.getPath());
	}

	public File addLib() {
		if (lib == null) {
			String libPath = getPath() + File.separator + "lib";
			lib = new File(libPath);
			lib.mkdir();
		}
		
		return lib;
	}

	public File getLib() {
		return lib;
	}
	
	public void setLib(File lib) {
		this.lib = lib;
	}
	
	public File addSrc() {
		if (src == null) {
			String srcPath = getPath() + File.separator + "src";
			src = new File(srcPath);
			src.mkdir();
		}
		
		return src;
	}
	
	public File getSrc() {
		if (src == null) {
			File[] files = listFiles();
			
			for (File file : files) {
				if (file.getName().equals("src")) {
					src = file;
					break;
				}
			}
		}
		
		return src;
	}
	
	public File addBin() {
		if (bin == null) {
			String binPath = getPath() + File.separator + "bin";
			bin = new File(binPath);
			bin.mkdir();
		}
		
		return bin;
	}
	
	public File getBin() {
		if (bin == null) {
			File[] files = listFiles();
			
			for (File file : files) {
				if (file.getName().equals("bin")) {
					bin = file;
					break;
				}
			}
		}
		
		return bin;
	}

	public File addMetadata() {
		metadata = getMetadata();
		if (metadata == null) {
			try {
				metadata = MetadataFileHandler.createMetadataFile(this);
			} catch (IOException e) {
				return null;
			}
		}
		return metadata;
	}

	public File getMetadata() {
		if (metadata == null && isDirectory()) {
			File[] files = listFiles();
		
			for (File file : files) {
				if (file.getName().equals(".metadata")) {
					metadata = file;
					break;
				}
			}
		}
		
		return metadata;
	}

	public void setMetadata(File metadata) {
		this.metadata = metadata;
	}
}
