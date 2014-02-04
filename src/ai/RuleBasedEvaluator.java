/*
 * Created on 30.09.2006
 */
package ai;

import gameelements.GameSituation;
import gameelements.Position;
import player.Player;

/**
 * Provides a quick and basic algorithm for evaluating positions.
 */
class RuleBasedEvaluator extends Evaluator
{
	/**
	 * Inititates a rule-based evaluator. 
	 * @param strategy the strategy the evlauator is used for.
	 */
	RuleBasedEvaluator(Strategy strategy)
	{
		super(strategy);
	}
	
	/* (non-Javadoc)
	 * @see ai.Evaluator#evaluatePosition(player.Player, gameelements.GameSituation, gameelements.Position)
	 */
	Double evaluatePosition(Player currentPlayer, GameSituation situation, Position pos)
	{
		// threatened but full field
		if (isThreatened(currentPlayer, situation, pos) && situation.isFull(pos))
		{
			return 5d;
		}
		
		// field which can be overtaken by the neighbor (same rank)
		if ( ! situation.isEmpty(pos) && hasOvertakingOpponents(situation, pos))
		{
			return 4d;
		}
		
		// always look for the least threatening situation
		// or stabilize situation if threats come down the way.
		if ( ! situation.isFull(pos) && hasBestRankAmongNeighbors(situation, pos) )
		{
			if ( hasOpponents(currentPlayer, situation, pos) )
			{
				return 3d;
			}
			else
			{
				return 2d;
			}
		}
		
		if ( situation.isEmpty(pos) && !hasOpponents(currentPlayer, situation, pos) )
		{
			return 1d;
		}
		
		if ( situation.isFull(pos) )
		{
			return -1d;
		}
		
		// else don't place next to the opponent
		if ( hasOpponents(currentPlayer, situation, pos) )
		{
			return -2d;
		}
		
		return 0d;
	}
	
	
	private boolean hasOvertakingOpponents(GameSituation situation, Position pos)
	{
		boolean retval = false;
		for (Position anOpponent : opponentsOfPosition(strategy.getPlayer(), situation, pos))
		{
			if ( situation.getLimit(pos) > situation.getLimit(anOpponent) )
			{
				return false; // there are stronger neighbors
			}
			else if ( situation.getLimit(pos) == situation.getLimit(anOpponent) )
			{
				retval = true;
			}
		}
		return retval;
	}
	
	private boolean hasBestRankAmongNeighbors(GameSituation situation, Position pos)
	{
		for ( Position aNeighbor : strategy.getPlayer().getSituation().getNeighbors(pos) )
		{
			if ( situation.getLimit(pos) >= situation.getLimit(aNeighbor) )
			{
				return false;
			}
		}
		return true;
	}
	
}
