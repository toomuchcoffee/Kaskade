/*
 * Created on 15.09.2006
 */
package gameelements;

import java.util.List;

/**
 * This class holds information about a certain game situation on a 
 * game board, like the number and color of game tokens in each
 * field. It thus represents the core of the game. It is used for
 * displaying the situation on the gui as well as for calculations
 * on future moves.
 */
public class GameSituation implements Cloneable
{
	/**
	 * Contains all fields of a situation in a 2-dimensional byte array.
	 */
	private byte[][] fields;
	
	/**
	 * The corresponding game board the game situation belongs to.
	 */
	protected GameBoard gameBoard;
	
	/**
	 * Class that contains properties for setting up a field within
	 * a board at initialization, to present a preceded game 
	 * situation. 
	 */
	public static class FieldSetup
	{
		/**
		 * The position of a game field within a board.
		 */
		public Position position;
		
		/**
		 * The color of the tokens that are being placed into the 
		 * field.
		 */
		public GameColor color;
		
		/**
		 * The number of tokens that are being placed into the field.
		 */
		public int tokens;
	}
	
	/**
	 * Constructs a game situation, either with predefined setup
	 * or with no tokens placed at all.
	 * @param gameBoard the gameBoard holding the dimensions and 
	 * structural properties of a corresponding board. 
	 * @param initialFieldSettings list of setup entities for 
	 * initializing a board with a preceded game situation.
	 */
	public GameSituation(GameBoard gameBoard, List<FieldSetup> initialFieldSettings)
	{
		this.init(gameBoard);
		
		if (initialFieldSettings != null)
		{
			for (FieldSetup aFieldSetting: initialFieldSettings)
			{
				int x = aFieldSetting.position.getX();
				int y = aFieldSetting.position.getY();
				int col = aFieldSetting.color == GameColor.BLACK ? -1 : 1;
				fields[x][y] = (byte) (col * aFieldSetting.tokens);
			}
		}
	}
	
	private void init(GameBoard gameBoard)
	{
		this.gameBoard = gameBoard;
		this.fields = new byte[gameBoard.getDimX()][gameBoard.getDimY()];
	}
	
	/**
	 * @see GameBoard#getPositions()
	 */
	public List<Position> getPositions()
	{
		return gameBoard.getPositions();
	}

	/**
	 * @see GameBoard#getDimX()
	 */
	public int getDimX()
	{
		return gameBoard.getDimX();
	}
	
	/**
	 * @see GameBoard#getDimY()
	 */
	public int getDimY()
	{
		return gameBoard.getDimY();
	}
	
	/**
	 * @see GameBoard#getLimit(Position)
	 */
	public int getLimit(Position pos)
	{
		return gameBoard.getLimit(pos);
	}

	/**
	 * @see GameBoard#getNeighbors(Position).
	 */
	public List<Position> getNeighbors(Position pos)
	{
		return gameBoard.getNeighbors(pos);
	}
		
	/**
	 * Returns the number of tokens held within the field of a
	 * given position.
	 * @param pos the position of the game field.
	 * @return the number of tokens in this field.
	 */
	public int getTokens(Position pos)
	{
		byte value = fields[pos.getX()][pos.getY()];
		if (value < 0)
		{
			return -value;
		}
		return value;
	}
	
	/**
	 * Returns the total number of tokens on the board, regardless 
	 * of color.
	 * @return the total number of tokens on the board. 
	 */
	public int getTokens()
	{
		int sum = 0;
		for (int x=0; x<getDimX(); x++)
			for (int y=0; y<getDimY(); y++)
				sum += Math.abs( fields[x][y] );
		
		return sum;
	}
	
	/**
	 * Returns the color of the tokens placed in the field of the
	 * given position. Returns null, if field is empty.
	 * @param pos theposition of the game field.
	 * @return the color of the tokens in this field.
	 */
	public GameColor getColor(Position pos)
	{
		byte value = fields[pos.getX()][pos.getY()];
		if (value < 0)
			return GameColor.BLACK;
		else if (value > 0)
			return GameColor.WHITE;
		else // == 0
			return null;
	}
	
	/**
	 * Indicates, if field of the given position has reached its
	 * capacity or if it can take another number of tokens.
	 * @param pos
	 * @return returns true, if field will overflow with an 
	 * additional token, otherwise returns false.
	 */
	public boolean isFull(Position pos)
	{
		byte value = fields[pos.getX()][pos.getY()];
		return gameBoard.getLimit(pos) - Math.abs(value) == 1;
	}
	
