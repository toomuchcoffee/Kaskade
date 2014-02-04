/*
 * Created on 06.01.2007
 */
package gameelements;

import player.ConsolePlayer;

/**
 * Overrides the game class mainly for the purpose of suppressing the creation
 * of any animations, which are not needed for console games.
 */
public class ConsoleGame extends Game
{
	/**
	 * Initiates a console game according to the given preferences.
	 * @param prefs the game preferences for the game.
	 */
	public ConsoleGame(GamePreferences prefs)
	{
		super(prefs);
	}
	
	/**
	 * Console games should not be observed by the gui.
	 * @see gameelements.Game#addGUIObserver()
	 */
	void addGUIObserver()
	{
		// do not add GUI as observer
	}
		
	/**
	 * If a console game is stopped, the console player should be notified.
	 * @see gameelements.Game#stopGame()
	 */
	public void stopGame()
	{
		super.stopGame();

		synchronized (this.getConsolePlayer())
		{
			this.getConsolePlayer().notifyAll();
		}
	}
	
	/**
	 * Returns the console player of this game.
	 * @return the console player of this game.
	 */
	public ConsolePlayer getConsolePlayer()
	{
		if (playerA instanceof ConsolePlayer)
			return (ConsolePlayer) playerA;
		else if (playerB instanceof ConsolePlayer)
			return (ConsolePlayer) playerB;
		else
			return null;
	}

}
