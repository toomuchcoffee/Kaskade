/*
 * Created on 07.01.2007
 */
package animation;

import gameelements.Game;
import gameelements.GameColor;
import gameelements.GamePreferences;
import gameelements.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import player.Player;

/**
 * Connects view and model by observing the model and creating information
 * for representation on the gui.
 */
public class Animator implements Observer
{
	/**
	 * The current game the animator runs for.
	 */
	private Game game;
	
	/**
	 * The current turn the animator provides for the view.
	 */
	private int currentTurn;
	
	/**
	 * Data structure used for queing-up animations states.
	 */
	private AnimationStateQueue animationStateQueue;
	
	/**
	 * The current state that is being called by the view.
	 */
	private AnimationState currentAnimationState;
	
	/**
	 * The state preceding the current state.
	 */
	private AnimationState previousAnimationState;
	
	/**
	 * The move that lead to the current state.
	 */
	private Position currentMove;
	
	/**
	 * List of positions that have been changed since last move.
	 */
	private List<Position> currentChangedPositions = new ArrayList<Position>();
	
	/**
	 * Initiates an animator for a game that should be displayed on the gui.
	 * @param game the game for which the animator is needed for.
	 */
	public Animator(Game game)
	{
		this.game = game;
		
		this.animationStateQueue = new AnimationStateQueue();
		AnimationState initialState = new AnimationState(game.getSituation().clone(), null);
		this.animationStateQueue.add( initialState );
		
		this.currentTurn = 1;
	}

	/**
	 * Adds an animation state to the queue.
	 * @param newAnimationState the animation state to add.
	 */
	void addAnimationState(AnimationState newAnimationState)
	{
		this.animationStateQueue.add(newAnimationState);
	}

	/**
	 * Processes the state queue by interpreting the start and the end of a 
	 * turn and determining all needed information needed for displaying the
	 * current animation step on the view.
	 */
	public void processStateQueue()
	{
		if (animationStateQueue.isEmpty())
			return;
		
		if (!GamePreferences.getInstance().isAnimatedSteps())
			while(!animationStateQueue.isEndOfTurn())
				animationStateQueue.remove();
		
		AnimationState nextAnimationState = animationStateQueue.remove();
		
		if (animationStateQueue.isStartOfCurrentQueue())
			currentMove = nextAnimationState.getCurrentMove();
		
		currentChangedPositions.addAll( compareAnmiationStates(previousAnimationState, nextAnimationState) );
		
		previousAnimationState = currentAnimationState;
		currentAnimationState = nextAnimationState;
		
		if (animationStateQueue.isEndOfCurrentQueue() && !currentAnimationState.getSituation().isUniColored())
		{
			currentChangedPositions = new ArrayList<Position>();
			currentMove = null;
			currentTurn = currentAnimationState.getSituation().getTokens()+1;
		}
	}
	
	private List<Position> compareAnmiationStates(AnimationState prevState, AnimationState nextState)
	{
		List<Position> changedPositions = new ArrayList<Position>();
		
		if (prevState == null)
			changedPositions.add( nextState.getCurrentMove() );
		else
			for (int x=0; x<game.getSituation().getDimX(); x++)
				for (int y=0; y<game.getSituation().getDimY(); y++)
				{
					Position aPos = new Position(x, y);
					if (prevState.getTokens(aPos) != nextState.getTokens(aPos))
					{
						changedPositions.add(aPos);
					}
				}
		
		return changedPositions;
	}

	/**
	 * Returns true if animation queue has more elements, else false.
	 * @return true if animation queue has more elements, else false. 
	 */
	public boolean hasNext()
	{
		return !animationStateQueue.isEmpty();
	}

	/**
	 * Returns the current turn that should be displazed on the view.
	 * @return the current turn that should be displazed on the view.
	 */
	public int getCurrentTurn()
	{
		return currentTurn;
	}

	/**
	 * Returns the player that is active in the current turn being displayed
	 * on the view.
	 * @return the active player of the current turn on the view.
	 */
	public Player getCurrentPlayer()
	{
		return game.getPlayerOfTurn(currentTurn);
	}

	/**
	 * Returns the current move that has lead to the current game situation
	 * displayed on the view.
	 * @return the current move of the current game situation on the view.
	 */
	public Position getCurrentMove()
	{
		return currentMove;
	}

	/**
	 * Returns a list of all positions in the current displayed state, that 
	 * have changed since the last move.
	 * @return a list of all changed positions since last move.
	 */
	public List<Position> getCurrentChangedPositions()
	{
		return currentChangedPositions;
	}
	
	/**
	 * Returns true if a current animation state exists, else false.
	 * @return true if a current animation state exists, else false.
	 */
	public boolean hasCurrentAnimationState()
	{
		return currentAnimationState != null;
	}
	
	/**
	 * @see AnimationState#getTokensOfColor(GameColor)
	 */
	public int getTokensOfColor(GameColor color)
	{
		return currentAnimationState.getTokensOfColor(color);
	}
	
	/**
	 * @see AnimationState#isFlowingOver(Position)
	 */
	public boolean isFlowingOver(Position pos)
	{
		return currentAnimationState.isFlowingOver(pos);
	}

	/**
	 * @see AnimationState#getColor(Position)
	 */
	public GameColor getColor(Position pos)
	{
		return currentAnimationState.getColor(pos);
	}
	
	/**
	 * @see AnimationState#getTokens(Position)
	 */
	public int getTokens(Position pos)
	{
		return currentAnimationState.getTokens(pos);
	}

	/**
	 * @see AnimationState#isUniColored()
	 */
	public boolean isUniColored()
	{
		return currentAnimationState.isUniColored();
	}
	
	/**
	 * Triggers the animator to collect another state from the model. For each
	 * new state added, another copy of the state will be added for realizing
	 * animation effects.
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if (arg instanceof Exception)
			return;
		
		Position newMove = null;
		if (arg instanceof Position)
			newMove = (Position) arg;
		
		AnimationState newSituation = new AnimationState(game.getSituation().clone(), newMove);
		
		synchronized(this)
		{
			this.addAnimationState(newSituation);
			
			this.notifyAll();
		}
	}

}
