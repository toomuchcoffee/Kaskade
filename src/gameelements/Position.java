/*
 * Created on 31.12.2006
 */
package gameelements;

/**
 * Class for keeping x- and y-coordinates of a field position in one data 
 * structure. Though very similiar to the @see Point-class, it has been 
 * rewritten for the convenience of having int instead of double as coordinate 
 * value types. Two Positions are considered equal if their x- and y-values
 * are equal. Thus, @see Object#hashCode() and 
 * @see Object#equals(Object) have been overidden. 
 */
public class Position
{
	/**
	 * The x-coordinate of the position.
	 */
	private int x;
	
	/**
	 * The y-coordinate of the position.
	 */
	private int y;
	
	/**
	 * Class for holding x- and y-ccordinates of a field position on
	 * a game board.
	 * @param x the positions x-ccordinate.
	 * @param y the positions y-coordinate.
	 */
	public Position(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the positions x-coordinate.
	 * @return the positions x-coordinate.
	 */
	public int getX()
	{
		return x;
	}
	
	/**
	 * Returns the positions y-coordinate.
	 * @return the positions y-coordinate.
	 */
	public int getY()
	{
		return y;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return 1000*this.y + this.x;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (o instanceof Position)
		{
			Position pos = (Position) o;
			return this.x==pos.getX() && this.y==pos.getY();
		}
		return false;
	}
}
