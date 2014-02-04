/*
 * Created on 15.10.2006
 */
package gameelements;

import gameelements.GameSituation.FieldSetup;

import java.util.List;

import player.Player;

/**
 * This class holds game preferences for the dimension of the board,
 * types and names of the participating players, strategy settings,
 * display options, and setup options for the board. These game 
 * preferences are used for both: console games and gui games.
 */
public class GamePreferences
{
	/**
	 * Default player name for server.
	 */
	public static final String DEFAULT_PLAYER_NAME = "GeraldsServer";
	
	/**
	 * The game boards x-axis dimension.
	 */
	private int dimX = 4;
	
	/**
	 * The game boards y-axis dimension.
	 */
	private int dimY = 4;
	
	/**
	 * The list of field setups for initializing fields on a game board in a 
	 * progressed game.
	 */
	private List<FieldSetup> setup;
	
	/**
	 * The available player types.
	 */
	private int[] playerTypes = { Player.TYPE_HUMAN, Player.TYPE_COMPUTER };
	
	/**
	 * The available player names.
	 */
	private String[] playerNames = { "Gandalf", "Saruman" };
	
	/**
	 * The host of the remote server.
	 */
	private String remoteServerHost = "132.176.66.13";
	
	/**
	 * The port on the remote server.
	 */
	private int remoteServerPort = 4711;
	
	/**
	 * The strategy type.
	 */
	private int strategy = 0;
	
	/**
	 * The maximal thinking time for game tree strategy.
	 */
	private long maxThinkingTime = 1000;
	
	/**
	 * Setting, whether inbetween animation steps should be animated or not.
	 */
	private boolean isAnimatedSteps = true;
	
	/**
	 * The length of each animation step in milliseconds.
	 */
	private long animationSpeed = 500;
	
	/**
	 * The singleton instance of the game preferences.
	 */
	private static GamePreferences instance;
	
	/**
	 * Never instantiate via constructor.
	 */
	private GamePreferences() { }
	
	/**
	 * Returns an individual set of game preferences, that is 
	 * independent from the preferences used by the gui. This is
	 * used for individual console games, that have to be independent
	 * from the gui.
	 * @return a new instance of game preferences with default 
	 * values.
	 */
	public static GamePreferences getNewGamePreferences()
	{
		return new GamePreferences();
	}
	
	/**
	 * Returns the single available instance of game preferences used
	 * by the gui games. A first call will return an instance with 
	 * default values.
	 * @return the instance of game prefernces as used by the gui.
	 */
	public static GamePreferences getInstance()
	{
		if (instance == null)
		{
			instance = new GamePreferences();
		}
		return instance;
	}
	
	/**
	 * Returns the current setting for the x-dimension.
	 * @return setting for x-dimension.
	 */
	public int getDimX()
	{
		return dimX;
	}
	
	/**
	 * Sets the current setting for the x-dimension.
	 * @param dimX the dimension to set.
	 */
	public void setDimX(int dimX)
	{
		this.dimX = dimX;
	}
	
	/**
	 * Returns the current setting for the y-dimension.
	 * @return setting for y-dimension.
	 */
	public int getDimY()
	{
		return dimY;
	}
	
	/**
	 * Sets the current setting for the y-dimension.
	 * @param dimY the dimension to set.
	 */
	public void setDimY(int dimY)
	{
		this.dimY = dimY;
	}
	
	/**
	 * Returns the current setting for the maximal allowed thinking
	 * time for the used strategy in milliseconds.
	 * @return setting for maximal allowed thinking time of strategy.
	 */
	public long getMaxThinkingTime()
	{
		return maxThinkingTime;
	}
	
	/**
	 * Sets the current setting for the maximal allowed thinking time
	 * for the used strategy in milliseconds.
	 * @param maxThinkingTime the maximal allowed thinking time to 
	 * set.
	 */
	public void setMaxThinkingTime(long maxThinkingTime)
	{
		this.maxThinkingTime = maxThinkingTime;
	}
	
