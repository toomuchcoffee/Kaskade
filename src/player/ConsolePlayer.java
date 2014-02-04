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

import network.Service;

/**
 * Represents a player that connects to the local server by utilizing a service
 * that is acting as client and which is provided by the local server to manage 
 * the communication between local game and player.
 */
public class ConsolePlayer extends AsynchronousPlayer implements Observer
{
	/**
	 * The service client used for the communication.
	 */
	private Service service; 
	
	/**
	 * Initiates a console player.
	 * @param name the name of the player.
	 * @param game the game the player participates in.
	 * @param color the color of the player.
	 */
	public ConsolePlayer(String name, Game game, GameColor color)
	{
		super(name, game, color);
	}

	private void respondWithMove(Position move)
	{
		this.service.respondWithMove(move);
	}

	private void respondWithBye(boolean b)
	{
		this.service.respondWithBye(b);
	}

	private void respondWithError(Exception e)
	{
		this.service.respondWithError(e);
	}

	/**
	 * Applies the service client to this player.
	 * @param service the client service to set.
	 */
	public void setService(Service service)
	{
		this.service = service;
	}

	/**
	 * Triggers server responses that will be viewable on the console of the 
	 * console player.
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if (arg instanceof IllegalMoveException)
		{
			this.respondWithError((Exception) arg);
			return;
		}
		
		if (!game.getActivePlayer().equals( this ))
		{
			this.respondWithMove((Position) arg);
			if (game.isGameOver())
			{
				this.respondWithBye(true);
			}
		}
		else if (game.isGameOver())
		{
			this.respondWithBye(false);
		}
	}

}
