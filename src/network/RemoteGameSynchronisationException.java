/*
 * Created on 04.01.2007
 */
package network;

/**
 * Exception thrown if synchroniszation between a local and a remote game 
 * is not successful.
 */
@SuppressWarnings("serial")
public class RemoteGameSynchronisationException extends Exception
{
	/**
	 * Initiates a remote game synchroniszation exception.
	 * @param message the message for the exception.
	 */
	public RemoteGameSynchronisationException(String message)
	{
		super(message);
	}
}
