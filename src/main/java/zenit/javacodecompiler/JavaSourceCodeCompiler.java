/**
 * JavaSourceCodeCompiler is responsible for compiling and running Java source code files.
 * It supports both compilation-only and compile-and-run modes.
 */
package main.java.zenit.javacodecompiler;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;

import main.java.zenit.console.ConsoleController;
import main.java.zenit.filesystem.RunnableClass;
import main.java.zenit.filesystem.metadata.Metadata;
import main.java.zenit.ui.MainController;

/**
 * JavaSourceCodeCompiler handles Java file compilation and execution.
 */
public class JavaSourceCodeCompiler {

	protected File file;
	protected File metadataFile;
	protected boolean inBackground;
	protected Buffer<?> buffer;
	protected MainController cont;
	protected ConsoleController consoleController;

	/**
	 * Constructs a JavaSourceCodeCompiler instance.
	 * @param file The Java source file to compile.
	 * @param inBackground Determines whether compilation runs in the background.
	 */
	public JavaSourceCodeCompiler(File file, boolean inBackground) {
		this(file, null, inBackground, null, null);
	}

	/**
	 * Constructs a JavaSourceCodeCompiler with additional parameters.
	 * @param file The Java source file.
	 * @param metadata The metadata file containing project information.
	 * @param inBackground Whether the compilation runs in the background.
	 * @param buffer The buffer for handling output and errors.
	 * @param cont The main controller of the application.
	 */
	public JavaSourceCodeCompiler(File file, File metadata, boolean inBackground, Buffer<?> buffer, MainController cont) {
		this.file = file;
		this.metadataFile = metadata;
		this.inBackground = inBackground;
		this.buffer = buffer;
		this.cont = cont;
	}

	/**
	 * Starts the compilation process.
	 */
	public void startCompile() {
		new Compile().start();
	}

	/**
	 * Starts the compilation process and executes the compiled class.
	 */
	public void startCompileAndRun() {
		new CompileAndRun().start();
	}

	/**
	 * Abstract base class for compilation processes.
	 */
	private abstract class CompilationBase extends Thread {
		protected String JDKPath;
		protected String sourcepath;
		protected String directory;
		protected File runPath;
		protected File projectFile;
		protected String[] internalLibraries;
		protected String[] externalLibraries;

		@Override
		public void run() {
			if (metadataFile != null) {
				decodeMetadata();
				createProjectPath();
			}
			Process process = compile();
			handlePostCompilation(process);
		}

		/**
		 * Initializes project file paths.
		 */
		protected void createProjectPath() {
			projectFile = metadataFile.getParentFile();
		}

		/**
		 * Decodes project metadata.
		 */
		protected void decodeMetadata() {
			Metadata metadata = new Metadata(metadataFile);
			JDKPath = metadata.getJREVersion();
			directory = metadata.getDirectory();
			sourcepath = metadata.getSourcePath();
			internalLibraries = metadata.getInternalLibraries();
			externalLibraries = metadata.getExternalLibraries();
		}

		/**
		 * Compiles the Java source file.
		 * @return The process executing the compilation.
		 */
		protected Process compile() {
			CommandBuilder cb = new CommandBuilder(CommandBuilder.COMPILE);
			cb.setJDK(JDKPath);
			cb.setRunPath(getCompilationPath());
			if (metadataFile != null) {
				cb.setDirectory(directory);
				cb.setSourcepath(sourcepath);
				cb.setInternalLibraries(internalLibraries);
				cb.setExternalLibraries(externalLibraries);
			}
			return executeAndRedirect(cb.generateCommand(), metadataFile != null ? projectFile : file.getParentFile());
		}

		protected abstract void handlePostCompilation(Process process);

		protected Process executeAndRedirect(String command, File workingDir) {
			Process process = executeCommand(command, workingDir);
			redirectStreams(process);
			return process;
		}

		protected Process executeCommand(String command, File projectFile) {
			if (inBackground) {
				DebugErrorBuffer deb = buffer instanceof DebugErrorBuffer ? (DebugErrorBuffer) buffer : null;
				return TerminalHelpers.runBackgroundCommand(command, projectFile, deb);
			}
			return TerminalHelpers.runCommand(command, projectFile);
		}

		protected void redirectStreams(Process process) {
			Executors.newSingleThreadExecutor().submit(new StreamRedirector(process.getInputStream(), System.out::println));
			Executors.newSingleThreadExecutor().submit(new StreamRedirector(process.getErrorStream(), System.err::println));
		}

		protected String getCompilationPath() {
			return metadataFile != null ? createRunPathInProject() : file.getPath();
		}

		private String createRunPathInProject() {
			return file.getPath().replaceAll(Matcher.quoteReplacement(metadataFile.getParentFile().getPath() + File.separator), "");
		}
	}

	/**
	 * Handles compilation without execution.
	 */
	private class Compile extends CompilationBase {
		@Override
		protected void handlePostCompilation(Process process) {
			if (inBackground && buffer instanceof DebugErrorBuffer) {
				cont.errorHandler((DebugErrorBuffer) buffer);
			}
		}
	}

	/**
	 * Handles compilation followed by execution.
	 */
	private class CompileAndRun extends CompilationBase {
		@Override
		protected void handlePostCompilation(Process process) {
			if (isCompiled(process)) {
				process = executeAndRedirect(getRunCommand(), projectFile);
				if (buffer instanceof ProcessBuffer) {
					((ProcessBuffer) buffer).put(process);
				}
			}
		}

		private boolean isCompiled(Process process) {
			try {
				return process.waitFor() == 0;
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return false;
			}
		}

		private String getRunCommand() {
			CommandBuilder cb = new CommandBuilder(CommandBuilder.RUN);
			cb.setJDK(JDKPath);
			cb.setRunPath(getRunPath());
			if (metadataFile != null) {
				cb.setInternalLibraries(internalLibraries);
				cb.setExternalLibraries(externalLibraries);
				cb.setDirectory(directory);
				Metadata metadata = new Metadata(metadataFile);
				RunnableClass rc = metadata.containRunnableClass(getRunPath());
				if (rc != null) {
					cb.setProgramArguments(rc.getPaArguments());
					cb.setVMArguments(rc.getVmArguments());
				}
			}
			return cb.generateCommand();
		}

		private String getRunPath() {
			String path = file.getPath();
			if (metadataFile != null) {
				path = super.runPath.getPath();
			}
			return path.replaceAll(Matcher.quoteReplacement("src" + File.separator), "").replaceAll(".java", "");
		}
	}
}