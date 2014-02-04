/*
 * Created on 03.10.2006
 */
package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The Server takes requests from remote clients to start a new console game 
 * and allocates a new service thread to the client to play the game.
 */
public class Server implements Runnable
{
	/**
	 * The singleton instance of the server.
	 */
	private static Server instance;
	
	/**
	 * The server socket this server connects to.
	 */
	private ServerSocket serversocket;
	
	/**
	 * Indicates if the server thread is running.
	 */
	private boolean running;
	
	/**
	 * Server should not be instantiated from outside.
	 */
	private Server() { }
	
	/**
	 * Returns the singleton instance of the server.
	 * @return the singleton instance of the server.
	 */
	public static Server getInstance()
	{
		if (instance == null)
		{
			instance = new Server();
		}
		return instance;
	}
	
	/**
	 * While the server is running, it will accept requests from remote clients
	 * and allocate service threads to each request.
	 * @see java.lang.Runnable#run()
	 */
	public void run() 
	{
		while (running) 
		{
			Socket so = null;
			try
			{
				so = serversocket.accept();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
				
			Thread t = new Thread(new Service(so));
			t.start();
		}
	}
	
	/**
	 * Connects to the server socket and starts the server thread.
	 * @throws IOException 
	 */
	public void startServer(int portNr) throws IOException
	{
		serversocket = new ServerSocket(portNr);
		
		Thread t = new Thread(this);
		this.running = true;
		t.start();
	}
	
	/**
	 * Stops the server and closes the server socket.
	 */
	public void stopServer()
	{
		this.running = false;
		
		if (this.serversocket != null)
		{
			try
			{
				this.serversocket.close();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

}
