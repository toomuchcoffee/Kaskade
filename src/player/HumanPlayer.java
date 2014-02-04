/*
 * Created on 16.10.2006
 */
package player;

import gameelements.Game;
import gameelements.GameColor;

/**
 * Provides a basic implementation for the human player.
 */
public class HumanPlayer extends AsynchronousPlayer
{
	
	/**
	 * Initiates a human player.
	 * @param name the name of the player.
	 * @param game the game the player participates in.
	 * @param color the color of the player.
	 */
	public HumanPlayer(String name, Game game, GameColor color)
	{
		super(name, game, color);
	}

}
