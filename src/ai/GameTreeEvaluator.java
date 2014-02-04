/*
 * Created on 05.10.2006
 */
package ai;

import gameelements.GameSituation;
import gameelements.ConsoleGame;
import gameelements.GamePreferences;
import gameelements.Position;

import java.util.ArrayList;
import java.util.List;

import network.ServerPreferences;
import player.Player;

/**
 * Evaluates a game situation by building up a game tree and examining the best
 * possible move within the generated game tree, utilizing an alphabeta 
 * algorhithm and iterative tree search.
 */
class GameTreeEvaluator extends Evaluator
{
	/**
	 * Weight factor for opponents tokens that are threatend by self.
	 */
	private static final double W_GAIN = 1.0;
	
	/**
	 * Weight factor for own tokens that are threatened by the opponent.
	 */
	private static final double W_LOSS = 0.5;
	
	/**
	 * Maximum tree depth limit.
	 */
	private static int MAX_TREE_DEPTH = 20;
	
	/**
	 * Tree depth of current tree iteration.
	 */
	private int currentMaxTreeDepth;
	
	/**
	 * Is set to true, if the tree search finds a winning situation for the 
	 * player.
	 */
	private boolean isWinningSituation;
	
	/**
	 * A fast evaluator for providing a value for sorting.
	 */
	private RuleBasedEvaluator secondaryEvaluator;
	
	/**
	 * Initiates an evaluator.
	 * @param strategy the strategy the evaluator is used for.
	 */
	GameTreeEvaluator(Strategy strategy)
	{
		super(strategy);
		secondaryEvaluator = new RuleBasedEvaluator(strategy);
	}
	
	/* (non-Javadoc)
	 * @see ai.Evaluator#selectBestPosition()
	 */
	Position selectBestPosition()
	{
		Position bestPos;
		
		boolean hasReachedMaxThinkingTime = false;
		
		currentMaxTreeDepth = 0;
		isWinningSituation = false;
				
		List<Position> bestPositions = getLegalPositions(getPlayer(), getPlayer().getSituation());
		bestPositions = getSecondaryEvaluatedPositions(bestPositions, getPlayer()); // presort
		
		List<Position> currentBestPositions;
		do
		{
			currentMaxTreeDepth++;
			
			List<EvaluablePosition> evaluatedPositions = getEvaluatedPositions(bestPositions, getPlayer());
			
			currentBestPositions = getBestPositions(evaluatedPositions);
			
			if (currentBestPositions.isEmpty())
			{
				hasReachedMaxThinkingTime = true;
			}
			else
			{
				bestPositions = currentBestPositions;
			}
		}
		while (!hasReachedMaxThinkingTime && !isWinningSituation && currentMaxTreeDepth < MAX_TREE_DEPTH);
		
		// sort equally evaluated positions with secondary evaluation method
		List<EvaluablePosition> postEvalPositions = secondaryEvaluator.getEvaluatedPositions(bestPositions, getPlayer());
		
		bestPositions = getBestPositions(postEvalPositions);
		
		bestPos = getOneBestPosition(bestPositions);
		
		return bestPos;
	}
	
	
	/**
	 * Sorts the given list of positions according to a fast evaluation 
	 * algorithm. 
	 * @param positions the legal positions for the player.
	 * @param player the player who wants to move.
	 * @return a list of positions sorted by a fast evaluation algorithm.
	 */
	private List<Position> getSecondaryEvaluatedPositions(List<Position> positions, Player player)
	{
		List<Position> sortedList = new ArrayList<Position>();
		List<EvaluablePosition> evalList = secondaryEvaluator.getEvaluatedPositions(positions, player);
		for (EvaluablePosition anEvalPos : evalList)
		{
			sortedList.add(new Position(anEvalPos.getX(), anEvalPos.getY()));
		}
		return sortedList;
	}
	
	
	/* (non-Javadoc)
	 * @see ai.Evaluator#evaluatePosition(player.Player, gameelements.GameSituation, gameelements.Position)
	 */
	Double evaluatePosition(Player currentPlayer, GameSituation situation, Position pos)
	{
		GameSituation situationCopy = situation.clone();
		situationCopy.addToken(pos, getPlayer().getColor(), false);
		
		double initialAlpha = -Double.MAX_VALUE;
		double initialBeta = Double.MAX_VALUE;
		
		Double rating = this.alphabeta(situationCopy, 1, initialAlpha, initialBeta); 
		
		System.out.println(
				"move(x=" + pos.getX() + ", y=" + pos.getY()+ ") --> rating (level= " + + currentMaxTreeDepth +"): " + rating);
		
		return rating;
	}
	

