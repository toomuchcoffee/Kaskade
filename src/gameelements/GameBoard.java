/*
 * Created on 27.10.2006
 */
package gameelements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Queue;

/**
 * Represents the layout of the game board used during the whole duration of a 
 * game. It is used for the context of the game situations that will be created 
 * during the game. All situations during a game will share the same 
 * dimensions, that are defined by the game board.
 */
public class GameBoard extends Observable
{
	/**
	 * Constant for northern orientation.
	 */
	private static final int DIRECTION_NORTH = 1;
	
	/**
	 * Constant for eastern orientation.
	 */
	private static final int DIRECTION_EAST = 2;
	
	/**
	 * Constant for southern orientation.
	 */
	private static final int DIRECTION_SOUTH = 3;
	
	/**
	 * Constant for western orientation.
	 */
	private static final int DIRECTION_WEST = 4;

	/**
	 * Dimension of the board on x-axis.
	 */
	private int dimX;
	
	/**
	 * Dimension of the board on y-axis.
	 */
	private int dimY;
	
	/**
	 * Array of all positions of the board.
	 */
	private Position[] positions;
	
	/**
	 * Array containing the information about neighbors
	 * of a position.
	 */
	private int[][][] neighbors;
	
	/**
	 * Returns the dimension for x.
	 * @return the dimension for x.
	 */
	public int getDimX()
	{
		return dimX;
	}
	
	
	/**
	 * Returns the dimension for y.
	 * @return the dimension for y.
	 */
	public int getDimY()
	{
		return dimY;
	}
	
	/**
	 * Returns all positions within the dimensions of the game board as a List.
	 * @return all positions of the game board.
	 */
	public List<Position> getPositions()
	{
		return Arrays.asList(positions);
	}
	
	/**
	 * Constructs a game board for the given dimensions.
	 * @param dimX the dimension of the board on the x-axis.
	 * @param dimY the dimension of the board on the y-axis.
	 */
	public GameBoard(int dimX, int dimY)
	{
		this.dimX = dimX;
		this.dimY = dimY;
		
		this.positions = new Position[dimX*dimY];
		this.neighbors = new int[dimX][dimY][];
		
		for (int x=0; x<dimX; x++)
		{
			for (int y=0; y<dimY; y++)
			{
				// Alle möglichen Positionen in eindimensionaler Form abspeichern
				this.positions[y*dimX + x] = new Position(x, y);
				
				if (x==0 && y==0) // linke obere Eckposition
				{
					this.neighbors[x][y] = new int[2];
					this.neighbors[x][y][0] = DIRECTION_EAST;
					this.neighbors[x][y][1] = DIRECTION_SOUTH;
				}
				else if (x==dimX-1 && y==0) // rechte obere Eckposition
				{
					this.neighbors[x][y] = new int[2];
					this.neighbors[x][y][0] = DIRECTION_SOUTH;
					this.neighbors[x][y][1] = DIRECTION_WEST;
				}
				else if (x==0 && y==dimY-1) // linke untere Eckposition
				{
					this.neighbors[x][y] = new int[2];
					this.neighbors[x][y][0] = DIRECTION_NORTH;
					this.neighbors[x][y][1] = DIRECTION_EAST;
				}
				else if (x==dimX-1 && y==dimY-1) // rechte untere Eckposition
				{
					this.neighbors[x][y] = new int[2];
					this.neighbors[x][y][0] = DIRECTION_NORTH;
					this.neighbors[x][y][1] = DIRECTION_WEST;
				}
				else if (x==0) // linke Randposition
				{
					this.neighbors[x][y] = new int[3];
					this.neighbors[x][y][0] = DIRECTION_NORTH;
					this.neighbors[x][y][1] = DIRECTION_EAST;
					this.neighbors[x][y][2] = DIRECTION_SOUTH;
				}
				else if (x==dimX-1) // rechte Randposition
				{
					this.neighbors[x][y] = new int[3];
					this.neighbors[x][y][0] = DIRECTION_NORTH;
					this.neighbors[x][y][1] = DIRECTION_SOUTH;
					this.neighbors[x][y][2] = DIRECTION_WEST;
				}
				else if (y==0) // obere Randposition
				{
					this.neighbors[x][y] = new int[3];
					this.neighbors[x][y][0] = DIRECTION_EAST;
					this.neighbors[x][y][1] = DIRECTION_SOUTH;
					this.neighbors[x][y][2] = DIRECTION_WEST;
				}
				else if (y==dimY-1) // untere Randposition
				{
					this.neighbors[x][y] = new int[3];
					this.neighbors[x][y][0] = DIRECTION_NORTH;
					this.neighbors[x][y][1] = DIRECTION_EAST;
					this.neighbors[x][y][2] = DIRECTION_WEST;
				}
				else // Mittelfeld
				{
					this.neighbors[x][y] = new int[4];
					this.neighbors[x][y][0] = DIRECTION_NORTH;
					this.neighbors[x][y][1] = DIRECTION_EAST;
					this.neighbors[x][y][2] = DIRECTION_SOUTH;
					this.neighbors[x][y][3] = DIRECTION_WEST;
				}
			}
		}
	}
	
