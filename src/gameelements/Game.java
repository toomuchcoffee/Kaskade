/*
 * Created on 16.09.2006
 */
package gameelements;

import gui.GamePanel;
import gui.MainFrame;

import java.util.Observable;
import java.util.Observer;
import java.util.Stack;

import player.HumanPlayer;
import player.Player;
import player.SynchronousPlayer;

/**
 * Represents a running game, containing the game board, the game situation, 
 * and the two players and manages each turn by asking the current player for 
 * its next move and applying it to the game, until the game is stopped or the 
 * game is won by one player.
 */
public class Game extends Observable implements Runnable
{
	/**
	 * Minimal allowed number of fields on the game board on each axis.
	 */
	public static final int NR_OF_FIELDS_MIN = 3;
	
	/**
	 * Maximal allowed number of fields on the game board on each axis.
	 */
	public static final int NR_OF_FIELDS_MAX = 20;
	
	/**
	 * The current game board of the game.
	 */
	protected GameBoard gameBoard;
	
	/**
	 * The current game situation on the game board.
	 */
	protected GameSituation situation;
	
	/**
	 * The previous game situation on the game board, for undo functionality.
	 */
	private GameSituation previousSituation;
	
	/**
	 * If undo has been called, this variable becomes true. 
	 */
	private boolean isUndone;
	
	/**
	 * Indicates if a game shall be stopped. Used to stop the thread, in which 
	 * the game is running.
	 */
	protected boolean stopped = false;
	
	
	/**
	 * This is the white (first, starting) player.
	 */
	protected Player playerA;
	
	
	/**
	 * This is the black (second) player.
	 */
	protected Player playerB;
	
	
	/**
	 * The player that is active in the current turn.
	 */
	private Player activePlayer;
	
	
	/**
	 * All moves that have been placed by any player during the game.
	 */
	protected Stack<Position> allMoves;
	
	
	/**
	 * Constructs a new game with the preferences set.
	 * @param prefs
	 */
	public Game(GamePreferences prefs) 
	{
		gameBoard = new GameBoard(prefs.getDimX(), prefs.getDimY());
		
		this.situation = new GameSituation(gameBoard, prefs.getSetup());
		
		playerA = Player.createPlayer( prefs.getPlayerName(0), this, GameColor.WHITE, prefs.getPlayerType(0) );
		playerB = Player.createPlayer( prefs.getPlayerName(1), this, GameColor.BLACK, prefs.getPlayerType(1) );
		
		this.addObservingPlayers();
		this.addGUIObserver();
	}
	
	
	/**
	 * Registers the main window as observer for occuring exceptions.
	 */
	void addGUIObserver()
	{
		this.addObserver(MainFrame.getInstance());
	}
	

	/**
	 * Registers certain player types as observers enabling them to perform
	 * custom behavior like validation or creating responses to a remote client.
	 */
	private void addObservingPlayers()
	{
		if (playerA instanceof Observer)
			this.addObserver((Observer) playerA);
		if (playerB instanceof Observer)
			this.addObserver((Observer) playerB);
	}
	

	/**
	 * Connects the view and the model by registering the views animator object
	 * as observer to the games game board.
	 * @param view
	 */
	public void connectView(GamePanel view)
	{
		this.gameBoard.addObserver(view.getAnimator());
	}
	
	
	/**
	 * Starts the game by doing some initializing and starting its own thread. 
	 */
	public void startGame()
	{
		stopped = false;
		
		this.initSynchronousPlayers();
		
		allMoves = new Stack<Position>();
		
		Thread t = new Thread(this);
		t.start();
	}

	private void initSynchronousPlayers()
	{
		if (playerA instanceof SynchronousPlayer)
		{
			((SynchronousPlayer)playerA).init();
		}
		if (playerB instanceof SynchronousPlayer)
		{
			((SynchronousPlayer)playerB).init();
		}
	}
	

	/**
	 * Stops the game and its thread.
	 */
	public void stopGame()
	{
		stopped = true;
	}
	
	
	/**
	 * Indicates if a game is stopped.
	 * @return true, if game is stopped, else false. 
	 */
	public boolean isStopped()
	{
		return stopped;
	}
	

	/**
	 * Calls all necessary actions for each turn until the game is either
	 * stopped or won by one player.
	 * @see java.lang.Runnable#run()
	 */
	public void run()
	{
		while ( !isGameOver() && !isStopped() )
		{	
			try
			{
				this.processTurn();
			} 
			catch (IllegalMoveException e)
			{
				e.printStackTrace();
				notifyWithError(e);
			}
		}
		
		this.stopGame();
	}
	

