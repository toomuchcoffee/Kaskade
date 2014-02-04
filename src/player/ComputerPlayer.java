/*
 * Created on 16.10.2006
 */
package player;

import gameelements.ConsoleGame;
import gameelements.Game;
import gameelements.GameColor;
import gameelements.GamePreferences;
import gameelements.Position;
import ai.Strategy;

/**
 * Represents the local computer player using the available strategy for 
 * generating moves.
 */
public class ComputerPlayer extends SynchronousPlayer
{
	/**
	 * The strategy used by this player.
	 */
	Strategy strategy;
	
	/**
	 * Initiates a computer player.
	 * @param name the name of the player.
	 * @param game the game the player participates in.
	 * @param color the color of the player.
	 */
	protected ComputerPlayer(String name, Game game, GameColor color)
	{
		super(name, game, color);
	}
	
	/* (non-Javadoc)
	 * @see player.Player#getNextMove()
	 */
	public Position getNextMove()
	{
		return strategy.requestMove();
	}
	
	/* (non-Javadoc)
	 * @see player.SynchronousPlayer#init()
	 */
	public void init()
	{
		this.strategy = new Strategy(this, getUsedStrategyType());
	}
	
	/**
	 * Returns the strategy type to set for this player.
	 * @return the strategy type to set for this player.
	 */
	private int getUsedStrategyType()
	{
		if (this.game instanceof ConsoleGame)
		{
			return Strategy.STRATEGY_HARD;
		}
		else
		{
			return GamePreferences.getInstance().getStrategy();
		}
	}
	
}