	/**
	 * Returns the name of the player.
	 * @param index index of player: 0 = first player, 1 = second 
	 * player.
	 * @return the name of the player.
	 */
	public String getPlayerName(int index)
	{
		return playerNames[index];
	}
	
	/**
	 * Sets the name of the player.
	 * @param index index of player: 0 = first player, 1 = second
	 * player
	 * @param playerName the name of the player to set.
	 */
	public void setPlayerName(int index, String playerName)
	{
		this.playerNames[index] = playerName;
	}
	
	/**
	 * Returns the type of the player as defined in @see Player.
	 * @param index index of player: 0 = first player, 1 = second
	 * player
	 * @return the type of the player.
	 */
	public int getPlayerType(int index)
	{
		return playerTypes[index];
	}
	
	/**
	 * Sets the type of the player as defined in @see Player.
	 * @param index index of player: 0 = first player, 1 = second
	 * player
	 * @param playerType the type of the player.
	 */
	public void setPlayerType(int index, int playerType)
	{
		this.playerTypes[index] = playerType;
	}
	
	/**
	 * Returns the host of the remote server to connect for network
	 * games.
	 * @return the remote server host.
	 */
	public String getRemoteServerHost()
	{
		return remoteServerHost;
	}
	
	/**
	 * Sets the host of the remote server to connect for network
	 * games.
	 * @param remoteServerHost the remote server host to set.
	 */
	public void setRemoteServerHost(String remoteServerHost)
	{
		this.remoteServerHost = remoteServerHost;
	}
	
	/**
	 * Returns the port of the remote server to connect for network
	 * games.
	 * @return the remote server port.
	 */
	public int getRemoteServerPort()
	{
		return remoteServerPort;
	}
	
	/**
	 * Sets the port of the remote server to connect for network
	 * games.
	 * @param remoteServerPort the remote server port to set.
	 */
	public void setRemoteServerPort(int remoteServerPort)
	{
		this.remoteServerPort = remoteServerPort;
	}
	
	/**
	 * Returns a list of gamefield properties, to set up a board 
	 * situation at game initialization.
	 * @return list of properties to setup fields of a board at init.
	 */
	public List<FieldSetup> getSetup()
	{
		return setup;
	}
	
	/**
	 * Sets a list of gamefield properties, to set up a board at 
	 * game initialization. For the property objects @see 
	 * {@link FieldSetup}.
	 * @param setup the list to set.
	 */
	public void setSetup(List<FieldSetup> setup)
	{
		this.setup = setup;
	}
	
	/**
	 * Returns the strategy type as defined in @see Strategy.
	 * @return the type of strategy.
	 */
	public int getStrategy()
	{
		return strategy;
	}
	
	/**
	 * Sets the strategy type as defined in @see Strategy.
	 * @param strategy the strategy type to set.
	 */
	public void setStrategy(int strategy)
	{
		this.strategy = strategy;
	}

	
	/**
	 * Returns the length of time in milliseconds between every 
	 * update of the gui before showing a new board situation.
	 * @return time between gui update of board situation.
	 */
	public long getAnimationSpeed()
	{
		return animationSpeed;
	}

	/**
	 * Sets the length of time in milliseconds between every 
	 * update of the gui before showing a new board situation.
	 * @param animationSpeed the length of pasuing time between 
	 * updates to set.
	 */
	public void setAnimationSpeed(long animationSpeed)
	{
		this.animationSpeed = animationSpeed;
	}

	/**
	 * Returns true, if intermediate board situation, that occur 
	 * while performing overflows, should be displayed. Returns 
	 * false, if no intermediate board situations should be 
	 * displayed.
	 * @return true, if gui should display intermediate board states.
	 */
	public boolean isAnimatedSteps()
	{
		return isAnimatedSteps;
	}

	/**
	 * Sets, whether intermediate board situations, that occur while 
	 * performing overflows, should be displayed.
	 * @param isAnimatedSteps the boolean expr to set.
	 */
	public void setAnimatedSteps(boolean isAnimatedSteps)
	{
		this.isAnimatedSteps = isAnimatedSteps;
	}
}
