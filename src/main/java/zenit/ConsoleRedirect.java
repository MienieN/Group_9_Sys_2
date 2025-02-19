package main.java.zenit;

import java.io.PrintStream;
import main.java.zenit.console.ConsoleArea;
import main.java.zenit.console.ConsoleAreaOutputStream;
import main.java.zenit.console.ConsoleAreaErrorStream;

public class ConsoleRedirect {

	public ConsoleRedirect(ConsoleArea ta) {
		try {
		
			ConsoleAreaOutputStream socat = new ConsoleAreaOutputStream(ta);
			ConsoleAreaErrorStream tacos = new ConsoleAreaErrorStream(ta);

			PrintStream outPrintStream = new PrintStream(socat);
			PrintStream errPrintStream = new PrintStream(tacos);

			System.setOut(outPrintStream);
			System.setErr(errPrintStream);

			/* 
			 * TODO
			 * System.setIn(in);
			 */
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}