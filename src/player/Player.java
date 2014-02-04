/*
 * Created on 24.09.2006
 */
package player;

import gameelements.GameSituation;
import gameelements.Game;
import gameelements.GameColor;
import gameelements.IllegalMoveException;
import gameelements.Position;

/**
 * Abstract class for encapsulating player logic. As the player types are very
 * different in regards of calculating their next move and communication with
 * the local game, the implementation of this logic is left to specialized 
 * classes.
 */
public abstract class Player
{
	/**
	 * Identiier for human player type.
	 */
	public static final int TYPE_HUMAN = 0;
	
	/**
	 * Identifier for console player type.
	 */
	public static final int TYPE_CONSOLE = 1;
	
	/**
	 * Identifier for remote player type.
	 */
	public static final int TYPE_REMOTE = 2;
	
	/**
	 * Identifier for computer player type.
	 */
	public static final int TYPE_COMPUTER = 3;
	
	/**
	 * The players name.
	 */
	protected String name;
	
	/**
	 * The game the player participates in.
	 */
	protected Game game;
	
	/**
	 * The color of tokens the player plays in this game.
	 */
	protected GameColor color;

	/**
	 * Initiates a player with name, color and the game he plays in.
	 * @param name the name of the player
	 * @param game the game the player participates in.
	 * @param color the color of the player.
	 */
	protected Player(String name, Game game, GameColor color)
	{
		this.name = name;
		this.game = game;
		this.color = color;
	}
	
	/**
	 * Factory method for creating the correct player according to the given
	 * player type. Returns an instance of a player.
	 * @param name the name of the player.
	 * @param game the game the player participates in.
	 * @param color the color of the player.
	 * @param type the type of the player.
	 * @return an instance of a player.
	 */
	public static Player createPlayer(String name, Game game, GameColor color, int type)
	{
		Player player;
		
		switch (type)
		{
			case TYPE_CONSOLE : 
			{
				player = new ConsolePlayer(name, game, color);
				break;
			}
			
			case TYPE_REMOTE : 
			{
				player = new RemotePlayer(name, game, color);
				break;
			}
			
			case TYPE_COMPUTER : 
			{
				player = new ComputerPlayer(name, game, color);
				break;
			}
			
			default : 
			{
				player = new HumanPlayer(name, game, color);
				break;
			}
		}
		
		return player;
	}
	
	/**
	 * Returns the next move of the player for the running game.
	 * @return the next Move of the player.
	 * @throws IllegalMoveException
	 */
	public abstract Position getNextMove() throws IllegalMoveException;
	
	/**
	 * Returns the name of the player.
	 * @return the name of the player.
	 */
	public String getPlayerName()
	{
		return name;
	}
	
	/**
	 * Sets the name of the player.
	 * @param name the name of the player to set.
	 */
	public void setPlayerName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the color of the player.
	 * @return the color of the player.
	 */
	public GameColor getColor()
	{
		return color;
	}
	
	/**
	 * Returns the opponent player of this player.
	 * @return the opponent player of this player.
	 */
	public Player getOpponent()
	{
		return game.getOpponent(this);
	}
	
	/**
	 * Returns the game the player particpates in.
	 * @return the game the player particpates in.
	 */
	public Game getGame()
	{
		return game;
	}
	
	/**
	 * Returns the current game istuation the player has to deal with.
	 * @return the current game istuation the player has to deal with.
	 */
	public GameSituation getSituation()
	{
		return this.getGame().getSituation();
	}
	
}
