/*
 * Created on 30.12.2006
 */
package ai;

import gameelements.GameSituation;
import gameelements.Position;
import player.Player;

/**
 * Does not implement a specific evaluation algorithm. 
 */
class RandomEvaluator extends Evaluator
{
	/**
	 * Initiates an evaluator with random evaluation.
	 * @param strategy
	 */
	RandomEvaluator(Strategy strategy)
	{
		super(strategy);
	}
	
	/* (non-Javadoc)
	 * @see ai.Evaluator#evaluatePosition(player.Player, gameelements.GameSituation, gameelements.Position)
	 */
	Double evaluatePosition(Player currentPlayer, GameSituation situation, Position pos)
	{
		return 0.0;
	}
}