	/**
	 * Takes notifications from other connected components, if an error occurs
	 * and notifies registered observers with the exception, that has been 
	 * thrown.
	 * @param e the exception that has been thrwon by the notifying component.
	 */
	public void notifyWithError(Exception e)
	{
		this.setChanged();
		this.notifyObservers(e);
	}
	

	/**
	 * Switches active player on every turn, asks current player for next move,
	 * proceeds with move and counts up the turn number.
	 * @throws IllegalMoveException
	 */
	protected void processTurn() throws IllegalMoveException
	{
		activePlayer = getPlayerOfTurn( getTurn() );
		Position move = activePlayer.getNextMove();
		if (move != null)
		{
			prepareUndo();
			
			this.makeMove(activePlayer, move);
			
			this.setChanged();
			this.notifyObservers(move);
		}
	}

	private void prepareUndo()
	{
		this.previousSituation = situation.clone();
	}
	
	
	public void undo()
	{
		if (canUndo())
		{
			this.situation = previousSituation; // TODO auch als Stack?
			this.allMoves.pop();
			
			this.isUndone = true;
			
			gameBoard.triggerAnimation(getLatestMove());
		}
	}
	
	/**
	 * Returns true, if an undo is allowed, else false.
	 * @return true, if an undo is allowed, else false.
	 */
	public boolean canUndo()
	{
		if (isUndone)
			return false;
		
		if ( !(playerA instanceof HumanPlayer) || !(playerB instanceof HumanPlayer) )
			return false;
		
		return true;
	}
	
	
	/**
	 * Returns the latest move that has been put in the current game.
	 * @return latest move in current game.
	 */
	public Position getLatestMove()
	{
		if (allMoves.isEmpty())
			return null;
		
		return allMoves.lastElement();
	}
	
	
	/**
	 * Returns the current active player of the given turn.
	 * @param turn the turn that the current active player is asked for.
	 * @return the active player of the given turn. 
	 */
	public Player getPlayerOfTurn(int turn)
	{
		return turn % 2 == 0 ? playerB : playerA;
	}
	
	
	/**
	 * Returns the current active player.
	 * @return the current active player.
	 */
	public Player getActivePlayer()
	{
		return activePlayer;
	}
	
	
	/**
	 * Returns true, if the given player is the player who starts the game, 
	 * else false.
	 * @param player the player in question.
	 * @return true, if player is starting player, else false. 
	 */
	public boolean isStartingPlayer(Player player)
	{
		return player.equals(playerA);
	}
	
	
	/**
	 * Returns the opponent player of the given player.
	 * @param player the player in question.
	 * @return the opponent of the given player.
	 */
	public Player getOpponent(Player player)
	{
		if (player.equals(playerA))
			return playerB;
		else
			return playerA;
	}
	
	
	/**
	 * Returns true, if game is won by one player, else false.
	 * @return true, if game is won by one player, else false.
	 */
	public boolean isGameOver()
	{
		return getWinner() != null;
	}
	
	
	/**
	 * Returns the winner of the game, null, if game is not won yet.
	 * @return the winner of the game, null, if game is not won yet.
	 */
	public Player getWinner()
	{
		if ( playerA.getColor().equals(situation.getColor()) )
			return playerA;
		else if ( playerB.getColor().equals(situation.getColor()) )
			return playerB;
		else
			return null;
	}
	
	
	/**
	 * Checks, if a players move is valid: throw an exception, if inactive 
	 * player tries to move or if a player tries an illegal move.
	 * @param player the moving player.
	 * @param move the current move of the player.
	 * @throws IllegalMoveException
	 */
	public void validateMove(Player player, Position move) throws IllegalMoveException
	{
		if ( player != getPlayerOfTurn( getTurn() ) )
		{
			throw new IllegalMoveException("inactive player tried to move");
		}
		
		if ( situation.getColor( move ) != null && player.getColor() != situation.getColor( move ) )
		{
			throw new IllegalMoveException("illegal move by " + player.getPlayerName() );
		}
	}
	
	
	/**
	 * Applies move to the current game situation.
	 * @param player the moving player.
	 * @param move the current move of the player.
	 * @throws IllegalMoveException
	 */
	public void makeMove(Player player, Position move) throws IllegalMoveException
	{
		if (isUndone)
		{
			this.isUndone = false;
			player = player.getOpponent();
		}
		
		this.validateMove(player, move);

		situation.addToken(move, player.getColor(), true);
		
		this.allMoves.push(move);
	}
	
	
	/**
	 * Returns the current game situation.
	 * @return the current game situation.
	 */
	public GameSituation getSituation()
	{
		return situation;
	}
	
	/**
	 * Returns the current turn number.
	 * @return the current turn number.
	 */
	public int getTurn()
	{
		if (allMoves.isEmpty())
			return 1;
		
		return allMoves.size() + 1;
	}

}
