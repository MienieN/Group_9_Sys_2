package main.java.zenit.exceptions;
/**
 * A custom exception class representing errors related to type codes.
 * This exception is typically used to indicate issues or invalid states
 * specific to type codes that may occur during program execution.
 */
public class TypeCodeException extends Exception {
	
	/**
	 * Constructs a new {@code TypeCodeException} with no detailed message.
	 * <p>
	 * This constructor initializes the exception without any specific information.
	 * It can be used to represent a general exception scenario related to type codes,
	 * where no additional context or explanation is required.
	 * </p>
	 */
	public TypeCodeException() {
		super();
	}

	/**
	 * Constructs a new {@code TypeCodeException} with the specified detail message.
	 *
	 * @param message the detail message, which provides a description of the exception.
	 */
	public TypeCodeException(String message) {
		super(message);
	}
}
