/*
 * Created on 30.12.2006
 */
package player;

import gameelements.Game;
import gameelements.GameColor;
import gameelements.IllegalMoveException;
import gameelements.Position;

import java.util.Observable;
import java.util.Observer;

import network.Client;
import network.IllegalCommandException;
import network.RemoteGameSynchronisationException;

/**
 * A player that represents the remote server, therefore utilizing a local 
 * client that connects the local game with the remote server. The player
 */
public class RemotePlayer extends SynchronousPlayer implements Observer
{
	/**
	 * The client used for communication with the remote server.
	 */
	private Client client;
	
	/**
	 * Initiates a remote player.
	 * @param name the name of th eplayer.
	 * @param game the game the player participates in.
	 * @param color the color of the player.
	 */
	public RemotePlayer(String name, Game game, GameColor color)
	{
		super(name, game, color);
		this.client = new Client(this);
	}

	/* (non-Javadoc)
	 * @see player.Player#getNextMove()
	 */
	public Position getNextMove() throws IllegalMoveException
	{
		try
		{
			return client.requestMove();
		} 
		catch (IllegalCommandException e)
		{
			e.printStackTrace();
			throw new IllegalMoveException(e.getMessage());
		}
	}

	/**
	 * Checking for synchronization errors between client and server. In case
	 * of an error, the game will be notified with the error 
	 */
	public void validate()
	{
		try
		{
			this.client.validate();
		} 
		catch (RemoteGameSynchronisationException e)
		{
			e.printStackTrace();
			game.notifyWithError(e);
		}
	}

	/* (non-Javadoc)
	 * @see player.SynchronousPlayer#init()
	 */
	public void init()
	{
		this.client.requestInit(
				game.getSituation(), 
				!game.isStartingPlayer(this), 
				this.getOpponent().getPlayerName());
	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if (arg instanceof Exception)
			return;
		
		if (this.equals(game.getActivePlayer()))
		{
			this.validate();
		}
	}
	
}
