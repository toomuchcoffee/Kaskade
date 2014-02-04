/*
 * Created on 30.12.2006
 */
package player;

import gameelements.Game;
import gameelements.GameColor;

/**
 * A player who communicates in a synchronous manner if asked for the next 
 * move.
 */
public abstract class SynchronousPlayer extends Player
{
	/**
	 * Initiates a synchronously communicating player.
	 * @param name the name of the player.
	 * @param game the game the player participates in.
	 * @param color the color of the player.
	 */
	public SynchronousPlayer(String name, Game game, GameColor color)
	{
		super(name, game, color);
	}

	/**
	 * Custom initialization for the player.
	 */
	public abstract void init();
}
