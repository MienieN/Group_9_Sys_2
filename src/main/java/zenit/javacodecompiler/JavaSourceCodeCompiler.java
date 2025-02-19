package main.java.zenit.javacodecompiler;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

import javafx.application.Platform;
import main.java.zenit.ConsoleRedirect;
import main.java.zenit.console.ConsoleArea;
import main.java.zenit.console.ConsoleController;
import main.java.zenit.filesystem.RunnableClass;
import main.java.zenit.filesystem.metadata.Metadata;
import main.java.zenit.ui.MainController;

public class JavaSourceCodeCompiler {
	
	protected File file;
	protected File metadataFile;
	protected boolean inBackground;
	protected Buffer<?> buffer;
	protected MainController cont;
	protected ConsoleController consoleController;

	public JavaSourceCodeCompiler(File file, boolean inBackground) {
		this(file, null, inBackground, null, null);
	}

	public JavaSourceCodeCompiler(File file, File metadata, boolean inBackground, Buffer<?> buffer, MainController cont) {
		this.file = file;
		this.metadataFile = metadata;
		this.inBackground = inBackground;
		this.buffer = buffer;
		this.cont = cont;
	}

	public void startCompile() {
		new Compile().start();	
	}
	
	public void startCompileAndRun() {
		new CompileAndRun().start();
	}

	private class Compile extends Thread {
		protected String JDKPath = null;
		protected String sourcepath;
		protected String directory;
		protected File runPath;
		protected File projectFile;
		protected String[] internalLibraries;
		protected String[] externalLibraries;

		public void run() {
			if (metadataFile != null) {
				decodeMetadata();
				createProjectPath();
				compileInPackage();
			} else {
				compile();
			}
			
			if (inBackground && buffer instanceof DebugErrorBuffer) {
				DebugErrorBuffer deb = (DebugErrorBuffer) buffer;
				cont.errorHandler(deb);
			}	
		}
	
		protected void createProjectPath() {
			projectFile = metadataFile.getParentFile();
		}

		protected void decodeMetadata() {
			Metadata metadata = new Metadata(metadataFile);
			
			JDKPath = metadata.getJREVersion();
			directory = metadata.getDirectory();
			sourcepath = metadata.getSourcepath();
			internalLibraries = metadata.getInternalLibraries();
			externalLibraries = metadata.getExternalLibraries();	
		}

		protected Process compile() {

			CommandBuilder cb = new CommandBuilder(CommandBuilder.COMPILE);
			cb.setJDK(JDKPath);
			cb.setRunPath(file.getPath());

			String command = cb.generateCommand();
			Process process = executeCommand(command, null);
			redirectStreams(process);
			return process;
		}

		protected Process compileInPackage() {
			runPath = new File(createRunPathInProject());

			CommandBuilder cb = new CommandBuilder(CommandBuilder.COMPILE);
			cb.setJDK(JDKPath);
			cb.setRunPath(runPath.getPath());
			cb.setDirectory(directory);
			cb.setSourcepath(sourcepath);
			cb.setInternalLibraries(internalLibraries);
			cb.setExternalLibraries(externalLibraries);
			
			String command = cb.generateCommand();
			Process process = executeCommand(command, projectFile);
			redirectStreams(process);
			return process;
		}

		protected Process executeCommand(String command, File projectFile) {
			if (inBackground) {
				DebugErrorBuffer deb = null;
				if (buffer != null && buffer instanceof DebugErrorBuffer) {
					deb = (DebugErrorBuffer) buffer;
				}
				return TerminalHelpers.runBackgroundCommand(command, projectFile, deb);
			} else {
				return TerminalHelpers.runCommand(command, projectFile);
			}
		}

		protected String createRunPathInProject() {
			File projectFile = metadataFile.getParentFile();
			String runPath = file.getPath();
			String projectPath = projectFile.getPath();

			runPath = runPath.replaceAll(Matcher.quoteReplacement(projectPath + File.separator), "");

			return runPath;
		}

		protected void redirectStreams(Process process) {
			StreamRedirector inStream = new StreamRedirector(process.getInputStream(), System.out::println);
			StreamRedirector errorStream = new StreamRedirector(process.getErrorStream(), System.err::println);

			Executors.newSingleThreadExecutor().submit(inStream);
			Executors.newSingleThreadExecutor().submit(errorStream);
		}
	}
	
	private class CompileAndRun extends Compile {
		
		@Override
		public void run() {
			Process process;
			if (metadataFile != null) {
				decodeMetadata();
				createProjectPath();
				process = compileInPackage();
				
				if (isCompiled(process)) {
					process = runFileInPackage();
				}
			} else {
				process = compile();
				if (isCompiled(process)) {
					process = runFile();
				}
			}
			
			if (buffer != null && buffer instanceof ProcessBuffer) {
				ProcessBuffer pb = (ProcessBuffer) buffer;
				pb.put(process);
			}
		}
		
		private boolean isCompiled(Process process) {
			int exitValue = -1;
			try {
				exitValue = process.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (exitValue == 0) {
				return true;
			} else {
				return false;
			}
		}
		
		private Process runFile() {
			runPath = new File(createRunPathForRunning(file.getName()));
			
			CommandBuilder cb = new CommandBuilder(CommandBuilder.RUN);
			cb.setJDK(JDKPath);
			cb.setRunPath(runPath.getPath());
			
			String command = cb.generateCommand();
			
			Process process = executeCommand(command, file.getParentFile());
		
			redirectStreams(process);
			return process;
		}
		
		private Process runFileInPackage() {
			runPath = new File(createRunPathForRunning(super.runPath.getPath()));
			
			CommandBuilder cb = new CommandBuilder(CommandBuilder.RUN);
			cb.setJDK(JDKPath);
			cb.setRunPath(runPath.getPath());

			cb.setInternalLibraries(internalLibraries);
			cb.setExternalLibraries(externalLibraries);
			cb.setDirectory(directory);
			
			Metadata metadata = new Metadata(metadataFile);
			RunnableClass rc = metadata.containRunnableClass(runPath.getPath());
			
			if (rc != null) {
				cb.setProgramArguments(rc.getPaArguments());
				cb.setVMArguments(rc.getVmArguments());
			}
					
			String command = cb.generateCommand();
			
			Process process = executeCommand(command, projectFile);
			
			// Runs command
			redirectStreams(process);
			
			return process;
		}
		
		private String createRunPathForRunning(String runPath) {
			String newRunPath;
			newRunPath = runPath.replaceAll(Matcher.quoteReplacement("src" + File.separator), "");
			newRunPath = newRunPath.replaceAll(".java", "");
			
			return newRunPath;
		}	
	}
}