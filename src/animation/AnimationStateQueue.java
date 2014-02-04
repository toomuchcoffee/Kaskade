/*
 * Created on 07.01.2007
 */
package animation;

import java.util.LinkedList;
import java.util.Queue;

/**
 * A complex data structure for helping the animator to queue animation states,
 * so that the states can be allocated to the turns they were created in.
 */
class AnimationStateQueue
{
	/**
	 * Nested queue. The inner queue covers all animation states within one
	 * turn while the outer queue covers the animation queues for all turns.
	 */
	private Queue<Queue<AnimationState>> turnQueue;
	
	/**
	 * Animation queue of the current displayed turn.
	 */
	private Queue<AnimationState> currentAnimationStateQueue;
	
	/**
	 * Is set to true if a new animation queue is read out.
	 */
	private boolean isStartOfTurn;
	
	/**
	 * The current queue that is open for adding new states from the model.
	 */
	private Queue<AnimationState> openAnimationStateQueue;
	
	/**
	 * Initiates a new animation state queue.
	 */
	AnimationStateQueue()
	{
		this.turnQueue = new LinkedList<Queue<AnimationState>>();
		this.openAnimationStateQueue = new LinkedList<AnimationState>();
		this.currentAnimationStateQueue = openAnimationStateQueue;
	}
	
	
	/**
	 * Adds an animation state to the current open queue. Also opens a new
	 * queue if a new turn starts.
	 * @param animationState
	 */
	void add(AnimationState animationState)
	{
		openAnimationStateQueue.add(animationState);
		
		if (!animationState.isFlowingOver())
		{
			openAnimationStateQueue.add( animationState );
			openAnimationStateQueue = new LinkedList<AnimationState>();
			turnQueue.add(openAnimationStateQueue);
		}
	}

	/**
	 * Returns true if queue is empty, else false.
	 * @return true if queue is empty, else false.
	 */
	boolean isEmpty()
	{
		if (currentAnimationStateQueue.isEmpty())
		{
			if (turnQueue.isEmpty())
				return true;
			
			return turnQueue.peek().isEmpty();
		}
		
		return false;
	}

	/**
	 * Removes the next state from the queue and returns it. Returns null if
	 * wueue is empty.
	 * @return the next state from the queue.
	 */
	AnimationState remove()
	{
		if (isStartOfTurn)
			isStartOfTurn = false;
		
		if (currentAnimationStateQueue.isEmpty())
		{
			if (turnQueue.isEmpty())
				return null;

			currentAnimationStateQueue = turnQueue.remove();
			isStartOfTurn = true;
			if (currentAnimationStateQueue.isEmpty())
				return null;
		}
		
		return currentAnimationStateQueue.remove();
	}
	
	/**
	 * Returns true if current inner queue has just started, else false.
	 * @return true if current inner queue has just started, else false.
	 */
	boolean isStartOfCurrentQueue()
	{
		return isStartOfTurn;
	}
	
	/**
	 * Returns true if current inner queue contains the minimum amount
	 * of states needed to display a turn: This is true if all states that
	 * indicate a state transformation within a turn, have been removed. 
	 * @return true if current inner queue only contains minimum number of 
	 * states to display a full turn.
	 */
	boolean isEndOfTurn()
	{
		return currentAnimationStateQueue.size() <= 2;
	}
	
	/**
	 * Returns true if current inner queue is empty, else false.
	 * @return true if current inner queue is empty, else false.
	 */
	boolean isEndOfCurrentQueue()
	{
		return currentAnimationStateQueue.isEmpty();
	}
	
}
