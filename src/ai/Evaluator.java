/*
 * Created on 04.01.2007
 */
package ai;

import gameelements.GameSituation;
import gameelements.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import player.Player;

/**
 * Provides a method for evaluating a game situation. Is used by the strategy 
 * class for the prime evaluation as well as for pre-evaluations for sorting 
 * purposes.
 */
abstract class Evaluator
{
	/**
	 * The strategy the evaluation method belongs to.
	 */
	Strategy strategy;
	
	/**
	 * The system time in milliseconds when evaluation starts.
	 */
	long startTime;
	
	/**
	 * The total passed time that has passed from evaluation start until end.
	 */
	long passedThinkingTime;

	/**
	 * Initiates evaluation method for chosen strategy.
	 * @param strategy the strategy for which the evaluator is needed.
	 */
	Evaluator(Strategy strategy)
	{
		this.strategy = strategy;
	}

	/**
	 * Evaluates a position in a game situation for the given player.
	 * @param player the player for which the position is being evaluated.
	 * @param situation the game situation on which evaluation takes place.
	 * @param pos the position that is being evaluated.
	 * @return the evaluated value
	 */
	abstract Double evaluatePosition(Player player, GameSituation situation, Position pos);
	
	/**
	 * Triggers the evaluation process, notes the passing time for evaluation,
	 * and returns the move, that has been evaluated as best move.
	 * @return the best evaluated move.
	 */
	Position selectMove()
	{
		System.out.println("calculate next move");
		startTime = System.currentTimeMillis();
		
		Position selectedPos;
		
		selectedPos = selectBestPosition();
		
		passedThinkingTime = System.currentTimeMillis()-startTime;
		System.out.println("calculation took " + passedThinkingTime + " ms");
			
		return selectedPos;
	}

	/**
	 * Returns the best move according to evaluation.
	 * @return the best move according to evaluation.
	 */
	Position selectBestPosition()
	{
		Position bestPos;
		
		List<Position> legalPositions = getLegalPositions(getPlayer(), getPlayer().getSituation());
		
		List<EvaluablePosition> evaluatedPositions = getEvaluatedPositions(legalPositions, getPlayer());
		
		List<Position> bestPositions = getBestPositions(evaluatedPositions);
		
		bestPos = getOneBestPosition(bestPositions);
		
		return bestPos;
	}
	
	/**
	 * Returns all legally playable positions for a player on a given game 
	 * situation.
	 * @param player the player who will move.
	 * @param situation the situation on which the move will take place.
	 * @return all legally playable positions for the player and situation.
	 */
	protected List<Position> getLegalPositions(Player player, GameSituation situation)
	{
		List<Position> list = new ArrayList<Position>();
		for (Position aPos : situation.getPositions())
		{
			if (situation.getColor(aPos) == null || situation.getColor(aPos) == player.getColor())
			{
				list.add( aPos );
			}
		}
		return list;
	}
	
	/**
	 * Takes a list of positions, evaluates each position, and returns a list
	 * of sorted evaluated positions.
	 * @param positionList the positions that are going to be evaluated.
	 * @param player the player for which the evaluation will be processed.
	 * @return a sorted list of evaluated positions.
	 */
	protected List<EvaluablePosition> getEvaluatedPositions(List<Position> positionList, Player player)
	{
		List<EvaluablePosition> evaluatedPositions = new ArrayList<EvaluablePosition>();
		for (Position aPos : positionList)
		{
			Double evaluation = this.evaluatePosition(getPlayer(), getPlayer().getSituation(), aPos);
			if (evaluation == null)
			{
				return new ArrayList<EvaluablePosition>();
			}
			EvaluablePosition anEvalPos= new EvaluablePosition(aPos.getX(), aPos.getY());
			anEvalPos.setEvaluation(evaluation);
			evaluatedPositions.add(anEvalPos);
		}
		
		Comparator<EvaluablePosition> comp; 
		if (player.equals(this.getPlayer()))
			comp = EvaluablePosition.comparatorDesc();
		else
			comp = EvaluablePosition.comparatorAsc();
		Collections.sort(evaluatedPositions, comp);
		
		return evaluatedPositions;
	}
	