	/**
	 * Returns the number of tokens after which a field will overrun.
	 * @param pos the position.
	 * @return the number of tokens after which a field will overrun.
	 */
	public int getLimit(Position pos)
	{
		return this.neighbors[pos.getX()][pos.getY()].length;
	}
	
	/**
	 * Returns all neighbors of a position.
	 * @param pos the position the neighbors are asked for.
	 * @return all neighbors of the given position.
	 */
	public List<Position> getNeighbors(Position pos)
	{
		int[] edges = this.neighbors[pos.getX()][pos.getY()];
		List<Position> list = new ArrayList<Position>();
		for (int i=0; i<edges.length; i++)
		{
			list.add( neighborPosition(pos, edges[i]) );
		}
		return list;
	}
	
	private Position neighborPosition(Position pos, int direction)
	{
		int deltaX, deltaY;
		switch (direction)
		{
			case 1 : deltaX = 0; deltaY = -1;
			break;
	
			case 2 : deltaX = 1; deltaY = 0;
			break;
	
			case 3 : deltaX = 0; deltaY = 1;
			break;
	
			case 4 : deltaX = -1; deltaY = 0;
			break;
	
			default : throw new RuntimeException("Ungültige Richtungsangabe");
		}
		return new Position( (pos.getX() + deltaX), (pos.getY() + deltaY) );
	}
	
	/**
	 * Triggers overflows, if any overflowing fields exist. Notifies observers
	 * about any state change of the situation.
	 * @param situation the current game situation.
	 * @param startPos the position form where to start with overflows.
	 * @param display if true, gui will be notified, else no notifications.
	 */
	void manageOverflows(GameSituation situation, Position startPos, boolean display)
	{
		if (display)
		{
			// take snapshot of current state
			this.triggerAnimation(startPos);
		}
		
		// Queue for managing all upcoming overflows.
		Queue<Position> overflowingPositions = new LinkedList<Position>();
		
		// If the current move triggers an overflow, the queue will be filled now.
		if ( situation.isFlowingOver( startPos ) ) 
		{
			overflowingPositions.add(startPos);
		}
		
		// Process the overflows.
		// Process will be abandoned if win-situation is reached.
		while ( overflowingPositions.size() > 0 && !situation.isUniColored() )
		{
			overflowStep(situation, overflowingPositions, display);
		}
	}

	private void overflowStep(GameSituation situation, Queue<Position> overflowingPositions, boolean display)
	{
		Position anOverflowingPosition = overflowingPositions.element();

		// Process overflow.
		List<Position> overflowingNeighbors = this.overflow( situation, anOverflowingPosition );
		
		// Registrate new overflowing positions in queue.
		for ( Position anOverflowingNeighbor : overflowingNeighbors )
		{
			if ( ! overflowingPositions.contains(anOverflowingNeighbor) )
			{
				overflowingPositions.add(anOverflowingNeighbor);
			}
		}
		
		// Take position out of queue
		if ( !situation.isFlowingOver( anOverflowingPosition ) ) 
		{
			// A field can be overfilled through more than one overflow.
			overflowingPositions.remove();
		}
		
		if (display)
		{
			this.triggerAnimation();
		}
	}
	
	private List<Position> overflow(GameSituation situation, Position pos)
	{
		List<Position> overflowingNeighbors = new ArrayList<Position>();
		
		for (Position neighbor : this.getNeighbors(pos))
		{
			situation.relocateToken( neighbor, situation.removeToken(pos) );
			
			if ( situation.isFlowingOver( neighbor ) )
			{
				overflowingNeighbors.add( neighbor );
			}
		}
		
		return overflowingNeighbors;
	}

	
	/**
	 * Triggers the animator for animating the current gameboard situation.
	 */
	public void triggerAnimation()
	{
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * @see GameBoard#triggerAnimation()
	 * @param move the move which caused the current game situation.
	 */
	public void triggerAnimation(Position move)
	{
		this.setChanged();
		this.notifyObservers( move );
	}
	
}
