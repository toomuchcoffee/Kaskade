/*
 * Created on 24.09.2006
 */
package ai;

import gameelements.Position;
import player.Player;

/**
 * Contains the intelligence of the computer player. Enables player to judge
 * the current game situation and decide for an advantageous move.
 */
public class Strategy
{
	/**
	 * The owning player of the strategy.
	 */
	private Player player;
	
	/**
	 * The evaluation method used for deciding the next move.
	 */
	private Evaluator evaluator;
	
	/**
	 * Constant for easy strategy.
	 */
	public static final int STRATEGY_EASY = 1;
	
	/**
	 * Constant for medium strategy.
	 */
	public static final int STRATEGY_MEDIUM = 2;
	
	/**
	 * Constant for difficult strategy.
	 */
	public static final int STRATEGY_HARD = 0;
	
	/**
	 * Initiates strategy for the player and according to the given type.
	 * @param player the player who draws moves with this strategy
	 * @param strategyType the type of the chosen strategy
	 */
	public Strategy(Player player, int strategyType)
	{
		this.player = player;
		switch (strategyType)
		{
			case STRATEGY_EASY : 
			{
				evaluator = new RandomEvaluator(this);
				break;
			}
			case STRATEGY_MEDIUM :
			{
				evaluator = new RuleBasedEvaluator(this);
				break;
			}
			default :
			{
				evaluator = new GameTreeEvaluator(this);
				break;
			}
		}
	}
	
	/**
	 * Returns the next move of the player.
	 * @return the next move of the player.
	 */
	public Position requestMove()
	{
		return evaluator.selectMove();
	}

	/**
	 * Returns the owning player of this strategy.
	 * @return the owning player of this strategy.
	 */
	Player getPlayer()
	{
		return player;
	}
	
}