	/**
	 * Indicates, if a field is currently in the state of flowing
	 * over.
	 * @param pos the position of the field.
	 * @return returns true, if field is currently flowing over.
	 */
	public boolean isFlowingOver(Position pos)
	{
		byte value = fields[pos.getX()][pos.getY()];
		return Math.abs(value) >= gameBoard.getLimit(pos);
	}
	

	/**
	 * Indicates, if a field is empty or holds tokens.
	 * @param pos the position of the field.
	 * @return returns true, if field does not contain any tokens.
	 */
	public boolean isEmpty(Position pos)
	{
		return this.getTokens(pos) == 0;
	}
	
	/**
	 * Adds a token of the given color in a given field. An overflow is being 
	 * triggered in case of a full field.
	 * @param pos the position of the field.
	 * @param color the color of the token being added.
	 */
	public void addToken(Position pos, GameColor color, boolean display)
	{
		this.relocateToken( pos, color );
		gameBoard.manageOverflows( this, pos, display );
	}

	/**
	 * Adds a token of the given color in a given field. Does not trigger any
	 * overflows. Just adds the token to the field and changes the the color of
	 * already placed tokens in the field, if they have a different color.
	 * @param pos the position of the field.
	 * @param color the color of the token being added.
	 */
	void relocateToken(Position pos, GameColor color)
	{
		byte value = fields[pos.getX()][pos.getY()];
		byte t = (byte) (Math.abs(value) + 1);
		if (color == GameColor.BLACK)
		{
			this.fields[pos.getX()][pos.getY()] = (byte) -t;
		}
		else
		{
			this.fields[pos.getX()][pos.getY()] = t;
		}
	}

	/**
	 * Removes a token from a field.
	 * @param pos the position of the field. 
	 * @return the color of the removed token.
	 */
	GameColor removeToken(Position pos)
	{
		GameColor color = null;
		
		byte value = fields[pos.getX()][pos.getY()];
		byte t = (byte) Math.abs(value);
		if (t > 0)
		{
			t -= 1;
		}
		
		if (value < 0)
		{
			color = GameColor.BLACK;
			this.fields[pos.getX()][pos.getY()] = (byte) -t;
		}
		else if (value > 0)
		{
			color = GameColor.WHITE;
			this.fields[pos.getX()][pos.getY()] = t;
		}
		return color;
	}
	
	/**
	 * Returns the color of the board, if it is uni-colored. Returns null, if 
	 * the containig fields still hold both colors. Also returns null, if there
	 * are less than two tokens placed on the board.
	 * @return the color of the board, if uni-colored, else null.
	 */
	public GameColor getColor()
	{
		int countNot0 = 0;
		int countPos = 0;
		int countNeg = 0;
		
		for (int x=0; x<gameBoard.getDimX(); x++)
		{
			for (int y=0; y<gameBoard.getDimY(); y++)
			{
				byte val = fields[x][y];
				
				if (val != 0)
				{
					countNot0++;
					
					if (val > 0)
						countPos++;
					else
						countNeg++;
				}
			}
		}
		
		// for evaluation of a win-situation, there have to be at 
		// least two tokens placed on the board.
		if (countNot0 < 2)
			return null;
		
		else if (countNot0==countPos)
			return GameColor.WHITE;
		
		else if (countNot0==countNeg)
			return GameColor.BLACK;
		
		else 
			return null;
	}
	
	/**
	 * Returns true if all fields of the board are either empty
	 * or hold tokens of the same color. There have to be at least 
	 * two tokens placed on board to be considerered uni-colored.
	 * @return true, if all fields are in the same color.
	 */
	public boolean isUniColored()
	{
		return this.getColor() != null;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	public GameSituation clone()
	{
		GameSituation situationClone = null;
		try
		{
			situationClone = (GameSituation) super.clone();
			int dimX = gameBoard.getDimX();
			int dimY = gameBoard.getDimY();
			situationClone.fields = new byte[dimX][dimY];
			for (int y=0; y<dimY; y++)
				for (int x=0; x<dimX; x++)
					situationClone.fields[x][y] = this.fields[x][y];
		} 
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return situationClone;
	}

}
