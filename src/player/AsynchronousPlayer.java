/*
 * Created on 28.12.2006
 */
package player;

import gameelements.Game;
import gameelements.GameColor;
import gameelements.Position;

/**
 * A player that naturally communicates in an ansynchronous manner, if asked
 * for the next move.
 */
public class AsynchronousPlayer extends Player
{
	/**
	 * Buffer for the player's next move.
	 */
	protected Position nextMove;
	
	public AsynchronousPlayer(String name, Game game, GameColor color)
	{
		super(name, game, color);
	}

	/**
	 * Enables the naturally asynchronous communication of this player to be 
	 * used in a synchronous way. Thus waits for the buffer to be filled and
	 * empties the buffer after a request for the next move has been fulfilled.
	 * @see player.Player#getNextMove()
	 */
	public Position getNextMove()
	{
		// this is, where the black magic happens.
		synchronized(this)
		{
			if (nextMove == null)
			{
				try
				{
					this.wait();
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		Position move = nextMove;
			
		this.clean();
		
		return move;
	}
	
	/**
	 * Buffers the next move.
	 * @param nextMove the next move to buffer.
	 */
	public void setNextMove(Position nextMove)
	{
		synchronized (this)
		{
			this.nextMove = nextMove;
			this.notifyAll();
		}
	}
	
	/**
	 * Cleans the buffer.
	 */
	protected void clean()
	{
		this.nextMove = null;
	}
}