	/**
	 * Returns a list of the best evaluated positions, if more than one 
	 * position with the highest evaluation value exist.
	 * @param evaluatedPositions a list of evaluated positions.
	 * @return the best evaluated positions.
	 */
	protected List<Position> getBestPositions(List<EvaluablePosition> evaluatedPositions)
	{
		List<Position> bestPositions = new ArrayList<Position>();
		
		if (evaluatedPositions.isEmpty())
		{
			return bestPositions;
		}
		
		double maxEval = evaluatedPositions.get(0).getEvaluation();
		
		for (EvaluablePosition anEvalPos : evaluatedPositions)
		{
			if ( anEvalPos.getEvaluation() != maxEval )
				break;
			
			bestPositions.add(new Position(anEvalPos.getX(), anEvalPos.getY()) );
		}
		
		return bestPositions;
	}
	
	/**
	 * Picks one position out of a list of positions. By default the choice is
	 * done randomly.
	 * @param bestPositions a list of equally evaluated best positions.
	 * @return one best position out of that list.
	 */
	protected Position getOneBestPosition(List<Position> bestPositions)
	{
		int choice = random(0, bestPositions.size()-1);
		return bestPositions.get(choice);
	}
	
	/**
	 * Determines if a position in a situation is threatened by the opponnent 
	 * of a given player.
	 * @param player the player who may be threatened by the opponent.
	 * @param situation the game situation the threat is estimated for.
	 * @param pos the position where the threat might take place.
	 * @return true, if the position is threatened by the opponent, else false.
	 */
	protected boolean isThreatened(Player player, GameSituation situation, Position pos)
	{
		for (Position anOpponent : opponentsOfPosition(player, situation, pos))
		{
			return situation.isFull(anOpponent);
		}
		return false;
	}
	
	/**
	 * Determines if a position in a situation is occupied by the opponent of
	 * a given player.
	 * @param player the player for whom it will be determined, if a position 
	 * is occupied by the opponent
	 * @param situation the game situation on which opponent positions will be 
	 * determined.
	 * @param position the position that is being tested for occupation by the 
	 * opponent.
	 * @return true, if position is occupied by opponent, else false.
	 */
	protected boolean isOpponent(Player player, GameSituation situation, Position position)
	{
		return (player.getColor() != situation.getColor(position)) && situation.getColor(position) != null;
	}
	
	/**
	 * Determines, if a position in a situation has one or more neighbor 
	 * positions, that are occupied by the opponent. 
	 * @param player the player for whom it will be determined, if a position
	 * has neighbors occupied by the opponent.
	 * @param situation the situation on which determination is taking place.
	 * @param pos the position for which its neighbors are being estimated.
	 * @return true, if position has neighbors occupied by the opponent, else 
	 * false.
	 */
	protected boolean hasOpponents(Player player, GameSituation situation, Position pos)
	{
		return opponentsOfPosition(player, situation, pos).size() > 0;
	}
	
	/**
	 * Lists a positions neighbors, that are occupied by the opponent.
	 * @param player the player for whom the opponent fields will be listed.
	 * @param situation the situation on which determination is taking place.
	 * @param pos the position for which its neighbors, occupied by the 
	 * opponent, will be listed
	 * @return a list of a positions neighbors, that are occupied by the 
	 * opponent.
	 */
	protected ArrayList<Position> opponentsOfPosition(Player player, GameSituation situation, Position pos)
	{
		ArrayList<Position> array = new ArrayList<Position>();
		for ( Position aNeighbor : player.getGame().getSituation().getNeighbors(pos) )
		{
			if ( isOpponent(player, situation, aNeighbor) )
			{
				array.add(aNeighbor);
			}
		}
		return array;
	}
	
	/**
	 * Returns a random int value between a given min and max value.
	 * @param min the minimum value.
	 * @param max the maximum value.
	 * @return a random int value.
	 */
	protected int random(int min, int max) 
	{
	    return (int)(Math.random() * ((max+1)-min) ) + min;
	}
	
	
	private Player getPlayer()
	{
		return this.strategy.getPlayer();
	}
	
}
