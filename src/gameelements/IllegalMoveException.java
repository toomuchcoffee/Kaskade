/*
 * Created on 04.01.2007
 */
package gameelements;

/**
 * This Exception is thrown, when an illegal move happens. E.g. 
 * placing a token on a field, which is occupied by the opponent. 
 */
@SuppressWarnings("serial")
public class IllegalMoveException extends Exception
{
	/**
	 * Constructor.
	 * @param message the message for the exception.
	 */
	public IllegalMoveException(String message)
	{
		super(message);
	}
}