/*
 * Created on 02.10.2006
 */
package gameelements;

/**
 * This class represents the two existing colors used for players
 * and game tokens.
 */
public class GameColor
{
	/**
	 * Game color white.
	 */
	public static final GameColor WHITE = new GameColor("white");
	
	/**
	 * Game color black.
	 */
	public static final GameColor BLACK = new GameColor("black");
	
	/**
	 * Value of the game color, either black or white.
	 */
	private String value;
	
	private GameColor(String value)
	{
		this.value = value;
	}
		
	/**
	 * Returns the value of the game color, either black or white.
	 * @return the value of the game color.
	 */
	public String getValue()
	{
		return value;
	}
	
	/**
	 * Returns black color if called on white color, and vice versa.
	 * @return the opposite color.
	 */
	public GameColor getOppositeColor()
	{
		if (this == WHITE)
		{
			return BLACK;
		}
		else
		{
			return WHITE;
		}
	}
}