	/**
	 * Evaluates the given game situation as a whole.
	 * @param situation the situation to evaluate.
	 * @return an evaluation value for the situation.
	 */
	double evaluateSituation(GameSituation situation)
	{
		double value = 0.0;
		
		for (Position aPos : situation.getPositions())
		{
			if (situation.getColor(aPos) != null)
			{
				// field occupied by self
				if (situation.getColor(aPos) == getPlayer().getColor())
				{
					if ( isThreatened(getPlayer(), situation, aPos) )
					{
						value -= W_LOSS * situation.getTokens(aPos);
					}
					else // not threatened
					{
						value += situation.getTokens(aPos);
					}
				}
				else // field occupied by opponent
				{
					if ( isThreatened(getPlayer(), situation, aPos) )
					{
						value += W_GAIN * situation.getTokens(aPos);
					}
					else // not threatened
					{
						value -= situation.getTokens(aPos);
					}
				}
			}
		}
		
		return value;
	}
	

	private Double alphabeta(GameSituation situation, int depth, Double alpha, Double beta)
	{
		this.addThinkingTime();
		if (!isInTime())
		{
			return null; // quit, if over time
		}
			
		if (situation.isUniColored())
		{
			if (situation.getColor().equals(getPlayer().getColor()))
			{
				isWinningSituation = true;
				return Double.MAX_VALUE;	// winning situation
			}
			else
			{
				return Double.MIN_VALUE;	// losing situation
			}
		}
		
		if (depth == currentMaxTreeDepth || situation.isUniColored())	// is leaf
		{
			double eval = evaluateSituation(situation); 
			return eval;
		}
		
		Player activePlayer = activePlayerOfDepth(depth);
		
		List<Position> legalMoves = getLegalPositions(activePlayer, situation);
		legalMoves = getSecondaryEvaluatedPositions(legalMoves, activePlayer); // presort
		for (Position aMove : legalMoves)
		{
			// do imaginary move
			GameSituation situationCopy = situation.clone();
			situationCopy.addToken(aMove, activePlayer.getColor(), false);
			
			if (isAlpha(depth))				// MAX-player has turn
			{
				// evaluate
				Double nextAlpha = alphabeta(situationCopy, depth+1, alpha, beta);
				if (nextAlpha == null) // for quick resolvance of recursion, if max time is reached
				{
					return null;
				}
				alpha = Math.max(alpha, nextAlpha);
				
				if (alpha >= beta)
				{
					return alpha;			// Beta-Cutoff
				}
			}
			else 							// MIN-player has turn
			{
				// evaluate
				Double nextBeta = alphabeta(situationCopy, depth+1, alpha, beta);
				if (nextBeta == null) // for quick resolvance of recursion, if max time is reached
				{
					return null;
				}
				beta = Math.min(beta, nextBeta);
				
				if (alpha >= beta)
				{
					return beta;			// Alpha-Cutoff
				}
			}
		}
		
		if (isAlpha(depth))					// MAX-player has turn
		{
			return alpha;
		}
		else 								// MIN-player has turn
		{
			return beta;
		}
	}

	private long getMaxThinkingTime()
	{
		if (getPlayer().getGame() instanceof ConsoleGame)
		{
			return ServerPreferences.getInstance().getThinkingTime();
		}
		else
		{
			return GamePreferences.getInstance().getMaxThinkingTime();
		}
	}

	private Player getPlayer()
	{
		return strategy.getPlayer();
	}
	
	private boolean isAlpha(int depth)
	{
		return depth % 2 == 0;
	}
	
	private boolean isInTime()
	{
		// the minimal tree depth will always be calculated fully - regardless of the max time given.
		return currentMaxTreeDepth <= 1 || (passedThinkingTime < 0.99 * getMaxThinkingTime());
	}
	
	private void addThinkingTime()
	{
		passedThinkingTime = System.currentTimeMillis() - startTime;
	}
	
	private Player activePlayerOfDepth(int depth)
	{
		return depth % 2 == 0 ? getPlayer() : getPlayer().getOpponent();
	}
	
}
