/*
 * Created on 04.01.2007
 */
package network;

/**
 * Exception thrown if an illegal command occurs within a client-server
 * -communication via the 'Kaskade'-protocol.
 */
@SuppressWarnings("serial")
public class IllegalCommandException extends Exception
{
	/**
	 * Initiates an illegal command exception.
	 * @param message the message of the exception.
	 */
	public IllegalCommandException(String message)
	{
		super(message);
	}
}
