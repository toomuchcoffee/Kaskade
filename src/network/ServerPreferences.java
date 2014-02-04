/*
 * Created on 25.12.2006
 */
package network;

/**
 * Preferences for the configuration of the server.
 */
public class ServerPreferences
{
	/**
	 * Default server's thinking time.
	 */
	public static long DEFAULT_THINKING_TIME = 5000;
	
	/**
	 * Default port on which server will connect on application start.
	 */
	public static int DEFAULT_SERVER_PORT = 4711;
	
	/**
	 * The server's thinking time.
	 */
	private long thinkingTime = DEFAULT_THINKING_TIME;
	
	/**
	 * The set port on which the server will connect.
	 */
	private int serverPort = DEFAULT_SERVER_PORT;
	
	/**
	 * The instance of the preference singleton.
	 */
	private static ServerPreferences instance;
	
	/**
	 * Returns the singleton instance of the server preferences.
	 * @return the instance of server preferences
	 */
	public static ServerPreferences getInstance()
	{
		if (instance == null)
		{
			instance = new ServerPreferences();
		}
		return instance;
	}
	
	/**
	 * Returns the port on which the server connects.
	 * @return the port on which the server connects.
	 */
	public int getServerPort()
	{
		return serverPort;
	}
	
	/**
	 * Sets the port on which the server connects.
	 * @param serverPort the port to set.
	 */
	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}
	
	/**
	 * Returns the thinking time of the server.
	 * @return the thinking time of the server.
	 */
	public long getThinkingTime()
	{
		return thinkingTime;
	}
	
	/**
	 * Sets the thinking time of the server.
	 * @param thinkingTime the thinking time to set.
	 */
	public void setThinkingTime(long thinkingTime)
	{
		this.thinkingTime = thinkingTime;
	}
	
}