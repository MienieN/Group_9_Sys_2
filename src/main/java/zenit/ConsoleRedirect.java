package main.java.zenit;

import java.io.PrintStream;
import main.java.zenit.console.ConsoleArea;
import main.java.zenit.console.ConsoleAreaOutputStream;
import main.java.zenit.console.ConsoleAreaErrorStream;

public class ConsoleRedirect {
	/**
	 Redirects the system's standard output and error streams to a specified {@code ConsoleArea}.
	 This allows all console output to be displayed in a custom UI component instead of the default system console.
	 * @param consoleArea the {@code ConsoleArea} where console output and error messages will be redirected.
	 */
	public ConsoleRedirect(ConsoleArea consoleArea) {
		try {
		
			ConsoleAreaOutputStream outputStream = new ConsoleAreaOutputStream(consoleArea);
			ConsoleAreaErrorStream errorStream = new ConsoleAreaErrorStream(consoleArea);

			PrintStream consoleOut = new PrintStream(outputStream);
			PrintStream consoleErr = new PrintStream(errorStream);

			System.setOut(consoleOut);
			System.setErr(consoleErr);

			// TODO System.setIn(in);
			
		} catch (Exception e) {
			System.out.println("Error in ConsoleRedirect = " + e);
		}
	}	
}