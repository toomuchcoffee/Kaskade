/*
 * Created on 07.01.2007
 */
package animation;

import gameelements.GameSituation;
import gameelements.GameColor;
import gameelements.Position;

/**
 * A container holding information for help displaying a situation on the view.
 */
public class AnimationState
{
	/**
	 * The situation the animation state is displaying.
	 */
	private GameSituation situation;
	
	/**
	 * The move that lead to the game situation represented by this state.
	 */
	private Position currentMove;
	
	/**
	 * Initiates an animation state.
	 * @param situation the situation the state stands for.
	 * @param currentMove the move that lead to the situation.
	 */
	AnimationState(GameSituation situation, Position currentMove)
	{
		this.situation = situation;
		this.currentMove = currentMove;
	}

	/**
	 * @see GameSituation#isUniColored()
	 */
	boolean isUniColored()
	{
		return situation.isUniColored();
	}
	
	/**
	 * Returns the color of the given position.
	 * @param pos the position in question.
	 * @return the color of the given position.
	 */
	GameColor getColor(Position pos)
	{
		return situation.getColor(pos);
	}
	
	/**
	 * Returns the number of tokens in a position.
	 * @param pos the position in question.
	 * @return the number of tokens in a position.
	 */
	int getTokens(Position pos)
	{
		return situation.getTokens(pos);
	}

	/**
	 * Returns true if the given position of the current game situation 
	 * displayed on the view is flowing over, else false.
	 * @param pos the position in question.
	 * @return true, if position is flowing over, else false.
	 */
	boolean isFlowingOver(Position pos)
	{
		return situation.isFlowingOver(pos);
	}

	/**
	 * returns true if any field in the state is flowing over, else false.
	 * @return true if any field in the state is flowing over, else false.
	 */
	boolean isFlowingOver()
	{
		for (int x=0; x<situation.getDimX(); x++)
			for (int y=0; y<situation.getDimY(); y++)
				if (situation.isFlowingOver(new Position(x, y)))
					return true;
		
		return false;
	}
	
	/**
	 * Returns the current move that lead to the current state.
	 * @return the current move that lead to the current state.
	 */
	Position getCurrentMove()
	{
		return currentMove;
	}

	/**
	 * Returns the number of tokens on the whole board with the given 
	 * color.
	 * @param color color of the tokens being counted.
	 * @return the number of tokens in given color.
	 */
	int getTokensOfColor(GameColor color)
	{
		int count = 0;
		
		for (int x=0; x<situation.getDimX(); x++)
		{
			for (int y=0; y<situation.getDimY(); y++)
			{
				Position aPos = new Position(x, y);
				GameColor aColor = situation.getColor(aPos);
				
				if (color.equals(aColor))
				{
					count += situation.getTokens(aPos);
				}
			}
		}
		return count;
	}

	/**
	 * Returns the game situation the animation state is associated with.
	 * @return the game situation the animation state is associated with.
	 */
	GameSituation getSituation()
	{
		return situation;
	}

}
